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
 *        file: PrivilegedCommandExample.java
 *
 *  Created on: Aug 24, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.client.test;

import gov.llnl.lc.infiniband.opensm.plugin.OsmNativeCommand;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmAdminApi;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmServiceManager;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmSession;
import gov.llnl.lc.logging.CommonLogger;
import gov.llnl.lc.system.CommandLineArguments;
import gov.llnl.lc.system.CommandLineResults;

import java.util.Arrays;
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
public class PrivilegedCommandExample implements CommonLogger
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
  public PrivilegedCommandExample()
  {
    OsmService = OsmServiceManager.getInstance();
  }

  public CommandLineResults reroute() throws Exception
  {
    OsmAdminApi adminInterface = ParentSession.getAdminApi();
    return adminInterface.invokePrivilegedCommand(new CommandLineArguments(OsmNativeCommand.OSM_NATIVE_REROUTE.getCommandName() + " the reroute cmd"));
  }
  
  public CommandLineResults updateDescription() throws Exception
  {
    OsmAdminApi adminInterface = ParentSession.getAdminApi();
    return adminInterface.invokePrivilegedCommand(new CommandLineArguments(OsmNativeCommand.OSM_NATIVE_UPDATE_DESC.getCommandName() + " the update desc cmd"));
  }
  
  public CommandLineResults lightSweep() throws Exception
  {
    OsmAdminApi adminInterface = ParentSession.getAdminApi();
    return adminInterface.invokePrivilegedCommand(new CommandLineArguments(OsmNativeCommand.OSM_NATIVE_LSWEEP.getCommandName() + " the NATIVE Light Sweep"));
  }
  
  public CommandLineResults heavySweep() throws Exception
  {
    OsmAdminApi adminInterface = ParentSession.getAdminApi();
    return adminInterface.invokePrivilegedCommand(new CommandLineArguments(OsmNativeCommand.OSM_NATIVE_HSWEEP.getCommandName() + " the NATIVE Heavy Sweep"));
  }
  
  public CommandLineResults forcePerfMgrSweep() throws Exception
  {
    OsmAdminApi adminInterface = ParentSession.getAdminApi();
    return adminInterface.invokePrivilegedCommand(new CommandLineArguments(OsmNativeCommand.OSM_NATIVE_PSWEEP.getCommandName() + " the prfmgr sweep cmd"));
  }
  
  public CommandLineResults clearPerfMgrCounters() throws Exception
  {
    OsmAdminApi adminInterface = ParentSession.getAdminApi();
    return adminInterface.invokePrivilegedCommand(new CommandLineArguments(OsmNativeCommand.OSM_NATIVE_PCLEAR.getCommandName() + " the prfmgr clear cmd"));
  }
  
  public CommandLineResults setPerfMgrSweepPeriod(int secs) throws Exception
  {
    OsmAdminApi adminInterface = ParentSession.getAdminApi();
    return adminInterface.invokePrivilegedCommand(new CommandLineArguments(OsmNativeCommand.OSM_NATIVE_PPERIOD.getCommandName() + " " + Integer.toString(secs)));
  }
  
  public CommandLineResults setLogLevel(int level) throws Exception
  {
    OsmAdminApi adminInterface = ParentSession.getAdminApi();
    return adminInterface.invokePrivilegedCommand(new CommandLineArguments(OsmNativeCommand.OSM_NATIVE_LOGLEVEL.getCommandName() + " 0x" + Integer.toHexString(level)));
  }
  
  public CommandLineResults enablePort(int lid, int portNum) throws Exception
  {
    return ibportstate(lid + " " + portNum + " enable");
  }
  
  public CommandLineResults disablePort(int lid, int portNum) throws Exception
  {
    return ibportstate(lid + " " + portNum + " disable");
  }
  
  public CommandLineResults ibportstate(String args) throws Exception
  {
    OsmAdminApi adminInterface = ParentSession.getAdminApi();
    return adminInterface.invokePrivilegedCommand(new CommandLineArguments("ibportstate " + args));
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
      ParentSession = OsmService.openSession(null, "10013", null, null);
    }
    catch (Exception e)
    {
      logger.severe(e.getStackTrace().toString());
      System.exit(0);
    }
  
    if(ParentSession != null)
    {
      System.out.println(forcePerfMgrSweep());
      TimeUnit.MINUTES.sleep(1);
      System.out.println(disablePort(4, 5));
      TimeUnit.MINUTES.sleep(1);
      System.out.println(disablePort(4, 6));
      TimeUnit.MINUTES.sleep(1);
      System.out.println(reroute());
      TimeUnit.MINUTES.sleep(1);
      System.out.println(enablePort(4, 5));
      TimeUnit.MINUTES.sleep(1);
      System.out.println(setPerfMgrSweepPeriod(133));
      TimeUnit.MINUTES.sleep(1);
      System.out.println(enablePort(4, 6));
      TimeUnit.MINUTES.sleep(1);
      System.out.println(updateDescription());
    }
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
    System.err.println("This is the Privileged Commands");
    PrivilegedCommandExample ade = new PrivilegedCommandExample();
    ade.test();
    if(args.length > 1)
    {
      System.out.println(Arrays.toString(args));
//      ade.ibportstate(args);
      
    }
    ade.close();
  }

}
