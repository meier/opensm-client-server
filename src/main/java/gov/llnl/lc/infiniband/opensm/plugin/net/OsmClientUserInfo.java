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
 *        file: OsmClientUserInfo.java
 *
 *  Created on: Nov 18, 2014
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.net;

import gov.llnl.lc.system.hostmachine.LocalHost;
import gov.llnl.lc.system.useraccount.User;
import gov.llnl.lc.util.SystemConstants;

import java.io.Serializable;
import java.util.ArrayList;

/**********************************************************************
 * Describe purpose and responsibility of OsmClientUserInfo
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Nov 18, 2014 2:41:42 PM
 **********************************************************************/
public class OsmClientUserInfo implements Serializable
{
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = 1304456124994078490L;

  private LocalHost ClientHost;
  private User      ClientUser;
 
  /************************************************************
   * Method Name:
   *  OsmClientUserInfo
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   ***********************************************************/
  public OsmClientUserInfo()
  {
    /* instantiate these classes from the client side,
     * and then use the public members on the server side to
     * fully identify the client
     */
    ClientUser = new User();
    ClientHost = new LocalHost();
  }

  /************************************************************
   * Method Name:
   *  getClientHost
   **/
  /**
   * Returns the value of clientHost
   *
   * @return the clientHost
   *
   ***********************************************************/
  
  public LocalHost getClientHost()
  {
    return ClientHost;
  }

  /************************************************************
   * Method Name:
   *  getClientUser
   **/
  /**
   * Returns the value of clientUser
   *
   * @return the clientUser
   *
   ***********************************************************/
  
  public User getClientUser()
  {
    return ClientUser;
  }
  
  public String[] getClientUserGroups()
  {
    // there should be groups in the users id, strip them out and return
    // them in an array of strings
    //
    // return null if something goes wrong
    
    // uid=54125(meier3) gid=54125(meier3) groups=54125(meier3),126(vboxusers)
        
    String[] sp = ClientUser.UserId.split("groups=");
    if(sp.length != 2)
      return null;
    
    // the groups are in the second half
    String[] grps = sp[1].split(",");
    String[] sList = new String[grps.length];
    int n = 0;
    
    for(String g: grps)
    {
      // loop through the groups and build the array
      int bndex = g.indexOf('(') + 1;
      int endex = g.indexOf(')');
      sList[n++] = g.substring(bndex, endex);
    }
    return sList;
  }
  
  /************************************************************
   * Method Name:
   *  toString
  **/
  /**
   * Describe the method here
   *
   * @see java.lang.Object#toString()
   *
   * @return
   ***********************************************************/
  public String toString()
  {
    StringBuffer buff = new StringBuffer();
    
    buff.append(User.getUserName() + SystemConstants.NEW_LINE);
    buff.append(getClientUser().UserName + SystemConstants.NEW_LINE);
    buff.append(getClientUser().UserId + SystemConstants.NEW_LINE);
    
    buff.append(LocalHost.getHostName() + SystemConstants.NEW_LINE);
    buff.append(getClientHost().HostName + SystemConstants.NEW_LINE);
    buff.append(getClientHost().HostAddress + SystemConstants.NEW_LINE);
    buff.append(getClientHost().OS_Name + SystemConstants.NEW_LINE);
    buff.append(getClientHost().OS_Version + SystemConstants.NEW_LINE);

    return buff.toString();
  }

  /************************************************************
   * Method Name:
   *  main
   **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects
   *
   * @param args
   ***********************************************************/
  public static void main(String[] args)
  {
    OsmClientUserInfo ci = new OsmClientUserInfo();
    
    System.out.println(ci.toString());
   }
}
