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
 *        file: IB_Gid.java
 *
 *  Created on: Jan 11, 2012
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.core;

import gov.llnl.lc.logging.CommonLogger;

import java.io.Serializable;
import java.net.Inet6Address;
import java.net.UnknownHostException;

/**********************************************************************
 * Describe purpose and responsibility of IB_Gid
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Jan 11, 2012 9:11:33 AM
 **********************************************************************/
public class IB_Gid implements Serializable, CommonLogger, Comparable<IB_Gid>
{
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = -5701272264537153005L;

  // a 128 bit IPv6 address
  //
  // usually consists of 64 bit subnet prefix (network/subnet address) followed by
  // a 64 bit Guid (40 IEEE/vendor unique bits, followed by 24 vendor bits for CA and port)
  IB_Guid SubnetPrefix;
  IB_Guid Guid;
  
  // of the form -    fe80:0000:0000:0000:0002:c902:0023:c28a
  
  public IB_Gid(byte[] addr) {
    super();
    Inet6Address address = null;
    try
    {
        address = Inet6Address.getByAddress("IB", addr, 0);
    }
    catch(UnknownHostException uhe)
    {
      logger.severe("Bad GID address");
    }
    logger.fine("IPv6 addr: (" + address.getHostAddress());
    
    // break this up into the two guids...
  }

  public IB_Gid(IB_Guid subnetPrefix, IB_Guid guid) {
    super();
    SubnetPrefix = subnetPrefix == null ? new IB_Guid(0L): new IB_Guid(subnetPrefix.toString());
    Guid = guid == null ? new IB_Guid(0L): new IB_Guid(guid.toString());
  }


  public IB_Guid getSubnetPrefix() {
    return SubnetPrefix;
  }

  public IB_Guid getGuid() {
    return Guid;
  }

  @Override
  public int compareTo(IB_Gid gid) {
    // the two guids need to be the same
        //
    // both object must exist (and of the same class)
    // and should be consistent with equals
    //
    // -1 if less than
    // 0 if the same
    // 1 if greater than
    //
    if(gid == null)
      return -1;
    
    int val1 = this.Guid.compareTo(gid.Guid);
    int val2 = this.SubnetPrefix.compareTo(gid.SubnetPrefix);
    
        // always return val1, unless they are not equal and val1 is zero
    if(val1 == val2)
    {
      return val1;
    }
    return (val1 != 0? val1: val2);  // 

  }

  @Override
  public boolean equals(Object obj) {
    return ((obj != null) && (obj instanceof IB_Gid) && (this.compareTo((IB_Gid)obj)==0));
  }

  @Override
  public String toString() {
    return SubnetPrefix.toColonString()+Guid.toColonString();
  }

  
}
