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
 *        file: SBN_Switch.java
 *
 *  Created on: Jul 12, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import gov.llnl.lc.infiniband.core.IB_Guid;
import gov.llnl.lc.infiniband.core.NativePeerClass;

import java.io.Serializable;
import java.util.Arrays;

/**********************************************************************
 * An <code>SBN_Switch</code> object contains relevant information about
 * a subnet switch.
 * To the extent that it is possible, it mirrors members in the 
 * <code>struct osm_switch</code>.
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
 * @version Nov 10, 2014 2:31:23 PM
 **********************************************************************/
@NativePeerClass("v2.0")
public class SBN_Switch implements Serializable
{
//  typedef struct sr_switch
//  {
//    uint64_t guid;
//
//    uint16_t max_lid_ho;
//    uint8_t num_ports;
//    uint16_t num_hops;
//    uint8_t hops;
//    uint8_t dimn_ports;
//    uint8_t lft[MAX_NUM_NODES];
//    uint8_t new_lft;
//    uint16_t lft_size;
//    int32_t mft_block_num;
//    uint32_t mft_position;
//    unsigned endport_links;
//    unsigned need_update;
//    uint32_t num_of_mcm;
//    uint8_t is_mc_member;
//  } sr_Switch_t;
//
//  typedef struct osm_switch {
//    cl_map_item_t map_item;
//    osm_node_t *p_node;
//    ib_switch_info_t switch_info;
//    uint16_t max_lid_ho;
//    uint8_t num_ports;
//    uint16_t num_hops;
//    uint8_t **hops;
//    osm_port_profile_t *p_prof;
//    uint8_t *search_ordering_ports;
//    uint8_t *lft;
//    uint8_t *new_lft;
//    uint16_t lft_size;
//    osm_mcast_tbl_t mcast_tbl;
//    int32_t mft_block_num;
//    uint32_t mft_position;
//    unsigned endport_links;
//    unsigned need_update;
//    void *priv;
//    cl_map_item_t mgrp_item;
//    uint32_t num_of_mcm;
//    uint8_t is_mc_member;
//  } osm_switch_t;

  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = 2224356058242743351L;

  /**  describe num_ports here **/
  public short num_ports;
  
  /**  the hop count table for this switch.  An array, indexed by destination node lid
   * and containing the minimum # of hops to that lid, or node **/
  public short hops[];
  
  /**  the linear forwarding table for this switch.  An array, indexed by destination node lid
   * and containing the port number to use to route to that lid, or node (max is 255 in C ) **/
  public short lft[];
  
  /**  describe new_lft here **/
  public short new_lft;
  /**  describe is_mc_member here **/
  public short is_mc_member;

  /**  describe max_lid_ho here **/
  public int   max_lid_ho;
  
  /**  the size of the minimum hop table **/
  public int   num_hops;
  /**  describe lft_size here **/
  public int   lft_size;
  /**  describe mft_block_num here **/
  public int   mft_block_num;
  /**  describe mft_position here **/
  public int   mft_position;
  /**  describe endport_links here **/
  public int   endport_links;
  /**  describe need_update here **/
  public int   need_update;
  /**  describe num_of_mcm here **/
  public int   num_of_mcm;

  /**  the switch's guid **/
  public long  guid;

  /************************************************************
   * Method Name:
   *  SBN_Switch
  **/
  /**
   * The fully parameterized constructor used by the native layer
   * to create an instance of this peer class.
   *
   *
   * @param num_ports
   * @param hops
   * @param lft
   * @param new_lft
   * @param is_mc_member
   * @param max_lid_ho
   * @param num_hops
   * @param lft_size
   * @param mft_block_num
   * @param mft_position
   * @param endport_links
   * @param need_update
   * @param num_of_mcm
   * @param guid
   ***********************************************************/
  public SBN_Switch(short num_ports, short[] hops, short[] lft, short new_lft, short is_mc_member,
      int max_lid_ho, int num_hops, int lft_size, int mft_block_num, int mft_position,
      int endport_links, int need_update, int num_of_mcm, long guid)
  {
    super();
    this.num_ports = num_ports;
    this.hops = hops;
    this.lft = lft;
    this.new_lft = new_lft;
    this.is_mc_member = is_mc_member;
    this.max_lid_ho = max_lid_ho;
    this.num_hops = num_hops;
    this.lft_size = lft_size;
    this.mft_block_num = mft_block_num;
    this.mft_position = mft_position;
    this.endport_links = endport_links;
    this.need_update = need_update;
    this.num_of_mcm = num_of_mcm;
    this.guid = guid;
  }

  /************************************************************
   * Method Name:
   *  SBN_Switch
   */
   /**
   * Default constructor
   *
   ***********************************************************/
  public SBN_Switch()
  {
  }

  public static void showSwitchRoute(SBN_Switch sw, OSM_Fabric fab)
  {
    IB_Guid guid = new IB_Guid(sw.guid);
    String name  = fab.getNameFromGuid(guid);
    int   lid    = fab.getLidFromGuid(guid);
    System.out.println("\nSwitch: " + (new IB_Guid(sw.guid)).toColonString());
    System.out.println("  lid: " + lid + ", desc: " + name);
    System.out.println("  table size: " + sw.lft_size + ", and " + sw.lft.length);
    
   }

  public static void showLftTable(SBN_Switch sw)
  {
    // I think the index is the destination lid, and the value is the "exit" port number
    // to get to the destination lid.
    // obviously the port numbers can't be bigger than 36
    int lnum = 0;
    for(short pn: sw.lft)
    {
      // pn == 0 is typically the route to SELF
      // only print them out if they look normal (between 0 and 255)
      if(pn < 255)
        System.out.println("LNUM: " + lnum + ", PN: " + pn);
      lnum++;
    }
  }

  public static void showHopTable(SBN_Switch sw)
  {
    // I think the index is the destination lid, and the value is the number of hops
    // to get to the destination lid.
    int lnum = 0;
    for(short hn: sw.hops)
    {
      if(hn < 255)
        System.out.println("LNUM: " + lnum + ", HN: " + hn);
      lnum++;
    }
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
    return "SBN_Switch [num_ports=" + num_ports + ", hops=" + Arrays.toString(hops) + ", lft="
        + Arrays.toString(lft) + ", new_lft=" + new_lft + ", is_mc_member=" + is_mc_member
        + ", max_lid_ho=" + max_lid_ho + ", num_hops=" + num_hops + ", lft_size=" + lft_size
        + ", mft_block_num=" + mft_block_num + ", mft_position=" + mft_position
        + ", endport_links=" + endport_links + ", need_update=" + need_update + ", num_of_mcm="
        + num_of_mcm + ", guid=" + guid + "]";
  }

}
