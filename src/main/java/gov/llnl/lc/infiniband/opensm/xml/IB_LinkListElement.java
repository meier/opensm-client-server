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
 *        file: IB_LinkListElement.java
 *
 *  Created on: Nov 17, 2014
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.xml;

import gov.llnl.lc.util.SystemConstants;

import java.io.Serializable;
import java.util.HashMap;

import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**********************************************************************
 * Describe purpose and responsibility of IB_LinkListElement
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Nov 17, 2014 9:39:35 AM
 **********************************************************************/
public class IB_LinkListElement implements Serializable
{
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = -4178254380729555145L;

  private Node Root;
  
  private HashMap<String, String> AttributeMap = new HashMap<String, String>();
  private java.util.ArrayList<IB_PortElement> PortElements = new java.util.ArrayList<IB_PortElement>();
  private java.util.ArrayList<Comment> CommentElements = new java.util.ArrayList<Comment>();
  
  public IB_LinkListElement(Node root)
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
      
      // loop through the ports for this host/node
      NodeList portList = ((Element) root).getElementsByTagName("port");
     for(int pn = 0; pn < portList.getLength(); pn++)
     {
       Element fstNmElmnt = (Element) portList.item(pn);
       
       IB_PortElement ppe = new IB_PortElement(fstNmElmnt);
       PortElements.add(ppe);
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

  public String getName()
  {
    return getAttributeValue("name");
  }

  public NamedNodeMap getAttributes()
  {
    return Root.getAttributes();
  }

  public HashMap<String, String> getAttributeMap()
  {
    return AttributeMap;
  }

  public String toXMLString(int indentLevel)
  {
    // this is basically printing out the XML document, but using the Java Objects
    StringBuffer buff = new StringBuffer();
    
    buff.append(IB_PortElement.getIndent(indentLevel));
    
    buff.append("<" + getElementName() +" ");
    buff.append(IB_PortElement.getAttributeString(Root, true));
    buff.append(">");

    // get all of the Node or IB_PortElements
    for(IB_PortElement pe: getPortElements())
    {
      buff.append(SystemConstants.NEW_LINE);
      buff.append(pe.toXMLString(indentLevel + 1));
    }
    buff.append(SystemConstants.NEW_LINE);
    buff.append(IB_PortElement.getIndent(indentLevel));
    
    buff.append("</" + getElementName() +">");
     return buff.toString();
  }

  @Override
  public String toString()
  {
    System.out.println("there are " + getCommentElements().size() + " comments");
    return "IB_LinkListElement [getElementName()=" + getElementName() + ", getName()=" + getName()
        + ", getAttributeMap()=" + getAttributeMap() + ", Number of Ports=" + getPortElements().size() + "]";
  }

  public java.util.ArrayList<IB_PortElement> getPortElements()
  {
    return PortElements;
  }

  public java.util.ArrayList<Comment> getCommentElements()
  {
    return CommentElements;
  }

  public IB_PortElement getPortElement(String portNumber)
  {
    // return the port element that matches this port number
    for(IB_PortElement pe: PortElements)
    {
      if(pe.getNumber().equalsIgnoreCase(portNumber))
        return pe;
    }
    return null;
  }
  
  public String getLinkDescription(String portNumber, boolean includeDetails)
  {
    // String suitable for describing the entire link
    //   include link width/speed info if details are desired
    
    IB_PortElement pe = getPortElement(portNumber);
    if(pe == null)
      return null;
    
    StringBuffer buff = new StringBuffer();
    buff.append("\"" + getName() + "\" " );
    buff.append("p: " + portNumber + " <==> ");
    buff.append("p: " + pe.getIB_RemotePortElement().getNumber() + " ");
    buff.append("\"" + pe.getIB_RemoteNodeElement().getName() + "\" " );
    return buff.toString();
  }

}
