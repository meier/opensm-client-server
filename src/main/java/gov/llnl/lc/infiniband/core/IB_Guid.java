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
 *        file: NativePeerClassExample.java
 *
 *  Created on: Aug 26, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.core;

import java.io.Serializable;
import java.math.BigInteger;

/**********************************************************************
 * An <code>IB_Guid</code> is a fundamental identification tag for entities
 * in the fabric.  Since it is a "globally unique identifier" it should always
 * be used as the key for nodes and ports.  This class provides some
 * Convenience functions for creating, comparing, and manipulating guids.
 * <p>
 * Java Peer Class, to the native interface.  All peer classes contain
 * public members which can be directly accessed.  All peer classes
 * have fully parameterized constructors that are used by the native
 * layer to create an instance of the class.
 * <p>
 * @see NativePeerClass
 *
 * @author meier3
 * 
 * @version Aug 26, 2011 11:06:13 AM
 **********************************************************************/
@NativePeerClass("v1.1")
public class IB_Guid implements Serializable, Comparable<IB_Guid>
{
	// one of many types of IB Addresses
	//
	// a 64 bit Guid (40 IEEE/vendor unique bits, followed by 24 vendor bits for CA and port)
	
	/**  describe serialVersionUID here **/
  private static final long serialVersionUID = -181382010594345973L;
  
  private static final String HEXCHARACTERS = "1234567890abcdefABCDEF";
  private static final String COLONCHARACTER = ":";
  
  /* the guids are long longs, or 64 bits.  Java doesn't support that with
   * fundamental data types, so we must "occasionally" use BigInteger's.  
   */
  public static final BigInteger MAX_GUID_VALUE = new BigInteger("18446744073709551615");
  public static final BigInteger MIN_GUID_VALUE = BigInteger.ZERO;

  
  private long lGuid;  // represented as a hex value;
	
	/************************************************************
	 * Method Name:
	 *  IB_Guid
	 */
	 /** A guid is a long.  This is the normal constructor used
	  * by the native layer.
	 *
	 * @param guid  - the globally unique identifier
	 ***********************************************************/
	public IB_Guid(long guid)
	{
		super();
		lGuid = guid;
	}

  /************************************************************
   * Method Name:
   *  IB_Guid
   */
   /** The copy constructor.
   *
   * @param guid - the globally unique identifier
   ***********************************************************/
  public IB_Guid(IB_Guid guid)
  {
    super();
    lGuid = guid == null ? 0L :guid.getGuid();
  }

  /************************************************************
   * Method Name:
   *  IB_Guid
   */
   /** A guid is an unsigned long.  Since we can't represent
    * unsigned longs in Java, we use BigIntegers.
   *
   * @param guid - the globally unique identifier
   ***********************************************************/
  public IB_Guid(BigInteger guid)
  {
    this(IB_Guid.convertBigIntegerToGuidLong(guid));
  }

	/************************************************************
	 * Method Name:
	 *  IB_Guid
	 */
	 /** The string constructor.  The string is expected to be in
	  * Hexadecimal (may or may not start with 0x and may or may
	  * not be colon delimited).  If the string contains ONLY the
	  * 0-9 digits (no hex or other chars) then it will be assumed
	  * the string represents the LONG value of the guid (not hex)
	  * and will be handled accordingly.
	 *
	 * @param sGuid a string that represents a guid
	 ***********************************************************/
  public IB_Guid(String sGuid)
  {
    this(0);
    if ((sGuid != null) && (sGuid.length() > 0))
    {
      // if it contains ANY non-digits, assume it is
      // in hex, and handle accordingly.
      // Otherwise, assume it may be a long, so
      // attempt to convert it directly to the lGuid
      //
      // an IB_Guid or a long

      try
      {
        // this will fail if it has colons or starts with an 0x
        lGuid = Long.parseLong(sGuid);
      }
      catch (NumberFormatException e1)
      {
        // must not be a long, convert as if an IB_Guid string

        // optional prefix of 0x permitted (strip off)
        String sguid = sGuid.toLowerCase().trim();
        int ndex = 0;
        if (sguid.startsWith("0x"))
          ndex = 2;

        sguid = sguid.substring(ndex);

        // optional colons permitted (strip off)
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < sguid.length(); i++)
        {
          if (COLONCHARACTER.indexOf(sguid.charAt(i)) < 0)
          {
            buff.append(sguid.charAt(i));
          }
        }
        
//        StringBuffer buff = new StringBuffer();
//        for (int i = 0; i < sguid.length(); i++)
//        {
//          if (HEXCHARACTERS.indexOf(sguid.charAt(i)) > -1)
//          {
//            buff.append(sguid.charAt(i));
//          }
//        }
        
        try
        {
          lGuid = Long.parseLong(buff.toString(), 16);
        }
        catch (NumberFormatException e2)
        {
          // assume its a large number, which needs to be converted using
          // BigIntegers
          BigInteger bi = new BigInteger(buff.toString(), 16);
          lGuid = convertBigIntegerToGuidLong(bi);
        }
      }
    }
  }
	
	public long getGuid()
	{
		return lGuid;
	}

	/************************************************************
	 * Method Name:
	 *  add
	 */
	 /** IB_Guid addition
	 *
	 * @param guid the guid to add
	 * @param val  the value to add
	 * @return  a new object representing the sum of the two
	 ***********************************************************/
//	public static IB_Guid add(IB_Guid guid, int val)
//	{
//		long lguid = (guid == null) ? val: guid.getGuid() + val;
//		return new IB_Guid(lguid);
//	}

	/************************************************************
	 * Method Name:
	 *  sub
	 */
	 /** IB_Guid subtraction
	 *
	 * @param guid the guid to subtract
	 * @param val  the value to subtract
	 * @return  a new object representing the difference of the two
	 ***********************************************************/
//	public static IB_Guid sub(IB_Guid guid, int val)
//	{
//		return IB_Guid.add(guid, -val);
//	}

  /************************************************************
   * Method Name:
   *  toGuidArrayString
   */
   /**
   * Given an array of guids (as longs), returns a formatted string
   * representation.
   *
   * @see     IB_Guid
   *
   * @param guid_array  an array of guids
   * @return a string representation of an array of guids in colon format
   ***********************************************************/
  public static String toGuidArrayString(long [] guid_array)
  {
    StringBuffer sbuff = new StringBuffer();

    if((guid_array != null) && (guid_array.length > 0))
    {
      /* iterate through the array, and build a string */
      for (long guid : guid_array)
      {
        sbuff.append(new IB_Guid(guid).toColonString() + "\n");
      }
    }
   return sbuff.toString(); 
  }

	/************************************************************
	 * Method Name:
	 *  toString
	 */
	 /** The hexidecimal representation of the IB_Guid.
	 *
	 * @see java.lang.Object#toString()
	 *
	 * @return a string representation of the guid using
	 *  Long.toHexString()
	 ***********************************************************/
	@Override
	public String toString() 
	{
		// hex, without leading 0x or zeros
		return Long.toHexString(lGuid).trim();
	}

	/************************************************************
	 * Method Name:
	 *  toColonString
	 */
	 /** A colon separated representation of the IB_Guid.
	 *
	 * @return a string representation of the guid using 
	 * Long.toHexString and colons in the form 0xnnn:nnnn:nnnn:nnnn
	 ***********************************************************/
	public String toColonString() 
	{
		// the form nnnn:nnnn:nnnn:nnnn
		
		StringBuffer sbuff = new StringBuffer(this.toString());
		
		// pad and insert colons until the resultant string is exactly 19
		while(sbuff.length() < 4)
			sbuff.insert(0, '0');
		
		sbuff.insert(sbuff.length() -4, ':');  // should be at least 5 long now
		
		while(sbuff.length() < 9)
			sbuff.insert(0, '0');
		
		sbuff.insert(sbuff.length() -9, ':');  // should be at least 10 long now
		
		while(sbuff.length() < 14)
			sbuff.insert(0, '0');
		
		sbuff.insert(sbuff.length() -14, ':');  // should be at least 15 long now
		
		while(sbuff.length() < 19)
			sbuff.insert(0, '0');
		
		return sbuff.toString();
	}
	
  public static BigInteger convertUnsignedLongLongToBigInteger(long n1)
  {
    // by definition, the argument is not negative
    BigInteger rtn;
    if(n1 < 0L)
    {
      /* its negative because it thinks its 2's compliment, so
       * need to undo that, ... compliment and add one
       */
      long n2 = -1L ^ n1;
      rtn = BigInteger.valueOf(n2).add( BigInteger.ONE);
    }
    else
      rtn = BigInteger.valueOf(n1);
    return rtn;
  }

  public static long convertBigIntegerToGuidLong(BigInteger bi)
  {
    // don't allow negative big integers, just return zero
    if(bi.compareTo(MIN_GUID_VALUE) <= 0) return 0L;
    
    //   FIXME:  what do I do with a big integer too big for an
    //           unsigned long long?
    //           * return max value for unsigned long
    bi = bi.min(MAX_GUID_VALUE);
    
    // this will do the twos compliment trick needed for unsigned long guids
    return bi.longValue();
  }

	/************************************************************
	 * Method Name:
	 *  compareTo
	 */
	 /** Compares two objects.  They must both exist, and be of
	  * the same class.
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 *
	 * @param guid the guid to compare to this one
	 * @return -1 if less than, 0 if equal, 1 if greater than
	 ***********************************************************/
	@Override
	public int compareTo(IB_Guid guid)
	{
		
		// the NodeGUID is the only thing that MUST be unique
        //
		// both object must exist (and of the same class)
		// and should be consistent with equals
		//
		// -1 if less than
		// 0 if the same
		// 1 if greater than
		//
		if(guid == null)
			throw new NullPointerException();

		// use big integer here
    BigInteger bi = convertUnsignedLongLongToBigInteger(this.lGuid);
    BigInteger bo = convertUnsignedLongLongToBigInteger(guid.getGuid());
    return bi.compareTo(bo);
		
//		return Long.signum(this.lGuid - guid.getGuid());
	}

	/************************************************************
	 * Method Name:
	 *  equals
	 */
	 /** Compares the two for equality.
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 *
	 * @param obj  the object to compare against this one
	 * @return true if equals, otherwise false
	 ***********************************************************/
	@Override
	public boolean equals(Object obj)
	{
		return ((obj != null) && (obj instanceof IB_Guid) && (this.compareTo((IB_Guid)obj)==0));
	}
	
  public static void main(String[] args)
  {
    // test
    IB_Guid g = new IB_Guid("f45214030045b3a0");
    System.err.println(g.toColonString());
    
    g = new IB_Guid("7fff:ffff:ffff:ffff");
    System.err.println(g.toColonString());
    
    g = new IB_Guid("8000:0000:0000:0000");
    System.err.println(g.toColonString());
    
    long bigLong = 9223372036854775807L;  
    long smallLong = -9223372036854775808L; 
    
    IB_Guid gmax = new IB_Guid(bigLong);
    IB_Guid gmin = new IB_Guid(smallLong);
    IB_Guid pg = new IB_Guid(1L);
    IB_Guid zg = new IB_Guid(0L);
    IB_Guid ng = new IB_Guid(-1L);
    
    System.err.println("Largest Guid is: " + gmax.toColonString() + ", and Smallest Guid is: " + gmin.toColonString());
    System.err.println("One Guid is: " + pg.toColonString() + ", Zero Guid is: " + zg.toColonString() + ", and Neg One Guid is: " + ng.toColonString());
    
    IB_Guid mbg = new IB_Guid(MAX_GUID_VALUE);
    System.err.println("The Max BI Guid is: " + mbg.toColonString());
  }

}
