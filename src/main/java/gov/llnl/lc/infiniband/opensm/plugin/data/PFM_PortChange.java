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
 *        file: PFM_PortChange.java
 *
 *  Created on: Jul 26, 2012
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import gov.llnl.lc.infiniband.core.IB_Address;
import gov.llnl.lc.infiniband.core.IB_Guid;
import gov.llnl.lc.infiniband.opensm.plugin.data.PFM_Port.PortCounterName;
import gov.llnl.lc.time.TimeStamp;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**********************************************************************
 * A PFM_PortChange objects represents the change that occurs on a port
 * during a specific time interval.  Given two instances or snapshots
 * of the same port, the time interval between the two snapshots is calculated
 * as well as computing the differences in the counters.
 * 
 * <p>
 * This object can be used to help calculate rates and bandwidths.
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Jul 26, 2012 4:42:42 PM
 **********************************************************************/
public class PFM_PortChange implements Serializable, Comparable<PFM_PortChange>
{
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = -8011062053405054177L;

  /** a recent instance of a PFM_Port **/
  private PFM_Port port1;
  
  /** an older instance of the same PFM_Port **/
  private PFM_Port port2;
  
  /** the count differences between the two instances of ports **/
  private long [] delta_port_counters;
  
   /**  the time difference between the two sets of port counters **/
  private long delta_counter_ts;
  
  /**  the time difference between the two sets of port counters **/
  private long delta_error_ts;
  
  /** indicates non-zero values in the delta_port_counters **/
  private boolean TrafficChange = false;
  private boolean ErrorChange   = false;
  
  /************************************************************
   * Method Name:
   *  getAddress
   **/
  /**
   * Since a PortChange object is the difference between two instances
   * of the same port at two different times, the address of both
   * ports should match.  This convenience method returns the address
   * of the port.
   *
   * @return the address
   *
   ***********************************************************/
  
  public IB_Address getAddress()
  {
    if((port1 != null) || (port2 == null))
    {
      if(port1 != null)
        return new IB_Address(port1.getNodeGuid());
      return new IB_Address(port2.getNodeGuid());
    }
    return null;
  }

  /************************************************************
   * Method Name:
   *  getPortNumber
   **/
  /**
   * Since a PortChange object is the difference between two instances
   * of the same port at two different times, the port number of both
   * ports should match.  This convenience method returns the number
   * of the port.
   *
   * @return the portNumber
   *
   ***********************************************************/
  
  public int getPortNumber()
  {
    if((port1 != null) || (port2 == null))
    {
      if(port1 != null)
        return (int)port1.port_num;
      return (int)port2.port_num;
     }
    return 0;
  }

  /************************************************************
   * Method Name:
   *  PFM_PortChange
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param   describe the parameters if any
   *
   * @param port1
   * @param port2
   ***********************************************************/
  public PFM_PortChange(PFM_Port port1, PFM_Port port2)
  {
    super();
    this.port1 = port1;
    this.port2 = port2;
    this.computeChange();
  }
  
  
  public String getOSM_PortKey()
  {
    return getOSM_PortKey(this);
  }

  public static String getOSM_PortKey(PFM_PortChange pc)
  {
    if((pc == null) || (pc.getAddress() == null))
      return null;
    
    return OSM_Port.getOSM_PortKey(pc.getAddress().getGuid().getGuid(), (short)pc.getPortNumber());
  }

  /************************************************************
   * Method Name:
   *  getPortChanges
  **/
  /**
   * Given a map of PFM_PortChange, this convenience
   * method will discover and return the ports associated with this
   * node guid.
   *
   * @see     describe related java objects  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   ***********************************************************/
  public static LinkedHashMap<String, PFM_PortChange> getPortChanges(HashMap <String, PFM_PortChange> pMap, IB_Guid guid, int numPorts)
  {
    if((pMap == null) || (guid == null))
      return null;
    
    LinkedHashMap<String, PFM_PortChange> pL = new LinkedHashMap<String, PFM_PortChange>();
    for(int pn = 0; pn < numPorts; pn++)
    {
      String key = PFM_PortChange.getPFM_PortChangeKey(guid, pn);
      PFM_PortChange p = pMap.get(key);
      if(p != null)
        pL.put(key, p);
    }
    return pL;
  }
  
  public static String getPFM_PortChangeKey(IB_Guid guid, int portNum)
  {
    if(guid == null)
      return null;
    return OSM_Port.getOSM_PortKey(guid.getGuid(), (short)portNum);
  }
  


  /************************************************************
   * Method Name:
   *  getPort1
   **/
  /**
   * Returns the value of port1
   *
   * @return the port1
   *
   ***********************************************************/
  
  public PFM_Port getPort1()
  {
    return port1;
  }

  /************************************************************
   * Method Name:
   *  getPort2
   **/
  /**
   * Returns the value of port2
   *
   * @return the port2
   *
   ***********************************************************/
  
  public PFM_Port getPort2()
  {
    return port2;
  }

  /************************************************************
   * Method Name:
   *  hasChange
   **/
  /**
   * Returns true if any (Error or Traffic) of the counters have
   * changed.
   *
   * @return the change
   *
   ***********************************************************/
  
  public boolean hasChange()
  {
    return hasErrorChange() || hasTrafficChange();
  }

  /************************************************************
   * Method Name:
   *  hasErrorChange
   **/
  /**
   * Returns true if any of the Error change counters are non-zero
   *
   * @return the change
   *
   ***********************************************************/
  
  public boolean hasErrorChange()
  {
    return ErrorChange;
  }

  /************************************************************
   * Method Name:
   *  hasTrafficChange
   **/
  /**
   * Returns true if any of the Traffic change counters are non-zero.
   *
   * @return the change
   *
   ***********************************************************/
  
  public boolean hasTrafficChange()
  {
    return TrafficChange;
  }

  /************************************************************
   * Method Name:
   *  getDelta_port_counters
   **/
  /**
   * Returns the value of delta_port_counters
   *
   * @return the delta_port_counters
   *
   ***********************************************************/
  
  public long[] getDelta_port_counters()
  {
    return delta_port_counters;
  }

  /************************************************************
   * Method Name:
   *  getDelta_port_counter
  **/
  /**
   * Returns the value of change for the named counter.
   *
   * @see     describe related java objects
  
   * @param   describe the parameters
   *
   * @param name
   * @return
   ***********************************************************/
  public long getDelta_port_counter(PFM_Port.PortCounterName name)
  {
    if(delta_port_counters.length < name.ordinal())
      return 0L;
    return delta_port_counters[name.ordinal()];
  }

  /************************************************************
   * Method Name:
   *  getChange_ts
   **/
  /**
   * Returns the timestamp value contained in Port1's counter timestamp.
   * This value represents the timestamp on the PFM_PortChange object.
   *
   * @return the timestamp for the change
   *
   ***********************************************************/
  
  public long getChange_ts()
  {
    return port1 != null ? port1.counter_ts: 0L;
  }

  /************************************************************
   * Method Name:
   *  getCounterTimeStamp
   */
  /**
   * Returns the value of counter_ts as a TimeStamp
   *
   * @return the counter_ts
   ***********************************************************/
  
  public TimeStamp getCounterTimeStamp()
  {
    // this is always from port1, it should be the most recent
    return getPort1().getCounterTimeStamp();
  }
  
  /************************************************************
   * Method Name:
   *  getDelta_counter_ts
   **/
  /**
   * Returns the value of delta_counter_ts
   *
   * @return the delta_counter_ts
   *
   ***********************************************************/
  
  public long getDelta_counter_ts()
  {
    return delta_counter_ts;
  }

  /************************************************************
   * Method Name:
   *  getDelta_error_ts
   **/
  /**
   * Returns the value of delta_error_ts
   *
   * @return the delta_error_ts
   *
   ***********************************************************/
  
  public long getDelta_error_ts()
  {
    return delta_error_ts;
  }

  private boolean computeChange()
  {
    delta_port_counters = new long[PortCounterName.values().length];

    
    if((port1 == null) || (port2 == null))
      return false;
    
    // they must be the same ports
    if(port1.compareTo(port2) != 0)
      return false;
    
    // do I have a snowballs chance of computing the difference?
    if((port1.port_counters != null) && (port1.port_counters.length > 0) && (port2.port_counters != null) && (port1.port_counters.length == port2.port_counters.length) )
    {
      // TRUST the timestamp
      //
      // the time counter only counts up, so ALWAYS make port1 the most recent (largest ts) port1, and subtract port2
      // swap if necessary
      BigInteger d_ts = PFM_Port.convertUnsignedLongLongToBigInteger(port1.counter_ts).subtract(PFM_Port.convertUnsignedLongLongToBigInteger(port2.counter_ts));
            
      if(d_ts.compareTo(BigInteger.ZERO) < 0)
      {
        //swap
        PFM_Port tmp = port1;
        port1 = port2;
        port2 = tmp;
        d_ts = d_ts.abs();
      }
      /* the time stamps are different, and port1 is more recent than port2, which means that I should not get a negative
       * result if I subtract port2 from port1
       */
      // I should be able to use normal math now, no need to use BigIntegers...
      
      delta_counter_ts = PFM_Port.convertBigIntegerToUnsignedLongLong(d_ts);
      delta_error_ts   = port1.error_ts - port2.error_ts;
      
      int n = 0;
      /* iterate through the enum, and find the differences */
      for (PortCounterName counter : PortCounterName.values())
      {
        n = counter.ordinal();
        delta_port_counters[n] = port1.port_counters[n] - port2.port_counters[n];
        
        // set the change flags
        if((delta_port_counters[n] != 0L) && ((ErrorChange == false) || (TrafficChange == false)))
        {
          if(PortCounterName.PFM_ERROR_COUNTERS.contains(counter))
            ErrorChange = true;
          if(PortCounterName.PFM_DATA_COUNTERS.contains(counter))
            TrafficChange = true;
        }
      }
    }
    return true;
  }

  /************************************************************
   * Method Name:
   *  compareTo
  **/
  /**
   * FIXME:  want to be able to sort changes (on different ports)
   *         by the biggest values in the delta
   *
   * @see java.lang.Comparable#compareTo(java.lang.Object)
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @param o
   * @return
   ***********************************************************/
  
  @Override
  public int compareTo(PFM_PortChange portC)
  {
    // compare the perfmgr ports (guid & port_num)
    if(portC == null)
      throw new NullPointerException();
    
    // compare ports first
    
    if(!this.port1.equals(portC.port1))
      return -1;
    
    if(!this.port2.equals(portC.port2))
      return 1;
    
    // all ports are the same
      return 0;
  }

  /************************************************************
   * Method Name:
   *  equals
  **/
  /**
   * Two Changes are the same if their ports match
   *
   * @see java.lang.Object#equals(java.lang.Object)
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @param obj
   * @return
   ***********************************************************/
  
  @Override
  public boolean equals(Object obj)
  {
    return ((obj != null) && (obj instanceof PFM_PortChange) && (this.compareTo((PFM_PortChange)obj)==0));
  }

  /************************************************************
   * Method Name:
   *  toErrorCounterString
  **/
  /**
   * Describe the method here
   *
   * @see java.lang.Object#toString()
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @return
   ***********************************************************/
  
  public String toCounterString(EnumSet<PortCounterName> nameSet)
  {
    StringBuffer buff = new StringBuffer();
    boolean initial = true;
  
    for (PortCounterName counter : nameSet)
    {
      long val = getDelta_port_counter(counter);
      if(val != 0L)
      {
        // add this to the error string
        if(!initial)
          buff.append(", ");
        else
          initial = false;
        buff.append(counter.name() + "=" + val);
      }
    }
    return buff.toString();
  }

  /************************************************************
   * Method Name:
   *  toTrafficString
  **/
  /**
   * Describe the method here
   *
   * @see java.lang.Object#toString()
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @return
   ***********************************************************/
  
  public String toTrafficString()
  {
    return "delta_traffic_counters=["
        + toCounterString(PortCounterName.PFM_DATA_COUNTERS) + "]";
  }

  /************************************************************
   * Method Name:
   *  toErrorString
  **/
  /**
   * Describe the method here
   *
   * @see java.lang.Object#toString()
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @return
   ***********************************************************/
  
  public String toShortErrorString()
  {
    return toCounterString(PortCounterName.PFM_ERROR_COUNTERS);
  }

  /************************************************************
   * Method Name:
   *  toErrorString
  **/
  /**
   * Describe the method here
   *
   * @see java.lang.Object#toString()
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @return
   ***********************************************************/
  
  public String toErrorString()
  {
    return "delta_err_counters=[" + toShortErrorString() + "]";
  }

  /************************************************************
   * Method Name:
   *  toErrorString
  **/
  /**
   * Describe the method here
   *
   * @see java.lang.Object#toString()
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @return
   ***********************************************************/
  
  public String toVerboseErrorString()
  {
    return "PFM_PortChange\n port=" + port1 + "\n delta_err_counters="
        + toCounterString(PortCounterName.PFM_ERROR_COUNTERS) + "]";
  }

  /************************************************************
   * Method Name:
   *  toString
  **/
  /**
   * Describe the method here
   *
   * @see java.lang.Object#toString()
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @return
   ***********************************************************/
  
  @Override
  public String toString()
  {
    return "PFM_PortChange\n port1=" + port1 + "\n port2=" + port2 + "\n delta_port_counters="
        + Arrays.toString(delta_port_counters) + "\n delta_counter_ts=" + delta_counter_ts
        + ", delta_error_ts=" + delta_error_ts + "]";
  }

}
