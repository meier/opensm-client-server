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
 *        file: SBN_PartitionKey.java
 *
 *  Created on: Jul 12, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import gov.llnl.lc.infiniband.core.IB_Guid;
import gov.llnl.lc.infiniband.core.NativePeerClass;
import gov.llnl.lc.logging.CommonLogger;

import java.io.Serializable;
import java.util.Arrays;

/**********************************************************************
 * An <code>SBN_MulticastGroup</code> object contains relevant information about
 * a multicast group.
 * To the extent that it is possible, it mirrors members in the 
 * <code>struct osm_mgrp</code>.
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
 * @version Nov 5, 2014 2:39:36 PM
 **********************************************************************/
@NativePeerClass("v2.0")
public class SBN_MulticastGroup implements Serializable, CommonLogger
{
//  typedef struct osm_mgrp {
//    cl_fmap_item_t map_item;
//    cl_list_item_t list_item;
//    ib_net16_t mlid;
//    cl_qmap_t mcm_port_tbl;
//    ib_member_rec_t mcmember_rec;
//    boolean_t well_known;
//    unsigned full_members;
//  } osm_mgrp_t;
  //
//  /* from the partitions mcast group tbl */
//  typedef struct sr_mcgroups
//  {
//    ib_net16_t mlid;
//    boolean_t well_known;
//    unsigned port_members;
//    uint8_t port_num_array[MAX_NUM_NODES];
//    uint64_t port_guid_array[MAX_NUM_NODES];
//  } sr_MCGroups_t;
  
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = -3492362748381135391L;

  /**  true, if this is a well known group **/
  public boolean well_known;

  /**  The network ordered LID of this Multicast Group **/
  public int     mlid;
  
  /**  the number of ports in the mcast group **/
  public int     port_members;

  /**  an array of port numbers, corresponding to the guid **/
  public short [] port_number;
  
  /**  an array of port guids that are members of this group **/
  public long [] port_guids;


  /************************************************************
   * Method Name:
   *  SBN_MulticastGroup
   */
   /**
   * Default constructor
   *
   ***********************************************************/
  public SBN_MulticastGroup()
  {
  }


  /************************************************************
   * Method Name:
   *  SBN_MulticastGroup
  **/
  /**
   * The fully parameterized constructor used by the native layer
   * to create an instance of this peer class.
   *
   *
   * @param well_known
   * @param mlid
   * @param port_members
   * @param port_number
   * @param port_guids
   ***********************************************************/
  public SBN_MulticastGroup(boolean well_known, int mlid, int port_members, short[] port_number, long[] port_guids)
  {
    super();
    this.well_known = well_known;
    this.mlid = mlid;
    this.port_members = port_members;
    this.port_number = port_number;
    this.port_guids = port_guids;
  }

  public boolean isMember(long g)
  {
    // check this guid to see if its in the table
    if(g != 0)
    {
      for(long pg: port_guids)
      {
        if(g == pg)
          return true;
      }
    }
    return false;
  }

  public boolean isMember(long g, short portNum)
  {
    // check this guid and port number to see if its in the table
    if((g != 0) && (portNum > 0))
    {
      int n = 0;
      for(long pg: port_guids)
      {
        short pn = port_number[n++];
        if((g == pg) && (portNum == pn))
          return true;
      }
    }
    return false;
  }

  public String toMulticastGroupString()
  {
    StringBuffer buff = new StringBuffer();
    String format = "%14s: %s\n";
    
    // not really a group, if there is only one (or fewer) members
      buff.append(String.format(format, "mlid", mlid + ", (0x" + Integer.toHexString(mlid) + ")"));
      buff.append(String.format(format, "well known", well_known));
      buff.append(String.format(format, "# port members", port_members));
      buff.append(String.format(format, "table size", port_guids.length + " guids, and " + port_number.length + " ports"));
    return buff.toString();
  }


  public String toMulticastTableString(OSM_Fabric fab, String prepend)
  {
    StringBuffer buff = new StringBuffer();
    String format = "%s%4d guid: %s:%2d, lid: %4d, (%s)\n";
    
    // I think the guid table and port table must be the same
    if(port_guids.length != port_number.length)
    {
      logger.severe("The guid array and port array in the multicast table are not the same");
      return "The guid array and port array in the multicast table are not the same";
    }
    int n = 0;
    for(long pg: port_guids)
    {
      short pn = port_number[n++];
      IB_Guid g = new IB_Guid(pg);
      try
      {
        int lid = 0;
        if(fab != null)
          lid = fab.getLidFromGuid(g);
        buff.append(String.format(format, prepend, n, g.toColonString(), pn, lid,"0x" + Integer.toHexString(lid)));
      }
      catch (Exception e)
      {
        logger.severe("Couldn't find lid for that guid in the fabric");
        e.printStackTrace();
      }
    }
    return buff.toString();

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
    return "SBN_MulticastGroup [well_known=" + well_known + ", mlid=" + mlid + ", port_members="
        + port_members + ", port_number=" + Arrays.toString(port_number) + ", port_guids="
        + IB_Guid.toGuidArrayString(port_guids) + "]";
  }
}
