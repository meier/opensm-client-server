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
 *        file: IB_Address.java
 *
 *  Created on: Jan 11, 2012
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.core;

import gov.llnl.lc.logging.CommonLogger;

import java.io.Serializable;

/**********************************************************************
 * Describe purpose and responsibility of IB_Address
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Jan 11, 2012 9:14:00 AM
 **********************************************************************/
public class IB_Address implements Serializable, CommonLogger, Comparable<IB_Address>
{
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = -4915620424186588256L;

  private IB_Guid Guid;   // CA, Switch, Router, etc.
  private IB_Gid GID;     // subnet GID + GUID
  private int LocalId;    // assigned by SM

  
  public IB_Address(IB_Guid guid, IB_Gid gid, int localId) {
    super();
    Guid = guid;
    GID = gid;
    LocalId = localId;
  }


  public IB_Address(IB_Guid guid, int localId) {
    super();
    Guid = guid;
    LocalId = localId;
  }


  public IB_Address(IB_Guid guid) {
    super();
    Guid = guid;
  }

  public static int toLidValue(String lidString)
  {
    int lid = -1;
    // trim it,and see if it starts with a 0x, if so interpret it like a hex number
    if(lidString != null)
    {
      if(lidString.toLowerCase().trim().startsWith("0x"))
      {
        // assume its hex
        lid = Integer.parseInt(lidString.substring(2), 16);
      }
      else
      {
        // else assume it is NOT hex
        lid = new Integer(lidString).intValue();
      }
    }
    return lid;
  }

  public static String toLidHexString(int lid)
  {
    return "0x" + Integer.toHexString(lid);
  }


  public IB_Gid getGID() {
    return GID;
  }


  public void setGID(IB_Gid gid) {
    GID = gid;
  }


  public int getLocalId() {
    return LocalId;
  }
  
    public String getLocalIdHexString()
    {
      return toLidHexString(LocalId);
  }


  public void setLocalId(int localId) {
    LocalId = localId;
  }


  public IB_Guid getGuid() {
    return Guid;
  }

  public int compareGuidTo(IB_Address addr) {
    // compares the two Guids
        //
    // both objects must exist (and of the same class)
    // and should be consistent with equals
    //
    // -1 if less than
    // 0 if the same
    // 1 if greater than
    //
    if((addr == null) || (this.getGuid() == null))
      throw new NullPointerException();
    
    return this.getGuid().compareTo(addr.getGuid());
  }


  public int compareLidTo(IB_Address addr) {
    // compares the two lids
        //
    // both objects must exist (and of the same class)
    // and should be consistent with equals
    //
    // -1 if less than
    // 0 if the same
    // 1 if greater than
    //
    if(addr == null)
      throw new NullPointerException();
    if(this.LocalId > addr.getLocalId())
      return 1;
    if(this.LocalId < addr.getLocalId())
      return -1;
    
    return 0;
  }

  public int compareToAll(IB_Address addr) {
    // ideally, the GID, GUID, and LID must all match
    // for addresses to be considered equal
    //
    //  but these are all supposed to be unique??
        //
    // both object must exist (and of the same class)
    // and should be consistent with equals
    //
    // -1 if less than (null or zero values)
    // 0 if the same
    // 1 if greater than
    //
    if((addr == null) || (this.Guid == null))
    {
      logger.severe("No Address or No Guid [" + addr + "] {" + this.Guid + "}");
      throw new NullPointerException();
    }
    if(this.GID == null)
      return 1;
    
    int val1 = this.compareGuidTo(addr);
    int val2 = this.GID.compareTo(addr.getGID());
    int val3 = this.compareLidTo(addr);
    
    if((val1 == val2) && (val1 == val3))
      return val1;
      
    return (val1 != 0? val1: val3);  // 
  }

  @Override
  public int compareTo(IB_Address addr) {
    // ideally, the GID, GUID, and LID must all match
    // for addresses to be considered equal
    //
    //  but these are all supposed to be unique??
        //
    // both object must exist (and of the same class)
    // and should be consistent with equals
    //
    // -1 if less than (null or zero values)
    // 0 if the same
    // 1 if greater than
    //
    return compareGuidTo(addr);
  }
  
  @Override
  public boolean equals(Object obj) {
    return ((obj != null) && (obj instanceof IB_Address) && (this.compareTo((IB_Address)obj)==0));
  }


  @Override
  public String toString() {
    StringBuffer stringValue = new StringBuffer();
    
    stringValue.append("Guid:"  + Guid + "\n");
    stringValue.append("LID:" + LocalId + "\n");
    stringValue.append("GID: " + GID + "\n");
  
    return stringValue.toString();
  }

  
}
