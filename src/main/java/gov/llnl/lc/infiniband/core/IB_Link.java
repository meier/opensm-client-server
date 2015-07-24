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
 *        file: IB_Link.java
 *
 *  Created on: Jan 11, 2012
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.core;

import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_LinkRate;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_LinkSpeed;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_LinkState;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_LinkSubState;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_LinkWidth;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Port;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_PortState;
import gov.llnl.lc.infiniband.opensm.plugin.data.SBN_Port;
import gov.llnl.lc.logging.CommonLogger;
import gov.llnl.lc.util.BinList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**********************************************************************
 * An IB_Link describes the relationship between two OSM_Ports.  Technically,
 * a link doesn't exist unless both ports exist and are active, but the notion
 * of an unterminated or inactive link is allowed.
 * 
 * <p>
 * This class is full of convenience functions performed between the implied relationship
 * between the two endpoints (OSM_Port). 
 * <p>
 * Take caution trusting information from a link who's state is DOWN
 * <p>
 * Generally, links are created from the information contained in nodes and ports.  The
 * code snippet below is an example of how it might be done.
 * <pre>
      OsmClientApi clientInterface = ParentSession.getClientApi();

      AllNodes = clientInterface.getOsmNodes();
      AllPorts = clientInterface.getOsmPorts();
      if((AllNodes != null) && (AllPorts != null))
      {
        ArrayList <IB_Link> ibla = null;
        if (AllPorts != null)
        {
          ibla = AllPorts.createIB_Links(AllNodes);
          // do what you want with the ib link array
        }
      }
 * </pre>
 * <p>
 * The list of links created in this way will not be in any particular order, but should
 * represent a valid list.  A valid list does not contain duplicate links, and all endpoints,
 * or ports, show up exactly once in the list.  Finally, endpoint1 should be "less than"
 * endpoint2 in each link.  Since links, like ports, are comparable, the list can be sorted
 * in the usual manner.
 * 
 * @see     #createIB_Links(ArrayList, boolean)
 * @see     OSM_LinkState
 *
 * @author meier3
 * 
 * @version Jan 11, 2012 8:32:30 AM
 **********************************************************************/
public class IB_Link  implements Serializable, CommonLogger, Comparable<IB_Link>
{
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = -4013751730075688968L;

  /**  Endpoint1 describes one end of the link **/
  public OSM_Port Endpoint1;

  /**  Endpoint2 describes the other end of the link **/
  public OSM_Port Endpoint2;
  
  /************************************************************
   * Method Name:
   *  IB_Link
  **/
  /**
   * Defines a relationship between two ports, and declares them "connected".
   * This constructor does NOT enforce the correct or expected behavior, it simply
   * creates the IB_Link object.  Normally, links should only be created if and
   * only if both endpoints think they are connected to each other.
   *
   * @see     #createIB_Links(ArrayList, boolean)
   *
   * @param endpoint1 an OSM_Port object that represents one end of the link
   * @param endpoint2 an OSM_Port object that represents the other end of the link
   ***********************************************************/
  public IB_Link(OSM_Port endpoint1, OSM_Port endpoint2)
  {
    super();
    Endpoint1 = endpoint1;
    Endpoint2 = endpoint2;
  }


  /************************************************************
   * Method Name:
   *  getEndpoint1
   **/
  /**
   * Returns the value of endpoint1
   *
   * @return the endpoint1
   *
   ***********************************************************/
  
  public OSM_Port getEndpoint1()
  {
    return Endpoint1;
  }


  /************************************************************
   * Method Name:
   *  getEndpoint2
   **/
  /**
   * Returns the value of endpoint2
   *
   * @return the endpoint2
   *
   ***********************************************************/
  
  public OSM_Port getEndpoint2()
  {
    return Endpoint2;
  }


  /************************************************************
   * Method Name:
   *  getLinkType
   **/
  /**
   * Determines the type of link.
   *
   * @return the link type
   *
   ***********************************************************/
  
  public synchronized IB_LinkType getLinkType()
  {
    return IB_LinkType.get(this);
  }

  /************************************************************
   * Method Name:
   *  getSpeed
   **/
  /**
   * Returns the value of links speed, which is just the lowest
   * speed of the endpoints.
   *
   * @return the speed
   *
   ***********************************************************/
  
  public OSM_LinkSpeed getSpeed()
  {
 // current speed is the lowest speed of the two endpoints
//    return "E1 (" + Endpoint1.getSpeedString() + "), E2 (" + Endpoint2.getSpeedString() + ")";
    if((Endpoint1 == null) || (Endpoint2 == null) || (Endpoint1.getSpeed() == null) || (Endpoint2.getSpeed() == null))
      return OSM_LinkSpeed.UNKNOWN;
    
    // compare the speed numbers
    if(Endpoint1.getSpeed().getSpeed() > Endpoint2.getSpeed().getSpeed())
      return Endpoint2.getSpeed();
    
//    return ((Endpoint1.getSpeed()).compareTo((Endpoint2.getSpeed())) > 0 ? Endpoint1.getSpeed(): Endpoint2.getSpeed());
    return Endpoint1.getSpeed();

  }


  /************************************************************
   * Method Name:
   *  getWidth
   **/
  /**
   * Returns the value of the links width, which is just the smallest
   * width of the two endpoints.
   *
   * @return the width
   *
   ***********************************************************/
  
  public OSM_LinkWidth getWidth()
  {
 // current size is the smallest size of the two endpoints
//    return "E1 (" + Endpoint1.getWidthString() + "), E2 (" + Endpoint2.getWidthString() + ")";
    if((Endpoint1 == null) || (Endpoint2 == null))
      return OSM_LinkWidth.UNKNOWN;
    
    return (Endpoint1.getWidth().compareTo(Endpoint2.getWidth()) > 0 ? Endpoint2.getWidth(): Endpoint1.getWidth());
  }


  /************************************************************
   * Method Name:
   *  getRate
   **/
  /**
   * Returns the value of the links rate, which is derived from
   * the links speed and width.
   *
   * @return the rate
   *
   ***********************************************************/
  
  public OSM_LinkRate getRate()
  {
 // current rate is derived from the links width and speed
    return OSM_LinkRate.get(this);
  }


  /************************************************************
   * Method Name:
   *  getState
   **/
  /**
   * Returns the links state
   *
   * @return the state
   *
   ***********************************************************/
  
  public OSM_LinkState getState()
  {
   // usually "Active / LinkUp" or "Down / Polling"
    return OSM_LinkState.get(this);
  }

  /************************************************************
   * Method Name:
   *  getSubstate
   **/
  /**
   * Returns the links sub-state
   *
   * @return the sub-state
   *
   ***********************************************************/
  
  public OSM_LinkSubState getSubstate()
  {
   // usually "Active / LinkUp" or "Down / Polling"
    return OSM_LinkSubState.get(this);
  }

  /************************************************************
   * Method Name:
   *  getRemoteEndpoint
   **/
  /**
   * Returns the opposite end of the link.  This link has two
   * endpoints, and if given one endpoint, the opposite endpoint
   * will be returned.
   *
   * @return the port at the opposite end of the link
   *
   ***********************************************************/
  
  public OSM_Port getRemoteEndpoint(OSM_Port port)
  {
    OSM_Port remote = null;
    if((port != null) && (this.contains(port)))
      remote = port.equals(this.Endpoint1) ? this.Endpoint2: this.Endpoint1;
    return remote;
  }

  /************************************************************
   * Method Name:
   *  contains
   **/
  /**
   * Returns true if either of the endpoints of this link matches
   * the provided port
   *
   * @return true if port is either end of the link
   *
   ***********************************************************/
  
  public boolean contains(OSM_Port port)
  {
    boolean rtnval = false;
    
    if(port != null)
      rtnval = ((port.compareTo(Endpoint1) == 0) || (port.compareTo(Endpoint2) == 0));
    return rtnval;
  }

  public boolean contains(IB_Guid guid, int portNumber)
  {
    if(guid != null)
    {
      if(Endpoint1.getNodeGuid().equals(guid) && Endpoint1.getPortNumber() == portNumber)
        return true;
      if(Endpoint2.getNodeGuid().equals(guid) && Endpoint2.getPortNumber() == portNumber)
        return true;
    }
    return false;
  }

  /************************************************************
   * Method Name:
   *  isActive
   **/
  /**
   * Returns true if both of the endpoints are active, which indicates
   * that this link is active.
   *
   * @return true if active, otherwise its down
   *
   ***********************************************************/
  
  public boolean isActive()
  {
    return getState() == OSM_LinkState.ACTIVE;
  }

  /************************************************************
   * Method Name:
   *  hasErrors
   **/
  /**
   * Returns true if either endpoint has errors
   *
   * @return true if either endpoint has errors
   *
   ***********************************************************/
  
  public boolean hasErrors()
  {
    // a link has an error, if either of its endpoints has an error
    if((Endpoint1 != null) && (Endpoint2 != null) && (Endpoint1.hasError() || Endpoint2.hasError()))
      return true;
    return false;
  }


  /************************************************************
   * Method Name:
   *  hasTraffic
   **/
  /**
   * Returns true if both endpoints have traffic
   *
   * @return true if both endpoints have traffic
   *
   ***********************************************************/
  
  public boolean hasTraffic()
  {
    // a link has traffic only if both endpoints have traffic
    if((Endpoint1 != null) && (Endpoint2 != null) && (Endpoint1.hasTraffic() && Endpoint2.hasTraffic()))
      return true;
    return false;
  }


  /************************************************************
   * Method Name:
   *  compareTo
  **/
  /**
   * Links are unique.  A link represents the relationship between
     two ports, so if two ports do not exist, then
     it is really not a valid "link"
<p>    
     Two Links can be considered the same if they
     have the same number of ports, and the ports
     match.  It does NOT have to be a valid link
     to be comparable.
<p>        
     Also, the speeds should match
     and should be consistent with equals
<pre>    
    
     -1 if less than (second null)
     0 if the same  (same # ports and they match)
     1 if greater than (first null)
</pre>    
     This method allows links to be sorted.
   *
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   * @param   the link to compare with this link
   *
   * @return  zero if they are the same link
   ***********************************************************/
  
  @Override
  public int compareTo(IB_Link link)
  {
    // a Link is unique
    // 
    // a link represents the relationship between
    // two ports, so if two ports do not exist, then
    // it is really not a valid "link"
    //
    // Two Links can be considered the same if they
    // have the same number of ports, and the ports
    // match.  It does NOT have to be a valid link
    // to be comparable.
    //
    // Also, the speeds should match
    // and should be consistent with equals
    //
    // -1 if less than (second null)
    // 0 if the same  (same, # ports and they match)
    // 1 if greater than (first null)
    //
    
    if(link == null)
        return -1;
    
    int nports = this.Endpoint1 == null ? 0: 1;
    nports += this.Endpoint2 == null ? 0: 1;
    int rports = link.Endpoint1 == null ? 0: 1;
    rports += link.Endpoint2 == null ? 0: 1;
    
    // if the two links don't have the same number of ports
    // then we can quit early, can't really compare
    if(nports != rports)
    {
      return nports > rports ? 1: -1;
    }
    
    // if here, we have the same number of ports
    if(nports == 0)
      return 0;
    
    if(nports == 1)
    {
      // compare the two (one on each link) ports
      OSM_Port el = this.Endpoint1 == null ? this.Endpoint2: this.Endpoint1;
      OSM_Port er = link.Endpoint1 == null ? link.Endpoint2: link.Endpoint1;
      return el.compareTo(er);
    }
    
    // both ports are comparable (nports ==2)
    if(((this.Endpoint1.compareTo(link.Endpoint1)==0) && (this.Endpoint2.compareTo(link.Endpoint2)==0)) ||
        (((this.Endpoint1.compareTo(link.Endpoint2))==0) && ((this.Endpoint2.compareTo(link.Endpoint1))==0)))
        return 0;
        
    return 1;
  }

  /************************************************************
   * Method Name:
   *  equals
  **/
  /**
   * Compares two links to determin equality.
   *
   * @see java.lang.Object#equals(java.lang.Object)
   * @see #compareTo(IB_Link)
  
   * @param obj
   * @return true if the links are the same
   ***********************************************************/
  @Override
  public boolean equals(Object obj)
  {
    return ((obj != null) && (obj instanceof IB_Link) && (this.compareTo((IB_Link)obj)==0));
  }

  public String getStateString()
  {
    return this.getState().getStateName() + "/ " + this.getSubstate().getSubstateName();
  }
    
  public String getRateString()
  {
    return this.getWidth().getWidthName() + " " + this.getSpeed().getSpeedName() + " = " + this.getRate().getRateName();
  }
    
  /************************************************************
   * Method Name:
   *  toContent
  **/
  /**
   * A pretty string for this link
   *
   * @see java.lang.Object#toString()
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @return
   ***********************************************************/
  
  public String toContent(boolean reverse)
  {
    OSM_Port E1 = reverse ? Endpoint2: Endpoint1;
    OSM_Port E2 = reverse ? Endpoint1: Endpoint2;
    return E1.toContent() + "  <->  " + E2.toContent();
  }

  public String toLinkInfo()
  {
    // should be in form;
    // 4X 10.0 Gbps Active/  LinkUp
    //                 Down/ Polling
    //
    
    
    return getRateString() + " " + getStateString();
  }

  public String toContent()
  {
    return toContent(false);
  }

  public String getIB_LinkKey()
  {
    return IB_Link.getIB_LinkKey(this);
  }
  
  public static String getIB_LinkKey(IB_Link l)
  {
    // use the endpoint 1
    if((l == null) || (l.Endpoint1 == null))
      return null;
    
    return l.Endpoint1.getOSM_PortKey();
  }
  
  public static String getIB_LinkKey(OSM_Port p)
  {
    // just use the port key, assume its Enpoint 1
    return p.getOSM_PortKey();
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
    return "IB_Link [Endpoint1=" + Endpoint1.toContent() + ", Endpoint2=" + Endpoint2.toContent() + "]";
  }


  public static ArrayList <IB_Link> createSuspectedIB_Links(ArrayList <OSM_Port> allPorts)
  {
  /* given a list of OSM_Ports, create a list of IB_Links that do NOT exist
   * 
   * 1. create a list of good links
   * 2. from that list, create a list of connected ports
   * 3. create a list of un-connected ports
   * 4. group all the connected ports by guid (the switch)
   * 5. group all the un-connected ports by guid
   * 6. find matching guids in both lists, and guess which other port might be connected.
   * 7. create a fake link, with the DOWN state
    
  */
  if((allPorts != null) && (allPorts.size() > 1))
  {
    // the good links
    java.util.ArrayList<OSM_Port> linkedPorts     = new java.util.ArrayList<OSM_Port>();
    java.util.ArrayList<OSM_Port> unLinkedPorts   = new java.util.ArrayList<OSM_Port>();
    java.util.ArrayList<IB_Link> goodLinks        = IB_Link.createIB_Links(allPorts, true);
    
    // the linked ports
    for(IB_Link gl: goodLinks)
    {
      linkedPorts.add(gl.getEndpoint1());
      linkedPorts.add(gl.getEndpoint2());
    }
    
    // the linked ports, sorted by guid
    BinList <OSM_Port> lPortBins = new BinList <OSM_Port>();
    for(OSM_Port lp: linkedPorts)
    {
      lPortBins.add(lp, Long.toString(lp.sbnPort.port_guid));
    }
    
    // the unlinked ports
    for(OSM_Port p: allPorts)
    {
      if(!linkedPorts.contains(p))
      {
        unLinkedPorts.add(p);
      }
    }
    
    // the unlinked ports, sorted by guid
    BinList <OSM_Port> uPortBins = new BinList <OSM_Port>();
    for(OSM_Port up: unLinkedPorts)
    {
      if(up.sbnPort != null)
        uPortBins.add(up, Long.toString(up.sbnPort.port_guid));
    }
    
    // try to find matching port_guids in each bin, print out
    java.util.Set <String> lKeys = lPortBins.getKeys();
    java.util.Set <String> uKeys = uPortBins.getKeys();
    
    if((lKeys == null) || (lKeys.size() == 0) || (uKeys == null) || (uKeys.size() == 0))
      return null;
    
    for(String lguid: lKeys)
    {
      if(uKeys.contains(lguid))
      {
        // sort these by port number
        ArrayList <OSM_Port> pal = lPortBins.getBin(lguid);
        Collections.sort(pal);
        boolean reverse = false;
        
        // print out both lists, deal with it later
        System.err.println("The ports with links are:");
        for(OSM_Port pl: pal)
        {
          // find the other side
          IB_Link link = IB_Link.getIB_Link(goodLinks, pl);
          reverse = pl.compareTo(link.getEndpoint1()) != 0;
          
          System.err.println(" Linked  -  " + link.toContent(reverse));
        }
        System.err.println("");
        
        ArrayList <OSM_Port> upal = uPortBins.getBin(lguid);
        Collections.sort(upal);
        
        System.err.println("The ports without links are:");
        for(OSM_Port pu: upal)
        {
          System.err.println(" Unlinked -  " + pu.toContent());
        }
        System.err.println("");
      }
    }
  }
  return null;    
  }

  /************************************************************
   * Method Name:
   *  createIB_Links
  **/
  /**
   * This is the primary way to get IB_Link objects.
   * Since an IB_Link object is a relationship between two ports, the
   * links can be established by examining all the information about
   * ports, so that mutual port relationships can be discovered.
   * 
   * Each port has a notion of being connected to a "remote" port.  If
   * that "remote" port thinks its connected to the "local" port, then a
   * link exists.  Links have state, rate and other attributes that are
   * mutually shared by both connected ports.
   *
   * @see     SBN_Port
  
   * @param   allPorts    - a list of all the ports to discover links for
   * @param   requireBoth - normally true (need both SBN_Port and PFM_Port elements
   *                        to be included in the discovery, otherwise only SBN_Port
   *                        information is considered.
   *
   * @return  an array of links containing the provided ports
   ***********************************************************/
  public static ArrayList <IB_Link> createIB_Links(ArrayList <OSM_Port> allPorts, boolean requireBoth)
  {
  // given a list of OSM_Ports, create a list of IB_Links
  //
  if((allPorts != null) && (allPorts.size() > 1))
  {
    // must have at least two ports to form a link
    java.util.ArrayList<IB_Link> links   = new java.util.ArrayList<IB_Link>();
    java.util.ArrayList<IB_Link> rtnlnks = new java.util.ArrayList<IB_Link>();
    BinList <IB_Link> aLinkBins = new BinList <IB_Link>();
    
    // link information is only contained in the SBN_Port, so this must exist
    // the remote, or linked, guid and port
    short lpn = 0;
    long lg = 0;
    short p1 = 0;
    long g1 = 0;
    // the local guid and port
    short pn = 0;
    long ng = 0;
    short p2 = 0;
    long g2 = 0;

    IB_Link link = null;
    
    
    // iterate through all the ports, but don't double count
    for(int i=0; i < allPorts.size(); i++)
    {
      OSM_Port p = allPorts.get(i);
      if((p.getSbnPort() != null) && ((p.getPfmPort() != null)  || !requireBoth))
      {
      lg = p.getSbnPort().linked_node_guid;
      lpn = p.getSbnPort().linked_port_num;
      g1 = p.getSbnPort().node_guid;
      p1 = p.getSbnPort().port_num;
      
      for(int j=i+1; j < allPorts.size(); j++)
      {
        OSM_Port rp = allPorts.get(j);
        if(rp.getSbnPort() != null)
        {
          g2 = rp.getSbnPort().linked_node_guid;
          p2 = rp.getSbnPort().linked_port_num;
          ng = rp.getSbnPort().node_guid;
          pn = rp.getSbnPort().port_num;
          
          // a link occurs if both ports think they are connected to each other
          boolean localToRemote = (ng == lg) && (pn == lpn);
          boolean remoteToLocal = (g1 == g2) && (p2 == p1);
          if(localToRemote && remoteToLocal)
          {
            // found two ports connected together, so create a link a link if all active
            if((p.getState() == OSM_PortState.ACTIVE) && (rp.getState() == OSM_PortState.ACTIVE))
            {
              // always order the endpoints based on the guid & port_nums
              // because this will help me find duplicates
              if((lg+lpn) > (p.getSbnPort().node_guid + p.getSbnPort().port_num))
                link = new IB_Link(p, rp);
              else
                link = new IB_Link(rp, p);
              links.add(link);
              // bin these links up, so duplicates will fall in the same bin
              aLinkBins.add(link, Long.toString(link.getEndpoint1().sbnPort.node_guid) + Long.toString(link.getEndpoint1().sbnPort.port_num));
              break;
            }
          }
        }
      }
     }
    }
    // all done, but there are probably duplicate links, in the array list
    // so create a new array list from the binlist
    
    // return the first ACTIVE link in each bin, thereby ignoring duplicates
    for(ArrayList <IB_Link> la: aLinkBins)
    {
      for(IB_Link l: la)
      {
        if(l.getState()==OSM_LinkState.ACTIVE)
        {
          rtnlnks.add(l);
          break;
        }
      }
    }
    
    // now we have a list of links, bin'd by endpoint1, do a sanity check on this list (a port should only show up once... EVER!)
    BinList <OSM_Port> portBins = new BinList <OSM_Port>();
    for(IB_Link rl: rtnlnks)
    {
      // add each endpoint to the bin of ports
      portBins.add(rl.getEndpoint1(), Long.toString(rl.getEndpoint1().sbnPort.node_guid) + Long.toString(rl.getEndpoint1().sbnPort.port_num));
      portBins.add(rl.getEndpoint2(), Long.toString(rl.getEndpoint2().sbnPort.node_guid) + Long.toString(rl.getEndpoint2().sbnPort.port_num));
    }
    
    // there should be no duplicates, each bin should have only a single port element, meaning the port is only connected once
    for(ArrayList <OSM_Port> lp: portBins)
    {
      if(lp.size() > 1)
         logger.severe("The port (" + lp.get(0).sbnPort.node_guid + ") shows up " + lp.size() + " in the list of active links... NOT GOOD!");
    }
    
    return rtnlnks.size() == 0 ? links: rtnlnks;
  }
  return null;    
  }


  public static ArrayList <IB_Link> getActiveIB_Links(ArrayList <IB_Link> allLinks)
  {
  // given a list of IB_Links, return only the Active ones
  //
  if((allLinks != null) && (allLinks.size() > 0))
  {
    java.util.ArrayList<IB_Link> links   = new java.util.ArrayList<IB_Link>();
    for(IB_Link la: allLinks)
    {
      if(la.getState() == OSM_LinkState.ACTIVE)
        links.add(la);
    }
    return links;
  }
  return null;    
  }

  /************************************************************
   * Method Name:
   *  getIB_Link
  **/
  /**
   * Returns the IB_Link that contains the provided OSM_Port.  Given a list
   * of IB_Links, search through it and find the supplied OSM_Port on either end
   * of a link.  Return the first link that contains the port.
   *
   * @param   allLinks  a list of IB_Links to use in the search.  This can be all
   *                    the links in the fabric, or a subset.
   * @param   port      a port to find in the list of links.  This port can be at
   *                    either end of a link.
   *
   * @return  the IB_Link that contains the provided port, or null if the
   * port is not found in the list of links.
   ***********************************************************/
  public static IB_Link getIB_Link(ArrayList <IB_Link> allLinks, OSM_Port port)
  {
  // given a list of IB_Links and a port, find the link that contains the port
  //
  if((allLinks != null) && (allLinks.size() > 0) && (port != null))
  {
    for(IB_Link la: allLinks)
    {
      if(la.contains(port))
        return la;
    }
  }
  return null;    
  }
}
