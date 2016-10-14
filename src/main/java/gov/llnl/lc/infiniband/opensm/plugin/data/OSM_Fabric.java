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
 *        file: OSM_Fabric.java
 *
 *  Created on: Feb 28, 2013
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import gov.llnl.lc.infiniband.core.IB_Address;
import gov.llnl.lc.infiniband.core.IB_Guid;
import gov.llnl.lc.infiniband.core.IB_Link;
import gov.llnl.lc.infiniband.opensm.plugin.event.OSM_EventStats;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmAdminApi;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmClientApi;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmEventApi;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmServerStatus;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmServiceManager;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmSession;
import gov.llnl.lc.infiniband.opensm.xml.IB_FabricConf;
import gov.llnl.lc.infiniband.opensm.xml.IB_LinkListElement;
import gov.llnl.lc.infiniband.opensm.xml.IB_PortElement;
import gov.llnl.lc.time.TimeStamp;
import gov.llnl.lc.util.BinList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

/**********************************************************************
 * The OSM_Fabric object represents a snapshot of all of the "fabric"
 * information that can be obtained through the OpenSM Monitoring Service.
 * 
 * Much of this information is "derived" from the primitive types, using
 * standard convenience functions.  The purpose of creating this single
 * "fabric" object is three-fold.
 *   1.  Atomic data:  All of the information contained within this
 *                     composite object can be considered temporally
 *                     related.  A snapshot or collection of all the
 *                     primitive objects were obtained at the same time
 *                     and then used to derive the rest of the information.
 *   2.  Performance:  Some of the "derived" objects require a noticeable
 *                     amount of time to construct.  This work can be
 *                     performed in the background, and new instances of
 *                     the "fabric" are made available when ready.
 *                     This means that instances of an OSM_Fabric are
 *                     copies of cached results, so are always stale by
 *                     a few seconds.
 *   3.  Convenience:  The primitive types mirror the data structures
 *                     within OpenSM, and are often difficult to use without
 *                     convenience functions.  The "fabric" contains the
 *                     results produced by the most common convenience
 *                     functions, thereby providing a more useful interface.
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Feb 28, 2013 8:12:24 AM
 **********************************************************************/
/**********************************************************************
 * Describe purpose and responsibility of OSM_Fabric
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Jun 7, 2016 3:21:12 PM
 **********************************************************************/
public class OSM_Fabric implements Serializable, gov.llnl.lc.logging.CommonLogger
{
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = 7197173847133741916L;

  private boolean     Initialized = false;
  
  private long stalePeriod = 1200;  // the default is 20 minutes
  
  // the creation or acquisition time
  private TimeStamp   timeStamp;
  
  // the primitive data types
  private OSM_Nodes      osmNodes;
  private OSM_Ports      osmPorts;
  private OSM_Stats      osmStats;
  private OSM_Subnet     osmSubnet;
  private OSM_SysInfo    osmSysInfo;
  private OSM_EventStats osmEventStats;
  
  //  // the derived data types
  private String FabricName;
//
  /* keyed off node guid */
private LinkedHashMap<String, OSM_Node> nodesAll = new LinkedHashMap<String, OSM_Node>();

/* keyed off port guid+port_num */
private LinkedHashMap<String, OSM_Port> portsAll  = new LinkedHashMap<String, OSM_Port>();

/* keyed off port guid+port_num of endpoint 1 */
private LinkedHashMap<String, IB_Link>  linksAll  = new LinkedHashMap<String, IB_Link>();

/* keyed off sys guid (core switches contain system guids) */
private BinList <IB_Guid> systemGuidBins = new BinList <IB_Guid>();

/* the primary subnet manager */
private OSM_Node ManagementNode;


  /************************************************************
   * Method Name:
   *  OSM_Fabric
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param osmNodes
   * @param osmPorts
   * @param osmStats
   * @param osmSubnet
   * @param osmSysInfo
   ***********************************************************/
  public OSM_Fabric(String name, OSM_Nodes osmNodes, OSM_Ports osmPorts, OSM_Stats osmStats, OSM_Subnet osmSubnet, OSM_SysInfo osmSysInfo, OSM_EventStats osmEstats)
  {
    super();
    init(name, osmNodes, osmPorts, osmStats, osmSubnet, osmSysInfo, osmEstats);
  }
  /************************************************************
   * Method Name:
   *  OSM_Fabric
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   ***********************************************************/
  public OSM_Fabric()
  {
    super();
  }
  
  public static OSM_Fabric getOSM_Fabric(String hostName, String portNumber)
  {
    // establish a connection
    logger.info("OSM_F: Opening the OMS Session");
    OsmSession ParentSession = null;

    /* the one and only OsmServiceManager */
    OsmServiceManager OsmService = OsmServiceManager.getInstance();

    try
    {
      ParentSession = OsmService.openSession(hostName, portNumber, null, null);
    }
    catch (Exception e)
    {
      logger.severe(e.getStackTrace().toString());
      System.exit(-1);
    }
    OSM_Fabric fabric = null;

    if (ParentSession != null)
    {
      logger.info("Getting the OMS Interfaces");
      OsmClientApi clientInterface = ParentSession.getClientApi();
      OsmAdminApi adminInterface = ParentSession.getAdminApi();
      OsmEventApi eventInterface = ParentSession.getEventApi();
      
      try
      {
        logger.info("Getting raw data from the OMS Interfaces");
        String name                        = adminInterface.getServerStatus().Server.getHost();
        OsmServerStatus RemoteServerStatus = adminInterface.getServerStatus();
        
        OSM_Nodes osmNodes           = clientInterface.getOsmNodes();
        OSM_Ports osmPorts           = clientInterface.getOsmPorts();
        OSM_Stats osmStats           = clientInterface.getOsmStats();
        OSM_Subnet osmSubnet         = clientInterface.getOsmSubnet();
        OSM_SysInfo osmSysInfo       = clientInterface.getOsmSysInfo();
        OSM_EventStats osmEventStats = eventInterface.getOsmEventStats();
        
        logger.info("Constructing the Fabric now");
        fabric = new OSM_Fabric(name, osmNodes, osmPorts, osmStats, osmSubnet, osmSysInfo, osmEventStats);
        logger.info("Done constructing the Fabric");
        
        OsmService.closeSession(ParentSession);
       }
      catch (Exception e)
      {
        logger.severe("Could not construct the OSM_Fabric object, check the constructor, and initializer??");
      }
    }
    else
    {
      logger.severe("Could not establish an OMS session");
    }
    return fabric;
  }

  
  public void init(String name, OSM_Nodes osmNodes, OSM_Ports osmPorts, OSM_Stats osmStats, OSM_Subnet osmSubnet, OSM_SysInfo osmSysInfo, OSM_EventStats osmEstats)
  {
    this.FabricName    = name;
    this.osmNodes      = osmNodes;
    this.osmPorts      = osmPorts;
    this.osmStats      = osmStats;
    this.osmSubnet     = osmSubnet;
    this.osmSysInfo    = osmSysInfo;
    this.osmEventStats = osmEstats;
    // give it a temporary time, replace from counter timestamp below
    this.timeStamp  = new TimeStamp();
    
    this.Initialized = updateDerivedData();
    logger.info("done initializing the fabric");
    if(timeStamp == null)
      logger.severe("  Could not obtain a timestamp for this fabric");
  }
  
  protected boolean updateDerivedData()
  {
    boolean status = true;
    // assume this is called only AFTER all the member objects exist
    status &= createOSM_Nodes();
    if (!status)
      logger.severe("error creating nodes");
    status &= createOSM_Ports();
    if (!status)
      logger.severe("error creating ports");
    try
    {
      status &= createIB_Links();
      if (!status)
        logger.severe("error creating links");
      logger.info("done creating links, moving on");
    }
    catch (Exception e)
    {
      logger.severe(e.getMessage());
      logger.severe("createIB_Links() threw an exception, I think its a null pointer");
      logger.severe("The size of the links is " + getIB_Links().size());
    }
    
    // create "system guid bins", by looking through all the switch nodes
    // and organizing them based on a common sys_guid
    status &= createSystemGuidBins(false);
    if (!status)
        logger.severe("error creating system guid bins");
      logger.info("done creating system guid bins, moving on");
    

    // ***** IMPORTANT TIMESTAMP is based on perfmgr *****
    // conditionally update the timestamp based on the performance counters
    if ((osmPorts != null) && (osmPorts.PerfMgrPorts != null) && (osmPorts.PerfMgrPorts.length > 4))
    {
      // if this is radically different than the current timestamp, raise a flag
      // (The PerfMgr contains a list of ports that may or may not be updating)
      // (but when the update occurs, they all get the same timestamp)
      // TODO - sanity check make sure all timestamps are the same

      // this should never be more than 20 mins old
//      TimeStamp ts = osmPorts.PerfMgrPorts[2].getErrorTimeStamp();
      TimeStamp ts = getPFM_TimeStamp();
      TimeStamp ots = getPFM_TimeStamp();
      ots.addMinutes(20);
      if (ots.before(timeStamp))
      {
        logger.severe("The PerfMgrPort data seems to be very stale, the timestamp is: " + ts);
      }
      // use the ts from the error counters
      timeStamp = ts;
    }
    else
    {
      logger.severe("The PerfMgrPorts seem to be missing, suspect sweeping is disabled?");
      timeStamp = null;
    }

    if ((osmSubnet != null) && (osmSubnet.Options != null))
      stalePeriod = osmSubnet.Options.perfmgr_sweep_time_s;

    return status;
  }

  /************************************************************
   * Method Name:
   *  isInitialized
   **/
  /**
   * Returns the value of initialized
   *
   * @return the initialized
   *
   ***********************************************************/
  
  public boolean isInitialized()
  {
    return Initialized;
  }
  
  public boolean checkFabricStructure(IB_FabricConf config, boolean includeDownedPorts)
  {
    return checkNodeStructure(config, null, includeDownedPorts);
  }
  
  
  public boolean checkNodeStructure(IB_FabricConf config, OSM_Node node, boolean includeDownedPorts)
  {
    LinkedHashMap<String, IB_Link> linkMap = getIB_Links();
    boolean status = false;

    // the fabric conf is supposed to be the reference, so report differences
    // between it and actual
    if(node != null)
      System.out.println("Evaluating the Node connectivity...");
    else
      System.out.println("Evaluating the Fabric connectivity...");
    
    boolean nodeFound = false;
      
    for (IB_LinkListElement lle : config.getNodeElements())
    {
      String nodeName = lle.getName(); // name of the node
      
      // if node is null, then check ALL nodes, otherwise limit the test to the one provided
      if((node != null) && !(nodeName.equalsIgnoreCase(node.sbnNode.description)) && !(nodeName.equalsIgnoreCase(node.pfmNode.getNode_name())))
          continue;  // skip the check, not the node we are interested in

      // if here, do a check on the Node (aka linkedlistelement)
      nodeFound = true;
      boolean foundIt = false;
      for (IB_PortElement pe : lle.getPortElements())
      {
        String portNumber = pe.getNumber(); // this port number
        String rNodeName = pe.getIB_RemoteNodeElement().getName();
        String rPortNumber = pe.getIB_RemotePortElement().getNumber();
        short pNum = Short.parseShort(portNumber);
        short rPNum = Short.parseShort(rPortNumber);

        // attempt to find this "ideal" link in the link map (keyed off guid +
        // portnum
        
        IB_Guid lGuid = getPortGuidFromName(nodeName, pNum);
        IB_Guid rGuid = getPortGuidFromName(rNodeName, rPNum);
        
        long lguid = 0L;
        long rguid = 0L;
        if (lGuid != null)
          lguid = lGuid.getGuid();
        if (rGuid != null)
          rguid = rGuid.getGuid();
        
        String llKey = OSM_Port.getOSM_PortKey(lguid, pNum);
        String rlKey = OSM_Port.getOSM_PortKey(rguid, rPNum);

        IB_Link llink = linkMap.get(llKey);
        IB_Link rlink = linkMap.get(rlKey);
        
        if (llink != null)
        {
          /*
           * the lLink.Endpoint1 matches the ideal Link, endpoint1, so make sure
           * lLink.Endopoint2 matches endpoint2
           */
          String eKey = OSM_Port.getOSM_PortKey(llink.Endpoint2.getNodeGuid().getGuid(),
              (short) llink.Endpoint2.getPortNumber());
          if (rlKey.equals(eKey))
            foundIt = true;
        }

        if (rlink != null)
        {
          /*
           * the rLink.Endpoint1 matches the ideal Link, endpoint2, so make sure
           * rLink.Endopoint2 matches endpoint1
           */
          String eKey = OSM_Port.getOSM_PortKey(rlink.Endpoint2.getNodeGuid().getGuid(),
              (short) rlink.Endpoint2.getPortNumber());
          if (llKey.equals(eKey))
            foundIt = true;
        }

        // show the problems (not founds)
        if (((llKey == null) && (rlKey == null)) || !foundIt)
        {
          // is either side of this link down?
          OSM_Port p = getOSM_Port(getOSM_PortKey(lguid, pNum));
          if (p != null)
            if (p.isActive())
            {
              // invalid
              String ivl = getLinkDescription(lguid, pNum, linkMap);
              String ils = config.getLinkDescription(nodeName, portNumber, false);
              System.out.println("");
              System.out.println("ERR: invalid link : " + ivl);
              System.out.println("     Should be    : " + ils);
            }
            else if (includeDownedPorts)
            {
              // down
              String ils = config.getLinkDescription(nodeName, portNumber, false);
              System.out.println("");
              System.out.println("ERR: port down: " + ils);
            }

          p = getOSM_Port(getOSM_PortKey(rguid, rPNum));
          if (p != null)
            if (p.isActive())
            {
              // invalid
              String ivl = getLinkDescription(rguid, rPNum, linkMap);
              String ils = config.getLinkDescription(nodeName, portNumber, false);
              System.out.println("");
              System.out.println("ERR: invalid link : " + ivl);
              System.out.println("     Should be    : " + ils);

            }
            else if (includeDownedPorts)
            {
              // down
              String ils = config.getLinkDescription(nodeName, portNumber, false);
              System.out.println("");
              System.out.println("ERR: port down: " + ils);
            }
        }
        if(nodeFound && foundIt)
          status = true;
        foundIt = false;
      }
    }
    if(!nodeFound)
      System.err.println("Could not find a matching node to compare: " + node.sbnNode.description + ", " + node.getNodeGuid().toColonString());
    return status;
  }
  
  /************************************************************
   * Method Name:
   *  getAge
  **/
  /**
   * Returns the number of time units (specified) difference from
   * this timestamp to the current time.  How old is the data,or what
   * is its age?
   * 
   * If the age can't be calculated (no timestamp?), then return 666L
   *
   * @see     #isStale()
   *
   * @param unitOfTime
   * @return
   ***********************************************************/
  public long getAge(TimeUnit unitOfTime)
  {
    if(timeStamp == null)
      return 666L;  // return 666 (to avoid division by zero errors) if not available
    long age = new TimeStamp().getTimeInSeconds() - timeStamp.getTimeInSeconds();
    if(unitOfTime != null)
      return unitOfTime.convert(age, TimeUnit.SECONDS);
    return age;
  }
  
  public int getPerfMgrSweepSecs()
  {
    if((osmSysInfo != null) && (osmSysInfo.PM_SweepTime > 2))
    {
      return osmSysInfo.PM_SweepTime;
    }
    
    // if the sweep time is not in the SysInfo, get it from the options map

    int sweepPeriod = 180;
    String defaultSweepString = "180";
    String sweepSecsString = defaultSweepString;
    String PerfSweepSecs = "perfmgr_sweep_time_s";
    
    LinkedHashMap<String, String> OptionsMap = getOptions();
    if (OptionsMap != null)
    {
      sweepSecsString = OptionsMap.get(PerfSweepSecs);
      if (sweepSecsString == null)
      {
        sweepSecsString = defaultSweepString;
        logger.warning("Using the default sweep period of " + sweepPeriod + " secs");
      }
      sweepPeriod = Integer.parseInt(sweepSecsString);
    }
    return sweepPeriod;
  }
  
  /************************************************************
   * Method Name:
   *  getStalePeriod
   **/
  /**
   * Returns the value of stalePeriod
   *
   * @return the stalePeriod
   *
   ***********************************************************/
  
  public long getStalePeriod()
  {
    return stalePeriod;
  }
  /************************************************************
   * Method Name:
   *  setStalePeriod
   **/
  /**
   * Sets the value of stalePeriod
   *
   * @param stalePeriod the stalePeriod to set
   *
   ***********************************************************/
  public void setStalePeriod(long stalePeriod)
  {
    this.stalePeriod = stalePeriod;
  }
  /************************************************************
   * Method Name:
   *  isStale
   **/
  /**
   * Returns true if the Fabrics timestamp is older (elapsed seconds)
   * than the stale period.  The stale period (if not directly set)
   * is normally the Perfmanagers update rate.
   *
   * @return the stale
   *
   ***********************************************************/
  
  public boolean isStale()
  {
    // calculate this, based on age
    if(getAge(TimeUnit.SECONDS) > this.stalePeriod)
      return true;

    return false;
  }
  
  public boolean isSwitchGuid(IB_Guid g)
  {
    OSM_Node n = getOSM_Node(g);
    return (n == null) ? false: n.isSwitch();
  }

  public boolean isUniquePortGuid(IB_Guid g)
  {
    // is this guid a port guid, which is different from its
    // parent node guid?
    if((g == null) || (isSwitchGuid(g)))
      return false;
    
    // loop through all ports to see if this exists
    SBN_Port [] SubnPorts = osmPorts.getSubnPorts();
    if((SubnPorts != null) && (SubnPorts.length > 0))
    {
      for(SBN_Port p: SubnPorts)
      {
         if(p.port_guid == g.getGuid())
         {
           // the guid matches the port guid, but does
           // the port guid match the node guid?
           if(p.port_guid != p.node_guid)
             return true;
         }
      }
    }
    return false;
   }

  public IB_Guid getParentGuid(IB_Guid portGuid)
  {
    if((portGuid == null) || (isSwitchGuid(portGuid)))
      return null;
    
    // loop through all ports to see if this exists
    // return the node-guid if so, otherwise null
    SBN_Port [] SubnPorts = osmPorts.getSubnPorts();
    if((SubnPorts != null) && (SubnPorts.length > 0))
    {
      for(SBN_Port p: SubnPorts)
      {
        if(p.port_guid == portGuid.getGuid())
          return new IB_Guid(p.node_guid);
      }
    }
    return null;
   }

  /************************************************************
   * Method Name:
   *  getFabricName
   **/
  /**
   * Returns the value of fabricName
   *
   * @return the fabricName
   *
   ***********************************************************/
  
  public String getFabricName()
  {
    return getFabricName(false);
  }
  
  public String getFabricName(boolean trim)
  {
       if(!trim || FabricName == null)
        return FabricName;
      
      // trim domain (no dots)
      int n = FabricName.indexOf('.');
      if(n < 1)
        return FabricName;
      return FabricName.substring(0, n);
  }


  /************************************************************
   * Method Name:
   *  getTimeStamp
   **/
  /**
   * Returns the value of timeStamp, based on the error counter ts.
   *
   * @return the timeStamp
   *
   ***********************************************************/
  
  public TimeStamp getTimeStamp()
  {
    // ideally, this is the same as PFM_TimeStamp which
    // is determined  in the updateDerivedData() method
    return timeStamp;
  }
  
  public TimeStamp getPFM_TimeStamp()
  {
    return getPFM_TimeStamp(this);
   }
  
  public static TimeStamp getPFM_TimeStamp(OSM_Fabric f)
  {
    if(f == null)
      return null;
    if((f.getOsmPorts() != null) && (f.getOsmPorts().PerfMgrPorts != null) && (f.getOsmPorts().PerfMgrPorts.length > 4))
    {
      // hopefully, this is the actual data timestamp
      return f.getOsmPorts().PerfMgrPorts[2].getErrorTimeStamp();
    }
    // FIXME  should we return this if the PFM_Timestamp is unavailable??  Missleading??
    return f.getTimeStamp();
   }
  
  public IB_Guid getGuidFromLid(int lid)
  {
    return osmPorts.getPortGuidFromLid(lid);
  }

  public int getLidFromGuid(IB_Guid guid)
  {
    // a guid can be from a port or a node, search ports first
    int lid = osmPorts.getLidFromPortGuid(guid);
    if (lid > 0)
      return lid;
    
    // assume I was given a node guid, with no matching port guid
    // when that happens, a valid port guid is just one more...
    lid = osmPorts.getLidFromPortGuid(new IB_Guid(guid.getGuid() + 1));
    return lid;
  }

  public String getNameFromGuid(IB_Guid guid)
  {
    return osmNodes.getNameFromGuid(guid);
  }

  public IB_Guid getGuidFromName(String nodeName)
  {
    return osmNodes.getGuidFromName(nodeName);
  }
  
  public IB_Guid getPortGuidFromName(String nodeName, int pNum)
  {
    OSM_Node n = getOSM_Node(getGuidFromName(nodeName));
    SBN_Port [] SubnPorts = osmPorts.getSubnPorts();
    
    if((SubnPorts != null) && (SubnPorts.length > 0) && n != null)
    {
      for(SBN_Port p: SubnPorts)
      {
        if((p.node_guid == n.getNodeGuid().getGuid()) && (p.port_num == pNum))
          return new IB_Guid(p.port_guid);
      }
    }
    return null;
  }
  
  private boolean createOSM_Ports()
  {
    // create the hashmap, using the colon delimined guid+portnumber string a a key
    OSM_Port p = null;
    
    if ((osmPorts != null) && (osmNodes != null) && (nodesAll != null) && (nodesAll.size() > 1))
    {
      // combine the OSM_Ports and OSM_Nodes objects, to create
      // a HashMap of all OSM_Port objects
      
        OSM_NodeType type = OSM_NodeType.UNKNOWN;
        if (osmPorts.PerfMgrPorts == null)
        {
          // BAD CASE: use only the subnet to create the list
          logger.severe("Using only Subnet ports info to create ports");
          for (SBN_Port s : osmPorts.SubnPorts)
          {
            OSM_Node n = nodesAll.get(getOSM_NodeKey(s.node_guid));
            type = OSM_NodeType.UNKNOWN;
            if(n != null)
            {
              if(OSM_NodeType.isEdgeNode(n.sbnNode))
                type = OSM_NodeType.CA_NODE;
              else if (OSM_NodeType.isSwitchNode(n.sbnNode))
                type = OSM_NodeType.SW_NODE;
            }
            else
              logger.severe("Could not find a node that matches this guid (" + nodesAll.size() + ": " + new IB_Guid(s.node_guid).toColonString() + ")");
            p = new OSM_Port(null, s, type);  
            portsAll.put(getOSM_PortKey(p), p);
          }
        }
        else
        {
          logger.info("Using both Subnet and PerfMgr port info to create ports");
          // GOOD CASE:  if here, have both perfmgr and subnet ports
          boolean matchFound = false;
          // they both exist, so we must match them up if possible
          for (SBN_Port s : osmPorts.SubnPorts)
//          for (PFM_Port pp : osmPorts.PerfMgrPorts)
          {
            matchFound = false;
            // look for a matching port in the subnet, and use together if
            // possible
            for (PFM_Port pp : osmPorts.PerfMgrPorts)
//            for (SBN_Port s : osmPorts.SubnPorts)
            {
              // the parent nodes and port numbers should match
              if ((pp.node_guid == s.node_guid) && (pp.port_num == s.port_num))
              {
                // good match!
                
                OSM_Node n = nodesAll.get(getOSM_NodeKey(s.node_guid));
                type = OSM_NodeType.UNKNOWN;
                if(n != null)
                {
                  if(OSM_NodeType.isEdgeNode(n.sbnNode))
                    type = OSM_NodeType.CA_NODE;
                  else if (OSM_NodeType.isSwitchNode(n.sbnNode))
                    type = OSM_NodeType.SW_NODE;
                 }
                else
                  logger.severe("Could not find a node that matches this guid (" + nodesAll.size() + ": " + new IB_Guid(s.node_guid).toColonString() + ")");
                
                
                p = new OSM_Port(pp, s, type);  
                portsAll.put(getOSM_PortKey(p), p);
                matchFound = true;
                break;
              }
            }
            // looked through the whole subnet list and couldn't find a match?
            // FIXME:  these are supposed to be hashed using the port guid, NOT the node guid
            if (!matchFound)
            {
              logger.severe("could not find a match between subnet and perf managers for this port");
//              p = new OSM_Port(pp, null, type);  
              p = new OSM_Port(null, s, type);  
//              portsAll.put(getOSM_PortKey(pp.node_guid, pp.port_num), p);
              portsAll.put(getOSM_PortKey(p), p);
            }
          }
        }
      
      if((portsAll != null) && (portsAll.size() > 0))
        return true;
    }
    return false;
  }
  
  public java.util.LinkedHashMap<String, OSM_Port> getOSM_Ports()
  {   
    return portsAll;
  }

  public LinkedHashMap<String, OSM_Node> getOSM_Nodes()
  {
    return nodesAll;
  }
  
  public OSM_Node getOSM_Node(String NodeKey)
  {
    return nodesAll.get(NodeKey);
  }
  
  public OSM_Node getOSM_Node(IB_Guid guid)
  {
    return (guid == null) ? null: getOSM_Node(guid.toColonString());
  }
  
  public BinList <IB_Guid> getSystemGuidBins()
  {
    if((systemGuidBins == null) || (systemGuidBins.isEmpty()))
        createSystemGuidBins(false);
    return systemGuidBins;
  }
  
  public ArrayList<IB_Guid> getNodeGuidsForSystemGuid(String SystemKey)
  {
    BinList <IB_Guid> bins = getSystemGuidBins();
    if(bins == null)
    {
      System.err.println("There are no system image guids");
      System.exit(0);
    }
    return bins.getBin(SystemKey);
  }
  
  /************************************************************
   * Method Name:
   *  getNodeGuidsForSystemGuid
  **/
  /**
   * Return an array list of node guids associated with the provided
   * system image guid.  
   * 
   *
   * @param systemGuid  the system image guid
   * @return  an array list of node guids associated the the provided
   *          system guid
   ***********************************************************/
  public ArrayList<IB_Guid> getNodeGuidsForSystemGuid(IB_Guid systemGuid)
  {
    return (systemGuid == null) ? null: getNodeGuidsForSystemGuid(systemGuid.toColonString());
  }
  
  private OSM_Node initManagementNode()
  {
    // The guid of the Subnet Manager is sometimes the node guid, and
    // sometimes the port guid.  Looking for a node with a port guid
    // will fail.  So try looking for the node first, and if that
    // fails, look for the port, and return its parent.
    
    // do I have info for this operation?
    if((osmSubnet == null) || (osmSubnet.Managers == null) || (osmSubnet.Managers.length < 1))
    {
      if(osmSubnet == null)
        System.err.println("The subnet is null");
      else if (osmSubnet.Managers == null)
        System.err.println("The array of managers does not exist");
      else if (osmSubnet.Managers.length < 1)
        System.err.println("The array of managers is less less than one");
      return null;
    }
    
    // find and return the first Subnet Manager in the Master state
    for(SBN_Manager m: osmSubnet.Managers)
    {
       if(m.State.startsWith("Master"))
      {
        // find a node or port
          OSM_Node mn = getOSM_Node(OSM_Fabric.getOSM_NodeKey(m.guid));
         if(mn == null)
         {
           // didn't find the guid in the node table, so check the port table
           OSM_Port pt = getOSM_Port(OSM_Fabric.getOSM_PortKey(m.guid, (short)1));
           if(pt == null)
             logger.severe("Could not initialize the Fabrics Subnet Manager");
           else
           {
             OSM_Node pn = getOSM_Node(OSM_Fabric.getOSM_NodeKey(pt));
             mn = getOSM_Node(pn.getOSM_NodeKey());
           }
         }
         return mn;
      }
    }
    return null;
  }
  
  public OSM_Node getManagementNode()
  {
    // initialize if doesn't already exist
    return (ManagementNode == null) ? (ManagementNode = initManagementNode()): ManagementNode;
  }
  
  public boolean isManagementNode(OSM_Node node)
  {
    if(node != null)
      return node.equals(getManagementNode());
    return false;
  }
  
  public OSM_Port getOSM_Port(String PortKey)
  {
    return portsAll.get(PortKey);
  }
  
  public OSM_Port getOSM_PortByNodeString(String nodeString)
  {
    int portNum = getPortNumber(nodeString);
    IB_Guid nodeGuid = getNodeGuid(nodeString);
    
    if((nodeGuid != null) && (portNum > 0))
      return getOSM_Port(OSM_Port.getOSM_PortKey(nodeGuid.getGuid(), (short)portNum));
    return null;
   }
  
  public static int getPortNumber(String portid)
  {
    // normally this would be of the form;
    // guid:pn
    // name pn
    // lid pn
    // guid pn
    
    // so only the last part of the string is the port number
    // return 0, indicating couldn't be found, or nothing specified
      if(portid != null)
      {
        // should be at least two words
        //  the very last word, is supposed to be the port number
        //  if only one word, then check to see if there are 4 colons, if so, port number is after that
        String[] args = portid.split(" ");
        if((args != null) && (args.length > 0))
        {
          int p = 0;
          if(args.length == 1)
          {
            // see if a port number is tagged on as the last value of a colon delimited guid+port string
            String[] octets = portid.split(":");
            if(octets.length > 4)
              p = Integer.parseInt(octets[octets.length -1]);
           }
          else
            p = Integer.parseInt(args[args.length -1]);
          return p;
        }
       }
     return 0;
  }
  
  public IB_Guid getNodeGuid(String nodeStr)
  {
    // given a string (name, lid, or guid), return a valid guid
    return getNodeGuid(nodeStr, false);
  }

  public IB_Guid getNodeGuid(String nodeStr, boolean portGuid)
  {
    // this string needs to represent a lid (in hex, or not), a guid (in hex or not) or
    // a node name.  It will return a guid that matches, or null
    
    // *** this only works if there is a node or port with the guid in the fabric
    
    // if portGuid is true, perform an additional check to see if the supplied guid
    // corresponds to a channel adapters port, which may be different than the node
    
      if(nodeStr != null)
      {
        String nodeid = nodeStr;
        String[] octets = nodeStr.split(":");
        
        // if there are 4 octets, then use them, and ignore any trailing crap (like a port number)
        if(octets.length > 3)
        {
          // since there are colons, start over, and use only the first word
          String[] args = nodeStr.split(" ");
                 octets = args[0].split(":");

          StringBuffer buff = new StringBuffer();
          for(int i=0; i<4; i++)
            buff.append(octets[i] + ":");
          nodeStr = buff.toString();
          int end = nodeStr.lastIndexOf(":");
          nodeid = nodeStr.substring(0, end);
        }
        IB_Guid g = null;
        int nodeLid = 0;
        boolean lidFailed  = true;
        boolean guidFailed = true;

        // this can be a name, lid, or guid (try name last)
        if(nodeid.length() < 8)
        {
          // could be a lid
          try
          {
            nodeLid = IB_Address.toLidValue(nodeid);
            lidFailed = false;
            g = getGuidFromLid(nodeLid);
          }
          catch(NumberFormatException nfe)
          {
            // perhaps a small name
            lidFailed = true;
          }
        }
        else
        {
          // an IB_Guid or a long
          try
          {
            // this should handle longs and hex (with and without leading 0x)
            g = new IB_Guid(nodeid);
            guidFailed = false;
          }
          catch(Exception e)
          {
            guidFailed = true;
           }
         }
        
        if(!guidFailed)
        {
          // is this a node guid?
          OSM_Node on = getOSM_Node(g);
          if(on == null)
          {
            // one final check, if desired
            if(portGuid)
            {
              // look through the fabric, is this a valid port guid
              if(!isUniquePortGuid(g))
                guidFailed = true;
            }
            else
              guidFailed = true;
          }
         }
        
        if(lidFailed && guidFailed)
        {
          // try a name search
          g = getGuidFromName(nodeid);
        }
        return g;
    }
     return null;
  }

  protected IB_Guid getPortsNodeGuid(String nodeStr)
  {
    // a port may or may not have the same guid as its parent
    
    // will this return a node guid?
    IB_Guid ng = getNodeGuid(nodeStr);
    if(ng != null)
      return ng;
    
    // the only situation may be if the string represents a port_guid
    // which can be different from the node_guid
    
    // try to convert this string into a guid
    IB_Guid pg = null;
    try
    {
      pg = new IB_Guid(nodeStr);
      if(pg == null)
        return null;
    }
    catch (Exception e)
    {
      logger.severe("Port Guid string conversion error: " + nodeStr);
      return null;
    }
    
    // search through the ports addresses, and find a match
    //  if found, return the NODE guid, not the port guid
      SBN_Port [] SubnPorts = getOsmPorts().getSubnPorts();
      
      if((SubnPorts != null) && (SubnPorts.length > 0))
      {
        for(SBN_Port p: SubnPorts)
        {
          if((p.port_guid == pg.getGuid()))
            return new IB_Guid(p.node_guid);
        }
      }
    return null;
  }

  
  private boolean createOSM_Nodes()
  {
    // create the hashmap, using the colon delimined guid string a a key
    if (osmNodes != null)
    {
      // always require subnNodes, but PerfMgrNodes are optional
      if((osmNodes != null) && (osmNodes.getSubnNodes() != null) && (osmNodes.getSubnNodes().length > 1))
      {
        PFM_Node[] pnodes = osmNodes.getPerfMgrNodes();
        SBN_Node[] snodes = osmNodes.getSubnNodes();
        
        if(((pnodes != null) && (pnodes.length > 1)))
        {
          boolean matched = false;
          // asked to return something, so try to match up the two types of nodes
          for(SBN_Node s: snodes)
          {
            matched = false;
            for(PFM_Node p: pnodes)
            {
              if(s.getNodeGuid().equals(p.getNodeGuid()))
              {
                OSM_Node n = new OSM_Node(p,s);
                nodesAll.put(getOSM_NodeKey(n), n);
                matched = true;
                break;
              }
            }
            if(!matched)
            {
              System.err.println("Couldn't find a matching PFM_Node for " + s.getNodeGuid().toColonString());
              System.err.println("Couldn't find a match PFM Node for " + s.description);
              // consider creating a node without PFM_Node
              OSM_Node n = new OSM_Node(s);
              nodesAll.put(getOSM_NodeKey(n), n);
            }
          }
        }
      }      
      if((nodesAll != null) && (nodesAll.size() > 0))
        return true;
     }
   return false;
}
  
  /************************************************************
   * Method Name:
   *  createSystemGuidBins
  **/
  /**
   * Iterate through all the nodes in the fabric, and create Bins for
   * all of the system guids, each bin will contain the switch node guid
   * associated with the system guid.
   *
   * @see     describe related java objects
   *
   * @param includeSingletons  true, if the bins should include system guids with only a single switch guid
   * @return
   ***********************************************************/
  public boolean createSystemGuidBins(boolean includeSingletons)
  {
    // key is the system image guid - must be different than the node guid, or I don't care
    // value is a BinList of node guids (all the nodes that share this system image guid)
    
    // create the bins, using the colon delimined guid string as a key
    if((nodesAll != null) && (nodesAll.size() > 0))
    {
     BinList <IB_Guid> sBins = new BinList <IB_Guid>();

     for(OSM_Node n: nodesAll.values())
     {
       // does this node have a system guid?
    	 if(n.sbnNode.sys_guid > 1)
    	 {
    		// is the system guid different than the node guid
    		if(n.sbnNode.sys_guid != n.sbnNode.node_guid)
          sBins.add(n.getNodeGuid(), (new IB_Guid(n.sbnNode.sys_guid)).toColonString());
 //       sBins.add(n.getNodeGuid(), Long.toString(n.sbnNode.sys_guid));
    	 }
     }
     
     if(sBins != null)
     {
       // I may only care about the system guid bin list that have more than one node guid in it
       BinList <IB_Guid> gBins = new BinList <IB_Guid>();
       for(String key: sBins.getKeys())
       {
         ArrayList <IB_Guid> o = sBins.getBin(key);
         if((o.size() > 1) || (includeSingletons && o.size() > 0))
         {
//           if(o.size() > 1)
//             System.err.println("Sys guid:  " + key + " has " + o.size() + " guids");
           gBins.addBin(o, key);
         }
       }
       if((gBins != null) && (gBins.size() > 0))
       {        
         systemGuidBins = gBins;
//         System.err.println("There are " + systemGuidBins.size() + " systems, with the following sizes");
//         if(gBins.size() < 5)
//           System.err.println(systemGuidBins.toString());         
       }
     }
     return true;
   }
   return false;
  }
  
    private boolean createIB_Links()
    {
      int badLinkCounter = 0;
      int badPortCounter = 0;
      if((portsAll != null) && (portsAll.size() > 1))
      {      
      // using the port map, construct links
      
        // link information is only contained in the SBN_Port, so this must exist
        // this end (#1)
        short lpn1 = 0;
        long lpg1 = 0;
        short pn1 = 0;
        long pg1 = 0;
        
        // the other end (#2)
        short pn2 = 0;
        long pg2 = 0;
        short lpn2 = 0;
        long lpg2 = 0;

        IB_Link link = null;
        String portKey = null;
        String linkKey = null;
        
      // iterate through all the ports
      for (OSM_Port p : portsAll.values())
      {
        if (p == null)
        {
          logger.severe("port is null, continuing");
          continue;
        }
        if (p.getSbnPort() != null)
        {
          // have info on both types of ports
          lpg1 = p.getSbnPort().linked_node_guid;
          lpn1 = p.getSbnPort().linked_port_num;
          pg1  = p.getSbnPort().node_guid;
          pn1  = p.getSbnPort().port_num;

          // try to find a remote (linked) port that matches the opposite end
          portKey = getOSM_PortKey(lpg1, lpn1);
          OSM_Port rp = (portKey == null) ? null : portsAll.get(portKey);
          
          if (rp != null)
          {
            // verify ?? port numbers should match up too
            lpg2 = rp.getSbnPort().linked_node_guid;
            lpn2 = rp.getSbnPort().linked_port_num;
            pg2  = rp.getSbnPort().node_guid;
            pn2  = rp.getSbnPort().port_num;

            // a link occurs if both ports think they are connected to each
            // other
            boolean localToRemote = (pg2 == lpg1) && (pn2 == lpn1);
            boolean remoteToLocal = (pg1 == lpg2) && (lpn2 == pn1);

            if (localToRemote && remoteToLocal)
            {
              // found two ports connected together, so create a link if all
              // active
              if ((p.getState() == OSM_PortState.ACTIVE) && (rp.getState() == OSM_PortState.ACTIVE))
              {
                // always order the endpoints based on the guid & port_nums
                // because this will help me find duplicates
                if ((lpg1 + lpn1) > (p.getSbnPort().node_guid + p.getSbnPort().port_num))
                  link = new IB_Link(p, rp);
                else
                  link = new IB_Link(rp, p);

                linkKey = getIB_LinkKey(link);
                if ((linkKey != null) && (link != null))
                {
                  linksAll.put(linkKey, link);
                }
                else
                  badLinkCounter++;
              }
            }
            else
              badLinkCounter++;
          }
          else
          {
            if((lpg1 != 0) || (lpn1 != 0))
              logger.fine("Could not find a remote port that matches (" + portKey + ")");
            badPortCounter++;
          }
        }
        else
        {
          logger.severe("Subnet Port is null, this should never happen!");
          badPortCounter++;
        }
      }
      if(badLinkCounter > 0 )
        logger.severe("Number of BAD or DOWN Links: " + badLinkCounter);
      if(badPortCounter > 0)
        logger.severe("Number of BAD or DOWN Ports: " + badPortCounter);
      
      if((linksAll != null) && (linksAll.size() > 0))
        return true;
      }
      return false;      
    }
    
    /************************************************************
     * Method Name:
     *  getLinksAll
     **/
    /**
     * Returns the value of linksAll
     *
     * @return the linksAll
     *
     ***********************************************************/
    
    public LinkedHashMap<String, IB_Link> getIB_Links()
    {
      return linksAll;
    }
    
    public LinkedHashMap<String, IB_Link> getIB_Links(IB_Guid g)
    {
      // return all the links associated with this guid
      if(g == null)
        return null;
      
      OSM_Node n = getOSM_Node(g);
      if(n == null)
        return null;
      
      // found a node, so build up a linkHashMap
      LinkedHashMap<String, IB_Link>  nodeLinks  = new LinkedHashMap<String, IB_Link>();
      for(short pn = 0; pn < n.sbnNode.num_ports; pn++)
      {
        IB_Link l = OSM_Fabric.getIB_Link(g.getGuid(), (short)(pn+1), linksAll);
        if(l != null)
          nodeLinks.put(l.getIB_LinkKey(), l);
      }
      return nodeLinks;
    }
    
    public static IB_Link getIB_Link(long guid, short portNum, LinkedHashMap<String, IB_Link> lMap)
    {
      // return a link that has this guid and port number at either end (first
      // match)
      String key = getOSM_PortKey(guid, portNum);
      IB_Link l = lMap.get(key);
      if (l != null)
        return l;

      // not easily found, so loop through the Map, and look for it at endpoint2
      for (IB_Link lv : lMap.values())
      {
        OSM_Port p = lv.getEndpoint2();
        if ((p.getNodeGuid().getGuid() == guid) && (p.getPortNumber() == (int) portNum))
          return lv;
      }
      return null;
    }
    
    public String getLinkDescription(long guid, short portNum, LinkedHashMap<String, IB_Link> lMap)
    {
      // return a link that has this guid and port number at either end (first
      // match)
      IB_Link l = getIB_Link(guid, portNum, lMap);
      if (l == null)
        return "unknown link";

      // refer to IB_PortElement, this string should closely match
      String nodeName = osmNodes.getNameFromGuid(new IB_Guid(guid));

      OSM_Port e1 = l.getEndpoint1();
      OSM_Port e2 = l.getEndpoint2();
      
      // swap, if necessary, so description starts with provided guid and portNum
      if((guid == e2.getNodeGuid().getGuid()) && ((int)portNum == e2.getPortNumber()))
      {
         e1 = l.getEndpoint2();
         e2 = l.getEndpoint1();
      }
      String rNodeName = osmNodes.getNameFromGuid(new IB_Guid(e2.getNodeGuid()));
      
      StringBuffer buff = new StringBuffer();
      buff.append("\"" + nodeName + "\" " );
      buff.append("p: " + e1.getPortNumber() + " <==> ");
      buff.append("p: " + e2.getPortNumber() + " ");
      buff.append("\"" + rNodeName + "\" " );
      return buff.toString();
    }
    
    public static String getOSM_NodeKey(long guid)
    {
      return (new IB_Guid(guid)).toColonString();      
    }
    
    public String getNodeIdString(IB_Guid g)
    {
      String format = "%-24s %s   lid: %4d (0x%s)";
      String name = getNameFromGuid(g);
      int lid     = getLidFromGuid(g);
      
      return String.format(format, name, g.toColonString(), lid, Integer.toHexString(lid));
    }
    
    public static String getNodeIdString(IB_Guid g, OSM_Fabric f)
    {
      if(f != null)
        return f.getNodeIdString(g);
      return null;
    }
    
    public static String getOSM_NodeKey(OSM_Node n)
    {
      // use the subnet
      if((n == null))
        return null;
      
      return n.getOSM_NodeKey();      
    }
    
    public static String getOSM_NodeKey(OSM_Port p)
    {
      // use the subnet
      if((p == null) || (p.sbnPort == null))
        return null;
      
      // use the node guid, NOT the port guid
      return getOSM_NodeKey(p.sbnPort.node_guid);      
    }
    
    public static String getOSM_PortKey(long guid, short port_num)
    {
      return (new IB_Guid(guid)).toColonString()+ ":"+ port_num;      
    }
    
    public static String getOSM_PortKey(OSM_Port p)
    {
      // use the subnet
      if((p == null) || (p.sbnPort == null))
        return null;
      
      // use the node guid, NOT the port guid
      return getOSM_PortKey(p.sbnPort.node_guid, p.sbnPort.port_num);
    }
    
    public static String getIB_LinkKey(IB_Link l)
    {
      // use the endpoint 1
      if((l == null) || (l.Endpoint1 == null))
        return null;
      
      return getOSM_PortKey(l.Endpoint1);
    }
    
    public static String getIB_LinkKey(OSM_Port p)
    {
      return getOSM_PortKey(p);
    }
    
    public static String getOSM_FabricKey(OSM_Fabric f)
    {
      // use the name and timestamp
      if((f == null) || (f.getFabricName() == null) || (f.getTimeStamp() == null))
        return null;
      
      return getOSM_FabricKey(f.getFabricName(), f.getTimeStamp());      
    }
    
 public static String getOSM_FabricKey(String name, TimeStamp timeStamp)
 {
   if(timeStamp != null)
     return name + ": " + timeStamp.toString();
   return null;
 }
 
 public static String getFabricNameFromFabricKey(String key)
 {
   if((key != null) && (key.length() > 20))
     return key.substring(0, key.indexOf(": "));
   return null;
 }

 public static TimeStamp getTimeStampFromFabricKey(String key)
 {
   if((key != null) && (key.length() > 20))
     return new TimeStamp(key.substring(key.indexOf(": ") + 2, key.length()));
   return null;
 }

    private static void putBoolean(HashMap<String, String> map, String key, boolean value)
    {
      // put only true
      if((value != false) && (key != null) && (map != null))
        map.put(key, Boolean.toString(value));
    }
    
    private static void putShort(HashMap<String, String> map, String key, short value)
    {
      // put only non-zero
      if((value != 0) && (key != null) && (map != null))
        map.put(key, Short.toString(value));
    }
    
    private static void putInt(HashMap<String, String> map, String key, int value)
    {
      // put only non-zero
      if((value != 0) && (key != null) && (map != null))
        map.put(key, Integer.toString(value));
    }
    
    private static void putLong(HashMap<String, String> map, String key, long value)
    {
      // put only non-zero
      if((value != 0) && (key != null) && (map != null))
        map.put(key, Long.toString(value));
    }
    
    private static void putString(HashMap<String, String> map, String key, String value)
    {
      // put only non-null
      if((value != null) && (key != null) && (map != null) && (value.length()>0))
        map.put(key, value);
    }
    
    public LinkedHashMap<String, String> getOptions()
    {
    	createSystemGuidBins(false);
      if(this.getOsmSubnet() == null)
        return null;
      return OSM_Fabric.getOtions(this.getOsmSubnet().Options);
    }
    
    public static LinkedHashMap<String, String> getOtions(SBN_Options o)
    {
      LinkedHashMap<String, String> oMap = new LinkedHashMap<String, String>();
      
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
    

    
    public OSM_Stats getOsmStats()
    {
      return osmStats;
    }
    public OSM_Subnet getOsmSubnet()
    {
      return osmSubnet;
    }
    public OSM_SysInfo getOsmSysInfo()
    {
      return osmSysInfo;
    }
    public OSM_Nodes getOsmNodes()
    {
      return osmNodes;
    }
    public OSM_Ports getOsmPorts()
    {
      return osmPorts;
    }
    public OSM_EventStats getOsmEventStats()
    {
      return osmEventStats;
    }
    

    public static OSM_Fabric readFabric(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException
    {
      FileInputStream fileInput = new FileInputStream(fileName);
      ObjectInputStream objectInputStream = new ObjectInputStream(fileInput);
      Object obj = objectInputStream.readObject();
      objectInputStream.close();
      return (OSM_Fabric) obj;
    }
    
    public static void writeFabric(String fileName, OSM_Fabric fabric) throws IOException
    {
      File outFile = new File(fileName);
      outFile.getParentFile().mkdirs();
      FileOutputStream fileOutput = new FileOutputStream(outFile);
      ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput);
      objectOutput.writeObject(fabric);
      objectOutput.close();
      return;
    }
    
    /************************************************************
     * Method Name:
     *  toString
    **/
    /**
     * Describe the method here
     *
     * @see java.lang.Object#toString()
     *
     * @return
     ***********************************************************/
    
    public String toInfo()
    {
      StringBuffer stringValue = new StringBuffer();
      stringValue.append(OSM_Fabric.class.getSimpleName() + "\n");
      
      stringValue.append("fabric name:             "  + this.getFabricName() + "\n");
      stringValue.append("timestamp:               " + this.toTimeString() + "\n");
      stringValue.append("# nodes:                 " + this.getOSM_Nodes().size() + "\n");
      stringValue.append("# ports:                 " + this.getOSM_Ports().size() + "\n");
      stringValue.append("# links:                 " + this.getIB_Links().size());
    
      return stringValue.toString();
    }
    
    /************************************************************
     * Method Name:
     *  toTimeString
    **/
    /**
     * Returns a list of TimeStamps for this object
     *
     * @see     describe related java objects
     *
     * @return
     ***********************************************************/
    public String toTimeString()
    {
      return getPFM_TimeStamp().toString();
    }

    public static boolean isSwitch(IB_Guid g, OSM_Fabric fabric)
    {
      // does the supplied guid belong to a switch node?
      if((fabric != null) && (g != null) && (fabric.osmSubnet != null))
        return fabric.osmSubnet.isSwitch(g);
      return false;
    }

    public boolean isSubnetManager(IB_Guid g)
    {
      OSM_Node n = getManagementNode();
      if((g != null) && (n != null))
      {
        return g.equals(n.getNodeGuid());
      }
      return false;
    }

}
