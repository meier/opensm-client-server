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
 *        file: OsmPluginMonitor.java
 *
 *  Created on: Jul 7, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin;

import gov.llnl.lc.infiniband.opensm.plugin.event.OsmEvent;
import gov.llnl.lc.infiniband.opensm.plugin.event.OsmEventManager;
import gov.llnl.lc.infiniband.opensm.plugin.event.OsmSessionEventSet;
import gov.llnl.lc.logging.CommonLogger;
import gov.llnl.lc.net.MultiThreadSSLServer;
import gov.llnl.lc.net.ObjectSessionEvent;
import gov.llnl.lc.net.ObjectSessionListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**********************************************************************
 * Describe purpose and responsibility of OsmPluginMonitor
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Oct 13, 2011 2:45:55 PM
 **********************************************************************/
public class OsmPluginMonitor implements Runnable, CommonLogger, ObjectSessionListener
{
  /** the data synchronization object **/
  private static Boolean semaphore            = new Boolean( true );
  
  private static final Long MonitorId = new Long(-123);
  private volatile static long Heartbeat = 0L;
  
  /** the one and only <code>OsmPluginMonitor</code> Singleton **/
  private volatile static OsmPluginMonitor globalPluginMonitor  = null;

  /** the one and only <code>MultiThreadSSLServer</code> Singleton **/
  private volatile static MultiThreadSSLServer globalServer  = null;

  /** boolean specifying whether the thread should continue **/
  private volatile static boolean Continue_Thread = true;
  
  /** thread responsible for listening for events **/
  private static java.lang.Thread Listener_Thread;
  
  /** the one and only <code>OsmEventManager</code> Singleton **/
  private volatile static OsmEventManager globalOsmEventManager  = null;
  
  /** logger for the class **/
  private final java.util.logging.Logger classLogger =
      java.util.logging.Logger.getLogger( getClass().getName() );


  /************************************************************
   * Method Name:
   *  OsmPluginMonitor
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param   describe the parameters if any
   *
   ***********************************************************/
  private OsmPluginMonitor()
  {
    super();
    
    globalOsmEventManager = OsmEventManager.getInstance();
    globalServer          = MultiThreadSSLServer.getInstance();
    
    // set up the thread to listen
    Listener_Thread = new Thread(this);
    Listener_Thread.setDaemon(true);
    Listener_Thread.setName("OsmPluginMonitorThread");
    
    classLogger.info("Creating the " + Listener_Thread.getName() + " Thread");
    logger.info("Creating the " + Listener_Thread.getName() + " Thread");

  }
  /**************************************************************************
   *** Method Name:
   ***     getInstance
   **/
   /**
   *** Get the singleton OsmPluginMonitor. This can be used if the application wants
   *** to share one monitor across the whole JVM.  Currently I am not sure
   *** how this ought to be used.
   *** <p>
   ***
   *** @return       the GLOBAL (or shared) OsmPluginMonitor
   **************************************************************************/

   public static OsmPluginMonitor getInstance()
   {
     synchronized( OsmPluginMonitor.semaphore )
     {
       if ( globalPluginMonitor == null )
       {
         globalPluginMonitor = new OsmPluginMonitor( );
       }
       return globalPluginMonitor;
     }
   }

  /************************************************************
   * Method Name:
   *  toString
  **/
  /**
   * Describe the method here
   *
   * @see java.lang.Object#toString()
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @return
   ***********************************************************/
  
  @Override
  public String toString()
  {
    return "OsmPluginMonitor";
  }
  
   /**************************************************************************
    *** Method Name:
    ***     startThread
    **/
    /**
    *** Summary_Description_Of_What_createThread_Does.
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

    private void startThread()
    {
      classLogger.info("Starting the " + Listener_Thread.getName() + " Thread");
      logger.info("Starting the " + Listener_Thread.getName() + " Thread");
        Listener_Thread.start();
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
       classLogger.info("Stopping the " + Listener_Thread.getName() + " Thread");
       logger.info("Stopping the " + Listener_Thread.getName() + " Thread");
       Continue_Thread = false;
     }
     /*-----------------------------------------------------------------------*/

     public void destroy()
     {
       classLogger.info("Terminating the Plugin Monitor");
       logger.info("Terminating the Plugin Monitor");
       globalServer.removeListener(this);
       globalOsmEventManager.removeSessionListener(MonitorId);
       stopThread();
     }
     
    /**************************************************************************
     *** Method Name:
     ***     init
     **/
     /**
     *** Summary_Description_Of_What_init_Does.
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
       
       classLogger.info("Initializing the OsmPluginMonitor");
       logger.info("Initializing the OsmPluginMonitor");
       
       /* do whatever it takes to initialize the OsmPluginMonitor */
       startThread();
       
       return success;
     }
     /*-----------------------------------------------------------------------*/


  /************************************************************
   * Method Name:
   *  run
  **/
  /**
   * Describe the method here
   *
   * @see java.lang.Runnable#run()
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   ***********************************************************/
  
  @Override
  public void run()
  {
    long eventCount = 0;
    OsmEvent osm_event;
    
    // make the monitor a listener to various updaters
    globalServer.addListener(this);
    globalOsmEventManager.addSessionListener(MonitorId);
    OsmSessionEventSet monitorSet = new OsmSessionEventSet(OsmEvent.OSM_ALL_EVENTS, MonitorId);

    // check the Thread Termination Flag, and continue if Okay
    while(Continue_Thread)
    {
      try
      {
        Heartbeat++;
        TimeUnit.MILLISECONDS.sleep(10);
        // wait for an event, convert it, then signal listeners
//        osm_event = globalOsmEventManager.waitForOsmEvent(300, OsmEvent.OSM_ALL_EVENTS);
        osm_event = globalOsmEventManager.waitForOsmEvents(300, monitorSet);

        eventCount++;
        classLogger.info("Event count is: " + eventCount + ", current event: " + osm_event);        
      }
      catch (Exception e)
      {
        logger.severe("Unhandled exception in Monitor Loop: (" + e.getMessage() + ")");
      }
    }
    classLogger.info("Terminating the " + Listener_Thread.getName() + " Thread");
    logger.info("Terminating the " + Listener_Thread.getName() + " Thread");
   /* fall through, must be done! */
  }
  /************************************************************
   * Method Name:
   *  monitorManagers
  **/
  /**
   * Do basic watchdog and sanity checks on the various <code>Managers</code>
   * and threads and attempt to detect and fix problems.  Specifically, watch
   * the connections for I/O problems, and make sure basic clean up occurs if
   * connections get dropped in an unexpected fashion.
   *
   * @see     describe related java objects
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @return
   ***********************************************************/
  private boolean monitorManagers()
  {
    // compare the list of event listener session id's with the
    // session id's of all the active sessions.  
    
    // the EventManagers list of event listener session id's should
    // be a subset of all the active sessions.  Compare the two
    // and remove event listeners that are no longer active
    
    ArrayList<Long> eventSessions  = globalOsmEventManager.getSessionListenerIds();
    ArrayList<Long> serverSessions = globalServer.getCurrentSessionIds();
    
    // make sure every eventSession exists in the serverSession, and if not, remove it
    // (exception is this Monitor's Id
    for(Long id: eventSessions)
    {
      if((id != MonitorId) && (!serverSessions.contains(id)))
      {
        classLogger.warning("removing stale event listener " + id );
        logger.warning("removing stale event listener " + id );
        globalOsmEventManager.removeSessionListener(id);
      }
    }
    
    return true;
  }
  
  public long getHeartbeat()
  {
    synchronized (OsmPluginMonitor.semaphore)
    {
      return Heartbeat;
    }
  }


  
  /************************************************************
   * Method Name:
   *  sessionUpdate
  **/
  /**
   * Describe the method here
   *
   * @see gov.llnl.lc.net.ObjectSessionListener#sessionUpdate(gov.llnl.lc.net.ObjectSessionEvent)
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @param arg0
   ***********************************************************/
  
  @Override
  public void sessionUpdate(ObjectSessionEvent event)
  {
    classLogger.info("Session event: " + event );
    
    // a session just came or went, do some sanity checking
    if(!monitorManagers())
    {
      classLogger.severe("Plugin Monitor encountered a problem");
      logger.severe("Plugin Monitor encountered a problem");
    }
    
  }

}
