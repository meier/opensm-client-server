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
 *        file: OSM_LinkRate.java
 *
 *  Created on: Jan 13, 2012
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import gov.llnl.lc.infiniband.core.IB_Link;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**********************************************************************
 * This enum describes the rate or bandwidth of a link.  It is a combination
 * of the links speed (clock speed), and data size (width).  There are
 * therefore several combinations of speed and width that can arrive at
 * the same rate.  This enum describes not only the resultant rate, but also
 * the speed and width used to achieve it.  Due to the nature of this enum
 * the comparable function is of little use.
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Jan 13, 2012 3:31:08 PM
 **********************************************************************/
public enum OSM_LinkRate
{
  ZERO(          0, OSM_LinkWidth.ONE_X, OSM_LinkSpeed.EXT,        OSM_RateNames.ZERO.getRateName()),
  TWOFIVE(       1, OSM_LinkWidth.ONE_X, OSM_LinkSpeed.TWOFIVE,    OSM_RateNames.TWOFIVE.getRateName()),
  FIVE(          2, OSM_LinkWidth.ONE_X, OSM_LinkSpeed.FIVE,       OSM_RateNames.FIVE.getRateName()),
  TEN(           4, OSM_LinkWidth.ONE_X, OSM_LinkSpeed.TEN,        OSM_RateNames.TEN.getRateName()),
  FOURTEEN(      6, OSM_LinkWidth.ONE_X, OSM_LinkSpeed.FOURTEEN,   OSM_RateNames.FOURTEEN.getRateName()),
  TWENTYFIVE(    7, OSM_LinkWidth.ONE_X, OSM_LinkSpeed.TWENTYFIVE, OSM_RateNames.TWENTYFIVE.getRateName()),

  ZERO4(       100, OSM_LinkWidth.FOUR_X, OSM_LinkSpeed.EXT,        OSM_RateNames.ZERO.getRateName()),
  TWOFIVE4(    104, OSM_LinkWidth.FOUR_X, OSM_LinkSpeed.TWOFIVE,    OSM_RateNames.TEN.getRateName()),
  FIVE4(       108, OSM_LinkWidth.FOUR_X, OSM_LinkSpeed.FIVE,       OSM_RateNames.TWENTY.getRateName()),
  TEN4(        116, OSM_LinkWidth.FOUR_X, OSM_LinkSpeed.TEN,        OSM_RateNames.FOURTY.getRateName()),
  FOURTEEN4(   124, OSM_LinkWidth.FOUR_X, OSM_LinkSpeed.FOURTEEN,   OSM_RateNames.FIFTYSIX.getRateName()),
  TWENTYFIVE4( 128, OSM_LinkWidth.FOUR_X, OSM_LinkSpeed.TWENTYFIVE, OSM_RateNames.ONE_HUNDRED.getRateName()),

  ZERO8(       200, OSM_LinkWidth.EIGHT_X, OSM_LinkSpeed.EXT,        OSM_RateNames.ZERO.getRateName()),
  TWOFIVE8(    208, OSM_LinkWidth.EIGHT_X, OSM_LinkSpeed.TWOFIVE,    OSM_RateNames.TWENTY.getRateName()),
  FIVE8(       216, OSM_LinkWidth.EIGHT_X, OSM_LinkSpeed.FIVE,       OSM_RateNames.FOURTY.getRateName()),
  TEN8(        232, OSM_LinkWidth.EIGHT_X, OSM_LinkSpeed.TEN,        OSM_RateNames.EIGHTY.getRateName()),
  FOURTEEN8(   248, OSM_LinkWidth.EIGHT_X, OSM_LinkSpeed.FOURTEEN,   OSM_RateNames.ONE_TWELVE.getRateName()),
  TWENTYFIVE8( 256, OSM_LinkWidth.EIGHT_X, OSM_LinkSpeed.TWENTYFIVE, OSM_RateNames.TWO_HUNDRED.getRateName()),

  ZERO12(      300, OSM_LinkWidth.TWELVE_X, OSM_LinkSpeed.EXT,        OSM_RateNames.ZERO.getRateName()),
  TWOFIVE12(   312, OSM_LinkWidth.TWELVE_X, OSM_LinkSpeed.TWOFIVE,    OSM_RateNames.THIRTY.getRateName()),
  FIVE12(      324, OSM_LinkWidth.TWELVE_X, OSM_LinkSpeed.FIVE,       OSM_RateNames.SIXTY.getRateName()),
  TEN12(       348, OSM_LinkWidth.TWELVE_X, OSM_LinkSpeed.TEN,        OSM_RateNames.ONE_TWENTY.getRateName()),
  FOURTEEN12(  372, OSM_LinkWidth.TWELVE_X, OSM_LinkSpeed.FOURTEEN,   OSM_RateNames.ONE_SIX_EIGHT.getRateName()),
  TWENTYFIVE12(384, OSM_LinkWidth.TWELVE_X, OSM_LinkSpeed.TWENTYFIVE, OSM_RateNames.THREE_HUNDRED.getRateName()),
  
  DONOTKNOW(-1, OSM_LinkWidth.UNKNOWN, OSM_LinkSpeed.UNKNOWN, OSM_RateNames.UNKNOWN_RATE.getRateName());

  /* following v1 ver1.2 p901 */
//  #define IB_PATH_RECORD_RATE_2_5_GBS   2
//  #define IB_PATH_RECORD_RATE_10_GBS    3
//  #define IB_PATH_RECORD_RATE_30_GBS    4
//  #define IB_PATH_RECORD_RATE_5_GBS     5
//  #define IB_PATH_RECORD_RATE_20_GBS    6
//  #define IB_PATH_RECORD_RATE_40_GBS    7
//  #define IB_PATH_RECORD_RATE_60_GBS    8
//  #define IB_PATH_RECORD_RATE_80_GBS    9
//  #define IB_PATH_RECORD_RATE_120_GBS   10
//  #define IB_PATH_RECORD_RATE_14_GBS    11
//  #define IB_PATH_RECORD_RATE_56_GBS    12
//  #define IB_PATH_RECORD_RATE_112_GBS   13
//  #define IB_PATH_RECORD_RATE_168_GBS   14
//  #define IB_PATH_RECORD_RATE_25_GBS    15
//  #define IB_PATH_RECORD_RATE_100_GBS   16
//  #define IB_PATH_RECORD_RATE_200_GBS   17
//  #define IB_PATH_RECORD_RATE_300_GBS   18
//
//  #define IB_MIN_RATE    IB_PATH_RECORD_RATE_2_5_GBS
//  #define IB_MAX_RATE    IB_PATH_RECORD_RATE_300_GBS
  
  public enum OSM_RateNames
  {
    ZERO(               0, "0 Gb/s"),
    TWOFIVE(         2500, "2.5 Gb/s"),
    FIVE(            5000, "5 Gb/s"),
    TEN(            10000, "10 Gb/s"),
    FOURTEEN(       14000, "14 Gb/s"),
    TWENTY(         20000, "20 Gb/s"),
    TWENTYFIVE(     25000, "25 Gb/s"),
    THIRTY(         30000, "30 Gb/s"),
    FOURTY(         40000, "40 Gb/s"),
    FIFTYSIX(       56000, "56 Gb/s"),
    SIXTY(          60000, "60 Gb/s"),
    EIGHTY(         80000, "80 Gb/s"),
    ONE_HUNDRED(   100000, "100 Gb/s"),
    ONE_TWELVE(    112000, "112 Gb/s"),
    ONE_TWENTY(    120000, "120 Gb/s"),
    ONE_SIX_EIGHT( 168000, "168 Gb/s"),
    TWO_HUNDRED(   200000, "200 Gb/s"),
    THREE_HUNDRED( 300000, "300 Gb/s"),
    UNKNOWN_RATE( -1, "UNKNOWN");
    
    public static final EnumSet<OSM_RateNames> OSMLINK_ALL_RATE_NAMES    = EnumSet.allOf(OSM_RateNames.class);
    private static final Map<Integer,OSM_RateNames> lookup = new HashMap<Integer,OSM_RateNames>();

    static 
    {
      for(OSM_RateNames s : OSMLINK_ALL_RATE_NAMES)
           lookup.put(s.getNum(), s);
    }
    
    private int Num;
    private String RateName;
    
    private OSM_RateNames(int num,  String name)
    {
        this.Num = num;
        this.RateName = name;
    }
    public int getNum()
    {
      return Num;
      }

    public String getRateName()
    {
      return RateName;
    }

    public static OSM_RateNames get(int Rate_num)
    { 
        return lookup.get(Rate_num); 
    }
    
    public static int getNum(String RateName)
    {
      for(OSM_RateNames s : OSMLINK_ALL_RATE_NAMES)
      {
        if(s.getRateName().equals(RateName))
          return s.getNum();
      }
      return 0;
    }
  }

  public static final int IB_PORT_CAP_HAS_EXT_SPEEDS  = 0x00004000;
  
  
  public static final EnumSet<OSM_LinkRate> OSMLINK_ALL_RATES    = EnumSet.allOf(OSM_LinkRate.class);
  public static final EnumSet<OSM_LinkRate> OSMLINK_UNIQUE_RATES = EnumSet.of(ZERO, TWOFIVE, FIVE, TEN,
        FOURTEEN, FIVE4, TWENTYFIVE, TWOFIVE12, TEN4, FOURTEEN4, FIVE12, TEN8, TWENTYFIVE4, FOURTEEN8,
        TEN12, FOURTEEN12, TWENTYFIVE8, TWENTYFIVE12);
  
  private static final Map<Integer,OSM_LinkRate> lookup = new HashMap<Integer,OSM_LinkRate>();

  static 
  {
    for(OSM_LinkRate s : OSMLINK_ALL_RATES)
         lookup.put(s.getRate(), s);
  }
  
  private OSM_LinkWidth width;
  private OSM_LinkSpeed speed;
  
  // this value is the number of the width times the number of the speed, offset by 100 per width jump
  private int Rate;
  private String RateName;

private OSM_LinkRate(int Rate_num, OSM_LinkWidth width, OSM_LinkSpeed speed, String Name)
{
    this.Rate = Rate_num;
    this.width = width;
    this.speed = speed;
    this.RateName = Name;
}

public int getRate()
{
  return Rate;
  }

public long getTheoreticalRateValue(long scale)
{
  // Typically scale would be from PFM_PortRate.PortCounterUnits
  
  // the rate num is Kbs. 
  int val = getRateNameNum();
  
  // normal scaling... up from Kbs to Gbs, then down to Bytes from bits
  long temp = (long)val * 131072L;
  
  // Convert to the desired scale - should be able to do a normal divide
  return temp/scale;
}

/************************************************************
 * Method Name:
 *  getRateValue
**/
/**
 * Applies the scale to the theoretical maximum rate, and also
 * removes the 20% overhead, to provide a realistic "payload"
 * rate.
 *
 * @see     describe related java objects
 *
 * @param scale
 * @return
 ***********************************************************/
public long getRateValue(long scale)
{
  // the actual rate value is about 80% of theoretical maximum 
  long val = getTheoreticalRateValue(scale);
  
  // should be able to do a normal divide
  return (val * 8L)/10L;
}

public String getRateName()
{
  return RateName;
}

protected int getRateNameNum()
{
  return OSM_RateNames.getNum(this.getRateName());
}

public static OSM_LinkRate get(int Rate_num)
{
	// if not found in the lookup table, return UNKNOWN
	OSM_LinkRate lr = lookup.get(Rate_num);
	if(lr != null)
		return lr;
	System.err.println("UNKNOWN Link Rate: " + Rate_num);
    return lookup.get(-1); 
}

public static OSM_LinkRate get(IB_Link link)
{ 
  if((link == null) || (link.getSpeed() == null) || (link.getWidth() == null))
    return ZERO;
  
  // get the rate from the links width and speed
  // this is how the numbers in the enum were calculated, so do it now to lookup the rate for this port
  int lookup_number = (link.getSpeed().getSpeed() * (link.getWidth().getMultiplier())) + link.getWidth().getOffset();
  
  OSM_LinkRate lr = get(lookup_number);
  if(lr == DONOTKNOW)
  {
	  System.err.println("Speed: " + link.getSpeed().getSpeed());
	  System.err.println("Width M: " + link.getWidth().getMultiplier() + ", O: " + link.getWidth().getOffset());
  }
  return lr;
}

public static OSM_LinkRate get(OSM_Port port)
{ 
  if(port == null)
    return ZERO;  
  return get(port.getSbnPort());
}

public static OSM_LinkRate get(SBN_Port port)
{ 
  if(port == null)
    return ZERO;  
  return get(port.port_info, port.ext_port_info);
}


public static OSM_LinkRate get(SBN_PortInfo portInfo, MLX_ExtPortInfo extPortInfo)
{ 
  if(portInfo == null)
    return ZERO;
  
  // the LinkRate is derived from the LinkSpeed and the LinkWidth
  int speed  = OSM_LinkSpeed.get(portInfo, extPortInfo).getSpeed();
  int widthM = OSM_LinkWidth.get(portInfo).getMultiplier();
  int widthO = OSM_LinkWidth.get(portInfo).getOffset();
  
  
  // this is how the numbers in the enum were calculated, so do it now to lookup the rate for this port
  int lookup_number = (OSM_LinkSpeed.get(portInfo, extPortInfo).getSpeed() * (OSM_LinkWidth.get(portInfo).getMultiplier())) + OSM_LinkWidth.get(portInfo).getOffset();
  
//  return get(lookup_number);
  
  OSM_LinkRate lr = get(lookup_number);
  if(lr == DONOTKNOW)
  {
    System.err.println("lookup number is: " + lookup_number);
    System.err.println("Speed: " + speed);
	  System.err.println("Width M: " + widthM + ", O: " + widthO);
  }
  return lr;
}

public int compareAgainst(OSM_LinkRate lr2)
{
  return OSM_LinkRate.compareAgainst(this, lr2);
}

public static int compareAgainst(OSM_LinkRate lr1, OSM_LinkRate lr2)
{ 
  if((lr1 == null) || (lr2 == null))
    return -1;
  
  // compare the two different link rates, return 1 only if the first one is greater than the second one
  //                                       return 0 if the two rates have the same value
  //                                       return -1 if the first one is less than the second one
  
  // if the linkrate names are the same, return zero
  if(lr1.getRateName().equals(lr2.getRateName()))
    return 0;
  
  // compare the numbers associated with the RateNames
  return lr1.getRateNameNum() < lr2.getRateNameNum() ? -1: 1;
}


}
