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
 *        file: OsmServiceStatus.java
 *
 *  Created on: Dec 16, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.client.test;

import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Ports;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Stats;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_SysInfo;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmAdminApi;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmClientApi;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmServerStatus;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmServiceManager;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmSession;
import gov.llnl.lc.net.ObjectSession;
import gov.llnl.lc.time.TimeStamp;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**********************************************************************
 * Describe purpose and responsibility of OsmServiceStatus
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Dec 16, 2011 1:24:25 PM
 **********************************************************************/
public class OsmServiceStatus
{
  static final String NEW_LINE = System.getProperty("line.separator");
  
  /** the command line options, if any **/
  private static CommandLine cmd_line = null;
  private static String hostName = null;
  private static String portNum = null;
  
  private static OSM_SysInfo SysInfo = null;
  private static OSM_Stats Stats     = null;
  
  /** from the admin interface **/
  private static ObjectSession ParentSessionStatus  = null;
  private static OsmServerStatus RemoteServerStatus = null;


  private static final String USAGE = "[-h=<host url>] [-p=<port num>]";
  private static final String HEADER =
                          "OsmServerStatus - basic status tool";
  private static final String FOOTER =
                          "Copyright (C) 2011, Lawrence Livermore National Security, LLC";
  
  public static boolean parseCommandLineOptions(String[] args)
  {
    Options options = new Options();
    CommandLine line = null;
    
    Option help = new Option( "?", "help", false, "print this message" );
    Option version = new Option( "v", "version", false, "print the version information and exit" );
    
    Option host_name   = OptionBuilder.hasArg(true).withArgName( "host url" ).withValueSeparator('=').withDescription(  "the host name of the OSM Monitoring Service" ).withLongOpt("host").create( "h" );

    Option port_num   = OptionBuilder.hasArg(true).withArgName( "port #" ).withValueSeparator('=').withDescription(  "the port number of the service" ).withLongOpt("port").create( "p" );

    options.addOption( host_name );
    options.addOption( port_num );
    options.addOption( help );
    options.addOption( version );
    
    CommandLineParser parser = new GnuParser();
    try 
    {
        // parse the command line arguments
        line = parser.parse( options, args, false );
    }
    catch( ParseException exp )
    {
        // oops, something went wrong
        System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
    }
    // save the command line in case we want it elsewhere
    
    
    cmd_line = line;
    
    if(line.hasOption("host"))
    {
      hostName = line.getOptionValue("host");
    }

    if(line.hasOption("port"))
    {
      portNum = line.getOptionValue("port");
    }

    if(line.hasOption("help"))
    {
      printUsage(options);
      /* end here */
      System.exit(0);
    }
    
    return true;
  }
  
  
private static void printUsage(Options options) 
{
  HelpFormatter helpFormatter = new HelpFormatter( );
  helpFormatter.setWidth( 80 );
  helpFormatter.printHelp( USAGE, HEADER, options, FOOTER );
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
   * @throws Exception 
   ***********************************************************/
  public static void main(String[] args) throws Exception
  {
    parseCommandLineOptions(args);
    OsmSession ParentSession = null;

    /* the one and only OsmServiceManager */
    OsmServiceManager OsmService = OsmServiceManager.getInstance();

    try
    {
      ParentSession = OsmService.openSession(hostName, portNum, null, null);
    }
    catch (Exception e)
    {
      System.err.println("Could not open a session to: " + hostName + ":" + portNum);
      System.exit(0);
    }

    if (ParentSession != null)
    {
      OsmClientApi clientInterface = ParentSession.getClientApi();
      OsmAdminApi   adminInterface = ParentSession.getAdminApi();
      
      if (clientInterface != null)
      {
        SysInfo = clientInterface.getOsmSysInfo();
        if(SysInfo != null)
          System.out.println(SysInfo.OsmJpi_Version + ", built for (" + SysInfo.OpenSM_Version + ")");
        else
          System.out.println("The system information from the OMS was unavailable");
      }

      if (adminInterface != null)
      {
        RemoteServerStatus = adminInterface.getServerStatus();
        if(RemoteServerStatus != null)
        {
          System.out.println(RemoteServerStatus.Server.getHost() + ", Service up since: " + RemoteServerStatus.Server.getStartTime().toString());
          long diffMillis = RemoteServerStatus.getServerTimeDiffFromNowInMillis();
          if(diffMillis > 5000L)
          {
            System.out.println("  Servers time differs from the Client by " + diffMillis/1000 + " seconds");
            System.out.println("  Servers time: " +  RemoteServerStatus.getServerTime());
            System.out.println("  Client time:  " +  new TimeStamp().toString());
          }
        }
        else
          System.out.println("The status of the remote server was unavailable");
      }
      
      if (clientInterface != null)
      {
        OSM_Ports allPorts = clientInterface.getOsmPorts();
        if((allPorts != null) && (allPorts.PerfMgrPorts.length > 1))
        {
          TimeStamp ts = allPorts.PerfMgrPorts[2].getErrorTimeStamp();
          if(ts != null)
          {
            System.out.println("The most recent timestamp for the fabric data is: " + ts.toString());
            if((RemoteServerStatus != null) && (SysInfo != null))
            {
              long secsToNew = ts.getTimeInSeconds() + SysInfo.PM_SweepTime;
              long deltaS    = secsToNew - (RemoteServerStatus.TimeInMillis/1000);
              System.out.println(" (seconds to next update: " + deltaS + ")");
            }
          }
          else
            System.out.println("The timestamp for the fabric data was unavailable");
          
        }
        else
          System.out.println("The timestamp for the fabric data was unavailable, because the PerfMgr info is missing");
      }
    }
  }
}
