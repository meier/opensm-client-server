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
package gov.llnl.lc.infiniband.core;

import java.io.Serializable;

/**********************************************************************
 * An <code>IB_Port</code> is a fundamental entity in the fabric.
 * It represents a connection point, or one end of a "link".
 * This class provides the information to uniquely identify a port.
 * <p>
 * Java Peer Class, to the native interface.  All peer classes contain
 * public members which can be directly accessed.  All peer classes
 * have fully parameterized constructors that are used by the native
 * layer to create an instance of the class.
 * <p>
 * @see IB_Guid
 * @see NativePeerClass
 *
 * @author meier3
 * 
 * @version Aug 29, 2011 11:59:38 AM
 **********************************************************************/
@NativePeerClass("v1.0")
public class IB_Port implements Serializable 
{
  /*
   * typedef struct jsi_port_desc {
  struct jsi_port_desc *next;
        uint64_t node_guid;
        uint8_t port_num;
        char print_desc[IB_NODE_DESCRIPTION_SIZE + 1];
} jsi_PortDesc_t;

and the original version from osm_console.c

typedef struct _port_report {
  struct _port_report *next;
  uint64_t node_guid;
  uint8_t port_num;
  char print_desc[IB_NODE_DESCRIPTION_SIZE + 1];
} port_report_t;

   * 
   */
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = -5277509988414424070L;
  
  /**  the guid that uniquely identifies this port **/
  public IB_Guid guid;
  /**  this ports number, with respect to its parent node **/
  public int portNumber;
  /**  information describing this ports' parent node **/
  public String Description;

  /************************************************************
   * Method Name:
   *  IB_Port
  **/
  /**
 *  The fully parameterized constructor INDIRECTLY used by the
 *   native layer to create an instance of this peer class.
   *
   * @param guid the IB_Guid object for this port
   * @param portNumber the port number
   * @param description a description, usually representing the node
   ***********************************************************/
  public IB_Port(IB_Guid guid, int portNumber, String description)
  {
    super();
    this.guid = guid;
    this.portNumber = portNumber;
    Description = description;
  }
  
  /************************************************************
   * Method Name:
   *  IB_Port
   */
   /**
 *  The fully parameterized constructor used by the native layer
 * to create an instance of this peer class.
   *
   * @see     IB_Guid
   *
   * @param lguid the port guid
   * @param portNumber the port number
   * @param description a description, usually representing the node
   ***********************************************************/
  public IB_Port(long lguid, int portNumber, String description)
  {
    this(new IB_Guid(lguid), portNumber, description);
  }

  @Override
  public String toString()
  {
    return "IB_Port [guid=" + guid + ", portNumber=" + portNumber + ", Description=" + Description
        + "]";
  }
   
}
