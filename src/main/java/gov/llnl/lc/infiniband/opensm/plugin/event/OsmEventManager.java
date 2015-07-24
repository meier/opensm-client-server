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
 *        file: OsmEventManager.java
 *
 *  Created on: Oct 18, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.event;

import gov.llnl.lc.infiniband.opensm.plugin.OsmInterface;
import gov.llnl.lc.infiniband.opensm.plugin.OsmPluginMain;
import gov.llnl.lc.infiniband.opensm.plugin.data.OsmUpdateManager;

import java.util.ArrayList;
import java.util.EnumSet;

public class OsmEventManager implements Runnable, gov.llnl.lc.logging.CommonLogger
{
  /** the data synchronization object **/
  private static Boolean semaphore            = new Boolean( true );
  
  /** a running total number of events, used as the heartbeat **/
  private volatile static long TotalEvents = 0L;
  
  private volatile static OSM_EventStats EventStatistics = null;

  /** the event synchronization object **/
  private volatile static OSM_EventSync eventSync = new OSM_EventSync();

  /** the one and only <code>OsmEventManager</code> Singleton **/
  private volatile static OsmEventManager globalOsmEventManager  = null;

  /** a list of Listeners, interested in knowing about events **/
  private volatile static java.util.Hashtable<Long, ArrayList <OsmTimeStampedEvent>> Event_Listener_Queues =
    new java.util.Hashtable<Long, ArrayList <OsmTimeStampedEvent>>();

  /** boolean specifying whether the thread should continue **/
  private volatile static boolean Continue_Thread = true;
  
  /** thread responsible for listening for events **/
  private static java.lang.Thread Listener_Thread;
  
  /** logger for the class **/
  private final java.util.logging.Logger classLogger =
      java.util.logging.Logger.getLogger( getClass().getName() );

  private OsmEventManager()
  {
    /* set the size, and initialize it to zero */
    EventStatistics = new OSM_EventStats(new long[OsmEvent.OSM_EVENT_MAX.getEvent()]);
    
    // set up the thread to listen
    Listener_Thread = new Thread(this);
    Listener_Thread.setDaemon(true);
    Listener_Thread.setName("OsmEventManagerListenerThread");
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
    
    logger.info("Initializing the EventManager");
    classLogger.info("Initializing the EventManager");
    
    /* do whatever it takes to initialize the event manager */
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
      logger.info("Terminating the EventManager");
      classLogger.info("Terminating the EventManager");
      stopThread();
    }
    
   /**************************************************************************
  *** Method Name:
  ***     getInstance
  **/
  /**
  *** Get the singleton OsmEventManager. This can be used if the application wants
  *** to share one manager across the whole JVM.  Currently I am not sure
  *** how this ought to be used.
  *** <p>
  ***
  *** @return       the GLOBAL (or shared) OsmEventManager
  **************************************************************************/

  public static OsmEventManager getInstance()
  {
    synchronized( OsmEventManager.semaphore )
    {
      if ( globalOsmEventManager == null )
      {
        globalOsmEventManager = new OsmEventManager( );
      }
      return globalOsmEventManager;
    }
  }
  /*-----------------------------------------------------------------------*/

  public Object clone() throws CloneNotSupportedException 
  {
    throw new CloneNotSupportedException(); 
  }
  
  
  private OsmEvent waitForOsmEvent(long timeout, EnumSet<OsmEvent> Events) throws InterruptedException
  {
    /** block, pend, wait for one of the events in the set to occur
     * or for the specified timeout (in seconds).  If one of the events
     * occurred, the event that satisfied the wait is returned.  If a wait
     * failed before the timeout elapsed, then a timeout event is returned
     **/
    
    // TODO fix the timeout so it doesn't reset if undesired event occurs.
    
    // block or wait here for ANY native event
    OsmEvent rtnEvent = eventSync.waitForEventSync(timeout);
    
    // is the returned event in the EnumSet?  if so return it, if not wait again
      if((rtnEvent.compareTo(OsmEvent.OSM_EVENT_TIMEOUT) == 0) || ((Events != null) && (Events.contains(rtnEvent))) )
      {
        classLogger.info("satisfied " + rtnEvent);

        return rtnEvent;
      }
      // if not an event I care about, wait again (recursive, re-entrant)
      // TODO - danger, timeout should end recursion for RARE events
      //        recursion can become very deep if the desired event(s) never occur
      //        among frequently occurring events.
      classLogger.info("not satisfied " + rtnEvent);
    return this.waitForOsmEvent(timeout, OsmEvent.OSM_ALL_EVENTS);
  }
  
  public synchronized boolean removeSessionListener(Long sessionId)
  {
        Event_Listener_Queues.remove(sessionId);
//        classLogger.info("SessionListener Removed: " + sessionId);
        return true;
  }
    
  public synchronized boolean addSessionListener(Long sessionId)
  {
        Event_Listener_Queues.put(sessionId, new java.util.ArrayList<OsmTimeStampedEvent>());
//        classLogger.info("SessionListener Added: " + sessionId);
        return true;
  }
    
  public int numSessionListeners()
  {
//    classLogger.info("Number of Event SessionListeners: " + Event_Listener_Queues.size());
    return Event_Listener_Queues.size();
   }
    
  public synchronized ArrayList<Long> getSessionListenerIds()
  {
    ArrayList<Long> rtnList = new java.util.ArrayList<Long>();
    if(Event_Listener_Queues.size() > 0)
        {
        // build up the array list
          for(Long id: Event_Listener_Queues.keySet())
          {
            rtnList.add(id);
          }
          classLogger.info("All of the event ids: " + rtnList);
        }
        return rtnList;
  }
    
  public synchronized long getTotalEvents()
  {
    return TotalEvents;
  }
    
  public synchronized OSM_EventStats getEventStatistics()
  {
    return EventStatistics;
  }
    
  public OsmEvent waitForOsmEvents(long timeout, OsmSessionEventSet eSet) throws InterruptedException
  {
//    check my sessions event queue, and return the next available (oldest) event.  If the event queue is
//    empty, then wait here until the timeout
    
    Long sessionId = eSet.SessionId;
    ArrayList<OsmTimeStampedEvent> queue = Event_Listener_Queues.get(sessionId);
    
    if(queue == null)
    {
      classLogger.warning("The event listener queue for this session " + sessionId + " was null");
    }
    if((queue == null) || (queue.isEmpty()))
    {
      // block or wait here for ANY native event
      return eventSync.waitForEventSync(timeout);
    }
    // the queue has elements in it, so return the oldest one from index 0
    OsmTimeStampedEvent tsEvent = queue.remove(0);    
    return tsEvent.getEvent();
  }
    
  @Override
  public void run()
  {
    long eventCount = 0;
    OsmPluginMain jServ = OsmPluginMain.getInstance();
    OsmInterface osmInt = jServ.getInterface();
    
    int osm_event;
    OsmEvent eventObj;
    OsmTimeStampedEvent tsEvent;

    // check the Thread Termination Flag, and continue if Okay
    while(Continue_Thread)
    {
      try
      {
        TotalEvents++;
        
        // wait for the native event, convert it, then signal listeners
        osm_event = osmInt.wait_for_event(300000);
        eventObj = OsmEvent.get(osm_event);
        EventStatistics.incrementCounter(eventObj);
        tsEvent = new OsmTimeStampedEvent(eventObj);
        
        if(!addToEventQueues(tsEvent))
        {
          logger.severe("Could not add (" + tsEvent + ") to Event Queues");
          classLogger.severe("Could not add (" + tsEvent + ") to Event Queues");
        }
          
        eventSync.setEventSync(eventObj);
        eventCount++;
        classLogger.info("Event count is: " + eventCount + ", current event: " + osm_event);
      }
      catch (Exception e)
      {
        // nothing to do yet
      }
    }
    logger.info("Terminating the " + Listener_Thread.getName() + " Thread");
    classLogger.info("Terminating the " + Listener_Thread.getName() + " Thread");
   /* fall through, must be done! */
  }


  /************************************************************
   * Method Name:
   *  addToEventQueues
  **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @param tsEvent
   * @return
   ***********************************************************/
  private boolean addToEventQueues(OsmTimeStampedEvent tsEvent)
  {
    // iterate through all the queues, and add this event to each one
    if (Event_Listener_Queues.isEmpty())
    {
      // can't add if no listeners, so this is not an error
      classLogger.info("Currently no Session Event Listeners");
      return true;
    }

    for (Long id : Event_Listener_Queues.keySet())
    {
      // add this event to all queues
      Event_Listener_Queues.get(id).add(tsEvent);
    }

    // if a registered listener is consuming events, the queue size
    // should be one, or less. If the queue size is large, there may be
    // a problem, or the listener is just slow
    for (ArrayList<OsmTimeStampedEvent> queue : Event_Listener_Queues.values())
    {
      int qs = queue.size();
      if (qs > 1)
        classLogger.info("The queue size is: " + qs);
      if (qs > 100)
      {
        classLogger.warning("Session Event Listener Queue is not being emptied, check it!");
        logger.warning("Session Event Listener Queue is not being emptied, check it!");
      }
    }
    return true;
  }

}
