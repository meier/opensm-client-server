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
 *        file: IB_RemotePortElement.java
 *
 *  Created on: Nov 17, 2014
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.xml;

import java.io.Serializable;
import java.util.HashMap;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**********************************************************************
 * Describe purpose and responsibility of IB_RemotePortElement
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Nov 17, 2014 10:14:53 AM
 **********************************************************************/
public class IB_RemotePortElement implements Serializable
{
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = -1678883922807359367L;

  private Node Root;
  
  private HashMap<String, String> AttributeMap = new HashMap<String, String>();
  
  public IB_RemotePortElement(Node root)
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
    }
  }

  public String getElementName()
  {
    return Root.getNodeName();
  }

  public String getNumber()
  {
    return IB_PortElement.getSubNodeValue(Root);
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
    StringBuffer buff = new StringBuffer();
    
    buff.append(IB_PortElement.getIndent(indentLevel));
    
    buff.append("<" + getElementName() +">");
    buff.append(getNumber());
    buff.append("</" + getElementName() +">");
    return buff.toString();
  }

  @Override
  public String toString()
  {
    return "IB_RemotePortElement [getElementName()=" + getElementName() + ", getNumber()=" + getNumber()
        + ", getAttributeMap()=" + getAttributeMap() + "]";
  }
}
