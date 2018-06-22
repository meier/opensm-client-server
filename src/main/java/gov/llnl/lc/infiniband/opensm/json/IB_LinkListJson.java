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
 *        file: IB_LinkListJson.java
 *
 *  Created on: May 31, 2018
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import gov.llnl.lc.infiniband.core.IB_Link;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Fabric;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Node;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Port;
import gov.llnl.lc.infiniband.opensm.xml.IB_LinkListElement;
import gov.llnl.lc.infiniband.opensm.xml.IB_PortElement;
import gov.llnl.lc.logging.CommonLogger;
import gov.llnl.lc.util.BinList;
import gov.llnl.lc.util.SystemConstants;

/**********************************************************************
 * Describe purpose and responsibility of IB_LinkListJson
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version May 31, 2018 1:35:37 PM
 **********************************************************************/
public class IB_LinkListJson implements Serializable, CommonLogger, Comparable<IB_LinkListJson>
{
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = 366212460937925310L;
  
  private String name;
  private String width;
  private String speed;
  private IB_LinkJson[] links;

  /************************************************************
   * Method Name:
   *  IB_LinkListJson
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   ***********************************************************/
  public IB_LinkListJson()
  {
    // TODO Auto-generated constructor stub
  }

  public IB_LinkListJson(IB_LinkListJson jsonObject, IB_FabricJson fab)
  {
    // copy constructor
    if ((jsonObject != null) && (jsonObject.getLinks() != null)
        && (jsonObject.getLinks().length > 0))
    {
      name  = jsonObject.getName();
      
      // if this objects speed exists, use it, otherwise use parents
      speed = jsonObject.getSpeed() == null ? fab.getSpeed(): jsonObject.getSpeed();
      width = jsonObject.getWidth() == null ? fab.getWidth(): jsonObject.getWidth();
      
      IB_LinkJson[] newLinks = new IB_LinkJson[jsonObject.getLinks().length];
      int ndex = 0;
      for (IB_LinkJson l : jsonObject.getLinks())
      {
        // if this objects speed exists, use it, otherwise use parents
        String lspeed = l.getSpeed() == null ? speed: l.getSpeed();
        String lwidth = l.getWidth() == null ? width: l.getWidth();
        
        newLinks[ndex] = new IB_LinkJson(name, l.getNum(), l.getR_node(), l.getR_port(), lspeed, lwidth);
        ndex++;
      }
      setLinks(newLinks);
    }
    else
      System.err.println("NULL OBJECTS in IBLLJ object");
  }

  /************************************************************
   * Method Name:
   *  IB_LinkListJson
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param lle
   ***********************************************************/
  public IB_LinkListJson(IB_LinkListElement lle)
  {
    // used by the constructor with IB_FabricConf XML object
    super();
    name  = lle.getName();
//    speed = lle.getSpeed();
//    width = lle.getWidth();
    
    addLinks(lle);
  }

  /************************************************************
  * Method Name:
  *  IB_LinkListJson
 **/
 /**
  * Describe the constructor here
  *
  * @see     describe related java objects
  *
  * @param la
  * @param ib_FabricJson
  ***********************************************************/
 public IB_LinkListJson(ArrayList<IB_Link> la, OSM_Fabric fabric, OSM_Node n)
 {
   super();
   this.name = n.pfmNode.getNode_name();
   
   addLinks(la, fabric, n);
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
    // DEPENDS on the links Array, look through that, and get the
    // dominant speed and width
    BinList <IB_LinkJson> spbL = new BinList <IB_LinkJson>();
    BinList <IB_LinkJson> wdbL = new BinList <IB_LinkJson>();
    
    for(IB_LinkJson l: links)
    {
      if(l != null)
      {
        if(l.getSpeed() != null)
          spbL.add(l, l.getSpeed());
        if(l.getWidth() != null)
          wdbL.add(l, l.getWidth());
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
  private void addLinks(OSM_Node n, OSM_Fabric fabric)
  {
    // iterate through all ports, and create an IB_PortJson for each one
    ArrayList<OSM_Port> oPorts = n.getOSM_Ports(fabric.getOsmPorts());
    ArrayList<IB_PortJson> portList = new ArrayList<IB_PortJson>();
    ArrayList<IB_LinkJson> linkList = new ArrayList<IB_LinkJson>();
    
    if((oPorts != null) && (!oPorts.isEmpty()))
    {
      for(OSM_Port p:oPorts)
      {
        if(p.hasRemote())  // is this port connected at the other end?
          portList.add(new IB_PortJson(p, fabric));
      }
      
      IB_PortJson[] newPorts = new IB_PortJson[portList.size()];
      IB_LinkJson[] newLinks = new IB_LinkJson[linkList.size()];
      int ndex = 0;
      for(IB_PortJson p: portList)
      {
          newPorts[ndex] = p;
          ndex++;
      }
      setLinks(newLinks);
    }
  }

  /************************************************************
   * Method Name:
   *  addLinks
  **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects
   *
   * @param lle
   ***********************************************************/
  private void addLinks(ArrayList<IB_Link> linkArray, OSM_Fabric fabric, OSM_Node n)
  {
    // constructor for this node, but limit it to the links in the link Array
    // this node may have more ports and links, but only use the ones from the
    // link array, and in this way, avoid duplicates
    
    ArrayList<OSM_Port> oPorts = n.getOSM_Ports(fabric.getOsmPorts());
    String nodeName = fabric.getNameFromGuid(n.getNodeGuid());

    if((linkArray != null) && !linkArray.isEmpty())
    {
      IB_LinkJson[] newLinks = new IB_LinkJson[linkArray.size()];
      int ndex = 0;
      for(IB_Link l: linkArray)
      {
        for(OSM_Port p:oPorts)
        {
          if(p.hasRemote())  // is this port connected at the other end?
          {
            // does this port match either end of this link?
            if(l.Endpoint1.equals(p))
            {
              // create the link with the help of this port
              newLinks[ndex++] = new IB_LinkJson(l, fabric, nodeName);
            }
            else if(l.Endpoint2.equals(p))
              System.err.println("Remote - should not happen!");
          }
        }
      }
      setLinks(newLinks);
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
  private void addLinks(IB_LinkListElement lle)
  {
    ArrayList<IB_PortElement> portElements = lle.getPortElements();

    // used by the constructor with IB_FabricConf XML object
    if((portElements != null) && !portElements.isEmpty())
    {
      IB_LinkJson[] newLinks = new IB_LinkJson[portElements.size()];
      int ndex = 0;
      for(IB_PortElement pe: portElements)
      {
        newLinks[ndex] = new IB_LinkJson(lle, pe);
        ndex++;
      }
      setLinks(newLinks);
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
   *  getLinks
  **/
  /**
   * Returns the value of ports
   *
   * @return the ports
   *
   ***********************************************************/
  
  public IB_LinkJson[] getLinks()
  {
    return links;
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
  
  public String toJsonString(boolean pretty, boolean concise, IB_FabricJson ib_FabricJson)
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
    
    // always start links on a new line
    buff.append(",\n" + indent + padding);
    buff.append(toLinksJsonString(pretty, concise));
    
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

  public void setLinks(IB_LinkJson[] links)
  {
    // sort these, in order of local port number
    Arrays.sort(links);
    this.links = links;
    
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
  private String toLinksJsonString(boolean pretty, boolean concise)
  {
    // if pretty, then name/value pair on each line
    // if concise, then only print children nvp if different than parents
    boolean initial = true;
    StringBuffer buff = new StringBuffer();
    String indent  = "    ";
    String padding = "  ";
    
    buff.append("\"links\": [");
    
    for (IB_LinkJson link: links)
    {
      if(!initial)
        buff.append(",\n" + indent + padding);
      else
      {
        initial = false;
        buff.append("\n" + indent + padding);
      }
      buff.append(link.toJsonString(pretty, concise, this));
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
  public String toLinkString(String delimiter)
  {
    // mimics the behavior of "ibparsefabricconf -d"delim""
    //
    // instead of using the ibfabricconf.xml file, uses the data structure
    // within IB_LinkListJson
    StringBuffer buff = new StringBuffer();
    
    // get all of the Node info
    for(IB_LinkJson link: links)
    {
      buff.append(link.toLinkString(delimiter));
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

    // use this lists speed and width, if the link isn't specified
    if((links != null) && (links.length > 0))
      for(IB_LinkJson l: links)
        l.setChildSpeedAndWidth(getSpeed(), getWidth());
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
  public String toXmlString(boolean concise, IB_FabricJson ib_FabricJson)
  {
    // if concise, then only print children nvp if different than parents
    StringBuffer buff = new StringBuffer();

    // this is basically printing out the XML document, but using the Java Objects
    
    // this corresponds to all the nodes in the fabric with links
    String indent = IB_FabricJson.getIndent(1);
    String elementName = "linklist";
    buff.append(indent);
    buff.append("<" + elementName + " name=\""+ name + "\"");

    // add speed and width if !concise or if different than node
    if((!concise) || !(speed.equalsIgnoreCase(ib_FabricJson.getSpeed())) || !(width.equalsIgnoreCase(ib_FabricJson.getWidth())))
      buff.append(" speed=\"" + speed + "\" width=\"" + width + "\"");
 
    buff.append(">");
    buff.append(SystemConstants.NEW_LINE);
    // this is basically printing out the XML document, but using the Java Objects
    for (IB_LinkJson link: links)
      buff.append(link.toXmlString(concise, this));

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
  public int compareTo(IB_LinkListJson o)
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
      System.err.println("ILLJ - The names are null");
    
    if((this.getSpeed() == null) || (o.getSpeed() == null))
      System.err.println("ILLJ - The speeds are null");
    
    if((this.getWidth() == null) || (o.getWidth() == null))
      System.err.println("ILLJ - The widths are null");
    
    if((this.getLinks() == null) || (o.getLinks() == null))
      System.err.println("ILLJ - The links are null");
    
     // only equal if everything is the same, otherwise return 1
    if((this.getName().equalsIgnoreCase(o.getName())) &&
       (this.getSpeed().equalsIgnoreCase(o.getSpeed())) &&
       (this.getWidth().equalsIgnoreCase(o.getWidth())) &&
       (this.compareLinks(o) == 0))
      return 0;
     
    return 1;
  }
  
  /************************************************************
   * Method Name:
   *  compareLinks
  **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects
   *
   * @param o
   * @return
   ***********************************************************/
  private int compareLinks(IB_LinkListJson o)
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
    
    int diff = this.getLinks().length - o.getLinks().length;
  
    if(diff != 0)
      return diff;   
    
    // they are exactly the same size, so find the matching local port number, and compare it
    for( IB_LinkJson myLink: getLinks())
    {
      int lpnum    = myLink.getLocalPort().getNum();
      String lname = myLink.getLocalPort().getName();
      
      boolean match = false;
      for( IB_LinkJson oLink: o.getLinks())
      {
        if((oLink.getLocalPort().getNum() == lpnum) && ( oLink.getLocalPort().getName().equals(lname)))
            match = myLink.equals(oLink);
        if(match)
          break;
        
        if((oLink.getRemotePort().getNum() == lpnum) && ( oLink.getRemotePort().getName().equals(lname)))
            match = myLink.equals(oLink);
        if(match)
          break;
      }
      // if I finished without a match, then return 1
      if(!match)
        return 1;
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
    return ((obj != null) && (obj instanceof IB_LinkListJson) && (this.compareTo((IB_LinkListJson)obj)==0));
  }


}
