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
 *        file: NativePeerClassExample.java
 *
 *  Created on: Aug 26, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import gov.llnl.lc.infiniband.core.IB_Guid;
import gov.llnl.lc.infiniband.core.NativePeerClass;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmClientInterface;
import gov.llnl.lc.util.filter.WhiteAndBlackListFilter;

/**********************************************************************
 * The top level object which contains information about all the
 * known Nodes in the subnet.
 * <p>
 * Java Peer Class, to the native interface.  All peer classes contain
 * public members which can be directly accessed.  All peer classes
 * have fully parameterized constructors that are used by the native
 * layer to create an instance of the class.  Instances of peer classes
 * are not intended to be created from the java environment.
 * <p>
 * @see  OsmClientInterface#getOsmNodes()
 * @see  PFM_Node
 * @see  SBN_Node
 * @see NativePeerClass
 *
 * @author meier3
 * 
 * @version Aug 26, 2011 10:28:13 AM
 **********************************************************************/
@NativePeerClass("v1.0")
public class OSM_Nodes implements Serializable
{
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = -6035911567963153115L;
  
  /**  the nodes managed by the perf manager **/
  public PFM_Node [] PerfMgrNodes;
  /**  the nodes managed by the subnet manager **/
  public SBN_Node [] SubnNodes;    

  
  /************************************************************
   * Method Name:
   *  OSM_Nodes
  **/
  /**
   * Default constructor.
   *
   ***********************************************************/
  public OSM_Nodes()
  {
  }

  /************************************************************
   * Method Name:
   *  OSM_Nodes
  **/
  /**
   * The fully parameterized constructor used by the native layer
   * to create an instance of this peer class.
   *
   * @see  PFM_Node
   * @see  SBN_Node
   *
   * @param perfMgrNodes - an array of perf manager nodes
   * @param subnNodes - an array of subnet manager nodes
   ***********************************************************/
  public OSM_Nodes(PFM_Node[] perfMgrNodes, SBN_Node[] subnNodes)
  {
    PerfMgrNodes = perfMgrNodes;
    SubnNodes = subnNodes;
  }

  
  /************************************************************
   * Method Name:
   *  getPerfMgrNodes
   **/
  /**
   * Returns the value of perfMgrNodes.  This array can also be
   * directly referenced since its field value is public.
   *
   * @return the perfMgrNodes
   *
   ***********************************************************/
  
  public PFM_Node[] getPerfMgrNodes()
  {
    return PerfMgrNodes;
  }

  public static ArrayList <OSM_Node> createOSM_Nodes(OSM_Nodes allNodes, boolean requireBoth)
  {
    java.util.ArrayList<OSM_Node> nodes   = new java.util.ArrayList<OSM_Node>();
    java.util.ArrayList<OSM_Node> rtnNodes = new java.util.ArrayList<OSM_Node>();
  // given OSM_Nodes, create a list of OSM_Node objects
  //
  // always requrie subnNodes, but PerfMgrNodes are optional
  if((allNodes != null) && (allNodes.getSubnNodes() != null) && (allNodes.getSubnNodes().length > 1))
  {
    PFM_Node[] pnodes = allNodes.getPerfMgrNodes();
    SBN_Node[] snodes = allNodes.getSubnNodes();
    
    if(!requireBoth || ((pnodes != null) && (pnodes.length > 1)))
    {
      // asked to return something, so try to match up the two types of nodes
      for(SBN_Node s: snodes)
      {
        for(PFM_Node p: pnodes)
        {
          if(s.getNodeGuid().compareTo(p.getNodeGuid()) == 0)
          {
              nodes.add(new OSM_Node(p,s));
              break;
          }
        }
        
      }
    }
  }
  return nodes;
  }
  
  public static  HashMap <String, OSM_Node> createOSM_NodeMap(OSM_Nodes osmNodes)
  {
    HashMap<String, OSM_Node> nodesAll = new HashMap<String, OSM_Node>();
    
    // create the hashmap, using the colon delimited guid string a a key
    if (osmNodes != null)
    {
      // always require subnNodes, but PerfMgrNodes are optional
      if((osmNodes != null) && (osmNodes.getSubnNodes() != null) && (osmNodes.getSubnNodes().length > 1))
      {
        PFM_Node[] pnodes = osmNodes.getPerfMgrNodes();
        SBN_Node[] snodes = osmNodes.getSubnNodes();
        
        if(((pnodes != null) && (pnodes.length > 1)))
        {
          // asked to return something, so try to match up the two types of nodes
          for(SBN_Node s: snodes)
          {
            for(PFM_Node p: pnodes)
            {
              if(s.getNodeGuid().compareTo(p.getNodeGuid()) == 0)
              {
                OSM_Node n = new OSM_Node(p,s);
                nodesAll.put(n.getOSM_NodeKey(), n);
                break;
              }
            }
          }
        }
      }      
      if((nodesAll != null) && (nodesAll.size() > 0))
        return nodesAll;
     }
   return null;
}
  
  
  public static OSM_Node getOSM_Node(ArrayList <OSM_Node> allNodes, long node_guid)
  {
    // given an array of nodes, return the first node with a matching guid
    if((allNodes != null) && (allNodes.size() > 0) && (node_guid != 0))
    {
      for(OSM_Node n: allNodes)
      {
        if((n.sbnNode != null) && (n.sbnNode.node_guid == node_guid))
          return n;
        
        if((n.pfmNode != null) && (n.pfmNode.node_guid == node_guid))
          return n;
      }
    }
    return null;
  }
    
  public String getNameFromGuid(IB_Guid guid)
  {
    // loop through all the nodes and return the first description
    // that matches the guid, or null
    
    // assume the guid is correct
    if(guid == null)
      return null;
    
    long val = guid.getGuid();
    
    if((PerfMgrNodes != null) && (PerfMgrNodes.length > 0))
    {
      for(PFM_Node n: PerfMgrNodes)
      {
        if(val == n.node_guid)
          return n.getNode_name();
      }
    }
    
    if((SubnNodes != null) && (SubnNodes.length > 0))
    {
      for(SBN_Node n: SubnNodes)
      {
        if(val == n.node_guid)
          return n.description;
      }
    }
    
    // if I am here, still haven't found a matching guid,
    // perhaps this guid is for a port, not a node?
    return null;
  }

  public IB_Guid getGuidFromName(String nodeName)
  {
    // loop through all the nodes and return the first description
    // that matches the guid, or null
    
    if((PerfMgrNodes != null) && (PerfMgrNodes.length > 0) && nodeName != null)
    {
      for(PFM_Node n: PerfMgrNodes)
      {
        if(n.getNode_name().trim().equalsIgnoreCase(nodeName.trim()))
          return n.getNodeGuid();
      }
    }
    return null;
  }

    /************************************************************
   * Method Name:
   *  getSubnNodes
   **/
  /**
   * Returns the value of subnNodes.  This array can also be
   * directly referenced since its field value is public.
   *
   * @return the subnNodes
   *
   ***********************************************************/
  
  public SBN_Node[] getSubnNodes()
  {
    return SubnNodes;
  }

  @Override
  public String toString()
  {
    return "OSM_Nodes [PerfMgrNodes=" + Arrays.toString(PerfMgrNodes) + "\n\tSubnNodes="
        + Arrays.toString(SubnNodes) + "]\n";
  }

  /************************************************************
   * Method Name:
   *  getOSM_Nodes
  **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects
   *
   * @param osmNodes
   * @param filter
   * @return
   ***********************************************************/
  public static OSM_Nodes getOSM_Nodes(OSM_Nodes osmNodes, WhiteAndBlackListFilter filter)
  {
    // given a valid filter and nodes
    if((osmNodes == null) || (filter == null))
      return osmNodes;

    // return the nodes that pass through the filter
    /**  the nodes managed by the perf manager **/
    ArrayList <PFM_Node> pNodes = new ArrayList <PFM_Node>();
    /**  the nodes managed by the subnet manager **/
    ArrayList <SBN_Node> sNodes = new ArrayList <SBN_Node>();
    
    // iterate through each list, and add them only if they pass through the filter
    for(SBN_Node n: osmNodes.getSubnNodes())
        if(!filter.isFiltered(n.getNodeGuid().toColonString()))
          sNodes.add(n);
 
    for(PFM_Node p: osmNodes.getPerfMgrNodes())
        if(!filter.isFiltered(p.getNodeGuid().toColonString()))
          pNodes.add(p);
    
    PFM_Node pA [] = new PFM_Node[pNodes.size()];
    SBN_Node sA [] = new SBN_Node[sNodes.size()];

    return new OSM_Nodes(pNodes.toArray(pA), sNodes.toArray(sA));
  }   
}
