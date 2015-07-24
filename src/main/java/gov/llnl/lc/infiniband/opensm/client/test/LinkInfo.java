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
 *        file: LinkInfo.java
 *
 *  Created on: Jan 5, 2012
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.client.test;

import gov.llnl.lc.infiniband.core.IB_Guid;
import gov.llnl.lc.infiniband.core.IB_Link;
import gov.llnl.lc.infiniband.core.IB_LinkType;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_NodeType;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Nodes;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Ports;
import gov.llnl.lc.infiniband.opensm.plugin.data.PFM_Node;
import gov.llnl.lc.infiniband.opensm.plugin.data.PFM_Port;
import gov.llnl.lc.infiniband.opensm.plugin.data.SBN_Node;
import gov.llnl.lc.infiniband.opensm.plugin.data.SBN_Port;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmClientApi;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmServiceManager;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmSession;
import gov.llnl.lc.logging.CommonLogger;
import gov.llnl.lc.util.BinList;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**********************************************************************
 * Describe purpose and responsibility of LinkInfo
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Jan 5, 2012 7:55:09 AM
 **********************************************************************/
public class LinkInfo implements CommonLogger
{
  static final String NEW_LINE = System.getProperty("line.separator");
  
  /** the command line options, if any **/
  private static CommandLine cmd_line = null;
  private static String hostName = null;
  private static String portNum = null;

  private static boolean showAll = false;
  private static boolean doDevice = false;
  private static boolean doPorts = false;
  private static boolean doEsp0 = false;

  private static final String USAGE = "[-h=<host url>] [-p=<port num>] [-v] [-i | -s]";
  private static final String HEADER =
                          "LinkInfo - An OpenSM Service Client." + NEW_LINE
                          + "A tool for discovering information about links.";
  private static final String FOOTER =
                          "Copyright (C) 2012, Lawrence Livermore National Security, LLC";
  
  public static boolean parseCommandLineOptions(String[] args)
  {
    Options options = new Options();
    CommandLine line = null;
    
    Option help = new Option( "?", "help", false, "print this message" );
    Option version = new Option( "v", "version", false, "print the version information and exit" );
    Option l_all = new Option( "a", "all", false, "dump all link info" );
    Option n_device = new Option( "nd", "device", false, "show the node device ids" );
    Option l_ports = new Option( "ap", "ports", false, "show the subnet manager and perf manager ports" );
    Option n_esp0 = new Option( "ne", "esp0", false, "show the nodes with esp0" );
    
    Option host_name   = OptionBuilder.hasArg(true).withArgName( "host url" ).withValueSeparator('=').withDescription(  "the host name of the OSM Monitoring Service" ).withLongOpt("host").create( "h" );

    Option port_num   = OptionBuilder.hasArg(true).withArgName( "port #" ).withValueSeparator('=').withDescription(  "the port number of the service" ).withLongOpt("port").create( "p" );

    options.addOption( host_name );
    options.addOption( port_num );
    options.addOption( help );
    options.addOption( version );
    options.addOption( l_all );
    options.addOption( n_device );
    options.addOption( l_ports );
    options.addOption( n_esp0 );
    
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
    
    
    LinkInfo.cmd_line = line;
    
    if(line.hasOption("a"))
    {
      LinkInfo.showAll = true;
    }

    if(line.hasOption("nd"))
    {
      LinkInfo.doDevice = true;
    }

    if(line.hasOption("ap"))
    {
      LinkInfo.doPorts = true;
    }

    if(line.hasOption("ne"))
    {
      LinkInfo.doEsp0 = true;
    }

    if(line.hasOption("host"))
    {
      logger.info("A host name was supplied");
      hostName = line.getOptionValue("host");
    }

    if(line.hasOption("port"))
    {
      logger.info("A port number was supplied");
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

  private static boolean isSwitchGuid(OSM_Nodes allNodes, IB_Guid guid) 
  {
    if(allNodes == null || guid == null)
      return false;
    
    ArrayList<SBN_Node> sbna = new ArrayList<SBN_Node>(Arrays.asList(allNodes.getSubnNodes()));
    
    // iterate through the nodes until a guid match is found
    for(SBN_Node s: sbna)
    {
      // return as soon as a match is found
      if(s.getNodeGuid().equals(guid))
      {
        return OSM_NodeType.isSwitchNode(s);
      }
    }
    return false;
  }

  private static boolean isSwitchLink(OSM_Nodes allNodes, IB_Link link) 
  {
    // return true if both ends of this link are from a switch
    if(allNodes == null || link == null || link.Endpoint1 == null || link.Endpoint2 == null)
      return false;
    
    ArrayList<SBN_Node> sbna =  new ArrayList<SBN_Node>(Arrays.asList(allNodes.getSubnNodes()));
    
    // a node can be a switch, a CA, or other
    
    OSM_NodeType e1type = null;
    OSM_NodeType e2type = null;
   
    IB_Guid e1 = new IB_Guid(link.Endpoint1.sbnPort.node_guid);
    IB_Guid e2 = new IB_Guid(link.Endpoint2.sbnPort.node_guid);
    
    // iterate through the nodes until guid matches are found
    for(SBN_Node s: sbna)
    {
      // return as soon as a match is found
      if(s.getNodeGuid().equals(e1))
      {
        e1type = OSM_NodeType.get(s);
      }
      if(s.getNodeGuid().equals(e2))
      {
        e2type = OSM_NodeType.get(s);;
      }
      
      if((e1type != null) && (e2type != null))
        break;
    }
    // return true only if both are switches
    if(e1type.isSwitchType() && e2type.isSwitchType())
      return true;
    return false;
  }


  private static boolean isEdgeLink(OSM_Nodes allNodes, IB_Link link) 
  {
    // return true if one end is from a switch, and the other is a CA or Edge Node
    if(allNodes == null || link == null || link.Endpoint1 == null || link.Endpoint2 == null)
      return false;
    
    ArrayList<SBN_Node> sbna =  new ArrayList<SBN_Node>(Arrays.asList(allNodes.getSubnNodes()));
    
    // a node can be a switch, a CA, or other
    
    OSM_NodeType e1type = null;
    OSM_NodeType e2type = null;
   
    IB_Guid e1 = new IB_Guid(link.Endpoint1.sbnPort.node_guid);
    IB_Guid e2 = new IB_Guid(link.Endpoint2.sbnPort.node_guid);
    
    // iterate through the nodes until guid matches are found
    for(SBN_Node s: sbna)
    {
      // return as soon as a match is found
      if(s.getNodeGuid().equals(e1))
      {
        e1type = OSM_NodeType.get(s);
      }
      if(s.getNodeGuid().equals(e2))
      {
        e2type = OSM_NodeType.get(s);;
      }
      
      if((e1type != null) && (e2type != null))
        break;
    }
    // return true only if one is a switch, and the other is a CA
    if((e1type.isSwitchType() && e2type.isEdgeType()) || (e2type.isSwitchType() && e1type.isEdgeType()))
      return true;
    return false;
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
    ArrayList<PFM_Node> pmna = new ArrayList<PFM_Node>();
    ArrayList<SBN_Node> sbna = new ArrayList<SBN_Node>();
    ArrayList<PFM_Port> pmpa = new ArrayList<PFM_Port>();
    ArrayList<SBN_Port> sbpa = new ArrayList<SBN_Port>();

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
        ArrayList <IB_Link> ibls = new ArrayList<IB_Link>();
        ArrayList <IB_Link> iblc = new ArrayList<IB_Link>();
        ArrayList <IB_Link> iblQ = new ArrayList<IB_Link>();  // unknown
        
//        pmna = new ArrayList<PFM_Node>(Arrays.asList(AllNodes.getPerfMgrNodes()));
//        sbna = new ArrayList<SBN_Node>(Arrays.asList(AllNodes.getSubnNodes()));
//        pmpa = new ArrayList<PFM_Port>(Arrays.asList(AllPorts.getPerfMgrPorts()));
//        sbpa = new ArrayList<SBN_Port>(Arrays.asList(AllPorts.getSubnPorts()));
        
        if (showAll)
        {
          // create IB_Links
          ibla = AllPorts.createIB_Links(AllNodes);
          
        BinList <IB_Link> LinkBins = new BinList <IB_Link>();
        for(IB_Link link: ibla)
        {
          // create a list of switch and edge links
          if(link.getLinkType() == IB_LinkType.SW_LINK)
            ibls.add(link);
          else if(link.getLinkType() == IB_LinkType.CA_LINK)
            iblc.add(link);
          else
            iblQ.add(link);
          
          System.err.println(link.toString());
          if(link.hasTraffic())
            LinkBins.add(link, "Has Traffic");

          if(link.isActive())
            LinkBins.add(link, "Is Active");

          if(link.hasErrors())
            LinkBins.add(link, "Has Errors");
          
          LinkBins.add(link, "State: " + link.getState());

          LinkBins.add(link, "Speed: " + link.getSpeed().getSpeedName());

          LinkBins.add(link, "Width: " + link.getWidth().getWidthName());

          LinkBins.add(link, "Rate: " + link.getRate().getRateName());

        }
        System.err.println("Number Links - ibla: " + ibla.size());
        System.err.println(LinkBins.toString());
        System.err.println("Number of Switch Links: " + ibls.size());
        System.err.println("Number of Edge Links: " + iblc.size());
        System.err.println("Number of unknown Links: " + iblQ.size());
        }
        
        if (doDevice)
        {
          BinList <SBN_Node> UniqueNodes = new BinList <SBN_Node>();
          BinList <SBN_Node> SsysbL = new BinList <SBN_Node>();
          BinList <SBN_Node> sysbL = new BinList <SBN_Node>();
          // using the SBN_Nodes, bin the nodes according to their sysguid
          for(SBN_Node sn: sbna)
          {
            sysbL.add(sn, sn.getSysGuid().toString());
          }
          
          ArrayList<Long> sysSizes = sysbL.getBinSizes();
          
          // throw away all the ones with only a single system guid
          int ndex = 0;
          for(Long ss: sysSizes)
          {
            // if this bin is larger than one, add it to the new BinList
            if(ss.intValue() > 1)
              SsysbL.addBin(sysbL.getBin(ndex), sysbL.getBin(ndex).get(0).getSysGuid().toString());
            ndex++;
          }
         
          System.err.println("Nodes with sys_guid: " + SsysbL.toString());
          
          // build up bins of unqueness
          String key = null;
          for(SBN_Node sn: sbna)
          {
            key = Short.toString(sn.node_type) + "-" + Short.toString(sn.num_ports) + "-" +Integer.toString(sn.device_id) + Integer.toString(sn.partition_cap) + Integer.toString(sn.port_num_vendor_id) + Integer.toString(sn.revision) + Short.toString(sn.base_version) + Short.toString(sn.class_version);
            UniqueNodes.add(sn, key);
          }
          System.err.println("Node Types: " + UniqueNodes.toString());
         }
        
        if (doPorts)
        {
          // print out all the ports
          for(PFM_Port pp: pmpa)
          {
            System.err.println(pp);
          }
          
          for(SBN_Port sp: sbpa)
          {
            System.err.println(sp);
          }
          
          System.err.println("Number Ports - sp: " + sbpa.size() + ", pp: " + pmpa.size());
        }
        
        if (doEsp0)
        {
          BinList <PFM_Node> pbL = new BinList <PFM_Node>();
          for(PFM_Node pn: pmna)
          {
            pbL.add(pn, "(nodes with eps0=" + Boolean.toString(pn.esp0) + ")");
          }
          System.err.println(pbL.toString());
        }
      }
      
      /* all done, so close the session(s) */
      OsmService.closeSession(ParentSession);
    }
    else
      System.err.println("Parent session to host (" + hostName + ") on port (" + portNum + ") could not be established");
   }
}
