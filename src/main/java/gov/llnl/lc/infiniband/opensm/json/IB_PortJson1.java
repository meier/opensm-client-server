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
 *        file: IB_PortJson1.java
 *
 *  Created on: May 31, 2018
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.json;

import java.io.Serializable;

import gov.llnl.lc.infiniband.core.IB_Guid;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Fabric;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Port;
import gov.llnl.lc.infiniband.opensm.xml.IB_PortElement;
import gov.llnl.lc.logging.CommonLogger;

/**********************************************************************
 * Describe purpose and responsibility of IB_PortJson1
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version May 31, 2018 1:31:13 PM
 **********************************************************************/
public class IB_PortJson1 implements Serializable, CommonLogger, Comparable<IB_PortJson1>
{
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = -2750711243008670899L;

  private int num;
  private String width;
  private String speed;
  private String name;

  /************************************************************
   * Method Name:
   *  IB_PortJson1
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   ***********************************************************/
  public IB_PortJson1()
  {
    // TODO Auto-generated constructor stub
  }

  /************************************************************
   * Method Name:
   *  IB_PortJson1
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param num
   * @param width
   * @param speed
   * @param name
   ***********************************************************/
  public IB_PortJson1(int num, String width, String speed, String name)
  {
    super();
    this.num = num;
    this.width = width;
    this.speed = speed;
    this.name = name;  // my hostname
  }

  /************************************************************
   * Method Name:
   *  IB_PortJson1
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param p
   * @param fabric
   ***********************************************************/
  public IB_PortJson1(OSM_Port p, OSM_Fabric fabric)
  {
    super();
    num = p.getPortNumber();
    
    // determine speed, width, and link info
    speed = p.getSpeedString() == null ? "unknown": p.getSpeedString();
    width = p.getWidthString() == null ? "unknown": p.getWidthString();
    
    name = fabric.getNameFromGuid(new IB_Guid(p.getNodeGuid()));
  }

  /************************************************************
   * Method Name:
   *  IB_PortJson1
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param pe
   ***********************************************************/
  public IB_PortJson1(IB_PortElement pe)
  {
    // used by the constructor with IB_FabricConf XML object
    super();
    num  = Integer.parseInt(pe.getNumber());
    speed = pe.getSpeed();
    width = pe.getWidth();
    
    name = "unknown node name";
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

  public String getName()
  {
    return name;
  }
  

  public void setName(String name)
  {
    this.name = name;
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
  protected void setSpeedAndWidth(String parentSpeed, String parentWidth)
  {
    // the parents speed and width are supplied if the port doesn't
    // have one
    if(getSpeed() == null)
      setSpeed(parentSpeed);
    
    if(getWidth() == null)
      setWidth(parentWidth);
    
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
  public int compareTo(IB_PortJson1 o)
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
    
    if((this.getName() == null) || (o.getName() == null))
      return -1;
    
    if((this.getSpeed() == null) || (o.getSpeed() == null))
      return -1;
    
    if((this.getWidth() == null) || (o.getWidth() == null))
      return -1;
    
    
    // only equal if everything is the same, otherwise return 1
    if((this.getNum()   == o.getNum()) &&
       (this.getSpeed().equalsIgnoreCase(o.getSpeed())) &&
       (this.getWidth().equalsIgnoreCase(o.getWidth())) &&
       (this.getName().equalsIgnoreCase(o.getName())))
      return 0;
    
    // return -1 if this port number is less than other one
    if(this.getNum() < o.getNum())
      return -1;
     
    return 1;
  }
  
  public boolean isSameEndpoint(IB_PortJson1 o)
  {
    // compare name and num only
    if((o == null) || (name == null) || (o.getName() == null))
      return false;
    
    if((this.getNum()   == o.getNum()) &&
        (this.getName().equalsIgnoreCase(o.getName())))
       return true;

    return false;
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
    return ((obj != null) && (obj instanceof IB_PortJson1) && (this.compareTo((IB_PortJson1)obj)==0));
  }

}
