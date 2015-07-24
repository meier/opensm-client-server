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
 *        file: OSM_LinkState.java
 *
 *  Created on: Jan 19, 2012
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import gov.llnl.lc.infiniband.core.IB_Link;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**********************************************************************
 * The state of a link is a function of the states of its endpoints, or
 * ports.
 * <p>
 * @see  OSM_PortState
 *
 * @author meier3
 * 
 * @version Jan 19, 2012 4:09:46 PM
 **********************************************************************/
public enum OSM_LinkState
{
  /* from ib_types.h
   * 
#define IB_LINK_NO_CHANGE 0
#define IB_LINK_DOWN      1
#define IB_LINK_INIT      2
#define IB_LINK_ARMED     3
#define IB_LINK_ACTIVE    4
#define IB_LINK_ACT_DEFER 5

port_state = ib_port_info_get_port_state(&p_physp->port_info);

ib_port_info_get_port_state(IN const ib_port_info_t * const p_pi)
{
  return ((uint8_t) (p_pi->state_info1 & IB_PORT_STATE_MASK));
}


      port_state == IB_LINK_ACTIVE ? "ACT" :
      port_state == IB_LINK_ARMED ? "ARM" :
      port_state == IB_LINK_INIT ? "INI" : "DWN",

   */
  
  DOWN(      0, "Down"),
  INIT(      1, "Initializing"),
  ARMED(     2, "Armed"),
  ACTIVE(    3, "Active"),
  UNKNOWN(   4, "Unknown");

  public static final EnumSet<OSM_LinkState> OSMLINK_ALL_STATES = EnumSet.allOf(OSM_LinkState.class);
  
  private static final Map<Integer,OSM_LinkState> lookup = new HashMap<Integer,OSM_LinkState>();

  static 
  {
    for(OSM_LinkState s : OSMLINK_ALL_STATES)
         lookup.put(s.getState(), s);
  }

  private int State;
  private String StateName;

private OSM_LinkState(int State, String Name)
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

public static OSM_LinkState get(int state_num)
{ 
    return lookup.get(state_num); 
}


/**********************************************************************
 * The state of a link is UNKNOWN, unless it can be determined to be 
 * ACTIVE or DOWN.
 *   A link is ARMED if both ports are ARMED.
 *   A link is INIT if both ports are INIT.
 *   A link is ACTIVE if both ports are ACTIVE.
 *   A link is DOWN if at least one of its ports are DOWN.
 *   in ALL other cases, the link state is UNKNOWN  
 * <p>
 * @see  OSM_PortState
 *
 * @param   IB_Link link
 **********************************************************************/
public static OSM_LinkState get(IB_Link link)
{ 
  if((link == null) || (link.Endpoint1 == null) || (link.Endpoint2 == null))
    return UNKNOWN;
  
  if((link.Endpoint1.getState() == OSM_PortState.ACTIVE) && (link.Endpoint2.getState() == OSM_PortState.ACTIVE))
    return ACTIVE;
  
  if((link.Endpoint1.getState() == OSM_PortState.ARMED) && (link.Endpoint2.getState() == OSM_PortState.ARMED))
    return ARMED;
  
  if((link.Endpoint1.getState() == OSM_PortState.INIT) && (link.Endpoint2.getState() == OSM_PortState.INIT))
    return INIT;
  
  if((link.Endpoint1.getState() == OSM_PortState.DOWN) || (link.Endpoint2.getState() == OSM_PortState.DOWN))
    return DOWN;
  
  return UNKNOWN;
}

}
