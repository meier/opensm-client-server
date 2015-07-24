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
 *        file: Configuration.java
 *
 *  Created on: Dec 16, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.client.test;

import gov.llnl.lc.infiniband.core.IB_Guid;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Configuration;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Fabric;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_NodeNameMap;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Nodes;
import gov.llnl.lc.infiniband.opensm.plugin.data.PFM_Node;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmClientApi;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmServiceManager;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmSession;

import java.util.HashMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**********************************************************************
 * Describe purpose and responsibility of Configuration
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Dec 16, 2011 1:24:25 PM
 **********************************************************************/
public class Configuration
{
  static final String NEW_LINE = System.getProperty("line.separator");
  
  /** the command line options, if any **/
  private static CommandLine cmd_line = null;
  private static String hostName = null;
  private static String portNum = null;
  
  // node-name-map and fabric config are in the Configuration object
  private static OSM_Configuration Config   = null;
  
  /** from the client interface **/
  private static OsmClientApi clientInterface = null;

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


  private static int getLidFromGuid(IB_Guid guid) throws Exception 
  {
    return clientInterface.getOsmPorts().getLidFromPortGuid(guid);
  }

  private static String getNameFromGuid(IB_Guid guid) throws Exception 
  {
    return clientInterface.getOsmNodes().getNameFromGuid(guid);
  }

  private static IB_Guid getGuidFromLid(int lid) throws Exception 
  {
    return clientInterface.getOsmPorts().getPortGuidFromLid(lid);
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
      clientInterface = ParentSession.getClientApi();
      
      if (clientInterface != null)
      {
        Config  = clientInterface.getOsmConfig();
        if(Config != null)
        {
          if(Config.getFabricConfig() != null)
          {
            System.out.println(Config.getFabricConfig().toString());
 //           System.out.println(Config.getFabricConfig().toXMLString(2));
            
            OSM_Fabric fab = clientInterface.getOsmFabric();
            fab.checkFabricStructure(Config.getFabricConfig(), true);
            
          }
          if (Config.getNodeNameMap() != null)
          {
//            compareGuids(Config.getNodeNameMap());
 //           compareNames(Config.getNodeNameMap());
//            compareNodeNameMap(Config.getNodeNameMap());
           }
        }
       }

     }
  }


  private static void compareNodeNameMap(OSM_NodeNameMap nodeNameMap) throws Exception
  {
    System.out.println(nodeNameMap.toString());
    
    // the node name map, should match the names and guids in the fabric, so compare
    //
    // iterate through the map, and compare the names obtained two different ways
    HashMap <String, String> NodeNameMap = nodeNameMap.getNodeNameMap();
    for(String guid : NodeNameMap.keySet())
    {
      IB_Guid nodeGuid = new IB_Guid(guid);
      String name1      = getNameFromGuid(nodeGuid);
      String name2      = NodeNameMap.get(guid);
      System.out.println(nodeGuid.toColonString() + ", lid: " + getLidFromGuid(nodeGuid));
      if(name1 != null && name1.equalsIgnoreCase(name2))
        System.out.println("Match: " + name1);
      else
        System.err.println("No Match: " + name1 + " vs " + name2);
    }
    
    
  }

  private static void compareGuids(OSM_NodeNameMap nodeNameMap) throws Exception
  {
    System.out.println("Guids from Nodes vs NodeNameMap");
    OSM_Nodes nodes = clientInterface.getOsmNodes();
    
    // the node name map, should match the names and guids in the fabric, so compare
    //
    // iterate through the map, and compare the names obtained two different ways
    System.out.println("NodeNameMap list");
    HashMap <String, String> NodeNameMap = nodeNameMap.getNodeNameMap();
    for(String guid : NodeNameMap.keySet())
    {
      IB_Guid nodeGuid = new IB_Guid(guid);
      System.out.println(nodeGuid.toColonString());
    }
    System.out.println("Node list");
    for(PFM_Node n : nodes.PerfMgrNodes)
    {
      IB_Guid nodeGuid = new IB_Guid(n.node_guid);
      System.out.println(nodeGuid.toColonString());
    }
  }

  private static void compareNames(OSM_NodeNameMap nodeNameMap) throws Exception
  {
    System.out.println("Names from Nodes vs NodeNameMap");
    OSM_Nodes nodes = clientInterface.getOsmNodes();
    
    // the node name map, should match the names and guids in the fabric, so compare
    //
    // iterate through the map, and compare the names obtained two different ways
    System.out.println("NodeNameMap list");
    HashMap <String, String> NodeNameMap = nodeNameMap.getNodeNameMap();
    for(String guid : NodeNameMap.keySet())
    {
      System.out.println( NodeNameMap.get(guid));
    }
    System.out.println("Node list");
    for(PFM_Node n : nodes.PerfMgrNodes)
    {
      System.out.println(n.getNode_name());
    }
  }

}
