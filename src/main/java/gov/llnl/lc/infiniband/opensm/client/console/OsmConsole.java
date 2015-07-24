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
 *        file: OsmConsole.java
 *
 *  Created on: Nov 2, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.client.console;

import gov.llnl.lc.infiniband.core.IB_Guid;
import gov.llnl.lc.infiniband.core.IB_Link;
import gov.llnl.lc.infiniband.core.IB_LinkType;
import gov.llnl.lc.infiniband.core.IB_Port;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_LinkRate;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_LinkSpeed;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_LinkState;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_LinkWidth;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_NodeType;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Nodes;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Ports;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Stats;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Subnet;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_SysInfo;
import gov.llnl.lc.infiniband.opensm.plugin.data.PFM_Node;
import gov.llnl.lc.infiniband.opensm.plugin.data.PFM_NodePortStatus;
import gov.llnl.lc.infiniband.opensm.plugin.data.PFM_Port;
import gov.llnl.lc.infiniband.opensm.plugin.data.PFM_Port.PortCounterName;
import gov.llnl.lc.infiniband.opensm.plugin.data.SBN_Manager;
import gov.llnl.lc.infiniband.opensm.plugin.data.SBN_Node;
import gov.llnl.lc.infiniband.opensm.plugin.data.SBN_NodePortStatus;
import gov.llnl.lc.infiniband.opensm.plugin.data.SBN_Options;
import gov.llnl.lc.infiniband.opensm.plugin.event.OSM_EventStats;
import gov.llnl.lc.infiniband.opensm.plugin.event.OsmEvent;
import gov.llnl.lc.infiniband.opensm.plugin.event.OsmEventListener;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmAdminApi;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmClientApi;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmEventApi;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmServerStatus;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmServiceManager;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmSession;
import gov.llnl.lc.logging.CommonLogger;
import gov.llnl.lc.net.ObjectSession;
import gov.llnl.lc.time.TimeListener;
import gov.llnl.lc.time.TimeService;
import gov.llnl.lc.time.TimeStamp;
import gov.llnl.lc.util.BinList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import jcurses.system.CharColor;
import jcurses.system.InputChar;
import jcurses.system.Toolkit;
import jcurses.util.Rectangle;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**********************************************************************
 * Describe purpose and responsibility of OsmConsole
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Nov 2, 2011 3:55:11 PM
 **********************************************************************/
public class OsmConsole implements Runnable, CommonLogger, TimeListener, OsmEventListener
{
  /** the one and only <code>OsmConsole</code> Singleton **/
  private volatile static OsmConsole Osm_Console  = null;

  /** the synchronization object **/
  private static Boolean semaphore            = new Boolean( true );

  /** the command line options, if any **/
  private static CommandLine cmd_line = null;
  private static Boolean withConnection = true;
  
  private volatile static TimeService Tserv = TimeService.getInstance();
  private volatile static long timeLoopCounter = 0;
  private static long refreshPeriod   = 30;
  
  private volatile static long eventCounter = 0;

  private volatile static OsmServiceManager OsmService = OsmServiceManager.getInstance();
  private static OsmSession ParentSession     = null;
  private static OsmClientApi clientInterface = null;
  private static OsmAdminApi adminInterface   = null;
  private static OsmEventApi eventInterface   = null;
  
  /** from the client interface **/
  private volatile static OSM_Nodes AllNodes  = null;
  private volatile static OSM_SysInfo SysInfo = null;
  private volatile static OSM_Ports AllPorts  = null;
  private volatile static OSM_Stats Stats     = null;
  private volatile static OSM_Subnet Subnet   = null;
  
  /** from the admin interface **/
  private volatile static ObjectSession ParentSessionStatus  = null;
  private volatile static OsmServerStatus RemoteServerStatus = null;
  
  /** from the event interface **/
  private volatile static OSM_EventStats EventStats = null;
  
  /** thread responsible for updating the screens **/
  private static java.lang.Thread Update_Thread;
  
  /** boolean specifying whether the thread has been created **/
  protected static boolean Thread_Exists = false;

  /** boolean specifying whether the thread is running **/
  protected static boolean Thread_Running = false;

  /** boolean specifying whether the thread should continue **/
  private volatile static boolean Continue_Thread = true;
  
  private volatile static int ScreenNum = 1;
  private static final int ScreenRows = 50;
  private static final int ScreenCols = 100;
  
  private int[] NodeColumns = null;

  private static CharColor Scrn0BTxtColor =  null;
  private static CharColor Scrn0FTxtColor =  null;
  private static CharColor ScrnTitleColor =  null;
  private static CharColor BkgndTxtColor  =  null;
  private static CharColor FrgndTxtColor  =  null;
  private static CharColor ErrorTxtColor  =  null;
  
  
  private static TimeStamp CurrentTime = null;

  private static final String USAGE = "[-h=<host url>] [-p=<port num>] [-v] [-i | -s]";
  private static final String HEADER =
                          "OsmConsole - An OpenSM Monitoring Service Client.";
  private static final String FOOTER =
                          "Copyright (C) 2011, Lawrence Livermore National Security, LLC";
  
  /** begin of PFM_Refresh **************************************************************/ 
  private static boolean PFM_Refresh = false;
  private static ArrayList<PFM_NodePortStatus> nps_array  = new ArrayList<PFM_NodePortStatus>();
  private static ArrayList<PFM_Node> pmna = new ArrayList<PFM_Node>();
  private static ArrayList<PFM_Port> pmpa = new ArrayList<PFM_Port>();
  
  /** all the node ports **/
  private static PFM_NodePortStatus pnpsa = null;
  
  /** only the nodes with more than 2 ports, and without esp0 */
  private static PFM_NodePortStatus pnpss = null;
  
  /** only the nodes with more than 2 ports, and with esp0 */      
  private static PFM_NodePortStatus pnpse = null;
  
  /** everything that doesn't look like a switch */
  private static PFM_NodePortStatus pnpsc = null;
/** end of PFM_Refresh ****************************************************************/ 

  /** begin of Link_Refresh **************************************************************/ 
  private static boolean Link_Refresh = false;

  // "ALL" IB_Links
  private static ArrayList <IB_Link> ibla = null;
  
  private static int[] Lnum_nodes  = new int[4];
  private static int[] Lnum_ports  = new int[4];
  
  // Separate the links into the different types
  private static ArrayList <IB_Link> ibls = new ArrayList<IB_Link>();
  private static ArrayList <IB_Link> iblc = new ArrayList<IB_Link>();
  private static ArrayList <IB_Link> iblQ = new ArrayList<IB_Link>();  // unknown
 
  // put the various attribute counts in bins
  private static BinList <IB_Link> aLinkBins = new BinList <IB_Link>();
  private static BinList <IB_Link> sLinkBins = new BinList <IB_Link>();
  private static BinList <IB_Link> cLinkBins = new BinList <IB_Link>();
  private static BinList <IB_Link> QLinkBins = new BinList <IB_Link>(); // unknown
  
  
  /** end of Link_Refresh ****************************************************************/ 

  private OsmConsole()
  {
    if(!Thread_Exists)
    {
    // set up the thread to listen
    Update_Thread = new Thread(this);
    Update_Thread.setDaemon(false);
    Update_Thread.setName("OsmConsole");
    Thread_Exists = true;
    }
  }
  
  int printTitleString(String title, int row)
  {
    // Centered with title color
    int col = (ScreenCols - title.length())/2;
    Toolkit.printString(title, col, row, ScrnTitleColor);
    return col + title.length();
  }
  
  int printTitleString(String title, int col, int row)
  {
    // Centered with title color
    Toolkit.printString(title, col, row, ScrnTitleColor);
    return col + title.length();
  }
  
  void printString(String text, int x, int y, CharColor color)
  {
    // forground printing that automatically pads at the end
    // only usefull for test that CHANGES LENGTH
    Toolkit.printString(text + "     ", x, y, color);
  }
  
  public synchronized boolean create()
  {
    boolean success = false;
    
    logger.info("Initializing the OsmConsole");
    
    if (Thread_Exists && !Thread_Running)
    {
    /* do whatever it takes to initialize the console */
    logger.info("Starting the " + Update_Thread.getName() + " Thread");
    
    /* this runs the interactive screens program */
    Update_Thread.start();
    success = true;
    }
    
    return success;
  }
  /*-----------------------------------------------------------------------*/
  public void destroy(String msg)
  {
    logger.info("Terminating the OsmConsole");
    logger.info(msg);
    
    /* this should break the interactive screen program out of its infinite loop, and end the thread */
    Continue_Thread = false;
    
    Tserv.removeListener(this);
    Toolkit.clearScreen(Scrn0BTxtColor);
    logger.severe("Ending now");
    Toolkit.shutdown();
    System.err.println(msg);
  }

  public OsmSession openParentSession(CommandLine line) throws Exception
  {
    logger.info("opening parent session");
    String hostName = null;
    String portNum = null;
    
    // the command line may contain an alternate hostname and port number, so
    // use them if supplied, otherwise get them from the properties file
    
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
    
    synchronized( OsmConsole.semaphore )
    {
      if ( ParentSession == null )
      {
        ParentSession = OsmService.openSession(hostName, portNum, null, null);
      }
      return ParentSession;
    }
  }
  
  public boolean paintForeground_0(int screenNum) throws Exception
  {
    // data from the service for this screen
    if(SysInfo != null)
    {
      Toolkit.printString(SysInfo.OpenSM_Version, 1, 1, Scrn0FTxtColor);
      Toolkit.printString(SysInfo.OsmJpi_Version, 1, 2, Scrn0FTxtColor);
    }
    if(CurrentTime != null)
      Toolkit.printString(CurrentTime.toString(), ScreenCols -21, 1, ScrnTitleColor);
    if(RemoteServerStatus != null)
    {
      printTitleString(RemoteServerStatus.Server.getHost(), 0);
      Toolkit.printString(RemoteServerStatus.Server.getStartTime().toString(), ScreenCols -21, 2, Scrn0FTxtColor);
    }
      return true;
  }
  
  public boolean paintBackground_0(int screenNum) throws Exception
  {
    int row = 1;
    int column = 1;
    
    // static graphics and text
    Rectangle rect1 = new Rectangle(0,0,ScreenCols,ScreenRows);
    Toolkit.drawBorder(rect1, new CharColor(CharColor.WHITE, CharColor.RED));  // outline
    Toolkit.drawHorizontalLine(1, 3, ScreenCols -2, ErrorTxtColor);
    Toolkit.drawHorizontalLine(1, ScreenRows-3, ScreenCols -2, ErrorTxtColor);
    // top area
    Toolkit.printString("up since:", ScreenCols -31, 2, Scrn0BTxtColor);
    Toolkit.printString("current:", ScreenCols -30, 1, Scrn0BTxtColor);

    // bottom area
    row=ScreenRows-2;
    column=1;
    
    int keySpace   = 28;
    int labelSpace = 48;
    int numKeys    = 9;
    int colPad = (ScreenCols - (keySpace+labelSpace))/(numKeys-1);
    if(colPad < 1)
      colPad = 1;
    
    Toolkit.printString("Esc-", column, row, Scrn0BTxtColor);
    Toolkit.printString("quit", column+=4, row, ErrorTxtColor);
    
    Toolkit.printString("F2-", column+=5, row, Scrn0BTxtColor);
    column = printTitleString("Status", column+=3, row);
    
    Toolkit.printString("F3-", column+=colPad, row, Scrn0BTxtColor);
    column = printTitleString("Nodes", column+=3, row);
    
    Toolkit.printString("F4-", column+=colPad, row, Scrn0BTxtColor);
    column = printTitleString("Ports", column+=3, row);
    
    Toolkit.printString("F5-", column+=colPad, row, Scrn0BTxtColor);
    column = printTitleString("PerfMgr", column+=3, row);
    
    Toolkit.printString("F6-", column+=colPad, row, Scrn0BTxtColor);
    column = printTitleString("Links", column+=3, row);
    
    Toolkit.printString("F7-", column+=colPad, row, Scrn0BTxtColor);
    column = printTitleString("Events", column+=3, row);
    
    Toolkit.printString("F8-", column+=colPad, row, Scrn0BTxtColor);
    column = printTitleString("Config", column+=3, row);
    
    Toolkit.printString("F9-", column+=colPad, row, Scrn0BTxtColor);
    column = printTitleString("Srvc", column+=3, row);
    return true;
  }

  public boolean paintForeground_1(int screenNum) throws Exception
  {
    // data from the service (may need to pad these)
    int column = 40;
    int row    = 6;
    
    /* keep in order, top down.  See row value increment */

    if(SysInfo != null)
    {
      Toolkit.printString(SysInfo.SM_State,       column, row++, FrgndTxtColor);
      Toolkit.printString(Integer.toString(SysInfo.SM_Priority),    column, row++, FrgndTxtColor);
      Toolkit.printString(SysInfo.SA_State,       column, row++, FrgndTxtColor);
      Toolkit.printString(SysInfo.RoutingEngine,  column, row++, FrgndTxtColor);
      Toolkit.printString("unknown",       column, row++, FrgndTxtColor);
      Toolkit.printString(Arrays.toString(SysInfo.EventPlugins), column, row++, FrgndTxtColor);
      row++;
      Toolkit.printString(SysInfo.PM_State + "/" + SysInfo.PM_SweepState,       column, row++, FrgndTxtColor);
      Toolkit.printString(Integer.toString(SysInfo.PM_SweepTime),       column, row++, FrgndTxtColor);
    }
    else
    {
      logger.warning("The SysInfo seems to be null");      
    }
    if(Stats != null)
    {
      row+=2;
      Toolkit.printString(Long.toString(Stats.qp0_mads_outstanding),       column, row++, FrgndTxtColor);
      Toolkit.printString(Long.toString(Stats.qp0_mads_outstanding_on_wire),       column, row++, FrgndTxtColor);
      Toolkit.printString(Long.toString(Stats.qp0_mads_rcvd),       column, row++, FrgndTxtColor);
      Toolkit.printString(Long.toString(Stats.qp0_mads_sent),       column, row++, FrgndTxtColor);
      Toolkit.printString(Long.toString(Stats.qp0_unicasts_sent),       column, row++, FrgndTxtColor);
      Toolkit.printString(Long.toString(Stats.qp0_mads_rcvd_unknown),       column, row++, FrgndTxtColor);
      Toolkit.printString(Long.toString(Stats.sa_mads_outstanding),       column, row++, FrgndTxtColor);
      Toolkit.printString(Long.toString(Stats.sa_mads_rcvd),       column, row++, FrgndTxtColor);
      Toolkit.printString(Long.toString(Stats.sa_mads_sent),       column, row++, FrgndTxtColor);
      Toolkit.printString(Long.toString(Stats.sa_mads_rcvd_unknown),       column, row++, FrgndTxtColor);
      Toolkit.printString(Long.toString(Stats.sa_mads_ignored),       column, row++, FrgndTxtColor);
    }
    else
    {
      logger.warning("The Stats seems to be null");      
    }
    if(Subnet != null)
    {
      row+=2;
      Toolkit.printString(Boolean.toString(Subnet.sweeping_enabled),       column, row++, FrgndTxtColor);
      Toolkit.printString(Integer.toString(Subnet.Options.sweep_interval),       column, row++, FrgndTxtColor);
      Toolkit.printString(Boolean.toString(Subnet.ignore_existing_lfts),       column, row++, FrgndTxtColor);
      Toolkit.printString(Boolean.toString(Subnet.subnet_initialization_error),       column, row++, FrgndTxtColor);
      Toolkit.printString(Boolean.toString(Subnet.in_sweep_hop_0),       column, row++, FrgndTxtColor);
      Toolkit.printString(Boolean.toString(Subnet.first_time_master_sweep),       column, row++, FrgndTxtColor);
      Toolkit.printString(Boolean.toString(Subnet.set_client_rereg_on_sweep),       column, row++, FrgndTxtColor);
      Toolkit.printString(Boolean.toString(Subnet.coming_out_of_standby),       column, row++, FrgndTxtColor);
      
      /* iterate through the managers, hopefully 2 or less */
      column = 1;
      row+=4;
      for(SBN_Manager m: Subnet.Managers)
      {
        Toolkit.printString(new IB_Guid(m.guid).toColonString(), column, row, FrgndTxtColor);
        Toolkit.printString(m.State, column+25, row, FrgndTxtColor);
        Toolkit.printString(Short.toString(m.pri_state), column+40, row++, FrgndTxtColor);
      }
    }
    else
    {
      logger.warning("The Subnet seems to be null");      
    }
    return true;
  }
  
  public boolean paintBackground_1(int screenNum) throws Exception
  {
    // static graphics and text
    int column = 1;
    int row    = 4;
    
    printTitleString("Subnet Status", row++);

    row++;    
    Toolkit.printString("SM State:", column, row++, BkgndTxtColor);
    Toolkit.printString("SM Priority:", column, row++, BkgndTxtColor);
    Toolkit.printString("SA State:", column, row++, BkgndTxtColor);
    Toolkit.printString("Routing Engine:", column, row++, BkgndTxtColor);
    Toolkit.printString("AR Routing:", column, row++, BkgndTxtColor);
    Toolkit.printString("Loaded event plugins:", column, row++, BkgndTxtColor);
    row++;
    Toolkit.printString("PerfMgr state/sweep state:", column, row++, BkgndTxtColor);
    Toolkit.printString("PerfMgr sweep time (seconds):", column, row++, BkgndTxtColor);
    row++;
    Toolkit.drawHorizontalLine(column, row, 30, ErrorTxtColor);
    Toolkit.printString("MAD stats", column+2, row++, BkgndTxtColor);
    Toolkit.printString("QP0 MADs outstanding", column, row++, BkgndTxtColor);
    Toolkit.printString("QP0 MADs outstanding (on wire)", column, row++, BkgndTxtColor);
    Toolkit.printString("QP0 MADs rcvd", column, row++, BkgndTxtColor);
    Toolkit.printString("QP0 MADs sent", column, row++, BkgndTxtColor);
    Toolkit.printString("QP0 unicasts sent", column, row++, BkgndTxtColor);
    Toolkit.printString("QP0 unknown MADs rcvd", column, row++, BkgndTxtColor);
    Toolkit.printString("SA MADs outstanding", column, row++, BkgndTxtColor);
    Toolkit.printString("SA MADs rcvd", column, row++, BkgndTxtColor);
    Toolkit.printString("SA MADs sent", column, row++, BkgndTxtColor);
    Toolkit.printString("SA unknown MADs rcvd", column, row++, BkgndTxtColor);
    Toolkit.printString("SA MADs ignored", column, row++, BkgndTxtColor);
    row++;
    Toolkit.drawHorizontalLine(column, row, 30, ErrorTxtColor);
    Toolkit.printString("Subnet flags", column+2, row++, BkgndTxtColor);
    Toolkit.printString("Sweeping enabled", column, row++, BkgndTxtColor);
    Toolkit.printString("Sweep interval (seconds)", column, row++, BkgndTxtColor);
    Toolkit.printString("Ignore existing lfts", column, row++, BkgndTxtColor);
    Toolkit.printString("Subnet Init errors", column, row++, BkgndTxtColor);
    Toolkit.printString("In sweep hop 0", column, row++, BkgndTxtColor);
    Toolkit.printString("First time master sweep", column, row++, BkgndTxtColor);
    Toolkit.printString("Set client rereg on sweep", column, row++, BkgndTxtColor);
    Toolkit.printString("Coming out of standby", column, row++, BkgndTxtColor);
    row++;
    Toolkit.drawHorizontalLine(column, row, 30, ErrorTxtColor);
    Toolkit.printString("Known SMs", column+2, row++, BkgndTxtColor);
    Toolkit.printString("Port GUID", column, row, BkgndTxtColor);
    Toolkit.printString("SM State", column+25, row, BkgndTxtColor);
    Toolkit.printString("Priority", column+40, row++, BkgndTxtColor);
    Toolkit.drawHorizontalLine(column, row, column+20, ErrorTxtColor);
    Toolkit.drawHorizontalLine(column+25, row, column+34, ErrorTxtColor);
    Toolkit.drawHorizontalLine(column+40, row++, column+49, ErrorTxtColor);

    return true;
  }

  public boolean paintForeground_2(int screenNum) throws Exception
  {
    ArrayList <Integer> c_array  = new ArrayList<Integer>();
    CharColor val_color = null;
    long PFrefreshPeriod = refreshPeriod;
    
    int row    = 7;
    
    // attribute column is 20 wide, last 4 get remainder
    int attribute_width = 20;
    int col_offset = (ScreenCols -attribute_width)/4;
    int cl= 0, rw=0;
 
    if((AllNodes != null) && (AllPorts != null))
    {
      /** periodically recalculate these, time intensive */
      if(!PFM_Refresh || (timeLoopCounter%PFrefreshPeriod == 0))
      {
        /* init flag - set true after first time throught, use time period after that */
        PFM_Refresh = true;

        nps_array  = new ArrayList<PFM_NodePortStatus>();
        PFM_Node[] pna = AllNodes.getPerfMgrNodes();
        PFM_Port[] ppa = AllPorts.getPerfMgrPorts();
        // the perfmgr may not have returned data due to start-up delay, check
        if((pna != null) && (ppa != null))
        {
        pmna = new ArrayList<PFM_Node>(Arrays.asList(pna));
        pmpa = new ArrayList<PFM_Port>(Arrays.asList(ppa));
        
        logger.warning("Calculating NodePortStatus");
        pnpsa = new PFM_NodePortStatus(pmna, pmpa, true);
        
        /* only the nodes with more than 2 ports, and without esp0 */
        pnpss = PFM_NodePortStatus.getSwitchNPS(pmna, pmpa, false);
        
        /* only the nodes with more than 2 ports, and with esp0 */      
        pnpse = PFM_NodePortStatus.getSwitchNPS(pmna, pmpa, true);
        
        /* everything that doesn't look like a switch */
        pnpsc = PFM_NodePortStatus.getChannelAdapterNPS(pmna, pmpa);
        
        }
        else
          logger.warning("The data from the PerfManager is not available... yet");

      }
      
      // the four arrays need to exist, or go no further
      if(nps_array == null || pmna == null || pmpa == null || pnpsa == null)
      {
        logger.severe("Painting Forground 2,have bad arrays, sup?");      
        return false;
      }
      
      c_array.add(0, col_offset + 7);
      nps_array.add(0, pnpss);  // just the switches (without eps0)
      c_array.add(1, col_offset*2 +3);
      nps_array.add(1, pnpse);  // just the eps ports
      c_array.add(2, col_offset*3 +1);
      nps_array.add(2, pnpsc);  // just the CA nodes & ports
      c_array.add(3, col_offset*4 -1);
      nps_array.add(3, pnpsa);  // this should be everything

      for(int n = 0; n < 4; n++)
      {
        rw = row;
        cl = c_array.get(n);
        PFM_NodePortStatus nps = nps_array.get(n);
        
        Toolkit.printString(Long.toString(nps.total_nodes),          cl, rw++, FrgndTxtColor);
        Toolkit.printString(Long.toString(nps.total_ports),          cl, rw++, FrgndTxtColor);
        rw++;

        long val = 0L;
        if(nps != null)
        for (PortCounterName counter : PortCounterName.PFM_ALL_COUNTERS)
        {
          /* show the count values, and color non-zero error counters red */
       
          val = nps.port_counters[counter.ordinal()];
          val_color = (PortCounterName.PFM_ERROR_COUNTERS.contains(counter) && (val != 0)) ? ErrorTxtColor: FrgndTxtColor;
          Toolkit.printString(Long.toString(val),  cl, rw++, val_color);
          
          /* separate errors from other counters with a blank line*/
          if(counter.equals(PFM_Port.PortCounterName.vl15_dropped))
            rw++;
        }
      }
      row = paintPortErrors(1, rw+2, pmpa);
    }
    else
    {
      logger.warning("The PerfMgr Objects seems to be null");      
    }
    return true;
  }
  
  public boolean paintBackground_2(int screenNum) throws Exception
  {
    // static graphics and text
    int column = 1;
    int row    = 4;
    
    // counter (attribute) column is 20 wide, last 4 get remainder
    int attribute_width = 20;
    int col_offset = (ScreenCols -attribute_width)/4;
    int col = column;
    
    printTitleString("Performance Manager", row++);
    row++;
    Toolkit.drawHorizontalLine(column, row, ScreenCols-4, ErrorTxtColor);

    Toolkit.printString("counter", col+2, row, BkgndTxtColor);
    
    Toolkit.printString(OSM_NodeType.SW_NODE.getAbrevName(), col+=col_offset+6, row, BkgndTxtColor);
    Toolkit.printString(OSM_NodeType.SW_NODE.getAbrevName()+"(esp0)", col+=col_offset-4, row, BkgndTxtColor);
    Toolkit.printString(OSM_NodeType.CA_NODE.getAbrevName(), col+=col_offset-2, row, BkgndTxtColor);
    Toolkit.printString("All", col+=col_offset-2, row++, BkgndTxtColor);

    Toolkit.printString("Total Nodes:", column, row++, BkgndTxtColor);
    Toolkit.printString("Total Ports:", column, row++, BkgndTxtColor);
    row++;

    for(PFM_Port.PortCounterName n : PortCounterName.PFM_ALL_COUNTERS)
    {
      Toolkit.printString(n + ":", column, row++, BkgndTxtColor);
      
      /* separate errors from other counters */
      if(n.equals(PFM_Port.PortCounterName.vl15_dropped))
        row++;
    }
    
    row++;
    Toolkit.drawHorizontalLine(column, row, 30, ErrorTxtColor);
     Toolkit.printString("top errors", column+2, row++, BkgndTxtColor);
    return true;
  }

  public boolean paintForeground_3(int screenNum) throws Exception
  {
    // data from the service (may need to pad these)
    ArrayList<SBN_NodePortStatus> nps_array  = new ArrayList<SBN_NodePortStatus>();
    ArrayList <Integer> c_array  = new ArrayList<Integer>();
    
    CharColor err_color = null;
    
    int row    = 7;
    
    // attribute column is 20 wide, last 4 get remainder
    int attribute_width = 20;
    int col_offset = (ScreenCols -attribute_width)/4;
    
    
    SBN_NodePortStatus tots = new SBN_NodePortStatus();
    SBN_NodePortStatus nps = null;
    int cl= 0, rw=0;
    
    /* keep in order, top down.  See row value increment */

    if(SysInfo != null)
    {
      nps_array.add(0, SysInfo.SW_PortStatus);
      c_array.add(0, col_offset + 1);
      nps_array.add(1, SysInfo.CA_PortStatus);
      c_array.add(1, col_offset*2 +1);
      nps_array.add(2, SysInfo.RT_PortStatus);
      c_array.add(2, col_offset*3 +1);
      nps_array.add(3, tots);
      c_array.add(3, col_offset*4 +1);

      for(int n = 0; n < 4; n++)
      {
        rw = row;
        cl = c_array.get(n);
        nps = nps_array.get(n);
        
        Toolkit.printString(Long.toString(nps.total_nodes),          cl, rw++, FrgndTxtColor);
        tots.total_nodes += nps.total_nodes;
        Toolkit.printString(Long.toString(nps.total_ports),      cl, rw++, FrgndTxtColor);
        tots.total_ports += nps.total_ports;
        rw++;
      Toolkit.printString(Long.toString(nps.ports_active),        cl, rw++, FrgndTxtColor);
      tots.ports_active += nps.ports_active;
      Toolkit.printString(Long.toString(nps.ports_down),          cl, rw++, FrgndTxtColor);
      
      tots.ports_down += nps.ports_down;
      err_color = nps.ports_disabled != 0? ErrorTxtColor: FrgndTxtColor;
      Toolkit.printString(Long.toString(nps.ports_disabled),      cl, rw++, err_color);
      tots.ports_disabled += nps.ports_disabled;
      rw++;
      Toolkit.printString(Long.toString(nps.ports_1X),            cl, rw++, FrgndTxtColor);
      tots.ports_1X += nps.ports_1X;
      Toolkit.printString(Long.toString(nps.ports_4X),            cl, rw++, FrgndTxtColor);
      tots.ports_4X += nps.ports_4X;
      Toolkit.printString(Long.toString(nps.ports_8X),            cl, rw++, FrgndTxtColor);
      tots.ports_8X += nps.ports_8X;
      Toolkit.printString(Long.toString(nps.ports_12X),           cl, rw++, FrgndTxtColor);
      tots.ports_12X += nps.ports_12X;
      err_color = nps.ports_reduced_width != 0? ErrorTxtColor: FrgndTxtColor;
      Toolkit.printString(Long.toString(nps.ports_reduced_width), cl, rw++, err_color);
      tots.ports_reduced_width += nps.ports_reduced_width;
      Toolkit.printString(Long.toString(nps.ports_unknown_width), cl, rw++, FrgndTxtColor);
      tots.ports_unknown_width += nps.ports_unknown_width;
      Toolkit.printString(Long.toString(nps.ports_unenabled_width), cl, rw++, FrgndTxtColor);
      tots.ports_unenabled_width += nps.ports_unenabled_width;
      rw++;
      Toolkit.printString(Long.toString(nps.ports_sdr),           cl, rw++, FrgndTxtColor);
      tots.ports_sdr += nps.ports_sdr;
      Toolkit.printString(Long.toString(nps.ports_ddr),           cl, rw++, FrgndTxtColor);
      tots.ports_ddr += nps.ports_ddr;
      Toolkit.printString(Long.toString(nps.ports_qdr),           cl, rw++, FrgndTxtColor);
      tots.ports_qdr += nps.ports_qdr;
      Toolkit.printString(Long.toString(nps.ports_fdr10),           cl, rw++, FrgndTxtColor);
      tots.ports_fdr10 += nps.ports_fdr10;
      Toolkit.printString(Long.toString(nps.ports_fdr),           cl, rw++, FrgndTxtColor);
      tots.ports_fdr += nps.ports_fdr;
      Toolkit.printString(Long.toString(nps.ports_edr),           cl, rw++, FrgndTxtColor);
      tots.ports_edr += nps.ports_edr;
      err_color = nps.ports_reduced_speed != 0? ErrorTxtColor: FrgndTxtColor;
      Toolkit.printString(Long.toString(nps.ports_reduced_speed), cl, rw++, err_color);
      tots.ports_reduced_speed += nps.ports_reduced_speed;
      Toolkit.printString(Long.toString(nps.ports_unknown_speed), cl, rw++, FrgndTxtColor);
      tots.ports_unknown_speed += nps.ports_unknown_speed;
      Toolkit.printString(Long.toString(nps.ports_unenabled_speed), cl, rw++, FrgndTxtColor);
      tots.ports_unenabled_speed += nps.ports_unenabled_speed;
      }
      row = rw;

      // show the ports with problems
      row++;
      int column = 1;
      Toolkit.drawHorizontalLine(column, row, 30, ErrorTxtColor);
      Toolkit.printString("Disabled Ports", column+2, row++, BkgndTxtColor);
      for(SBN_NodePortStatus ps: nps_array)
        row = paintPortProblem(column, row, ps.NodeType, ps.disabled_ports);
      
      clearRow(row);
      row++;
      Toolkit.drawHorizontalLine(column, row, 30, ErrorTxtColor);
      Toolkit.printString("Reduced Speed", column+2, row++, BkgndTxtColor);
      for(SBN_NodePortStatus ps: nps_array)
        row = paintPortProblem(column, row, ps.NodeType, ps.reduced_speed_ports);
      
      clearRow(row);
      row++;
      Toolkit.drawHorizontalLine(column, row, 30, ErrorTxtColor);
      Toolkit.printString("Reduced Width", column+2, row++, BkgndTxtColor);
      for(SBN_NodePortStatus ps: nps_array)
        row = paintPortProblem(column, row, ps.NodeType, ps.reduced_width_ports);
      
      clearRow(row);
      row++;
      Toolkit.drawHorizontalLine(column, row, 30, ErrorTxtColor);
      Toolkit.printString("Unenabled Width", column+2, row++, BkgndTxtColor);
      for(SBN_NodePortStatus ps: nps_array)
        row = paintPortProblem(column, row, ps.NodeType, ps.unenabled_width_ports);
      
      clearRow(row);
      row++;
      Toolkit.drawHorizontalLine(column, row, 30, ErrorTxtColor);
      Toolkit.printString("Unenabled Speed", column+2, row++, BkgndTxtColor);
      for(SBN_NodePortStatus ps: nps_array)
        row = paintPortProblem(column, row, ps.NodeType, ps.unenabled_speed_ports);
      
    }
    else
    {
      logger.warning("The SysInfo seems to be null");      
    }
    return true;
  }
  
  public boolean paintBackground_3(int screenNum) throws Exception
  {
    // static graphics and text
    int column = 1;
    int row    = 4;
    
    // attribute column is 20 wide, last 4 get remainder
    int attribute_width = 20;
    int col_offset = (ScreenCols -attribute_width)/4;
    int col = column;
    
    printTitleString("Port Status", row++);
    row++;
    Toolkit.drawHorizontalLine(column, row, ScreenCols-4, ErrorTxtColor);

    Toolkit.printString("attribute", col+2, row, BkgndTxtColor);
    
    Toolkit.printString("SW", col+=col_offset, row, BkgndTxtColor);
    Toolkit.printString("CA", col+=col_offset, row, BkgndTxtColor);
    Toolkit.printString("RT", col+=col_offset, row, BkgndTxtColor);
    Toolkit.printString("All", col+=col_offset, row++, BkgndTxtColor);

    Toolkit.printString("Total Nodes:", column, row++, BkgndTxtColor);
    Toolkit.printString("Total Ports:", column, row++, BkgndTxtColor);
    row++;
    Toolkit.printString("Active:", column, row++, BkgndTxtColor);
    Toolkit.printString("Down:", column, row++, BkgndTxtColor);
    Toolkit.printString("Disabled:", column, row++, BkgndTxtColor);
    row++;
    Toolkit.printString("1X:", column, row++, BkgndTxtColor);
    Toolkit.printString("4X:", column, row++, BkgndTxtColor);
    Toolkit.printString("8X:", column, row++, BkgndTxtColor);
    Toolkit.printString("12X:", column, row++, BkgndTxtColor);
    Toolkit.printString("reduced width:", column, row++, BkgndTxtColor);
    Toolkit.printString("unknown width:", column, row++, BkgndTxtColor);
    Toolkit.printString("unenabled width:", column, row++, BkgndTxtColor);
    row++;
    Toolkit.printString("SDR:", column, row++, BkgndTxtColor);
    Toolkit.printString("DDR:", column, row++, BkgndTxtColor);
    Toolkit.printString("QDR:", column, row++, BkgndTxtColor);
    Toolkit.printString("FDR10:", column, row++, BkgndTxtColor);
    Toolkit.printString("FDR:", column, row++, BkgndTxtColor);
    Toolkit.printString("EDR:", column, row++, BkgndTxtColor);
    Toolkit.printString("reduced speed:", column, row++, BkgndTxtColor);
    Toolkit.printString("unknown speed:", column, row++, BkgndTxtColor);
    Toolkit.printString("unenabled speed:", column, row++, BkgndTxtColor);
    return true;
  }

  public boolean paintForeground_4(int screenNum) throws Exception
  {
    // data from the service (may need to pad these)
    int column = 40;
    int row    = 6;
    
    /* keep in order, top down.  See row value increment */
    if(EventStats != null)
    {
      for(OsmEvent s : OsmEvent.OSM_STAT_EVENTS)
         Toolkit.printString(Long.toString(EventStats.getCounter(s)), column, row++, FrgndTxtColor);
    }
    else
    {
      logger.warning("The EventStats seems to be null");      
    }
    return true;
  }
  
  public boolean paintBackground_4(int screenNum) throws Exception
  {
    // static graphics and text
    int column = 1;
    int row    = 4;
    
    printTitleString("OpenSM Events", row++);

    Toolkit.drawHorizontalLine(column, row, 30, ErrorTxtColor);
    Toolkit.printString("Event Counters", column+2, row++, BkgndTxtColor);
    
    for(OsmEvent s : OsmEvent.OSM_STAT_EVENTS)
     Toolkit.printString(s.getEventName() + ":", column, row++, BkgndTxtColor);
    
    row++;
    Toolkit.drawHorizontalLine(column, row, 30, ErrorTxtColor);
    Toolkit.printString("Recent Events", column+2, row++, BkgndTxtColor);
    
    
    return true;
  }

  public boolean paintForeground_5(int screenNum) throws Exception
  {
    // data from the service (may need to pad these)
    int column = 40;
    int row    = 6;
    
    /* keep in order, top down.  See row value increment */

    if(RemoteServerStatus != null)
    {
      Toolkit.printString(RemoteServerStatus.Server.getHost(),                 column, row++, FrgndTxtColor);
      Toolkit.printString(Integer.toString(RemoteServerStatus.Server.getPortNum()),                 column, row++, FrgndTxtColor);
      Toolkit.printString(RemoteServerStatus.Server.getServerName(),                 column, row++, FrgndTxtColor);
      Toolkit.printString(Long.toString(RemoteServerStatus.Server.getThreadId()),                 column, row++, FrgndTxtColor);
      row++;    
      if(ParentSessionStatus != null)
      {
        Toolkit.printString(ParentSessionStatus.getAuthenticator(),       column, row++, FrgndTxtColor);
        Toolkit.printString(Boolean.toString(RemoteServerStatus.AllowLocalHost), column, row++, FrgndTxtColor);
        Toolkit.printString(ParentSessionStatus.getProtocol(),       column, row++, FrgndTxtColor);
      }
      row++;    
      printString(Integer.toString(RemoteServerStatus.NativeUpdatePeriodSecs), column, row++, FrgndTxtColor);
      printString(Integer.toString(RemoteServerStatus.NativeReportPeriodSecs), column, row++, FrgndTxtColor);
      printString(Long.toString(RemoteServerStatus.NativeHeartbeatCount), column, row++, FrgndTxtColor);
      printString(Integer.toString(RemoteServerStatus.ServerUpdatePeriodSecs), column, row++, FrgndTxtColor);
      printString(Long.toString(RemoteServerStatus.ServerHeartbeatCount), column, row++, FrgndTxtColor);
      printString(Integer.toString(RemoteServerStatus.NativeEventTimeoutMsecs), column, row++, FrgndTxtColor);
      printString(Long.toString(RemoteServerStatus.NativeEventCount), column, row++, FrgndTxtColor);
      row++;    
      printString(Integer.toString(RemoteServerStatus.Server.getCurrent_Sessions().size()), column, row++, FrgndTxtColor);
      printString(Integer.toString(RemoteServerStatus.Server.getHistorical_Sessions().size()), column, row++, FrgndTxtColor);
      printString(Integer.toString(RemoteServerStatus.MaxParentSessions), column, row++, FrgndTxtColor);
      printString(Integer.toString(RemoteServerStatus.MaxChildSessions), column, row++, FrgndTxtColor);
    }
    else
    {
      logger.warning("The RemoteServerStatus seems to be null");      
    }
    
    row+=2;
    if(ParentSessionStatus != null)
    {
      Toolkit.printString(ParentSessionStatus.getHost(),       column, row++, FrgndTxtColor);
      Toolkit.printString(Integer.toString(ParentSessionStatus.getPort()),       column, row++, FrgndTxtColor);
      Toolkit.printString(ParentSessionStatus.getSessionName(),       column, row++, FrgndTxtColor);
      Toolkit.printString(Long.toString(ParentSessionStatus.getThreadId()),       column, row++, FrgndTxtColor);
      Toolkit.printString(ParentSessionStatus.getUser(),       column, row++, FrgndTxtColor);
      Toolkit.printString(ParentSessionStatus.getOpenTime().toString(),       column, row++, FrgndTxtColor);
      row++;
      Toolkit.printString(ParentSessionStatus.getAuthenticator(),       column, row++, FrgndTxtColor);
      Toolkit.printString(ParentSessionStatus.getClientProtocol(),       column, row++, FrgndTxtColor);
    }
    else
    {
      logger.warning("The ParentSessionStatus seems to be null");      
    }
        
    row++;
    Toolkit.printString(Long.toString(this.refreshPeriod),       column, row++, FrgndTxtColor);
    
    return true;
  }
  
  public boolean paintBackground_5(int screenNum) throws Exception
  {
    // static graphics and text
    int column = 1;
    int row    = 4;
    
    printTitleString("Monitor Service", row++);

    Toolkit.drawHorizontalLine(column, row, 30, ErrorTxtColor);
    Toolkit.printString("Remote Service", column+2, row++, BkgndTxtColor);
    Toolkit.printString("Host:", column, row++, BkgndTxtColor);
    Toolkit.printString("Port:", column, row++, BkgndTxtColor);
    Toolkit.printString("Name:", column, row++, BkgndTxtColor);
    Toolkit.printString("Thread ID:", column, row++, BkgndTxtColor);
    row++;    
    Toolkit.printString("Authenticator:", column, row++, BkgndTxtColor);
    Toolkit.printString(" (localhost allowed?):", column, row++, BkgndTxtColor);
    Toolkit.printString("Protocol:", column, row++, BkgndTxtColor);
    row++;    
    Toolkit.printString("Plugin refresh period (seconds):", column, row++, BkgndTxtColor);
    Toolkit.printString("Plugin report period (seconds):", column, row++, BkgndTxtColor);
    Toolkit.printString("Plugin refresh count:", column, row++, BkgndTxtColor);
    Toolkit.printString("Server refresh period (seconds):", column, row++, BkgndTxtColor);
    Toolkit.printString("Server refresh count:", column, row++, BkgndTxtColor);
    Toolkit.printString("Event timeout (milliseconds):", column, row++, BkgndTxtColor);
    Toolkit.printString("Event count:", column, row++, BkgndTxtColor);
    row++;    
    Toolkit.printString("Active Clients:", column, row++, BkgndTxtColor);
    Toolkit.printString("Cumulative Clients:", column, row++, BkgndTxtColor);
    Toolkit.printString("Max Parent Sessions:", column, row++, BkgndTxtColor);
    Toolkit.printString("Max Child Sessions (per parent):", column, row++, BkgndTxtColor);
    row++;
    Toolkit.drawHorizontalLine(column, row, 30, ErrorTxtColor);
    Toolkit.printString("OsmConsole Client (this)", column+2, row++, BkgndTxtColor);
    Toolkit.printString("Remote Host:", column, row++, BkgndTxtColor);
    Toolkit.printString("Port:", column, row++, BkgndTxtColor);
    Toolkit.printString("Name:", column, row++, BkgndTxtColor);
    Toolkit.printString("Thread ID:", column, row++, BkgndTxtColor);
    Toolkit.printString("User ID:", column, row++, BkgndTxtColor);
    Toolkit.printString("up since:", column, row++, BkgndTxtColor);
    row++;    
    Toolkit.printString("Authenticator:", column, row++, BkgndTxtColor);
    Toolkit.printString("Protocol:", column, row++, BkgndTxtColor);
    row++;    
    Toolkit.printString("Console refresh period (seconds):", column, row++, BkgndTxtColor);
    return true;
  }

  public boolean paintForeground_6(int screenNum) throws Exception
  {
    // data from the service (may need to pad these)
    int column = 40;
    int row    = 20;
    BinList <SBN_Node> UniqueNodes = new BinList <SBN_Node>();
    BinList <PFM_Node> pNodes = new BinList <PFM_Node>();
    ArrayList<PFM_Node> pmna = new ArrayList<PFM_Node>();
    ArrayList<SBN_Node> sbna = new ArrayList<SBN_Node>();
    int totalSW = 0;
    int totalSWp = 0;
    int totalCA = 0;
    int totalCAp = 0;
    int totalNodes = 0;
    int totalPorts = 0;

    if(AllNodes != null)
    {
      PFM_Node[] pna = AllNodes.getPerfMgrNodes();
      SBN_Node[] sna = AllNodes.getSubnNodes();
      // the perfmgr may not have returned data due to start-up delay, check
      if((pna != null) && (sna != null))
      {
      pmna = new ArrayList<PFM_Node>(Arrays.asList(pna));
      sbna = new ArrayList<SBN_Node>(Arrays.asList(sna));
    
      // build up bins of unque nodes based on these criteria
      String key = null;
      for(SBN_Node sn: sbna)
      {
        key = Short.toString(sn.node_type) + "-" + Short.toString(sn.num_ports) + "-" +Integer.toString(sn.device_id) + Integer.toString(sn.partition_cap) + Integer.toString(sn.port_num_vendor_id) + Integer.toString(sn.revision) + Short.toString(sn.base_version) + Short.toString(sn.class_version);
        UniqueNodes.add(sn, key);
      }
      
      for(PFM_Node pn: pmna)
      {
        key = Short.toString(pn.getNum_ports()) + Boolean.toString(pn.isEsp0());
        pNodes.add(pn, key);
      }
      
      for(ArrayList<SBN_Node> sn: UniqueNodes)
      {
        // each node in the bin looks identical, so just use the first one
        SBN_Node s = sn.get(0);
        // each column should match the attribute from the background screen
        if(row < (ScreenRows - 3))
        {
        Toolkit.printString(Integer.toString(sn.size()),               NodeColumns[0], row, FrgndTxtColor);
        Toolkit.printString(OSM_NodeType.get(s).getAbrevName(),        NodeColumns[1], row, FrgndTxtColor);
        Toolkit.printString(Short.toString(s.num_ports),               NodeColumns[2], row, FrgndTxtColor);
        Toolkit.printString(Integer.toHexString(s.device_id),          NodeColumns[3], row, FrgndTxtColor);
        Toolkit.printString(Integer.toHexString(s.port_num_vendor_id), NodeColumns[4], row, FrgndTxtColor);
        Toolkit.printString(Integer.toHexString(s.revision),           NodeColumns[5], row, FrgndTxtColor);
        Toolkit.printString(Short.toString(s.base_version),            NodeColumns[6], row, FrgndTxtColor);
        Toolkit.printString(Short.toString(s.class_version),           NodeColumns[7], row, FrgndTxtColor);
        Toolkit.printString(Integer.toHexString(s.partition_cap),      NodeColumns[8], row, FrgndTxtColor);
        }
        /* calculate totals here */
        if(s.node_type == 1)
        {
          // CA totals
          totalCA  += sn.size();
          totalCAp += (s.num_ports * sn.size());
        }
        if(s.node_type == 2)
        {
          // SW totals
          totalSW  += sn.size();
          totalSWp += (s.num_ports * sn.size());
        }
        row++;
      }
      
      totalNodes = totalCA  + totalSW;
      totalPorts = totalCAp + totalSWp;
      
      // overall totals
      row = 7;
      Toolkit.printString(Integer.toString(totalCA),               NodeColumns[0], row, FrgndTxtColor);
      Toolkit.printString(OSM_NodeType.CA_NODE.getAbrevName(),     NodeColumns[1], row, FrgndTxtColor);
      Toolkit.printString(Integer.toString(totalCAp),              NodeColumns[2], row, FrgndTxtColor);
      row++;
      Toolkit.printString(Integer.toString(totalSW),               NodeColumns[0], row, FrgndTxtColor);
      Toolkit.printString(OSM_NodeType.SW_NODE.getAbrevName(),     NodeColumns[1], row, FrgndTxtColor);
      Toolkit.printString(Integer.toString(totalSWp),              NodeColumns[2], row, FrgndTxtColor);
      row++;
      Toolkit.printString(Integer.toString(totalNodes),             NodeColumns[0], row, FrgndTxtColor);
      Toolkit.printString("ALL",                                     NodeColumns[1], row, FrgndTxtColor);
      Toolkit.printString(Integer.toString(totalPorts),              NodeColumns[2], row, FrgndTxtColor);
      row+=3;

      // totals per type
      int pn_type = 0;
      String esp = "";
      for(ArrayList<PFM_Node> pn: pNodes)
      {
        // each node in the bin looks identical, so just use the first one
        PFM_Node p = pn.get(0);
        pn_type = p.getNum_ports() > 2 ? 2: 1;
        esp = p.isEsp0() ? "  Y": " ";
        
        // each column should match the attribute from the background screen
        Toolkit.printString(Integer.toString(pn.size()),               NodeColumns[0], row, FrgndTxtColor);
        Toolkit.printString(OSM_NodeType.get(pn_type).getAbrevName(),  NodeColumns[1], row, FrgndTxtColor);
        Toolkit.printString(Short.toString(p.num_ports),               NodeColumns[2], row, FrgndTxtColor);
        Toolkit.printString(esp,                                       NodeColumns[3], row, FrgndTxtColor);
        row++;
      }
      row++;
      }
      else
        logger.warning("The data from the PerfManager is not available... yet");
    }
    else
    {
      logger.warning("The OSM_Nodes seems to be null");      
    }
    return true;
  }
  
  public boolean paintBackground_6(int screenNum) throws Exception
  {
    // static graphics and text
    int column = 1;
    int col_offset = 1;
    int row    = 4;
    int stringSizes = 0;
    String[] heads = { "QTY", "Type", "# ports", "device_id", "pnv_id", "revision", "base_v", "class_v", "prt_cap" };
    
    printTitleString("OpenSM Nodes", row++);

    row    = 19;
    Toolkit.drawHorizontalLine(column, row, ScreenCols-3, ErrorTxtColor);
    
    // evenly space the columns
    int j = 0;
    for(String s: heads)
      stringSizes += s.length();
    
    col_offset = (ScreenCols - (stringSizes + 2))/(heads.length);
    int c_pos = 3;
    int prev_string_size = 0;
    NodeColumns = new int[heads.length];
    
   // these are the columns, which need to be used for the foreground too
    for(String s: heads)
    {
      if(j == 0)
        c_pos = 3;
      else
        c_pos += col_offset + prev_string_size;
      prev_string_size = s.length();
      NodeColumns[j++] = c_pos;
      Toolkit.printString(s, c_pos, row, BkgndTxtColor);
    }
    
    row=6;
    j = 0;
    Toolkit.drawHorizontalLine(column, row, ScreenCols/2, ErrorTxtColor);
    for(String s: heads)
    {
      Toolkit.printString(s, NodeColumns[j++], row, BkgndTxtColor);
      if(j > 1)
        break;
    }
    Toolkit.printString("T ports", NodeColumns[j++], row, BkgndTxtColor);

    row=11;
    j = 0;
    Toolkit.drawHorizontalLine(column, row, ScreenCols/2, ErrorTxtColor);
    for(String s: heads)
    {
      Toolkit.printString(s, NodeColumns[j++], row, BkgndTxtColor);
      if(j > 2)
        break;
    }
    Toolkit.printString("esp0", NodeColumns[j++], row, BkgndTxtColor);
    return true;
  }

  public boolean paintForeground_7(int screenNum) throws Exception
  {
    // data from the service (may need to pad these)
    ArrayList <Integer> c_array  = new ArrayList<Integer>();
    long LrefreshPeriod = refreshPeriod * 2;
    CharColor cc = FrgndTxtColor;
    int[] num_links  = new int[4];
    int[] num_active   = new int[4];
    int[] num_down     = new int[4];
    int[] num_traffic  = new int[4];
    int[] num_errors   = new int[4];
    
    int row    = 7;
    int rw     = 0;
    int cl     = 0;
    
    // attribute column is 20 wide, last 4 get remainder
    int attribute_width = 20;
    int col_offset = (ScreenCols -attribute_width)/4;
     
    if((AllPorts != null) && (AllNodes != null))
    {
      // some link attributes depend on perfmgr data, so wait until available
      if((AllNodes.getSubnNodes() != null) && (AllNodes.getPerfMgrNodes() != null))
      {
      /** periodically recalculate these, time intensive */
      if(!Link_Refresh || (timeLoopCounter%LrefreshPeriod == 0))
      {
        /* init flag - set true after first time throught, use time period after that */
        Link_Refresh = true;
        
        // clear the counters
        for(int d = 0; d < 4; d++)
        {
          Lnum_nodes[d] = 0;
          Lnum_ports[d] = 0;
        }
      ArrayList<SBN_Node> sbna = new ArrayList<SBN_Node>(Arrays.asList(AllNodes.getSubnNodes()));
      for(SBN_Node sn: sbna)
      {
        if(OSM_NodeType.get(sn) == OSM_NodeType.SW_NODE)
        {
          Lnum_nodes[0] +=1;
          Lnum_ports[0] += sn.num_ports;
        } else if(OSM_NodeType.get(sn) == OSM_NodeType.CA_NODE)
        {
          Lnum_nodes[1] +=1;
          Lnum_ports[1] += sn.num_ports;
        }
        else
        {
          Lnum_nodes[2] +=1;
          Lnum_ports[2] += sn.num_ports;
        }
      }

      // create IB_Links
      ibla = AllPorts.createIB_Links(AllNodes);
      
      // clear all other data structures (arrays and binLists)
      ibls = new ArrayList<IB_Link>();
      iblc = new ArrayList<IB_Link>();
      iblQ = new ArrayList<IB_Link>();  // unknown
      aLinkBins = new BinList <IB_Link>();
      sLinkBins = new BinList <IB_Link>();
      cLinkBins = new BinList <IB_Link>();
      QLinkBins = new BinList <IB_Link>(); // unknown
      
      for(IB_Link link: ibla)
      {
        // create a list of switch and edge links
        if(link.getLinkType() == IB_LinkType.SW_LINK)
          ibls.add(link);
        else if(link.getLinkType() == IB_LinkType.CA_LINK)
          iblc.add(link);
        else
          iblQ.add(link);
        
        // bin up the types for ALL links
        if(link.hasTraffic())
          aLinkBins.add(link, "Traffic:");

        if(link.hasErrors())
          aLinkBins.add(link, "Errors:");
        
        aLinkBins.add(link, "State: " + link.getState().getStateName());
        aLinkBins.add(link, "Speed: " + link.getSpeed().getSpeedName());
        aLinkBins.add(link, "Width: " + link.getWidth().getWidthName());
        aLinkBins.add(link, "Rate: " + link.getRate().getRateName());
      }
      
      for(IB_Link link: ibls)
      {
        // bin up the types for SW links
        if(link.hasTraffic())
          sLinkBins.add(link, "Traffic:");

        if(link.hasErrors())
          sLinkBins.add(link, "Errors:");
        
        sLinkBins.add(link, "State: " + link.getState().getStateName());
        sLinkBins.add(link, "Speed: " + link.getSpeed().getSpeedName());
        sLinkBins.add(link, "Width: " + link.getWidth().getWidthName());
        sLinkBins.add(link, "Rate: " + link.getRate().getRateName());
      }
      
      for(IB_Link link: iblc)
      {
        // bin up the types for CA links
        if(link.hasTraffic())
          cLinkBins.add(link, "Traffic:");

        if(link.hasErrors())
          cLinkBins.add(link, "Errors:");
        
        cLinkBins.add(link, "State: " + link.getState().getStateName());
        cLinkBins.add(link, "Speed: " + link.getSpeed().getSpeedName());
        cLinkBins.add(link, "Width: " + link.getWidth().getWidthName());
        cLinkBins.add(link, "Rate: " + link.getRate().getRateName());
      }
      
      for(IB_Link link: iblQ)
      {
        // bin up the types for CA links
        if(link.hasTraffic())
          QLinkBins.add(link, "Traffic:");

        if(link.hasErrors())
          QLinkBins.add(link, "Errors:");
        
        QLinkBins.add(link, "State: " + link.getState().getStateName());
        QLinkBins.add(link, "Speed: " + link.getSpeed().getSpeedName());
        QLinkBins.add(link, "Width: " + link.getWidth().getWidthName());
        QLinkBins.add(link, "Rate: " + link.getRate().getRateName());
      }
      }
      
      // done processing, get ready for painting      
      Lnum_nodes[3] = AllNodes.SubnNodes.length;
      Lnum_ports[3] = AllPorts.SubnPorts.length;
      num_links[0] = ibls.size();
      num_links[1] = iblc.size();
      num_links[2] = iblQ.size();
      num_links[3] = ibla.size();
      
      // the various Bins may not exist (if there was no element to add the bin doesn't get created) so protect against null     
      num_active[0] = sLinkBins.getBin("State: " + OSM_LinkState.ACTIVE.getStateName()) == null ? 0: sLinkBins.getBin("State: " + OSM_LinkState.ACTIVE.getStateName()).size();
      num_active[1] = cLinkBins.getBin("State: " + OSM_LinkState.ACTIVE.getStateName()) == null ? 0: cLinkBins.getBin("State: " + OSM_LinkState.ACTIVE.getStateName()).size();
      num_active[2] = QLinkBins.getBin("State: " + OSM_LinkState.ACTIVE.getStateName()) == null ? 0: QLinkBins.getBin("State: " + OSM_LinkState.ACTIVE.getStateName()).size();
      num_active[3] = aLinkBins.getBin("State: " + OSM_LinkState.ACTIVE.getStateName()) == null ? 0: aLinkBins.getBin("State: " + OSM_LinkState.ACTIVE.getStateName()).size();
      
      num_down[0] = sLinkBins.getBin("State: " + OSM_LinkState.DOWN.getStateName()) == null ? 0: sLinkBins.getBin("State: " + OSM_LinkState.DOWN.getStateName()).size();
      num_down[1] = cLinkBins.getBin("State: " + OSM_LinkState.DOWN.getStateName()) == null ? 0: cLinkBins.getBin("State: " + OSM_LinkState.DOWN.getStateName()).size();
      num_down[2] = QLinkBins.getBin("State: " + OSM_LinkState.DOWN.getStateName()) == null ? 0: QLinkBins.getBin("State: " + OSM_LinkState.DOWN.getStateName()).size();
      num_down[3] = aLinkBins.getBin("State: " + OSM_LinkState.DOWN.getStateName()) == null ? 0: aLinkBins.getBin("State: " + OSM_LinkState.DOWN.getStateName()).size();
      
      num_traffic[0] = sLinkBins.getBin("Traffic:") == null ? 0: sLinkBins.getBin("Traffic:").size();
      num_traffic[1] = cLinkBins.getBin("Traffic:") == null ? 0: cLinkBins.getBin("Traffic:").size();
      num_traffic[2] = QLinkBins.getBin("Traffic:") == null ? 0: QLinkBins.getBin("Traffic:").size();
      num_traffic[3] = aLinkBins.getBin("Traffic:") == null ? 0: aLinkBins.getBin("Traffic:").size();
      
      num_errors[0] = sLinkBins.getBin("Errors:") == null ? 0: sLinkBins.getBin("Errors:").size();
      num_errors[1] = cLinkBins.getBin("Errors:") == null ? 0: cLinkBins.getBin("Errors:").size();
      num_errors[2] = QLinkBins.getBin("Errors:") == null ? 0: QLinkBins.getBin("Errors:").size();
      num_errors[3] = aLinkBins.getBin("Errors:") == null ? 0: aLinkBins.getBin("Errors:").size();
      
      c_array.add(0, col_offset + 1);
      c_array.add(1, col_offset*2 +1);
      c_array.add(2, col_offset*3 +1);
      c_array.add(3, col_offset*4 +1);

      int column = 1;
      for(int n = 0; n < 4; n++)
      {
        rw = row;
        cl = c_array.get(n);
        // total nodes, ports and links, broken down by type
        Toolkit.printString(Long.toString(Lnum_nodes[n]),          cl, rw++, FrgndTxtColor);
        Toolkit.printString(Long.toString(Lnum_ports[n]),          cl, rw++, FrgndTxtColor);
        Toolkit.printString(Long.toString(num_links[n]),          cl, rw++, FrgndTxtColor);
        rw++;
        // active and down links, broken down by type
        Toolkit.printString(Long.toString(num_active[n]),        cl, rw++, FrgndTxtColor);
        Toolkit.printString(Long.toString(num_down[n]),         cl, rw++, FrgndTxtColor);
        rw++;
        
        // links with traffic and errors, broken down by type
        Toolkit.printString("traffic:", column, rw, BkgndTxtColor);
        Toolkit.printString(Long.toString(num_traffic[n]),        cl, rw++, FrgndTxtColor);
        Toolkit.printString("errors:", column, rw, BkgndTxtColor);
        if(num_errors[n] == 0)
          cc = FrgndTxtColor;
        else
          cc = ErrorTxtColor;
        Toolkit.printString(Long.toString(num_errors[n]),         cl, rw++, cc);
      }
      // from here down, paint the foreground and background, because I don't know which attributes
      // are non-zero.
      // width, speed and rate, broken down by type     
      // use the "ALL" binlist to determine the number of rows for each width, speed, and rate type
      
      // how many unique widths (must be at least one)?
      row = rw+1;
      Toolkit.drawHorizontalLine(column, row, 7, ErrorTxtColor);
      Toolkit.printString("width", column+1, row++, BkgndTxtColor);
      
      for(OSM_LinkWidth lw: OSM_LinkWidth.OSMLINK_ALL_WIDTHS)
      {
        ArrayList<IB_Link> la = aLinkBins.getBin("Width: " + lw.getWidthName());
        // place a background if necessary
        if(la != null)
        {
          // there is at least one of these, so loop through all types
          Toolkit.printString(lw.getWidthName(), column, row, BkgndTxtColor);
          num_links[0] = sLinkBins.getBin("Width: " + lw.getWidthName()) == null ? 0: sLinkBins.getBin("Width: " + lw.getWidthName()).size();
          num_links[1] = cLinkBins.getBin("Width: " + lw.getWidthName()) == null ? 0: cLinkBins.getBin("Width: " + lw.getWidthName()).size();
          num_links[2] = QLinkBins.getBin("Width: " + lw.getWidthName()) == null ? 0: QLinkBins.getBin("Width: " + lw.getWidthName()).size();
          num_links[3] = aLinkBins.getBin("Width: " + lw.getWidthName()) == null ? 0: aLinkBins.getBin("Width: " + lw.getWidthName()).size();
          
          for(int n = 0; n < 4; n++)
          {
            rw = row;
            cl = c_array.get(n);
            Toolkit.printString(Long.toString(num_links[n]),        cl, rw++, FrgndTxtColor);
          }
          row = rw;
        }
      }
     
      // how many unique speeds (must be at least one)?
      row = rw+1;
      Toolkit.drawHorizontalLine(column, row, 7, ErrorTxtColor);
      Toolkit.printString("speed", column+1, row++, BkgndTxtColor);
      
      for(OSM_LinkSpeed ls: OSM_LinkSpeed.OSMLINK_ALL_SPEEDS)
      {
        ArrayList<IB_Link> la = aLinkBins.getBin("Speed: " + ls.getSpeedName());
        // place a background if necessary
        if(la != null)
        {
          // there is at least one of these, so loop through all types
          Toolkit.printString(ls.getSpeedName(), column, row, BkgndTxtColor);
          num_links[0] = sLinkBins.getBin("Speed: " + ls.getSpeedName()) == null ? 0: sLinkBins.getBin("Speed: " + ls.getSpeedName()).size();
          num_links[1] = cLinkBins.getBin("Speed: " + ls.getSpeedName()) == null ? 0: cLinkBins.getBin("Speed: " + ls.getSpeedName()).size();
          num_links[2] = QLinkBins.getBin("Speed: " + ls.getSpeedName()) == null ? 0: QLinkBins.getBin("Speed: " + ls.getSpeedName()).size();
          num_links[3] = aLinkBins.getBin("Speed: " + ls.getSpeedName()) == null ? 0: aLinkBins.getBin("Speed: " + ls.getSpeedName()).size();
          
          for(int n = 0; n < 4; n++)
          {
            rw = row;
            cl = c_array.get(n);
            Toolkit.printString(Long.toString(num_links[n]),        cl, rw++, FrgndTxtColor);
          }
          row = rw;
        }
      }
     
      // how many unique rates (must be at least one)?
      row = rw+1;
      Toolkit.drawHorizontalLine(column, row, 7, ErrorTxtColor);
      Toolkit.printString("rate", column+1, row++, BkgndTxtColor);
      
      for(OSM_LinkRate lw: OSM_LinkRate.OSMLINK_UNIQUE_RATES)
      {
        ArrayList<IB_Link> la = aLinkBins.getBin("Rate: " + lw.getRateName());
        // place a background if necessary
        if(la != null)
        {
          // there is at least one of these, so loop through all types
          Toolkit.printString(lw.getRateName(), column, row, BkgndTxtColor);
          num_links[0] = sLinkBins.getBin("Rate: " + lw.getRateName()) == null ? 0: sLinkBins.getBin("Rate: " + lw.getRateName()).size();
          num_links[1] = cLinkBins.getBin("Rate: " + lw.getRateName()) == null ? 0: cLinkBins.getBin("Rate: " + lw.getRateName()).size();
          num_links[2] = QLinkBins.getBin("Rate: " + lw.getRateName()) == null ? 0: QLinkBins.getBin("Rate: " + lw.getRateName()).size();
          num_links[3] = aLinkBins.getBin("Rate: " + lw.getRateName()) == null ? 0: aLinkBins.getBin("Rate: " + lw.getRateName()).size();
          
          for(int n = 0; n < 4; n++)
          {
            rw = row;
            cl = c_array.get(n);
            Toolkit.printString(Long.toString(num_links[n]),        cl, rw++, FrgndTxtColor);
          }
          row = rw;
        }
      }
    }
      else
        logger.warning("PerfMgr data is not available... yet");
    }
    else
    {
      logger.warning("The Node and Port info seems to be unavailable");      
    }
    return true;
  }
  
  public boolean paintBackground_7(int screenNum) throws Exception
  {
    // static graphics and text
    int column = 1;
    int row    = 4;
    
    // attribute column is 20 wide, last 4 get remainder
    int attribute_width = 20;
    int col_offset = (ScreenCols -attribute_width)/4;
    int col = column;
    
    printTitleString("Links", row++);
    row++;
    Toolkit.drawHorizontalLine(column, row, ScreenCols-4, ErrorTxtColor);

    Toolkit.printString("attribute", col+2, row, BkgndTxtColor);
    
    Toolkit.printString(OSM_NodeType.SW_NODE.getAbrevName(), col+=col_offset, row, BkgndTxtColor);
    Toolkit.printString(OSM_NodeType.CA_NODE.getAbrevName(), col+=col_offset, row, BkgndTxtColor);
    Toolkit.printString(OSM_NodeType.UNKNOWN.getAbrevName(), col+=col_offset, row, BkgndTxtColor);
    Toolkit.printString("All", col+=col_offset, row++, BkgndTxtColor);

    Toolkit.printString("Total Nodes:", column, row++, BkgndTxtColor);
    Toolkit.printString("Total Ports:", column, row++, BkgndTxtColor);
    Toolkit.printString("Total Links:", column, row++, BkgndTxtColor);
    row++;
    Toolkit.printString("Active:", column, row++, BkgndTxtColor);
    Toolkit.printString("Down:", column, row++, BkgndTxtColor);
    row++;
    return true;
  }

  public boolean paintForeground_8(int screenNum) throws Exception
  {
    // data from the service (may need to pad these)
    int column = 40;
    int row    = 6;
    
    /* keep in order, top down.  See row value increment */

    if(SysInfo != null)
    {
      Toolkit.printString(SysInfo.SM_State,       column, row++, FrgndTxtColor);
    }
    else
    {
      logger.warning("The SysInfo seems to be null");      
    }
    return true;
  }
  
  public boolean paintBackground_8(int screenNum) throws Exception
  {
    // static graphics and text
    int column = 1;
    int row    = 4;
    
    printTitleString("OpenSM Events", row++);

    Toolkit.drawHorizontalLine(column, row, 30, ErrorTxtColor);
    Toolkit.printString("Remote Service", column+2, row++, BkgndTxtColor);
    Toolkit.printString("Host:", column, row++, BkgndTxtColor);
    return true;
  }

  private int  paintOptionMap(HashMap<String, String> map, int row, int col)
  {
//    int midPoint = 46;
    int midPoint = (ScreenCols-col)/2;
    int maxKeySize = midPoint/2;
    int column = col;
    String key;
    int endOfLine;
    
// the maximum "name" length is "maxKeySize"
// from the beginning of the line (supplied col value), if key plus "value" lenght
    //    is greater than "midPoint" then the NVP gets the whole line
    //    otherwise start a new NVP at "midPoint" if the next one will fit.
    // (if doesn't fit, start a new line)
    //  assume num columns 100
    for (Map.Entry<String, String> entry: map.entrySet())
    {
      // check to see if this key/value pair will fit on remainder of line
      endOfLine = column + maxKeySize + entry.getValue().length();
      if((endOfLine > (ScreenCols -(col + 2))) && (column != col))
      {
        column = col;
        row++;
      }
      
      // truncate the name (key) if necessary
      key = entry.getKey().length()< maxKeySize ? entry.getKey() : entry.getKey().substring(0, maxKeySize);
      Toolkit.printString(key + ":",  column, row, BkgndTxtColor);
      column += maxKeySize + col + 1;
      Toolkit.printString(entry.getValue(), column, row, FrgndTxtColor);
      
      // setup the row and column for the next, max two per line
      if((column + entry.getValue().length()) > midPoint)
      {
        column = col;
        row++;
      }
      else
      {
        column = midPoint;
      }
      // increment the row if column is over halfway
    }
    return row;
  }

  
  public boolean paintForeground_9(int screenNum) throws Exception
  {
    // data from the service (may need to pad these)
    int column = 1;
    int row    = 6;
    
    /* keep in order, top down.  See row value increment */

    if(Subnet != null)
    {
      HashMap<String, String> map = convertOtions(Subnet.Options);
      
      row = paintOptionMap(map, row, column);
    }
    else
    {
      logger.warning("The Subnet seems to be null");      
    }
    return true;
  }
  
  private void putBoolean(HashMap<String, String> map, String key, boolean value)
  {
    // put only true
    if((value != false) && (key != null) && (map != null))
      map.put(key, Boolean.toString(value));
  }
  
  private void putShort(HashMap<String, String> map, String key, short value)
  {
    // put only non-zero
    if((value != 0) && (key != null) && (map != null))
      map.put(key, Short.toString(value));
  }
  
  private void putInt(HashMap<String, String> map, String key, int value)
  {
    // put only non-zero
    if((value != 0) && (key != null) && (map != null))
      map.put(key, Integer.toString(value));
  }
  
  private void putLong(HashMap<String, String> map, String key, long value)
  {
    // put only non-zero
    if((value != 0) && (key != null) && (map != null))
      map.put(key, Long.toString(value));
  }
  
  private void putString(HashMap<String, String> map, String key, String value)
  {
    // put only non-null
    if((value != null) && (key != null) && (map != null) && (value.length()>0))
      map.put(key, value);
  }
  
  private HashMap<String, String> convertOtions(SBN_Options o)
  {
    HashMap<String, String> oMap = new HashMap<String, String>();
    
    if(o != null)
    {
      // convert only the true booleans
      putBoolean(oMap, "lmc_esp0", o.lmc_esp0);
      putBoolean(oMap, "reassign_lids", o.reassign_lids);
      putBoolean(oMap, "ignore_other_sm", o.ignore_other_sm);
      putBoolean(oMap, "single_thread", o.single_thread);
      putBoolean(oMap, "disable_multicast", o.disable_multicast);
      putBoolean(oMap, "force_log_flush", o.force_log_flush);
      putBoolean(oMap, "use_mfttop", o.use_mfttop);
      putBoolean(oMap, "force_heavy_sweep", o.force_heavy_sweep);
      putBoolean(oMap, "no_partition_enforcement", o.no_partition_enforcement);
      putBoolean(oMap, "qos", o.qos);
      putBoolean(oMap, "accum_log_file", o.accum_log_file);
      putBoolean(oMap, "port_profile_switch_nodes", o.port_profile_switch_nodes);
      putBoolean(oMap, "sweep_on_trap", o.sweep_on_trap);
      putBoolean(oMap, "use_ucast_cache", o.use_ucast_cache);
      putBoolean(oMap, "connect_roots", o.connect_roots);
      putBoolean(oMap, "sa_db_dump", o.sa_db_dump);
      putBoolean(oMap, "do_mesh_analysis", o.do_mesh_analysis);
      putBoolean(oMap, "exit_on_fatal", o.exit_on_fatal);
      putBoolean(oMap, "honor_guid2lid_file", o.honor_guid2lid_file);
      putBoolean(oMap, "daemon", o.daemon);
      putBoolean(oMap, "sm_inactive", o.sm_inactive);
      putBoolean(oMap, "babbling_port_policy", o.babbling_port_policy);
      putBoolean(oMap, "use_optimized_slvl", o.use_optimized_slvl);
      putBoolean(oMap, "enable_quirks", o.enable_quirks);
      putBoolean(oMap, "no_clients_rereg", o.no_clients_rereg);
      putBoolean(oMap, "perfmgr", o.perfmgr);
      putBoolean(oMap, "perfmgr_redir", o.perfmgr_redir);
      putBoolean(oMap, "consolidate_ipv6_snm_req", o.consolidate_ipv6_snm_req);
      putBoolean(oMap, "m_key_lookup", o.m_key_lookup);
      putBoolean(oMap, "allow_both_pkeys", o.allow_both_pkeys);
      putBoolean(oMap, "port_shifting", o.port_shifting);
      putBoolean(oMap, "remote_guid_sorting", o.remote_guid_sorting);
      putBoolean(oMap, "guid_routing_order_no_scatter", o.guid_routing_order_no_scatter);
      putBoolean(oMap, "drop_event_subscriptions", o.drop_event_subscriptions);
      putBoolean(oMap, "fsync_high_avail_files", o.fsync_high_avail_files);
      putBoolean(oMap, "congestion_control", o.congestion_control);
      putBoolean(oMap, "perfmgr_ignore_cas", o.perfmgr_ignore_cas);
      putBoolean(oMap, "perfmgr_log_errors", o.perfmgr_log_errors);
      putBoolean(oMap, "perfmgr_query_cpi", o.perfmgr_query_cpi);
      putBoolean(oMap, "perfmgr_xmit_wait_log", o.perfmgr_xmit_wait_log);
      
      // convert only the non-zero shorts, ints, etc
      putShort(oMap, "sm_priority", o.sm_priority);
      putShort(oMap, "lmc", o.lmc);
      putShort(oMap, "max_op_vls", o.max_op_vls);
      putShort(oMap, "force_link_speed", o.force_link_speed);
      putShort(oMap, "subnet_timeout", o.subnet_timeout);
      putShort(oMap, "packet_life_time", o.packet_life_time);
      putShort(oMap, "vl_stall_count", o.vl_stall_count);
      putShort(oMap, "leaf_vl_stall_count", o.leaf_vl_stall_count);
      putShort(oMap, "head_of_queue_lifetime", o.head_of_queue_lifetime);
      putShort(oMap, "leaf_head_of_queue_lifetime", o.leaf_head_of_queue_lifetime);
      putShort(oMap, "local_phy_errors_threshold", o.local_phy_errors_threshold);
      putShort(oMap, "overrun_errors_threshold", o.overrun_errors_threshold);
      putShort(oMap, "log_flags", o.log_flags);
      putShort(oMap, "lash_start_vl", o.lash_start_vl);
      putShort(oMap, "sm_sl", o.sm_sl);
      putShort(oMap, "m_key_protect_bits", o.m_key_protect_bits);
      putShort(oMap, "force_link_speed_ext", o.force_link_speed_ext);
      putShort(oMap, "fdr10", o.fdr10);
      putShort(oMap, "sm_assigned_guid", o.sm_assigned_guid);
      putShort(oMap, "cc_sw_cong_setting_threshold", o.cc_sw_cong_setting_threshold);
      putShort(oMap, "cc_sw_cong_setting_packet_size", o.cc_sw_cong_setting_packet_size);
      putShort(oMap, "cc_sw_cong_setting_credit_starvation_threshold", o.cc_sw_cong_setting_credit_starvation_threshold);

      putInt(oMap, "m_key_lease_period", o.m_key_lease_period);
      putInt(oMap, "sweep_interval", o.sweep_interval);
      putInt(oMap, "max_wire_smps", o.max_wire_smps);
      putInt(oMap, "max_wire_smps2", o.max_wire_smps2);
      putInt(oMap, "max_smps_timeout", o.max_smps_timeout);
      putInt(oMap, "transaction_timeout", o.transaction_timeout);
      putInt(oMap, "transaction_retries", o.transaction_retries);
      putInt(oMap, "sminfo_polling_timeout", o.sminfo_polling_timeout);
      putInt(oMap, "polling_retry_number", o.polling_retry_number);
      putInt(oMap, "max_msg_fifo_timeout", o.max_msg_fifo_timeout);
      putInt(oMap, "console_port", o.console_port);
      putInt(oMap, "max_reverse_hops", o.max_reverse_hops);
      putInt(oMap, "perfmgr_sweep_time_s", o.perfmgr_sweep_time_s);
      putInt(oMap, "perfmgr_max_outstanding_queries", o.perfmgr_max_outstanding_queries);
      putInt(oMap, "ca_port", o.ca_port);
      putInt(oMap, "part_enforce_enum", o.part_enforce_enum);
      putInt(oMap, "scatter_ports", o.scatter_ports);
      putInt(oMap, "cc_max_outstanding_mads", o.cc_max_outstanding_mads);
      putInt(oMap, "cc_sw_cong_setting_control_map", o.cc_sw_cong_setting_control_map);
      putInt(oMap, "cc_sw_cong_setting_marking_rate", o.cc_sw_cong_setting_marking_rate);
      putInt(oMap, "cc_ca_cong_setting_port_control", o.cc_ca_cong_setting_port_control);
      putInt(oMap, "cc_ca_cong_setting_control_map", o.cc_ca_cong_setting_control_map);
      putInt(oMap, "perfmgr_rm_nodes", o.perfmgr_rm_nodes);
      putInt(oMap, "perfmgr_xmit_wait_threshold", o.perfmgr_xmit_wait_threshold);
      
      putString(oMap, "guid", new IB_Guid(o.guid).toString());
      putLong(oMap, "m_key", o.m_key);
      putLong(oMap, "sm_key", o.sm_key);
      putLong(oMap, "sa_key", o.sa_key);
      putLong(oMap, "subnet_prefix", o.subnet_prefix);
      putLong(oMap, "log_max_size", o.log_max_size);
      putLong(oMap, "cc_key", o.cc_key);
     
      // convert all strings
      putString(oMap, "config_file", o.config_file);
      putString(oMap, "dump_files_dir", o.dump_files_dir);
      putString(oMap, "log_file", o.log_file);
      putString(oMap, "partition_config_file", o.partition_config_file);
      putString(oMap, "qos_policy_file", o.qos_policy_file);
      putString(oMap, "console", o.console);
      putString(oMap, "port_prof_ignore_file", o.port_prof_ignore_file);
      putString(oMap, "hop_weights_file", o.hop_weights_file);
      putString(oMap, "routing_engine_names", o.routing_engine_names);
      putString(oMap, "lid_matrix_dump_file", o.lid_matrix_dump_file);
      putString(oMap, "lfts_file", o.lfts_file);
      putString(oMap, "root_guid_file", o.root_guid_file);
      putString(oMap, "cn_guid_file", o.cn_guid_file);
      putString(oMap, "io_guid_file", o.io_guid_file);
      putString(oMap, "ids_guid_file", o.ids_guid_file);
      putString(oMap, "guid_routing_order_file", o.guid_routing_order_file);
      putString(oMap, "sa_db_file", o.sa_db_file);
      putString(oMap, "torus_conf_file", o.torus_conf_file);
      putString(oMap, "event_db_dump_file", o.event_db_dump_file);
      putString(oMap, "event_plugin_name", o.event_plugin_name);
      putString(oMap, "event_plugin_options", o.event_plugin_options);
      putString(oMap, "node_name_map_name", o.node_name_map_name);
      putString(oMap, "prefix_routes_file", o.prefix_routes_file);
      putString(oMap, "log_prefix", o.log_prefix);
      putString(oMap, "ca_name", o.ca_name);
      putString(oMap, "force_link_speed_file", o.force_link_speed_file);
      putString(oMap, "part_enforce", o.part_enforce);
      putString(oMap, "port_search_ordering_file", o.port_search_ordering_file);
      putString(oMap, "per_module_logging_file", o.per_module_logging_file);
    }
    else
      logger.severe("The Options appear to be null");
    
    return oMap;
  }
  
  public boolean paintBackground_9(int screenNum) throws Exception
  {
    // static graphics and text
    int column = 1;
    int row    = 4;
    String title = Subnet == null ? "configuration" : Subnet.Options.config_file;
    
    printTitleString("OpenSM Configuration", row++);

    Toolkit.drawHorizontalLine(column, row, 30, ErrorTxtColor);
    Toolkit.printString(title, column+2, row++, BkgndTxtColor);
    
    // see forgreound for remainder
    
    return true;
  }


  
  public boolean paintForeground(int screenNum) throws Exception
  {
    paintForeground_0(screenNum);
    
    switch (screenNum)
    {
      case 1:
        paintForeground_1(screenNum);
        break;
        
      case 2:
        paintForeground_2(screenNum);
        break;
        
      case 3:
        paintForeground_3(screenNum);
        break;
        
      case 4:
        paintForeground_4(screenNum);
        break;
        
      case 5:
        paintForeground_5(screenNum);
        break;
        
      case 6:
        paintForeground_6(screenNum);
        break;
        
      case 7:
        paintForeground_7(screenNum);
        break;
        
      case 8:
        paintForeground_8(screenNum);
        break;
        
      case 9:
        paintForeground_9(screenNum);
        break;
        
        default:
          break;
    }
    
    return true;
  }

  public boolean paintBackground(int screenNum) throws Exception
  {
    // paint the forground and background of this screen
    // also accept input, which invariable changes the screen via
    // the global ScreenNum
    //
    
    paintBackground_0(screenNum);
    
    switch (screenNum)
    {
      case 1:
        paintBackground_1(screenNum);
        break;
        
      case 2:
        paintBackground_2(screenNum);
        break;
        
      case 3:
        paintBackground_3(screenNum);
        break;
        
      case 4:
        paintBackground_4(screenNum);
        break;
        
      case 5:
        paintBackground_5(screenNum);
        break;
        
      case 6:
        paintBackground_6(screenNum);
        break;
        
      case 7:
        paintBackground_7(screenNum);
        break;
        
      case 8:
        paintBackground_8(screenNum);
        break;
        
      case 9:
        paintBackground_9(screenNum);
        break;
        
        default:
          break;
    }
    
    return true;
  }
  
  private String getAbbreviatedType(String s)
  {
    if(s != null)
    {
      if(s.startsWith("S"))
        return OSM_NodeType.SW_NODE.getAbrevName();
      if(s.startsWith("C"))
        return OSM_NodeType.CA_NODE.getAbrevName();
      if(s.startsWith("R"))
        return OSM_NodeType.RT_NODE.getAbrevName();
    }
    return OSM_NodeType.UNKNOWN.getAbrevName();
  }
  
  protected int clearRow(int row)
  {
    int startCol = 1;
    int endCol   = ScreenCols-1;
    int n = endCol - startCol;
    String pad = String.format("%1$-" + n + "s", " ");
     
    Toolkit.printString(pad, startCol, row++, FrgndTxtColor);

    return row;
  }

  protected int paintPortProblem(int column, int row, String type, IB_Port[] pArray)
  {
    // show the ports with problems
    if((pArray != null) && (pArray.length > 0))
    {
      // collect all these ports into bins, organized by port guids
      BinList <IB_Port> pbL = new BinList <IB_Port>();
      for(IB_Port p: pArray)
      {
        pbL.add(p, p.guid.toColonString());
      }
      
      // there should be at least one bin
      int n = pbL.size();
      for(int j = 0; j < n; j++)
      {
        ArrayList<IB_Port> pL = pbL.getBin(j);
        IB_Port p0 = pL.get(0);
        String pDesc = ("guid=" + p0.guid + " desc=" + p0.Description);
        StringBuffer sbuff = new StringBuffer();
        for(IB_Port p: pL)
          sbuff.append(p.portNumber + ", ");
        
        // strip off the trailing 
        sbuff.setLength((sbuff.length()-2));
        String pNum  = sbuff.toString();

        // clear the line
        clearRow(row);
        Toolkit.printString(getAbbreviatedType(type) + "--" + pDesc, column, row++, FrgndTxtColor);
        // clear the line
        clearRow(row);
        Toolkit.printString("port(s)=" + pNum, column+4, row++, FrgndTxtColor);
      }
    }
    return row;
  }
  

  protected int paintPortErrors(int column, int row, ArrayList<PFM_Port> pmpa)
  {
    int NUM_TOP_ERRORS = 5;
    long total_top_errors = 0L;
    
    // show the top ports with with errors, organized by node
    if((pmpa != null) && (pmpa.size() > 0))
    {
      // collect all the ports with errors into bins, organized by port guids
      // organize the ports by guid
      BinList <PFM_Port> pbL  = new BinList <PFM_Port>();
      BinList <PFM_Port> tpbL = new BinList <PFM_Port>();
      
      for(PFM_Port p: pmpa)
      {
        /* if this port has an error, add it */
        if(p.hasError())
        {
          pbL.add(p, p.getNodeGuid().toColonString());
        }
      }
      
      // there should be at least one bin, determine a bin error count
      int n = pbL.size();
      int[] errorArray = new int[n];
      int[] used_ndex  = new int[n];
      int num_added = 0;
      
      for(int j = 0; j < n; j++)
      {
        for(PFM_Port pp: pbL.getBin(j))
        {
          errorArray[j] += pp.getTotalErrors();
        }
      }
      
      // now sort this errorArray, worst to best
      int [] sortedArray = Arrays.copyOf(errorArray, errorArray.length);
      Arrays.sort(sortedArray);
      
      // construct a new BinList, sorted by total errors, and limited in size to NUM_TOP_ERRORS
      for(int j = n-1; (j >= 0) && (num_added < NUM_TOP_ERRORS); j--)
      {
        // get the bin that matches the Reversed sorted error counts
        for(int ndex = 0; ndex < n; ndex++)
        {
          if(sortedArray[j] == errorArray[ndex])
          {
            // found a match using ndex, check to see if we have found this already
            //  is ndex in the used_ndex array?  if so break;
            boolean skip = false;
            for(int k = 0; k < num_added; k++)
              if(ndex == used_ndex[k])
              {
                skip = true;
                break;
              }
            if(skip)
              break;
            
            tpbL.addBin(pbL.getBin(ndex));
            total_top_errors += sortedArray[j];
            
            // mark this ndex, or Bin, used, so we don't add it again
            used_ndex[num_added] = ndex;
            num_added++;
            break;
          }
        }
      }
      
      // print out only the top errors, or until I run out of lines
      
      int c = 14;
      String tte = Long.toString(total_top_errors);
      Toolkit.printString("(", c, row-1, BkgndTxtColor);
      Toolkit.printString(tte, c+1, row-1, FrgndTxtColor);
      Toolkit.printString(")", c+1+tte.length(), row-1, BkgndTxtColor);

      int numErrorBins = tpbL.size();
      String ErrorString = null;
      String PortDescString = null;
      int maxStringLength = ScreenCols - (column+4);
      int maxNumLines = ScreenRows - (row+3);
      int numLines = 0;
      
      for(int j = 0; (j < numErrorBins) && (numLines < maxNumLines); j++)
      {
        ArrayList<PFM_Port> pL = tpbL.getBin(j);
        PortDescString = PFM_Port.getPortDescription(pL);
        Toolkit.printString(PortDescString, column, row++, FrgndTxtColor);
        numLines++;
        for (PortCounterName counter : PortCounterName.PFM_ERROR_COUNTERS)
        {
          // error_name (num): port_num=value, port_num = value, ...
          StringBuffer sbuff = new StringBuffer();
            int num_errs = 0;
            for(PFM_Port p : pL)
              if(p.getCounter(counter) != 0L)
                num_errs++;

            // skip this error, if all ports are zero
            if(num_errs > 0)
            {
            boolean newError = true;
            for(PFM_Port p : pL)
            {
              if(p.getCounter(counter) != 0L)
              {
                // if this is the first one, include the name
                if(newError)
                {
                  sbuff.append(counter.name() + "(" + num_errs + "): " + p.port_num + "=" + p.getCounter(counter));
                  newError = false;
                }
                else
                {
                  sbuff.append(", " + p.port_num + "=" + p.getCounter(counter));
                }
              }
            }
            }
            ErrorString = sbuff.toString();
        // don't exceed ScreenCols in length or go out of the box
        if((ErrorString != null) && (ErrorString.length() > 0) && (numLines < maxNumLines) && (row < (ScreenRows - 3)))
        {
          numLines++;
          if(ErrorString.length() > maxStringLength)
            Toolkit.printString(ErrorString.substring(0, maxStringLength-4) + "...", column+4, row++, FrgndTxtColor);
          else
            Toolkit.printString(ErrorString, column+4, row++, FrgndTxtColor);
        }
        }
      }
    }
    return row;
  }
  
  protected synchronized boolean getApiData(int screenNum) throws Exception
  {
    boolean success = true;
    if ((ParentSession != null) && (ParentSession.isConnected()))
    {
      ParentSessionStatus = ParentSession.getSessionStatus();

      if (clientInterface != null)
      {
        AllNodes = clientInterface.getOsmNodes();
        SysInfo = clientInterface.getOsmSysInfo();
        AllPorts = clientInterface.getOsmPorts();
        Stats = clientInterface.getOsmStats();
        Subnet = clientInterface.getOsmSubnet();
      }
      else
      {
        success = false;
        logger.severe("Could not get ClientInterface data");
      }

      if (adminInterface != null)
      {
        RemoteServerStatus = adminInterface.getServerStatus();
      }
      else
      {
        success = false;
        logger.severe("Could not get AdminInterface data");
      }

      if (eventInterface != null)
      {
        EventStats = eventInterface.getOsmEventStats();
      }
      else
      {
        success = false;
        logger.severe("Could not get EventInterface data");
      }

    }
    else
    {
      success = false;
      logger.severe("Could not get ParenSession data");
    }
    return success;
  }

    public boolean getInput(int screenNum) throws Exception
    {
      // paint the forground and background of this screen
      // also accept input, which invariable changes the screen via
      // the global ScreenNum
      //
      // Some obvious keys don't seem to be mapped
//      Esc = 27
//      Tab = 9
//      Rtn = 10

      if(ParentSession != null)
      {
        // if I can't get the api data, I probably lost the connection, so quit
        if(! getApiData(screenNum))
        {
          destroy("Cant get the api data, closed connection?");
          return false;
        }
      }
      else
        logger.severe("Could not get the Session Data");
    
      // this blocks, so sometimes key must be struck before app will close
      InputChar c = Toolkit.readCharacter();
    
    // check to see if it was one of the special keys
    if(c.isSpecialCode() || c.getCode()==27 || c.getCode()==9 || c.getCode()==10)
    {
      // handle esc, tab, enter, and function keys
      logger.severe("The special character was: " + c.getCode());
      if(c.getCode()==InputChar.KEY_END || c.getCode()==27)
        destroy("");
      
      if(c.getCode()==InputChar.KEY_F4)
        changeScreen(3);   

      if(c.getCode()==InputChar.KEY_F3)
        changeScreen(6);   

      if(c.getCode()==InputChar.KEY_F2 || c.getCode()==InputChar.KEY_HOME)
        changeScreen(1);   

      if(c.getCode()==InputChar.KEY_F5)
        changeScreen(2);   

      if(c.getCode()==InputChar.KEY_F6)
        changeScreen(7);   

      if(c.getCode()==InputChar.KEY_F7)
        changeScreen(4);   

      if(c.getCode()==InputChar.KEY_F9)
        changeScreen(5);   

      if(c.getCode()==InputChar.KEY_F8)
        changeScreen(9);   

    }
    else
    {
      // one of the normal keys
    }
    return true;
  }

    public boolean paintScreen(int screenNum) throws Exception
    {
      // paint the forground and background of this screen
      // also accept input, which invariable changes the screen via
      // the global ScreenNum
      //
      paintBackground(screenNum);
      paintForeground(screenNum);
      getInput(screenNum);
      
      /* TODO remove this dwell */
      TimeUnit.MILLISECONDS.sleep(99);

      return true;
    }

    boolean changeScreen(int screenNum)
    {
      // changes the current screen number, and clears the screen for painting
      ScreenNum = screenNum;
      Toolkit.clearScreen(Scrn0BTxtColor);
      
      return true;
    }

 private boolean test_events() throws Exception
 {
   boolean done = false;
   int loop_counter = 0;
   
   if(withConnection)
   {
   try
     {
     // dwell here a second to allow the ParentSession to get established
     // since this is user dependent operation, loop
     
     /* TODO remove this dwell */
     TimeUnit.MILLISECONDS.sleep(99);
     while(!done && ((ParentSession == null) || !ParentSession.isAuthenticated()))
     {
       TimeUnit.MILLISECONDS.sleep(99);
       loop_counter++;
       if(loop_counter > 300)
         done = true;
     }

     
       if((ParentSession != null) && ParentSession.isAuthenticated())
       {
         TimeUnit.MILLISECONDS.sleep(999);
         logger.severe("Getting the EventApi now");
         eventInterface   = ParentSession.getEventApi();
       }
       else
       {
         logger.severe("Could not authenticate, or ParentSession not established");
         return false;
       }
     }
     catch (Exception e)
     {
       System.err.println("Problem getting the EventApi");
       logger.severe(e.getStackTrace().toString());
       return false;
     }
   }
 if(eventInterface != null)
   eventInterface.addListener(this);
 else
   logger.severe("EventInterface was null, could not add myself to listen for events");
 
 logger.info("All done setting up events for testing");

   return false;
 }
    
    /************************************************************
   * Method Name:
   *  interactive
  **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects
  
   * @param line
   * @return false if could not establish a service connection
   * @throws Exception
   ***********************************************************/
    private boolean interactive() throws Exception
    {
      if(withConnection)
      {
      try
        {
          ParentSession = openParentSession(cmd_line);
          if((ParentSession != null) && ParentSession.isAuthenticated())
          {
            clientInterface  = ParentSession.getClientApi();
            adminInterface   = ParentSession.getAdminApi();
            eventInterface   = ParentSession.getEventApi();
          }
          else
          {
            System.err.println("Could not authenticate");
            logger.severe("Could not authenticate");
            return false;
          }
            
        }
        catch (Exception e)
        {
          System.err.println("Could not establish connection (see log file for details)");
          logger.severe(e.getStackTrace().toString());
          return false;
        }
      }
//    if(eventInterface != null)
//      eventInterface.addListener(this);
    
    Toolkit.init();
    Scrn0BTxtColor =  new CharColor(CharColor.WHITE, CharColor.BLACK, CharColor.NORMAL, CharColor.NORMAL);
    Scrn0FTxtColor =  new CharColor(CharColor.WHITE, CharColor.BLACK, CharColor.BOLD, CharColor.BOLD);
    BkgndTxtColor  =  new CharColor(CharColor.WHITE, CharColor.BLACK, CharColor.NORMAL, CharColor.NORMAL);
    ScrnTitleColor  =  new CharColor(CharColor.WHITE, CharColor.BLUE, CharColor.BOLD, CharColor.BOLD);
    FrgndTxtColor  =  new CharColor(CharColor.WHITE, CharColor.BLUE, CharColor.NORMAL, CharColor.NORMAL);
    ErrorTxtColor  =  new CharColor(CharColor.WHITE, CharColor.RED, CharColor.NORMAL, CharColor.NORMAL);

    // go to the initial screen
    changeScreen(1);
    
    Tserv.addListener(this);

    while(Continue_Thread && ParentSession != null)
      try
      {
        paintScreen(ScreenNum);  // blocks on readCharacter
      }
      catch (Exception e)
      {
        e.printStackTrace();
        logger.severe("Unexpected Exception: "+ e.getMessage());
        
        // this should break me out of this loop
        destroy(e.getMessage());
      }
    
//    if(eventInterface != null)
//      eventInterface.removeListener(this);
    
    
    /* all done, so close the session(s) */
    try
    {
      OsmService.closeSession(ParentSession);
    }
    catch (Exception e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return true;     
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
  public static void main(String[] args)
  {
    OsmConsole osmCon = OsmConsole.getInstance();
    
    Options options = new Options();
    OptionGroup optionGroup = new OptionGroup( );
    CommandLine line = null;
    
    Option help = new Option( "?", "help", false, "print this message" );
    Option version = new Option( "v", "version", false, "print the version information and exit" );
    Option inter_active = new Option( "i", "interactive", false, "open an interactive OsmConsole session (default)" );
    Option non_inter = new Option( "s", "static", false, "open a static OsmConsole, without a session" );
    
    Option host_name   = OptionBuilder.hasArg(true).withArgName( "host url" ).withValueSeparator('=').withDescription(  "the host name of the OSM Monitoring Service" ).withLongOpt("host").create( "h" );

    Option port_num   = OptionBuilder.hasArg(true).withArgName( "port #" ).withValueSeparator('=').withDescription(  "the port number of the service" ).withLongOpt("port").create( "p" );
    Option refresh = OptionBuilder.hasArg(true).withArgName( "# secs" ).withValueSeparator('=').withDescription(  "the console refresh period in seconds" ).withLongOpt("refresh").create( "r" );

    optionGroup.addOption(inter_active);
    optionGroup.addOption(non_inter);
    
    options.addOption( host_name );
    options.addOption( port_num );
    options.addOption( help );
    options.addOption( version );
    options.addOption( refresh );
    options.addOptionGroup(optionGroup);
    
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
    
    
    osmCon.cmd_line = line;
    
    if(line.hasOption("i"))
    {
      osmCon.withConnection = true;
      osmCon.create();
//      osmCon.test_events();
    }

      if(line.hasOption("static"))
        try
        {
          osmCon.withConnection = false;
          osmCon.create();
        }
        catch (Exception e)
        {
          // TODO: try to determine what happened, may have to restore the screen
          e.printStackTrace();
        }

        if(line.hasOption("refresh"))
        {
          logger.info("A refresh period was supplied");
          String rString = line.getOptionValue("refresh");
          int rVal = new Integer(rString);
          refreshPeriod = (rVal > 4) && (rVal < 30) ? rVal: refreshPeriod;
        }

    if(line.hasOption("help"))
    {
      printUsage(options);
    }
    
    logger.severe("Exiting main() thread now.");
  }
  
  private static void printUsage(Options options) 
  {
    HelpFormatter helpFormatter = new HelpFormatter( );
    helpFormatter.setWidth( 80 );
    helpFormatter.printHelp( USAGE, HEADER, options, FOOTER );
  }

  /************************************************************
   * Method Name:
   *  timeUpdate
  **/
  /**
   * Describe the method here
   *
   * @see gov.llnl.lc.time.TimeListener#timeUpdate(gov.llnl.lc.time.TimeStamp)
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @param arg0
   ***********************************************************/
  
  @Override
  public synchronized void timeUpdate(TimeStamp arg0)
  {
    // this should get called once per second
    timeLoopCounter++;
  
    CurrentTime = arg0;
    try
    {
      // do periodic work
      if(timeLoopCounter%refreshPeriod == 0)
      {
        // get fresh data from the interfaces
        if(ParentSession != null)
        {
          if(!getApiData(ScreenNum))
          {
            destroy("can't get the api data within the timeUpdate");
            return;
          }
        }
      }
      // update the current foreground
      paintForeground(ScreenNum);
    }
    catch (Exception e)
    {
//      e.printStackTrace();
      logger.severe("Unexpected Exception in timeUpdate: "+ e.getMessage());
//      destroy(e.getMessage());
    }    
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
    return OsmEvent.OSM_ALL_EVENTS;
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
   * @param arg0
   ***********************************************************/
  
  @Override
  public synchronized void osmEventUpdate(OsmEvent osmEvent)
  {
    logger.info("osmEventUpdate: (" + eventCounter++ + ") ["+ osmEvent + "]");
  }

  /**************************************************************************
   *** Method Name:
   ***     getInstance
   **/
   /**
   *** Get the singleton OsmConsole. This can be used if the application wants
   *** to share one manager across the whole JVM.  Currently I am not sure
   *** how this ought to be used.
   *** <p>
   ***
   *** @return       the GLOBAL (or shared) OsmConsole
   **************************************************************************/

   public static OsmConsole getInstance()
   {
     synchronized( OsmConsole.semaphore )
     {
       if ( Osm_Console == null )
       {
         Osm_Console = new OsmConsole( );
       }
       return Osm_Console;
     }
   }
   /*-----------------------------------------------------------------------*/

   public Object clone() throws CloneNotSupportedException
   {
     throw new CloneNotSupportedException(); 
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
    Thread_Running = true;
    
    try
    {
      interactive();
    }
    catch (Exception e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    Thread_Running = false;
    return;  
  }

}
