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
 *        file: OSM_Node.java
 *
 *  Created on: Jan 11, 2012
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import gov.llnl.lc.infiniband.core.IB_Guid;
import gov.llnl.lc.infiniband.core.IB_Link;
import gov.llnl.lc.logging.CommonLogger;

/**********************************************************************
 * Describe purpose and responsibility of OSM_Node
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Jan 11, 2012 8:29:05 AM
 **********************************************************************/
public class OSM_Node implements Serializable, CommonLogger, Comparable<OSM_Node>
{
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = 7573828370296034537L;

  /**  the node managed by the perf manager **/
  public PFM_Node pfmNode;
  /**  the node managed by the subnet manager **/
  public SBN_Node sbnNode;    


  /************************************************************
   * Method Name:
   *  OSM_Node
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param   describe the parameters if any
   *
   * @param pfmNode
   * @param sbnNode
   ***********************************************************/
  public OSM_Node()
  {
    this(new PFM_Node(), new SBN_Node());
  }

  /************************************************************
   * Method Name:
   *  OSM_Node
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param   describe the parameters if any
   *
   * @param pfmNode
   * @param sbnNode
   ***********************************************************/
  public OSM_Node(SBN_Node sbnNode)
  {
    this(new PFM_Node(), sbnNode);
  }

  /************************************************************
   * Method Name:
   *  OSM_Node
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param   describe the parameters if any
   *
   * @param pfmNode
   * @param sbnNode
   ***********************************************************/
  public OSM_Node(PFM_Node pfmNode)
  {
    this(pfmNode, new SBN_Node());
  }

  /************************************************************
   * Method Name:
   *  OSM_Node
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param   describe the parameters if any
   *
   * @param pfmNode
   * @param sbnNode
   ***********************************************************/
  public OSM_Node(PFM_Node pfmNode, SBN_Node sbnNode)
  {
    super();
    this.pfmNode = pfmNode;
    this.sbnNode = sbnNode;
  }


  /************************************************************
   * Method Name:
   *  getNodeguid
   **/
  /**
   * Returns the value of node_guid
   *
   * @return the node_guid as an IB_Guid
   *
   ***********************************************************/
  
  public IB_Guid getNodeGuid()
  {
    //returns the node guid from either the sbnNode or pfmNode
    //  ideally, they should match and ideally they should both
    //  exist.  Prefer the sbnNode
    if(this.sbnNode != null)
      return this.sbnNode.getNodeGuid();
    
    if(this.pfmNode != null)
      return this.pfmNode.getNodeGuid();
    
    return null;
  }

  /************************************************************
   * Method Name:
   *  getOSM_Ports
  **/
  /**
   * Given a list of all the OSM_Ports that exist, this convenience
   * method will discover and return the ports associated with this
   * node.
   *
   * @see     describe related java objects  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   ***********************************************************/
  public ArrayList<OSM_Port> getOSM_Ports(OSM_Ports allPorts)
  {
    return OSM_Port.getOSM_Ports(allPorts, this.getNodeGuid());
  }
  
  public ArrayList<OSM_Port> getOSM_Ports(ArrayList<OSM_Port> aPorts)
  {
    return OSM_Port.getOSM_Ports(aPorts, this.getNodeGuid());
  }
  
  public ArrayList<OSM_Port> getOSM_Ports(HashMap <String, OSM_Port> pMap)
  {
    return OSM_Port.getOSM_Ports(pMap, this.getNodeGuid(), this.sbnNode.num_ports);
  }
  
  public ArrayList<IB_Link> getIB_Links(HashMap <String, IB_Link> lMap)
  {
    return IB_Link.getIB_Links(lMap, this.getNodeGuid());
  }
  
  /************************************************************
   * Method Name:
   *  compareTo
  **/
  /**
   * OSM_Nodes are considered to be the same, if both types of nodes
   * exist, and if the guids match
   *
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   * @param   describe the parameters
   *
   * @return  describe the value returned
   ***********************************************************/
  
  @Override
  public int compareTo(OSM_Node node)
  {    
    // just comparing the guids for now
    //
    if(node == null)
      return -1;
    
    if(this.getNodeGuid() != null)
    {
      return this.getNodeGuid().compareTo(node.getNodeGuid());
    }
    return -1;
  }

  @Override
  public boolean equals(Object obj) {
    return ((obj != null) && (obj instanceof OSM_Node) && (this.compareTo((OSM_Node)obj)==0));
  }


  public String getOSM_NodeKey()
  {
    return OSM_Node.getOSM_NodeKey(this);      
  }
  
  public static String getOSM_NodeKey(long guid)
  {
    return (new IB_Guid(guid)).toColonString();      
  }
  
  public static String getOSM_NodeKey(OSM_Node n)
  {
    // use the subnet
    if((n == null) || (n.sbnNode == null))
      return null;
    
    return OSM_Node.getOSM_NodeKey(n.sbnNode.node_guid);      
  }
  
  public boolean isSwitch()
  {
    return OSM_NodeType.isSwitchNode(sbnNode);
  }

  
  /************************************************************
   * Method Name:
   *  toString
  **/
  /**
   * Describe the method here
   *
   * @see java.lang.Object#toString()
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @return
   ***********************************************************/
  
   public String toVerboseString()
  {
    return "OSM_Node [pfmNode=" + pfmNode + ", sbnNode=" + sbnNode + "]";
  }

  /************************************************************
   * Method Name:
   *  toString
  **/
  /**
   * Describe the method here
   *
   * @see java.lang.Object#toString()
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @return
   ***********************************************************/
  
  @Override
  public String toString()
  {
    return "OSM_Node [pfmNode=" + pfmNode + ", sbnNode=" + sbnNode + "]";
  }

}
