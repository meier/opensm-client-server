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
 *        file: OSM_Subnet.java
 *
 *  Created on: Jul 12, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import gov.llnl.lc.infiniband.core.IB_Guid;
import gov.llnl.lc.infiniband.core.NativePeerClass;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmClientInterface;

import java.io.Serializable;
import java.util.Arrays;

/**********************************************************************
 * An <code>OSM_Subnet</code> object is a top level representation of
 * the subnet.  To the extent that it is possible, it mirrors members
 * in the <code>struct osm_subn</code>.
 * <p>
 * Java Peer Class, to the native interface.  All peer classes contain
 * public members which can be directly accessed.  All peer classes
 * have fully parameterized constructors that are used by the native
 * layer to create an instance of the class.  Instances of peer classes
 * are not intended to be created from the java environment.
 * <p>
 * @see  OsmClientInterface#getOsmSubnet()
 * @see NativePeerClass
 *
 * @author meier3
 * 
 * @version Nov 3, 2014 12:56:50 PM
 **********************************************************************/
@NativePeerClass("v2.0")
public class OSM_Subnet implements Serializable
{
  /* new version */
//  typedef struct osm_subn {
//    struct osm_opensm *p_osm;
//    cl_qmap_t sw_guid_tbl;
//    cl_qmap_t node_guid_tbl;
//    cl_qmap_t port_guid_tbl;
//    cl_qmap_t alias_port_guid_tbl;
//    cl_qmap_t assigned_guids_tbl;
//    cl_qmap_t rtr_guid_tbl;
//    cl_qlist_t prefix_routes_list;
//    cl_qmap_t prtn_pkey_tbl;
//    cl_qmap_t sm_guid_tbl;
//    cl_qlist_t sa_sr_list;
//    cl_qlist_t sa_infr_list;
//    cl_qlist_t alias_guid_list;
//    cl_ptr_vector_t port_lid_tbl;
//    ib_net16_t master_sm_base_lid;
//    ib_net16_t sm_base_lid;
//    ib_net64_t sm_port_guid;
//    uint8_t last_sm_port_state;
//    uint8_t sm_state;
//    osm_subn_opt_t opt;
//    struct osm_qos_policy *p_qos_policy;
//    uint16_t max_ucast_lid_ho;
//    uint16_t max_mcast_lid_ho;
//    uint8_t min_ca_mtu;
//    uint8_t min_ca_rate;
//    uint8_t min_data_vls;
//    uint8_t min_sw_data_vls;
//    boolean_t ignore_existing_lfts;
//    boolean_t subnet_initialization_error;
//    boolean_t force_heavy_sweep;
//    boolean_t force_reroute;
//    boolean_t in_sweep_hop_0;
//    boolean_t first_time_master_sweep;
//    boolean_t set_client_rereg_on_sweep;
//    boolean_t coming_out_of_standby;
//    boolean_t sweeping_enabled;
//    unsigned need_update;
//    cl_fmap_t mgrp_mgid_tbl;
//    osm_db_domain_t *p_g2m;
//    osm_db_domain_t *p_neighbor;
//    void *mboxes[IB_LID_MCAST_END_HO - IB_LID_MCAST_START_HO + 1];
//
  
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = -8443659267720036470L;
  
  public boolean             ignore_existing_lfts;
  public boolean             subnet_initialization_error;
  public boolean             force_heavy_sweep;
  public boolean             force_reroute;
  public boolean             in_sweep_hop_0;
  public boolean             first_time_master_sweep;
  public boolean             set_client_rereg_on_sweep;
  public boolean             coming_out_of_standby;
  public boolean             sweeping_enabled;

  public short               min_ca_mtu;
  public short               min_ca_rate;
  public short               min_data_vls;
  public short               min_sw_data_vls;
  public short               need_update;
  public short               sm_state;
  public short               last_sm_port_state;

  public int                 max_ucast_lid_ho;
  public int                 max_mcast_lid_ho;
  public int                 master_sm_base_lid;
  public int                 sm_base_lid;

  public long                sm_port_guid;

  public SBN_Options         Options;
  public SBN_Manager []      Managers;
  public SBN_Router []       Routers;
  public SBN_Switch []       Switches;
  public SBN_PartitionKey [] PKeys;
  
  /** List of all known Multicast Groups (I believe the index may be the mgid)  **/
  public SBN_MulticastGroup [] MCGroups;

  
  /************************************************************
   * Method Name:
   *  OSM_Subnet
  **/
  /**
   * The fully parameterized constructor used by the native layer
   * to create an instance of this peer class.
   *
   *
   * @param ignore_existing_lfts
   * @param subnet_initialization_error
   * @param force_heavy_sweep
   * @param force_reroute
   * @param in_sweep_hop_0
   * @param first_time_master_sweep
   * @param set_client_rereg_on_sweep
   * @param coming_out_of_standby
   * @param sweeping_enabled
   * @param min_ca_mtu
   * @param min_ca_rate
   * @param min_data_vls
   * @param min_sw_data_vls
   * @param need_update
   * @param sm_state
   * @param last_sm_port_state
   * @param max_ucast_lid_ho
   * @param max_mcast_lid_ho
   * @param master_sm_base_lid
   * @param sm_base_lid
   * @param sm_port_guid
   * @param options
   * @param managers
   * @param routers
   * @param switches
   * @param pKeys
   * @param mCGroups
   ***********************************************************/
  public OSM_Subnet(boolean ignore_existing_lfts, boolean subnet_initialization_error,
      boolean force_heavy_sweep, boolean force_reroute, boolean in_sweep_hop_0,
      boolean first_time_master_sweep, boolean set_client_rereg_on_sweep,
      boolean coming_out_of_standby, boolean sweeping_enabled, short min_ca_mtu, short min_ca_rate,
      short min_data_vls, short min_sw_data_vls, short need_update, short sm_state,
      short last_sm_port_state, int max_ucast_lid_ho, int max_mcast_lid_ho, int master_sm_base_lid,
      int sm_base_lid, long sm_port_guid, SBN_Options options, SBN_Manager[] managers,
      SBN_Router[] routers, SBN_Switch[] switches, SBN_PartitionKey[] pKeys,
      SBN_MulticastGroup[] mCGroups)
  {
    super();
    this.ignore_existing_lfts = ignore_existing_lfts;
    this.subnet_initialization_error = subnet_initialization_error;
    this.force_heavy_sweep = force_heavy_sweep;
    this.force_reroute = force_reroute;
    this.in_sweep_hop_0 = in_sweep_hop_0;
    this.first_time_master_sweep = first_time_master_sweep;
    this.set_client_rereg_on_sweep = set_client_rereg_on_sweep;
    this.coming_out_of_standby = coming_out_of_standby;
    this.sweeping_enabled = sweeping_enabled;
    this.min_ca_mtu = min_ca_mtu;
    this.min_ca_rate = min_ca_rate;
    this.min_data_vls = min_data_vls;
    this.min_sw_data_vls = min_sw_data_vls;
    this.need_update = need_update;
    this.sm_state = sm_state;
    this.last_sm_port_state = last_sm_port_state;
    this.max_ucast_lid_ho = max_ucast_lid_ho;
    this.max_mcast_lid_ho = max_mcast_lid_ho;
    this.master_sm_base_lid = master_sm_base_lid;
    this.sm_base_lid = sm_base_lid;
    this.sm_port_guid = sm_port_guid;
    Options = options;
    Managers = managers;
    Routers = routers;
    Switches = switches;
    PKeys = pKeys;
    MCGroups = mCGroups;
  }

  /************************************************************
   * Method Name:
   *  OSM_Subnet
   */
   /**
   * Default constructor
   *
   ***********************************************************/
   public OSM_Subnet()
  {
  }
  
  /************************************************************
   * Method Name:
   *  getMulticastGroup
  **/
  /**
   * I believe the MCast groups are indexed via MGIDS, so this just
   * returns the MCast group at the provided index.  MLIDS are 
   * contained within the groups, and appear to be something different,
   * but I am not sure of the distinction.
   *
   * @see     describe related java objects
   *
   * @param mGid
   * @return
   ***********************************************************/
  SBN_MulticastGroup getMulticastGroup(int mGid)
  {
    if((MCGroups != null) && (MCGroups.length > mGid) && (mGid >= 0))
      return MCGroups[mGid];
    return null;
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
    return "OSM_Subnet [ignore_existing_lfts=" + ignore_existing_lfts
        + ", subnet_initialization_error=" + subnet_initialization_error + ", force_heavy_sweep="
        + force_heavy_sweep + ", force_reroute=" + force_reroute + ", in_sweep_hop_0="
        + in_sweep_hop_0 + ", first_time_master_sweep=" + first_time_master_sweep
        + ", set_client_rereg_on_sweep=" + set_client_rereg_on_sweep + ", coming_out_of_standby="
        + coming_out_of_standby + ", sweeping_enabled=" + sweeping_enabled + ", min_ca_mtu="
        + min_ca_mtu + ", min_ca_rate=" + min_ca_rate + ", min_data_vls=" + min_data_vls
        + ", min_sw_data_vls=" + min_sw_data_vls + ", need_update=" + need_update + ", sm_state="
        + sm_state + ", last_sm_port_state=" + last_sm_port_state + ", max_ucast_lid_ho="
        + max_ucast_lid_ho + ", max_mcast_lid_ho=" + max_mcast_lid_ho + ", master_sm_base_lid="
        + master_sm_base_lid + ", sm_base_lid=" + sm_base_lid + ", sm_port_guid=" + sm_port_guid
        + ", Options=" + Options + ", Managers=" + Arrays.toString(Managers) + ", Routers="
        + Arrays.toString(Routers) + ", Switches=" + Arrays.toString(Switches) + ", PKeys="
        + Arrays.toString(PKeys) + ", MCGroups=" + Arrays.toString(MCGroups) + "]";
  }

  public boolean isSwitch(IB_Guid g)
  {
    if ((Switches != null) && (Switches.length > 0))
    {
      for(SBN_Switch s: Switches)
      {
        // does the guid match?
        if(s.guid == g.getGuid())
          return true;
      }
    }
    return false;
  }

 
}
