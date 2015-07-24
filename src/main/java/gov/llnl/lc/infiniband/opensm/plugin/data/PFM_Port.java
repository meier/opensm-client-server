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
 *        file: PFM_Port.java
 *
 *  Created on: Jul 11, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import gov.llnl.lc.infiniband.core.IB_Guid;
import gov.llnl.lc.infiniband.core.NativePeerClass;
import gov.llnl.lc.time.TimeStamp;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**********************************************************************
 * A <code>PFM_Port</code> represents the information the perf manager
 * maintains on each port.  To the extent that it is possible,
 * it mirrors members in the <code>struct pm_port</code>.
 * <p>
 * Java Peer Class, to the native interface.  All peer classes contain
 * public members which can be directly accessed.  All peer classes
 * have fully parameterized constructors that are used by the native
 * layer to create an instance of the class.  Instances of peer classes
 * are not intended to be created from the java environment.
 * <p>
 * @see  OSM_Ports
 * @see TimeStamp
 * @see IB_Guid
 * @see NativePeerClass
 *
 * @author meier3
 * 
 * @version Aug 26, 2011 4:42:25 PM
 **********************************************************************/
@NativePeerClass("v1.0")
public class PFM_Port implements Serializable, Comparable<PFM_Port>
{

//  typedef struct pm_port
//  {
//    uint64_t node_guid;
//    uint8_t port_num;
//    uint64_t port_counters[NUM_PORT_COUNTERS];
//    time_t counter_ts;
//    time_t error_ts;
//    time_t wait_ts;
//  } pm_Port_t;

  
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = 576937545259313284L;
  public static final short MAX_PORT_NUM = 38;
  public static final short MIN_PORT_NUM = 0;
  
  /* the counters are unsigned long longs, or 64 bits.  Java doesn't support that with
   * fundamental data types, so we must "occasionally" use BigInteger's.  
   */
  public static final BigInteger MAX_COUNTER_VALUE = new BigInteger("18446744073709551615");
  public static final BigInteger MIN_COUNTER_VALUE = BigInteger.ZERO;
  
  
  /**********************************************************************
   * The <code>PortCounterName</code> enum matches the order of the array
   * from the native layer.  Do not alter.  The order in the array dictates
   * the type of counter.   See db_port_t, perfmgr_db_err_reading_t  & 
   * perfmgr_db_data_cnt_reading_t in osm_perfmgr_db.h 
   * <p>
   *
   * @author meier3
   * 
   * @version Aug 26, 2011 4:40:35 PM
   **********************************************************************/
  public enum PortCounterName
  {
    symbol_err_cnt(       0, "symbol_err_cnt",      1, "description"),    
    link_err_recover(     1, "link_err_recover",    1, "description"),    
    link_downed(          2, "link_downed",         1, "description"),    
    rcv_err(              3, "rcv_err",             1, "description"),    
    rcv_rem_phys_err(     4, "rcv_rem_phys_err",    1, "description"),    
    rcv_switch_relay_err( 5, "rcv_switch_relay_err",1, "description"),    
    xmit_discards(        6, "xmit_discards",       1, "description"),    
    xmit_constraint_err(  7, "xmit_constraint_err", 1, "description"),    
    rcv_constraint_err(   8, "rcv_constraint_err",  1, "description"),    
    link_integrity(       9, "link_integrity",      1, "description"),    
    buffer_overrun(      10, "buffer_overrun",      1, "description"),    
    vl15_dropped(        11, "vl15_dropped",        1, "description"),    
    xmit_data(           12, "xmit_data",           4, "description"),    
    rcv_data(            13, "rcv_data",            4, "description"),    
    xmit_pkts(           14, "xmit_pkts",           1, "description"),    
    rcv_pkts(            15, "rcv_pkts",            1, "description"),    
    unicast_xmit_pkts(   16, "unicast_xmit_pkts",   1, "description"),    
    unicast_rcv_pkts(    17, "unicast_rcv_pkts",    1, "description"),    
    multicast_xmit_pkts( 18, "multicast_xmit_pkts", 1, "description"),    
    multicast_rcv_pkts(  19, "multicast_rcv_pkts",  1, "description"),    
    xmit_wait(           20, "xmit_wait",           1, "description");    

    public static final EnumSet<PortCounterName> PFM_ALL_COUNTERS = EnumSet.allOf(PortCounterName.class);
    
    public static final EnumSet<PortCounterName> PFM_ERROR_COUNTERS  = EnumSet.range(PortCounterName.symbol_err_cnt, PortCounterName.vl15_dropped);
    public static final EnumSet<PortCounterName> PFM_DATA_COUNTERS   = EnumSet.range(PortCounterName.xmit_data, PortCounterName.rcv_data);
    public static final EnumSet<PortCounterName> PFM_PACKET_COUNTERS = EnumSet.range(PortCounterName.xmit_pkts, PortCounterName.rcv_pkts);

    public static final EnumSet<PortCounterName> PFM_SUPPRESS_COUNTERS = EnumSet.of(PortCounterName.rcv_rem_phys_err, PortCounterName.rcv_switch_relay_err);

    private static final Map<Integer,PortCounterName> lookup = new HashMap<Integer,PortCounterName>();

    static 
    {
      for(PortCounterName s : PFM_ALL_COUNTERS)
           lookup.put(s.getIndex(), s);
    }
    
  // the index that matches the native peer array
    private int Index;
    
    private int Scale;
    
    // the name of the counter
    private String Name;
    
    // a description of the counter
    private String Description;
    
    private PortCounterName(int index, String name, int scale, String description)
    {
      Index = index;
      Name = name;
      Scale = scale;
      Description = description;
    }
    
    public static PortCounterName getByName(String name)
    {
      PortCounterName t = null;
      
      // return the first property with an exact name match
      for(PortCounterName s : PFM_ALL_COUNTERS)
      {
        if(s.getName().equals(name))
          return s;
      }
      return t;
    }

    public static PortCounterName getByIndex(int index)
    {
      PortCounterName t = null;
      
      // return the first property with an exact name match
      for(PortCounterName s : PFM_ALL_COUNTERS)
      {
        if(s.getIndex() == index)
          return s;
      }
      return t;
    }

    public int getIndex()
    {
      return Index;
    }

    public int getScale()
    {
      return Scale;
    }


    public String getName()
    {
      return Name;
    }


    public String getDescription()
    {
      return Description;
    }
  };

  /**  the port number **/
  public short port_num;
  /**  the nodes guid **/
  public long node_guid;
  /**  the time the counters were acquired **/
  public long counter_ts;
  /**  the time the error counters were acquired **/
  public long error_ts;
  /**  the wait time **/
  public long wait_ts;
  
  /**  this array contains both port counters and errors, ordered by the <code>enum PortCounterName</code> **/
  /** NOTE: all values are unsigned 64bit values, so use the special compare when necessary, and also
   * use the special toUnsignedString when necessary
   */
  public long [] port_counters;

  /** a set of error counters to ignore, by default PFM_Port.PortCounterName.PFM_SUPPRESS_COUNTERS **/ 
  private EnumSet<PortCounterName> Suppressed_Counters = PFM_Port.PortCounterName.PFM_SUPPRESS_COUNTERS;

/************************************************************
     * Method Name:
     *  PFM_Port
     */
     /** Default constructor
     *
     ***********************************************************/
    public PFM_Port()
    {
      super();
      // default, can be changed later
      this.Suppressed_Counters = PFM_Port.PortCounterName.PFM_SUPPRESS_COUNTERS;
    }

    /************************************************************
     * Method Name:
     *  PFM_Port
     */
     /**
     * The fully parameterized constructor used by the native layer
     * to create an instance of this peer class.
     *
     * @param port_num  the port number
     * @param node_guid the node guid
     * @param counter_ts the timestamp for the counters
     * @param error_ts   the timestamp for the error counters
     * @param wait_ts    the timestamp for the wait
     * @param port_counters the array of counters
     ***********************************************************/
    public PFM_Port(short port_num, long node_guid, long counter_ts, long error_ts, long wait_ts,
        long[] port_counters)
    {
      this();
      this.port_num = port_num;
      this.node_guid = node_guid;
      this.counter_ts = counter_ts;
      this.error_ts = error_ts;
      this.wait_ts = wait_ts;
      this.port_counters = port_counters;
    }
    
    /************************************************************
     * Method Name:
     *  PFM_Port
     */
     /**
     * The copy constructor.
     *
     ***********************************************************/
    public PFM_Port(PFM_Port oPort)
    {
      this();
      this.port_num = oPort.port_num;
      this.node_guid = oPort.node_guid;
      this.counter_ts = oPort.counter_ts;
      this.error_ts = oPort.error_ts;
      this.wait_ts = oPort.wait_ts;
      this.port_counters = new long [PortCounterName.values().length];
      for(int n=0; n< PortCounterName.values().length; n++)
        this.port_counters[n] = oPort.port_counters[n];
    }
    
    
    
    /************************************************************
     * Method Name:
     *  getSuppressed_Counters
     **/
    /**
     * Returns the value of suppressed_Counters
     *
     * @return the suppressed_Counters
     *
     ***********************************************************/
    
    public EnumSet<PortCounterName> getSuppressed_Counters()
    {
      return Suppressed_Counters;
    }

    /************************************************************
     * Method Name:
     *  setSuppressed_Counters
     **/
    /**
     * Sets the value of suppressed_Counters
     *
     * @param suppressed_Counters the suppressed_Counters to set
     *
     ***********************************************************/
    public void setSuppressed_Counters(EnumSet<PortCounterName> suppressed_Counters)
    {
      Suppressed_Counters = suppressed_Counters;
    }

    /************************************************************
     * Method Name:
     *  getCounter
     */
     /** Returns the value of the named port counter.
     *
     * @see     PortCounterName
     *
     * @param name
     * @return  the value of the named counter
     ***********************************************************/
    public long getCounter(PortCounterName name)
    {
      long rtnval = 0L;
      if((port_counters != null) && (port_counters.length > 0) && !((Suppressed_Counters != null) && (Suppressed_Counters.size() > 0) && (Suppressed_Counters.contains(name))))
      {
         /* assume all or nothing array */
        rtnval = port_counters[name.ordinal()];
      }
     return rtnval; 
    }

    /************************************************************
     * Method Name:
     *  toCounterArrayString
     */
     /** Provides a crude string representation of all the counters.
     *
     * @return a crude string representation of all the counters
     ***********************************************************/
    public String toPFM_CounterString()
    {
      StringBuffer sbuff = new StringBuffer();

      if((port_counters != null) && (port_counters.length > 0))
      {
        /* iterate through the enum, and build a string */
        for (PortCounterName counter : PortCounterName.PFM_DATA_COUNTERS)
        {
          sbuff.append(counter + "=" + toUnsignedLongLongString(port_counters[counter.ordinal()]) + "\n");
        }
      }
     return sbuff.toString(); 
    }

    public String toPortIdString(String name, int maxLen)
    {
      int maxPortNumLen = 2;
      int maxNameLength = (maxLen > 4) && (maxLen < 40) ? maxLen: 16;
      
      String formatString = "%" + maxNameLength + "s %s:%" + maxPortNumLen + "d";
      
      // this should be a short, but complete identification string for the port
      StringBuffer sbuff = new StringBuffer();
      // first 10 characters of name
      if(name.length() > maxNameLength)
        name = name.substring(0,maxNameLength -1 );
      
      sbuff.append(String.format(formatString, name, getNodeGuid().toColonString(), getPortNumber()));

      return sbuff.toString();
    }
  
    public short getPortNumber()
    {
       return port_num;
    }

    public String toPFM_ID_String()
    {
     return ("Guid=" + new IB_Guid(node_guid).toColonString() + ", port=" + port_num); 
    }
  

    public String toPFM_ErrorString()
    {
      StringBuffer sbuff = new StringBuffer();

      if((port_counters != null) && (port_counters.length > 0))
      {
        /* iterate through the enum, and build a string */
        for (PortCounterName counter : PortCounterName.PFM_ERROR_COUNTERS)
        {
          if(port_counters[counter.ordinal()] != 0L)
            sbuff.append(counter + "=" + toUnsignedLongLongString(port_counters[counter.ordinal()]) + "\n");
        }
      }
     return sbuff.toString(); 
    }
  
    /************************************************************
     * Method Name:
     *  toCounterArrayString
     */
     /** Provides a crude string representation of all the counters.
     *
     * @return a crude string representation of all the counters
     ***********************************************************/
    public String toCounterArrayString()
    {
      StringBuffer sbuff = new StringBuffer();

      if((port_counters != null) && (port_counters.length > 0))
      {
        /* iterate through the enum, and build a string */
        for (PortCounterName counter : PortCounterName.values())
        {
          sbuff.append(counter + "=" + port_counters[counter.ordinal()] + "\n");
        }
      }
     return sbuff.toString(); 
    }

    /************************************************************
     * Method Name:
     *  getCounterTimeStamp
     */
    /**
     * Returns the value of counter_ts as a TimeStamp
     *
     * @return the counter_ts
     ***********************************************************/
    
    public TimeStamp getCounterTimeStamp()
    {
      return new TimeStamp(counter_ts * 1000);
    }
    

    /************************************************************
     * Method Name:
     *  getNodeGuid
     */
    /**
     * Returns the value of node_guid as an IB_Guid
     *
     * @return the node_guid
     ***********************************************************/
    
    public IB_Guid getNodeGuid()
    {
      return new IB_Guid(node_guid);
    }

    /************************************************************
     * Method Name:
     *  isLessThanUnsigned
    **/
    /**
     * Compares two unsigned longs to see if the first one is
     * less than the second one.  If so, returns true.
     *
     * @see     describe related java objects
     * @param n1
     * @param n2
     * @return
     ***********************************************************/
    public static boolean isLessThanUnsigned(long n1, long n2)
    {
      return (n1 < n2) ^ ((n1 < 0) != (n2 < 0));
    }

    /************************************************************
     * Method Name:
     *  isGreaterThanUnsigned
    **/
    /**
     * Compares two unsigned longs to see if the first one is
     * greater than the second one.  If so, returns true.
     *
     * @see     this{@link #isLessThanUnsigned(long, long)}
     * @param n1
     * @param n2
     * @return
     ***********************************************************/
    public static boolean isGreaterThanUnsigned(long n1, long n2)
    {
      return isLessThanUnsigned(n2, n1);
    }
    
    public static String toUnsignedLongLongString(long n1)
    {
      if(n1 < 0L)
      {
        /* its negative because it thinks its 2's compliment, so
         * need to undo that, ... compliment and add one
         */
        return convertUnsignedLongLongToBigInteger(n1).toString();
      }
       return Long.toString(n1);
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

    public static long convertBigIntegerToUnsignedLongLong(BigInteger bi)
    {
      // by definition, the result can not be negative
      //   FIXME: what do I with a negative big integer??
      //          * set it to zero for now, can't allow negatives
      
      if(bi.compareTo(BigInteger.ZERO) <= 0) return 0L;
      
      //   FIXME:  what do I do with a big integer too big for an
      //           unsigned long long?
      //           * return max value for unsigned long
      
      bi = bi.min(MAX_COUNTER_VALUE);
      return bi.longValue();
    }

    public static ArrayList <PFM_Port> getPortsWithErrors(ArrayList <PFM_Port> Ports)
    {
      java.util.ArrayList<PFM_Port> ports = new java.util.ArrayList<PFM_Port>();
      if((Ports != null) && (Ports.size() > 0))
      {
        // build up a new list with ports that have errors
        for(PFM_Port p: Ports)
        {
          if(p.hasError())
            ports.add(p);
        }
      }
      return ports;
    }

    public static String getPortDescription(ArrayList <PFM_Port> pL)
    {
      // assume this ArrayList are ports from the same node (common guid)
      if((pL != null) && (pL.size() > 0))
      {
        StringBuffer sbuff = new StringBuffer();
        short topPortNum = 0;
        PFM_Port p = pL.get(0);
        long total_errors = 0L;
        for(PFM_Port pe: pL)
        {
          total_errors += pe.getTotalErrors();
          topPortNum = topPortNum > pe.port_num ? topPortNum: pe.port_num;
        }
        
// FIXME this test does not work in all cases, need to determine if really a switch (a switch can have port 1
        /* does this look like a switch? */
        if((pL.size() > 2) || (topPortNum > 2))
          sbuff.append(OSM_NodeType.SW_NODE.getAbrevName());
        else
          sbuff.append(OSM_NodeType.CA_NODE.getAbrevName());
        
        sbuff.append("--guid=" + new IB_Guid(p.node_guid) + ", total # errors (ports+types)=" + Long.toString(total_errors));
        
        return sbuff.toString();
      }
      return null;
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
    public int compareTo(PFM_Port port)
    {
      
  // compare the perfmgr ports (guid & port_num)
      if(port == null)
        throw new NullPointerException();
      
      // compare port numbers first
      if(this.port_num != port.port_num)
        return (int)this.port_num - (int)port.port_num;
      
      // ports are the same, so compare guids
      if(this.node_guid != port.node_guid)
        return (this.node_guid - port.node_guid) > 0L ? 1: -1;
        
      // both the port number, and guids are the same
        return 0;
    }

    /************************************************************
     * Method Name:
     *  equals
    **/
    /**
     * Two Ports are the same if their port numbers and guids match
     *
     * @see java.lang.Object#equals(java.lang.Object)
    
     * @param   describe the parameters
     *
     * @return  describe the value returned
     * @param obj
     * @return
     ***********************************************************/
    
    @Override
    public boolean equals(Object obj)
    {
      return ((obj != null) && (obj instanceof PFM_Port) && (this.compareTo((PFM_Port)obj)==0));
    }
    
    /************************************************************
     * Method Name:
     *  getErrorTimeStamp
     */
    /**
     * Returns the value of error_ts as a TimeStamp
     *
     * @return the error_ts
     ***********************************************************/
    
    public TimeStamp getErrorTimeStamp()
    {
      return new TimeStamp(error_ts * 1000);
    }
    
    public boolean hasError()
    {
      // by default, ignore the suppressed counters
      return hasError(this.getSuppressed_Counters());
    }

    public boolean hasError(EnumSet<PortCounterName> suppressedCounters)
    {
      if(port_num > MAX_PORT_NUM)
        return false;
      
      // look through the error counters and return at the first instance of an error
      for(PFM_Port.PortCounterName n : PortCounterName.PFM_ERROR_COUNTERS)
      {
        if((suppressedCounters != null) && (suppressedCounters.size() > 0) && (suppressedCounters.contains(n)))
          continue;
        if(port_counters[n.ordinal()] != 0)
          return true;
      }
      return false;
    }

    public long getTotalErrors()
    {
      // by default, ignore the suppressed counters
      return getTotalErrors(this.getSuppressed_Counters());
    }

    public long getTotalErrors(EnumSet<PortCounterName> suppressedCounters)
    {
      long cum_errors = 0L;
      // look through the error counters and add up how many error types for this port
      for(PFM_Port.PortCounterName n : PortCounterName.PFM_ERROR_COUNTERS)
      {
        if((suppressedCounters != null) && (suppressedCounters.size() > 0) && (suppressedCounters.contains(n)))
          continue;

        cum_errors += port_counters[n.ordinal()] == 0 ? 0L: 1L;
      }
      return cum_errors;
    }

    public boolean hasTraffic()
    {
      // look through the traffic counters and return at the first instance of traffic
      for(PFM_Port.PortCounterName n : PortCounterName.PFM_DATA_COUNTERS)
        if(port_counters[n.ordinal()] != 0)
          return true;
      return false;
    }

    public boolean isStrange()
    {
      // look at the guid and port number to see if it makes sense
      if((port_num > MAX_PORT_NUM) || (port_num < MIN_PORT_NUM))
          return true;
      if(this.node_guid == 0L)
        return true;
      return false;
    }

    public String toErrorString()
    {
      StringBuffer stringValue = new StringBuffer();

      stringValue.append("node_guid=" + new IB_Guid(node_guid).toColonString() + ", port_num=" + port_num + "\n");
      for(PFM_Port.PortCounterName n : PortCounterName.PFM_ERROR_COUNTERS)
      {
        stringValue.append(", " +n.name() + "=" + toUnsignedLongLongString(port_counters[n.ordinal()] ));
      }
      return stringValue.toString();
    }
  
    public String toString()
    {
      return "PFM_Port [port_num=" + port_num + "\n\tnode_guid=" + new IB_Guid(node_guid).toColonString() + "\n\tcounter_ts="
          + new TimeStamp(counter_ts * 1000) + "\n\terror_ts=" + new TimeStamp(error_ts * 1000) + "\n\twait_ts=" + new TimeStamp(wait_ts * 1000) + "\n\tport_counters="
          + Arrays.toString(port_counters) + "]\n";
    }
  
}
