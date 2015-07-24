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
 *        file: PFM_Node.java
 *
 *  Created on: Aug 26, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import gov.llnl.lc.infiniband.core.IB_Guid;
import gov.llnl.lc.infiniband.core.NativePeerClass;

import java.io.Serializable;

/**********************************************************************
 * The <code>PFM_Node</code> represents the information the performance
 * manager maintains on each node.  To the extent that it is possible,
 * it mirrors members in the <code>struct pm_node</code>.
 * <p>
 * Java Peer Class, to the native interface.  All peer classes contain
 * public members which can be directly accessed.  All peer classes
 * have fully parameterized constructors that are used by the native
 * layer to create an instance of the class.  Instances of peer classes
 * are not intended to be created from the java environment.
 *
 * @see OSM_Nodes
 * @see NativePeerClass
 * 
 * @author meier3
 * 
 * @version Jul 2, 2012 10:52:00 AM
 **********************************************************************/
@NativePeerClass("v1.1")
public class PFM_Node implements Serializable
{
//  typedef struct pm_node
//  {
//    uint64_t node_guid;
//    char node_name[MAX_NODE_NAME_SIZE];
//    uint8_t num_ports;
//    boolean_t esp0;
//    boolean_t active;
//  } pm_Node_t;
  
// and also;
  
//  typedef struct db_node {
//    cl_map_item_t map_item; /* must be first */
//    uint64_t node_guid;
//    boolean_t active;       /* activly being monitored */
//    boolean_t esp0;
//    db_port_t *ports;
//    uint8_t num_ports;
//    char node_name[NODE_NAME_SIZE];
//  } db_node_t;
//

  
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = 4887391751008628476L;

  /**  the nodes name **/
  public String node_name;
  
  /**  the number of ports **/
  public short num_ports;
  
  /**  the nodes guid **/
  public long node_guid;
  
  /**  if switch port zero is used **/
  public boolean esp0;
  
  /**  if actively being monitored by the perf manager **/
  public boolean active;
  
  
  /************************************************************
   * Method Name:
   *  PFM_Node
  **/
  /**
   * The default constructor.
   * 
   ***********************************************************/
  public PFM_Node()
  {
    this("undefined pfm node", (short)0, 0L, false, false);
  }

  /************************************************************
   * Method Name:
   *  PFM_Node
  **/
  /**
   * The fully parameterized constructor used by the native layer
   * to create an instance of this peer class.
   *
   * @param node_name - the node name
   * @param num_ports - the number of ports
   * @param node_guid - the nodes guid
   ***********************************************************/
  public PFM_Node(String node_name, short num_ports, long node_guid, boolean esp0, boolean active)
  {
    super();
    this.node_name = node_name;
    this.num_ports = num_ports;
    this.node_guid = node_guid;
    this.esp0      = esp0;
    this.active    = active;
  }

  /************************************************************
   * Method Name:
   *  getNode_name
   **/
  /**
   * Returns the value of node_name
   *
   * @return the node_name
   *
   ***********************************************************/
  
  public String getNode_name()
  {
    return node_name;
  }

  /************************************************************
   * Method Name:
   *  getNum_ports
   **/
  /**
   * Returns the value of num_ports
   *
   * @return the num_ports
   *
   ***********************************************************/
  
  public short getNum_ports()
  {
    return num_ports;
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
    return new IB_Guid(node_guid);
  }

  /************************************************************
   * Method Name:
   *  isEsp0
   **/
  /**
   * Returns the value of esp0
   *
   * @return the esp0
   *
   ***********************************************************/
  
  public boolean isEsp0()
  {
    return esp0;
  }
  

  /************************************************************
   * Method Name:
   *  isActive
   **/
  /**
   * Returns the value of active
   *
   * @return the active
   *
   ***********************************************************/
  
  public boolean isActive()
  {
    return active;
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
    return "PFM_Node [node_name=" + node_name + ", num_ports=" + num_ports + ", node_guid="
        + node_guid + ", esp0=" + esp0 + ", active=" + active + "]";
  }


}
