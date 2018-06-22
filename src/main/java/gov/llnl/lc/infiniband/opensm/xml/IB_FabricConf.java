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
 *        file: IB_FabricConf.java
 *
 *  Created on: Nov 17, 2014
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.xml;


import java.io.Serializable;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import gov.llnl.lc.util.SystemConstants;

/**********************************************************************
 * An object that represents the ibfabricconf.xml file.  Its structure
 * is relatively simple, consisting of a root node IB_FabricNameElement,
 * that contains the name of the fabric, and the overall speed and width
 * of the links.
 * 
 * It is followed by a collection of IB_LinkListElement(s) which represent
 * the nodes in the fabric that have a links.  Its name is the node name,
 * and it, in turn, has a collection of IB_PortElements, which represent the 
 * various links for that node.
 * 
 * The IB_PortElement, represents a link.  One side of the link is this nodes
 * port, and port number, and the other side of the link is a different node
 * and port number.  Each IB_PortElement, therefore, has its own port number
 * plus the IB_RemotePortElement and the IB_RemoteNodeElement.
 * 
 * The IB_RemotePortElement has a port number.
 * The IB_RemoteNodeElement has a name.
 * 
 * All of these elements may or may not have additional attributes and comments.
 * <p>
 * @see  IB_FabricNameElement
 * @see  IB_LinkListElement
 * @see  IB_PortElement
 * @see  IB_RemotePortElement
 * @see  IB_RemoteNodeElement
 *
 * @author meier3
 * 
 * @version Nov 17, 2014 8:59:17 AM
 **********************************************************************/
public class IB_FabricConf implements Serializable, gov.llnl.lc.logging.CommonLogger, Comparable<IB_FabricConf>
{
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = -3674524890551108029L;
  
  private Node Root;
  private Document Doc;
  private String fileName = "unkown";
  
  private IB_FabricNameElement FabricNameElement;
  private java.util.ArrayList<IB_LinkListElement> NodeElements = new java.util.ArrayList<IB_LinkListElement>();
  private java.util.ArrayList<Comment> CommentElements = new java.util.ArrayList<Comment>();
  
  private int nLinks     = 0;
  private int nNodes     = 0;
  private int nPorts     = 0;
  private int nDownPorts = 0;

  public IB_FabricConf(String fileName)
  {
    this.fileName = fileName;
    if(fileName != null)
    {
      try
      {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(fileName);
        initFromDocument(doc);
        calcStats();
      }
       catch (Exception e)
      {
        logger.severe("Could NOT parse IB_FabricConf");
        logger.severe(e.getMessage());
      }      
    }
  }
  
  public IB_FabricConf(InputSource is)
  {
    try
    {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.parse(is);
      initFromDocument(doc);
      calcStats();
    }
     catch (Exception e)
    {
      logger.severe("Could NOT parse InputSource");
      logger.severe(e.getMessage());
    }      
  }
  
  private void initFromDocument(Document doc)
  {
    Doc = doc;
    Node root = doc.getDocumentElement();
    root.normalize();

    Root = root;
    
    // construct the object
    FabricNameElement = new IB_FabricNameElement(root);
    
    NodeList nodeLst = doc.getElementsByTagName("linklist");

    // loop through the hosts that have a link list//
    for (int s = 0; s < nodeLst.getLength(); s++)
    {
      Node fstNode = nodeLst.item(s);
      
      IB_LinkListElement lle = new IB_LinkListElement(fstNode);
      NodeElements.add(lle);
    }
     CommentElements = IB_PortElement.getChildComments(root);
  }
  
  public IB_FabricNameElement getIB_FabricNameElement()
  {
    return FabricNameElement;
  }
  
  /************************************************************
   * Method Name:
   *  getFileName
   **/
  /**
   * Returns the value of fileName
   *
   * @return the fileName
   *
   ***********************************************************/
  
  public String getFileName()
  {
    return fileName;
  }

  public static void main(String[] args)
  {
    IB_FabricConf fc = new IB_FabricConf("/home/meier3/dev/ibfabricconf.xml");
    
    System.out.println(fc.toXMLString(0));
  }

  public java.util.ArrayList<IB_LinkListElement> getNodeElements()
  {
    return NodeElements;
  }
  
  public void showComments()
  {
    // loop through the hosts that have a link list//
    for (int s = 0; s < getNodeElements().size(); s++)
    {
      IB_LinkListElement lle = getNodeElements().get(s);
      System.out.println("Node: " + lle.getName() + " has " + lle.getCommentElements().size() + " comments");
    }
  }
  
  public java.util.ArrayList<Comment> getNodeCommentElements(IB_LinkListElement lle)
  {
    return lle.getCommentElements();
  }

  public java.util.ArrayList<Comment> getCommentElements()
  {
    return CommentElements;
  }

//  public String toJsonString2(int indentLevel)
//  {
//    // this is basically printing out the XML document, but using the Java Objects
//    StringBuffer buff = new StringBuffer();
//    
//    buff.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
//    buff.append(SystemConstants.NEW_LINE);
//    buff.append(FabricNameElement.toXMLString(indentLevel));
//    
//    // get all of the Node or IB_LinkListElements
//    for(IB_LinkListElement lle: getNodeElements())
//    {
//      buff.append(SystemConstants.NEW_LINE);
//      buff.append(lle.toXMLString(indentLevel + 1));
//    }
//    buff.append(SystemConstants.NEW_LINE);
//    buff.append(FabricNameElement.toXMLString(indentLevel, true));
//    return buff.toString();
//  }
//
  public String toXMLString(int indentLevel)
  {
    // this is basically printing out the XML document, but using the Java Objects
    StringBuffer buff = new StringBuffer();
    
    buff.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
    buff.append(SystemConstants.NEW_LINE);
    buff.append(FabricNameElement.toXMLString(indentLevel));
    
    // get all of the Node or IB_LinkListElements
    for(IB_LinkListElement lle: getNodeElements())
    {
      buff.append(SystemConstants.NEW_LINE);
      buff.append(lle.toXMLString(indentLevel + 1));
    }
    buff.append(SystemConstants.NEW_LINE);
    buff.append(FabricNameElement.toXMLString(indentLevel, true));
    return buff.toString();
  }

  public String toLinkStrings(String delimiter)
  {
    // mimics the behavior of "ibparsefabricconf -d"delim""
    //
    // instead of using the ibfabricconf.xml file, uses the data structure
    // within IB_FabricConf
    StringBuffer buff = new StringBuffer();
    
    // get all of the Node or IB_LinkListElements
    for(IB_LinkListElement lle: getNodeElements())
      buff.append(lle.toLinkString(delimiter));

    return buff.toString();
  }

  @Override
  public String toString()
  {
    System.out.println(getCommentElements().size());
    showComments();

    return "IB_FabricConf [FabricNameElement=" + FabricNameElement + ", Number of Nodes=" + getNodeElements().size() +"]";
  }

  public String toContent()
  {
    StringBuffer stringValue = new StringBuffer();
    stringValue.append(IB_FabricConf.class.getSimpleName() + SystemConstants.NEW_LINE);
    
    stringValue.append(" fabric name:          " + getFabricName()  + SystemConstants.NEW_LINE);
    stringValue.append(" filename:             " + getFileName()  + SystemConstants.NEW_LINE);
    stringValue.append(SystemConstants.NEW_LINE);
    stringValue.append(toXMLString(1));
    return stringValue.toString();
  }

  public IB_LinkListElement getNodeElement(String nodeName)
  {
    // return the link list element that matches this node name
    for(IB_LinkListElement lle: NodeElements)
    {
      if(lle.getName().equalsIgnoreCase(nodeName))
        return lle;
    }
    return null;
  }

  public IB_PortElement getPortElement(String nodeName, String portNumber)
  {
    // return the port element that matches this node and port number
    IB_LinkListElement lle = getNodeElement(nodeName);
    if(lle != null)
    {
      return lle.getPortElement(portNumber);
    }
    return null;
  }

  public String getLinkDescription(String nodeName, String portNumber, boolean includeDetails)
  {
    // String suitable for describing the entire link
    //   include link width/speed info if details are desired
    IB_LinkListElement lle = getNodeElement(nodeName);
    if(lle == null)
      return null;
    return lle.getLinkDescription(portNumber, includeDetails);
  }

  public String getFabricName()
  {
    if(FabricNameElement != null)
      return FabricNameElement.getName();
    return null;
  }

  public String toInfo()
  {
    StringBuffer stringValue = new StringBuffer();
    stringValue.append(IB_FabricConf.class.getSimpleName() + "\n");
    String speed = "unspecified";
    String width = "unspecified";
    if(getIB_FabricNameElement() != null)
    {
      speed = getIB_FabricNameElement().getSpeed();
      width = getIB_FabricNameElement().getWidth();
    }
    
    stringValue.append("   fabric name:              " + getFabricName() + "\n");
    stringValue.append("   filename:                 " + getFileName() + "\n");
    stringValue.append("   speed:                    " + speed + "\n");
    stringValue.append("   width:                    " + width + "\n");
    stringValue.append("   # nodes:                  " + getNumNodes() + "\n");
    stringValue.append("   # ports:                  " + getNumPorts() + "\n");
    stringValue.append("   # down ports (suspected): " + getNumDownPorts() + "\n");
    stringValue.append("   # links:                  " + getNumLinks() + "\n");
    stringValue.append("   # switches:               " + getNodeElements().size() + "\n");
  
    return stringValue.toString();
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
  public int compareTo(IB_FabricConf conf)
  {    
    // the filename, fabric name, number of nodes, and number of comments
        //
    // both object must exist (and of the same class)
    // and should be consistent with equals
    //
    // -1 if less than
    // 0 if the same
    // 1 if greater than
    //
    if(conf == null)
            return -1;
    
    if(getIB_FabricNameElement() == null)
    {
      if(conf.getIB_FabricNameElement() == null)
        return 0;
      return 1;
    }
    
    // this is the normal case, check everything
    if(getFileName().equals(conf.getFileName()))
      if(getFabricName().equals(conf.getFabricName()))
        if(getNodeElements().size() == conf.getNodeElements().size())
          if(getCommentElements().size() == conf.getCommentElements().size())
            return 0;
    return 1;
  }

  @Override
  public boolean equals(Object obj) {
    return ((obj != null) && (obj instanceof IB_FabricConf) && (this.compareTo((IB_FabricConf)obj)==0));
  }

  private void calcStats()
  {
    // loop through the hosts that have a link list, and count up unique Nodes, links, and ports//
    
    // Nodes are unique by name
    // Ports are unique by node name and port number
    // links are unique by port1 and port2
    HashMap<String, String> nodeMap  = new HashMap<String, String>();
    HashMap<String, String> portMap  = new HashMap<String, String>();
    HashMap<String, String> dPortMap = new HashMap<String, String>();
    HashMap<String, String> linkMap  = new HashMap<String, String>();
    
    for(IB_LinkListElement n: getNodeElements())
    {
      // a nodes comments usually contain placeholder information for how port
      // should be connected.  Since it is commented out, it is deliberately
      // "downed".
      for(Comment ce: n.getCommentElements())
      {
        // if this element contains a port number, keep track of it
        String val = ce.getData().trim();
        String pnstr = "port num=\"";
        int ndex = val.indexOf(pnstr);
        if(ndex > 0)
        {
          int start = ndex + pnstr.length();
          int end = val.indexOf("\">");
          if((end > 0) && (start < end))
          {
            // create a portnumber string, and build the downport map
            String dp = n.getName().trim()+":"+val.substring(start, end);
            dPortMap.put(dp,  "value");
          }
         }
        }
      
      // save this node, and then look through its links
      nodeMap.put(n.getName().trim(), n.getElementName());
      for(IB_PortElement p: n.getPortElements())
      {
        // the port has a remote node, which should be saved
        nodeMap.put(p.getIB_RemoteNodeElement().getName().trim(), "value");
        
        // save each port, with the node name
        String lp = n.getName().trim()+":"+p.getNumber().trim();
        String rp = p.getIB_RemoteNodeElement().getName().trim()+":"+p.getIB_RemotePortElement().getNumber().trim();
        portMap.put(lp, "value");
        portMap.put(rp, "value");
        
        // avoid double counting the links, so put them in a predictable order
        String link = lp.compareTo(rp) > 0 ? lp: rp;
        linkMap.put(link, "value");
      }
    }
    
    nNodes     = nodeMap.size();
    nPorts     = portMap.size();
    nDownPorts = dPortMap.size();
    nLinks     = linkMap.size();
   }
  
  public int getNumNodes()
  {
    // TODO remove this once synced at server end
    calcStats();  // normally this happens in the constructor, but.....
    return nNodes;
  }

  public int getNumPorts()
  {
    // TODO remove this once synced at server end
    calcStats();  // normally this happens in the constructor, but.....
    return nPorts;
  }

  public int getNumDownPorts()
  {
    // TODO remove this once synced at server end
    calcStats();  // normally this happens in the constructor, but.....
    return nDownPorts;
  }

  public int getNumLinks()
  {
    // TODO remove this once synced at server end
    calcStats();  // normally this happens in the constructor, but.....
    return nLinks;
  }
  
  public String getSpeed()
  {
    Node n = Root.getAttributes().getNamedItem("speed");
    if(n != null)
      return n.getNodeValue();
    return "";
  }

  public String getWidth()
  {
    Node n = Root.getAttributes().getNamedItem("width");
    if(n != null)
      return n.getNodeValue();
    return "";
  }

  /************************************************************
   * Method Name:
   *  getRoot
   **/
  /**
   * Returns the value of root
   *
   * @return the root
   *
   ***********************************************************/
  
  public Node getRoot()
  {
    return Root;
  }

  /************************************************************
   * Method Name:
   *  getDoc
   **/
  /**
   * Returns the value of doc
   *
   * @return the doc
   *
   ***********************************************************/
  
  public Document getDoc()
  {
    return Doc;
  }

}
