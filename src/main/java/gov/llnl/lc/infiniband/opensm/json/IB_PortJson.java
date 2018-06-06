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
 *        file: IB_PortJson.java
 *
 *  Created on: May 31, 2018
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.json;

import java.io.Serializable;

import gov.llnl.lc.infiniband.core.IB_Guid;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Fabric;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Port;

/**********************************************************************
 * Describe purpose and responsibility of IB_PortJson
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version May 31, 2018 1:31:13 PM
 **********************************************************************/
public class IB_PortJson implements Serializable, gov.llnl.lc.logging.CommonLogger
{
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = -2750711243008670899L;

  private int num;
  private String width;
  private String speed;
  private int r_port;
  private String r_node;

  /************************************************************
   * Method Name:
   *  IB_PortJson
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   ***********************************************************/
  public IB_PortJson()
  {
    // TODO Auto-generated constructor stub
  }

  /************************************************************
   * Method Name:
   *  IB_PortJson
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param p
   * @param fabric
   ***********************************************************/
  public IB_PortJson(OSM_Port p, OSM_Fabric fabric)
  {
    super();
    num = p.getPortNumber();
    
    // determine speed, width, and link info
    speed = p.getSpeedString() == null ? "unknown": p.getSpeedString();
    width = p.getWidthString() == null ? "unknown": p.getWidthString();
    
    r_port = p.sbnPort.linked_port_num;
    r_node = fabric.getNameFromGuid(new IB_Guid(p.sbnPort.linked_node_guid));
  }

  /************************************************************
   * Method Name:
   *  getNum
  **/
  /**
   * Returns the value of num
   *
   * @return the num
   *
   ***********************************************************/
  
  public int getNum()
  {
    return num;
  }

  /************************************************************
   * Method Name:
   *  getR_port
  **/
  /**
   * Returns the value of r_port
   *
   * @return the r_port
   *
   ***********************************************************/
  
  public int getR_port()
  {
    return r_port;
  }

  /************************************************************
   * Method Name:
   *  getR_node
  **/
  /**
   * Returns the value of r_node
   *
   * @return the r_node
   *
   ***********************************************************/
  
  public String getR_node()
  {
    return r_node;
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
   *
   * @see java.lang.Object#toString()
   *
   * @return
   ***********************************************************/
  
  public String toJsonString(boolean pretty, boolean concise, IB_CaJson ib_CaJson)
  {
    // if pretty, then name/value pair on each line
    // if concise, then only print children nvp if different than parents
    
    
    StringBuffer buff = new StringBuffer();
    String indent  = "        ";
    String padding = "  ";
    
    String prettyNL   = pretty ? "\n" + indent + padding: "";
    String continueNL = pretty ? ",\n" + indent + padding: ", ";

    buff.append(padding + "{ ");
    
    String widthString = concise && ib_CaJson.getWidth().equalsIgnoreCase(getWidth()) ? "": "\"width\": \"" + width + "\"";
    String speedString = concise && ib_CaJson.getSpeed().equalsIgnoreCase(getSpeed()) ? "": "\"speed\": \"" + speed + "\"";
        
    // add the elements
    buff.append(prettyNL);
    buff.append("\"num\": " + num);

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
    buff.append("\"r_port\": " + r_port);
    
    buff.append(continueNL );
    buff.append("\"r_node\": \"" + r_node + "\"");
    
    if(pretty)
      buff.append("\n" + indent);
    buff.append("}");
    return buff.toString();
  }

  public void setNum(int num)
  {
    this.num = num;
  }

  public void setWidth(String width)
  {
    this.width = width;
  }

  public void setSpeed(String speed)
  {
    this.speed = speed;
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
   *  toLinkString
  **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects
   *
   * @param ib_CaJson
   * @param delimiter
   * @return
   ***********************************************************/
  public String toLinkString(IB_CaJson ib_CaJson, String delimiter)
  {
    // mimics the behavior of "ibparsefabricconf -d"delim""
    //
    // instead of using the ibfabricconf.xml file, uses the data structure
    // within IB_PortJson
    
    StringBuffer buff = new StringBuffer();
    buff.append(ib_CaJson.getName());
    buff.append( delimiter);
    buff.append(getNum());
    buff.append( delimiter);
    buff.append(getR_port());
    buff.append( delimiter);
    buff.append(getR_node());
    buff.append( delimiter);
    buff.append(getSpeed());
    buff.append( delimiter);
    buff.append(getWidth());
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
    // the parents speed and width are supplied if the port doesn't
    // have one
    if(getSpeed() == null)
      setSpeed(parentSpeed);
    
    if(getWidth() == null)
      setWidth(parentWidth);
    
  }
}
