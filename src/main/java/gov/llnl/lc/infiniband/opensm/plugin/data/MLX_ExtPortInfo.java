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
 *        file: MLX_ExtPortInfo.java
 *
 *  Created on: Nov 13, 2014
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import gov.llnl.lc.infiniband.core.NativePeerClass;

import java.io.Serializable;

/**********************************************************************
 * Describe purpose and responsibility of MLX_ExtPortInfo
 * An <code>MLX_ExtPortInfo</code> represents the extended melanox information
 * maintained for each port.  To the extent that it is possible,
 * it mirrors members in the <code>struct _ib_mlnx_ext_port_info</code>.
 * <p>
 * Java Peer Class, to the native interface.  All peer classes contain
 * public members which can be directly accessed.  All peer classes
 * have fully parameterized constructors that are used by the native
 * layer to create an instance of the class.  Instances of peer classes
 * are not intended to be created from the java environment.
 * <p>
 * @see  OSM_Ports
 * @see SBN_Port
 * @see NativePeerClass
 *
 * @author meier3
 * 
 * @version Nov 13, 2014 11:48:34 AM
 **********************************************************************/
@NativePeerClass("v2.0")
public class MLX_ExtPortInfo implements Serializable
{
  
//  typedef struct mlnx_port_info 
//  {
//          uint8_t state_change_enable;
//          uint8_t link_speed_supported;
//          uint8_t link_speed_enabled;
//          uint8_t link_speed_active;
//  } mlnx_port_info_t;
//

  
  
//  typedef struct _ib_mlnx_ext_port_info {
//    uint8_t resvd1[3];
//    uint8_t state_change_enable;
//    uint8_t resvd2[3];
//    uint8_t link_speed_supported;
//    uint8_t resvd3[3];
//    uint8_t link_speed_enabled;
//    uint8_t resvd4[3];
//    uint8_t link_speed_active;
//    uint8_t resvd5[48];
//  } PACK_SUFFIX ib_mlnx_ext_port_info_t;
//

  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = -5966340958954299076L;
  
  public short state_change_enable;
  public short link_speed_supported;
  public short link_speed_enabled;
  public short link_speed_active;
  
  
  /************************************************************
   * Method Name:
   *  MLX_ExtPortInfo
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   ***********************************************************/
  public MLX_ExtPortInfo()
  {
  }


  /************************************************************
   * Method Name:
   *  MLX_ExtPortInfo
  **/
  /**
 *  The fully parameterized constructor used by the native layer
 * to create an instance of this peer class.
   *
   * @see     SBN_Port
   *
   * @param state_change_enable
   * @param link_speed_supported
   * @param link_speed_enabled
   * @param link_speed_active
   ***********************************************************/
  public MLX_ExtPortInfo(short state_change_enable, short link_speed_supported,
      short link_speed_enabled, short link_speed_active)
  {
    super();
    this.state_change_enable = state_change_enable;
    this.link_speed_supported = link_speed_supported;
    this.link_speed_enabled = link_speed_enabled;
    this.link_speed_active = link_speed_active;
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
    return "MLX_ExtPortInfo [state_change_enable=" + state_change_enable
        + ", link_speed_supported=" + link_speed_supported + ", link_speed_enabled="
        + link_speed_enabled + ", link_speed_active=" + link_speed_active + "]";
  }
  
  
}
