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
 *        file: OSM_FabricDelta.java
 *
 *  Created on: Mar 8, 2013
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import gov.llnl.lc.infiniband.opensm.plugin.event.OSM_EventStats;
import gov.llnl.lc.infiniband.opensm.plugin.event.OsmEvent;
import gov.llnl.lc.time.TimeStamp;
import gov.llnl.lc.util.BinList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**********************************************************************
 * The OSM_FabricDelta Object provides convenience methods for comparing
 * two OSM_Fabric Objects.  The comparison may be for the purposes of
 * analyzing changes over time, such as traffic or errors on a port, or
 * can be used to detect the difference between an "Ideal" fabric, and
 * the "current" fabric.
 * <p>
 * @see  PFM_PortChange
 *
 * @author meier3
 * 
 * @version Mar 8, 2013 9:47:06 AM
 **********************************************************************/
public class OSM_FabricDelta implements Serializable, gov.llnl.lc.logging.CommonLogger
{
  
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = -1195076280059046589L;

  /** the original, first, or "oldest" fabric in this object **/
  private OSM_Fabric fabric1;
  
  /** the current, next, or "newest" fabric in this object **/
  private OSM_Fabric fabric2;
  
  private OSM_EventStats  eventDiff;

  private OSM_Stats  statsDiff;

  /* keyed off port guid+port_num */
  private LinkedHashMap<String, PFM_PortChange> portsWithChange  = new LinkedHashMap<String, PFM_PortChange>();
  private LinkedHashMap<String, PFM_PortChange> portsWithoutChange  = new LinkedHashMap<String, PFM_PortChange>();
  
  private PFM_PortChangeRange portRanges = null;
  
  /************************************************************
   * Method Name:
   *  getFabric1
   **/
  /**
   * Returns the value of fabric1
   *
   * @return the fabric1
   *
   ***********************************************************/
  
  public OSM_Fabric getFabric1()
  {
    return fabric1;
  }

  /************************************************************
   * Method Name:
   *  getFabric2
   **/
  /**
   * Returns the value of fabric2
   *
   * @return the fabric2
   *
   ***********************************************************/
  
  public OSM_Fabric getFabric2()
  {
    return fabric2;
  }
  
  public static OSM_FabricDelta getOSM_FabricDelta(String hostName, String portNumber, OSM_Fabric oldFabric)
  {
    // create a FabricDelta, using the supplied fabric as the original instance
    // (fabric1), and obtaining a new one (fabric2) for comparison.
    //
    // if the supplied fabric is null, just go get both instances separated in time
    // by the perf managers sweep period.
    
    OSM_Fabric Fabric  = OSM_Fabric.getOSM_Fabric(hostName, portNumber);
    OSM_Fabric Fabric1 = oldFabric;
    OSM_Fabric Fabric2 = Fabric;
    
    if(Fabric1 == null)
    {
      Fabric1 = Fabric;
      // I need to get another instance, but must wait for new data
      int sleepTime = Fabric.getPerfMgrSweepSecs();
      
      // wait for the perfmanager to perform another sweep, and produce new data
      //  (add a little extra time to make sure)
      try
      {
        TimeUnit.SECONDS.sleep(sleepTime + 5);
      }
      catch (InterruptedException e)
      {
        logger.severe("Simple sleep failed");
      }
      
      // get the second fabric
      Fabric2 = OSM_Fabric.getOSM_Fabric(hostName, portNumber);
    }
    return new OSM_FabricDelta(Fabric1, Fabric2);
  }
  
  public static int getDeltaSeconds(OSM_Fabric oldFabric, OSM_Fabric newFabric)
  {
    // create a FabricDelta, using the supplied fabrics, and then return
    // the ACTUAL time difference in seconds
    
    if((oldFabric == null) || (newFabric == null))
      return -1;
    
    return (new OSM_FabricDelta(oldFabric, newFabric)).getDeltaSeconds();
  }
  
  
  public static OSM_FabricDelta getOSM_FabricDelta(String hostName, String portNumber)
  {
    // create a FabricDelta starting from scratch
    return OSM_FabricDelta.getOSM_FabricDelta(hostName, portNumber, null);
  }

  
  private PFM_PortChange calculatePortChange()
  {
    // loop through all the ports, and calculate the differences
    LinkedHashMap<String, OSM_Port> portMap1 = getFabric1().getOSM_Ports();
    LinkedHashMap<String, OSM_Port> portMap2 = getFabric2().getOSM_Ports();
    OSM_Port oport  = null;
    PFM_PortChange pChange = null;
    
    for(Map.Entry<String, OSM_Port> portMapEntry: portMap1.entrySet())
    {
      // find a matching port in the other fabric so we can do a difference
      oport = portMap2.get(portMapEntry.getKey());
      if(oport != null)
      {
        // found a match, so calculate the difference and add it to the change map
        pChange = new PFM_PortChange(portMapEntry.getValue().getPfmPort(), oport.getPfmPort());
        if(pChange.hasChange())
          portsWithChange.put(portMapEntry.getKey(), pChange);
        else
          portsWithoutChange.put(portMapEntry.getKey(), pChange);
      }
      else
        logger.severe("Could not find matching ports in both Fabrics [" + OSM_Fabric.getOSM_FabricKey(getFabric1()) + ": " + portMapEntry.getKey() + "], and [" + OSM_Fabric.getOSM_FabricKey(getFabric2()) + "]");
    }
    portRanges = new PFM_PortChangeRange(getPortsWithChange());
    return null;
  }

  private OSM_Stats calculateStatsChange()
  {
    // find the difference
    OSM_Stats s1 = getFabric1().getOsmStats();
    OSM_Stats s2 = getFabric2().getOsmStats();
    
    if((s1 == null) || (s2 == null))
      return null;
    
    // counters only increment, so subtract 1 from 2
    OSM_Stats diff = new OSM_Stats(s2.qp0_mads_outstanding         - s1.qp0_mads_outstanding,
        s2.qp0_mads_outstanding_on_wire - s1.qp0_mads_outstanding_on_wire,
        s2.qp0_mads_rcvd                - s1.qp0_mads_rcvd,
        s2.qp0_mads_sent                - s1.qp0_mads_sent,
        s2.qp0_unicasts_sent            - s1.qp0_unicasts_sent,
        s2.qp0_mads_rcvd_unknown        - s1.qp0_mads_rcvd_unknown,
        s2.sa_mads_outstanding          - s1.sa_mads_outstanding,
        s2.sa_mads_rcvd                 - s1.sa_mads_rcvd,
        s2.sa_mads_sent                 - s1.sa_mads_sent,
        s2.sa_mads_rcvd_unknown         - s1.sa_mads_rcvd_unknown,
        s2.sa_mads_ignored              - s1.sa_mads_ignored);
    
    return diff;
  }

  private OSM_EventStats calculateEventChange()
  {
    // find the difference
    OSM_EventStats e1 = getFabric1().getOsmEventStats();
    OSM_EventStats e2 = getFabric2().getOsmEventStats();
    
    if((e1 == null) || (e2 == null))
      return null;
    
    // counters only increment, so subtract 1 from 2
    long[] event_counters = new long[OsmEvent.OSM_STAT_EVENTS.size()];
    int ndex = 0;
    
    for(OsmEvent s : OsmEvent.OSM_STAT_EVENTS)
    {
      event_counters[ndex] = e2.getCounter(s) - e1.getCounter(s);
//      System.err.println(s.getEventName() + ": " + e2.getCounter(s) + ", and " + e1.getCounter(s) + ", with a diff of " + event_counters[ndex]);
      ndex++;
    }
    OSM_EventStats diff = new OSM_EventStats(event_counters);
    
    return diff;
  }

  /************************************************************
   * Method Name:
   *  isComparable
   **/
  /**
   * The two fabrics can be compared if they both exist, and have
   * the same name.  Many of the internal "delta" functions performs
   * this check before proceeding.  This compares the two "member" fabrics
   * of this object, to see if a "delta" object even makes sense.  It
   * does not compare two deltas.
   *
   * @return the comparable
   *
   ***********************************************************/
  
  public boolean isComparable()
  {
    if((fabric1 != null) && (fabric1.getFabricName() != null) && (fabric2 != null))
      return fabric1.getFabricName().equals(fabric2.getFabricName());
    return false;
  }

  /************************************************************
   * Method Name:
   *  getFabricName
   **/
  /**
   * Returns the value of fabricName
   *
   * @return the fabricName
   *
   ***********************************************************/
  
  public String getFabricName()
  {
    if(fabric1 != null)
      return fabric1.getFabricName();
    return "UNKNOWN";
  }
  
  public TimeStamp getTimeStamp()
  {
    // there are two fabrics, return the most current one (2)
    if(fabric2 != null)
      return fabric2.getTimeStamp();
    return null;
  }
  
  public int getDeltaSeconds()
  {
    return (int)getAgeDifference(TimeUnit.SECONDS);
  }
  
public PFM_PortChangeRange getRangeOfChanges()
{
  // always make sure its up to date
  return portRanges;
}

/************************************************************
 * Method Name:
 *  getPortChanges
 **/
/**
 * Returns the value of portsWithChange
 *
 * @return the portsWithChange
 *
 ***********************************************************/

public LinkedHashMap<String, PFM_PortChange> getPortChanges()
{
  // the complete list of differences (with and without changes)
  LinkedHashMap<String, PFM_PortChange> changes  = new LinkedHashMap<String, PFM_PortChange>();
  changes.putAll(portsWithChange);
  changes.putAll(portsWithoutChange);
  return changes;
}

/************************************************************
 * Method Name:
 *  getPortChanges
 **/
/**
 * Returns the value of portsWithChange
 *
 * @return the portsWithChange
 *
 ***********************************************************/

public PFM_PortChange getPortChange( OSM_Port port)
{
  // return just the change for this named port
  return getPortChanges().get(OSM_Port.getOSM_PortKey(port));
}

  /************************************************************
   * Method Name:
   *  getPortsWithChange
   **/
  /**
   * Returns the value of portsWithChange
   *
   * @return the portsWithChange
   *
   ***********************************************************/
  
  public LinkedHashMap<String, PFM_PortChange> getPortsWithChange()
  {
    return portsWithChange;
  }

  /************************************************************
   * Method Name:
   *  getPortChangesFromNode
   **/
  /**
   * Builds a new HashMap consisting of PFM_PortChange objects that
   * represent ports associated with a specific node, or switch.
   *
   * @return a map of all the ports from a switch
   *
   ***********************************************************/
  
  public LinkedHashMap<String, PFM_PortChange> getPortChangesFromNode(OSM_Node SwitchNode)
  {
    LinkedHashMap<String, PFM_PortChange> portChanges = getPortChanges();
    if((portChanges != null) && (SwitchNode != null))
      return PFM_PortChange.getPortChanges(portChanges, SwitchNode.getNodeGuid(), (int)(SwitchNode.sbnNode.num_ports));
      
     return null;
  }

  /************************************************************
   * Method Name:
   *  getPortsWithErrorChange
   **/
  /**
   * Builds a new HashMap consisting of PFM_PortChange objects that
   * represent ports who's error counters changed during the sample
   * (or sweep) period.
   *
   * @return a map of all the ports with active errors
   *
   ***********************************************************/
  
  public static LinkedHashMap<String, PFM_PortChange> getPortsWithErrorChange(HashMap<String, PFM_PortChange> changedPorts)
  {
    LinkedHashMap<String, PFM_PortChange> portsWithErrorChange  = new LinkedHashMap<String, PFM_PortChange>();
    if(changedPorts != null)
      for(Map.Entry<String, PFM_PortChange> pc: changedPorts.entrySet())
        if(pc.getValue().hasErrorChange())
          portsWithErrorChange.put(pc.getKey(), pc.getValue());
    return portsWithErrorChange;
  }

  public LinkedHashMap<String, PFM_PortChange> getPortsWithErrorChange()
  {
    return getPortsWithErrorChange(this.getPortsWithChange());
  }

  public void showErrorChanges()
  {
    LinkedHashMap<String, PFM_PortChange> pe = getPortsWithErrorChange();
    for(PFM_PortChange pec: pe.values())
    {
      System.err.println(pec.toErrorString());
    }
  }
  
  public void showRangeOfChanges()
  {
    System.err.println("Range of all changes:");
    System.err.println(getRangeOfChanges().toRangeOfChangeString());
  }

  /************************************************************
   * Method Name:
   *  getPortsWithTrafficChange
   **/
  /**
   * Builds a new HashMap consisting of PFM_PortChange objects that
   * represent ports who's traffic counters changed during the sample
   * (or sweep) period.
   *
   * @return a map of all the ports with active traffic
   *
   ***********************************************************/
  
  public static LinkedHashMap<String, PFM_PortChange> getPortsWithTrafficChange(HashMap<String, PFM_PortChange> changedPorts)
  {
    LinkedHashMap<String, PFM_PortChange> portsWithTrafficChange  = new LinkedHashMap<String, PFM_PortChange>();
    if(changedPorts != null)
      for(Map.Entry<String, PFM_PortChange> pc: changedPorts.entrySet())
        if(pc.getValue().hasTrafficChange())
          portsWithTrafficChange.put(pc.getKey(), pc.getValue());
    return portsWithTrafficChange;
  }
  
  public LinkedHashMap<String, PFM_PortChange> getPortsWithTrafficChange()
  {
    return getPortsWithTrafficChange(this.getPortsWithChange());
  }

  public static BinList <String> getPortCounterChangeBins( PFM_Port.PortCounterName name, HashMap<String, PFM_PortChange> changedPorts, int numBins, double scaleFactor)
  {
    if((changedPorts == null) || (changedPorts.size() < 2) || (name == null) || (numBins < 2) || (scaleFactor < 1.0))
      return null;
    
    PFM_PortChangeRange ranges = new PFM_PortChangeRange(changedPorts);
    long max = ranges.getMaxPortCounterValue(name);
    long top = (long)(((double) max) * scaleFactor);
    return getPortCounterChangeBins( name, changedPorts, numBins, top);
  }
  
  public static BinList <String> getPortCounterChangeBins( PFM_Port.PortCounterName name, HashMap<String, PFM_PortChange> changedPorts, int numBins, long maxCount)
  {
    /* create bins based on the min/max values of the "named" port counter
     * and then put the changed port in the bins
     * 
     * the key of each bin is the upper limit value for the bin - each element in the
     * bin should be less than that value
     * 
     * each bin is initially populated with a placeholder, which helps build the BinList, and
     * define the boundries of each bin.  This means that a bin with only one element is
     * actually empty, because it only contains a placeholder.
     * 
     * the individual bins contain the keys to the PFM_PortChange object.  Typically you would
     * retrieve the key from the bin, to then reference the actual PFM_PortChange object from
     * the HashMap that was provided as an argument.
     * 
     */
    
    if((changedPorts == null) || (changedPorts.size() < 2) || (name == null) || (numBins < 2) || (maxCount < 10))
      return null;
    
    BinList <String> changedPortKeyBins = new BinList <String>();
    long div = maxCount/(numBins-1);
    
//    System.err.println("Counter: " + name + ", numBins: " + numBins );
//    System.err.println("    top: " + maxCount + ", div: " + div);
//    
    long binLimit = 0L;
    for(int n = 0; n < numBins; n++, binLimit+=div)
    {
      if(n >= (numBins -1))
        binLimit = maxCount;
      changedPortKeyBins.add("place holder", Long.toString(binLimit));
    }
//    System.err.println("The bin sizes: " + changedPortKeyBins.getBinSizes());
//    System.err.println("The bin contents: " + changedPortKeyBins.toString());
    long numAdded = 0;
    
    // now loop through the ports, and place them in the proper bins
    for(Map.Entry<String, PFM_PortChange> pc: changedPorts.entrySet())
    {
      // start at the lowest bin, work my way up, stop as soon as I find a bin
      binLimit = 0L;
      for(int b = 0; b < numBins; b++, binLimit+=div)
      {
        if(b >= (numBins -1))
          binLimit = maxCount;
        if(pc.getValue().getDelta_port_counter(name) <= binLimit)
        {
          // it goes in this bin (stop here, no more bin checking)
          changedPortKeyBins.add(pc.getKey(), Long.toString(binLimit));
          numAdded++;
          break;
        }
        else
        {
          // if the counter is greater than the overall maxCount
          // it gets ignored - won't be included in ANY of the bins
          // so make sure you pick a good maxCount
        }
      }
    }
    System.err.println("The bin contents: " + changedPortKeyBins.toString());
    
    return changedPortKeyBins;
  }
  
  
  /************************************************************
   * Method Name:
   *  getPortsWithoutChange
   **/
  /**
   * Returns the value of portsWithoutChange
   *
   * @return the portsWithoutChange
   *
   ***********************************************************/
  
  public LinkedHashMap<String, PFM_PortChange> getPortsWithoutChange()
  {
    return portsWithoutChange;
  }

  /************************************************************
   * Method Name:
   *  getAgeDifference
   **/
  /**
   * Returns the time delta.  This Object represents the difference
   * between two snapshots of the same fabric at two different times.
   * This method returns the time difference.
   * 
   * Often times, this difference represents the sweep period of the performance
   * manager, since that is usually the determining factor for when new
   * data is available. 
   *
   * @return the ageDifference
   *
   ***********************************************************/
  
  public long getAgeDifference(TimeUnit unitOfTime)
  {
    // use seconds to calculate difference, but return desired units
    
    // by convention, fabric1's age should be greater than fabric2, so
    // this should return a positive number (but always return positive anyway)
    if(isComparable())
      return unitOfTime.convert(fabric1.getAge(TimeUnit.SECONDS) - fabric2.getAge(TimeUnit.SECONDS), TimeUnit.SECONDS); 
    return 0L;
  }

  /************************************************************
   * Method Name:
   *  OSM_FabricDelta
  **/
  /**
   * Construct the delta from two fabrics.  The first fabric should
   * be older (smaller time since epoch) than the second. Both
   * fabrics must have the same name.
   *
   * @see     describe related java objects
   *
   * @param fabric1
   * @param fabric2
   ***********************************************************/
  public OSM_FabricDelta(OSM_Fabric fabric1, OSM_Fabric fabric2)
  {
    super();
    this.fabric1 = fabric1;
    this.fabric2 = fabric2;
    if(isComparable())
    {
      calculatePortChange();
      statsDiff = calculateStatsChange();
      eventDiff = calculateEventChange();
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
    stringValue.append(OSM_FabricDelta.class.getSimpleName() + "\n");
    
    stringValue.append("fabric name:             " + this.getFabricName() + "\n");
    stringValue.append("first timestamp:         " + this.getFabric1().toTimeString() + "\n");
    stringValue.append("last timestamp:          " + this.getFabric2().toTimeString() + "\n");
    stringValue.append("# secs between records:  " + this.getAgeDifference(TimeUnit.SECONDS) + "\n");
    stringValue.append("# nodes:                 " + this.getFabric1().getOSM_Nodes().size() + "\n");
    stringValue.append("# ports:                 " + this.getFabric1().getOSM_Ports().size() + "\n");
    stringValue.append("# links:                 " + this.getFabric1().getIB_Links().size());
  
    return stringValue.toString();
  }

  
  /************************************************************
   * Method Name:
   *  toTimeString
  **/
  /**
   * Returns a list of TimeStamps for this object
   *
   * @see     describe related java objects
   *
   * @return
   ***********************************************************/
  public String toTimeString()
  {
    
    return getFabric1().toTimeString() + " -> " + getFabric2().toTimeString();
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
  
  public String toString()
  {
    return "OSM_FabricDelta [fabricName=" + getFabricName() + ", fabricAgeDifference=" + getAgeDifference(TimeUnit.SECONDS) + " seconds, # portsWithChange="
        + portsWithChange.size() + ", # portsWithoutChange=" + portsWithoutChange.size() + ", # portsWithTrafficChange=" + getPortsWithTrafficChange().size()
        + ", # portsWithErrorChange=" + getPortsWithErrorChange().size();
  }

  
  public static OSM_FabricDelta readFabricDelta(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException
  {
    FileInputStream fileInput = new FileInputStream(fileName);
    ObjectInputStream objectInputStream = new ObjectInputStream(fileInput);

    return (OSM_FabricDelta) objectInputStream.readObject();
  }
  
  public static void writeFabricDelta(String fileName, OSM_FabricDelta fabric) throws IOException
  {
    File fabricFile = new File(fileName);
    fabricFile.getParentFile().mkdirs();
    FileOutputStream fileOutput = new FileOutputStream(fabricFile);
    ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput);
    objectOutput.writeObject(fabric);
    return;
  }

  public OSM_Stats getStatChanges()
  {
    return statsDiff;
  }

  public OSM_EventStats getEventChanges()
  {
    return eventDiff;
  }

}
