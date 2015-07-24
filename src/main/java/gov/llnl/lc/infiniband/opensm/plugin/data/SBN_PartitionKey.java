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

import java.io.Serializable;

/**********************************************************************
 * An <code>SBN_PartitionKey</code> object contains relevant information about
 * a subnet partition.
 * To the extent that it is possible, it mirrors members in the 
 * <code>struct osm_prtn</code>.
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
public class SBN_PartitionKey implements Serializable
{
//  typedef struct osm_prtn {
//    cl_map_item_t map_item;
//    ib_net16_t pkey;
//    uint8_t sl;
//    cl_map_t full_guid_tbl;
//    cl_map_t part_guid_tbl;
//    char name[32];
//    osm_mgrp_t **mgrps;
//    int nmgrps;
//  } osm_prtn_t;
  
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = 5963910983535358841L;

  /**  true, if this is a well known partition **/
  public boolean well_known;

  /**  the partitions service level **/
  public short   sl;
  /**  the partition key **/
  public int     pkey;
  /**  describe mlid here **/
  public int     mlid;
  /**  the number of full members **/
  public int     full_members;

  /**  an array of guids that have partial membership to this partition **/
  public long [] partial_member_guids;
  /**  an array of guids that are full partition members **/
  public long [] full_member_guids;

  /**  the name of the partition **/
  public String  Name;

 
  /************************************************************
   * Method Name:
   *  SBN_PartitionKey
  **/
  /**
   * The fully parameterized constructor used by the native layer
   * to create an instance of this peer class.
   *
   * @param well_known
   * @param sl
   * @param pkey
   * @param mlid
   * @param full_members
   * @param partial_member_guids
   * @param full_member_guids
   * @param name
   ***********************************************************/
  public SBN_PartitionKey(boolean well_known, short sl, int pkey, int mlid, int full_members,
      long[] partial_member_guids, long[] full_member_guids, String name)
  {
    super();
    this.well_known = well_known;
    this.sl = sl;
    this.pkey = pkey;
    this.mlid = mlid;
    this.full_members = full_members;
    this.partial_member_guids = partial_member_guids;
    this.full_member_guids = full_member_guids;
    Name = name;
  }

  public boolean isMember(long g, boolean full)
  {
    // check this guid to see if it is full or partial member
    // the the full argument is true, then return true only if a full member
    // otherwise return true if partial or full
    
    if(g > 0)
    {
      for(long pg: full_member_guids)
      {
        if(g == pg)
          return true;
      }
      // if here, didn't find it in the full table, so conditionally check partial
      if(full)
        return false;
      
      for(long pg: partial_member_guids)
      {
        if(g == pg)
          return true;
      }
    }
    return false;
  }

  public String toPartitionKeyString()
  {
    StringBuffer buff = new StringBuffer();
    String format = "%15s: %s\n";
    
      buff.append(String.format(format, "Partition Name", Name ));
      buff.append(String.format(format, "pKey", "0x" + Integer.toHexString(pkey)));
      buff.append(String.format(format, "mlid", mlid + ", (0x" + Integer.toHexString(mlid) + ")"));
      buff.append(String.format(format, "well known", well_known));
      buff.append(String.format(format, "sl", sl));
      buff.append(String.format(format, "full members", full_member_guids.length));
      buff.append(String.format(format, "partial members", partial_member_guids.length));
    return buff.toString();
  }


  public String toFullMemberString(OSM_Fabric fab, String prepend)
  {
    StringBuffer buff = new StringBuffer();
    for(long pg: full_member_guids)
        buff.append(prepend + fab.getNodeIdString(new IB_Guid(pg)) + "\n");
    return buff.toString();
  }

  public String toPartialMemberString(OSM_Fabric fab, String prepend)
  {
    StringBuffer buff = new StringBuffer();
    for(long pg: partial_member_guids)
        buff.append(prepend + fab.getNodeIdString(new IB_Guid(pg)) + "\n");
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
    return "SBN_PartitionKey [well_known=" + well_known + ", sl=" + sl + ", pkey=" + pkey
        + ", mlid=" + mlid + ", full_members=" + full_members + ", partial_member_guids="
        + IB_Guid.toGuidArrayString(partial_member_guids) + ", full_member_guids="
        + IB_Guid.toGuidArrayString(full_member_guids) + ", Name=" + Name + "]";
  }

}
