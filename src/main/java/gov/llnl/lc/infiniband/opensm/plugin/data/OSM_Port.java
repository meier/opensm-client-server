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
 *        file: OSM_Port.java
 *
 *  Created on: Jan 11, 2012
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import gov.llnl.lc.infiniband.core.IB_Address;
import gov.llnl.lc.infiniband.core.IB_Gid;
import gov.llnl.lc.infiniband.core.IB_Guid;
import gov.llnl.lc.logging.CommonLogger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**********************************************************************
 * An OSM_Port contains all of the information known about a port from
 * the two native Peer Classes, PFM_Port and SBN_Port.  Typically the
 * Performance Manager maintains a complete list of ports, even inactive
 * ones.  The Subnet Manager may not have information on every port, so
 * this member can be null. 
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Jan 11, 2012 8:30:32 AM
 **********************************************************************/
public class OSM_Port implements Serializable, CommonLogger, Comparable<OSM_Port>
{
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = 8813669148455138115L;

  /**  the port managed by the perf manager **/
  public PFM_Port pfmPort;

  /**  the port managed by the subnet manager **/
  public SBN_Port sbnPort;    

  /** in the case of a port from a CA, its the ports address (guid), but for
   *  ports from SW, it is simply the switches guid **/
  private IB_Address Address;
  
  private int PortNumber;  // actual (internal) port number
  
  private OSM_NodeType NodeType;  // the type of the parent node
  
  /************************************************************
   * Method Name:
   *  OSM_Port
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param   describe the parameters if any
   *
   * @param pfmPort
   * @param sbnPort
   ***********************************************************/
  public OSM_Port(PFM_Port pfmPort, SBN_Port sbnPort, OSM_NodeType type)
  {
    super();
    
    // always prefer the subnet information over the perf manager, so
    // set the perfmgr first, then overwrite redundant info from sbnPort

    setPfmPort(pfmPort);
    setSbnPort(sbnPort);
    setNodeType(type);
  }

  /************************************************************
   * Method Name:
   *  getPfmPort
   **/
  /**
   * Returns the value of pfmPort
   *
   * @return the pfmPort
   *
   ***********************************************************/
  
  public synchronized PFM_Port getPfmPort()
  {
    return pfmPort;
  }

  /************************************************************
   * Method Name:
   *  setPfmPort
   **/
  /**
   * Sets the value of pfmPort
   *
   * @param pfmPort the pfmPort to set
   *
   ***********************************************************/
  public synchronized void setPfmPort(PFM_Port pfmPort)
  {
    this.pfmPort = pfmPort;
    
    // always prefer the subnet information over the perf manager
    // so only write (or overwrite) this if the sbnPort is null
    if((pfmPort != null) && (sbnPort == null))
    {
      Address = new IB_Address(pfmPort.getNodeGuid());
      PortNumber = (int)pfmPort.port_num;
    }
  }

  /************************************************************
   * Method Name:
   *  getSbnPort
   **/
  /**
   * Returns the value of sbnPort
   *
   * @return the sbnPort
   *
   ***********************************************************/
  
  public synchronized SBN_Port getSbnPort()
  {
    return sbnPort;
  }

  /************************************************************
   * Method Name:
   *  setSbnPort
   **/
  /**
   * Sets the value of sbnPort.  This over writes any redundant
   * attributes that may have been set by setPfmPort().
   *
   * @param sbnPort the sbnPort to set
   *
   ***********************************************************/
  public synchronized void setSbnPort(SBN_Port sbnPort)
  {
    this.sbnPort = sbnPort;
    
    if(sbnPort != null)
    {
      int lid = 0;
      // the Address contains the ports actual guid
      // which may or may not be the same as the node_guid
      // ** prefer node-guid for identification resolution (don't use address) **
      IB_Guid guid   = new IB_Guid(sbnPort.port_guid);
      IB_Gid gid     = null;
      if(sbnPort.port_info != null)
      {
        lid = sbnPort.port_info.base_lid;
//        IB_Guid prefix = new IB_Guid(sbnPort.port_info.subnet_prefix);
//        gid = new IB_Gid(prefix, guid);
       }
      Address = new IB_Address(guid, gid, lid);
      PortNumber = (int)sbnPort.port_num;
    }
  }

  /************************************************************
   * Method Name:
   *  getNodeGuid
  **/
  /**
   * Returns the parents node guid, which may or may not be the
   * same as the ports guid.
   *
   * @see     describe related java objects
   *
   * @return
   ***********************************************************/
  public IB_Guid getNodeGuid()
  {
    if(sbnPort != null)
      return new IB_Guid(sbnPort.node_guid);
    
    return pfmPort == null ? null: new IB_Guid(pfmPort.node_guid);
  }
  
  /************************************************************
   * Method Name:
   *  getPortGuid
  **/
  /**
   * Returns the this ports guid, which may or may not be the
   * same as the parents node guid.
   *
   * @see     describe related java objects
   *
   * @return
   ***********************************************************/
  public IB_Guid getPortGuid()
  {
    if(sbnPort != null)
      return new IB_Guid(sbnPort.port_guid);
    
    return pfmPort == null ? null: new IB_Guid(pfmPort.node_guid + pfmPort.port_num);
  }
  
  /************************************************************
   * Method Name:
   *  getAddress
   **/
  /**
   * Returns the value of address
   *
   * @return the address
   *
   ***********************************************************/
  
  public synchronized IB_Address getAddress()
  {
    return Address;
  }

  public String getOSM_PortKey()
  {
    return OSM_Port.getOSM_PortKey(this);      
  }
  
  public static String getOSM_PortKey(long guid, short port_num)
  {
    return (new IB_Guid(guid)).toColonString()+ ":"+ port_num;      
  }
  
  public static String getOSM_PortKey(OSM_Port p)
  {
    // use the subnet
    if((p == null) || (p.sbnPort == null))
      return null;
    
    return OSM_Port.getOSM_PortKey(p.sbnPort.node_guid, p.sbnPort.port_num);
  }
  /************************************************************
   * Method Name:
   *  getOSM_Ports
  **/
  /**
   * Given a list of all the OSM_Ports that exist, this convenience
   * method will discover and return the ports associated with this
   * node.
   *
   * @see     describe related java objects  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   ***********************************************************/
  public static ArrayList<OSM_Port> getOSM_Ports(ArrayList<OSM_Port> allPortsArray, IB_Guid guid)
  {
    if((allPortsArray == null) || (allPortsArray.size() < 1) || (guid == null))
      return null;
    
    ArrayList<OSM_Port> pL = new ArrayList<OSM_Port>();
    for(OSM_Port p: allPortsArray)
    {
      if((p.getNodeGuid() != null) && (p.getNodeGuid().equals(guid)))
        pL.add(p);
//      if((p.getAddress() != null) && (p.getAddress().getGuid() != null) && (p.getAddress().getGuid().equals(guid)))
//        pL.add(p);
    }
    
    return pL;
  }
  
  /************************************************************
   * Method Name:
   *  getOSM_Port
  **/
  /**
   * Given a list of all the OSM_Ports that exist, this convenience
   * method will discover and return the first port with this port guid.
   *
   * @see     describe related java objects  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   ***********************************************************/
  public static OSM_Port getOSM_Port(ArrayList<OSM_Port> allPortsArray, IB_Guid portGuid)
  {
    if((allPortsArray == null) || (allPortsArray.size() < 1) || (portGuid == null))
      return null;
    
    for(OSM_Port p: allPortsArray)
    {
      if((p.getPortGuid() != null) && (p.getPortGuid().equals(portGuid)))
        return p;
    }
    
    for(OSM_Port p: allPortsArray)
    {
      if((p.getNodeGuid() != null) && (p.getNodeGuid().equals(portGuid)))
        return p;
    }
    
    return null;
  }
  
  /************************************************************
   * Method Name:
   *  getOSM_Ports
  **/
  /**
   * Given a list of all the OSM_Ports that exist, this convenience
   * method will discover and return the ports associated with this
   * node.
   *
   * @see     describe related java objects  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   ***********************************************************/
  public static ArrayList<OSM_Port> getOSM_Ports(OSM_Ports allPorts, IB_Guid guid)
  {
    if((allPorts == null) || (guid == null))
      return null;
    
    return getOSM_Ports( allPorts.createOSM_Ports(), guid);
  }
  
  
  /************************************************************
   * Method Name:
   *  getOSM_Ports
  **/
  /**
   * Given a map of OSM_Ports, this convenience
   * method will discover and return the ports associated with this
   * node guid.
   *
   * @see     describe related java objects  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   ***********************************************************/
  public static ArrayList<OSM_Port> getOSM_Ports(HashMap <String, OSM_Port> pMap, IB_Guid guid, int numPorts)
  {
    if((pMap == null) || (guid == null))
      return null;
    
    ArrayList<OSM_Port> pL = new ArrayList<OSM_Port>();
    for(int pn = 0; pn < numPorts; pn++)
    {
      String key = OSM_Port.getOSM_PortKey(guid.getGuid(), (short)pn);
      OSM_Port p = pMap.get(key);
      if(p != null)
        pL.add(p);
    }
    return pL;
  }
  
  
  /************************************************************
   * Method Name:
   *  setAddress
   **/
  /**
   * Sets the value of address
   *
   * @param address the address to set
   *
   ***********************************************************/
  public synchronized void setAddress(IB_Address address)
  {
    Address = address;
  }

  /************************************************************
   * Method Name:
   *  getPortNumber
   **/
  /**
   * Returns the value of portNumber
   *
   * @return the portNumber
   *
   ***********************************************************/
  
  public synchronized int getPortNumber()
  {
    return PortNumber;
  }

  /************************************************************
   * Method Name:
   *  setPortNumber
   **/
  /**
   * Sets the value of portNumber
   *
   * @param portNumber the portNumber to set
   *
   ***********************************************************/
  public synchronized void setPortNumber(int portNumber)
  {
    PortNumber = portNumber;
  }

  /************************************************************
   * Method Name:
   *  getSpeedString
   **/
  /**
   * Returns the value of speed
   *
   * @return the speed
   *
   ***********************************************************/
  
  public OSM_LinkSpeed getSpeed()
  {
    return OSM_LinkSpeed.get(this);
  }

  /************************************************************
   * Method Name:
   *  getSpeedString
   **/
  /**
   * Returns the value of speed
   *
   * @return the speed
   *
   ***********************************************************/
  
  public String getSpeedString()
  {
 // return the link speed from the sbnPort portinfo
    return getSpeed().getSpeedName();
  }


  /************************************************************
   * Method Name:
   *  getWidth
   **/
  /**
   * Returns the value of width
   *
   * @return the width
   *
   ***********************************************************/
  
  public OSM_LinkWidth getWidth()
  {
    return OSM_LinkWidth.get(this);
  }

  /************************************************************
   * Method Name:
   *  getWidthString
   **/
  /**
   * Returns the value of width
   *
   * @return the width
   *
   ***********************************************************/
  
  public String getWidthString()
  {
    return getWidth().getWidthName();
  }

  /************************************************************
   * Method Name:
   *  getRateString
   **/
  /**
   * Returns the value of rate
   *
   * @return the rate
   *
   ***********************************************************/
  
  public OSM_LinkRate getRate()
  {
 // the rate is a combination of the link speed and width
    return OSM_LinkRate.get(this);
  }

  /************************************************************
   * Method Name:
   *  getRateString
   **/
  /**
   * Returns the value of rate
   *
   * @return the rate
   *
   ***********************************************************/
  
  public String getRateString()
  {
 // the rate is a combination of the link speed and width
    if(getRate() == null)
      return "Unknown Rate";
    return getRate().getRateName();
  }


  /************************************************************
   * Method Name:
   *  getState
   **/
  /**
   * Returns the value of state
   *
   * @return the state
   *
   ***********************************************************/
  
  public OSM_PortState getState()
  {
    return OSM_PortState.get(this);
  }

  /************************************************************
   * Method Name:
   *  getStateString
   **/
  /**
   * Returns the value of state
   *
   * @return the state
   *
   ***********************************************************/
  
  public String getStateString()
  {
    // usually "Active / LinkUp" or "Down / Polling"
    return getState().getStateName();
  }

  /************************************************************
   * Method Name:
   *  isActive
   **/
  /**
   * Returns the value of errors
   *
   * @return the errors
   *
   ***********************************************************/
  
  public boolean isActive()
  {
    // if the State begins with the word "Active"
    return (OSM_PortState.ACTIVE == getState());
  }

  /************************************************************
   * Method Name:
   *  hasErrors
   **/
  /**
   * Returns the value of errors
   *
   * @return the errors
   *
   ***********************************************************/
  
  public boolean hasError()
  {
    // the PFM_Port may know if this port has errors
    if(pfmPort != null)
      return pfmPort.hasError();
    return false;
  }


  /************************************************************
   * Method Name:
   *  hasErrors
   **/
  /**
   * Returns the value of errors
   *
   * @return the errors
   *
   ***********************************************************/
  
  public boolean hasPort(PFM_Port p)
  {
    // the PFM_Port may know if this port has errors
    if((pfmPort != null) && (p != null))
    {
      return pfmPort.equals(p);
    }
    return false;
  }


  /************************************************************
   * Method Name:
   *  hasTraffic
   **/
  /**
   * Returns the value of traffic
   *
   * @return the traffic
   *
   ***********************************************************/
  
  public boolean hasTraffic()
  {
    // the PFM_Port may know if this port has traffic
    if(pfmPort != null)
      return pfmPort.hasTraffic();
    return false;
  }

  /************************************************************
   * Method Name:
   *  getNodeType
   **/
  /**
   * Returns the value of nodeType
   *
   * @return the nodeType
   *
   ***********************************************************/
  
  public synchronized OSM_NodeType getNodeType()
  {
    return NodeType;
  }

  /************************************************************
   * Method Name:
   *  setNodeType
   **/
  /**
   * Sets the value of nodeType
   *
   * @param nodeType the nodeType to set
   *
   ***********************************************************/
  public synchronized void setNodeType(OSM_NodeType nodeType)
  {
    NodeType = nodeType;
  }

  /************************************************************
   * Method Name:
   *  compareTo
  **/
  /**
   * OSM_Ports are considered to be the same, if their guids
   * and port numbers match.
   *
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   * @param   describe the parameters
   *
   * @return  describe the value returned
   ***********************************************************/
  
  @Override
  public int compareTo(OSM_Port port)
  {    
    // the Address is the only thing that MUST be unique
        //
    // both object must exist (and of the same class)
    // and should be consistent with equals
    //
    // -1 if less than
    // 0 if the same
    // 1 if greater than
    //
    if(port == null)
            return -1;
    
    if(this.Address == null)
    {
      if(port.getAddress() == null)
        return 0;
      return 1;
    }
    
    if(port.getAddress() == null)
      return -1;
    
    int result = Address.compareTo(port.getAddress());

    // if the addresses are the same, compare the port numbers
    if(result == 0)
      result = this.PortNumber - port.getPortNumber();
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    return ((obj != null) && (obj instanceof OSM_Port) && (this.compareTo((OSM_Port)obj)==0));
  }

  /************************************************************
   * Method Name:
   *  toInfo
  **/
  /**
   * A pretty string for this port
   *
   * @see java.lang.Object#toString()
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @return
   ***********************************************************/
  
  public String toInfo()
  {
    StringBuffer buff = new StringBuffer();
    String formatString0 = "  %2d: %6s:  %s";
    String formatString = "%12s:  %s";
     if(getState().equals(OSM_PortState.DOWN))
       buff.append(String.format(formatString0, getPortNumber(), "state", getStateString()) + System.getProperty("line.separator"));
    else
    {
      buff.append(String.format(formatString0, getPortNumber(), "state", getStateString()) + System.getProperty("line.separator"));
      buff.append(String.format(formatString, "rate", getRateString()) + System.getProperty("line.separator"));
      buff.append(String.format(formatString, "speed", getSpeedString()) + System.getProperty("line.separator"));
      buff.append(String.format(formatString, "width", getWidthString()) + System.getProperty("line.separator"));
      buff.append(String.format(formatString, "errors?", hasError()) + System.getProperty("line.separator"));
    }
    return buff.toString();
  }

  /************************************************************
   * Method Name:
   *  toContent
  **/
  /**
   * A pretty string for this port
   *
   * @see java.lang.Object#toString()
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @return
   ***********************************************************/
  
  public String toContent()
  {
    return "Guid:" + getAddress().getGuid() + ", Port:" + getPortNumber();
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
   ***********************************************************/
  
  public String toVerboseString()
  {
    return "OSM_Port [pfmPort=" + pfmPort + ", sbnPort=" + sbnPort + ", Address=" + Address
        + ", PortNumber=" + PortNumber + "]";
  }
  
  public String toString()
  {
     return Integer.toString(this.getPortNumber());
  }
  


}
