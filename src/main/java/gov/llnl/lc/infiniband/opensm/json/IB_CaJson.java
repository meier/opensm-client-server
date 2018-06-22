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
 *        file: IB_CaJson.java
 *
 *  Created on: May 31, 2018
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import gov.llnl.lc.infiniband.core.IB_Guid;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Fabric;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Node;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Port;
import gov.llnl.lc.infiniband.opensm.xml.IB_LinkListElement;
import gov.llnl.lc.infiniband.opensm.xml.IB_PortElement;
import gov.llnl.lc.logging.CommonLogger;
import gov.llnl.lc.util.BinList;
import gov.llnl.lc.util.SystemConstants;

/**********************************************************************
 * Describe purpose and responsibility of IB_CaJson
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version May 31, 2018 1:35:37 PM
 **********************************************************************/
public class IB_CaJson implements Serializable, CommonLogger, Comparable<IB_CaJson>
{
  @Deprecated
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = 366212460937925310L;
  
  private String name;
  private String width;
  private String speed;
  private IB_PortJson[] ports;

  /************************************************************
   * Method Name:
   *  IB_CaJson
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   ***********************************************************/
  public IB_CaJson()
  {
    // TODO Auto-generated constructor stub
  }

  /************************************************************
   * Method Name:
   *  IB_CaJson
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param n
   * @param fabric
   ***********************************************************/
  public IB_CaJson(OSM_Node n, OSM_Fabric fabric)
  {
    super();
    IB_Guid  g = n.getNodeGuid();
    name = fabric.getNameFromGuid(g);
    
    addPorts(n, fabric);
  }

  /************************************************************
   * Method Name:
   *  IB_CaJson
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param lle
   ***********************************************************/
  public IB_CaJson(IB_LinkListElement lle)
  {
    // used by the constructor with IB_FabricConf XML object
    super();
    name  = lle.getName();
    speed = lle.getSpeed();
    width = lle.getWidth();
    
    addPorts(lle);
  }

  /************************************************************
   * Method Name:
   *  IB_CaJson
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param lle
   ***********************************************************/
  public IB_CaJson(IB_PortElement p)
  {
    // this is for adding a RemoteNode element and port from a link
    super();
    name  = p.getIB_RemoteNodeElement().getName().trim();
    speed = p.getSpeed();
    width = p.getWidth();
    
    // by definition, just a single port for this constructor
    IB_PortJson[] newPorts = new IB_PortJson[1];
    newPorts[0] = new IB_PortJson(p);
    setPorts(newPorts);
  }

   /************************************************************
   * Method Name:
   *  determinSpeedAndWidth
  **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects
   *
   * @param fabric
   ***********************************************************/
  private void determinSpeedAndWidth()
  {
    // DEPENDS on the ports Array, look through that, and get the
    // dominant speed and width
    
    BinList <IB_PortJson> spbL = new BinList <IB_PortJson>();
    BinList <IB_PortJson> wdbL = new BinList <IB_PortJson>();
    for(IB_PortJson p: ports)
    {
      if(p!= null)
      {
        if(p.getSpeed() != null)
          spbL.add(p, p.getSpeed());
        if(p.getWidth() != null)
          wdbL.add(p, p.getWidth());
      }
    }
    
    // the majority wins (unless already specified)
    String dominantSpeed = spbL.getMaxBinKey();
    String dominantWidth = wdbL.getMaxBinKey();
    
    if(dominantSpeed != null)
      setSpeed(dominantSpeed);
    
    if(dominantWidth != null)
      setWidth(dominantWidth);
  }

  /************************************************************
   * Method Name:
   *  addPorts
  **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects
   *
   * @param n
   * @param fabric
   ***********************************************************/
  private void addPorts(OSM_Node n, OSM_Fabric fabric)
  {
    // iterate through all ports, and create an IB_PortJson for each one
    ArrayList<OSM_Port> oPorts = n.getOSM_Ports(fabric.getOsmPorts());
    ArrayList<IB_PortJson> portList = new ArrayList<IB_PortJson>();
    
    if((oPorts != null) && (!oPorts.isEmpty()))
    {
      for(OSM_Port p:oPorts)
      {
        if(p.hasRemote())  // is this port connected at the other end?
          portList.add(new IB_PortJson(p, fabric));
      }
      
      IB_PortJson[] newPorts = new IB_PortJson[portList.size()];
      int ndex = 0;
      for(IB_PortJson p: portList)
      {
          newPorts[ndex] = p;
          ndex++;
      }
      
      setPorts(newPorts);
    }
  }

  /************************************************************
   * Method Name:
   *  addPorts
  **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects
   *
   * @param lle
   ***********************************************************/
  private void addPorts(IB_LinkListElement lle)
  {
    ArrayList<IB_PortElement> portElements = lle.getPortElements();

    // used by the constructor with IB_FabricConf XML object
    if((portElements != null) && !portElements.isEmpty())
    {
      IB_PortJson[] newPorts = new IB_PortJson[portElements.size()];
      int ndex = 0;
      for(IB_PortElement pe: portElements)
      {
        newPorts[ndex] = new IB_PortJson(pe);
        ndex++;
      }
      setPorts(newPorts);
    }
    
  }

  /************************************************************
   * Method Name:
   *  getName
  **/
  /**
   * Returns the value of name
   *
   * @return the name
   *
   ***********************************************************/
  
  public String getName()
  {
    return name;
  }

  /************************************************************
   * Method Name:
   *  getPorts
  **/
  /**
   * Returns the value of ports
   *
   * @return the ports
   *
   ***********************************************************/
  
  public IB_PortJson[] getPorts()
  {
    return ports;
  }

  /************************************************************
   * Method Name:
   *  getSpeed
  **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects
   *
   * @return
   ***********************************************************/
  public String getSpeed()
  {
    return speed;
  }

  /************************************************************
   * Method Name:
   *  getWidth
  **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects
   *
   * @return
   ***********************************************************/
  public String getWidth()
  {
    return width;
  }

  /************************************************************
   * Method Name:
   *  toJsonString
  **/
  /**
   * Describe the method here
   * @param ib_FabricJson 
   *
   * @see java.lang.Object#toString()
   *
   * @return
   ***********************************************************/
  
  public String toJsonString(boolean pretty, boolean concise, IB_FabricJson1 ib_FabricJson)
  {
    // if pretty, then name/value pair on each line
    // if concise, then only print children nvp if different than parents
    
    StringBuffer buff = new StringBuffer();
    String indent  = "    ";
    String padding = "  ";
    
    String prettyNL   = pretty ? "\n" + indent + padding: "";
    String continueNL = pretty ? ",\n" + indent + padding: ", ";
    
    String widthString = concise && ib_FabricJson.getWidth().equalsIgnoreCase(getWidth()) ? "": "\"width\": \"" + width + "\"";
    String speedString = concise && ib_FabricJson.getSpeed().equalsIgnoreCase(getSpeed()) ? "": "\"speed\": \"" + speed + "\"";
    
    // add the elements
    buff.append("\n" + indent + "{ ");
    buff.append(prettyNL);
    buff.append("\"name\": \"" + name + "\"");

    if(widthString.length() > 0)
    {
      buff.append(continueNL );
      buff.append(widthString);
    }
  
    if(speedString.length() > 0)
    {
      buff.append(continueNL );
      buff.append(speedString);
    }
    
    // always start ports on a new line
    buff.append(",\n" + indent + padding);
    buff.append(toPortsJsonString(pretty, concise));
    
    // done with ports
    if(pretty)
      buff.append("\n" + indent);
    buff.append("}");
    return buff.toString();
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public void setWidth(String width)
  {
    this.width = width;
  }

  public void setSpeed(String speed)
  {
    this.speed = speed;
  }

  public void setPorts(IB_PortJson[] ports)
  {
    this.ports = ports;
    
    // set the dominant speed and width
    determinSpeedAndWidth();
  }

  /************************************************************
   * Method Name:
   *  toPortsJsonString
  **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects
   *
   * @param pretty
   * @param concise
   * @param ib_FabricJson 
   * @return
   ***********************************************************/
  private String toPortsJsonString(boolean pretty, boolean concise)
  {
    // if pretty, then name/value pair on each line
    // if concise, then only print children nvp if different than parents
    boolean initial = true;
    StringBuffer buff = new StringBuffer();
    String indent  = "    ";
    String padding = "  ";
    
    buff.append("\"ports\": [");
    
    for (IB_PortJson port: ports)
    {
      if(!initial)
        buff.append(",\n" + indent + padding);
      else
      {
        initial = false;
        buff.append("\n" + indent + padding);
      }
      buff.append(port.toJsonString(pretty, concise, this));
    }
    
    if(pretty)
      buff.append("\n" + indent + padding);
    buff.append("]");
    
    return buff.toString();
  }

  /************************************************************
   * Method Name:
   *  toLinkString
  **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects
   *
   * @param delimiter
   * @return
   ***********************************************************/
  public String toLinkString(IB_FabricJson1 parent, String delimiter)
  {
    // mimics the behavior of "ibparsefabricconf -d"delim""
    //
    // instead of using the ibfabricconf.xml file, uses the data structure
    // within IB_CaJson
    StringBuffer buff = new StringBuffer();
    
    // get all of the Node info
    for(IB_PortJson port: ports)
    {
      buff.append(port.toLinkString(this, delimiter));
      buff.append(SystemConstants.NEW_LINE);
    }

    return buff.toString();
  }

  /************************************************************
   * Method Name:
   *  setChildSpeedAndWidth
  **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects
   *
   * @param speed2
   * @param width2
   ***********************************************************/
  protected void setChildSpeedAndWidth(String parentSpeed, String parentWidth)
  {
    // the parents speed and width are supplied if the node doesn't
    // have one
    if(getSpeed() == null)
      setSpeed(parentSpeed);
    
    if(getWidth() == null)
      setWidth(parentWidth);

    // use this nodes speed and width, if the port isn't specified
    if((ports != null) && (ports.length > 0))
      for(IB_PortJson p: ports)
        p.setChildSpeedAndWidth(getSpeed(), getWidth());
    else
      logger.warning("Null ports array when attempting to set speed and width");
      
  }

  /************************************************************
   * Method Name:
   *  toXmlString
  **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects
   *
   * @param concise
   * @param ib_FabricJson
   * @return
   ***********************************************************/
  public String toXmlString(boolean concise, IB_FabricJson1 ib_FabricJson)
  {
    // if concise, then only print children nvp if different than parents
    StringBuffer buff = new StringBuffer();

    // this is basically printing out the XML document, but using the Java Objects
    String indent = IB_FabricJson1.getIndent(1);
    String elementName = "linklist";
    buff.append(indent);
    buff.append("<" + elementName + " name=\""+ name + "\"");

    // add speed and width if !concise or if different than node
    if((!concise) || !(speed.equalsIgnoreCase(ib_FabricJson.getSpeed())) || !(width.equalsIgnoreCase(ib_FabricJson.getWidth())))
      buff.append(" speed=\"" + speed + "\" width=\"" + width + "\"");
 
    buff.append(">");
    buff.append(SystemConstants.NEW_LINE);
    // this is basically printing out the XML document, but using the Java Objects
    for (IB_PortJson port: ports)
      buff.append(port.toXmlString(concise, this));

    buff.append(indent);
    buff.append("</" + elementName + ">");
    buff.append(SystemConstants.NEW_LINE);
    return buff.toString();
  }

  /************************************************************
   * Method Name:
   *  compareTo
  **/
  /**
   * Describe the method here
   *
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   *
   * @param o
   * @return
   ***********************************************************/
  
  @Override
  public int compareTo(IB_CaJson o)
  {
    // both objects must exist (and of the same class)
    // and should be consistent with equals
    //
    // -1 if less than
    // 0 if the same
    // 1 if greater than
    //
    if(o == null)
        return -1;
    
    // only equal if everything is the same, otherwise return 1
    if((this.getName().equalsIgnoreCase(o.getName())) &&
       (this.getSpeed().equalsIgnoreCase(o.getSpeed())) &&
       (this.getWidth().equalsIgnoreCase(o.getWidth())) &&
       (this.comparePorts(o) == 0))
      return 0;
     
    return 1;
  }
  
  /************************************************************
   * Method Name:
   *  comparePorts
  **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects
   *
   * @param o
   * @return
   ***********************************************************/
  private int comparePorts(IB_CaJson o)
  {
    // both objects must exist (and of the same class)
    // and should be consistent with equals
    //
    // -1 if less than
    // 0 if the same
    // 1 if greater than
    //
    if(o == null)
      return -1;
    
    int diff = this.getPorts().length - o.getPorts().length;
  
    if(diff != 0)
      return diff;
    
    // they are exactly the same size, so find the matching port number, and compare it
    // FIXME - can I assume all portnumbers are distinct and valid? (don't need to be in same order)
    for( IB_PortJson myPort: getPorts())
    {
      for( IB_PortJson otherPort: o.getPorts())
      {
        // only compare the corresponding port
        if(otherPort.getNum() != myPort.getNum())
          continue;
        
        // matching portnum, so check if everything else matches
        if(!myPort.equals(otherPort))
          return 1;
      }
    }
    // If here, arrays compared favorably
    return 0;
  }

  /************************************************************
   * Method Name:
   *  equals
  **/
  /**
   * Describe the method here
   *
   * @see java.lang.Object#equals(java.lang.Object)
   *
   * @param obj
   * @return
   ***********************************************************/
  
  @Override
  public boolean equals(Object obj)
  {
    return ((obj != null) && (obj instanceof IB_CaJson) && (this.compareTo((IB_CaJson)obj)==0));
  }

  /************************************************************
   * Method Name:
   *  getDifferenceReport
  **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects
   *
   * @param myNode
   * @param ib_FabricJson
   * @param otherNode
   * @param currentFabric
   * @return
   ***********************************************************/
  public static String getDifferenceReport(IB_CaJson myNode, IB_FabricJson1 myFabric,
      IB_CaJson otherNode, IB_FabricJson1 currentFabric)
  {
    StringBuffer buff = new StringBuffer();

    Set<IB_PortJson> expectedButNotFound = new HashSet<IB_PortJson>();
    Set<IB_PortJson> foundButNotExpected = new HashSet<IB_PortJson>();
    ArrayList<IB_PortJson> portList = new ArrayList<IB_PortJson>( Arrays.asList(otherNode.getPorts()));
    
    // start with a full set, then remove
    foundButNotExpected.addAll(portList);

    // matching node names, so check if everything else matches
    if (!myNode.equals(otherNode))
    {
      buff.append("\nDifferences for Node: " + myNode.getName() + "\n");
      // assume "this" is the baseline, and the "current" is the question

      // iterate through my ports, and find the correct one to compare it with
      for (IB_PortJson myPort : myNode.getPorts())
      {
        boolean numMatch = false;
        for (IB_PortJson otherPort : otherNode.getPorts())
        {
          // only compare the corresponding port
          if (myPort.getNum() != otherPort.getNum())
            continue;

          foundButNotExpected.remove(otherPort); // found a match, so this is not "not expected"
          numMatch = true;

          // matching node names, so check if everything else matches
          if (!myPort.equals(otherPort))
            buff.append(IB_PortJson.getDifferenceReport(myPort, myNode, myFabric, otherPort, otherNode, currentFabric));
        }

        if (!numMatch)
          expectedButNotFound.add(myPort);
      }

      // show all the un-expected things, if any
      if (!expectedButNotFound.isEmpty())
      {
        buff.append("  Missing Ports of Node: " + myNode.getName()  + ", (" + expectedButNotFound.size() + " missing)\n");
        for (IB_PortJson myPort : expectedButNotFound)
          buff.append("  " + myPort.toJsonString(false, false, myNode) + "\n");
      }

      if (!foundButNotExpected.isEmpty())
      {
        buff.append("  Extra or unexpected Ports of Node: " + otherNode.getName() + ", (" + foundButNotExpected.size() + " extra)\n");
        for (IB_PortJson otherPort : foundButNotExpected)
          buff.append("  " + otherPort.toJsonString(false, false, otherNode) + "\n");
      }
    }
    return buff.toString();
  }

}
