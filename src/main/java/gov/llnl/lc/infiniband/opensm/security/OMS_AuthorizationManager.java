/************************************************************
 * Copyright (c) 2015, Lawrence Livermore National Security, LLC.
 * Produced at the Lawrence Livermore National Laboratory.
 * Written by Timothy Meier, meier3@llnl.gov, All rights reserved.
 * LLNL-CODE-673346
 *
 * This file is part of the OpenSM Monitoring Service (OMS) package.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License (as published by
 * the Free Software Foundation) version 2.1 dated February 1999.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * OUR NOTICE AND TERMS AND CONDITIONS OF THE GNU GENERAL PUBLIC LICENSE
 *
 * Our Preamble Notice
 *
 * A. This notice is required to be provided under our contract with the U.S.
 * Department of Energy (DOE). This work was produced at the Lawrence Livermore
 * National Laboratory under Contract No.  DE-AC52-07NA27344 with the DOE.
 *
 * B. Neither the United States Government nor Lawrence Livermore National
 * Security, LLC nor any of their employees, makes any warranty, express or
 * implied, or assumes any liability or responsibility for the accuracy,
 * completeness, or usefulness of any information, apparatus, product, or
 * process disclosed, or represents that its use would not infringe privately-
 * owned rights.
 *
 * C. Also, reference herein to any specific commercial products, process, or
 * services by trade name, trademark, manufacturer or otherwise does not
 * necessarily constitute or imply its endorsement, recommendation, or favoring
 * by the United States Government or Lawrence Livermore National Security,
 * LLC. The views and opinions of authors expressed herein do not necessarily
 * state or reflect those of the United States Government or Lawrence Livermore
 * National Security, LLC, and shall not be used for advertising or product
 * endorsement purposes.
 *
 *        file: OMS_AuthorizationManager.java
 *
 *  Created on: Nov 20, 2014
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.security;

import gov.llnl.lc.infiniband.opensm.plugin.OsmNativeCommand;
import gov.llnl.lc.infiniband.opensm.plugin.event.OsmTimeStampedEvent;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmClientUserInfo;
import gov.llnl.lc.net.ObjectSession;
import gov.llnl.lc.system.CommandLineArguments;
import gov.llnl.lc.util.filter.WhiteAndBlackListFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**********************************************************************
 * OMS_AuthorizationManager is responsible for determining if a user, or client
 * is authorized to use parts of the OMS interface tagged as "privileged".
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Nov 20, 2014 10:57:16 AM
 **********************************************************************/
public class OMS_AuthorizationManager implements OMS_AuthorizationConstants, Runnable, gov.llnl.lc.logging.CommonLogger
{
  /** the data synchronization object **/
  private static Boolean semaphore            = new Boolean( true );
  
  /** the Managers update counter **/
  private static long AuthHeartbeat = 0;

  /** the Managers update period in seconds**/
  private static int AuthUpdatePeriod = 60;

  /* keep only the ten most recent additions, throw all others away */
  public static final int MAX_COLLECTION_SIZE = 10;
  
  private int MaxSize = MAX_COLLECTION_SIZE;
  
  /** the Authorization Filters **/
  private WhiteAndBlackListFilter AuthUserFilter    = null;
  private WhiteAndBlackListFilter AuthGroupFilter   = null;
  private WhiteAndBlackListFilter AuthCommandFilter = null;
  
  /** the one and only <code>OMS_AuthorizationManager</code> Singleton **/
  private volatile static OMS_AuthorizationManager globalOsmAuthManager  = null;

  /** boolean specifying whether the thread should continue **/
  private volatile static boolean Continue_Thread = true;
  
  /** thread responsible for listening for events **/
  private static java.lang.Thread Listener_Thread;
  
  /* keyed off the object session thread and port number */
  private LinkedHashMap<String, OsmClientUserInfo> userHistory = new LinkedHashMap<String, OsmClientUserInfo>(MaxSize+1, .75F, false)
  {  
    /**  describe serialVersionUID here **/
    private static final long serialVersionUID = 5747506901280727041L;

    protected boolean removeEldestEntry(Map.Entry<String, OsmClientUserInfo> eldest)  
    { 
      // return true if I want the oldest removed.  If I wanted to "handle" the eldest, I would do it here before returning
      return size() > MaxSize;                                    
    }  
  }; 
     
  /** a list of Listeners, interested in knowing about authorization events **/
  private volatile static java.util.Hashtable<Long, ArrayList <OsmTimeStampedEvent>> Auth_Listener_Queues =
    new java.util.Hashtable<Long, ArrayList <OsmTimeStampedEvent>>();

  /** logger for the class **/
  private final java.util.logging.Logger classLogger =
      java.util.logging.Logger.getLogger( getClass().getName() );

  private OMS_AuthorizationManager()
  {
    // set up the thread to listen
    Listener_Thread = new Thread(this);
    Listener_Thread.setDaemon(true);
    Listener_Thread.setName("OMS_AuthorizationManagerListenerThread");
    
    // read the properties file, and initialize the white and black lists
    initFilters();    
  }
 
  
  private void initFilters()
  {
    OMS_AuthorizationProperties prop = new OMS_AuthorizationProperties();
    
    /* attempt to find the auth filter files */
    try
    {
      String val =  prop.getProperty(PRIVILEGED_USER_FILE_KEY, PRIV_DEFAULT_USER_FILENAME);
      AuthUserFilter    = new WhiteAndBlackListFilter(val);
      val =  prop.getProperty(PRIVILEGED_GROUP_FILE_KEY, PRIV_DEFAULT_GROUP_FILENAME);
      AuthGroupFilter   = new WhiteAndBlackListFilter(val);
      val =  prop.getProperty(PRIVILEGED_COMMAND_FILE_KEY, PRIV_DEFAULT_COMMAND_FILENAME);
      AuthCommandFilter = new WhiteAndBlackListFilter(val);
    }
    catch (IOException e)
    {
      e.printStackTrace();
      logger.severe("Couldn't initialize the Authorization Filters");
    }
  }


  /**************************************************************************
  *** Method Name:
  ***     init
  **/
  /**
  *** Initializes the authorization listener thread.  This is not absolutely
  *   necessary.  It enables the manager to notify subscribed listeners when
  *   an authorization event takes place.
  *   Most authorization activities occur outside of the listener/notification
  *   thread.
  *** <p>
  ***
  *** @see          Method_related_to_this_method
  ***
  *** @param        Parameter_name  Description_of_method_parameter__Delete_if_none
  ***
  *** @return       Description_of_method_return_value__Delete_if_none
  ***
  *** @throws       Class_name  Description_of_exception_thrown__Delete_if_none
  **************************************************************************/

  public synchronized boolean init()
  {
    boolean success = false;
    
    logger.info("Initializing the AuthorizationManager");
    classLogger.info("Initializing the AuthorizationManager");
    
    /* do whatever it takes to initialize the authorization manager */
    startThread();
    
    return success;
  }
  /*-----------------------------------------------------------------------*/


  /**************************************************************************
   *** Method Name:
   ***     startThread
   **/
   /**
   *** Summary_Description_Of_What_startThread_Does.
   *** <p>
   ***
   *** @see          Method_related_to_this_method
   ***
   *** @param        Parameter_name  Description_of_method_parameter__Delete_if_none
   ***
   *** @return       Description_of_method_return_value__Delete_if_none
   ***
   *** @throws       Class_name  Description_of_exception_thrown__Delete_if_none
   **************************************************************************/

   private boolean startThread()
   {
     classLogger.info("Starting the " + Listener_Thread.getName() + " Thread");
     Listener_Thread.start();
     return true;
   }
   /*-----------------------------------------------------------------------*/

   /**************************************************************************
    *** Method Name:
    ***     stopThread
    **/
    /**
    *** Summary_Description_Of_What_startThread_Does.
    *** <p>
    ***
    *** @see          Method_related_to_this_method
    ***
    *** @param        Parameter_name  Description_of_method_parameter__Delete_if_none
    ***
    *** @return       Description_of_method_return_value__Delete_if_none
    ***
    *** @throws       Class_name  Description_of_exception_thrown__Delete_if_none
    **************************************************************************/

    private void stopThread()
    {
      logger.info("Stopping the " + Listener_Thread.getName() + " Thread");
      classLogger.info("Stopping the " + Listener_Thread.getName() + " Thread");
      Continue_Thread = false;
    }
    /*-----------------------------------------------------------------------*/


    public void destroy()
    {
      logger.info("Terminating the AuthorizationManager");
      classLogger.info("Terminating the AuthorizationManager");
      stopThread();
    }
    
   /**************************************************************************
  *** Method Name:
  ***     getInstance
  **/
  /**
  *** Get the singleton globalOsmAuthManager. This can be used if the application wants
  *** to share one manager across the whole JVM.  Currently I am not sure
  *** how this ought to be used.
  *** <p>
  ***
  *** @return       the GLOBAL (or shared) globalOsmAuthManager
  **************************************************************************/

  public static OMS_AuthorizationManager getInstance()
  {
    synchronized( OMS_AuthorizationManager.semaphore )
    {
      if ( globalOsmAuthManager == null )
      {
        globalOsmAuthManager = new OMS_AuthorizationManager( );
      }
      return globalOsmAuthManager;
    }
  }
  /*-----------------------------------------------------------------------*/

  public Object clone() throws CloneNotSupportedException 
  {
    throw new CloneNotSupportedException(); 
  }
  
  public synchronized boolean removeAuthListener(Long sessionId)
  {
    Auth_Listener_Queues.remove(sessionId);
        return true;
  }
    
  public synchronized boolean addAuthListener(Long sessionId)
  {
    Auth_Listener_Queues.put(sessionId, new java.util.ArrayList<OsmTimeStampedEvent>());
        return true;
  }
    
  public int numAuthListeners()
  {
    return Auth_Listener_Queues.size();
   }
    
  public synchronized ArrayList<Long> getAuthListenerIds()
  {
    ArrayList<Long> rtnList = new java.util.ArrayList<Long>();
    if(Auth_Listener_Queues.size() > 0)
        {
        // build up the array list
          for(Long id: Auth_Listener_Queues.keySet())
          {
            rtnList.add(id);
          }
          classLogger.info("All of the auth ids: " + rtnList);
        }
        return rtnList;
  }
  
  public long getHeartbeat()
  {
    synchronized (OMS_AuthorizationManager.semaphore)
    {
      return AuthHeartbeat;
    }
  }

  public void incrementHeartbeat()
  {
    synchronized (OMS_AuthorizationManager.semaphore)
    {
      AuthHeartbeat++;
    }
  }
  
  public boolean isAuthorizedUser(OsmClientUserInfo clientUser)
  {
    // check against list of authorized users, return true if allowed
    
    if((clientUser == null) || (clientUser.getClientUser() == null))
      return false;
    
    if(AuthUserFilter == null)
      return false;
    
    // if not filtered, then must be allowed
    return !(AuthUserFilter.isFiltered(clientUser.getClientUser().UserName));
  }

  public boolean isInAuthorizedGroup(OsmClientUserInfo clientUser)
  {
    // check against list of authorized groups, return true if user is in one of them
    if((clientUser == null) || (clientUser.getClientUser() == null))
      return false;
    
    if(AuthGroupFilter == null)
      return false;
    
    // loop through the users groups, and return as soon as NOT FILTERED (meaning allowed)
    String [] gArray = clientUser.getClientUserGroups();
    
    if((gArray == null) || (gArray.length < 1))
      return false;
    
    for(String grp: gArray)
    {
      if(!(AuthGroupFilter.isFiltered(grp)))
        return true;
    }
    return false;
  }

  public boolean isAuthorized(OsmClientUserInfo clientUser)
  {
    return (isAuthorizedUser(clientUser) || isInAuthorizedGroup(clientUser));
  }

  private String getSessionKey(ObjectSession os)
  {
    // the combination of threadId and port number should be unique
    return os.getThreadId() + ":" + os.getPort();
  }

  public boolean setSessionUser(OsmClientUserInfo clientUser, ObjectSession os)
  {
    // save this users info with the session key, so it can be retrieved later
    // if a privileged command is attempted during this session
    userHistory.put(getSessionKey(os), clientUser);
    return isAuthorized(clientUser);
  }

  public OsmClientUserInfo getSessionUser(ObjectSession os)
  {
    // attempt to retrieve the users info, based on the session key
    // this is typically done by the AuthorizationManager to see if a
    // user is allowed to do something
    return userHistory.get(getSessionKey(os));
  }

  @Override
  public void run()
  {
    // check the Thread Termination Flag, and continue if Okay
    while(Continue_Thread)
    {
      /* spin loop, keep alive for listening */
      try
      {
        incrementHeartbeat();
        TimeUnit.SECONDS.sleep(AuthUpdatePeriod);
      }
      catch (InterruptedException e)
      {
        logger.severe("Crap, the " + Listener_Thread.getName() + " Thread had an unknown exception.");
        logger.severe(e.getMessage());
      }
 
    }
    logger.info("Terminating the " + Listener_Thread.getName() + " Thread");
    classLogger.info("Terminating the " + Listener_Thread.getName() + " Thread");
   /* fall through, must be done! */
  }

 
  /************************************************************
   * Method Name:
   *  main
   **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects
   *
   * @param args
   ***********************************************************/
  public static void main(String[] args)
  {
    OsmClientUserInfo         me = new OsmClientUserInfo();
    CommandLineArguments command = new CommandLineArguments("rpm -qi llnl-ldapotp-clt-jni-auth-libs-1.0.0-4.ch4.4");
    System.out.println(me);
    System.out.println(command.getCommandLine());
    
    boolean authUser = OMS_AuthorizationManager.getInstance().isAuthorized(me);
    System.out.println(authUser);
    
    boolean authCmd = OMS_AuthorizationManager.getInstance().isAuthorizedCommand(command);
    System.out.println(authCmd);

  }

  public boolean isAuthorizedCommand(CommandLineArguments command)
  {
    // check to see if this command is in the list of allowed commands
    if((command == null) || (command.getCommandLine() == null ) || (command.getCommandLine().length() < 2))
      return false;
    
    if(AuthCommandFilter == null)
      return false;
    
    String [] cmdargs = command.getCommandLine().split(" ");
    
    // if not filtered, then must be allowed
    return !(AuthCommandFilter.isFiltered(cmdargs[0]));
  }

  public boolean isNativeCommand(CommandLineArguments command)
  {
    // almost any shell command can be invoked
    // but there are only a limited number of supported NativeCommands that
    // can be triggered from within the native layer, inside OpenSm.
    //
    // Is this one of the valid NativeCommands?
    return OsmNativeCommand.isNativeCommand(command);
  }


  public boolean isAuthorizedSession(ObjectSession session)
  {
    // check the user history to obtain the user, and see if they are
    // authorized
    OsmClientUserInfo user = getSessionUser(session);
    if(user != null)
      return isAuthorized(user);
    
    return false;
  }
  

}
