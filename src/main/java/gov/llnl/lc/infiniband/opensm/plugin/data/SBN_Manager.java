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
 *        file: SBN_Manager.java
 *
 *  Created on: Jul 12, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import gov.llnl.lc.infiniband.core.IB_Guid;
import gov.llnl.lc.infiniband.core.NativePeerClass;

import java.io.Serializable;

/**********************************************************************
 * An <code>SBN_Manager</code>  object contains relevant information about
 * the active subnet manager.
 * To the extent that it is possible, it mirrors members in the 
 * <code>struct _ib_sm_info</code>.
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
 * @version Aug 29, 2011 2:16:12 PM
 **********************************************************************/
@NativePeerClass("v1.0")
public class SBN_Manager implements Serializable
{
//long guid
  
//  #include <complib/cl_packon.h>
//  typedef struct _ib_sm_info {
//    ib_net64_t guid;
//    ib_net64_t sm_key;
//    ib_net32_t act_count;
//    uint8_t pri_state;
//  } PACK_SUFFIX ib_sm_info_t;
  
//  refer to dump_sms in osm_console

  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = 6030459835682959860L;
  
  /**  the subnet managers priority code **/
  public short  pri_state;  /** originally priority & state, now just priority **/
  /**  Activity counter used as a heartbeat **/
  public int    act_count;

  /**  the subnet managers guid **/
  public long   guid;
  /**  the subnet managers key **/
  public long   sm_key;
  /**  the subnet managers state **/
  public String State;

  /************************************************************
   * Method Name:
   *  SBN_Manager
   */
   /**
   * The fully parameterized constructor used by the native layer
   * to create an instance of this peer class.
   *
   * @param pri_state
   * @param act_count
   * @param guid
   * @param sm_key
   * @param state
   ***********************************************************/
  public SBN_Manager(short pri_state, int act_count, long guid, long sm_key, String state)
  {
    super();
    this.pri_state = pri_state;
    this.act_count = act_count;
    this.guid = guid;
    this.sm_key = sm_key;
    State = state;
  }
  /************************************************************
   * Method Name:
   *  SBN_Manager
   */
   /**
   * Default constructor
   *
   ***********************************************************/
  public SBN_Manager()
  {
  }
  
  @Override
  public String toString()
  {
    return "SBN_Manager [pri_state=" + pri_state + ", act_count=" + act_count + ", guid=" + new IB_Guid(guid).toColonString()
        + ", sm_key=" + sm_key + ", State=" + State + "]";
  }
  
}
