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
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import org.xml.sax.InputSource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Fabric;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Node;
import gov.llnl.lc.infiniband.opensm.xml.IB_FabricConf;
import gov.llnl.lc.infiniband.opensm.xml.IB_LinkListElement;
import gov.llnl.lc.infiniband.opensm.xml.IB_PortElement;
import gov.llnl.lc.logging.CommonLogger;
import gov.llnl.lc.util.BinList;
import gov.llnl.lc.util.SystemConstants;

/**********************************************************************
 * Describe purpose and responsibility of IB_FabricJson
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version May 31, 2018 1:39:46 PM
 **********************************************************************/
public class IB_FabricJson implements Serializable, CommonLogger, Comparable<IB_FabricJson>
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

  public IB_FabricJson(IB_FabricConf fabricConf)
  {
    super();
    // these three are part of the FabricNameElement
    name  = fabricConf.getFabricName();
    speed = fabricConf.getIB_FabricNameElement().getSpeed();
    width = fabricConf.getIB_FabricNameElement().getWidth();
    
    System.err.println(fabricConf.toInfo());
    
    addNodes(fabricConf.getNodeElements());
  }
  
  public IB_FabricConf toIB_FabricConf()
  {
    // not perfect, but provides the basic elements
    String xml = toXmlString(true);
    
    InputSource is = new InputSource(new StringReader(xml));

    return new IB_FabricConf(is);
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
   * @return
   ***********************************************************/
  public String toXmlString(boolean concise)
  {
    // if concise, then only print children nvp if different than parents
    
    StringBuffer buff = new StringBuffer();

    // this is basically printing out the XML document, but using the Java Objects
    buff.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
    buff.append(SystemConstants.NEW_LINE);
    
    
    buff.append(getIndent(0));
    String elementName = "ibfabric";
    buff.append("<" + elementName + " name=\""+ name + "\" speed=\"" + speed + "\" width=\"" + width + "\" schemaVersion=\"1.0\">");

    buff.append(SystemConstants.NEW_LINE);
    for (IB_CaJson node: nodes)
      buff.append(node.toXmlString(concise, this));

    buff.append(getIndent(0));
    buff.append("</" + elementName + ">");
    buff.append(SystemConstants.NEW_LINE);
    return buff.toString();
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
   * @param nodeElements
   ***********************************************************/
  private void addNodes(ArrayList<IB_LinkListElement> nodeElements)
  {
    // used by the constructor with IB_FabricConf XML object
    if((nodeElements != null) && !nodeElements.isEmpty())
    {
      // determine the number of nodes
      ArrayList<IB_CaJson> nodeList = new ArrayList<IB_CaJson>();
      for(IB_LinkListElement lle: nodeElements)
      {
        // save this node, and then look through its links
        nodeList.add(new IB_CaJson(lle));
        
        for(IB_PortElement p: lle.getPortElements())
        {
          // the port has a remote node, which should be saved
          IB_CaJson rNode = new IB_CaJson(p);
          if(rNode != null)
            nodeList.add(rNode);
        }
      }
      
      IB_CaJson[] newNodes = new IB_CaJson[nodeList.size()];
      int ndex = 0;
      for(IB_CaJson n: nodeList)
      {
        newNodes[ndex] = n;
        ndex++;
      }
      setNodes(newNodes);
    }
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
      if((n != null) && (n.getSpeed() != null))
        spbL.add(n, n.getSpeed());
      if((n != null) && (n.getWidth() != null))
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
    {
      if(n == null)
        System.err.println("NULL NODES IN ARRAY!");
      else
        n.setChildSpeedAndWidth(getSpeed(), getWidth());
        
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
  
  public String toStats()
  {
    int numSwitches = 0;
    int numPorts = 0;
    for(IB_CaJson n: this.getNodes())
    {
      int nPorts = n.getPorts().length;
      numPorts += nPorts;
      if(nPorts > 1)
        numSwitches++;
    }

    StringBuffer buff = new StringBuffer();
    buff.append("fabric name: " + this.getName() + "\n");
    buff.append("speed:       " + this.getSpeed() + "\n");
    buff.append("width:       " + this.getWidth() + "\n");
    buff.append("# nodes:     " + this.getNodes().length + "\n");
    buff.append("# ports:     " + numPorts + "\n");
    buff.append("# links:     " + numPorts/2 + "\n");
    buff.append("# switches:  " + numSwitches + "\n");
    return buff.toString();
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

  public boolean isValid()
  {
    // this object should have at least name and a node
    boolean valid =  ((name != null) && (nodes != null) && (nodes.length > 0)) ? true: false;
    
    if(valid)
      return this.areNodeNamesUnique();
    return valid;
  }

  public boolean areNodeNamesUnique()
  {
    // the names in the array are the key, so if there
    // is more than one, they must be unique or else return false
    if((nodes == null) || (nodes.length < 2))
      return true;

    for (int i = 0; i < nodes.length-1; i++)
    {
      for (int j = i+1; j < nodes.length; j++)
      {
           if (nodes[i].equals(nodes[j]))
               return false;
      }
    }              
    return true;          
  }

  protected static String getIndent(int iLevel)
  {
    // to prettify things
    StringBuffer buff = new StringBuffer();
    int numSpacesPerIndent = 4;
    
    /* legal values are 0 through 6 */
    int level = iLevel < 0 ? 0: (iLevel > 6 ? 6: iLevel);
    int numSpaces = numSpacesPerIndent * level;
    
    for(int ndex = 0; ndex < numSpaces; ndex++)
      buff.append(" ");
    
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
  public int compareTo(IB_FabricJson o)
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
       (this.compareNodes(o) == 0))
      return 0;
     
    return 1;
  }
  /************************************************************
   * Method Name:
   *  compareNodes
  **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects
   *
   * @param o
   * @return
   ***********************************************************/
  private int compareNodes(IB_FabricJson o)
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
    
    int diff = this.getNodes().length - o.getNodes().length;
  
    if(diff != 0)
      return diff;
    
    // they are exactly the same size, so find the matching node name, and compare it
    // FIXME - can I assume all node names are distinct and valid? (don't need to be in same order)
    for( IB_CaJson myNode: getNodes())
    {
      for( IB_CaJson otherNode: o.getNodes())
      {
        // only compare the corresponding node
        if(!otherNode.getName().equals(myNode.getName()))
          continue;
        
        // matching node names, so check if everything else matches
        if(!myNode.equals(otherNode))
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
    return ((obj != null) && (obj instanceof IB_FabricJson) && (this.compareTo((IB_FabricJson)obj)==0));
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
   * @param currentFabric
   ***********************************************************/
  public String getDifferenceReport(IB_FabricJson currentFabric)
  {
    // before we compare, make sure all children are populated
    this.setChildSpeedAndWidth();
    currentFabric.setChildSpeedAndWidth();
    
    Set<IB_CaJson> expectedButNotFound = new HashSet<IB_CaJson>();
    Set<IB_CaJson> foundButNotExpected = new HashSet<IB_CaJson>();
    ArrayList<IB_CaJson> nodeList = new ArrayList<IB_CaJson>(Arrays.asList(currentFabric.getNodes()));
    foundButNotExpected.addAll(nodeList);
    
    StringBuffer buff = new StringBuffer();
    
    buff.append("Evaluating the Fabric connectivity...1\n");
    if(this.compareTo(currentFabric) != 0)
    {
      // assume "this" is the baseline, and the "current" is the question
      buff.append(this.toStats() + "\n");
      buff.append(currentFabric.toStats() + "\n");
      
      // iterate through my nodes, and find the correct one to compare it with
      for( IB_CaJson myNode: getNodes())
      {
        boolean nameMatch = false;
        for( IB_CaJson otherNode: currentFabric.getNodes())
        {
          // only compare the corresponding node
          if(!otherNode.getName().equals(myNode.getName()))
            continue;
          
          foundButNotExpected.remove(otherNode);  // found a match, so this is not "not expected"
          nameMatch = true;
          
          // matching node names, so check if everything else matches
          if(!myNode.equals(otherNode))
            buff.append(IB_CaJson.getDifferenceReport(myNode, this, otherNode, currentFabric));
        }
        
        if(!nameMatch)
          expectedButNotFound.add(myNode);
      }
      
      // show all the un-expected things, if any
      if(!expectedButNotFound.isEmpty())
      {
        buff.append("\nMissing Nodes of Fabric: " + this.getName() + ", (" + expectedButNotFound.size() + " missing)");
        for(IB_CaJson myNode: expectedButNotFound)
          buff.append(myNode.toJsonString(false, false, this) + "\n");
      }
      
      if(!foundButNotExpected.isEmpty())
      {
        buff.append("\nExtra or unexpected Nodes of Fabric: " + this.getName() + ", (" + foundButNotExpected.size() + " extra)");
        for(IB_CaJson otherNode: foundButNotExpected)
          buff.append(otherNode.toJsonString(false, false, currentFabric) + "\n");
      }
    }
    return buff.toString();
  }



}
