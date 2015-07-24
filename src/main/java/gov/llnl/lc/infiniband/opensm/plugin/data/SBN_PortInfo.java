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
 *        file: SBN_PortInfo.java
 *
 *  Created on: Jul 11, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import gov.llnl.lc.infiniband.core.NativePeerClass;

import java.io.Serializable;

/**********************************************************************
 * An <code>SBN_PortInfo</code> represents the information the subnet manager
 * maintains on each port.  To the extent that it is possible,
 * it mirrors members in the <code>struct _ib_port_info</code>.
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
 * @version Oct 29, 2014 11:48:34 AM
 **********************************************************************/
@NativePeerClass("v2.0")
public class SBN_PortInfo implements Serializable
{
//  typedef struct _ib_port_info {
//    ib_net64_t m_key;
//    ib_net64_t subnet_prefix;
//    ib_net16_t base_lid;
//    ib_net16_t master_sm_base_lid;
//    ib_net32_t capability_mask;
//    ib_net16_t diag_code;
//    ib_net16_t m_key_lease_period;
//    uint8_t local_port_num;
//    uint8_t link_width_enabled;
//    uint8_t link_width_supported;
//    uint8_t link_width_active;
//    uint8_t state_info1;  /* LinkSpeedSupported and PortState */
//    uint8_t state_info2;  /* PortPhysState and LinkDownDefaultState */
//    uint8_t mkey_lmc; /* M_KeyProtectBits and LMC */
//    uint8_t link_speed; /* LinkSpeedEnabled and LinkSpeedActive */
//    uint8_t mtu_smsl;
//    uint8_t vl_cap;   /* VLCap and InitType */
//    uint8_t vl_high_limit;
//    uint8_t vl_arb_high_cap;
//    uint8_t vl_arb_low_cap;
//    uint8_t mtu_cap;
//    uint8_t vl_stall_life;
//    uint8_t vl_enforce;
//    ib_net16_t m_key_violations;
//    ib_net16_t p_key_violations;
//    ib_net16_t q_key_violations;
//    uint8_t guid_cap;
//    uint8_t subnet_timeout; /* cli_rereg(1b), mcast_pkey_trap_suppr(1b), reserv(1b), timeout(5b) */
//    uint8_t resp_time_value; /* reserv(3b), rtv(5b) */
//    uint8_t error_threshold; /* local phy errors(4b), overrun errors(4b) */
//    ib_net16_t max_credit_hint;
//    ib_net32_t link_rt_latency; /* reserv(8b), link round trip lat(24b) */
//    ib_net16_t capability_mask2;
//    uint8_t link_speed_ext; /* LinkSpeedExtActive and LinkSpeedExtSupported */
//    uint8_t link_speed_ext_enabled; /* reserv(3b), LinkSpeedExtEnabled(5b) */
//  } PACK_SUFFIX ib_port_info_t;


  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = 3411642766538990571L;
  
  public short local_port_num;
  public short link_width_enabled;
  public short link_width_supported;
  public short link_width_active;
  public short state_info1;  /* LinkSpeedSupported and PortState */
  public short state_info2;  /* PortPhysState and LinkDownDefaultState */
  public short mkey_lmc;
  public short link_speed; /* LinkSpeedEnabled and LinkSpeedActive */
  public short link_speed_ext; /* LinkSpeedExtActive and LinkSpeedExtSupported */
  public short link_speed_ext_enabled; /* reserv(3b), LinkSpeedExtEnabled(5b) */
  public short mtu_smsl;
  public short vl_cap;   /* VLCap and InitType */
  public short vl_high_limit;
  public short vl_arb_high_cap;
  public short vl_arb_low_cap;
  public short mtu_cap;
  public short vl_stall_life;
  public short vl_enforce;
  public short guid_cap;
  public short subnet_timeout; /* cli_rereg(1b), mcast_pkey_trap_suppr(1b), resrv(1b), timeout(5b) */
  public short resp_time_value;
  public short error_threshold; /* local phy errors(4b), overrun errors(4b) */
  public int base_lid;          /*********** this is the LID ***************/
  public int master_sm_base_lid;
  public int capability_mask;
  public int capability_mask2;
  public int diag_code;
  public int m_key_lease_period;
  public int m_key_violations;
  public int p_key_violations;
  public int q_key_violations;
  public int max_credit_hint;
  public int link_rt_latency; /* reserv(8b), link round trip lat(24b) */
  public long m_key;
  public long subnet_prefix;

  /************************************************************
   * Method Name:
   *  SBN_PortInfo
   */
   /**
   * Default constructor
   *
   ***********************************************************/
  public SBN_PortInfo()
  {
  }
  /************************************************************
   * Method Name:
   *  SBN_PortInfo
  **/
  /**
 *  The fully parameterized constructor used by the native layer
 * to create an instance of this peer class.
   *
   * @see     SBN_Port
   *
   * @param local_port_num
   * @param link_width_enabled
   * @param link_width_supported
   * @param link_width_active
   * @param state_info1
   * @param state_info2
   * @param mkey_lmc
   * @param link_speed
   * @param link_speed_ext
   * @param link_speed_ext_enabled
   * @param mtu_smsl
   * @param vl_cap
   * @param vl_high_limit
   * @param vl_arb_high_cap
   * @param vl_arb_low_cap
   * @param mtu_cap
   * @param vl_stall_life
   * @param vl_enforce
   * @param guid_cap
   * @param subnet_timeout
   * @param resp_time_value
   * @param error_threshold
   * @param base_lid
   * @param master_sm_base_lid
   * @param capability_mask
   * @param capability_mask2
   * @param diag_code
   * @param m_key_lease_period
   * @param m_key_violations
   * @param p_key_violations
   * @param q_key_violations
   * @param max_credit_hint
   * @param link_rt_latency
   * @param m_key
   * @param subnet_prefix
   ***********************************************************/
  public SBN_PortInfo(short local_port_num, short link_width_enabled, short link_width_supported,
      short link_width_active, short state_info1, short state_info2, short mkey_lmc,
      short link_speed, short link_speed_ext, short link_speed_ext_enabled, short mtu_smsl,
      short vl_cap, short vl_high_limit, short vl_arb_high_cap, short vl_arb_low_cap,
      short mtu_cap, short vl_stall_life, short vl_enforce, short guid_cap, short subnet_timeout,
      short resp_time_value, short error_threshold, int base_lid, int master_sm_base_lid,
      int capability_mask, int capability_mask2, int diag_code, int m_key_lease_period,
      int m_key_violations, int p_key_violations, int q_key_violations, int max_credit_hint,
      int link_rt_latency, long m_key, long subnet_prefix)
  {
    super();
    this.local_port_num = local_port_num;
    this.link_width_enabled = link_width_enabled;
    this.link_width_supported = link_width_supported;
    this.link_width_active = link_width_active;
    this.state_info1 = state_info1;
    this.state_info2 = state_info2;
    this.mkey_lmc = mkey_lmc;
    this.link_speed = link_speed;
    this.link_speed_ext = link_speed_ext;
    this.link_speed_ext_enabled = link_speed_ext_enabled;
    this.mtu_smsl = mtu_smsl;
    this.vl_cap = vl_cap;
    this.vl_high_limit = vl_high_limit;
    this.vl_arb_high_cap = vl_arb_high_cap;
    this.vl_arb_low_cap = vl_arb_low_cap;
    this.mtu_cap = mtu_cap;
    this.vl_stall_life = vl_stall_life;
    this.vl_enforce = vl_enforce;
    this.guid_cap = guid_cap;
    this.subnet_timeout = subnet_timeout;
    this.resp_time_value = resp_time_value;
    this.error_threshold = error_threshold;
    this.base_lid = base_lid;
    this.master_sm_base_lid = master_sm_base_lid;
    this.capability_mask = capability_mask;
    this.capability_mask2 = capability_mask2;
    this.diag_code = diag_code;
    this.m_key_lease_period = m_key_lease_period;
    this.m_key_violations = m_key_violations;
    this.p_key_violations = p_key_violations;
    this.q_key_violations = q_key_violations;
    this.max_credit_hint = max_credit_hint;
    this.link_rt_latency = link_rt_latency;
    this.m_key = m_key;
    this.subnet_prefix = subnet_prefix;
  }

  /************************************************************
   * Method Name:
   *  toString
  **/
  /**
   * A simple default method to show the native members.
   *
   * @see java.lang.Object#toString()
   *
   * @return
   ***********************************************************/
  
  @Override
  public String toString()
  {
    return "SBN_PortInfo [local_port_num=" + local_port_num + ", link_width_enabled="
        + link_width_enabled + ", link_width_supported=" + link_width_supported
        + ", link_width_active=" + link_width_active + ", state_info1=" + state_info1
        + ", state_info2=" + state_info2 + ", mkey_lmc=" + mkey_lmc + ", link_speed=" + link_speed
        + ", link_speed_ext=" + link_speed_ext + ", link_speed_ext_enabled="
        + link_speed_ext_enabled + ", mtu_smsl=" + mtu_smsl + ", vl_cap=" + vl_cap
        + ", vl_high_limit=" + vl_high_limit + ", vl_arb_high_cap=" + vl_arb_high_cap
        + ", vl_arb_low_cap=" + vl_arb_low_cap + ", mtu_cap=" + mtu_cap + ", vl_stall_life="
        + vl_stall_life + ", vl_enforce=" + vl_enforce + ", guid_cap=" + guid_cap
        + ", subnet_timeout=" + subnet_timeout + ", resp_time_value=" + resp_time_value
        + ", error_threshold=" + error_threshold + ", base_lid=" + base_lid
        + ", master_sm_base_lid=" + master_sm_base_lid + ", capability_mask=" + capability_mask
        + ", capability_mask2=" + capability_mask2 + ", diag_code=" + diag_code
        + ", m_key_lease_period=" + m_key_lease_period + ", m_key_violations=" + m_key_violations
        + ", p_key_violations=" + p_key_violations + ", q_key_violations=" + q_key_violations
        + ", max_credit_hint=" + max_credit_hint + ", link_rt_latency=" + link_rt_latency
        + ", m_key=" + m_key + ", subnet_prefix=" + subnet_prefix + "]";
  }  
  
}
