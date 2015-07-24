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
 *        file: OSM_EventStats.java
 *
 *  Created on: Nov 13, 2014
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.event;

import java.io.Serializable;
import java.util.Arrays;

/**********************************************************************
 * Describe purpose and responsibility of OSM_EventStats
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Nov 13, 2014 2:59:27 PM
 **********************************************************************/
public class OSM_EventStats implements Serializable
{
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = -8940846616399843974L;

  /** the cumulative number of events of each type, since the service started **/
  public long [] event_counters;


  /************************************************************
   * Method Name:
   *  OSM_EventStats
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param event_counters
   ***********************************************************/
  public OSM_EventStats(long[] event_counters)
  {
    super();
    this.event_counters = event_counters;
  }


  /************************************************************
   * Method Name:
   *  getCounter
   */
   /** Returns the value of the desired event counter.
   *
   * @see     OsmEvent
   *
   * @param name
   * @return  the value of the specified event counter
   ***********************************************************/
  public long getCounter(OsmEvent eventType)
  {
    long rtnval = 0L;
    if((event_counters != null) && (event_counters.length > 0) && (eventType != null))
    {
       /* assume all or nothing array */
      if((eventType.getEvent() > -1) && (eventType.getEvent() < event_counters.length))
        rtnval = event_counters[eventType.getEvent()];
    }
   return rtnval; 
  }

  /************************************************************
   * Method Name:
   *  incrementCounter
   */
   /** Increments the running total for the specified event
   *
   * @see     OsmEvent
   *
   * @param name
   * @return  the value of the specified event counter
   ***********************************************************/
  public long incrementCounter(OsmEvent eventType)
  {
    long rtnval = 0L;
    if((event_counters != null) && (event_counters.length > 0))
    {
       /* assume all or nothing array */
      rtnval = ++event_counters[eventType.getEvent()];
    }
   return rtnval; 
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
    return "OSM_EventStats [event_counters=" + Arrays.toString(event_counters) + "]";
  }


}
