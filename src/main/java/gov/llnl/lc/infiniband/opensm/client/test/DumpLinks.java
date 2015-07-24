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
 *        file: DumpLinks.java
 *
 *  Created on: Jul 24, 2012
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.client.test;

import gov.llnl.lc.infiniband.core.IB_Link;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_LinkState;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Nodes;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Port;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Ports;
import gov.llnl.lc.infiniband.opensm.plugin.data.PFM_Port;
import gov.llnl.lc.infiniband.opensm.plugin.data.SBN_Port;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmClientApi;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmServerStatus;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmServiceManager;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmSession;
import gov.llnl.lc.logging.CommonLogger;
import gov.llnl.lc.net.ObjectSession;

import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


/**********************************************************************
 * Describe purpose and responsibility of DumpLinks
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Jul 24, 2012 4:43:34 PM
 **********************************************************************/
public class DumpLinks implements CommonLogger
{
  static final String NEW_LINE = System.getProperty("line.separator");
  
  /** the command line options, if any **/
  private static CommandLine cmd_line = null;
  private static String hostName = null;
  private static String portNum = null;

  /** from the admin interface **/
  private static ObjectSession ParentSessionStatus  = null;
  private static OsmServerStatus RemoteServerStatus = null;

  private static final String USAGE = "[-h=<host url>] [-p=<port num>] [-v] [-i | -s]";
  private static final String HEADER =
                          "DumpLinks - An OpenSM Service Client." + NEW_LINE + "A diagnostic for printing links.";
  private static final String FOOTER =
                          "Copyright (C) 2012, Lawrence Livermore National Security, LLC";
  
  public static boolean parseCommandLineOptions(String[] args)
  {
    Options options = new Options();
    CommandLine line = null;
    
    Option help = new Option( "?", "help", false, "print this message");
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
    
    
    DumpLinks.cmd_line = line;
    

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

    OSM_Nodes AllNodes  = null;
    OSM_Ports AllPorts  = null;

    /* the one and only OsmServiceManager */
    OsmServiceManager OsmService = OsmServiceManager.getInstance();

    try
    {
      ParentSession = OsmService.openSession(hostName, portNum, null, null);
    }
    catch (Exception e)
    {
      logger.severe(e.getStackTrace().toString());
      System.exit(0);
    }

    if (ParentSession != null)
    {
      OsmClientApi clientInterface = ParentSession.getClientApi();

      /* use the api's to get information */
      AllNodes = clientInterface.getOsmNodes();
      AllPorts = clientInterface.getOsmPorts();
      if((AllNodes != null) && (AllPorts != null))
      {
        ArrayList <IB_Link> ibla = null;
        if (AllPorts != null)
        {
          ibla = AllPorts.createIB_Links(AllNodes);

          for (IB_Link link: ibla)
          {
            OSM_Port o1 = link.getEndpoint1();
            PFM_Port p1 = o1.getPfmPort();
            SBN_Port s1 = o1.getSbnPort();

            OSM_Port o2 = link.getEndpoint2();
            PFM_Port p2 = o2.getPfmPort();
            SBN_Port s2 = o2.getSbnPort();
            
            if(link.getState() == OSM_LinkState.ACTIVE)            
              System.out.println(s1.node_guid + ",port" + s1.port_num + " " + s2.node_guid + ",port" + s2.port_num);
            else
              System.out.println(s1.node_guid + ",port" + s1.port_num + " " + s2.node_guid + ",port" + s2.port_num + ", e1: " + o1.getStateString() + ", e2: " + o2.getStateString());
          }
          System.out.println("Creating the port list array");
          ArrayList<OSM_Port> pL = AllPorts.createOSM_Ports(AllNodes);
          System.out.println("Creating the suspected ib links");
          IB_Link.createSuspectedIB_Links(pL);
        }
      }
    }
  }

}
