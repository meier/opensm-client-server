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
 *        file: IB_LinkJson.java
 *
 *  Created on: May 31, 2018
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.json;

import java.io.Serializable;

import gov.llnl.lc.infiniband.core.IB_Link;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Fabric;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_LinkSpeed;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_LinkWidth;
import gov.llnl.lc.infiniband.opensm.xml.IB_LinkListElement;
import gov.llnl.lc.infiniband.opensm.xml.IB_PortElement;
import gov.llnl.lc.logging.CommonLogger;
import gov.llnl.lc.util.SystemConstants;

/**********************************************************************
 * Describe purpose and responsibility of IB_LinkJson
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version May 31, 2018 1:35:37 PM
 **********************************************************************/
public class IB_LinkJson implements Serializable, CommonLogger, Comparable<IB_LinkJson>
{
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = 366212460937925310L;
  
  private String name;
  private String width;
  private String speed;
  private IB_PortJson1 localPort;
  private IB_PortJson1 remotePort;
  private int num;
  private int r_port;
  private String r_node;

  /************************************************************
   * Method Name:
   *  IB_LinkJson
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   ***********************************************************/
  public IB_LinkJson()
  {
  }

  public IB_LinkJson(String name, int num, String r_node, int r_port, String speed, String width)
  {
    this.name   = name;
    this.num    = num;
    this.r_node = r_node;
    this.r_port = r_port;
    this.speed  = speed;
    this.width  = width;
    
    // create local and remote ports, and set them
    localPort  = new IB_PortJson1(num, width, speed, name);
    remotePort = new IB_PortJson1(r_port, width, speed, r_node);
  }

  /************************************************************
   * Method Name:
   *  IB_LinkJson
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param lle
   ***********************************************************/
  public IB_LinkJson(IB_Link link, OSM_Fabric fabric, String Name)
  {
    // used by the constructor with IB_FabricConf XML object
    super();
    speed = OSM_LinkSpeed.get(link.Endpoint1).getSpeedName();
    width = OSM_LinkWidth.get(link.Endpoint1).getWidthName();
    
    localPort   = new IB_PortJson1(link.getEndpoint1(), fabric);
    remotePort  = new IB_PortJson1(link.getEndpoint2(), fabric);
    
    // assign the endpoint with the provided name, to local port
    if(Name.equalsIgnoreCase(remotePort.getName()))
    {
      // swap endpoints
      IB_PortJson1 tmpPort = remotePort;
      remotePort = localPort;
      localPort  = tmpPort;
    }
    
    this.name   = localPort.getName();
    this.num    = localPort.getNum();
    this.r_node = remotePort.getName();
    this.r_port = remotePort.getNum();
  }

  /************************************************************
   * Method Name:
   *  IB_LinkJson
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param lle
   ***********************************************************/
  public IB_LinkJson(IB_LinkListElement lle, IB_PortElement pe)
  {
    // used by the constructor with IB_FabricConf XML object
    super();
    name  = lle.getName();
    speed = pe.getSpeed();
    width = pe.getWidth();
    
    localPort  = new IB_PortJson1(Integer.parseInt(pe.getNumber()), pe.getWidth(), pe.getSpeed(), name);
    remotePort = new IB_PortJson1(Integer.parseInt(pe.getIB_RemotePortElement().getNumber()), pe.getWidth(), pe.getSpeed(), pe.getIB_RemoteNodeElement().getName());

    this.name   = localPort.getName();
    this.num    = localPort.getNum();
    this.r_node = remotePort.getName();
    this.r_port = remotePort.getNum();    
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

  public IB_PortJson1 getLocalPort()
  {
    return localPort;
  }

  public IB_PortJson1 getRemotePort()
  {
    return remotePort;
  }
  
  public int getNum()
  {
    return num;
  }

  public int getR_port()
  {
    return r_port;
  }

  public String getR_node()
  {
    return r_node;
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
  
  public String toJsonString(boolean pretty, boolean concise, IB_LinkListJson parent)
  {
    // if pretty, then name/value pair on each line
    // if concise, then only print children nvp if different than parents
    
    StringBuffer buff = new StringBuffer();
    String indent  = "        ";
    String padding = "  ";
    
    String prettyNL   = pretty ? "\n" + indent + padding: "";
    String continueNL = pretty ? ",\n" + indent + padding: ", ";

    buff.append(padding + "{ ");
    buff.append(prettyNL);
    
    // only add this speed and width if different than the LinkList speed and width
    
    String nameString  = concise && parent.getName().equalsIgnoreCase(this.getName()) ? "": "\"name\": \"" + this.getName() + "\"";
    String widthString = concise && parent.getWidth().equalsIgnoreCase(this.getWidth()) ? "": "\"width\": \"" + this.getWidth() + "\"";
    String speedString = concise && parent.getSpeed().equalsIgnoreCase(this.getSpeed()) ? "": "\"speed\": \"" + this.getSpeed() + "\"";
        
    // add the elements
    if(nameString.length() > 0)
    {
      buff.append(nameString);
      buff.append(continueNL );
    }

    buff.append("\"num\": " + getLocalPort().getNum());

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
  
    buff.append(continueNL );
    buff.append("\"r_port\": " + getRemotePort().getNum());
    
    buff.append(continueNL );
    buff.append("\"r_node\": \"" + getRemotePort().getName() + "\"");
    
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
  public String toLinkString(String delimiter)
  {
    // mimics the behavior of "ibparsefabricconf -d"delim""
    //
    // instead of using the ibfabricconf.xml file, uses the data structure
    // within IB_LinkJson
    StringBuffer buff = new StringBuffer();
    
    // get all of the link info
    buff.append(localPort.getName() + delimiter);
    buff.append(localPort.getNum() + delimiter);
    buff.append(remotePort.getNum() + delimiter);
    buff.append(remotePort.getName() + delimiter);
    buff.append(this.getSpeed() + delimiter);
    buff.append(this.getWidth());
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
    if(localPort != null)
    {
      if(localPort.getSpeed() == null)
        localPort.setSpeed(getSpeed());
      if(localPort.getWidth() == null)
        localPort.setWidth(getWidth());
    }
    
    if(remotePort != null)
    {
      if(remotePort.getSpeed() == null)
        remotePort.setSpeed(getSpeed());
      if(remotePort.getWidth() == null)
        remotePort.setWidth(getWidth());
    }
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
  public String toXmlString(boolean concise, IB_LinkListJson ib_LinkListJson)
  {
    // if concise, then only print children nvp if different than parents
    StringBuffer buff = new StringBuffer();

    // this is basically printing out the XML document, but using the Java Objects
    String indent = IB_FabricJson.getIndent(2);
    String elementName = "port";
    buff.append(indent);
    buff.append("<" + elementName + " num=\"" + localPort.getNum() + "\"");
    
    // add speed and width if !concise or if different than node
    if((!concise) || !(speed.equalsIgnoreCase(ib_LinkListJson.getSpeed())) || !(width.equalsIgnoreCase(ib_LinkListJson.getWidth())))
      buff.append(" speed=\"" + speed + "\" width=\"" + width + "\"");
    
    buff.append(">");
    // add the other end
    buff.append("<r_port>" + remotePort.getNum() + "</r_port><r_node>" + remotePort.getName() + "</r_node>");
    buff.append("</" + elementName + ">");
    buff.append(SystemConstants.NEW_LINE);
    
    return buff.toString();
  }

  public void setLocalPort(IB_PortJson1 localPort)
  {
    this.localPort = localPort;
  }

  public void setRemotePort(IB_PortJson1 remotePort)
  {
    this.remotePort = remotePort;
  }

  public void setNum(int num)
  {
    this.num = num;
  }

  public void setR_port(int r_port)
  {
    this.r_port = r_port;
  }

  public void setR_node(String r_node)
  {
    this.r_node = r_node;
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
  public int compareTo(IB_LinkJson o)
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
    if((this.getSpeed().equalsIgnoreCase(o.getSpeed())) &&
       (this.getWidth().equalsIgnoreCase(o.getWidth())) &&
       (this.comparePorts(o) == 0))
      return 0;
    
    // return -1 if local port is less than other port (and names are same)
    if(this.getName().equals(o.getName())) 
      if(this.getLocalPort().getNum() < o.getLocalPort().getNum())
        return -1;

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
  private int comparePorts(IB_LinkJson o)
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
    
    // this is a link, insensitive to remote/local designation.  Same if ports are reversed too
    
    if((localPort == null) || (remotePort == null) || (o.getLocalPort() == null) || (o.getRemotePort() == null))
      return -1;
    
    // return 0 if remote and local match, even if reversed
    if(localPort.equals(o.getLocalPort()) && remotePort.equals(o.getRemotePort()))
      return comparePortAttribs(this);
    
    if(localPort.equals(o.getRemotePort()) && remotePort.equals(o.getLocalPort()))
      return comparePortAttribs(this);

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
  private int comparePortAttribs(IB_LinkJson o)
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
    
    // this is a link, insensitive to remote/local designation.  Same if ports are reversed too
    
    if((localPort == null) || (o.getLocalPort() == null))
      return -1;
    
    // return 0 if remote and local speeds and width
      if( (o.getLocalPort().getSpeed().equalsIgnoreCase(localPort.getSpeed())) &&
          (o.getLocalPort().getWidth().equalsIgnoreCase(localPort.getWidth())))
         return 0;

    return 1;
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
    return ((obj != null) && (obj instanceof IB_LinkJson) && (this.compareTo((IB_LinkJson)obj)==0));
  }

  /************************************************************
   * Method Name:
   *  hasHostName
  **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects
   *
   * @param hostName
   * @return
   ***********************************************************/
  public boolean hasHostName(String hostName)
  {
    // return true if this host is at either end of the link
    if((localPort.getName() == null) || (remotePort.getName() == null))
      return false;

    if(localPort.getName().equals(hostName) || remotePort.getName().equals(hostName))
      return true;
    return false;
  }

  /************************************************************
   * Method Name:
   *  hasCommonEndpoint
  **/
  /**
   * Return true, if the local or remote port is the same as one
   * of the ports of this link.
   *
   * @see     describe related java objects
   *
   * @param ol
   * @return
   ***********************************************************/
  public boolean hasCommonEndpoint(IB_LinkJson ol)
  {
    // links have unique endpoints, so if two links share the same endpoint
    // that could be an indication of a problem
    
    if(localPort.isSameEndpoint(ol.getLocalPort()) ||
        localPort.isSameEndpoint(ol.getRemotePort()) ||
        remotePort.isSameEndpoint(ol.getLocalPort()) ||
        remotePort.isSameEndpoint(ol.getRemotePort()))
      return true;
    
    return false;
  }
  /************************************************************
   * Method Name:
   *  isValid
  **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects
   *
   * @return
   ***********************************************************/
  public boolean isValid()
  {
    // only valid if it actually has valid local and remote ports
    // also should have name and speed and width
    if((localPort == null) || (remotePort == null))
      return false;
    
    if((name == null) || (speed == null) || (width == null) || (r_node == null))
      return false;
    
    return true;
  }
}
