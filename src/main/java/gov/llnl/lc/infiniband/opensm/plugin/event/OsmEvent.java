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
 *        file: OsmEvent.java
 *
 *  Created on: Jul 28, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.event;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;


/**********************************************************************
 * Describe purpose and responsibility of OsmEvent
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Nov 12, 2014 4:08:24 PM
 **********************************************************************/
public enum OsmEvent implements Serializable
{
  /* 
   * as of Nov 12, 2014, these are the supported event types
 typedef enum {
  OSM_EVENT_ID_PORT_ERRORS = 0,
  OSM_EVENT_ID_PORT_DATA_COUNTERS,
  OSM_EVENT_ID_PORT_SELECT,
  OSM_EVENT_ID_TRAP,
  OSM_EVENT_ID_SUBNET_UP,
  OSM_EVENT_ID_HEAVY_SWEEP_START,
  OSM_EVENT_ID_HEAVY_SWEEP_DONE,
  OSM_EVENT_ID_UCAST_ROUTING_DONE,
  OSM_EVENT_ID_STATE_CHANGE,
  OSM_EVENT_ID_SA_DB_DUMPED,
  OSM_EVENT_ID_LFT_CHANGE,
  OSM_EVENT_ID_MAX
} osm_epi_event_id_t;

   */
  OSM_EVENT_PORT_ERRORS(       0, "port error"),    
  OSM_EVENT_PORT_DATA_COUNTERS(1, "port data counters"),    
  OSM_EVENT_PORT_SELECT(       2, "port select"),    
  OSM_EVENT_TRAP(              3, "trap"),    
  OSM_EVENT_SUBNET_UP(         4, "subnet up"),    
  OSM_EVENT_HEAVY_SWEEP_START( 5, "sweep start"),    
  OSM_EVENT_HEAVY_SWEEP_DONE(  6, "sweep done"),    
  OSM_EVENT_UCAST_ROUTING_DONE(7, "routing done"),    
  OSM_EVENT_STATE_CHANGE(      8, "state change"),    
  OSM_EVENT_SA_DB_DUMPED(      9, "sa db dumped"),
  OSM_EVEMT_LFT_CHANGE(       10, "lft change"),
  OSM_EVENT_TIMEOUT(          11, "event timeout"),    
  OSM_EVENT_MAX(              12, "final event");
  
  public static final EnumSet<OsmEvent> OSM_STAT_EVENTS = EnumSet.range(OSM_EVENT_PORT_ERRORS, OSM_EVENT_TIMEOUT);
  public static final EnumSet<OsmEvent> OSM_ALL_EVENTS = EnumSet.allOf(OsmEvent.class);
  
  private static final Map<Integer,OsmEvent> lookup = new HashMap<Integer,OsmEvent>();

  static 
  {
    for(OsmEvent s : OSM_ALL_EVENTS)
         lookup.put(s.getEvent(), s);
  }

  private int Event;
  private String EventName;

private OsmEvent(int Event, String Name)
{
    this.Event = Event;
    this.EventName = Name;
}

public int getEvent()
{
  return Event;
  }

public String getEventName()
{
  return EventName;
}

public static String getEventEnumTable()
{
  StringBuffer buffer = new StringBuffer();
  
  for(OsmEvent s : OSM_ALL_EVENTS)
  {
    buffer.append(s.getEvent() + ": " + s.getEventName() + "\n");
  }
  return buffer.toString();
}

public static OsmEvent get(int Event)
{ 
    return lookup.get(Event); 
}


}
