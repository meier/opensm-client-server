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
 *        file: PFM_PortCounterRange.java
 *
 *  Created on: Mar 11, 2013
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import gov.llnl.lc.infiniband.opensm.plugin.data.PFM_Port.PortCounterName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**********************************************************************
 * Given a set of PFM_PortChange objects, find all the mins and max's of
 * all the port counters.
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Mar 11, 2013 12:37:36 PM
 **********************************************************************/
public class PFM_PortChangeRange implements Serializable
{
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = -462965464036265362L;
  private ArrayList <Long>   minvals = new ArrayList <Long> ();
  private ArrayList <String> mindesc = new ArrayList <String> ();
  
  private ArrayList <Long>   maxvals = new ArrayList <Long> ();
  private ArrayList <String> maxdesc = new ArrayList <String> ();
  
    /************************************************************
   * Method Name:
   *  PFM_PortCounterRange
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param name
   * @param minimum
   * @param portChange
   ***********************************************************/
  public PFM_PortChangeRange( HashMap<String, PFM_PortChange> portChanges)
  {
    super();
    initRange(portChanges);
  }
  
  private boolean fillArrayList(ArrayList <Long> list, long val)
  {
    // FIXME:  initialize it (new - add) or replace it (set)
    for (PortCounterName counter : PortCounterName.PFM_ALL_COUNTERS)
    {
      list.add(new Long(val));
    }
    return true;
  }
  
  private boolean fillArrayList(ArrayList <String> list, String desc)
  {
    // FIXME:  initialize it (new - add) or replace it (set)
    for (PortCounterName counter : PortCounterName.PFM_ALL_COUNTERS)
    {
      list.add(desc);
    }
    return true;
  }
  
  private boolean initRange(HashMap<String, PFM_PortChange> changes)
  {
    // initialize all arrays, because I need to work on a fully constructed
    // set, so I can access them randomly by ordinal value
    fillArrayList(minvals, -1L);         // the minimum values for all counters
    fillArrayList(maxvals, -1L);         // the maximum values for all counters
    fillArrayList(mindesc, "unknown");   // description of the minumum, the node+portnum
    fillArrayList(maxdesc, "unknown");   // description of the maximum, the node+portnum 
    
    // *NOTE* arrays are organized in order by ordinal value of counter names
    
    // loop through all of the changes
    return updateRange(changes);
  }
  
  public boolean updateRange(HashMap<String, PFM_PortChange> changes)
  {
    // assume these are already initialized, and we just want to update
    // the values by checking additional HashMaps of changes
    
    // loop through all of the changes
     for(Map.Entry<String, PFM_PortChange> changeMapEntry: changes.entrySet())
    {
       for (PortCounterName counter : PortCounterName.PFM_ALL_COUNTERS)
       {
         long value = changeMapEntry.getValue().getDelta_port_counter(counter);
         long currMin = minvals.isEmpty() ? -1L: minvals.get(counter.ordinal()).longValue();
         long currMax = maxvals.isEmpty() ? -1L: maxvals.get(counter.ordinal()).longValue();
         
         if((currMin == -1L) || (currMin > value))
         {
           minvals.set(counter.ordinal(), new Long(value));
           mindesc.set(counter.ordinal(), changeMapEntry.getKey());
         }
         
         if((currMax == -1L) || (currMax < value))
         {
           maxvals.set(counter.ordinal(), new Long(value));
           maxdesc.set(counter.ordinal(), changeMapEntry.getKey());
         }        
       }
     }
    return false;
  }
  
  public long getMaxPortCounterValue(PortCounterName name)
  {
     return maxvals.get(name.ordinal());
  }
  
  public String getMaxPortCounterPortDescription(PortCounterName name)
  {
     return maxdesc.get(name.ordinal());
  }
  
  public long getMinPortCounterValue(PortCounterName name)
  {
     return minvals.get(name.ordinal());
  }
  
  public String getMinPortCounterPortDescription(PortCounterName name)
  {
     return mindesc.get(name.ordinal());
  }
  
  /************************************************************
   * Method Name:
   *  toRangeOfChangeString
  **/
  /**
   * Describe the method here
   *
   * @see java.lang.Object#toString()
   *
   * @return
   ***********************************************************/
  
  public String toRangeOfChangeString()
  {
    StringBuffer buff = new StringBuffer();
    
    for (PortCounterName counter : PortCounterName.PFM_ALL_COUNTERS)
    {
      long currMin = minvals.get(counter.ordinal()).longValue();
      long currMax = maxvals.get(counter.ordinal()).longValue();
      
      buff.append(counter.name() + ": max=" + currMax + " (" + maxdesc.get(counter.ordinal()) + ")\n");
      buff.append(counter.name() + ": min=" + currMin + " (" + mindesc.get(counter.ordinal()) + ")\n");
    }
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
    return "PFM_PortChangeRange [minvals=" + minvals + ", mindesc=" + mindesc + ", maxvals="
        + maxvals + ", maxdesc=" + maxdesc + "]";
  }
  
}
