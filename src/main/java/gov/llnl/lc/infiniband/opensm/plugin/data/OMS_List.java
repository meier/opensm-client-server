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
 *        file: OMS_List.java
 *
 *  Created on: Nov 19, 2014
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**********************************************************************
 * Describe purpose and responsibility of OMS_List
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Nov 19, 2014 2:28:09 PM
 **********************************************************************/
public class OMS_List implements Serializable, gov.llnl.lc.logging.CommonLogger
{
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = 937005287977449344L;

  /* keep only the two most recent additions, throw all others away */
  public static final int MAX_COLLECTION_SIZE = 2;
  
  private int MaxSize = MAX_COLLECTION_SIZE;
  
  /* keyed off fabric name and timestamp (insertion order is important) */
  private LinkedHashMap<String, OpenSmMonitorService> omsHistory = new LinkedHashMap<String, OpenSmMonitorService>(MaxSize+1, .75F, false)
  {  
    /**  describe serialVersionUID here **/
    private static final long serialVersionUID = 2260774503118846904L;

    protected boolean removeEldestEntry(Map.Entry<String, OpenSmMonitorService> eldest)  
    { 
      // return true if I want the oldest removed.  If I wanted to "handle" the eldest, I would do it here before returning
      return size() > MaxSize;                                    
    }  
  }; 
     
  public OpenSmMonitorService putCurrentOMS(OpenSmMonitorService oms)
  {
    // by default, do not replace, or update, the OMS with a new instance that has the same timestamp
    //  assume the original instance had data correlated in time
    return putCurrentOMS(oms, false);
  }
  
  public OpenSmMonitorService putCurrentOMS(OpenSmMonitorService oms, boolean replace)
  {
    // always goes at the end, but if this key already exists, then conditionally replace it.
    // normally duplicates (a put with the same key will replace a previous value) will be replaced
    //  the key is based on a timestamp, so should be unique.
    // Expect this to return null, but if not, there was a previous oms with the same key
    if(!replace && (omsHistory.containsKey(OMS_Collection.getOMS_Key(oms))))
      return oms;
    return omsHistory.put(OMS_Collection.getOMS_Key(oms), oms);
  }
  
  public OpenSmMonitorService [] getRecentOMSs(int num)
  {
    // return the most recent ones, or top of the stack
    Object [] oa = omsHistory.values().toArray();
    if((oa == null) || (oa.length < 1))
      return null;
    
    int size = (oa.length > num) ? num: oa.length;
    OpenSmMonitorService [] ffa = new OpenSmMonitorService[size];
    
    // keep the order (grows from 0 up, so 0 is the oldest.  adjust to retrieve most recent
    for(int n=0; n < size; n++)
      ffa[n] = (OpenSmMonitorService)oa[(oa.length -size)+n];
    return ffa;
  }

  public OpenSmMonitorService getOldestOMS()
  {
    if(size() > 1)
      return getRecentOMSs(MaxSize)[0];  // get all of them, and return the first one
      
    return getNewestOMS();
  }

  protected OpenSmMonitorService getNewestOMS()
  {
    OpenSmMonitorService [] oa = getRecentOMSs(1);
    if((oa != null) && (oa.length > 0))
      return oa[0];
    return null;
  }

  public OpenSmMonitorService getPreviousOMS()
  {
    // one index off newest, or newest if only one
    OpenSmMonitorService [] oa = getRecentOMSs(2);
    if((oa != null) && (oa.length > 0))
    {
      if(oa.length > 1)
        return oa[1];  // this is the most recent
      else
        return oa[0];  // this is the next most recent
    }
    return null;
  }

  public OpenSmMonitorService getCurrentOMS()
  {
    return getNewestOMS();
  }
  
  public boolean isEmpty()
  {
    return omsHistory.isEmpty();
  }

  public int size()
  {
    return omsHistory.size();
  }

  /************************************************************
   * Method Name:
   *  OMS_List
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param maxSize
   ***********************************************************/
  public OMS_List(int maxSize)
  {
    super();
    MaxSize = maxSize;
  }
  
  public OMS_List()
  {
    this(2);
  }
  
  public OMS_List(OMS_Collection history)
  {
    this(history, false);
  }
  
  public OMS_List(OMS_Collection history, boolean oldest)
  {
    super();
    if((history != null) && (history.getSize() > 1))
    {
      // use the two most current, or the two oldest
      if(oldest)
      {
        this.putCurrentOMS(history.getOMS(0));
        this.putCurrentOMS(history.getOMS(1));                
      }
      else
      {
        int enDex = history.getSize();
        this.putCurrentOMS(history.getOMS(enDex-2));
        this.putCurrentOMS(history.getOMS(enDex-1));        
      }
        
    }
  }
  
  
  /************************************************************
   * Method Name:
   *  toInfo
  **/
  /**
   * Describe the method here
   *
   * @see java.lang.Object#toString()
   *
   * @return
   ***********************************************************/
  
  public String toInfo()
  {
    StringBuffer stringValue = new StringBuffer();
    stringValue.append(OMS_List.class.getSimpleName() + "\n");
    OpenSmMonitorService oms = getCurrentOMS();
    OpenSmMonitorService oms2 = getOldestOMS();
    OSM_Fabric Fabric1 = oms.getFabric();
    OSM_Fabric Fabric2 = oms2.getFabric();
    
    OSM_FabricDelta fd = new OSM_FabricDelta(Fabric2, Fabric1);
    stringValue.append("collection size:             " + size() + "\n");
    stringValue.append(fd.toInfo());
    return stringValue.toString();
  }


}
