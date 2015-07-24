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
 *        file: OsmEventInterface.java
 *
 *  Created on: Jul 28, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.net;

import gov.llnl.lc.infiniband.opensm.plugin.event.OSM_EventStats;
import gov.llnl.lc.infiniband.opensm.plugin.event.OsmEvent;
import gov.llnl.lc.infiniband.opensm.plugin.event.OsmEventListener;
import gov.llnl.lc.infiniband.opensm.plugin.event.OsmSessionEventSet;
import gov.llnl.lc.logging.CommonLogger;
import gov.llnl.lc.net.AbstractObjectClientProtocol;
import gov.llnl.lc.net.ObjectProtocolConstants;
import gov.llnl.lc.net.ObjectSessionInterface;

import java.io.EOFException;
import java.util.concurrent.TimeUnit;

/**********************************************************************
 * Describe purpose and responsibility of OsmEventInterface
 * <p>
 * @see  related classes and interfaces
 * @see  OsmApiSession#getEventApi()
 *
 * @author meier3
 * 
 * @version Jul 28, 2011 3:25:58 PM
 **********************************************************************/
public class OsmEventInterface extends ObjectSessionInterface implements Runnable, CommonLogger, OsmEventApi
{
  private OsmApiSession Session;  // the parent session
  private OsmObjectClient Protocol;
  
  /** a list of Listeners, interested in knowing when a message gets posted **/
  private static java.util.ArrayList <OsmEventListener> Event_Listeners =
    new java.util.ArrayList<OsmEventListener>();

  /** boolean specifying whether the thread should continue **/
  private volatile boolean Continue_Thread = true;
  
  /** boolean specifying whether the thread has been created **/
  protected boolean Thread_Exists = false;

  /** boolean specifying whether the thread is running **/
  protected boolean Thread_Running = false;

  /** thread responsible for listening for events **/
  private java.lang.Thread Listener_Thread;
  
  /** logger for the class **/
  private final java.util.logging.Logger classLogger =
      java.util.logging.Logger.getLogger( getClass().getName() );

  public OsmEventInterface(OsmApiSession session)
  {
  Session = session;
  Protocol = Session.ClientProtocol;
  
  createThread();
  }

  /************************************************************
   * Method Name:
   *  waitForEvent
   **/
  /**
   * Describe the method here
   *
   * @see gov.llnl.lc.infiniband.opensm.plugin.net.OsmEventApi#waitForEvent()
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @return
   ***********************************************************/


  public OsmEvent waitForEvent(long id)
  {
    OsmSessionEventSet Events = new OsmSessionEventSet(OsmEvent.OSM_ALL_EVENTS, id);
    
    Object inObj = null;
    try
    {
      inObj = Protocol.waitForEvents(Events);
    }
    catch (Exception e)
    {
      logger.severe(e.getMessage());
      logger.severe("The wait id is: " + id);
      classLogger.severe(e.getMessage());
      return null;
    }
    
    return (OsmEvent)inObj;
  }

  /************************************************************
   * Method Name:
   *  addListener
  **/
  /**
   * Add the provided listener to the list of subscribers interested
   * in being notified of events.  The listener provides an EnumSet
   * of OsmEvents that it cares about, and will only be notified
   * (via the osmEventUpdate() callback) when one of the events
   * in the listeners set occurs.
   *
   * @see gov.llnl.lc.infiniband.opensm.plugin.event.OsmEventUpdater#addListener(gov.llnl.lc.infiniband.opensm.plugin.event.OsmEventListener)
   *
   * @param listener
   ***********************************************************/
  
  @Override
  public synchronized void addListener(OsmEventListener listener) throws Exception
  {
    // add the listener, and its set of events
    classLogger.info("adding event listener");
    if(listener != null)
    {
      Event_Listeners.add(listener);
      
      // conditionally start the listener thread if not already running
      if(!Thread_Running)
        startThread();
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
    return "OsmEventInterface [Continue_Thread=" + Continue_Thread + ", Thread_Exists="
        + Thread_Exists + ", Thread_Running=" + Thread_Running + ", Listener_Thread="
        + Listener_Thread + "]\nEvents:";
  }



  /************************************************************
   * Method Name:
   *  removeListener
  **/
  /**
   * Describe the method here
   *
   * @see gov.llnl.lc.infiniband.opensm.plugin.event.OsmEventUpdater#removeListener(gov.llnl.lc.infiniband.opensm.plugin.event.OsmEventListener)
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @param listener
   * @return
   ***********************************************************/
  
  @Override
  public synchronized boolean removeListener(OsmEventListener listener) throws Exception
  {
    classLogger.info("removing event listener");
    if (Event_Listeners.remove(listener))
    {
     }
    return true;
  }
  
  /**************************************************************************
  *** Method Name:
  ***     updateAllListeners
  ***
  **/
  /**
  *** Notifies all listeners that some event has occurred.
  *** <p>
  ***
  **************************************************************************/
  private synchronized void updateAllListeners(OsmEvent osmEvent) throws Exception
  {
    for( int i = 0; i < Event_Listeners.size(); i++ )
    {
      OsmEventListener listener = (OsmEventListener)Event_Listeners.get( i );
      
      // does this listener care about this event?
      if((listener != null) && (listener.getEventSet().contains(osmEvent)))
        listener.osmEventUpdate(osmEvent);
    }
  }
  /*-----------------------------------------------------------------------*/

  /**************************************************************************
   *** Method Name:
   ***     createThread
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

   private void createThread()
   {
     if (!Thread_Exists)
     {
       // set up the thread to listen
       Listener_Thread = new Thread(this);
       Listener_Thread.setDaemon(true);
       Listener_Thread.setName("OsmEventInterfaceListenerThread");
       
       logger.info("Creating the " + Listener_Thread.getName() + " Thread");
       classLogger.info("Creating the " + Listener_Thread.getName() + " Thread");

       Thread_Exists = true;
     }
   }
   /*-----------------------------------------------------------------------*/

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
     * @throws Exception 
    ***
    *** @throws       Class_name  Description_of_exception_thrown__Delete_if_none
    **************************************************************************/

    private void startThread()
    {
      if (Thread_Exists && !Thread_Running)
      {
        classLogger.info("Starting the " + Listener_Thread.getName() + " Thread");
        Listener_Thread.start();
        Thread_Running = true;
      }
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
    int dwell = 500;
    
    long id = Session.getSessionStatus().getThreadId();
    long id2 = this.getSessionStatus().getThreadId();
    
    classLogger.info("Attempting to add this session to the Servers EventListener List ("+ id + ")");
    classLogger.info("Attempting to add this session to the Servers EventListener List ("+ id2 + ")");
    try
    {
      Protocol.addEventListenerSession(id);
      classLogger.info("Number of EventListeners is: " + Protocol.getNumEventListenerSessions(id));
    }
    catch (Exception e)
    {
      logger.severe("Couldn't add the " + Listener_Thread.getName() + " Thread to the EventManager (" + id + ")");
      classLogger.severe("Couldn't add the " + Listener_Thread.getName() + " Thread to the EventManager (" + id + ")");
      e.printStackTrace();
    }

    // check the Thread Termination Flag, and continue if Okay
    while(Continue_Thread)
    {
      try
      {
        // wait for an event, convert it, then signal listeners
        osm_event =  waitForEvent(id);
        
        if (osm_event != null)
        {
          updateAllListeners(osm_event);
          eventCount++;
          if((eventCount % 10) == 0)
            classLogger.info("Event count is: " + eventCount + ", last event: " + osm_event);
        }
        else
        {
          // this should not happen, slow things down....
          classLogger.severe("OsmEventInterface received a null event, pausing for " + dwell + " milliseconds");
          try
          {
            TimeUnit.MILLISECONDS.sleep(dwell);
          }
          catch (InterruptedException e)
          {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }

      }
      catch (Exception e)
      {
        // nothing to do yet
      }
    }
    classLogger.info("Attempting to remove this session from the Servers Listener List ("+ id + ")");
    try
    {
      Protocol.removeEventListenerSession(id);
      classLogger.info("Number of EventListeners is: " + Protocol.getNumEventListenerSessions(id));
    }
    catch (Exception e)
    {
      logger.severe("Couldn't remove the " + Listener_Thread.getName() + " Thread from the EventManager (" + id + ")");
      classLogger.severe("Couldn't remove the " + Listener_Thread.getName() + " Thread from the EventManager (" + id + ")");
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    logger.info("Terminating the " + Listener_Thread.getName() + " Thread");
    classLogger.info("Terminating the " + Listener_Thread.getName() + " Thread");
    Thread_Running = false;
   /* fall through, must be done! */
  }
  
  /************************************************************
   * Method Name:
   *  getSessionClientProtocol
  **/
  /**
   * Describe the method here
   *
   * @see gov.llnl.lc.net.ObjectSessionApi#getSessionClientProtocol()
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @return
   ***********************************************************/
  
  @Override
  public AbstractObjectClientProtocol getSessionClientProtocol()
  {
    return Protocol;
  }

  /************************************************************
   * Method Name:
   *  isAuthenticated
  **/
  /**
   * Describe the method here
   *
   * @see gov.llnl.lc.net.ObjectSessionApi#isAuthenticated()
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @return
   ***********************************************************/
  
  @Override
  public boolean isAuthenticated()
  {
    // TODO Auto-generated method stub
    return Session.isAuthenticated();
  }

  /************************************************************
   * Method Name:
   *  isConnected
  **/
  /**
   * Describe the method here
   *
   * @see gov.llnl.lc.net.ObjectSessionApi#isConnected()
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @return
   ***********************************************************/
  
  @Override
  public boolean isConnected()
  {
    // TODO Auto-generated method stub
    return Session.isConnected();
  }

  @Override
  public OSM_EventStats getOsmEventStats() throws Exception
  {
    Object inObj = null;
    try
    {
      inObj = Protocol.getOsmEventStats();
    }
    catch (Exception e)
    {
      logger.severe(e.getMessage());
      Session.setConnected(false);
      throw new EOFException(ObjectProtocolConstants.CONNECTION_CLOSED);
    }
    
    if (inObj instanceof OSM_EventStats)
      return (OSM_EventStats)inObj;
    return (OSM_EventStats)inObj;
  }
}
