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
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.xml.sax.InputSource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gov.llnl.lc.infiniband.core.IB_Link;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Fabric;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Node;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Port;
import gov.llnl.lc.infiniband.opensm.xml.IB_FabricConf;
import gov.llnl.lc.infiniband.opensm.xml.IB_LinkListElement;
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
  private IB_LinkListJson[] nodes;
  
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
  
  public IB_FabricJson cloneIB_FabricJson(IB_FabricJson srcObject)
  {
    // copy constructor
    // we need this for reading files that don't exactly conform to this object

    if ((srcObject != null) && (srcObject instanceof IB_FabricJson))
    {
      // copy the fabric stuff
      setName(srcObject.getName());
      setSpeed(srcObject.getSpeed());
      setWidth(srcObject.getWidth());

      // add the links last, will also set the ports
      // but will also cause the speed and width to be recalculated
      addLinkLists(srcObject.getNodes());
    }
    return this;
  }
  
  public IB_FabricJson(IB_FabricJson srcObject)
  {
    // copy constructor
    // we need this for reading files that don't exactly conform to this object
    this.cloneIB_FabricJson(srcObject);

  }
  
  public IB_FabricJson(String fileName)
  {
    if (fileName != null)
    {
      try
      {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        IB_FabricJson json = gson.fromJson(bufferedReader, IB_FabricJson.class);

        // if the json document was parsed correctly, then brute force copy
        if ((json != null) && (json instanceof IB_FabricJson))
          this.cloneIB_FabricJson(json);
        else
          System.err.println("Seems to be a problem of the instance??  NULL or not IB_FabricJson");
      }
      catch (Exception e)
      {
        e.printStackTrace();
        logger.severe(e.getCause().toString());
        logger.severe("Could NOT parse json IB_FabricJson file");
        logger.severe(e.getMessage());
      }
    }
  }
  
  public IB_FabricJson(OSM_Fabric fabric)
  {
    super();
    name = fabric.getFabricName(true);
    
    addLinkLists(fabric);
  }

  public IB_FabricJson(IB_FabricConf fabricConf)
  {
    super();
    // these three are part of the FabricNameElement
    name  = fabricConf.getFabricName();
    speed = fabricConf.getIB_FabricNameElement().getSpeed();
    width = fabricConf.getIB_FabricNameElement().getWidth();
    
    addLinkLists(fabricConf.getNodeElements());
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
    for (IB_LinkListJson node: getNodes())
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
  private void addLinkLists(ArrayList<IB_LinkListElement> nodeElements)
  {
    // used by the constructor with IB_FabricConf XML object
    
    // nodeElements get converted to LinkList objects
    if((nodeElements != null) && !nodeElements.isEmpty())
    {
      // determine the number of nodes
      ArrayList<IB_LinkListJson> nodeList = new ArrayList<IB_LinkListJson>();
      for(IB_LinkListElement lle: nodeElements)
      {
        // save this node, child objects should be created as well
        nodeList.add(new IB_LinkListJson(lle));
      }
      
      IB_LinkListJson[] newNodes = new IB_LinkListJson[nodeList.size()];
      int ndex = 0;
      for(IB_LinkListJson n: nodeList)
      {
        newNodes[ndex] = n;
        ndex++;
      }
      setNodes(newNodes);
    }
  }

  private void addLinkLists(IB_LinkListJson[] oNodes)
  {
    // used by the constructor with IB_FabricJson object
    
    // basically, just create and copy
    if((oNodes != null) && (oNodes.length > 0))
    {
      int ndex = 0;
      IB_LinkListJson[] newNodes = new IB_LinkListJson[oNodes.length];
      for(IB_LinkListJson n: oNodes)
      {
        newNodes[ndex] = new IB_LinkListJson(n, this);
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
  private void addLinkLists(OSM_Fabric fabric)
  {
    // get all the links from the fabric, and work our way down from
    // the top and add them.  Once added, don't add them again
    
    LinkedHashMap<String, OSM_Port>    allPorts     = fabric.getOSM_Ports();
    LinkedHashMap<String, IB_Link>     allLinks     = fabric.getIB_Links();    
    
    LinkedHashMap<String, OSM_Node>    allNodes     = fabric.getOSM_Nodes();
    
    Collection<OSM_Node>                 nodeValues = allNodes.values();
    ArrayList<OSM_Node>                 listOfNodes = new ArrayList<OSM_Node>(nodeValues);
    LinkedHashMap<String, OSM_Node> switchNodes     = new LinkedHashMap<String, OSM_Node>();
    LinkedHashMap<String, OSM_Node> leafSwitchNodes = new LinkedHashMap<String, OSM_Node>();
    
    // iterate through all nodes, determine which ones are switches
    for (Entry<String, OSM_Node> entry : allNodes.entrySet())
    {
      OSM_Node e = entry.getValue();
      if(e.isSwitch())
        switchNodes.put(entry.getKey(), e);
    }
    
    // iterate through all switches, determine which ones are top level switches
    for (Entry<String, OSM_Node> entry : switchNodes.entrySet())
    {
      OSM_Node e = entry.getValue();
      ArrayList<OSM_Port> pArray = e.getOSM_Ports(allPorts);
      int numRemoteSwitches = 0;
      for (OSM_Port p: pArray)
      {
        // check the node at the other end of the port to see if it is also a switch
        OSM_Node rn = p.getRemoteOSM_Node(listOfNodes);
        if(rn.isSwitch())
          numRemoteSwitches++;
      }
      
      // if this switch is connected mostly to other switches, then not a leafSwitch
      if((pArray.size() - numRemoteSwitches) > 8 )
      {
        leafSwitchNodes.put(entry.getKey(), e);
      }
    }

    for (Entry<String, OSM_Node> entry : leafSwitchNodes.entrySet())
      switchNodes.remove(entry.getKey());

    
    // now build the linkLists
//    System.err.println("\nnum nodes        : " + allNodes.size());
//    System.err.println(  "num top switches : " + switchNodes.size());
//    System.err.println(  "num leaf switches: " + leafSwitchNodes.size());
//    System.err.println(  "num ports        : " + allPorts.size());
//    System.err.println(  "num links        : " + allLinks.size());
    
    // start with links of the top level switches TLS.  TLS are switches which are NOT leaf switches
    //
    // 1. top level switches
    // 2. leaf switches
    // 3. remainder of nodes, if any
    
    // 1. top level
    ArrayList<IB_LinkListJson> arrayOfll = new ArrayList<IB_LinkListJson>();
    for (Entry<String, OSM_Node> entry : switchNodes.entrySet())
    {
      OSM_Node e = entry.getValue();
      
      // build an IB_LinkListJson object from this node and its links
      ArrayList<IB_Link> la = e.getIB_Links(allLinks);
      IB_LinkListJson llj = new IB_LinkListJson(la, fabric, e);
      arrayOfll.add(llj);
      
      // these are now accounted for, so remove them from the list of links
      for (IB_Link l : la)
        allLinks.remove(l.getIB_LinkKey());
    }
    
    // 2. leaf level
    for (Entry<String, OSM_Node> entry : leafSwitchNodes.entrySet())
    {
      OSM_Node e = entry.getValue();
      
      // build an IB_LinkListJson object from this node and its links
      ArrayList<IB_Link> la = e.getIB_Links(allLinks);
      IB_LinkListJson llj = new IB_LinkListJson(la, fabric, e);
      arrayOfll.add(llj);
      
      // these are now accounted for, so remove them from the list of links
      for (IB_Link l : la)
        allLinks.remove(l.getIB_LinkKey());
    }
    
    // 3. remaining nodes, but there shouldn't be any links left
    if(allLinks.size() > 0)
    {
      System.err.println("Shouldn't have links left");
      System.err.println(allLinks.size());
      
      // handle this somehow
    }
    
    IB_LinkListJson[] newNodes = new IB_LinkListJson[arrayOfll.size()];
    int ndex = 0;
    for(IB_LinkListJson n: arrayOfll)
    {
      newNodes[ndex] = n;
      ndex++;
    }
    setNodes(newNodes);
   }

  /************************************************************
   * 
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
    BinList <IB_LinkListJson> spbL = new BinList <IB_LinkListJson>();
    BinList <IB_LinkListJson> wdbL = new BinList <IB_LinkListJson>();
    
    for(IB_LinkListJson n: getNodes())
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
    for(IB_LinkListJson n: getNodes())
    {
      if(n == null)
        System.err.println("NULL NODES IN IB_LinkListJson ARRAY!");
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
  
  public IB_LinkListJson[] getNodes()
  {
    return nodes;
  }
  
  public Set<IB_LinkJson> getLinks()
  {
    // return all of the links in the fabric (no duplicates)
    Set<IB_LinkJson> allLinks = new HashSet<IB_LinkJson>();
    for(IB_LinkListJson n: this.getNodes())
      allLinks.addAll(Arrays.asList(n.getLinks()));
    return allLinks;
  }
  
  public String toStats()
  {
    // assume no duplicate links
    
    // get all the links (# ports = 2x links)
    // build a list of unique host names ( # nodes)
    // go through links and count how many times host names mentioned (determine sw or ca)
    
    // count the links and ports
    Set<IB_LinkJson> allLinks = getLinks();
    
    // count the host nodes
   Set<String> hostNames = new HashSet<String>();
    for(IB_LinkJson l: allLinks)
    {
      // add the name from both sides
      hostNames.add(l.getLocalPort().getName());
      hostNames.add(l.getRemotePort().getName());
    }
    
    // of those hosts, how many switches
    int numSwitches = 0;
    for(String hostName: hostNames)
    {
      int numPorts = 0;
      for(IB_LinkJson l: allLinks)
        if(l.hasHostName(hostName))
          numPorts++;
      
      if(numPorts > 1)
        numSwitches++;
    }

    StringBuffer buff = new StringBuffer();
    buff.append("fabric name: " + this.getName() + "\n");
    buff.append("speed:       " + this.getSpeed() + "\n");
    buff.append("width:       " + this.getWidth() + "\n");
    buff.append("# nodes:     " + hostNames.size() + "\n");
    buff.append("# ports:     " + allLinks.size()*2 + "\n");
    buff.append("# links:     " + allLinks.size() + "\n");
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
    
    buff.append(toLinksJsonString(pretty, concise));
    
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
    for(IB_LinkListJson node: getNodes())
      buff.append(node.toLinkString(delimiter));

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

  public void setNodes(IB_LinkListJson[] linkLists)
  {
    this.nodes = linkLists;
    
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
  public String toLinksJsonString(boolean pretty, boolean concise)
  {
    // if pretty, then name/value pair on each line
    // if concise, then only print children nvp if different than parents
    boolean initial = true;
    StringBuffer buff = new StringBuffer();
    String padding = "  ";

    // always start nodes on a new line    
    buff.append("\n" + padding + "\"nodes\": [");
    
    for (IB_LinkListJson node: getNodes())
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
    boolean valid =  ((name != null) && (getNodes() != null) && (getNodes().length > 0)) ? true: false;
    
   // and the nodes in the linklist should have unique names
    if(valid)
      valid = areLinkListNamesUnique();
   
    return valid;
  }

  public boolean areLinkListNamesUnique()
  {
    // the names in the array are the key, so if there
    // is more than one, they must be unique or else return false
    if((getNodes() == null) || (getNodes().length < 2))
      return true;

    for (int i = 0; i < getNodes().length-1; i++)
    {
       for (int j = i+1; j < getNodes().length; j++)
      {
           if (getNodes()[i].equals(getNodes()[j]))
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
       (this.compareLinkLists(o) == 0))
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
  private int compareLinkLists(IB_FabricJson o)
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
    for( IB_LinkListJson myList: getNodes())
    {
      for( IB_LinkListJson otherList: o.getNodes())
      {
        // only compare the corresponding node
        if(!otherList.getName().equals(myList.getName()))
          continue;
        
        // matching node names, so check if everything else matches
        if(!myList.equals(otherList))
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
   * Creates a string that represents the difference between the
   * fabrics ideal/configured/expected topology, and the current
   * state of the fabric.
   *
   * @see     describe related java objects
   *
   * @param currentFabric
   ***********************************************************/
  public String getDifferenceReport(IB_FabricJson otherFabric)
  {
    // before we compare, make sure all children are populated
    this.setChildSpeedAndWidth();
    otherFabric.setChildSpeedAndWidth();

    // the results are collected here, empty if all GOOD matches
    Set<IB_LinkJson> expectedButNotFound = new HashSet<IB_LinkJson>();
    Set<IB_LinkJson> foundButNotExpected = new HashSet<IB_LinkJson>();
    
    // normally empty, but if populated, contain suspected matches
    ArrayList <IB_LinkJson> shouldBeThis = new ArrayList<IB_LinkJson>();
    ArrayList <IB_LinkJson> butIsThis    = new ArrayList<IB_LinkJson>();

    // compare all the links in fabric, against all of links in other fabric
    // ( don't care how the links are organized, with respect to node lists)

    StringBuffer buff = new StringBuffer();

    buff.append("Evaluating the Fabric connectivity...\n");
    expectedButNotFound.addAll(getLinks());
    foundButNotExpected.addAll(otherFabric.getLinks());

    // loop through all of my links, and try to find a match
    for (IB_LinkJson ml : getLinks())
    {
      for (IB_LinkJson ol : otherFabric.getLinks())
      {
        if (ml.equals(ol))
        {
          // ideal case, remove from both lists
          expectedButNotFound.remove(ml);
          foundButNotExpected.remove(ol);
          continue;
        }
      }
    }
    
    // previous tests looked for exact matches, check for similarities that could
    // indicate speed or width differences (use the results from above)
    // 
    // find similar matches only if both sets are non-empty
    if(!expectedButNotFound.isEmpty() && !foundButNotExpected.isEmpty())
    {
      // maybe these match up, kinda sorta
      for(IB_LinkJson ml : expectedButNotFound)
      {
        for(IB_LinkJson ol : foundButNotExpected)
        {
          // links are supposed to have unique endpoints, so if they
          // share an endpoint, maybe they are supposed to be the same
          if (ml.hasCommonEndpoint(ol))
          {
            shouldBeThis.add(ml);
            butIsThis.add(ol);
            
            // don't remove from the found/not found list
            // this is all speculative, so leave original results alone
          }
        }
      }
    }
    
    // assume "this" is the baseline, and the "current" is the question
//    buff.append(this.toStats() + "\n");
//    buff.append(otherFabric.toStats() + "\n");

    if(expectedButNotFound.isEmpty() && foundButNotExpected.isEmpty())
    {
      // nothing unexpected found
      buff.append("\n Fabric matches expected configuration\n");
    }
    else
    {
      // show all the un-expected things, if any
      if (!expectedButNotFound.isEmpty())
      {
        buff.append("\n Expected but not found (missing or down)\n");
        for (IB_LinkJson ml : expectedButNotFound)
        {
          buff.append(ml.toJsonString(false, false, null) + "\n");
        }
      }

      if (!foundButNotExpected.isEmpty())
      {
        buff.append("\n Found but not expected (extra or new)\n");
        for (IB_LinkJson ol : foundButNotExpected)
        {
          buff.append(ol.toJsonString(false, false, null) + "\n");
        }
      }

      if(!shouldBeThis.isEmpty() && (shouldBeThis.size() == butIsThis.size()))
      {
        buff.append("\n Not exact match (downgraded or cable error)");

        for (int ndex = 0; ndex < shouldBeThis.size(); ndex++)
        {
          IB_LinkJson ml = shouldBeThis.get(ndex);
          IB_LinkJson ol = butIsThis.get(ndex);
          
          buff.append("\n  this missing link:\n   ");
          buff.append(ml.toJsonString(false, false, null) + "\n");
          buff.append("  looks similar to this extra link:\n   ");
          buff.append(ol.toJsonString(false, false, null) + "\n");
        }
      }      
    }
    return buff.toString();
  }

}
