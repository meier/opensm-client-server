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
 *        file: OSM_LinkSpeed.java
 *
 *  Created on: Jan 13, 2012
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import gov.llnl.lc.logging.CommonLogger;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**********************************************************************
 * This enum represents the various link speeds.  They are generally
 * derived from the osm_helper files and osm_dump.c.  The speed includes
 * extraneous speed values, but are comparable in the sense that the
 * speeds are ordered from slowest to fastest.  
 * <p>
 * @see  related classes and interfaces (see the osm_helper files)
 *       also see osm_dump.c
 *
 * @author meier3
 * 
 * @version Jan 13, 2012 9:26:32 AM
 **********************************************************************/
public enum OSM_LinkSpeed implements CommonLogger
{
  EXT(        0, "Ext",      "Ext"),
  TWOFIVE(    1, "2.5",      "SDR"),
  FIVE(       2, "5",        "DDR"),
  QM(         3, "Unknown",  "Unknown"),
  TEN(        4, "10",       "QDR"),
  STD(        5, "Std",      "Std"),
  FOURTEEN(   6, "14",       "FDR"),
  TWENTYFIVE( 7, "25",       "EDR"),
  FDR10(      8, "FDR10",    "FDR10"),
  UNKNOWN(    9, "Unknown",  "Unknown");

 // FIXME TODO the above order needs to be from lowest to highest
  // because it should be comparable by natural order
  //
  // FDR10 is out of order,  Where does EDR go, and what do I do
  // with STD, & QM??
  
public static final short FDR10_MASK   =   0x01;
//  #define FDR10 0x01
public static final short IB_PORT_LINK_SPEED_SHIFT          = 4;
public static final short IB_PORT_LINK_SPEED_SUPPORTED_MASK = 0xF0;
public static final short IB_PORT_LINK_SPEED_ACTIVE_MASK    = 0xF0;
public static final short IB_PORT_LINK_SPEED_ENABLED_MASK   = 0x0F;

public static final short IB_LINK_SPEED_ACTIVE_2_5          = 1;
public static final short IB_LINK_SPEED_ACTIVE_5            = 2;
public static final short IB_LINK_SPEED_ACTIVE_10           = 4;

public static final short IB_LINK_SPEED_EXT_ACTIVE_NONE     = 0;
public static final short IB_LINK_SPEED_EXT_ACTIVE_14       = 1;
public static final short IB_LINK_SPEED_EXT_ACTIVE_25       = 2;
public static final short IB_LINK_SPEED_EXT_DISABLE         = 30;
public static final short IB_LINK_SPEED_EXT_SET_LSES        = 31;

//

//#define IB_LINK_SPEED_ACTIVE_2_5    1
//#define IB_LINK_SPEED_ACTIVE_5      2
//#define IB_LINK_SPEED_ACTIVE_10     4
//#define IB_LINK_SPEED_EXT_ACTIVE_NONE   0
//#define IB_LINK_SPEED_EXT_ACTIVE_14   1
//#define IB_LINK_SPEED_EXT_ACTIVE_25   2
//#define IB_LINK_SPEED_EXT_DISABLE   30
//#define IB_LINK_SPEED_EXT_SET_LSES    31



//  const char *osm_get_lsa_str(IN uint8_t lsa, IN uint8_t lsea, IN uint8_t state,
//            IN uint8_t fdr10)
//  {
//    if (lsa > IB_LINK_SPEED_ACTIVE_10)
//      return lsa_str_fixed_width[3];
//    if (lsea == IB_LINK_SPEED_EXT_ACTIVE_NONE) {
//      if (fdr10)
//        return "FDR10";
//      else
//        return lsa_str_fixed_width[lsa];
//    }
//    if (lsea > IB_LINK_SPEED_EXT_ACTIVE_25)
//      return lsa_str_fixed_width[3];
//    return lsea_str_fixed_width[lsea];
//  }
//
//
//  static const char *lsea_str_fixed_width[] = {
//  "Std ",
//  "14  ",
//  "25  "
//  };


//  osm_get_lsa_str
//  (ib_port_info_get_link_speed_active(p_pi),
//   ib_port_info_get_link_speed_ext_active(p_pi),
//   ib_port_info_get_port_state(p_pi),
//   p_physp->ext_port_info.link_speed_active & FDR10));

 // from IB_TYPES.H
  
//static inline uint8_t OSM_API
//ib_port_info_get_link_speed_ext_active(IN const ib_port_info_t * const p_pi)
//{
//  return ((p_pi->link_speed_ext & 0xF0) >> 4);
//}



//  static inline uint8_t OSM_API
//  ib_port_info_get_link_speed_active(IN const ib_port_info_t * const p_pi)
//  {
//    return ((uint8_t) ((p_pi->link_speed &
//            IB_PORT_LINK_SPEED_ACTIVE_MASK) >>
//           IB_PORT_LINK_SPEED_SHIFT));
//  }
//
//  /*
//  * PARAMETERS
//  * p_pi
//  *   [in] Pointer to a PortInfo attribute.
//  *
//  * RETURN VALUES
//  * Returns the link speed active value assigned to this port.
//  *
//  * NOTES
//  *
//  * SEE ALSO
//  *********/
//
  
  public static final EnumSet<OSM_LinkSpeed> OSMLINK_ALL_SPEEDS = EnumSet.allOf(OSM_LinkSpeed.class);
  
  private static final Map<Integer,OSM_LinkSpeed> lookup = new HashMap<Integer,OSM_LinkSpeed>();

  static 
  {
    for(OSM_LinkSpeed s : OSMLINK_ALL_SPEEDS)
         lookup.put(s.getSpeed(), s);
  }

  private int Speed;
  private String SpeedName;
  private String SpeedValueName;

private OSM_LinkSpeed(int speed_num, String value, String Name)
{
    this.Speed = speed_num;
    this.SpeedValueName = value;
    this.SpeedName = Name;
}

public int getSpeed()
{
  return Speed;
  }

public String getSpeedName()
{
  return SpeedName;
  }

public String getSpeedValueName()
{
  return SpeedValueName;
  }

public static OSM_LinkSpeed get(int speed_num)
{ 
    return lookup.get(speed_num); 
}

public static OSM_LinkSpeed get(OSM_Port port)
{ 
  if(port == null)
    return UNKNOWN;  
  return get(port.getSbnPort());
}

public static OSM_LinkSpeed get(SBN_Port port)
{ 
  if(port == null)
    return UNKNOWN;  
  return get(port.port_info, port.ext_port_info);
}

/************************************************************
 * Method Name:
 *  get
**/
/**
 * Determine the link speed from the SBN_PortInfo object, which
 * is one side of the link.  This needs to be done for both sides
 * to determine the actual speed.
 * 
 *
 * @see     describe related java objects
 *
 * @param portInfo
 * @return
 ***********************************************************/
public static OSM_LinkSpeed get(SBN_PortInfo portInfo, MLX_ExtPortInfo extPortInfo)
{ 
  if(portInfo == null)
    return UNKNOWN;
  
  // get the speed from the portinfo object
  short ls = portInfo.link_speed;
  short link_speed_active     = (short) ((portInfo.link_speed & IB_PORT_LINK_SPEED_ACTIVE_MASK) >> IB_PORT_LINK_SPEED_SHIFT);
  
  // are we using some sort of extended speed mode?
  boolean link_speed_extended = (portInfo.link_speed_ext_enabled != 0);
  
  // if so, override the previous link speed with the new one
  if(link_speed_extended)
  {
    // portInfo.link_speed_ext; == 1  is QDR
    if(portInfo.link_speed_ext == 1)
      return OSM_LinkSpeed.TEN;
 
    // portInfo.link_speed_ext; == 17  is FDR
    if(portInfo.link_speed_ext == 17)
      return OSM_LinkSpeed.FOURTEEN;
    
    logger.severe("Unknown Extended Link Speed: " + portInfo.link_speed_ext);
    return OSM_LinkSpeed.QM;
  }
  
  
  // TODO - support Mellanox extended port info, which includes link speed stuff
  short lsex = portInfo.link_speed_ext;
  short lsee = portInfo.link_speed_ext_enabled;
//  short link_speed_ext_active = extPortInfo.link_speed_active;
//  short link_speed_ext_enabled = extPortInfo.link_speed_enabled;
//  short link_speed_ext_supported = extPortInfo.link_speed_supported;
  
//  logger.severe("Port Info: " + ls + ", and: " + link_speed_active + ", and: " + lsee + ", and: " + lsex);
  
//  if (link_speed_active > IB_LINK_SPEED_ACTIVE_10)
//        return OSM_LinkSpeed.QM;
  
  
  return OSM_LinkSpeed.get(link_speed_active);
//
//  
//    if (link_speed_ext_active == IB_LINK_SPEED_EXT_ACTIVE_NONE)
//  {
//      if (false)
//        return OSM_LinkSpeed.FDR10;
//      else
//        return OSM_LinkSpeed.get(link_speed_active);
//    }
//    
//    // should not be here until I support the extended port info
//    if (link_speed_ext_active > IB_LINK_SPEED_EXT_ACTIVE_25)
//      return OSM_LinkSpeed.QM;
//    return OSM_LinkSpeed.get(link_speed_ext_active + OSM_LinkSpeed.STD.getSpeed());
}

}
