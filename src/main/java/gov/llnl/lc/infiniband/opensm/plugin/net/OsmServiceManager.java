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
 *        file: OsmServiceManager.java
 *
 *  Created on: Jun 27, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.net;

/**********************************************************************
 * The OsmServiceManager is used by client applications to establish and
 * manage connections to the OSM monitoring service running on a remote host.
 * <p>
 * Typically, the client would open a session on the remote host, get the
 * desired API, and then invoke API methods.  See sample code below;
 * 
 *  <pre>
public class ClientInterfaceExample implements CommonLogger
{
  public static void main(String[] args) throws Exception
  {
    OsmSession ParentSession = null;
    OsmServiceManager OsmService = OsmServiceManager.getInstance();

    try
    {
      ParentSession = OsmService.openSession(null, null, null);
    }
    catch (Exception e)
    {
      logger.severe(e.getStackTrace().toString());
      System.exit(0);
    }

    if (ParentSession != null)
    {
      OsmClientApi clientInterface = ParentSession.getClientApi();

      // use the api's to get information
      OSM_Nodes nodes = clientInterface.getOsmNodes();
      System.out.println(nodes);
    }
    // all done, so close the session(s)
    OsmService.closeSession(ParentSession);
  }
}
 *  </pre>
 *  
 * @see  OsmSession
 * @see  OsmSession
 *
 * @author meier3
 * 
 * @version Jun 27, 2011 11:43:50 AM
 **********************************************************************/
public class OsmServiceManager implements gov.llnl.lc.logging.CommonLogger
{
  /** you are limited to this many parent sessions **/
  public static final int MaxParents = 8;

  /** the synchronization object **/
  private static Boolean semaphore            = new Boolean( true );

  /** the one and only <code>OsmServiceManager</code> Singleton **/
  private volatile static OsmServiceManager globalOsmServiceManager  = null;

  /** a Parent session can spawn child sessions, using the same config **/
  private volatile static java.util.ArrayList <OsmApiSession> Parent_Sessions;
      
  /** logger for the class **/
  private final java.util.logging.Logger classLogger =
      java.util.logging.Logger.getLogger( getClass().getName() );

  private OsmServiceManager()
  {
    init();
  }
  
  /**************************************************************************
  *** Method Name:
  ***     init
  **/
  /**
  *** The managers initialization routine, called by the private constructor.
  *** <p>
  ***
  **************************************************************************/

  protected synchronized boolean init()
  {
    boolean success = false;
    
    logger.info("Initializing the ServiceManager");
    /* put stuff here that can be initialized or reset, use constructor for static initialization */
    Parent_Sessions = new java.util.ArrayList<OsmApiSession>();
    
    return success;
  }
  /*-----------------------------------------------------------------------*/

    
   /**************************************************************************
  *** Method Name:
  ***     getInstance
  **/
  /**
  *** Get the singleton OsmServiceManager. This can be used if the application wants
  *** to share one manager across the whole JVM.  Normally, this is used in place of
  *** a constructor, since the constructor is private.
  *** <p>
  ***
  *** @return       the GLOBAL (or shared) OsmServiceManager
  **************************************************************************/

  public static OsmServiceManager getInstance()
  {
    synchronized( OsmServiceManager.semaphore )
    {
      if ( globalOsmServiceManager == null )
      {
        globalOsmServiceManager = new OsmServiceManager( );
      }
      return globalOsmServiceManager;
    }
  }
  /*-----------------------------------------------------------------------*/
  /************************************************************
   * Method Name:
   *  clone
  **/
  /**
   * You can not clone the manager, it is a Singleton.
   *
   * @see java.lang.Object#clone()
   * @throws CloneNotSupportedException
   ***********************************************************/
  public Object clone() throws CloneNotSupportedException 
  {
    throw new CloneNotSupportedException(); 
  }
  

  /************************************************************
   * Method Name:
   *  openSession
  **/
  /**
   * Opens a "parent" session to the remote host running the service.  Client authentication
   * is normally required.  If the username and password are not provided, it will typically
   * be requested via an interactive command line.
   *
   * @see     OsmSession
   * @param hostname - the url of the remote host running the service
   * @param username - null, or the users account name
   * @param password - null, or the users password
   * @return an OsmSession if successful, otherwise throws and exception
   * @throws Exception
   ***********************************************************/
  public synchronized OsmSession openSession(String hostname, String portnum, String username, String password) throws Exception
  {
    OsmApiSession api = null;
    /* open, then return */
    if ((Parent_Sessions != null) && (Parent_Sessions.size() < MaxParents))
    {
      api = new OsmApiSession(hostname, portnum, username, password);

      if (api != null)
      {
        Parent_Sessions.add(api);
      }
    }
    else
      logger.severe("Exceeded the maximum number of sessions (" + MaxParents + ")");
    return api;
  }

  /************************************************************
   * Method Name:
   *  openSession
  **/
  /**
   * Opens a "child" session from the "parent" session.  Once the original
   * "parent" session is created, it can be used to create additional sessions,
   * thereby avoiding the authentication process (since authentication has
   * already been established).
   *
   * @see     #isParentSession(OsmSession)
   * @param session - it must be a "parent" session, a child is not allowed
   * @return an OsmSession if successful, otherwise throws and exception
   * @throws Exception
   ***********************************************************/
  public synchronized OsmSession openSession(OsmSession session) throws Exception
  {
    /* open a child session, if the parent session is open and active */
    if(isParentSession(session))
    {
      return session.cloneSession();      
    }
    else
    {
      logger.severe("Could not create a clone session from this session (must be a parent)");
    }
    return null;
  }

  /************************************************************
   * Method Name:
   *  close
  **/
  /**
   * Close the OsmServiceManager, causing all of the parent sessions
   * as well as their child sessions to close.
   *
   * @see     OsmApiSession#close()
   * @throws Exception
   ***********************************************************/
  public void close() throws Exception
  {
    /* close all the sessions, shutting down */
    for (OsmApiSession s : Parent_Sessions) 
    {
        s.close();        
    }
    Parent_Sessions.clear();
  }

  /************************************************************
   * Method Name:
   *  closeSession
  **/
  /**
   * Closes the specified session.
   *
   * @see     OsmApiSession#close()
   * @throws Exception
   ***********************************************************/
  public synchronized void closeSession(OsmSession session) throws Exception
  {
    /* closing a parent should close its children */
    OsmApiSession r = null;
    for (OsmApiSession s : Parent_Sessions) 
    {
      if(s.equals((OsmApiSession)session))
      {
        r = s;
        s.close();
        break;
      }
    }
    if(r != null)
      Parent_Sessions.remove(r);
  }

  /**
   * @return Returns the validSession.
   */
  /************************************************************
   * Method Name:
   *  isParentSession
  **/
  /**
   * Returns true if the provided session is a "parent".
   *
   * @param session  - the session to test
   * @return true if a parent, otherwise false
   ***********************************************************/
  protected boolean isParentSession(OsmSession session)
  {
    boolean isParent = false;
    if((session != null) && (session instanceof OsmApiSession ))
    {
      /* check the list to see if its there */
      for (OsmApiSession s : Parent_Sessions) 
      {
        if(s.equals((OsmApiSession)session))
          isParent = true;
      }
    }
    return isParent;
  }
  
  
}
