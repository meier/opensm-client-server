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
 *        file: OMS_WhatsUpInfo.java
 *
 *  Created on: Dec 10, 2014
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import gov.llnl.lc.system.whatsup.WhatsUpInfo;

import java.io.Serializable;
import java.util.ArrayList;

/**********************************************************************
 * Describe purpose and responsibility of OMS_WhatsUpInfo
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Dec 10, 2014 4:56:27 PM
 **********************************************************************/
public class OMS_WhatsUpInfo implements Serializable
{
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = 8699354101477906318L;
  
  private WhatsUpInfo whatsUp;
  private OSM_Nodes nodes;
  private ArrayList<String> NodeNameList = new ArrayList<String>();
  
  static final String SPACE              = " ";

  /************************************************************
   * Method Name:
   *  OMS_WhatsUpInfo
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param whatsUp
   * @param nodes
   ***********************************************************/
  public OMS_WhatsUpInfo(WhatsUpInfo whatsUp, OSM_Nodes nodes)
  {
    super();
    this.whatsUp = whatsUp;
    this.nodes = nodes;
    
    // create an list of shortened nodes names
    if(nodes != null)
      for(PFM_Node n: nodes.getPerfMgrNodes())
        NodeNameList.add(getHostName(n.getNode_name()));
   }
  
  public String [] getDownNodesNotInFabric()
  {
    // return the down nodes, but trim it
    if(whatsUp.getDownNodes() == null)
      return null;
    
    ArrayList<String> nList = new ArrayList<String>();
    
    // node of these should be in the nodes list, so add them
    // directly without checking
    for(String n: whatsUp.getDownNodes())
          nList.add(n.trim());

     return nList.toArray(new String[nList.size()]);
  }
  
  public String [] getUnknownNodesNotInFabric()
  {
    // return the unknown nodes, but trim it
    if(whatsUp.getUnknownNodes() == null)
      return null;
    
    ArrayList<String> nList = new ArrayList<String>();
    
    // node of these should be in the nodes list, so add them
    // directly without checking
    for(String n: whatsUp.getUnknownNodes())
          nList.add(n.trim());

     return nList.toArray(new String[nList.size()]);
  }
  
  public String [] getUpNodesNotInFabric()
  {
    // if the number of up nodes from whatsUp exceeds the
    // number of OSM_Nodes, then return the extra node names
    int nWU = getNumUpNodes(false);
    int nNO = getNumUpNodes(true);
    
    // if they are equal in size, assume the lists are the same
    
    if(nWU > nNO)
    {
      ArrayList<String> nList = new ArrayList<String>();
      
      for(String n: whatsUp.getUpNodes())
      {
        if(!NodeNameList.contains(n.trim()))
          nList.add(n.trim());
      }
       return nList.toArray(new String[nList.size()]);
    }
    return null;
  }
  
  protected String getHostName(String hostName)
  {
    // trim whatever is provided, and return only the
    // first word.
    // typically, the name of a node is the name of
    // a host, plus extra verbage describing the interface
    if(hostName == null)
      return null;
    
    String [] names = hostName.split(SPACE);
    String n = names[0].trim();
    return n;
  }
  
  public boolean isUp(String hostName)
  {
    if((hostName == null) || (whatsUp.getUpNodes() == null))
      return false;
    
    // is this in the Up array
    for(String name: whatsUp.getUpNodes())
    {
      if(name.trim().equalsIgnoreCase(getHostName(hostName)))
        return true;
    }
    return false;
  }
  
  public boolean isDown(String hostName)
  {
    if((hostName == null) || (whatsUp.getDownNodes() == null))
      return false;
    
    // is this in the Down array
    for(String name: whatsUp.getDownNodes())
    {
      if(name.trim().equalsIgnoreCase(getHostName(hostName)))
        return true;
    }
    return false;
  }
  
  public boolean isUnknown(String hostName)
  {
    if((hostName == null) || (whatsUp.getUnknownNodes() == null))
      return false;
    
    // is this in the Unknown array
    for(String name: whatsUp.getUnknownNodes())
    {
      if(name.trim().equalsIgnoreCase(getHostName(hostName)))
        return true;
    }
    return false;
  }
  
  public void showUpNodesNotInFabric()
  {
    String [] nodes = getUpNodesNotInFabric();
    
    if(nodes == null)
      return;
    
    for(String n: nodes)
      System.out.println(n);
   }

  public void showUpNodes(boolean usingFabric)
  {
    if(usingFabric)
    {
      for(PFM_Node n: nodes.getPerfMgrNodes())
      {
        // find this node in the whats up info
        String hostName = n.getNode_name();
        if(isUp(hostName))
          System.out.println(hostName);
      }
    }
    else if( whatsUp.getUpNodes() != null)
    {
      for(String name: whatsUp.getUpNodes())
        System.out.println(name.trim());
    }
   }

  public int getNumUpNodes(boolean usingFabric)
  {
    int num = 0;
    if(usingFabric)
    {
      for(PFM_Node n: nodes.getPerfMgrNodes())
      {
        // find this node in the whats up info
        String hostName = n.getNode_name();
        if(isUp(hostName))
          num++;
      }
    }
    else
      num = whatsUp.getUpNodes() == null ? 0: whatsUp.getUpNodes().length;
    
    return num;
    }

  public int getNumUpNodesNotInFabric()
  {
    // if the number of up nodes from whatsUp exceeds the
    // number of OSM_Nodes, then return the extra number
    return getNumUpNodes(false) - getNumUpNodes(true);
  }

  public int getNumDownNodes(boolean usingFabric)
  {
    int num = 0;
    if(usingFabric)
    {
      for(PFM_Node n: nodes.getPerfMgrNodes())
      {
        // find this node in the whats up info
        String hostName = n.getNode_name();
        if(isDown(hostName))
          num++;
      }
    }
    else
      num = whatsUp.getDownNodes() == null ? 0: whatsUp.getDownNodes().length;
    
    return num;
    }

  /************************************************************
   * Method Name:
   *  getWhatsUp
   **/
  /**
   * Returns the value of whatsUp
   *
   * @return the whatsUp
   *
   ***********************************************************/
  
  public WhatsUpInfo getWhatsUp()
  {
    return whatsUp;
  }

  public int getNumUnknownNodes(boolean usingFabric)
  {
    int num = 0;
    if(usingFabric)
    {
      for(PFM_Node n: nodes.getPerfMgrNodes())
      {
        // find this node in the whats up info
        String hostName = n.getNode_name();
        if(isUnknown(hostName))
          num++;
      }
    }
    else
      num = whatsUp.getUnknownNodes() == null ? 0: whatsUp.getUnknownNodes().length;
    
    return num;
    }

  public void showDownNodes(boolean usingFabric)
  {
    if(usingFabric)
    {
      for(PFM_Node n: nodes.getPerfMgrNodes())
      {
        // find this node in the whats up info
        String hostName = n.getNode_name();
        if(isDown(hostName))
          System.out.println(hostName);
      }
    }
    else if( whatsUp.getDownNodes() != null)
    {
      for(String name: whatsUp.getDownNodes())
        System.out.println(name.trim());
    }
   }

  public void showUnknownNodes(boolean usingFabric)
  {
    if(usingFabric)
    {
      for(PFM_Node n: nodes.getPerfMgrNodes())
      {
        // find this node in the whats up info
        String hostName = n.getNode_name();
        if(isUnknown(hostName))
          System.out.println(hostName);
      }
    }
    else if( whatsUp.getUnknownNodes() != null)
    {
      for(String name: whatsUp.getUnknownNodes())
        System.out.println(name.trim());
    }
   }
  public String toInfo()
  {
    return toInfo("");
  }
  
  public String toInfo(String prePend)
  {
    StringBuffer buff = new StringBuffer();
    if((whatsUp != null) && (whatsUp.getReturnCode() != 0))
    {
      buff.append("Error: " + whatsUp.getReturnMessage() + "\n");
      return buff.toString();
    }
    
   buff.append(prePend + "# up nodes (from whatsup)     : " + getNumUpNodes(false) + "\n");
   buff.append(prePend + "# CA nodes (from fabric)      : " + getNumUpNodes(true) + "\n");
   buff.append(prePend + "# up nodes not in fabric      : " + getNumUpNodesNotInFabric() + "\n");
   if(getNumUpNodesNotInFabric() > 0)
   {
     String [] nodes = getUpNodesNotInFabric();
     if(nodes != null)
     {
       for(String n: nodes)
         buff.append(prePend + "   " + n + "\n");
     }
   }
   buff.append(prePend + "# down nodes (from whatsup)   : " + getNumDownNodes(false) + "\n");
   if(getNumDownNodes(false) > 0)
   {
     String [] nodes = getDownNodesNotInFabric();
     if(nodes != null)
     {
       for(String n: nodes)
         buff.append(prePend + "   " + n + "\n");
     }
   }
   buff.append(prePend + "# unknown nodes (from whatsup): " + getNumUnknownNodes(false) + "\n");
   if(getNumUnknownNodes(false) > 0)
   {
     String [] nodes = getUnknownNodesNotInFabric();
     if(nodes != null)
     {
       for(String n: nodes)
         buff.append(prePend + "   " + n + "\n");
     }
   }
    return buff.toString();
  }

}
