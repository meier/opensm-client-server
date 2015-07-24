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
 *        file: EventListenerExample.java
 *
 *  Created on: Aug 24, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.client.test;

import gov.llnl.lc.infiniband.opensm.plugin.event.OSM_EventStats;
import gov.llnl.lc.infiniband.opensm.plugin.event.OsmEvent;
import gov.llnl.lc.infiniband.opensm.plugin.event.OsmEventListener;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmEventApi;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmServiceManager;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmSession;
import gov.llnl.lc.logging.CommonLogger;
import gov.llnl.lc.net.ObjectSession;
import gov.llnl.lc.time.TimeStamp;
import gov.llnl.lc.time.TimeStampedObject;
import gov.llnl.lc.util.BinList;
import gov.llnl.lc.util.TS_BinList;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

/**********************************************************************
 * Describe purpose and responsibility of EventListenerExample
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Aug 24, 2011 11:12:23 AM
 **********************************************************************/
public class EventListenerExample implements OsmEventListener, CommonLogger
{

  private OsmSession ParentSession = null;
  
  private EnumSet<OsmEvent> EventSet = null;
  
  private TS_BinList<TimeStampedObject<OsmEvent>> TeventBinList = null;
  
  private BinList<OsmEvent> eventBinList = null;
  
  long counter = 0;
  
  /* the one and only OsmServiceManager */
  private volatile OsmServiceManager OsmService = null;
  
  /************************************************************
   * Method Name:
   *  EventListenerExample
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param   describe the parameters if any
   *
   ***********************************************************/
  public EventListenerExample()
  {
    OsmService = OsmServiceManager.getInstance();
  }

  /************************************************************
   * Method Name:
   *  main
   **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @param args
   ***********************************************************/
  public void test(EnumSet<OsmEvent> eventSet) throws Exception
  {
    /* attempt to open a session */
    try
    {
      ParentSession = OsmService.openSession(null, null, null, null);
    }
    catch (Exception e)
    {
      logger.severe(e.getStackTrace().toString());
      System.exit(0);
    }
  
    TeventBinList = new TS_BinList<TimeStampedObject<OsmEvent>>();
    eventBinList = new BinList<OsmEvent>();
    ObjectSession sessStatus  = null;
    ObjectSession eventStatus = null;
    if(ParentSession != null)
    {
      sessStatus        = ParentSession.getSessionStatus();
      System.out.println("My Parent Session");
      System.out.println(sessStatus);
    }

    OsmEventApi eventInterface   = ParentSession.getEventApi();
    // the Events we want to listen for must be specified BEFORE we listen
    
    if(eventInterface != null)
    {
    EventSet = eventSet;
    eventStatus = eventInterface.getSessionStatus();
    System.out.println("===================================================");
    System.out.println("Event Session Status");
    System.out.println(eventStatus.toString());

    System.out.println("===================================================");
    System.out.println("Event Statistics");
    OSM_EventStats estats = eventInterface.getOsmEventStats();
    System.out.println(estats.toString());
    System.out.println("===================================================");

    
    // once you add this as a listener, you can't use the interface/session/stream
    // because it is in use by the events.  Only api calls that remain local
    // and don't use the stream are valid.  Add and Remove are permissible, but
    // you can no longer  getSessionStatus();
    eventInterface.addListener(this);

    System.out.println("My Event Session");
    System.out.println(eventStatus);

    // wait here, while events arrive, and my Update method gets called
    TimeUnit.MINUTES.sleep(15);
    eventInterface.removeListener(this);
    
    // wait for a second so anything left in the stream  can be drained, before I try to use it to get the session status
    TimeUnit.SECONDS.sleep(1);
    eventStatus = eventInterface.getSessionStatus();

    System.out.println("My Event Session");
    System.out.println(eventStatus);


    }
    else
    {
      System.err.println("Could not get the event interface");
      logger.severe("Could not get the event interface");
    }
    
    if(ParentSession != null)
    {
      sessStatus        = ParentSession.getSessionStatus();
      System.out.println("My Session");
      System.out.println(sessStatus);
    }
    /* all done, so close the session(s) */
    OsmService.closeSession(ParentSession);
    
    System.out.println("The BinList: " + eventBinList.size());
    ArrayList<Long> bSizes = eventBinList.getBinSizes();
    System.out.println("The BinSizes: " + bSizes.toString());
    
    Long cumEvents = 0L;
    for(int ndex = 0; ndex < eventBinList.size(); ndex++)
    {
      String key = eventBinList.getKey(ndex);
      long num = eventBinList.getBin(ndex).size();
      System.out.println("Event Time: " + key + ", size: " + num);
      cumEvents += num;
    }
    System.out.println("Total Events: " + cumEvents);
  }

  /************************************************************
   * Method Name:
   *  main
   **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @param args
   ***********************************************************/
  public static void main(String[] args) throws Exception
  {
    EventListenerExample ele = new EventListenerExample();
    ele.test(OsmEvent.OSM_ALL_EVENTS);

  }

  /************************************************************
   * Method Name:
   *  osmEventUpdate
  **/
  /**
   * Describe the method here
   *
   * @see gov.llnl.lc.infiniband.opensm.plugin.event.OsmEventListener#osmEventUpdate(gov.llnl.lc.infiniband.opensm.plugin.event.OsmEvent)
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @param osmEvent
   ***********************************************************/
  
  @Override
  public void osmEventUpdate(OsmEvent osmEvent)
  {
//    System.out.println("osmEventUpdate: (" + counter++ + ") ["+ osmEvent + "]");
//    logger.info("osmEventUpdate: (" + counter + ") ["+ osmEvent + "]");
    
    // collect all events in a BinList
//    eventBinList.addObject(new TimeStampedObject(osmEvent));
    eventBinList.add(osmEvent, (new TimeStamp()).toString());
  }

  /************************************************************
   * Method Name:
   *  getEventSet
  **/
  /**
   * Describe the method here
   *
   * @see gov.llnl.lc.infiniband.opensm.plugin.event.OsmEventListener#getEventSet()
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @return
   ***********************************************************/
  
  @Override
  public EnumSet<OsmEvent> getEventSet()
  {
    return EventSet;
  }

}
