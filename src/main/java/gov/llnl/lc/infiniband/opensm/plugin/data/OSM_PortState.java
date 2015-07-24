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
 *        file: OSM_PortState.java
 *
 *  Created on: Jan 13, 2012
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**********************************************************************
 * Describe purpose and responsibility of OSM_PortState
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Jan 13, 2012 7:59:22 AM
 **********************************************************************/
public enum OSM_PortState
{
  NOP(       0, "No State Change (NOP)"),
  DOWN(      1, "Down"),
  INIT(      2, "Initializing"),
  ARMED(     3, "Armed"),
  ACTIVE(    4, "Active"),
  ACTDEFER(  5, "Active Defered"),
  UNKNOWN(   6, "Unknown");
  
private static final short IB_PORT_STATE_MASK   =   0x0F;
//#define IB_PORT_LMC_MASK      0x07
//#define IB_PORT_LMC_MAX       0x07
//#define IB_PORT_MPB_MASK      0xC0
//#define IB_PORT_MPB_SHIFT     6
//#define IB_PORT_LINK_SPEED_SHIFT    4
//#define IB_PORT_LINK_SPEED_SUPPORTED_MASK 0xF0
//#define IB_PORT_LINK_SPEED_ACTIVE_MASK    0xF0
//#define IB_PORT_LINK_SPEED_ENABLED_MASK   0x0F
//#define IB_PORT_PHYS_STATE_MASK     0xF0
//return ((uint8_t) (p_pi->state_info1 & IB_PORT_STATE_MASK));
//#define IB_LINK_NO_CHANGE 0
//#define IB_LINK_DOWN      1
//#define IB_LINK_INIT    2
//#define IB_LINK_ARMED     3
//#define IB_LINK_ACTIVE    4
//#define IB_LINK_ACT_DEFER 5
//static const char *const __ib_port_state_str[] = {
//  "No State Change (NOP)",
//  "DOWN",
//  "INIT",
//  "ARMED",
//  "ACTIVE",
//  "ACTDEFER",
//  "UNKNOWN"
//};

  
  public static final EnumSet<OSM_PortState> OSMPORTS_ALL_STATES = EnumSet.allOf(OSM_PortState.class);
  
  private static final Map<Integer,OSM_PortState> lookup = new HashMap<Integer,OSM_PortState>();

  static 
  {
    for(OSM_PortState s : OSMPORTS_ALL_STATES)
         lookup.put(s.getState(), s);
  }

  private int State;
  private String StateName;

private OSM_PortState(int State, String Name)
{
    this.State = State;
    this.StateName = Name;
}

public int getState()
{
  return State;
  }

public String getStateName()
{
  return StateName;
  }

public static OSM_PortState get(int state_num)
{ 
    return lookup.get(state_num); 
}

public static OSM_PortState get(short state_info1)
{
  return get((int)(state_info1 & IB_PORT_STATE_MASK)); 
}

public static OSM_PortState get(OSM_Port port)
{ 
  if(port == null)
    return UNKNOWN;  
  return get(port.getSbnPort());
}

public static OSM_PortState get(SBN_Port port)
{ 
  if(port == null)
    return UNKNOWN;  
  return get(port.port_info);
}

public static OSM_PortState get(SBN_PortInfo portInfo)
{
  if(portInfo == null)
    return UNKNOWN;  
  return get(portInfo.state_info1);
}


}
