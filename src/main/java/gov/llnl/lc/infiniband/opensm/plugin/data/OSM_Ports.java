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
 *        file: OSM_SysInfo.java
 *
 *  Created on: Jun 30, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import gov.llnl.lc.infiniband.core.IB_Guid;
import gov.llnl.lc.infiniband.core.IB_Link;
import gov.llnl.lc.infiniband.core.NativePeerClass;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmClientInterface;
import gov.llnl.lc.util.filter.WhiteAndBlackListFilter;

/**********************************************************************
 * The top level object which contains information about all the
 * known Ports in the subnet.
 * <p>
 * Java Peer Class, to the native interface.  All peer classes contain
 * public members which can be directly accessed.  All peer classes
 * have fully parameterized constructors that are used by the native
 * layer to create an instance of the class.  Instances of peer classes
 * are not intended to be created from the java environment.
 * <p>
 * @see  OsmClientInterface#getOsmPorts()
 * @see  PFM_Port
 * @see  SBN_Port
 * @see NativePeerClass
 *
 * @author meier3
 * 
 * @version Aug 26, 2011 4:22:08 PM
 **********************************************************************/
@NativePeerClass("v1.0")
public class OSM_Ports implements Serializable
{ 
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = -3750604241242259904L;
  
  /**  the ports managed by the perf manager **/
  public PFM_Port [] PerfMgrPorts;

  /**  the ports managed by the subnet manager **/
  public SBN_Port [] SubnPorts;    

  
  /************************************************************
   * Method Name:
   *  OSM_Ports
   */
   /** Default constructor
   *
   ***********************************************************/
  public OSM_Ports()
  {
  }

  /************************************************************
   * Method Name:
   *  OSM_Ports
   */
   /**
   * The fully parameterized constructor used by the native layer
   * to create an instance of this peer class.
   *
   * @see     PFM_Port
   * @see     SBN_Port
   *
   * @param perfMgrPorts the perf manager ports
   * @param subnPorts  the subnet managers ports
   ***********************************************************/
  public OSM_Ports(PFM_Port[] perfMgrPorts, SBN_Port[] subnPorts)
  {
    PerfMgrPorts = perfMgrPorts;
    SubnPorts = subnPorts;
  }
  
  /************************************************************
   * Method Name:
   *  getPerfMgrPorts
   */
   /** Returns all the ports managed by the perf manager
   *
   * @return the array of perf manager ports
   ***********************************************************/
  public PFM_Port[] getPerfMgrPorts()
  {
    return PerfMgrPorts;
  }
  
  public void setPerfMgrPorts(PFM_Port[] perfMgrPorts)
  {
    PerfMgrPorts = perfMgrPorts;
  }
  /************************************************************
   * Method Name:
   *  getSubnPorts
   */
   /** Returns all the ports managed by the subnet manager
   *
   * @return the array of subnet manager ports
   ***********************************************************/
  public SBN_Port[] getSubnPorts()
  {
    return SubnPorts;
  }
  
  public void setSubnPorts(SBN_Port[] subnPorts)
  {
    SubnPorts = subnPorts;
  }
  
  public int getLidFromPortGuid(IB_Guid guid)
  {
 // see  OSM_FabricAnalyzer

    // loop through all the ports and return the first lid
    // that matches the guid, or -1
    
    if((SubnPorts != null) && (SubnPorts.length > 0) && guid != null)
    {
      for(SBN_Port p: SubnPorts)
      {
        if(guid.equals(new IB_Guid(p.port_guid)))
          return p.port_info.base_lid;
      }
    }
    return -1;
  }

  public IB_Guid getPortGuidFromLid(int lid)
  {
 // see  OSM_FabricAnalyzer

    // loop through all the ports and return the first guid
    // that matches the lid, or null
    
    // EDGES, PORTS, and LINKS ** ALWAYS ** use the parent node guid + port # for identification
    //   almost never need port guid, which doesn't seem to be handled consistently across vendors


    if((SubnPorts != null) && (SubnPorts.length > 0))
    {
      for(SBN_Port p: SubnPorts)
      {
        if(p.port_info.base_lid == lid)
          return new IB_Guid(p.port_guid);
      }
    }
    return null;
  }
  /************************************************************
   * Method Name:
   *  createOSM_Ports
  **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   ***********************************************************/
  public ArrayList<OSM_Port> createOSM_Ports()
  {
    // using the PerfMgrPorts and SubnPorts, create a list of OSM_Ports
    if((this.PerfMgrPorts == null) && (this.SubnPorts == null))
      return null;
     
    OSM_NodeType type = OSM_NodeType.UNKNOWN;
    ArrayList<OSM_Port> pL = new ArrayList<OSM_Port>();
    if(SubnPorts == null)
    {
      // use only the perfmgr to create the list
      for(PFM_Port p: PerfMgrPorts)
        pL.add(new OSM_Port(p, null, type));
    }
    else if(PerfMgrPorts == null)
    {
      // use only the subnet to create the list
      for(SBN_Port s: SubnPorts)
        pL.add(new OSM_Port(null, s, type));
    }
    else
    {
      boolean matchFound = false;
      // they both exist, so we must match them up if possible
      for(PFM_Port p: PerfMgrPorts)
      {
        matchFound = false;
        // look for a matching port in the subnet, and use together if possible
        for(SBN_Port s: SubnPorts)
        {
          // the parent nodes and port numbers should match
          if((p.node_guid == s.node_guid) && (p.port_num == s.port_num))
            {
              // good match!
              pL.add(new OSM_Port(p, s, type));
              matchFound = true;
              break;
            }
        }
        // looked through the whole subnet list and couldn't find a match?
        if(!matchFound)
          pL.add(new OSM_Port(p, null, type));
      }
    }
    return pL;
  }
  
  /************************************************************
   * Method Name:
   *  createOSM_Ports
  **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   ***********************************************************/
  public static ArrayList<OSM_Port> createOSM_Ports(OSM_Nodes allNodes, OSM_Ports allPorts)
  {
    if((allNodes == null) && (allPorts == null))
      return null;
    if(allNodes == null)
      return allPorts.createOSM_Ports();
    
    return allPorts.createOSM_Ports(allNodes);
  }
  
  public static HashMap <String, OSM_Port> createOSM_PortMap(OSM_Nodes osmNodes, OSM_Ports osmPorts)
  {
    // create a hashmap of the nodes for efficiency
    HashMap <String, OSM_Node> nodeMap = OSM_Nodes.createOSM_NodeMap(osmNodes);
    
    // create the hashmap, using the colon delimined guid+portnumber string a a key
    HashMap<String, OSM_Port> portMap = new HashMap<String, OSM_Port>();

    OSM_Port p = null;
    
    if ((osmPorts != null) && (osmNodes != null) && (nodeMap != null) && (nodeMap.size() > 1))
    {
      // combine the OSM_Ports and OSM_Nodes objects, to create
      // a HashMap of all OSM_Port objects
        OSM_NodeType type = OSM_NodeType.UNKNOWN;
        if (osmPorts.PerfMgrPorts == null)
        {
          for (SBN_Port s : osmPorts.SubnPorts)
          {
            OSM_Node n = nodeMap.get(OSM_Node.getOSM_NodeKey(s.node_guid));
            type = OSM_NodeType.UNKNOWN;
            if(n != null)
            {
              if(OSM_NodeType.isEdgeNode(n.sbnNode))
                type = OSM_NodeType.CA_NODE;
              else if (OSM_NodeType.isSwitchNode(n.sbnNode))
                type = OSM_NodeType.SW_NODE;
            }
            p = new OSM_Port(null, s, type);  
            portMap.put(p.getOSM_PortKey(), p);
          }
        }
        else
        {
          // GOOD CASE:  if here, have both perfmgr and subnet ports
          boolean matchFound = false;
          // they both exist, so we must match them up if possible
          for (SBN_Port s : osmPorts.SubnPorts)
          {
            matchFound = false;
            // look for a matching port in the subnet, and use together if
            // possible
            for (PFM_Port pp : osmPorts.PerfMgrPorts)
            {
              // the parent nodes and port numbers should match
              if ((pp.node_guid == s.node_guid) && (pp.port_num == s.port_num))
              {
                // good match!
                OSM_Node n = nodeMap.get(OSM_Node.getOSM_NodeKey(s.node_guid));
                type = OSM_NodeType.UNKNOWN;
                if(n != null)
                {
                  if(OSM_NodeType.isEdgeNode(n.sbnNode))
                    type = OSM_NodeType.CA_NODE;
                  else if (OSM_NodeType.isSwitchNode(n.sbnNode))
                    type = OSM_NodeType.SW_NODE;
                 }
                
                p = new OSM_Port(pp, s, type);  
                portMap.put(p.getOSM_PortKey(), p);
                matchFound = true;
                break;
              }
            }
            // looked through the whole subnet list and couldn't find a match?
            // FIXME:  these are supposed to be hashed using the port guid, NOT the node guid
            if (!matchFound)
            {
              p = new OSM_Port(null, s, type);  
              portMap.put(p.getOSM_PortKey(), p);
            }
          }
        }
      
      if((portMap != null) && (portMap.size() > 0))
        return portMap;
    }
    return null;
  }
  
  public ArrayList<OSM_Port> createOSM_Ports(OSM_Nodes allNodes)
  {
    if(allNodes == null)
      return createOSM_Ports();

    ArrayList<OSM_Port> pL = new ArrayList<OSM_Port>();
    ArrayList<IB_Guid> egL = new ArrayList<IB_Guid>();
    ArrayList<IB_Guid> sgL = new ArrayList<IB_Guid>();
    
    ArrayList<SBN_Node> sbna = new ArrayList<SBN_Node>(Arrays.asList(allNodes.getSubnNodes()));
    OSM_NodeType type = OSM_NodeType.UNKNOWN;
    
    for(SBN_Node n: sbna)
    {
      // create a list of CA guids (fewer than SW guids)
      if(OSM_NodeType.isEdgeNode(n))
        egL.add(n.getNodeGuid());
      else if(OSM_NodeType.isSwitchNode(n))
        sgL.add(n.getNodeGuid());
    }

      if (PerfMgrPorts == null)
      {
        // use only the subnet to create the list
        for (SBN_Port s : SubnPorts)
        {
          type = OSM_NodeType.UNKNOWN;
          if(egL.contains(new IB_Guid(s.node_guid)))
              type = OSM_NodeType.CA_NODE;
          else if (sgL.contains(new IB_Guid(s.node_guid)))
            type = OSM_NodeType.SW_NODE;
          
          pL.add(new OSM_Port(null, s, type));
        }
      }
      else
      {
        boolean matchFound = false;
        // they both exist, so we must match them up if possible
        for (PFM_Port p : PerfMgrPorts)
        {
          matchFound = false;
          // look for a matching port in the subnet, and use together if
          // possible
          for (SBN_Port s : SubnPorts)
          {
            // the parent nodes and port numbers should match
            if ((p.node_guid == s.node_guid) && (p.port_num == s.port_num))
            {
              // good match!
              type = OSM_NodeType.UNKNOWN;
              if(egL.contains(new IB_Guid(s.node_guid)))
                  type = OSM_NodeType.CA_NODE;
              else if (sgL.contains(new IB_Guid(s.node_guid)))
                type = OSM_NodeType.SW_NODE;
              
              pL.add(new OSM_Port(p, s, type));
              matchFound = true;
              break;
            }
          }
          // looked through the whole subnet list and couldn't find a match?
          if (!matchFound)
            pL.add(new OSM_Port(p, null, OSM_NodeType.UNKNOWN));
        }
      }
    return pL;
  }
  
  /************************************************************
   * Method Name:
   *  createOSM_Links
  **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   ***********************************************************/
  public ArrayList<IB_Link> createIB_Links(OSM_Nodes allNodes)
  {
    ArrayList<OSM_Port> pL = this.createOSM_Ports(allNodes);
    
    if((pL == null) || (pL.size() < 2))
      return null;
    
    // using the port list, construct links
    return IB_Link.createIB_Links(pL, true);
  }
  
  @Override
  public String toString()
  {
    return "OSM_Ports [PerfMgrPorts=" + Arrays.toString(PerfMgrPorts) + "\n\tSubnPorts="
        + Arrays.toString(SubnPorts) + "]\n";
  }

  /************************************************************
   * Method Name:
   *  getOSM_Ports
  **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects
   *
   * @param osmPorts
   * @param filter
   * @return
   ***********************************************************/
  public static OSM_Ports getOSM_Ports(OSM_Ports osmPorts, WhiteAndBlackListFilter filter)
  {
    // given a valid filter and ports
    if((osmPorts == null) || (filter == null))
      return osmPorts;

    // return the nodes that pass through the filter
    /**  the nodes managed by the perf manager **/
    ArrayList <PFM_Port> pPorts = new ArrayList <PFM_Port>();
    /**  the nodes managed by the subnet manager **/
    ArrayList <SBN_Port> sPorts = new ArrayList <SBN_Port>();
    
    // iterate through each list, and add them only if they pass through the filter
    for(SBN_Port n: osmPorts.getSubnPorts())
        if(!filter.isFiltered(new IB_Guid(n.node_guid).toColonString()))
          sPorts.add(n);
 
    for(PFM_Port p: osmPorts.getPerfMgrPorts())
        if(!filter.isFiltered(p.getNodeGuid().toColonString()))
          pPorts.add(p);
    
    PFM_Port pA [] = new PFM_Port[pPorts.size()];
    SBN_Port sA [] = new SBN_Port[sPorts.size()];

    return new OSM_Ports(pPorts.toArray(pA), sPorts.toArray(sA));
  }
   

}
