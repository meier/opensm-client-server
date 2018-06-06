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
 *        file: IB_PortElement.java
 *
 *  Created on: Nov 17, 2014
 *      Author: meier3
 ********************************************************************/

package gov.llnl.lc.infiniband.opensm.xml;

import java.io.Serializable;
import java.util.HashMap;

import org.w3c.dom.Comment;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**********************************************************************
 * Describe purpose and responsibility of IB_PortElement
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Nov 17, 2014 10:04:13 AM
 **********************************************************************/
public class IB_PortElement implements Serializable
{
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = 1710515592949753468L;

  private Node Root;
  
  /** the other side of the link **/
  private IB_RemotePortElement r_port;
  private IB_RemoteNodeElement r_node;
  
  private HashMap<String, String> AttributeMap = new HashMap<String, String>();
  private java.util.ArrayList<Comment> CommentElements = new java.util.ArrayList<Comment>();
  
  public IB_PortElement(Node root)
  {
    super();
    if(root.getNodeType() == Node.ELEMENT_NODE)
    {
      Root = root;
      // iterate through the attributes and build the map
      for(int ndex = 0; ndex < root.getAttributes().getLength(); ndex++)
      {
        Node e = root.getAttributes().item(ndex);
        AttributeMap.put(e.getNodeName(), e.getNodeValue());
      }
      
      // loop through the sub nodes for this port, fill in the other side of link
      NodeList nlist = root.getChildNodes();
      for (int c = 0; c < nlist.getLength(); c++)
      {
        Node rpEl = nlist.item(c);
        if("r_port".equalsIgnoreCase(rpEl.getNodeName()))
          r_port = new IB_RemotePortElement(rpEl);
         else if("r_node".equalsIgnoreCase(rpEl.getNodeName()))
          r_node = new IB_RemoteNodeElement(rpEl);
      }
      CommentElements = IB_PortElement.getChildComments(root);
    }
  }

  public String getElementName()
  {
    return Root.getNodeName();
  }

  public String getAttributeValue(String name)
  {
    // this is a convenience function, but named item may not exist
    // always return unspecified or null
    Node n = Root.getAttributes().getNamedItem(name);
    return (n!= null) ? n.getNodeValue(): "unspecified";
  }

  public String getNumber()
  {
    return getAttributeValue("num");
  }

  public NamedNodeMap getAttributes()
  {
    return Root.getAttributes();
  }

  public HashMap<String, String> getAttributeMap()
  {
    return AttributeMap;
  }

  protected static String getSubNodeValue(Node root) 
  {
    if(root.hasChildNodes())
    {
       NodeList list = root.getChildNodes();
      for (int i=0; i < list.getLength(); i++)
      {
          Node subnode = list.item(i);
          if (subnode.getNodeType() == Node.TEXT_NODE)
          {
            return subnode.getNodeValue();
          }
       }
    }

  return "";
  }
  
  public static String getAttributeString(Node root, boolean xml)
  {
    StringBuffer buff = new StringBuffer();
    boolean firsTime = true;
    
    for(int ndex = 0; ndex < root.getAttributes().getLength(); ndex++)
    {
      Node e = root.getAttributes().item(ndex);
      if(!firsTime)
      {
        buff.append(" ");
      }
      firsTime=false;
      buff.append(e.getNodeName() + "=\"" + e.getNodeValue() + "\"");
    }

    return buff.toString();
  }



  protected static java.util.ArrayList<Comment> getChildComments(Node root) 
  {
    java.util.ArrayList<Comment> CommentElements = new java.util.ArrayList<Comment>();
    if(root.hasChildNodes())
    {
       NodeList list = root.getChildNodes();
      for (int i=0; i < list.getLength(); i++)
      {
          Node subnode = list.item(i);
          if (subnode.getNodeType() == Node.COMMENT_NODE)
          {
            Comment comment=(Comment) subnode;
            CommentElements.add(comment);
           }
       }
    }
  return CommentElements;
  }

  protected static String getIndent(int iLevel)
  {
    // this is basically printing out the XML document, but using the Java Objects
    StringBuffer buff = new StringBuffer();
    int numSpacesPerIndent = 4;
    
    /* legal values are 0 through 6 */
    int level = iLevel < 0 ? 0: (iLevel > 6 ? 6: iLevel);
    int numSpaces = numSpacesPerIndent * level;
    
    for(int ndex = 0; ndex < numSpaces; ndex++)
      buff.append(" ");
    
    return buff.toString();
  }

  public String toXMLString(int indentLevel)
  {
    // this is basically printing out the XML document, but using the Java Objects
    StringBuffer buff = new StringBuffer();
    
    buff.append(IB_PortElement.getIndent(indentLevel));
    
    buff.append("<" + getElementName() +" ");
    buff.append(IB_PortElement.getAttributeString(Root, true));
    buff.append(">");
    buff.append(getIB_RemotePortElement().toXMLString(0));
    buff.append(getIB_RemoteNodeElement().toXMLString(0));
    buff.append("</" + getElementName() +">");
    return buff.toString();
  }

  @Override
  public String toString()
  {
    System.out.println(getCommentElements().size());

    return "IB_PortElement [r_port=" + r_port + ", r_node=" + r_node + ", AttributeMap="
        + AttributeMap + ", getElementName()=" + getElementName() + ", getNumber()=" + getNumber()
        + ", getAttributeMap()=" + getAttributeMap() + "]";
  }

  public IB_RemotePortElement getIB_RemotePortElement()
  {
    return r_port;
  }

  public IB_RemoteNodeElement getIB_RemoteNodeElement()
  {
    return r_node;
  }

  public String getWidth()
  {
    if(!getWidthAttribute().equals("unspecified"))
      return getWidthAttribute();
    Node parentSW = Root.getParentNode();
    
    Node n = parentSW.getAttributes().getNamedItem("width");
    if(n != null)
      return n.getNodeValue();
    
    Node parentFab = parentSW.getParentNode();
    
    n = parentFab.getAttributes().getNamedItem("width");
    if(n != null)
      return n.getNodeValue();
   
    return "unspecified";
  }

  public String getSpeed()
  {
    if(!getSpeedAttribute().equals("unspecified"))
      return getSpeedAttribute();
    Node parentSW = Root.getParentNode();
    
    Node n = parentSW.getAttributes().getNamedItem("speed");
    if(n != null)
      return n.getNodeValue();
    
    Node parentFab = parentSW.getParentNode();
    
    n = parentFab.getAttributes().getNamedItem("speed");
    if(n != null)
      return n.getNodeValue();
   
    return "unspecified";
  }

  public String getWidthAttribute()
  {
    return getAttributeValue("width");
  }

  public String getSpeedAttribute()
  {
    return getAttributeValue("speed");
  }


  public java.util.ArrayList<Comment> getCommentElements()
  {
    return CommentElements;
  }

  /************************************************************
   * Method Name:
   *  toPortString
  **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects
   *
   * @param name
   * @param delimiter
   * @return
   ***********************************************************/
  public Object toPortString(String name, String delimiter)
  {
    StringBuffer buff = new StringBuffer();
    buff.append(name);
    buff.append( delimiter);
    buff.append(getNumber());
    buff.append( delimiter);
    buff.append(r_port.getNumber());
    buff.append( delimiter);
    buff.append(r_node.getName());
    buff.append( delimiter);
    buff.append(getSpeed());
    buff.append( delimiter);
    buff.append(getWidth());
    return buff.toString();
  }
}
