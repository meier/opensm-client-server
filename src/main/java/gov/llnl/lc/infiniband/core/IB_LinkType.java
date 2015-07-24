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
 *        file: IB_LinkType.java
 *
 *  Created on: Jan 18, 2012
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.core;

import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_NodeType;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**********************************************************************
 * Each link can be categorized by one of three different types, largely
 * dependent upon the endpoints.  If both endpoints are ports from switches,
 * then the link is considered to be a switch link.  If at lease one of the
 * endpoints is a channel adapter, then the link is considered to be an edge
 * link.  If one of the endpoints is a router (almost never), then its a
 * router link.  Finally, if the type of the endpoints can't be determined
 * then it is UNKNOWN.
 * <p>
 * @see  OSM_NodeType
 *
 * @author meier3
 * 
 * @version Jan 18, 2012 4:58:09 PM
 **********************************************************************/
public enum IB_LinkType
{
  /** Unknown link **/
  UNKNOWN(0, "UNKNOWN"),
  /** Channel adapter, or edge link **/
  CA_LINK(1, "Edge"),
  /** Switch link **/
  SW_LINK(2, "Switch"),
  /** Router link **/
  RT_LINK(3, "Router");
  
public static final EnumSet<IB_LinkType> LINK_ALL_TYPES = EnumSet.allOf(IB_LinkType.class);
  
  private static final Map<Integer,IB_LinkType> lookup = new HashMap<Integer,IB_LinkType>();

  static 
  {
    for(IB_LinkType s : LINK_ALL_TYPES)
         lookup.put(s.getType(), s);
  }

  private int Type;
  private String Name;

private IB_LinkType(int Type_num, String Name)
{
    this.Type = Type_num;
    this.Name = Name;
}

public static IB_LinkType get(OSM_NodeType n1, OSM_NodeType n2)
{
  // need both node types to make a determination
  if(n1 == null || n2 == null)
    return IB_LinkType.UNKNOWN;

  // both nodes must be of type SW for it to be considered a switch link
  if(n1.isSwitchType() && n2.isSwitchType())
    return IB_LinkType.SW_LINK;
  
  // if at least one node is of type CA, then so is the link
  if(n1.isEdgeType() || n2.isEdgeType())
    return IB_LinkType.CA_LINK;
  
  // if at least one node is of type RT, then so is the link
  if(n1.isRouterType() || n2.isRouterType())
    return IB_LinkType.RT_LINK;
  
  return IB_LinkType.UNKNOWN;
}

/************************************************************
 * Method Name:
 *  get
**/
/**
 * Determines the IB_LinkType of the supplied link
 *
 * @param link  the link whos type is to be determined
 * @return  the type of the link
 ***********************************************************/
public static IB_LinkType get(IB_Link link)
{
  // get the node type from the sbn_node
  if(link == null || link.Endpoint1 == null || link.Endpoint2 == null)
    return IB_LinkType.UNKNOWN;
  
  return get(link.Endpoint1.getNodeType(), link.Endpoint2.getNodeType());
}

public int getType()
{
  return Type;
}

/************************************************************
 * Method Name:
 *  getName
 **/
/**
 * Returns the value of fullName
 *
 * @return the fullName
 *
 ***********************************************************/

public synchronized String getName()
{
  return Name;
}


}
