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
 *        file: PFM_NodePortStatus.java
 *
 *  Created on: Jan 3, 2012
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import gov.llnl.lc.infiniband.opensm.plugin.data.PFM_Port.PortCounterName;
import gov.llnl.lc.logging.CommonLogger;
import gov.llnl.lc.util.BinList;

import java.util.ArrayList;
import java.util.Arrays;

/**********************************************************************
 * An <code>PFM_NodePortStatus</code> represents the node & port status
 * for a collection of nodes types (usually switch, router, channel adapter).
 * <p>
 * Given a set of Nodes and Ports from the Perf Manager, this class provides
 * convenience functions to analyze the data.
 * <p>
  *
 * @author meier3
 * 
 * @version Jan 3, 2012 2:52:37 PM
 **********************************************************************/
public class PFM_NodePortStatus implements CommonLogger
{  
  /**  the total number of nodes **/
  public long      total_nodes = 0;

  /**  the total number of ports **/
  public long      total_ports = 0;
  
  /** the port_counters "counter", this is a count of non-zero values in each port_counter for the nodes **/
  public long [] port_counters = new long[PortCounterName.PFM_ALL_COUNTERS.size()];

  /** the information in this object refers to a collection of nodes of this type 
   * Switch, Router, Channel Adapter, etc **/
  public String    NodeType  = "UNKNOWN";

  public static PFM_NodePortStatus getSwitchNPS(ArrayList <PFM_Node> nodes, ArrayList <PFM_Port> ports, boolean esp0)
  {
    // given all the nodes and ports, return the NodePortStatus for just the switches
    // NOTE:  always returns a valid PFM_NodePortStatus object
    
    // organize the ports by guid
    BinList <PFM_Port> pbL = new BinList <PFM_Port>();
    for(PFM_Port p: ports)
    {
      pbL.add(p, p.getNodeGuid().toColonString());
    }
    
    ArrayList <PFM_Node> sn = new ArrayList<PFM_Node>();
    ArrayList <PFM_Port> sp = new ArrayList<PFM_Port>();
        
    // now I have two sets of node lists, keep only the switch nodes
    for(PFM_Node n: nodes)
    {
      /* is this a switch? */
      if(n.num_ports > 2)
      {
        /* only get the esp0 type we asked for */
        if(n.isEsp0() == esp0)
        {
          // I want this node, so add it to an ArrayList, and find its matching set of ports
          sn.add(n);
          ArrayList <PFM_Port> mnp = pbL.getBin(n.getNodeGuid().toColonString());
          if((mnp != null) && (!mnp.isEmpty()))
            sp.addAll(mnp);
        }
      }
    }
    return new PFM_NodePortStatus(sn, sp, esp0);
  }

  public static PFM_NodePortStatus getChannelAdapterNPS(ArrayList <PFM_Node> nodes, ArrayList <PFM_Port> ports)
  {
    // given all the nodes and ports, return the NodePortStatus for just the ChannelAdapters
    // organize the ports by guid
    BinList <PFM_Port> pbL = new BinList <PFM_Port>();
    for(PFM_Port p: ports)
    {
      pbL.add(p, p.getNodeGuid().toColonString());
    }
    
    ArrayList <PFM_Node> sn = new ArrayList<PFM_Node>();
    ArrayList <PFM_Port> sp = new ArrayList<PFM_Port>();
        
    // now I have two sets of node lists, keep only the CA nodes
    for(PFM_Node n: nodes)
    {
      /* is this a CA? */
      if(n.num_ports < 3)
      {
          // I want this node, so add it to an ArrayList, and find its matching set of ports
        sn.add(n);
        ArrayList <PFM_Port> mnp = pbL.getBin(n.getNodeGuid().toColonString());
        if((mnp != null) && (!mnp.isEmpty()))
          sp.addAll(mnp);
      }    
    }
    return new PFM_NodePortStatus(sn, sp, false);
  }

  public PFM_NodePortStatus(PFM_Node[] nodes, PFM_Port[] ports, boolean esp0)
  {
    this(new ArrayList<PFM_Node>(Arrays.asList(nodes)), new ArrayList<PFM_Port>(Arrays.asList(ports)), esp0);
  }

  public PFM_NodePortStatus(ArrayList <PFM_Node> nodes, ArrayList <PFM_Port> ports, boolean esp0)
  {
    if((nodes != null) && (ports != null))
    {
      if((nodes.size() == 0) || (ports.size() == 0))
      {
        logger.severe("PFM_NodePortStatus cannot be determined without nodes and ports: num nodes (" + nodes.size() + "), num ports (" + ports.size() + ")");
        return;
      }

      // calculate the total nodes and ports from each supplied argument, and check for consistency
      long n_tn = nodes.size();
      long n_tp = numPorts(nodes);
      
      // all nodes have a #1 port
      long p_tn = getPortNum(ports, (short)1).size();
      long p_tp = ports.size();
      
      // continue only if these agree
//      if((n_tn == p_tn) && (n_tp == p_tp))
        if(n_tp == p_tp)
      {
        total_nodes = n_tn;
        total_ports = p_tp;
        
        // now analyze the port counters
        BinList <PFM_Port> ebL = new BinList <PFM_Port>();
        if((ports != null) && ports.size() > 0)
        {
          // copy it to the list
          for (PFM_Port pr : ports)
          {
            // iterate through this ports counters, and add it to the
            // appropriate bins (make sure I create empty bins!!)
            for (PortCounterName counter : PortCounterName.PFM_ALL_COUNTERS)
            {
              if (pr.port_counters[counter.ordinal()] != 0L)
                ebL.add(pr, counter.name());
              else
                ebL.newBin(counter.name());
            }
          }
          
          // all done, so copy this list to the counter array
          java.util.ArrayList <Long> ccSizes = ebL.getBinSizes();
//          port_counters = new long[ccSizes.size()];
          
          int nn = 0;
          for(Long v: ccSizes)
            port_counters[nn++] = v.longValue();
        }
      }
      else
      {
        logger.severe("Num Nodes from Nodes: " + n_tn);
        logger.severe("Num Nodes from Ports: " + p_tn);
        logger.severe("Num Ports from Nodes: " + n_tp);
        logger.severe("Num Ports from Ports: " + p_tp);
      }
    }
    else
      logger.severe("Crap, the node list and port lists are null");      

  }
  
  private static int numPorts(ArrayList <PFM_Node> nodes)
  {
    int total = 0;
    // add up all the ports
    if((nodes != null) && nodes.size() > 0)
    {
      // count
      for(PFM_Node n: nodes)
      {
        total += n.num_ports;
      }
    }
    return total;
  }

  private static ArrayList <PFM_Port> getPortNum(ArrayList <PFM_Port> ports, short num)
  {
    // return a list of Ports that all have the same port number
    java.util.ArrayList<PFM_Port> rtnPorts = new java.util.ArrayList<PFM_Port>();
    if((ports != null) && (num >= 0) && ports.size() > 0)
    {
      // copy it to the list
      for(PFM_Port pr: ports)
        if(pr.port_num == num)
          rtnPorts.add(pr);
    }
    return rtnPorts;
  }

  /************************************************************
   * Method Name:
   *  toString
  **/
  /**
   * Describe the method here
   *
   * @see java.lang.Object#toString()
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @return
   ***********************************************************/
  
  @Override
  public String toString()
  {
    return "PFM_NodePortStatus [total_nodes=" + total_nodes + ", total_ports=" + total_ports
        + ", port_counters=" + Arrays.toString(port_counters) + ", NodeType=" + NodeType + "]";
  }
}
