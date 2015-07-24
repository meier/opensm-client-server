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

import gov.llnl.lc.infiniband.core.IB_Guid;
import gov.llnl.lc.infiniband.core.NativePeerClass;

import java.io.Serializable;

/**********************************************************************
 * A <code>SBN_Node</code> represents the information the subnet manager
 * maintains on each node.  To the extent that it is possible,
 * it mirrors members in the <code>struct pt_node</code> and the
 * <code>struct _ib_node_info</code>.
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
 * @version Aug 26, 2011 1:59:21 PM
 **********************************************************************/
@NativePeerClass("v1.0")
public class SBN_Node implements Serializable
{
/**  describe serialVersionUID here **/
  private static final long serialVersionUID = 1625262988003906134L;

//  typedef struct pt_node
//  {
//    ib_node_info_t node_info;
//    uint8_t description[IB_NODE_DESCRIPTION_SIZE];
//  } pt_Node_t;
  
//  typedef struct _ib_node_info {
//    uint8_t base_version;
//    uint8_t class_version;
//    uint8_t node_type;
//    uint8_t num_ports;
//    ib_net64_t sys_guid;
//    ib_net64_t node_guid;
//    ib_net64_t port_guid;
//    ib_net16_t partition_cap;
//    ib_net16_t device_id;
//    ib_net32_t revision;
//    ib_net32_t port_num_vendor_id;
//  } PACK_SUFFIX ib_node_info_t;
  
/**  the node description **/
public String description;

/**  describe base_version here **/
public short base_version;

/**  describe class_version here **/
public short class_version;

/**  describe node_type here **/
public short node_type;

/**  describe num_ports here **/
public short num_ports;

/**  describe partition_cap here **/
public int partition_cap;

/**  describe device_id here **/
public int device_id;

/**  describe revision here **/
public int revision;

/**  describe port_num_vendor_id here **/
public int port_num_vendor_id;

/**  the system guid, which is often only available for large switches (collection of switches) **/
public long sys_guid;

/**  if different from the port_guid, corresponds to the port 0 guid, meaning this node is a switch **/
public long node_guid;

/**  the port guid **/
public long port_guid;



/************************************************************
 * Method Name:
 *  SBN_Node
 */
 /** The default constructor
 *
 ***********************************************************/
public SBN_Node()
{
  this("undefined sbn node", (short)0, (short)0, (short)0, (short)0, 0, 0, 0, 0, 0L, 0L, 0L);
}

/************************************************************
 * Method Name:
 *  SBN_Node
 */
 /**
 *  The fully parameterized constructor used by the native layer
 * to create an instance of this peer class.
 *
 * @param description the node description
 * @param base_version version type
 * @param class_version class type
 * @param node_type node type
 * @param num_ports the number of ports
 * @param partition_cap
 * @param device_id
 * @param revision
 * @param port_num_vendor_id
 * @param sys_guid
 * @param node_guid
 * @param port_guid
 ***********************************************************/
public SBN_Node(String description, short base_version, short class_version, short node_type,
    short num_ports, int partition_cap, int device_id, int revision, int port_num_vendor_id,
    long sys_guid, long node_guid, long port_guid)
{
  super();
  this.description = description;
  this.base_version = base_version;
  this.class_version = class_version;
  this.node_type = node_type;
  this.num_ports = num_ports;
  this.partition_cap = partition_cap;
  this.device_id = device_id;
  this.revision = revision;
  this.port_num_vendor_id = port_num_vendor_id;
  this.sys_guid = sys_guid;
  this.node_guid = node_guid;
  this.port_guid = port_guid;
}


/************************************************************
 * Method Name:
 *  getSysGuid
 */
/** Returns the value of sys_guid
 *
 * @return the sys_guid
 ***********************************************************/

public IB_Guid getSysGuid()
{
  return new IB_Guid(sys_guid);
}

/************************************************************
 * Method Name:
 *  getNodeGuid
 */
/** Returns the value of node_guid
 *
 * @return the node_guid
 ***********************************************************/

public IB_Guid getNodeGuid()
{
  return new IB_Guid(node_guid);
}

/************************************************************
 * Method Name:
 *  getPortGuid
 */
/** Returns the value of port_guid
 *
 * @return the port_guid
 ***********************************************************/

public IB_Guid getPortGuid()
{
  return new IB_Guid(port_guid);
}

@Override
public String toString()
{
  return "SBN_Node [description=" + description + "\n\tbase_version=" + Long.toHexString(base_version).trim()
      + "\n\tclass_version=" + Long.toHexString(class_version).trim() + "\n\tnode_type=" + node_type + "\n\tnum_ports="
      + num_ports + "\n\tpartition_cap=" + Long.toHexString(partition_cap).trim() + "\n\tdevice_id=" + Long.toHexString(device_id).trim() + "\n\trevision="
      + Long.toHexString(revision).trim() + "\n\tport_num_vendor_id=" + Long.toHexString(port_num_vendor_id).trim() + "\n\tsys_guid=" + new IB_Guid(sys_guid).toColonString()
      + "\n\tnode_guid=" + new IB_Guid(node_guid).toColonString() + "\n\tport_guid=" + new IB_Guid(port_guid).toColonString() + "]\n";
}

}
