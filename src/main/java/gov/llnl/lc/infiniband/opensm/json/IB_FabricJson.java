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
 *        file: IB_FabricJson.java
 *
 *  Created on: May 31, 2018
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.json;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Fabric;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Node;
import gov.llnl.lc.util.BinList;

/**********************************************************************
 * Describe purpose and responsibility of IB_FabricJson
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version May 31, 2018 1:39:46 PM
 **********************************************************************/
public class IB_FabricJson implements Serializable, gov.llnl.lc.logging.CommonLogger
{
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = -5916131949556310740L;
  
  private String name;
  private String width;
  private String speed;
  private IB_CaJson[] nodes;

  /************************************************************
   * Method Name:
   *  IB_FabricJson
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   ***********************************************************/
  public IB_FabricJson()
  {
    // TODO Auto-generated constructor stub
  }
  
  public IB_FabricJson(String fileName)
  {
    if(fileName != null)
    {
      try
      {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        IB_FabricJson json = gson.fromJson(bufferedReader, IB_FabricJson.class);
        
        // if the json document was parsed correctly, then brute force copy
        if((json != null) && (json instanceof IB_FabricJson))
        {
          setName(json.getName());
          setSpeed(json.getSpeed());
          setWidth(json.getWidth());
          
          // set the nodes last, will also set the ports
          // but will also cause the speed and width to be recalculated
          setNodes(json.getNodes());
        }
      }
       catch (Exception e)
      {
        logger.severe("Could NOT parse json FabricConf file");
        logger.severe(e.getMessage());
      }      
    }
  }
  
  public IB_FabricJson(OSM_Fabric fabric)
  {
    super();
    name = fabric.getFabricName(true);
    
    addNodes(fabric);
  }

  /************************************************************
   * Method Name:
   *  addNodes
  **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects
   *
   * @param fabric
   ***********************************************************/
  private void addNodes(OSM_Fabric fabric)
  {
    // iterate through all nodes, and create an IB_CaJson for each one
    LinkedHashMap<String, OSM_Node> oNodes = fabric.getOSM_Nodes();
    
    if((oNodes != null) && (!oNodes.isEmpty()))
    {
      IB_CaJson[] newNodes = new IB_CaJson[oNodes.size()];
      int ndex = 0;
      Set<String> keys = oNodes.keySet();
      for(String k:keys)
      {
        OSM_Node n = oNodes.get(k);
        newNodes[ndex] = new IB_CaJson(n, fabric);
        ndex++;
      }
      setNodes(newNodes);
    }
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
    // DEPENDS on the nodes array, look through that, and get the
    // dominant speed and width
    BinList <IB_CaJson> spbL = new BinList <IB_CaJson>();
    BinList <IB_CaJson> wdbL = new BinList <IB_CaJson>();
    for(IB_CaJson n: nodes)
    {
      spbL.add(n, n.getSpeed());
      wdbL.add(n, n.getWidth());
    }
    
    // the majority wins (unless already specified)
    String dominantSpeed = spbL.getMaxBinKey();
    String dominantWidth = wdbL.getMaxBinKey();
    
    if(dominantSpeed != null)
      setSpeed(dominantSpeed);
    
    if(dominantWidth != null)
      setWidth(dominantWidth);
    
    setChildSpeedAndWidth();
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
  private void setChildSpeedAndWidth()
  {
    for(IB_CaJson n: nodes)
      n.setChildSpeedAndWidth(getSpeed(), getWidth());
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
   *  getWidth
  **/
  /**
   * Returns the value of width
   *
   * @return the width
   *
   ***********************************************************/
  
  public String getWidth()
  {
    return width;
  }

  /************************************************************
   * Method Name:
   *  getSpeed
  **/
  /**
   * Returns the value of speed
   *
   * @return the speed
   *
   ***********************************************************/
  
  public String getSpeed()
  {
    return speed;
  }

  /************************************************************
   * Method Name:
   *  getNodes
  **/
  /**
   * Returns the value of nodes
   *
   * @return the nodes
   *
   ***********************************************************/
  
  public IB_CaJson[] getNodes()
  {
    return nodes;
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
  
  public String toJsonString(boolean pretty, boolean concise)
  {
    // if pretty, then name/value pair on each line
    // if concise, then only print children nvp if different than parents
    
    StringBuffer buff = new StringBuffer();
    String padding = "  ";
    buff.append("{ ");
    
    String prettyNL = pretty ? "\n" + padding: "";
    buff.append(prettyNL );

    buff.append("\"name\": \"" + name + "\",");
    
    buff.append(prettyNL );
    buff.append("\"width\": \"" + width + "\","); 
    
    buff.append(prettyNL );
    buff.append("\"speed\": \"" + speed + "\","); 
    
    buff.append(toNodesJsonString(pretty, concise));
    
    if(pretty)
      buff.append("\n");
    buff.append("}");
    return buff.toString();
  }

  /************************************************************
   * Method Name:
   *  toLinkString
  **/
  /**
   * Provides one line of information for each link in the fabric.
   * The delimiter string is used to separate field values.
   *
   * @see java.lang.Object#toString()
   *
   * @return
   ***********************************************************/
  
  public String toLinkString(String delimiter)
  {
    // mimics the behavior of "ibparsefabricconf -d"delim""
    //
    // instead of using the ibfabricconf.xml file, uses the data structure
    // within IB_FabricJson
    StringBuffer buff = new StringBuffer();
    
    // get all of the Node info
    for(IB_CaJson node: nodes)
      buff.append(node.toLinkString(this, delimiter));

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

  public void setNodes(IB_CaJson[] nodes)
  {
    this.nodes = nodes;
    
    // set the dominant speed and width
    determinSpeedAndWidth();
  }

  /************************************************************
   * Method Name:
   *  toNodesJsonString
  **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects
   *
   * @return
   ***********************************************************/
  private String toNodesJsonString(boolean pretty, boolean concise)
  {
    // if pretty, then name/value pair on each line
    // if concise, then only print children nvp if different than parents
    boolean initial = true;
    StringBuffer buff = new StringBuffer();
    String padding = "  ";

    // always start nodes on a new line    
    buff.append("\n" + padding + "\"nodes\": [");
    
    for (IB_CaJson node: nodes)
    {
      if(!initial)
        buff.append(",");
      else
        initial = false;
      buff.append(node.toJsonString(pretty, concise, this));
    }
    if(pretty)
      buff.append("\n" + padding);
    buff.append("]");
    
    return buff.toString();    
  }
  
}
