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
 *        file: OSM_NodeType.java
 *
 *  Created on: Jan 18, 2012
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**********************************************************************
 * Describe purpose and responsibility of OSM_NodeType
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Jan 18, 2012 10:02:26 AM
 **********************************************************************/
public enum OSM_NodeType
{
  UNKNOWN(0, "??", "UNKNOWN"),
  CA_NODE(1, "CA", "Channel Adapter"),
  SW_NODE(2, "SW", "Switch"),
  RT_NODE(3, "RT", "Router");

public static final EnumSet<OSM_NodeType> OSMNODE_ALL_TYPES = EnumSet.allOf(OSM_NodeType.class);
  
  private static final Map<Integer,OSM_NodeType> lookup = new HashMap<Integer,OSM_NodeType>();

  static 
  {
    for(OSM_NodeType s : OSMNODE_ALL_TYPES)
         lookup.put(s.getType(), s);
  }

  private int Type;
  private String FullName;
  private String AbrevName;

private OSM_NodeType(int Type_num, String abrevName, String fullName)
{
    this.Type = Type_num;
    this.AbrevName = abrevName;
    this.FullName = fullName;
}

public int getType()
{
  return Type;
}

/************************************************************
 * Method Name:
 *  getFullName
 **/
/**
 * Returns the value of fullName
 *
 * @return the fullName
 *
 ***********************************************************/

public synchronized String getFullName()
{
  return FullName;
}

/************************************************************
 * Method Name:
 *  getAbrevName
 **/
/**
 * Returns the value of abrevName
 *
 * @return the abrevName
 *
 ***********************************************************/

public synchronized String getAbrevName()
{
  return AbrevName;
}

public static OSM_NodeType get(int type_num)
{ 
    return lookup.get(type_num); 
}

public static OSM_NodeType get(SBN_Node node)
{
  // get the node type from the sbn_node
  if(node == null)
    return OSM_NodeType.UNKNOWN;
  
  return get((int)node.node_type);
}

public static OSM_NodeType get(OSM_Node node)
{
  // get the node type from the sbn_node
  if(node == null || node.sbnNode == null)
    return OSM_NodeType.UNKNOWN;
  
  return get(node.sbnNode);
}

public boolean isRouterType()
{
  // am I a switch
  return (this.compareTo(OSM_NodeType.RT_NODE) == 0);
}

public boolean isSwitchType()
{
  // am I a switch
  return (this.compareTo(OSM_NodeType.SW_NODE) == 0);
}

public static boolean isSwitchNode(SBN_Node node)
{
  if(node == null)
    return false;
  return OSM_NodeType.get(node).isSwitchType();
  
}


public boolean isEdgeType()
{
  // am I a CA Node?
  return (this.compareTo(OSM_NodeType.CA_NODE) == 0);
}

public static boolean isEdgeNode(SBN_Node node)
{
  if(node == null)
    return false;
  return OSM_NodeType.get(node).isEdgeType();
  
}

}
