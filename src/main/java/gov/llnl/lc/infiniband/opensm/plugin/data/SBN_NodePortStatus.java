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
 *        file: SBN_NodePortStatus.java
 *
 *  Created on: Jul 26, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import gov.llnl.lc.infiniband.core.IB_Port;
import gov.llnl.lc.infiniband.core.NativePeerClass;

import java.io.Serializable;
import java.util.Arrays;

/**********************************************************************
 * An <code>SBN_NodePortStatus</code> represents the node & port status
 * for a collection of nodes types (usually switch, router, channel adapter).
 * <p>
 * Java Peer Class, to the native interface.  All peer classes contain
 * public members which can be directly accessed.  All peer classes
 * have fully parameterized constructors that are used by the native
 * layer to create an instance of the class.  Instances of peer classes
 * are not intended to be created from the java environment.
 * <p>
 * @see  OSM_SysInfo
 * @see NativePeerClass
 *
 * @author meier3
 * 
 * @version Nov 12, 2014 2:52:37 PM
 **********************************************************************/
@NativePeerClass("v2.0")
public class SBN_NodePortStatus implements Serializable
{
  /*  see getPortStats(cl_map_item_t * const p_map_item, void *context)
   * 
   *  also
   *  
typedef struct {
  uint8_t node_type_lim;  // limit the results; 0 == ALL
  uint64_t total_nodes;
  uint64_t total_ports;
  uint64_t ports_down;
  uint64_t ports_active;
  uint64_t ports_disabled;
  port_report_t *disabled_ports;
  uint64_t ports_1X;
  uint64_t ports_4X;
  uint64_t ports_8X;
  uint64_t ports_12X;
  uint64_t ports_unknown_width;
  uint64_t ports_unenabled_width;
  port_report_t *unenabled_width_ports;
  uint64_t ports_reduced_width;
  port_report_t *reduced_width_ports;
  uint64_t ports_sdr;
  uint64_t ports_ddr;
  uint64_t ports_qdr;
  uint64_t ports_fdr10;
  uint64_t ports_fdr;
  uint64_t ports_edr;
  uint64_t ports_unknown_speed;
  uint64_t ports_unenabled_speed;
  port_report_t *unenabled_speed_ports;
  uint64_t ports_reduced_speed;
  port_report_t *reduced_speed_ports;
} fabric_stats_t;

// this is basically a clone of fabric_stats_t in console.c, shown above
typedef struct jsi_port_stats{
        uint8_t node_type_lim;  // limit the results; 0 == ALL
        uint64_t total_nodes;
        uint64_t total_ports;
        uint64_t ports_down;
        uint64_t ports_active;
        uint64_t ports_disabled;
        jsi_PortDesc_t *disabled_ports;
        uint64_t ports_1X;
        uint64_t ports_4X;
        uint64_t ports_8X;
        uint64_t ports_12X;
        uint64_t ports_unknown_width;
        uint64_t ports_unenabled_width;
        jsi_PortDesc_t *unenabled_width_ports;
        uint64_t ports_reduced_width;
        jsi_PortDesc_t *reduced_width_ports;
        uint64_t ports_sdr;
        uint64_t ports_ddr;
        uint64_t ports_qdr;
        uint64_t ports_fdr10;
        uint64_t ports_fdr;
        uint64_t ports_edr;
        uint64_t ports_unknown_speed;
        uint64_t ports_unenabled_speed;
        jsi_PortDesc_t *unenabled_speed_ports;
        uint64_t ports_reduced_speed;
        jsi_PortDesc_t *reduced_speed_ports;
} jsi_PortStats_t;
*/
  
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = -1276556774102460135L;

  /**  the total number of nodes **/
  public long      total_nodes;

  /**  the total number of ports **/
  public long      total_ports;
  
  /**  the number of downed ports **/
  public long      ports_down;
  
  /**  the number of active ports **/
  public long      ports_active;
  
  /**  the number of disabled ports **/
  public long      ports_disabled;
  
  /**  describe ports_1X here **/
  public long      ports_1X;
  
  /**  describe ports_4X here **/
  public long      ports_4X;
  
  /**  describe ports_8X here **/
  public long      ports_8X;
  
  /**  describe ports_12X here **/
  public long      ports_12X;
  
  /**  the number of ports whos width cannot be determined **/
  public long      ports_unknown_width;
  
  /**  the number of ports that have reduced width **/
  public long      ports_reduced_width;
  
  /**  the number of SDR ports **/
  public long      ports_sdr;
  
  /**  the number of DDR ports **/
  public long      ports_ddr;
  
  /**  the number of QDR ports **/
  public long      ports_qdr;
  
  /**  the number of ports whos speed cannot be determined **/
  public long      ports_unknown_speed;
  
  /**  the number of ports that have reduced speed **/
  public long      ports_reduced_speed;

  /**  the number of ports whos widths are not enabled **/
  public long      ports_unenabled_width;

  /**  the number of FDR10 ports **/
  public long      ports_fdr10;

  /**  the number of FDR ports **/
  public long      ports_fdr;

  /**  the number of EDR ports **/
  public long      ports_edr;

  /**  the number of ports with speed unenabled usually due to problems with force_link_speed **/
 public long      ports_unenabled_speed;

  /**  an array of ports that are disabled **/
  public IB_Port[] disabled_ports;
  
  /**  an array of ports that have reduced width **/
  public IB_Port[] reduced_width_ports;
  
  /**  an array of ports that have reduced speed **/
  public IB_Port[] reduced_speed_ports;

  /**  an array of ports that have their widths unenabled **/
  public IB_Port[] unenabled_width_ports;

  /**  an array of ports that have their speed unenabled **/
  public IB_Port[] unenabled_speed_ports;

  /** the information in this object refers to a collection of nodes of this type 
   * Switch, Router, Channel Adapter (from ib_types.h) **/
  public String    NodeType;

  /************************************************************
   * Method Name:
   *  SBN_NodePortStatus
   */
   /** Default constructor.
   *
   ***********************************************************/
  public SBN_NodePortStatus()
  {
  }
  
  /************************************************************
   * Method Name:
   *  SBN_NodePortStatus
  **/
  /** The fully parameterized constructor used by the native layer
  * to create an instance of this peer class.
   *
   * @see     IB_Port
   *
   * @param total_nodes
   * @param total_ports
   * @param ports_down
   * @param ports_active
   * @param ports_disabled
   * @param ports_1x
   * @param ports_4x
   * @param ports_8x
   * @param ports_12x
   * @param ports_unknown_width
   * @param ports_reduced_width
   * @param ports_sdr
   * @param ports_ddr
   * @param ports_qdr
   * @param ports_unknown_speed
   * @param ports_reduced_speed
   * @param ports_unenabled_width
   * @param ports_fdr10
   * @param ports_fdr
   * @param ports_edr
   * @param ports_unenabled_speed
   * @param disabled_ports
   * @param reduced_width_ports
   * @param reduced_speed_ports
   * @param unenabled_width_ports
   * @param unenabled_speed_ports
   * @param nodeType
   ***********************************************************/
  public SBN_NodePortStatus(long total_nodes, long total_ports, long ports_down, long ports_active,
      long ports_disabled, long ports_1x, long ports_4x, long ports_8x, long ports_12x,
      long ports_unknown_width, long ports_reduced_width, long ports_sdr, long ports_ddr,
      long ports_qdr, long ports_unknown_speed, long ports_reduced_speed,
      long ports_unenabled_width, long ports_fdr10, long ports_fdr, long ports_edr,
      long ports_unenabled_speed, IB_Port[] disabled_ports, IB_Port[] reduced_width_ports,
      IB_Port[] reduced_speed_ports, IB_Port[] unenabled_width_ports,
      IB_Port[] unenabled_speed_ports, String nodeType)
  {
    super();
    this.total_nodes = total_nodes;
    this.total_ports = total_ports;
    this.ports_down = ports_down;
    this.ports_active = ports_active;
    this.ports_disabled = ports_disabled;
    ports_1X = ports_1x;
    ports_4X = ports_4x;
    ports_8X = ports_8x;
    ports_12X = ports_12x;
    this.ports_unknown_width = ports_unknown_width;
    this.ports_reduced_width = ports_reduced_width;
    this.ports_sdr = ports_sdr;
    this.ports_ddr = ports_ddr;
    this.ports_qdr = ports_qdr;
    this.ports_unknown_speed = ports_unknown_speed;
    this.ports_reduced_speed = ports_reduced_speed;
    this.ports_unenabled_width = ports_unenabled_width;
    this.ports_fdr10 = ports_fdr10;
    this.ports_fdr = ports_fdr;
    this.ports_edr = ports_edr;
    this.ports_unenabled_speed = ports_unenabled_speed;
    this.disabled_ports = disabled_ports;
    this.reduced_width_ports = reduced_width_ports;
    this.reduced_speed_ports = reduced_speed_ports;
    this.unenabled_width_ports = unenabled_width_ports;
    this.unenabled_speed_ports = unenabled_speed_ports;
    NodeType = nodeType;
  }
  
  public boolean add(SBN_NodePortStatus nps)
  {
    // this is used for creating a total by accumulating other ports into this "total" NodePortStatus
    
    // just do a member-wise add
    this.total_nodes += nps.total_nodes;
    this.total_ports += nps.total_ports;
    this.ports_down += nps.ports_down;
    this.ports_active += nps.ports_active;
    this.ports_disabled += nps.ports_disabled;
    ports_1X += nps.ports_1X;
    ports_4X += nps.ports_4X;
    ports_8X += nps.ports_8X;
    ports_12X += nps.ports_12X;
    this.ports_unknown_width += nps.ports_unknown_width;
    this.ports_reduced_width += nps.ports_reduced_width;
    this.ports_sdr += nps.ports_sdr;
    this.ports_ddr += nps.ports_ddr;
    this.ports_qdr += nps.ports_qdr;
    this.ports_unknown_speed += nps.ports_unknown_speed;
    this.ports_reduced_speed += nps.ports_reduced_speed;
    this.ports_unenabled_width += nps.ports_unenabled_width;
    this.ports_fdr10 += nps.ports_fdr10;
    this.ports_fdr += nps.ports_fdr;
    this.ports_edr += nps.ports_edr;
    this.ports_unenabled_speed += nps.ports_unenabled_speed;
    
    return true;
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
    return "SBN_NodePortStatus [total_nodes=" + total_nodes + ", total_ports=" + total_ports
        + ", ports_down=" + ports_down + ", ports_active=" + ports_active + ", ports_disabled="
        + ports_disabled + ", ports_1X=" + ports_1X + ", ports_4X=" + ports_4X + ", ports_8X="
        + ports_8X + ", ports_12X=" + ports_12X + ", ports_unknown_width=" + ports_unknown_width
        + ", ports_reduced_width=" + ports_reduced_width + ", ports_sdr=" + ports_sdr
        + ", ports_ddr=" + ports_ddr + ", ports_qdr=" + ports_qdr + ", ports_unknown_speed="
        + ports_unknown_speed + ", ports_reduced_speed=" + ports_reduced_speed
        + ", ports_unenabled_width=" + ports_unenabled_width + ", ports_fdr10=" + ports_fdr10
        + ", ports_fdr=" + ports_fdr + ", ports_edr=" + ports_edr + ", ports_unenabled_speed="
        + ports_unenabled_speed + ", disabled_ports=" + Arrays.toString(disabled_ports)
        + ", reduced_width_ports=" + Arrays.toString(reduced_width_ports)
        + ", reduced_speed_ports=" + Arrays.toString(reduced_speed_ports)
        + ", unenabled_width_ports=" + Arrays.toString(unenabled_width_ports)
        + ", unenabled_speed_ports=" + Arrays.toString(unenabled_speed_ports) + ", NodeType="
        + NodeType + "]";
  }

}
