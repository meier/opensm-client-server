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
 *        file: OSM_LinkSubState.java
 *
 *  Created on: Feb 14, 2012
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import gov.llnl.lc.infiniband.core.IB_Link;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**********************************************************************
 * The substate of a link is a function of its main state and the states
 * of its endpoints, or ports.
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Feb 14, 2012 8:00:32 AM
 **********************************************************************/
public enum OSM_LinkSubState
{
  LINKUP(   0, "LinkUp"),
  POLLING(  1, "Polling"),
  SLEEP(    2, "Sleep"),
  UNKNOWN(  3, "Unknown");

  public static final EnumSet<OSM_LinkSubState> OSMLINK_ALL_SUBSTATES = EnumSet.allOf(OSM_LinkSubState.class);
  
  private static final Map<Integer,OSM_LinkSubState> lookup = new HashMap<Integer,OSM_LinkSubState>();

  static 
  {
    for(OSM_LinkSubState s : OSMLINK_ALL_SUBSTATES)
         lookup.put(s.getSustate(), s);
  }

  private int State;
  private String StateName;

private OSM_LinkSubState(int State, String Name)
{
    this.State = State;
    this.StateName = Name;
}

public int getSustate()
{
  return State;
  }

public String getSubstateName()
{
  return StateName;
  }

public static OSM_LinkSubState get(int state_num)
{ 
    return lookup.get(state_num); 
}


public static OSM_LinkSubState get(IB_Link link)
{
  // LinkUp only if both are active
  // Sleep if both are down
  // Polling if none of the above
  
  if((link == null) || (link.Endpoint1 == null) || (link.Endpoint2 == null))
    return UNKNOWN;
  
  if((link.Endpoint1.getState() == OSM_PortState.ACTIVE) && (link.Endpoint2.getState() == OSM_PortState.ACTIVE))
    return LINKUP;
  
  if((link.Endpoint1.getState() == OSM_PortState.DOWN) && (link.Endpoint2.getState() == OSM_PortState.DOWN))
    return SLEEP;
  
  return POLLING;
}

}
