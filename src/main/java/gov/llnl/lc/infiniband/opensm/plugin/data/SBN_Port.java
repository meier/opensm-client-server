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
 *        file: SBN_Port.java
 *
 *  Created on: Jul 11, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import gov.llnl.lc.infiniband.core.IB_Guid;
import gov.llnl.lc.infiniband.core.NativePeerClass;

import java.io.Serializable;

/**********************************************************************
 * An <code>SBN_Port</code> represents the information the subnet manager
 * maintains on each port.  To the extent that it is possible,
 * it mirrors members in the <code>struct pt_port</code> and the
 * <code>struct _ib_port_info</code>.
 * <p>
 * Java Peer Class, to the native interface.  All peer classes contain
 * public members which can be directly accessed.  All peer classes
 * have fully parameterized constructors that are used by the native
 * layer to create an instance of the class.  Instances of peer classes
 * are not intended to be created from the java environment.
 * <p>
 * @see  OSM_Ports
 * @see SBN_PortInfo
 * @see NativePeerClass
 *
 * @author meier3
 * 
 * @version Nov 13, 2011 4:42:40 PM
 **********************************************************************/
@NativePeerClass("v2.0")
public class SBN_Port implements Serializable
{

/**  describe serialVersionUID here **/
  private static final long serialVersionUID = -8434956932006260704L;
  
//  typedef struct pt_port
//  {
//    uint64_t node_guid;
//    uint64_t port_guid;
//    uint8_t port_num;
//    ib_port_info_t port_info;
//
//    mlnx_port_info_t ext_port_info;
//
//    uint64_t linked_node_guid;
//    uint64_t linked_port_guid;
//    uint8_t linked_port_num;
//  } pt_Port_t;
  
  /**  this ports number **/
  public short port_num;
  /**  the number of the port connected to this port **/
  public short linked_port_num;
  /**  the node's guid that owns this port **/
  public long node_guid;
  /**  the port's guid **/
  public long port_guid;
  /**  the guid of the node connected to this port **/
  public long linked_node_guid;
  /**  the guid of the port connected to this port **/
  public long linked_port_guid;
  /**  the peer class containing information about this port **/
  public SBN_PortInfo port_info;
  /**  the peer class containing extended information about this port **/
  public MLX_ExtPortInfo ext_port_info;

  /************************************************************
   * Method Name:
   *  SBN_Port
   */
   /**
   * Default constructor.
   *
   ***********************************************************/
  public SBN_Port()
  {
  }
  
  /************************************************************
   * Method Name:
   *  SBN_Port
  **/
  /**
 *  The fully parameterized constructor used by the native layer
 * to create an instance of this peer class.
   *
   * @see     SBN_PortInfo
   *
   * @param port_num the port number
   * @param linked_port_num if linked to a port, its number
   * @param node_guid this ports node guid
   * @param port_guid this ports guid
   * @param linked_node_guid if linked to a port, its node guid
   * @param linked_port_guid if linked to a port, its guid
   * @param port_info a peer class containing port information
   * @param ext_port_info a peer class containing extended port information
   ***********************************************************/
  public SBN_Port(short port_num, short linked_port_num, long node_guid, long port_guid,
      long linked_node_guid, long linked_port_guid, SBN_PortInfo port_info,
      MLX_ExtPortInfo ext_port_info)
  {
    super();
    this.port_num = port_num;
    this.linked_port_num = linked_port_num;
    this.node_guid = node_guid;
    this.port_guid = port_guid;
    this.linked_node_guid = linked_node_guid;
    this.linked_port_guid = linked_port_guid;
    this.port_info = port_info;
    this.ext_port_info = ext_port_info;
  }

  /************************************************************
   * Method Name:
   *  SBN_Port
   */
   /**
   * The copy constructor.  Don't copy the port_info objects.
   *
   ***********************************************************/
  public SBN_Port(SBN_Port oPort)
  {
    this(oPort.port_num, oPort.linked_port_num, oPort.node_guid, oPort.port_guid,
        oPort.linked_node_guid, oPort.linked_port_guid, null, null);
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
  
  @Override
  public String toString()
  {
    return "SBN_Port [port_num=" + port_num + ", linked_port_num=" + linked_port_num
        + ", node_guid=" + new IB_Guid(node_guid).toColonString() + ", port_guid=" + new IB_Guid(port_guid).toColonString() + ", linked_node_guid="
        + new IB_Guid(linked_node_guid).toColonString() + ", linked_port_guid=" + new IB_Guid(linked_port_guid).toColonString() + ", port_info=" + port_info
        + ", ext_port_info=" + ext_port_info + "]";
  }
  
  
}
