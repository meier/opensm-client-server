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
 *        file: DumpPorts
 *
 *  Created on: March 5, 2012
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.client.test;

import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Ports;
import gov.llnl.lc.infiniband.opensm.plugin.data.PFM_Port;
import gov.llnl.lc.infiniband.opensm.plugin.data.PFM_PortChange;
import gov.llnl.lc.infiniband.opensm.plugin.data.PFM_PortTransferRate;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmClientApi;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmServiceManager;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmSession;
import gov.llnl.lc.logging.CommonLogger;

import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class DumpPortRates implements CommonLogger
{
  static final String NEW_LINE = System.getProperty("line.separator");
  
  /** the command line options, if any **/
  private static CommandLine cmd_line = null;
  private static String hostName = null;
  private static String portNum = null;
  
  private static int samplePeriod = 300;

  private static final String USAGE = "[-h=<host url>] [-p=<port num>] [-v] [-i | -s]";
  private static final String HEADER =
                          "DumpPortRates - An OpenSM Service Client." + NEW_LINE + "A diagnostic for showing traffic rates.";
  private static final String FOOTER =
                          "Copyright (C) 2012, Lawrence Livermore National Security, LLC";
  
  public static boolean parseCommandLineOptions(String[] args)
  {
    Options options = new Options();
    CommandLine line = null;
    
    Option help = new Option( "?", "help", false, "print this message" );
    Option version = new Option( "v", "version", false, "print the version information and exit" );
    
    Option host_name   = OptionBuilder.hasArg(true).withArgName( "host url" ).withValueSeparator('=').withDescription(  "the host name of the OSM Monitoring Service" ).withLongOpt("host").create( "h" );

    Option port_num   = OptionBuilder.hasArg(true).withArgName( "port #" ).withValueSeparator('=').withDescription(  "the port number of the service" ).withLongOpt("port").create( "p" );

    Option period = OptionBuilder.hasArg(true).withArgName( "# secs" ).withValueSeparator('=').withDescription(  "the sample period in seconds" ).withLongOpt("period").create( "t" );

    options.addOption( host_name );
    options.addOption( port_num );
    options.addOption( period );
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
    DumpPortRates.cmd_line = line;
    
    if(line.hasOption("period"))
    {
      logger.info("A sample period was supplied");
      String rString = line.getOptionValue("period");
      int rVal = new Integer(rString);
      samplePeriod = (rVal > 4) && (rVal < 300) ? rVal: samplePeriod;
    }

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
   *  
   * Simple example of using the client api to dump node info.
   *
   * @see     OsmServiceManager
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
      logger.severe(e.getStackTrace().toString());
      System.exit(-1);
    }

    if (ParentSession != null)
    {
      OsmClientApi clientInterface = ParentSession.getClientApi();

      /* use the api's to get the port information */
      OSM_Ports AllPorts2 = clientInterface.getOsmPorts();
      System.out.println("Acquired the first set of counters, now waiting for " + samplePeriod + " seconds before getting the second set.");            
      TimeUnit.SECONDS.sleep(samplePeriod);  // wait for new data to become available
      OSM_Ports AllPorts1 = clientInterface.getOsmPorts();

      System.out.println("Acquired, calculating....");            
      /* all done, so close the session(s) */
      OsmService.closeSession(ParentSession);
      if ((AllPorts1 != null) && (AllPorts2 !=null))
      {
        // I only care about the PFM_Ports
        PFM_Port[] p1a = AllPorts1.getPerfMgrPorts();
        PFM_Port[] p2a = AllPorts2.getPerfMgrPorts();
        
        // sanity check
        System.out.println("# PerfMgr ports: " + p1a.length);
        System.out.println("# PerfMgr ports: " + p2a.length);
        
        //
        
        for (int j = 0; j < 100; j++)
        {
          System.out.println(PFM_PortTransferRate.toVerboseDiagnosticString(new PFM_PortChange(p1a[j], p2a[j])));
        }
      }
      else
        logger.severe("getOsmPorts() returned null");

    }
  }
}
