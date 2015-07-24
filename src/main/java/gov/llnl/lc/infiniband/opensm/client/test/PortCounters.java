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
 *        file: ClientInterfaceExample.java
 *
 *  Created on: Aug 24, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.client.test;

import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Nodes;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Ports;
import gov.llnl.lc.infiniband.opensm.plugin.data.PFM_Node;
import gov.llnl.lc.infiniband.opensm.plugin.data.PFM_NodePortStatus;
import gov.llnl.lc.infiniband.opensm.plugin.data.PFM_Port;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmClientApi;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmServiceManager;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmSession;
import gov.llnl.lc.logging.CommonLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**********************************************************************
 * Interrogate the PerfManager and show all the port counters
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Aug 24, 2011 1:32:21 PM
 **********************************************************************/
public class PortCounters implements CommonLogger
{
  static final String NEW_LINE = System.getProperty("line.separator");
  
  /** the command line options, if any **/
  private static CommandLine cmd_line = null;
  private static int periodInSecs = 20;
  private static int numInTop = 10;
  private static boolean doRate = false;
  private static boolean doCounters = false;
  private static boolean doErrors = false;
  private static boolean doPM = false;
  private static boolean doDuplicates = false;
  private static boolean doStrange = false;
  
  private static String hostName = null;
  private static String portNum = null;
  
  
  

  private static final String USAGE = "[-h=<host url>] [-p=<port num>] [-v] [-i | -s]";
  private static final String HEADER =
                          "PortCounters - An OpenSM PerfManager Client.";
  private static final String FOOTER =
                          "Copyright (C) 2011, Lawrence Livermore National Security, LLC";
  
  public static boolean parseCommandLineOptions(String[] args)
  {
    Options options = new Options();
    CommandLine line = null;
    
    Option help = new Option( "?", "help", false, "print this message" );
    Option version = new Option( "v", "version", false, "print the version information and exit" );
    Option p_counters = new Option( "pc", "counters", false, "show the port counters" );
    Option p_errors = new Option( "pe", "errors", false, "show the port errors" );
    Option p_manager = new Option( "pm", "manager", false, "perf manager status" );
    Option p_dups = new Option( "pd", "duplicates", false, "show duplicate ports" );
    Option p_rate = new Option( "pr", "rate", false, "show traffic rates" );
    Option p_strange = new Option( "ps", "strange", false, "show strange entries in the perf manager database" );
    
    Option host_name   = OptionBuilder.hasArg(true).withArgName( "host url" ).withValueSeparator('=').withDescription(  "the host name of the OSM Monitoring Service" ).withLongOpt("host").create( "h" );

    Option port_num   = OptionBuilder.hasArg(true).withArgName( "port #" ).withValueSeparator('=').withDescription(  "the port number of the service" ).withLongOpt("port").create( "p" );
    Option integration_time = OptionBuilder.hasArg(true).withArgName( "# secs" ).withValueSeparator('=').withDescription(  "seconds between samples for rate calculation" ).withLongOpt("time").create( "t" );

    options.addOption( host_name );
    options.addOption( port_num );
    options.addOption( help );
    options.addOption( version );
    options.addOption( p_counters );
    options.addOption( p_errors );
    options.addOption( p_manager );
    options.addOption( p_dups );
    options.addOption( p_rate );
    options.addOption( p_strange );
    options.addOption( integration_time );
    
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
    
    
    PortCounters.cmd_line = line;
    
    if(line.hasOption("pc"))
    {
      PortCounters.doCounters = true;
    }

    if(line.hasOption("pm"))
    {
      PortCounters.doPM = true;
    }

    if(line.hasOption("pd"))
    {
      PortCounters.doDuplicates = true;
    }

    if(line.hasOption("pe"))
    {
      PortCounters.doErrors = true;
    }

    if(line.hasOption("pr"))
    {
      PortCounters.doRate = true;
    }

    if(line.hasOption("ps"))
    {
      PortCounters.doStrange = true;
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
       if(line.hasOption("t"))
        {
          logger.info("A integration period was supplied");
          String rString = line.getOptionValue("t");
          int rVal = new Integer(rString);
          periodInSecs = (rVal > 4) && (rVal < 30) ? rVal: periodInSecs;
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


  public static ArrayList <PFM_Port> getPortNum(ArrayList <PFM_Port> ports, short num)
  {
    // return a list of Ports that all have the same port number
    java.util.ArrayList<PFM_Port> rtnPorts = new java.util.ArrayList<PFM_Port>();
    if((ports != null) && (num >= 0) && ports.size() > 0)
    {
      // copy it to the list
      for(PFM_Port pr: ports)
        if(pr.port_num == num)
          rtnPorts.add(pr);
    }
    return rtnPorts;
  }

  public static ArrayList <PFM_Port> getPortsWithTraffic(ArrayList <PFM_Port> ports)
  {
    // return a list of Ports that all have the same port number
    java.util.ArrayList<PFM_Port> rtnPorts = new java.util.ArrayList<PFM_Port>();
    if((ports != null) && ports.size() > 0)
    {
      // copy it to the list
      for(PFM_Port pr: ports)
        if(pr.hasTraffic())
          rtnPorts.add(pr);
    }
    return rtnPorts;
  }

  public static ArrayList <PFM_Node> getSwitchNodes(ArrayList <PFM_Node> nodes, boolean esp0)
  {
    // return a list of Nodes that have more than 1 port
    java.util.ArrayList<PFM_Node> rtnNodes = new java.util.ArrayList<PFM_Node>();
    if((nodes != null) && nodes.size() > 0)
    {
      // copy it to the list
      for(PFM_Node n: nodes)
      {
        if((n.num_ports > 2) && (n.isEsp0() == esp0))
          rtnNodes.add(n);
      }
    }
    return rtnNodes;
  }

  public static int numPorts(ArrayList <PFM_Node> nodes)
  {
    int total = 0;
    // add up all the ports
    if((nodes != null) && nodes.size() > 0)
    {
      // count
      for(PFM_Node n: nodes)
      {
        total += n.num_ports;
      }
    }
    return total;
  }

  public static int numSwitchPorts(ArrayList <PFM_Node> nodes, boolean esp0)
  {
    int total = 0;
    // add up all the ports greater than 2, conditionally include esp0 ports
    if((nodes != null) && nodes.size() > 0)
    {
      // count
      for(PFM_Node n: nodes)
      {
        if(n.num_ports > 2)
        {
          total += n.num_ports;
          /* adjust the count if we don't want to include esp0 port */
          if(n.isEsp0() && !esp0)
            total--;
        }
      }
    }
    return total;
  }

  public static int numCAPorts(ArrayList <PFM_Node> nodes)
  {
    return numPorts(nodes) - numSwitchPorts(nodes, true);
  }

  public static void showNodes(ArrayList <PFM_Node> nodes, boolean esp0)
  {
    int numNodes  = nodes.size();
    int numEsp0   = getSwitchNodes(nodes, true).size();
    int numSwitch = getSwitchNodes(nodes, false).size();
    int numCA     = numNodes - (numSwitch + numEsp0);
    
    if((nodes != null) && nodes.size() > 0)
    {
      // copy it to the list
      for(PFM_Node n: nodes)
        System.err.println(n);
    }
    
    System.err.println("The total # of Nodes is:    " + numNodes);
    System.err.println("The total # of Switches is: " + numSwitch);
    System.err.println("The total # of Esp0 is:     " + numEsp0);
    System.err.println("The total # of CA is:       " + numCA);
    System.err.println("The total # of Ports is:    " + numPorts(nodes));
    System.err.println("The total # of Switch Ports is: " + numSwitchPorts(nodes, true));
    System.err.println("The total # of Esp0 Ports is:   " + (numSwitchPorts(nodes, true) - numSwitchPorts(nodes, false)));
    System.err.println("The total # of CA Ports is:     " + numCAPorts(nodes));
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
    long MEG = 1048576;
    
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
      System.exit(0);
    }

    if (ParentSession != null)
    {
      OsmClientApi clientInterface = ParentSession.getClientApi();

      /* use the api's to get information */
      OSM_Ports OldPorts = clientInterface.getOsmPorts();
      if (doRate)
      {
        System.out.println("Top Port Counters:  (waiting for " + periodInSecs
            + " secs, to acquire 2nd data set)");
        System.out
            .println("===================================================================================");
        TimeUnit.SECONDS.sleep(periodInSecs); // wait for new data to become
                                              // available
        OSM_Nodes ONodes = clientInterface.getOsmNodes();
        OSM_Ports NewPorts = clientInterface.getOsmPorts();
        
        ArrayList<PFM_Node> pmna = new ArrayList<PFM_Node>(Arrays.asList(ONodes.getPerfMgrNodes()));

        // turn these into array lists, and then remove duplicates
        ArrayList<PFM_Port> opa = new ArrayList<PFM_Port>(Arrays.asList(OldPorts.getPerfMgrPorts()));
        ArrayList<PFM_Port> npa = new ArrayList<PFM_Port>(Arrays.asList(NewPorts.getPerfMgrPorts()));

        PFM_NodePortStatus pnps = new PFM_NodePortStatus(ONodes.getPerfMgrNodes(), OldPorts.getPerfMgrPorts(), true);
        System.err.println(" *** Here is the NodePortStatus *** " +  pnps.toString());
        
        System.out.println("Num Old: " + OldPorts.getPerfMgrPorts().length + ", Num New: "
            + NewPorts.getPerfMgrPorts().length);

        System.out.println("***Rates***");
        // iterate through the lists, find a match, create a portrate for each
        // one

        int numRates = 0;
        int numErrs = 0;
 
        System.out.println("");

        /*
         * take the latest "unique" list, and print out the ports with the most
         * errors
         */
        System.out.println("***Errors***");


        showNodes(pmna, true);
      }
      
      if(doCounters)
      {
        short pn = 1;
        // turn these into array lists, and then remove duplicates
        ArrayList<PFM_Port> opa = new ArrayList<PFM_Port>(Arrays.asList(OldPorts.getPerfMgrPorts()));

        
      }
      
      if(doErrors)
      {
        // turn these into array lists, and then remove duplicates
        ArrayList<PFM_Port> opa = new ArrayList<PFM_Port>(Arrays.asList(OldPorts.getPerfMgrPorts()));
        System.out.println("Num Orig Ports: " + OldPorts.getPerfMgrPorts().length );
       
      }
      
      if(doPM)
      {
        // create an array of unique sane ports
        ArrayList<PFM_Port> opa = new ArrayList<PFM_Port>(Arrays.asList(OldPorts.getPerfMgrPorts()));
       
      }
      
      if(doDuplicates)
      {
        // turn these into array lists, and then remove duplicates
        ArrayList<PFM_Port> opa = new ArrayList<PFM_Port>(Arrays.asList(OldPorts.getPerfMgrPorts()));

       
      }
      
      if(doStrange)
      {
        // turn these into array lists, and then remove duplicates
        ArrayList<PFM_Port> opa = new ArrayList<PFM_Port>(Arrays.asList(OldPorts.getPerfMgrPorts()));
       
      }
      
     
    }
    /* all done, so close the session(s) */
    OsmService.closeSession(ParentSession);
  }
}
