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
 *        file: AdminInterfaceExample.java
 *
 *  Created on: Aug 24, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.client.test;

import gov.llnl.lc.infiniband.opensm.plugin.OsmNativeCommand;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmAdminApi;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmClientUserInfo;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmServerStatus;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmServiceManager;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmSession;
import gov.llnl.lc.logging.CommonLogger;
import gov.llnl.lc.net.ObjectSession;
import gov.llnl.lc.system.CommandLineArguments;
import gov.llnl.lc.system.CommandLineResults;
import gov.llnl.lc.system.whatsup.WhatsUpInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**********************************************************************
 * Describe purpose and responsibility of AdminInterfaceExample
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Aug 24, 2011 12:03:43 PM
 **********************************************************************/
public class AdminInterfaceExample implements CommonLogger
{

  private OsmSession ParentSession = null;
  
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
  public AdminInterfaceExample()
  {
    OsmService = OsmServiceManager.getInstance();
  }

  public void clear() throws Exception
  {
    ParentSession.getAdminApi().clearSessionHistory();
  }
  
  public void close() throws Exception
  {
    /* all done, so close the session(s) */
    OsmService.closeSession(ParentSession);
  
  }
  /************************************************************
   * Method Name:
   *  test
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
  public void test() throws Exception
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
  
    ObjectSession sessStatus  = null;
    if(ParentSession != null)
    {
      sessStatus        = ParentSession.getSessionStatus();
      System.out.println("My Parent Session");
      System.out.println(sessStatus);
    }

    OsmAdminApi adminInterface = ParentSession.getAdminApi();
    WhatsUpInfo whatsUp = adminInterface.getWhatsUpInfo();
    OsmClientUserInfo u = new OsmClientUserInfo();
    System.out.println("===================================================");
    System.out.println("UserInfo");
    System.out.println(u.toString());
    System.out.println("** An Authorized User? ** : " + adminInterface.isUserAuthorized(u));

    OsmServerStatus servStatus = adminInterface.getServerStatus();
    sessStatus = ParentSession.getSessionStatus();
    System.out.println("===================================================");
    System.out.println("WhatsUpInfo");
    System.out.println(whatsUp.toString());
    System.out.println("===================================================");
    System.out.println("The Server");
    System.out.println(servStatus);
    System.out.println("===================================================");
    System.out.println("My Session");
    System.out.println(sessStatus);
    System.out.println("===================================================");

    TimeUnit.MINUTES.sleep(1);

    OsmServerStatus servStatus1 = adminInterface.getServerStatus();
    sessStatus = ParentSession.getSessionStatus();
    System.out.println("The Server after sleep");
    System.out.println(servStatus1);
    System.out.println("===================================================");
    System.out.println("My Session");
    System.out.println(sessStatus);
    System.out.println("===================================================");
    System.out.println("The LS Command");
    CommandLineResults results = adminInterface.invokePrivilegedCommand(new CommandLineArguments("ls -lart"));

    System.out.println(results);
    System.out.println("===================================================");
    System.out.println("Command Check");
    boolean ok = adminInterface.isCommandAuthorized(new CommandLineArguments(OsmNativeCommand.OSM_NATIVE_ECHO.getCommandName() + " the NATIVE ECHO"));

    System.out.println(ok);
    System.out.println("===================================================");
    System.out.println("Command Check");
    ok = adminInterface.isCommandAuthorized(new CommandLineArguments(OsmNativeCommand.OSM_NATIVE_LSWEEP.getCommandName() + " the NATIVE ECHO"));

    System.out.println(ok);
    System.out.println("===================================================");
    System.out.println("Command Check");
    ok = adminInterface.isCommandAuthorized(new CommandLineArguments("ls"));

    System.out.println(ok);
    System.out.println("===================================================");
    System.out.println("Command Check");
    ok = adminInterface.isCommandAuthorized(new CommandLineArguments("reboot now"));

    System.out.println(ok);
    System.out.println("===================================================");
    System.out.println("The Native Command");
    results = adminInterface.invokePrivilegedCommand(new CommandLineArguments(OsmNativeCommand.OSM_NATIVE_ECHO.getCommandName() + " the NATIVE ECHO"));

    System.out.println(results);
    System.out.println("===================================================");
    System.out.println("The Native Command");
    results = adminInterface.invokePrivilegedCommand(new CommandLineArguments(OsmNativeCommand.OSM_NATIVE_LSWEEP.getCommandName() + " the NATIVE Light Sweep"));

    System.out.println(results);
    System.out.println("===================================================");

    System.out.println("The Native Command");
    results = adminInterface.invokePrivilegedCommand(new CommandLineArguments(OsmNativeCommand.OSM_NATIVE_PPERIOD.getCommandName() + " 150"));

    System.out.println(results);
    System.out.println("===================================================");

    System.out.println("The Native Command");
    results = adminInterface.invokePrivilegedCommand(new CommandLineArguments(OsmNativeCommand.OSM_NATIVE_PPERIOD.getCommandName() + " 145 ;lsd"));

    System.out.println(results);
    System.out.println("===================================================");

    System.out.println("The Native Command");
    results = adminInterface.invokePrivilegedCommand(new CommandLineArguments(OsmNativeCommand.OSM_NATIVE_LOGLEVEL.getCommandName() + " 0x82"));

    System.out.println(results);
    System.out.println("===================================================");

    System.out.println("The Native Command");
    results = adminInterface.invokePrivilegedCommand(new CommandLineArguments(OsmNativeCommand.OSM_NATIVE_LOGLEVEL.getCommandName() + " x84"));

    System.out.println(results);
    System.out.println("===================================================");

    System.out.println("The Native Command");
    results = adminInterface.invokePrivilegedCommand(new CommandLineArguments(OsmNativeCommand.OSM_NATIVE_UPDATE_DESC.getCommandName()));

    System.out.println(results);
    System.out.println("===================================================");

    TimeUnit.MINUTES.sleep(1);

    System.out.println("The Native Command");
    results = adminInterface.invokePrivilegedCommand(new CommandLineArguments(OsmNativeCommand.OSM_NATIVE_REROUTE.getCommandName()));

    System.out.println(results);
    System.out.println("===================================================");

    TimeUnit.MINUTES.sleep(1);

    System.out.println("The Native Command");
    results = adminInterface.invokePrivilegedCommand(new CommandLineArguments(OsmNativeCommand.OSM_NATIVE_HSWEEP.getCommandName()));

    System.out.println(results);
    System.out.println("===================================================");

    TimeUnit.MINUTES.sleep(1);

    System.out.println("The Native Command");
    results = adminInterface.invokePrivilegedCommand(new CommandLineArguments(OsmNativeCommand.OSM_NATIVE_LSWEEP.getCommandName()));

    System.out.println(results);
    System.out.println("===================================================");

    TimeUnit.SECONDS.sleep(15);

    System.out.println("The Native Command");
    results = adminInterface.invokePrivilegedCommand(new CommandLineArguments(OsmNativeCommand.OSM_NATIVE_PCLEAR.getCommandName()));

    System.out.println(results);
    System.out.println("===================================================");

    TimeUnit.SECONDS.sleep(15);

    System.out.println("The Native Command");
    results = adminInterface.invokePrivilegedCommand(new CommandLineArguments(OsmNativeCommand.OSM_NATIVE_PSWEEP.getCommandName()));

    System.out.println(results);
    System.out.println("===================================================");

    TimeUnit.SECONDS.sleep(15);

  }

  /************************************************************
   * Method Name:
   *  killTest
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
  public static void killTest(String[] args) throws Exception
  {
    InputStreamReader istream = new InputStreamReader(System.in);
    BufferedReader bufRead = new BufferedReader(istream);

    OsmSession ParentSession = null;

    /* the one and only OsmServiceManager */
    OsmServiceManager OsmService = OsmServiceManager.getInstance();

    try
    {
      ParentSession = OsmService.openSession(null, null, null, null);
    }
    catch (Exception e)
    {
      logger.severe(e.getStackTrace().toString());
      System.exit(0);
    }
    ObjectSession sessStatus = null;
    if (ParentSession != null)
    {
      sessStatus = ParentSession.getSessionStatus();
      System.out.println("My Session");
      System.out.println(sessStatus);
    }

    OsmSession session2 = null;
    try
    {
      session2 = OsmService.openSession(ParentSession);
    }
    catch (Exception e)
    {

      logger.severe(e.getMessage());
      logger.severe(e.getStackTrace().toString());
      e.printStackTrace();
      System.exit(0);
    }
    OsmAdminApi adminInterface = ParentSession.getAdminApi();
    OsmAdminApi adminInterface2 = session2.getAdminApi();
    OsmServerStatus servStatus = adminInterface.getServerStatus();
    sessStatus = ParentSession.getSessionStatus();
    System.out.println("===================================================");
    System.out.println("The Server");
    System.out.println(servStatus);
    System.out.println("===================================================");
    System.out.println("My Session");
    System.out.println(sessStatus);
    System.out.println("===================================================");

    boolean clearStatus = adminInterface.clearSessionHistory();
    servStatus = adminInterface.getServerStatus();
    System.out.println("The Server after clear");
    System.out.println(servStatus);
    System.out.println("===================================================");

    try
    {
      System.out.println("Please Enter the TheadId to kill: ");
      String threadId = bufRead.readLine();
      /* try to kill this ID */
      boolean killStatus = adminInterface.killSession(Long.parseLong(threadId));
    }
    catch (IOException err)
    {
      System.out.println("Error reading line");
    }

    System.out.println("===================================================");
    servStatus = adminInterface.getServerStatus();
    System.out.println("The Server after kill");
    System.out.println(servStatus);

    System.out.println("===================================================");

    TimeUnit.MINUTES.sleep(3);

    /* all done, so close the session(s) */
    OsmService.closeSession(ParentSession);

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
    System.err.println("This is the adminInterface");
    AdminInterfaceExample ade = new AdminInterfaceExample();
    ade.test();
//    ade.clear();
    ade.close();
  }

}
