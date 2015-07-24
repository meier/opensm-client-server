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
 *        file: SBN_Router.java
 *
 *  Created on: Jul 12, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import gov.llnl.lc.infiniband.core.IB_Guid;
import gov.llnl.lc.infiniband.core.NativePeerClass;

import java.io.Serializable;

/**********************************************************************
 * An <code>SBN_Router</code> object contains relevant information about
 * any routers in the subnet.
 * To the extent that it is possible, it mirrors members in the 
 * <code>struct osm_router</code>.
 * <p>
 * Java Peer Class, to the native interface.  All peer classes contain
 * public members which can be directly accessed.  All peer classes
 * have fully parameterized constructors that are used by the native
 * layer to create an instance of the class.  Instances of peer classes
 * are not intended to be created from the java environment.
 * <p>
 * @see OSM_Subnet
 * @see NativePeerClass
 *
 * @author meier3
 * 
 * @version Aug 29, 2011 2:25:59 PM
 **********************************************************************/
@NativePeerClass("v1.0")
public class SBN_Router implements Serializable
{
//  long guid
  
//  typedef struct osm_router {
//    cl_map_item_t map_item;
//    osm_port_t *p_port;
//  } osm_router_t;

  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = 7198728536714134832L;
  
  /**  the routers guid **/
  public long guid;

  /************************************************************
   * Method Name:
   *  SBN_Router
   */
   /**
   * The fully parameterized constructor used by the native layer
   * to create an instance of this peer class.
   *
   * @param guid the routers guid
   ***********************************************************/
  public SBN_Router(long guid)
  {
    super();
    this.guid = guid;
  }

  /************************************************************
   * Method Name:
   *  SBN_Router
   */
   /**
   * Default constructor
   *
   ***********************************************************/
  public SBN_Router()
  {
  }
  
  @Override
  public String toString()
  {
    return "SBN_Router [guid=" + new IB_Guid(guid).toColonString() + "]";
  }
  
}
