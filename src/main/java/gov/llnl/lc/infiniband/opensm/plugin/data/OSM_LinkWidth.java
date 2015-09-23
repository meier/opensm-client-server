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
 *        file: OSM_LinkWidth.java
 *
 *  Created on: Jan 13, 2012
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**********************************************************************
 * This enum represents the various link widths.  They are generally
 * derived from the osm_helper files and osm_dump.c.  The widths are
 * comparable in the sense that the values are ordered from small to
 * large.
 * <p>
 * @see  related classes and interfaces (see the osm_helper files)
 *       also see osm_dump.c
 *
 * @author meier3
 * 
 * @version Jan 13, 2012 9:26:32 AM
 **********************************************************************/
public enum OSM_LinkWidth
{
  ONE_X(     1, 1,    0, "1x"),
  FOUR_X(    2, 4,  100, "4x"),
  EIGHT_X(   4, 8,  200, "8x"),
  TWELVE_X(  8, 12, 300, "12x"),
  UNKNOWN(   0, 1,    0, "1x");   // default to 1x if not known
  
//  fprintf(file, "PHY=%s LOG=%s SPD=%s\n",
//      p_physp->port_info.link_width_active == 1 ? "1x" :
//      p_physp->port_info.link_width_active == 2 ? "4x" :
//      p_physp->port_info.link_width_active == 8 ? "12x" :
//      "??",

//  static const char *lwa_str_fixed_width[] = {
//    "???",
//    "1x ",
//    "4x ",
//    "???",
//    "8x ",
//    "???",
//    "???",
//    "???",
//    "12x"
//  };
//
//  const char *osm_get_lwa_str(IN uint8_t lwa)
//  {
//    if (lwa > 8)
//      return lwa_str_fixed_width[0];
//    else
//      return lwa_str_fixed_width[lwa];
//  }

 // from IB_TYPES.H
  
//
//  #define IB_LINK_WIDTH_ACTIVE_1X     1
//  #define IB_LINK_WIDTH_ACTIVE_4X     2
//  #define IB_LINK_WIDTH_ACTIVE_8X     4
//  #define IB_LINK_WIDTH_ACTIVE_12X    8
  
  public static final EnumSet<OSM_LinkWidth> OSMLINK_ALL_WIDTHS = EnumSet.allOf(OSM_LinkWidth.class);
  
  private static final Map<Integer,OSM_LinkWidth> lookup = new HashMap<Integer,OSM_LinkWidth>();

  static 
  {
    for(OSM_LinkWidth s : OSMLINK_ALL_WIDTHS)
         lookup.put(s.getWidth(), s);
  }

  private int Width;
  private int multiplier;
  private int offset;
  private String WidthName;

private OSM_LinkWidth(int Width_num, int multiplier, int offset, String Name)
{
    this.Width = Width_num;
    this.multiplier = multiplier;
    this.offset = offset;
    this.WidthName = Name;
}

public int getWidth()
{
  return Width;
  }

public int getMultiplier()
{
  return multiplier;
  }

public int getOffset()
{
  return offset;
  }

public String getWidthName()
{
  return WidthName;
  }

public static OSM_LinkWidth get(int Width_num)
{ 
    return lookup.get(Width_num); 
}

public static OSM_LinkWidth get(OSM_Port port)
{ 
  if(port == null)
    return UNKNOWN;  
  return get(port.getSbnPort());
}

public static OSM_LinkWidth get(SBN_Port port)
{ 
  if(port == null)
    return UNKNOWN;  
  return get(port.port_info);
}


public static OSM_LinkWidth get(SBN_PortInfo portInfo)
{ 
  if(portInfo == null)
    return UNKNOWN;  
  return get(portInfo.link_width_active);
}

}
