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
 *        file: OsmConsoleClient.java
 *
 *  Created on: Jun 27, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.net.test;

import gov.llnl.lc.infiniband.opensm.plugin.event.OsmEvent;
import gov.llnl.lc.infiniband.opensm.plugin.event.OsmEventListener;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmAdminApi;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmEventApi;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmServerStatus;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmServiceManager;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmSession;
import gov.llnl.lc.logging.CommonLogger;
import gov.llnl.lc.net.ObjectSession;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;


/**********************************************************************
 * OsmConsoleClient is a simple command line test and diagnostic tool
 * used primarily for development purposes.
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Jun 27, 2011 11:35:43 AM
 **********************************************************************/
public class OsmConsoleClient implements Runnable, OsmEventListener, CommonLogger
{
  private static OsmSession ParentSession = null;
  
  private EnumSet<OsmEvent> EventSet = null;


  /************************************************************
   * Method Name:
   *  main
   **/
  /**
   * Exercises the main features of the OsmService and serves
   * as and example as to its intended use.
   *
   * @see     describe related java objects  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @param args
   * @throws Exception 
   ***********************************************************/
  public static void main(String[] args) throws Exception
  {
    InputStreamReader istream = new InputStreamReader(System.in) ;

    BufferedReader bufRead = new BufferedReader(istream) ;
    /* the one and only OsmServiceManager */
    OsmServiceManager osmService = OsmServiceManager.getInstance();
    
    /* attempt to open a session */
//    OsmSession session = null;
    try
    {
      ParentSession = osmService.openSession("corni.llnl.gov", null, null, null);
    }
    catch (Exception e)
    {
      logger.severe(e.getStackTrace().toString());
      System.exit(0);
    }
    ObjectSession sessStatus = null;
    System.out.println("Here 1");
    if(ParentSession != null)
    {
      sessStatus        = ParentSession.getSessionStatus();
    System.out.println("My Session");
    System.out.println(sessStatus);
    }
    
    System.out.println("===================================================");

    OsmSession session2 = null;
    try
    {
      session2 = osmService.openSession(ParentSession);
    }
    catch (Exception e)
    {
      
      logger.severe(e.getMessage());
      logger.severe(e.getStackTrace().toString());
      e.printStackTrace();
      System.exit(0);
    }
    System.out.println("Here I am today!!");
    
    System.out.println("I think I have two sessions");

    /* this session provides various api's, so get the ones I want */
//    OsmClientApi clientInterface = ParentSession.getClientApi();
    OsmAdminApi adminInterface   = ParentSession.getAdminApi();
    OsmEventApi eventInterface   = ParentSession.getEventApi();
    
    eventInterface.addListener(new OsmConsoleClient(EnumSet.of(OsmEvent.OSM_EVENT_STATE_CHANGE, OsmEvent.OSM_EVENT_TRAP, OsmEvent.OSM_EVENT_PORT_DATA_COUNTERS)));
    
//    OsmClientApi clientInterface2 = session2.getClientApi();
//    OsmAdminApi adminInterface2   = session2.getAdminApi();
//    OsmEventApi eventInterface2   = session2.getEventApi();
    
    /* use the api's to get information */
//    OSM_Nodes nodes     = clientInterface.getOsmNodes();
//    OSM_SysInfo sysInfo = clientInterface.getOsmSysInfo();
    OsmServerStatus servStatus = adminInterface.getServerStatus();
    sessStatus        = ParentSession.getSessionStatus();
//    System.out.println("Here 3");
//    System.out.println("===================================================");
//    System.out.println("The System");
//    System.out.println(sysInfo);
//    System.out.println("===================================================");
//    System.out.println("The Nodes");
//    System.out.println(nodes);
    System.out.println("===================================================");
    System.out.println("The Server");
    System.out.println(servStatus);
    System.out.println("===================================================");
    System.out.println("My Session");
    System.out.println(sessStatus);    
    System.out.println("===================================================");

//    boolean clearStatus             = adminInterface.clearSessionHistory();
//    servStatus = adminInterface.getServerStatus();
//    System.out.println("The Server after clear");
//    System.out.println(servStatus);
//    System.out.println("===================================================");
//    
//    try 
//    {
//      System.out.println("Please Enter the TheadId to kill: ");
//      String threadId = bufRead.readLine();
//      /* try to kill this ID */
//      boolean killStatus  = adminInterface.killSession(Long.parseLong(threadId)); 
// }
// catch (IOException err)
// {
//      System.out.println("Error reading line");
// }
// OsmSession session3 = null;
// try
// {
//   session3 = osmService.openSession(session);
// }
// catch (Exception e)
// {
//   
//   logger.severe(e.getMessage());
//   logger.severe(e.getStackTrace().toString());
//   e.printStackTrace();
//   System.exit(0);
// }
// System.out.println("Here 2");
// 
// System.out.println("I think I have two sessions");
//
// 
// System.out.println("===================================================");
// servStatus = adminInterface.getServerStatus();
// System.out.println("The Server after kill");
// System.out.println(servStatus);
// 
// System.out.println("===================================================");

// sysInfo = clientInterface.getOsmSysInfo();
// OSM_Ports ports = clientInterface.getOsmPorts();
// OSM_Stats stats = clientInterface.getOsmStats();
// OSM_Subnet subnet = clientInterface.getOsmSubnet();
// System.out.println(sysInfo);
// System.out.println("===================================================");
//// System.out.println(ports);
// System.out.println("===================================================");
// System.out.println(stats);
// System.out.println("===================================================");
// System.out.println(subnet);
// System.out.println("===================================================");
 
// wait for, and print out the next two events
// OsmEvent event = eventInterface2.waitForEvent();
// System.out.println("Second wait for event: " + event);
 
// OsmTestApi testInterface = ParentSession.getTestApi();
// System.out.printf("Test API: OSM Version (%s), and JPI Version (%s)\n", testInterface.getOsmVersion(), testInterface.getPluginVersion());
//
// // now try to get the same event from two different sessions simultaneously
//// new OsmConsoleClient(ParentSession);
//// EnumSet<OsmEvent> MY_EVENTS = EnumSet.of(OsmEvent.OSM_EVENT_STATE_CHANGE, OsmEvent.OSM_EVENT_TRAP, OsmEvent.OSM_EVENT_PORT_DATA_COUNTERS);
//
// System.out.printf("Test API: OSM Version (%s), and JPI Version (%s)\n", testInterface.getOsmVersion(), testInterface.getPluginVersion());

// System.out.println("Just launched a separate thread to wait, now waiting locally for an event");
// for(int j = 0; j < 50; j++)
// {
//   event = eventInterface.waitForEvent();
////   event = eventInterface.waitForEvent(MY_EVENTS);
//   System.out.println("That was my " + j + " event: " + event);
//   
// }
// event = eventInterface.waitForEvent();
// System.out.println("Third wait for event: " + event);
// 
// 
TimeUnit.MINUTES.sleep(3);

 /* all done, so close the session(s) */
    osmService.closeSession(ParentSession);
  }

  /************************************************************
   * Method Name:
   *  OsmConsoleClient
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param   describe the parameters if any
   *
   * @param eventSet
   ***********************************************************/
  public OsmConsoleClient(EnumSet<OsmEvent> eventSet)
  {
    super();
    EventSet = eventSet;
  }

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
    logger.severe("Attempting to open child service in seperate thread.");

    /* the one and only OsmServiceManager */
    OsmServiceManager osmService = OsmServiceManager.getInstance();
    OsmSession session2 = null;
    OsmEventApi eventInterface = null;
    OsmEvent event = null;
    try
    {
      session2 = osmService.openSession(ParentSession);
      eventInterface   = session2.getEventApi();
    }
    catch (Exception e)
    {
      
      logger.severe(e.getMessage());
      logger.severe(e.getStackTrace().toString());
      e.printStackTrace();
      return;
    }
    logger.severe("Past the attempt to open a service from a thread");
    
    System.out.println("I think I have another sessions");
    
    EnumSet<OsmEvent> MY_EVENTS = EnumSet.of(OsmEvent.OSM_EVENT_STATE_CHANGE, OsmEvent.OSM_EVENT_PORT_ERRORS);
    
 // System.out.println("Just launched a separate thread to wait, now waiting locally for an event");
//  for(int j = 0; j < 25; j++)
//  {
//    event = eventInterface.waitForEvent(MY_EVENTS);
//    System.out.println("Thread:  That was my " + j + " event: " + event);
//    
//  }
  }

  /************************************************************
   * Method Name:
   *  OsmConsoleClient
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param   describe the parameters if any
   *
   ***********************************************************/
  public OsmConsoleClient( OsmSession ParentSession)
  {
    // TODO Auto-generated constructor stub
    new Thread(this, "OsmConsoleClient").start();
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
    
    System.out.println("osmEventUpdate: " + osmEvent);
    
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
