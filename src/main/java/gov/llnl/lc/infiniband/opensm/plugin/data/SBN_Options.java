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
 *        file: SBN_Options.java
 *
 *  Created on: Jul 12, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import gov.llnl.lc.infiniband.core.NativePeerClass;

import java.io.Serializable;

/**********************************************************************
 * An <code>SBN_Options</code> object contains most (if not all) of the
 * subnets understanding of the current options, settings, and configuration.
 * To the extent that it is possible, it mirrors members in the 
 * <code>struct osm_subn_opt</code>.
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
 * @version Nov 4, 2014 1:34:31 PM
 **********************************************************************/
@NativePeerClass("v2.0")
public class SBN_Options implements Serializable
{
//  typedef struct osm_subn_opt {
//    char *config_file;
//    ib_net64_t guid;
//    ib_net64_t m_key;
//    ib_net64_t sm_key;
//    ib_net64_t sa_key;
//    ib_net64_t subnet_prefix;
//    ib_net16_t m_key_lease_period;
//    uint32_t sweep_interval;
//    uint32_t max_wire_smps;
//    uint32_t max_wire_smps2;
//    uint32_t max_smps_timeout;
//    uint32_t transaction_timeout;
//    uint32_t transaction_retries;
//    uint8_t sm_priority;
//    uint8_t lmc;
//    boolean_t lmc_esp0;
//    uint8_t max_op_vls;
//    uint8_t force_link_speed;
//    boolean_t reassign_lids;
//    boolean_t ignore_other_sm;
//    boolean_t single_thread;
//    boolean_t disable_multicast;
//    boolean_t force_log_flush;
//    uint8_t subnet_timeout;
//    uint8_t packet_life_time;
//    uint8_t vl_stall_count;
//    uint8_t leaf_vl_stall_count;
//    uint8_t head_of_queue_lifetime;
//    uint8_t leaf_head_of_queue_lifetime;
//    uint8_t local_phy_errors_threshold;
//    uint8_t overrun_errors_threshold;
//    boolean_t use_mfttop;
//    uint32_t sminfo_polling_timeout;
//    uint32_t polling_retry_number;
//    uint32_t max_msg_fifo_timeout;
//    boolean_t force_heavy_sweep;
//    uint8_t log_flags;
//    char *dump_files_dir;
//    char *log_file;
//    unsigned long log_max_size;
//    char *partition_config_file;
//    boolean_t no_partition_enforcement;
//    boolean_t qos;
//    char *qos_policy_file;
//    boolean_t accum_log_file;
//    char *console;
//    uint16_t console_port;
//    char *port_prof_ignore_file;
//    char *hop_weights_file;
//    char *dimn_ports_file;
//    boolean_t port_profile_switch_nodes;
//    boolean_t sweep_on_trap;
//    char *routing_engine_names;
//    boolean_t use_ucast_cache;
//    boolean_t connect_roots;
//    char *lid_matrix_dump_file;
//    char *lfts_file;
//    char *root_guid_file;
//    char *cn_guid_file;
//    char *io_guid_file;
//    uint16_t max_reverse_hops;
//    char *ids_guid_file;
//    char *guid_routing_order_file;
//    char *sa_db_file;
//    boolean_t sa_db_dump;
//    char *torus_conf_file;
//    boolean_t do_mesh_analysis;
//    boolean_t exit_on_fatal;
//    boolean_t honor_guid2lid_file;
//    boolean_t daemon;
//    boolean_t sm_inactive;
//    boolean_t babbling_port_policy;
//    boolean_t use_optimized_slvl;
//    osm_qos_options_t qos_options;
//    osm_qos_options_t qos_ca_options;
//    osm_qos_options_t qos_sw0_options;
//    osm_qos_options_t qos_swe_options;
//    osm_qos_options_t qos_rtr_options;
//    boolean_t enable_quirks;
//    boolean_t no_clients_rereg;
//    boolean_t no_fallback_routing_engine;
//  #ifdef ENABLE_OSM_PERF_MGR
//    boolean_t perfmgr;
//    boolean_t perfmgr_redir;
//    uint16_t perfmgr_sweep_time_s;
//    uint32_t perfmgr_max_outstanding_queries;
//    char *event_db_dump_file;
//  #endif        /* ENABLE_OSM_PERF_MGR */
//    char *event_plugin_name;
//    char *event_plugin_options;
//    char *node_name_map_name;
//    char *prefix_routes_file;
//    char *log_prefix;
//    boolean_t consolidate_ipv6_snm_req;
//    struct osm_subn_opt *file_opts; /* used for update */
//    uint8_t lash_start_vl;      /* starting vl to use in lash */
//    uint8_t sm_sl;      /* which SL to use for SM/SA communication */
//  } osm_subn_opt_t;
  
  
// new version
// this data structure can be found in osm_subnet.h
// and is implemented (in the native layer) in jni_PeerClass(.c & .h),
// jni_SharedResources(.c & .h) and  jni_Subnet.c 
//
//  typedef struct osm_subn_opt {
//    const char *config_file;
//    ib_net64_t guid;
//    char *ca_name; /* alternative to guid */
//    int ca_port; /* alternative to guid */
//    ib_net64_t m_key;
//    ib_net64_t sm_key;
//    ib_net64_t sa_key;
//    ib_net64_t subnet_prefix;
//    ib_net16_t m_key_lease_period;
//    uint8_t m_key_protect_bits;
//    boolean_t m_key_lookup;
//    uint32_t sweep_interval;
//    uint32_t max_wire_smps;
//    uint32_t max_wire_smps2;
//    uint32_t max_smps_timeout;
//    uint32_t transaction_timeout;
//    uint32_t transaction_retries;
//    uint8_t sm_priority;
//    uint8_t lmc;
//    boolean_t lmc_esp0;
//    uint8_t max_op_vls;
//    uint8_t force_link_speed;
//    uint8_t force_link_speed_ext;
//    uint8_t fdr10;
//    char *force_link_speed_file;
//    boolean_t reassign_lids;
//    boolean_t ignore_other_sm;
//    boolean_t single_thread;
//    boolean_t disable_multicast;
//    boolean_t force_log_flush;
//    uint8_t subnet_timeout;
//    uint8_t packet_life_time;
//    uint8_t vl_stall_count;
//    uint8_t leaf_vl_stall_count;
//    uint8_t head_of_queue_lifetime;
//    uint8_t leaf_head_of_queue_lifetime;
//    uint8_t local_phy_errors_threshold;
//    uint8_t overrun_errors_threshold;
//    boolean_t use_mfttop;
//    uint32_t sminfo_polling_timeout;
//    uint32_t polling_retry_number;
//    uint32_t max_msg_fifo_timeout;
//    boolean_t force_heavy_sweep;
//    uint8_t log_flags;
//    char *dump_files_dir;
//    char *log_file;
//    unsigned long log_max_size;
//    char *partition_config_file;
//    boolean_t no_partition_enforcement;
//    char *part_enforce;
//    osm_partition_enforce_type_enum part_enforce_enum;
//    boolean_t allow_both_pkeys;
//    uint8_t sm_assigned_guid;
//    boolean_t qos;
//    char *qos_policy_file;
//    boolean_t accum_log_file;
//    char *console;
//    uint16_t console_port;
//    char *port_prof_ignore_file;
//    char *hop_weights_file;
//    char *port_search_ordering_file;
//    boolean_t port_profile_switch_nodes;
//    boolean_t sweep_on_trap;
//    char *routing_engine_names;
//    boolean_t use_ucast_cache;
//    boolean_t connect_roots;
//    char *lid_matrix_dump_file;
//    char *lfts_file;
//    char *root_guid_file;
//    char *cn_guid_file;
//    char *io_guid_file;
//    boolean_t port_shifting;
//    uint32_t scatter_ports;
//    boolean_t remote_guid_sorting;
//    uint16_t max_reverse_hops;
//    char *ids_guid_file;
//    char *guid_routing_order_file;
//    boolean_t guid_routing_order_no_scatter;
//    char *sa_db_file;
//    boolean_t sa_db_dump;
//    char *torus_conf_file;
//    boolean_t do_mesh_analysis;
//    boolean_t exit_on_fatal;
//    boolean_t honor_guid2lid_file;
//    boolean_t daemon;
//    boolean_t sm_inactive;
//    boolean_t babbling_port_policy;
//    boolean_t drop_event_subscriptions;
//    boolean_t use_optimized_slvl;
//    boolean_t fsync_high_avail_files;
//    osm_qos_options_t qos_options;
//    osm_qos_options_t qos_ca_options;
//    osm_qos_options_t qos_sw0_options;
//    osm_qos_options_t qos_swe_options;
//    osm_qos_options_t qos_rtr_options;
//    boolean_t congestion_control;
//    ib_net64_t cc_key;
//    uint32_t cc_max_outstanding_mads;
//    ib_net32_t cc_sw_cong_setting_control_map;
//    uint8_t cc_sw_cong_setting_victim_mask[IB_CC_PORT_MASK_DATA_SIZE];
//    uint8_t cc_sw_cong_setting_credit_mask[IB_CC_PORT_MASK_DATA_SIZE];
//    uint8_t cc_sw_cong_setting_threshold;
//    uint8_t cc_sw_cong_setting_packet_size;
//    uint8_t cc_sw_cong_setting_credit_starvation_threshold;
//    osm_cct_entry_t cc_sw_cong_setting_credit_starvation_return_delay;
//    ib_net16_t cc_sw_cong_setting_marking_rate;
//    ib_net16_t cc_ca_cong_setting_port_control;
//    ib_net16_t cc_ca_cong_setting_control_map;
//    osm_cacongestion_entry_t cc_ca_cong_entries[IB_CA_CONG_ENTRY_DATA_SIZE];
//    osm_cct_t cc_cct;
//    boolean_t enable_quirks;
//    boolean_t no_clients_rereg;
//  #ifdef ENABLE_OSM_PERF_MGR
//    boolean_t perfmgr;
//    boolean_t perfmgr_redir;
//    uint16_t perfmgr_sweep_time_s;
//    uint32_t perfmgr_max_outstanding_queries;
//    boolean_t perfmgr_ignore_cas;
//    char *event_db_dump_file;
//    int perfmgr_rm_nodes;
//    boolean_t perfmgr_log_errors;
//    boolean_t perfmgr_query_cpi;
//    boolean_t perfmgr_xmit_wait_log;
//    uint32_t perfmgr_xmit_wait_threshold;
//  #endif        /* ENABLE_OSM_PERF_MGR */
//    char *event_plugin_name;
//    char *event_plugin_options;
//    char *node_name_map_name;
//    char *prefix_routes_file;
//    char *log_prefix;
//    boolean_t consolidate_ipv6_snm_req;
//    struct osm_subn_opt *file_opts; /* used for update */
//    uint8_t lash_start_vl;      /* starting vl to use in lash */
//    uint8_t sm_sl;      /* which SL to use for SM/SA communication */
//    char *per_module_logging_file;
//  } osm_subn_opt_t;

  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = -1066287754689004508L;

  public boolean lmc_esp0;
  public boolean reassign_lids;
  public boolean ignore_other_sm;
  public boolean single_thread;
  public boolean disable_multicast;
  public boolean force_log_flush;
  public boolean use_mfttop;
  public boolean force_heavy_sweep;
  public boolean no_partition_enforcement;
  public boolean qos;
  public boolean accum_log_file;
  public boolean port_profile_switch_nodes;
  public boolean sweep_on_trap;
  public boolean use_ucast_cache;
  public boolean connect_roots;
  public boolean sa_db_dump;
  public boolean do_mesh_analysis;
  public boolean exit_on_fatal;
  public boolean honor_guid2lid_file;
  public boolean daemon;
  public boolean sm_inactive;
  public boolean babbling_port_policy;
  public boolean use_optimized_slvl;
  public boolean enable_quirks;
  public boolean no_clients_rereg;
  public boolean perfmgr;
  public boolean perfmgr_redir;
  public boolean consolidate_ipv6_snm_req;
  public boolean  m_key_lookup;
  public boolean  allow_both_pkeys;
  public boolean  port_shifting;
  public boolean  remote_guid_sorting;
  public boolean  guid_routing_order_no_scatter;
  public boolean  drop_event_subscriptions;
  public boolean  fsync_high_avail_files;
  public boolean  congestion_control;
  public boolean  perfmgr_ignore_cas;
  public boolean  perfmgr_log_errors;
  public boolean  perfmgr_query_cpi;
  public boolean  perfmgr_xmit_wait_log;
  
  public short   sm_priority;
  public short   lmc;
  public short   max_op_vls;
  public short   force_link_speed;
  public short   subnet_timeout;
  public short   packet_life_time;
  public short   vl_stall_count;
  public short   leaf_vl_stall_count;
  public short   head_of_queue_lifetime;
  public short   leaf_head_of_queue_lifetime;
  public short   local_phy_errors_threshold;
  public short   overrun_errors_threshold;
  public short   log_flags;
  public short   lash_start_vl;
  public short   sm_sl;
  public short m_key_protect_bits;
  public short force_link_speed_ext;
  public short fdr10;
  public short sm_assigned_guid;
  public short cc_sw_cong_setting_threshold;
  public short cc_sw_cong_setting_packet_size;
  public short cc_sw_cong_setting_credit_starvation_threshold;
  
  public int     m_key_lease_period;
  public int     sweep_interval;
  public int     max_wire_smps;
  public int     max_wire_smps2;
  public int     max_smps_timeout;
  public int     transaction_timeout;
  public int     transaction_retries;
  public int     sminfo_polling_timeout;
  public int     polling_retry_number;
  public int     max_msg_fifo_timeout;
  public int     console_port;
  public int     max_reverse_hops;
  public int     perfmgr_sweep_time_s;
  public int     perfmgr_max_outstanding_queries;
  public int ca_port; /* alternative to guid */
  public int part_enforce_enum;
  public int scatter_ports;
  public int cc_max_outstanding_mads;
  public int cc_sw_cong_setting_control_map;
  public int cc_sw_cong_setting_marking_rate;
  public int cc_ca_cong_setting_port_control;
  public int cc_ca_cong_setting_control_map;
  public int  perfmgr_rm_nodes;
  public int  perfmgr_xmit_wait_threshold;

  public long    guid;
  public long    m_key;
  public long    sm_key;
  public long    sa_key;
  public long    subnet_prefix;
  public long    log_max_size;
  public long    cc_key;

  public String  config_file;
  public String  dump_files_dir;
  public String  log_file;
  public String  partition_config_file;
  public String  qos_policy_file;
  public String  console;
  public String  port_prof_ignore_file;
  public String  hop_weights_file;
  public String  routing_engine_names;
  public String  lid_matrix_dump_file;
  public String  lfts_file;
  public String  root_guid_file;
  public String  cn_guid_file;
  public String  io_guid_file;
  public String  ids_guid_file;
  public String  guid_routing_order_file;
  public String  sa_db_file;
  public String  torus_conf_file;
  public String  event_db_dump_file;
  public String  event_plugin_name;
  public String  event_plugin_options;
  public String  node_name_map_name;
  public String  prefix_routes_file;
  public String  log_prefix;
  public String  ca_name; /* alternative to guid */
  public String  force_link_speed_file;
  public String  part_enforce;
  public String  port_search_ordering_file;
  public String  per_module_logging_file;

  public SBN_Options()
  {
    // TODO Auto-generated constructor stub
  }
  
  /************************************************************
   * Method Name:
   *  SBN_Options
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param lmc_esp0
   * @param reassign_lids
   * @param ignore_other_sm
   * @param single_thread
   * @param disable_multicast
   * @param force_log_flush
   * @param use_mfttop
   * @param force_heavy_sweep
   * @param no_partition_enforcement
   * @param qos
   * @param accum_log_file
   * @param port_profile_switch_nodes
   * @param sweep_on_trap
   * @param use_ucast_cache
   * @param connect_roots
   * @param sa_db_dump
   * @param do_mesh_analysis
   * @param exit_on_fatal
   * @param honor_guid2lid_file
   * @param daemon
   * @param sm_inactive
   * @param babbling_port_policy
   * @param use_optimized_slvl
   * @param enable_quirks
   * @param no_clients_rereg
   * @param perfmgr
   * @param perfmgr_redir
   * @param consolidate_ipv6_snm_req
   * @param m_key_lookup
   * @param allow_both_pkeys
   * @param port_shifting
   * @param remote_guid_sorting
   * @param guid_routing_order_no_scatter
   * @param drop_event_subscriptions
   * @param fsync_high_avail_files
   * @param congestion_control
   * @param perfmgr_ignore_cas
   * @param perfmgr_log_errors
   * @param perfmgr_query_cpi
   * @param perfmgr_xmit_wait_log
   * @param sm_priority
   * @param lmc
   * @param max_op_vls
   * @param force_link_speed
   * @param subnet_timeout
   * @param packet_life_time
   * @param vl_stall_count
   * @param leaf_vl_stall_count
   * @param head_of_queue_lifetime
   * @param leaf_head_of_queue_lifetime
   * @param local_phy_errors_threshold
   * @param overrun_errors_threshold
   * @param log_flags
   * @param lash_start_vl
   * @param sm_sl
   * @param m_key_protect_bits
   * @param force_link_speed_ext
   * @param fdr10
   * @param sm_assigned_guid
   * @param cc_sw_cong_setting_threshold
   * @param cc_sw_cong_setting_packet_size
   * @param cc_sw_cong_setting_credit_starvation_threshold
   * @param m_key_lease_period
   * @param sweep_interval
   * @param max_wire_smps
   * @param max_wire_smps2
   * @param max_smps_timeout
   * @param transaction_timeout
   * @param transaction_retries
   * @param sminfo_polling_timeout
   * @param polling_retry_number
   * @param max_msg_fifo_timeout
   * @param console_port
   * @param max_reverse_hops
   * @param perfmgr_sweep_time_s
   * @param perfmgr_max_outstanding_queries
   * @param ca_port
   * @param part_enforce_enum
   * @param scatter_ports
   * @param cc_max_outstanding_mads
   * @param cc_sw_cong_setting_control_map
   * @param cc_sw_cong_setting_marking_rate
   * @param cc_ca_cong_setting_port_control
   * @param cc_ca_cong_setting_control_map
   * @param perfmgr_rm_nodes
   * @param perfmgr_xmit_wait_threshold
   * @param guid
   * @param m_key
   * @param sm_key
   * @param sa_key
   * @param subnet_prefix
   * @param log_max_size
   * @param cc_key
   * @param config_file
   * @param dump_files_dir
   * @param log_file
   * @param partition_config_file
   * @param qos_policy_file
   * @param console
   * @param port_prof_ignore_file
   * @param hop_weights_file
   * @param routing_engine_names
   * @param lid_matrix_dump_file
   * @param lfts_file
   * @param root_guid_file
   * @param cn_guid_file
   * @param io_guid_file
   * @param ids_guid_file
   * @param guid_routing_order_file
   * @param sa_db_file
   * @param torus_conf_file
   * @param event_db_dump_file
   * @param event_plugin_name
   * @param event_plugin_options
   * @param node_name_map_name
   * @param prefix_routes_file
   * @param log_prefix
   * @param ca_name
   * @param force_link_speed_file
   * @param part_enforce
   * @param port_search_ordering_file
   * @param per_module_logging_file
   ***********************************************************/
  public SBN_Options(boolean lmc_esp0, boolean reassign_lids, boolean ignore_other_sm,
      boolean single_thread, boolean disable_multicast, boolean force_log_flush,
      boolean use_mfttop, boolean force_heavy_sweep, boolean no_partition_enforcement, boolean qos,
      boolean accum_log_file, boolean port_profile_switch_nodes, boolean sweep_on_trap,
      boolean use_ucast_cache, boolean connect_roots, boolean sa_db_dump, boolean do_mesh_analysis,
      boolean exit_on_fatal, boolean honor_guid2lid_file, boolean daemon, boolean sm_inactive,
      boolean babbling_port_policy, boolean use_optimized_slvl, boolean enable_quirks,
      boolean no_clients_rereg, boolean perfmgr, boolean perfmgr_redir,
      boolean consolidate_ipv6_snm_req, boolean m_key_lookup, boolean allow_both_pkeys,
      boolean port_shifting, boolean remote_guid_sorting, boolean guid_routing_order_no_scatter,
      boolean drop_event_subscriptions, boolean fsync_high_avail_files, boolean congestion_control,
      boolean perfmgr_ignore_cas, boolean perfmgr_log_errors, boolean perfmgr_query_cpi,
      boolean perfmgr_xmit_wait_log, short sm_priority, short lmc, short max_op_vls,
      short force_link_speed, short subnet_timeout, short packet_life_time, short vl_stall_count,
      short leaf_vl_stall_count, short head_of_queue_lifetime, short leaf_head_of_queue_lifetime,
      short local_phy_errors_threshold, short overrun_errors_threshold, short log_flags,
      short lash_start_vl, short sm_sl, short m_key_protect_bits, short force_link_speed_ext,
      short fdr10, short sm_assigned_guid, short cc_sw_cong_setting_threshold,
      short cc_sw_cong_setting_packet_size, short cc_sw_cong_setting_credit_starvation_threshold,
      int m_key_lease_period, int sweep_interval, int max_wire_smps, int max_wire_smps2,
      int max_smps_timeout, int transaction_timeout, int transaction_retries,
      int sminfo_polling_timeout, int polling_retry_number, int max_msg_fifo_timeout,
      int console_port, int max_reverse_hops, int perfmgr_sweep_time_s,
      int perfmgr_max_outstanding_queries, int ca_port, int part_enforce_enum, int scatter_ports,
      int cc_max_outstanding_mads, int cc_sw_cong_setting_control_map,
      int cc_sw_cong_setting_marking_rate, int cc_ca_cong_setting_port_control,
      int cc_ca_cong_setting_control_map, int perfmgr_rm_nodes, int perfmgr_xmit_wait_threshold,
      long guid, long m_key, long sm_key, long sa_key, long subnet_prefix, long log_max_size,
      long cc_key, String config_file, String dump_files_dir, String log_file,
      String partition_config_file, String qos_policy_file, String console,
      String port_prof_ignore_file, String hop_weights_file, String routing_engine_names,
      String lid_matrix_dump_file, String lfts_file, String root_guid_file, String cn_guid_file,
      String io_guid_file, String ids_guid_file, String guid_routing_order_file, String sa_db_file,
      String torus_conf_file, String event_db_dump_file, String event_plugin_name,
      String event_plugin_options, String node_name_map_name, String prefix_routes_file,
      String log_prefix, String ca_name, String force_link_speed_file, String part_enforce,
      String port_search_ordering_file, String per_module_logging_file)
  {
    super();
    this.lmc_esp0 = lmc_esp0;
    this.reassign_lids = reassign_lids;
    this.ignore_other_sm = ignore_other_sm;
    this.single_thread = single_thread;
    this.disable_multicast = disable_multicast;
    this.force_log_flush = force_log_flush;
    this.use_mfttop = use_mfttop;
    this.force_heavy_sweep = force_heavy_sweep;
    this.no_partition_enforcement = no_partition_enforcement;
    this.qos = qos;
    this.accum_log_file = accum_log_file;
    this.port_profile_switch_nodes = port_profile_switch_nodes;
    this.sweep_on_trap = sweep_on_trap;
    this.use_ucast_cache = use_ucast_cache;
    this.connect_roots = connect_roots;
    this.sa_db_dump = sa_db_dump;
    this.do_mesh_analysis = do_mesh_analysis;
    this.exit_on_fatal = exit_on_fatal;
    this.honor_guid2lid_file = honor_guid2lid_file;
    this.daemon = daemon;
    this.sm_inactive = sm_inactive;
    this.babbling_port_policy = babbling_port_policy;
    this.use_optimized_slvl = use_optimized_slvl;
    this.enable_quirks = enable_quirks;
    this.no_clients_rereg = no_clients_rereg;
    this.perfmgr = perfmgr;
    this.perfmgr_redir = perfmgr_redir;
    this.consolidate_ipv6_snm_req = consolidate_ipv6_snm_req;
    this.m_key_lookup = m_key_lookup;
    this.allow_both_pkeys = allow_both_pkeys;
    this.port_shifting = port_shifting;
    this.remote_guid_sorting = remote_guid_sorting;
    this.guid_routing_order_no_scatter = guid_routing_order_no_scatter;
    this.drop_event_subscriptions = drop_event_subscriptions;
    this.fsync_high_avail_files = fsync_high_avail_files;
    this.congestion_control = congestion_control;
    this.perfmgr_ignore_cas = perfmgr_ignore_cas;
    this.perfmgr_log_errors = perfmgr_log_errors;
    this.perfmgr_query_cpi = perfmgr_query_cpi;
    this.perfmgr_xmit_wait_log = perfmgr_xmit_wait_log;
    this.sm_priority = sm_priority;
    this.lmc = lmc;
    this.max_op_vls = max_op_vls;
    this.force_link_speed = force_link_speed;
    this.subnet_timeout = subnet_timeout;
    this.packet_life_time = packet_life_time;
    this.vl_stall_count = vl_stall_count;
    this.leaf_vl_stall_count = leaf_vl_stall_count;
    this.head_of_queue_lifetime = head_of_queue_lifetime;
    this.leaf_head_of_queue_lifetime = leaf_head_of_queue_lifetime;
    this.local_phy_errors_threshold = local_phy_errors_threshold;
    this.overrun_errors_threshold = overrun_errors_threshold;
    this.log_flags = log_flags;
    this.lash_start_vl = lash_start_vl;
    this.sm_sl = sm_sl;
    this.m_key_protect_bits = m_key_protect_bits;
    this.force_link_speed_ext = force_link_speed_ext;
    this.fdr10 = fdr10;
    this.sm_assigned_guid = sm_assigned_guid;
    this.cc_sw_cong_setting_threshold = cc_sw_cong_setting_threshold;
    this.cc_sw_cong_setting_packet_size = cc_sw_cong_setting_packet_size;
    this.cc_sw_cong_setting_credit_starvation_threshold = cc_sw_cong_setting_credit_starvation_threshold;
    this.m_key_lease_period = m_key_lease_period;
    this.sweep_interval = sweep_interval;
    this.max_wire_smps = max_wire_smps;
    this.max_wire_smps2 = max_wire_smps2;
    this.max_smps_timeout = max_smps_timeout;
    this.transaction_timeout = transaction_timeout;
    this.transaction_retries = transaction_retries;
    this.sminfo_polling_timeout = sminfo_polling_timeout;
    this.polling_retry_number = polling_retry_number;
    this.max_msg_fifo_timeout = max_msg_fifo_timeout;
    this.console_port = console_port;
    this.max_reverse_hops = max_reverse_hops;
    this.perfmgr_sweep_time_s = perfmgr_sweep_time_s;
    this.perfmgr_max_outstanding_queries = perfmgr_max_outstanding_queries;
    this.ca_port = ca_port;
    this.part_enforce_enum = part_enforce_enum;
    this.scatter_ports = scatter_ports;
    this.cc_max_outstanding_mads = cc_max_outstanding_mads;
    this.cc_sw_cong_setting_control_map = cc_sw_cong_setting_control_map;
    this.cc_sw_cong_setting_marking_rate = cc_sw_cong_setting_marking_rate;
    this.cc_ca_cong_setting_port_control = cc_ca_cong_setting_port_control;
    this.cc_ca_cong_setting_control_map = cc_ca_cong_setting_control_map;
    this.perfmgr_rm_nodes = perfmgr_rm_nodes;
    this.perfmgr_xmit_wait_threshold = perfmgr_xmit_wait_threshold;
    this.guid = guid;
    this.m_key = m_key;
    this.sm_key = sm_key;
    this.sa_key = sa_key;
    this.subnet_prefix = subnet_prefix;
    this.log_max_size = log_max_size;
    this.cc_key = cc_key;
    this.config_file = config_file;
    this.dump_files_dir = dump_files_dir;
    this.log_file = log_file;
    this.partition_config_file = partition_config_file;
    this.qos_policy_file = qos_policy_file;
    this.console = console;
    this.port_prof_ignore_file = port_prof_ignore_file;
    this.hop_weights_file = hop_weights_file;
    this.routing_engine_names = routing_engine_names;
    this.lid_matrix_dump_file = lid_matrix_dump_file;
    this.lfts_file = lfts_file;
    this.root_guid_file = root_guid_file;
    this.cn_guid_file = cn_guid_file;
    this.io_guid_file = io_guid_file;
    this.ids_guid_file = ids_guid_file;
    this.guid_routing_order_file = guid_routing_order_file;
    this.sa_db_file = sa_db_file;
    this.torus_conf_file = torus_conf_file;
    this.event_db_dump_file = event_db_dump_file;
    this.event_plugin_name = event_plugin_name;
    this.event_plugin_options = event_plugin_options;
    this.node_name_map_name = node_name_map_name;
    this.prefix_routes_file = prefix_routes_file;
    this.log_prefix = log_prefix;
    this.ca_name = ca_name;
    this.force_link_speed_file = force_link_speed_file;
    this.part_enforce = part_enforce;
    this.port_search_ordering_file = port_search_ordering_file;
    this.per_module_logging_file = per_module_logging_file;
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
    return "SBN_Options [lmc_esp0=" + lmc_esp0 + ", reassign_lids=" + reassign_lids
        + ", ignore_other_sm=" + ignore_other_sm + ", single_thread=" + single_thread
        + ", disable_multicast=" + disable_multicast + ", force_log_flush=" + force_log_flush
        + ", use_mfttop=" + use_mfttop + ", force_heavy_sweep=" + force_heavy_sweep
        + ", no_partition_enforcement=" + no_partition_enforcement + ", qos=" + qos
        + ", accum_log_file=" + accum_log_file + ", port_profile_switch_nodes="
        + port_profile_switch_nodes + ", sweep_on_trap=" + sweep_on_trap + ", use_ucast_cache="
        + use_ucast_cache + ", connect_roots=" + connect_roots + ", sa_db_dump=" + sa_db_dump
        + ", do_mesh_analysis=" + do_mesh_analysis + ", exit_on_fatal=" + exit_on_fatal
        + ", honor_guid2lid_file=" + honor_guid2lid_file + ", daemon=" + daemon + ", sm_inactive="
        + sm_inactive + ", babbling_port_policy=" + babbling_port_policy + ", use_optimized_slvl="
        + use_optimized_slvl + ", enable_quirks=" + enable_quirks + ", no_clients_rereg="
        + no_clients_rereg + ", perfmgr=" + perfmgr + ", perfmgr_redir=" + perfmgr_redir
        + ", consolidate_ipv6_snm_req=" + consolidate_ipv6_snm_req + ", m_key_lookup="
        + m_key_lookup + ", allow_both_pkeys=" + allow_both_pkeys + ", port_shifting="
        + port_shifting + ", remote_guid_sorting=" + remote_guid_sorting
        + ", guid_routing_order_no_scatter=" + guid_routing_order_no_scatter
        + ", drop_event_subscriptions=" + drop_event_subscriptions + ", fsync_high_avail_files="
        + fsync_high_avail_files + ", congestion_control=" + congestion_control
        + ", perfmgr_ignore_cas=" + perfmgr_ignore_cas + ", perfmgr_log_errors="
        + perfmgr_log_errors + ", perfmgr_query_cpi=" + perfmgr_query_cpi
        + ", perfmgr_xmit_wait_log=" + perfmgr_xmit_wait_log + ", sm_priority=" + sm_priority
        + ", lmc=" + lmc + ", max_op_vls=" + max_op_vls + ", force_link_speed=" + force_link_speed
        + ", subnet_timeout=" + subnet_timeout + ", packet_life_time=" + packet_life_time
        + ", vl_stall_count=" + vl_stall_count + ", leaf_vl_stall_count=" + leaf_vl_stall_count
        + ", head_of_queue_lifetime=" + head_of_queue_lifetime + ", leaf_head_of_queue_lifetime="
        + leaf_head_of_queue_lifetime + ", local_phy_errors_threshold="
        + local_phy_errors_threshold + ", overrun_errors_threshold=" + overrun_errors_threshold
        + ", log_flags=" + log_flags + ", lash_start_vl=" + lash_start_vl + ", sm_sl=" + sm_sl
        + ", m_key_protect_bits=" + m_key_protect_bits + ", force_link_speed_ext="
        + force_link_speed_ext + ", fdr10=" + fdr10 + ", sm_assigned_guid=" + sm_assigned_guid
        + ", cc_sw_cong_setting_threshold=" + cc_sw_cong_setting_threshold
        + ", cc_sw_cong_setting_packet_size=" + cc_sw_cong_setting_packet_size
        + ", cc_sw_cong_setting_credit_starvation_threshold="
        + cc_sw_cong_setting_credit_starvation_threshold + ", m_key_lease_period="
        + m_key_lease_period + ", sweep_interval=" + sweep_interval + ", max_wire_smps="
        + max_wire_smps + ", max_wire_smps2=" + max_wire_smps2 + ", max_smps_timeout="
        + max_smps_timeout + ", transaction_timeout=" + transaction_timeout
        + ", transaction_retries=" + transaction_retries + ", sminfo_polling_timeout="
        + sminfo_polling_timeout + ", polling_retry_number=" + polling_retry_number
        + ", max_msg_fifo_timeout=" + max_msg_fifo_timeout + ", console_port=" + console_port
        + ", max_reverse_hops=" + max_reverse_hops + ", perfmgr_sweep_time_s="
        + perfmgr_sweep_time_s + ", perfmgr_max_outstanding_queries="
        + perfmgr_max_outstanding_queries + ", ca_port=" + ca_port + ", part_enforce_enum="
        + part_enforce_enum + ", scatter_ports=" + scatter_ports + ", cc_max_outstanding_mads="
        + cc_max_outstanding_mads + ", cc_sw_cong_setting_control_map="
        + cc_sw_cong_setting_control_map + ", cc_sw_cong_setting_marking_rate="
        + cc_sw_cong_setting_marking_rate + ", cc_ca_cong_setting_port_control="
        + cc_ca_cong_setting_port_control + ", cc_ca_cong_setting_control_map="
        + cc_ca_cong_setting_control_map + ", perfmgr_rm_nodes=" + perfmgr_rm_nodes
        + ", perfmgr_xmit_wait_threshold=" + perfmgr_xmit_wait_threshold + ", guid=" + guid
        + ", m_key=" + m_key + ", sm_key=" + sm_key + ", sa_key=" + sa_key + ", subnet_prefix="
        + subnet_prefix + ", log_max_size=" + log_max_size + ", cc_key=" + cc_key
        + ", config_file=" + config_file + ", dump_files_dir=" + dump_files_dir + ", log_file="
        + log_file + ", partition_config_file=" + partition_config_file + ", qos_policy_file="
        + qos_policy_file + ", console=" + console + ", port_prof_ignore_file="
        + port_prof_ignore_file + ", hop_weights_file=" + hop_weights_file
        + ", routing_engine_names=" + routing_engine_names + ", lid_matrix_dump_file="
        + lid_matrix_dump_file + ", lfts_file=" + lfts_file + ", root_guid_file=" + root_guid_file
        + ", cn_guid_file=" + cn_guid_file + ", io_guid_file=" + io_guid_file + ", ids_guid_file="
        + ids_guid_file + ", guid_routing_order_file=" + guid_routing_order_file + ", sa_db_file="
        + sa_db_file + ", torus_conf_file=" + torus_conf_file + ", event_db_dump_file="
        + event_db_dump_file + ", event_plugin_name=" + event_plugin_name
        + ", event_plugin_options=" + event_plugin_options + ", node_name_map_name="
        + node_name_map_name + ", prefix_routes_file=" + prefix_routes_file + ", log_prefix="
        + log_prefix + ", ca_name=" + ca_name + ", force_link_speed_file=" + force_link_speed_file
        + ", part_enforce=" + part_enforce + ", port_search_ordering_file="
        + port_search_ordering_file + ", per_module_logging_file=" + per_module_logging_file + "]";
  }
}
