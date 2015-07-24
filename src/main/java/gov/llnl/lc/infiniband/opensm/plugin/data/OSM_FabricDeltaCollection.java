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
 *        file: OSM_FabricDeltaCollection.java
 *
 *  Created on: Mar 7, 2013
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import gov.llnl.lc.infiniband.opensm.plugin.net.OsmServiceManager;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmSession;
import gov.llnl.lc.time.TimeStamp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**********************************************************************
 * The OSM_FabricDeltaCollection can contain several instances of an
 * OSM_FabricDelta object.  Typically, the collection is a time series,
 * and this collection is used to visualize and analyze changes over
 * time.  Since each OSM_FabricDelta contains two OSM_Fabric objects,
 * a collection (time series) of OSM_Fabrics can also be obtained from
 * this collection.
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Mar 7, 2013 9:09:06 AM
 **********************************************************************/
public class OSM_FabricDeltaCollection implements Serializable, gov.llnl.lc.logging.CommonLogger
{

  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = 3511158699682779011L;

  /** the max/min of all port counters, over the entire collection **/
  private PFM_PortChangeRange portRanges = null;
  
  /************************************************************
   * Method Name:
   *  OSM_FabricDeltaCollection
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param maxSize
   ***********************************************************/
  public OSM_FabricDeltaCollection()
  {
    this(0);
  }

  public OSM_FabricDeltaCollection(int maxSize)
  {
    super();
    setMaxSize(maxSize);
  }

  /* don't allow the collection to grow infinitely large, sliding window */
  public static final int MAX_COLLECTION_SIZE = 2000;
  
  private int MaxSize = MAX_COLLECTION_SIZE;
  
  /** compress the object when serializing it to a file? **/
  private static boolean useCompression = true;
  
  private int AveDeltaSeconds = 0;


  /* keyed off fabric name and timestamp (insertion order is important) */
  private LinkedHashMap<String, OSM_FabricDelta> fabricsAll = new LinkedHashMap<String, OSM_FabricDelta>(MaxSize+1, .75F, false)
     {  
    /**  describe serialVersionUID here **/
      private static final long serialVersionUID = 9035519111182529621L;

    protected boolean removeEldestEntry(Map.Entry<String, OSM_FabricDelta> eldest)  
    {  
      return size() > MaxSize;                                    
    }  
     };    
  
     public static String getOSM_FabricDeltaKey(OSM_FabricDelta fd)
     {
       // use the name and timestamp
       if((fd == null) || (fd.getFabricName() == null) || (fd.getTimeStamp() == null))
         return null;
       
       return getOSM_FabricDeltaKey(fd.getFabricName(), fd.getTimeStamp());      
     }
     
     /************************************************************
      * Method Name:
      *  getAveDeltaSeconds
      **/
     /**
      * Returns the value of aveDeltaSeconds
      *
      * @return the aveDeltaSeconds
      *
      ***********************************************************/
     
     public int getAveDeltaSeconds()
     {
       return AveDeltaSeconds;
     }

     public static String getOSM_FabricDeltaKey(String name, TimeStamp timeStamp)
  {
    return name + ": " + timeStamp.toString();   
  }
  
  public LinkedHashMap<String, OSM_FabricDelta> getOSM_FabricDeltas()
  {
    return fabricsAll;
  }

  public LinkedHashMap<String, OSM_Fabric> getOSM_Fabrics()
  {
    // the OSM_FabricDeltaCollection contains OSM_FabricDeltas, which in turn contain OSM_Fabrics
    // so this is just a convenience function to get those
    if((fabricsAll != null) && (fabricsAll.size() > 0))
    {
      // iterate through the Deltas, and build a HashMap for the Fabrics
      OSM_FabricCollection fabricHistory = new OSM_FabricCollection();
      
      // start with the oldest fabric, then add the rest
      fabricHistory.put(this.getOldestOSM_FabricDelta().getFabric1());
      
      for (Map.Entry<String, OSM_FabricDelta> entry : fabricsAll.entrySet())
      {
        OSM_FabricDelta delta = entry.getValue();
        
        // each delta has two fabrics, prev & current, safe to just add the 2nd (avoid redundancy)
        fabricHistory.put(delta.getFabric2());
      }
      return fabricHistory.getOSM_Fabrics();
    }
    return null;
  }

  public OSM_FabricDelta getOSM_FabricDelta(String name, TimeStamp timeStamp)
  {
    return fabricsAll.get(getOSM_FabricDeltaKey(name, timeStamp));
  }
  
  public OSM_FabricDelta getOldestOSM_FabricDelta()
  {
    // return the oldest, or bottom of the stack
    Object [] fa = fabricsAll.values().toArray();
    return (OSM_FabricDelta)fa[0];
  }
  
  public OSM_FabricDelta getCurrentOSM_FabricDelta()
  {
    // return the most recent, or top of the stack
    Object [] fa = fabricsAll.values().toArray();
    return (OSM_FabricDelta)fa[fa.length-1];
  }
  
  public OSM_FabricDelta getOSM_FabricDelta(int ndex)
  {
    // return the specified index
    Object [] fa = fabricsAll.values().toArray();
    
    // throw ArrayIndexOutOfBounds exception, or return null?
    if((fa != null) && (fa.length > ndex))
      return (OSM_FabricDelta)fa[ndex];
    return null;
  }
  

  
  public OSM_FabricDelta [] getRecentOSM_FabricDeltas(int num)
  {
    // return the most recent ones, or top of the stack
    Object [] oa = fabricsAll.values().toArray();
    if((oa == null) || (oa.length < 1))
      return null;
    
    int size = (oa.length > num) ? num: oa.length;
    OSM_FabricDelta [] ffa = new OSM_FabricDelta[size];
    
    // keep the order
    for(int n=0; n < size; n++)
      ffa[n] = (OSM_FabricDelta)oa[(oa.length -size)+n];
    return ffa;
  }
  
  /************************************************************
   * Method Name:
   *  getRangeOfChanges
  **/
  /**
   * Returns the object that contains the max/min values of all
   * the port counters, for the entire collection.  This is useful
   * for setting limits, boundries, etc. for alerts or for plotting.
   * 
   * Since the collection is mutable, each time this value is accessed
   * it is recalculated.  This can be time-consuming if the collection
   * is large, or if it is excessively accessed.
   *
   * @see     describe related java objects
   *
   * @return
   ***********************************************************/
  public PFM_PortChangeRange getRangeOfChanges()
  {
    // always make sure its up to date
    this.calculatePortChangeRanges();
    return portRanges;
  }
  
  public static boolean recordHistory(String host, String port, int duration, TimeUnit durationUnitOfTime, String fileName, boolean showInfo) throws IOException 
  {
    // establish a connection
    logger.info("OSM_FD: Opening the OMS Session");
    OsmSession ParentSession = null;

    /* the one and only OsmServiceManager */
    OsmServiceManager OsmService = OsmServiceManager.getInstance();
    
    try
    {
      ParentSession = OsmService.openSession(host, port, null, null);
    }
    catch (Exception e)
    {
      logger.severe(e.getStackTrace().toString());
      System.exit(-1);
    }

    if (ParentSession != null)
    {
      OSM_FabricDeltaCollection fabricHistory = new OSM_FabricDeltaCollection();
      fabricHistory.collectFabricDelta(ParentSession, duration, durationUnitOfTime, fileName);
      
      /* done with session, so close it and return the info */
      try
      {
        OsmService.closeSession(ParentSession);
      }
      catch (Exception e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      if(showInfo)
        System.out.println(fabricHistory.toInfo());
    }
    else
    {
      logger.severe("Could not establish an OMS session");
    }
    return true;    
  }


  
  public int collectFabricDelta(OsmSession ParentSession, int duration, TimeUnit durationUnitOfTime)
  {
    int rtn = 0;
    try
    {
      rtn = collectFabricDelta(ParentSession, duration, durationUnitOfTime, (String)null);
    }
    catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return rtn;
  }
  
  public int collectFabricDelta(OsmSession ParentSession, int duration, TimeUnit durationUnitOfTime, String fileName) throws IOException
  {
    // return the number collected
    
    // if the unit of time is null, assume duration is really number of records to collect
    if(durationUnitOfTime == null)
      return collectFabricDelta(ParentSession, duration, fileName);
    
    long durationInSeconds = TimeUnit.SECONDS.convert(duration, durationUnitOfTime);
    OSM_FabricDelta fabricDelta = null;
    OSM_Fabric           fabric = null;
    int sweepPeriod = 0;
    
    // if the collection is empty, get a new fabric
    // if the collection has at least one element, get the most current fabric
    
    if(getSize() > 0)
    {
      fabricDelta = this.getCurrentOSM_FabricDelta();
      if(fabricDelta != null)
        fabric = fabricDelta.getFabric2();
    }
    else
    {
      OpenSmMonitorService oms = OpenSmMonitorService.getOpenSmMonitorService(ParentSession);
      fabric = oms.getFabric();
    }
    
    // from the fabric, calculate the number to collect, and then collect
    if (fabric != null)
    {
      sweepPeriod = fabric.getPerfMgrSweepSecs();
      return collectFabricDelta(ParentSession, (int)(durationInSeconds/sweepPeriod), fileName);
    }
    return 0;
  }
  
  public int collectFabricDelta(OsmSession ParentSession, int numberToCollect)
  {
    int rtn = 0;
    try
    {
      rtn = collectFabricDelta(ParentSession, numberToCollect, (String)null);
    }
    catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return rtn;
  }

  
  public int collectFabricDelta(OsmSession ParentSession, int numberToCollect, String fileName) throws IOException
  {
    // return the number collected
    int numCollected       = 0;
    
    if(ParentSession != null)
    {
    numCollected = getSize();
    numberToCollect = numberToCollect < numCollected ? numCollected: numberToCollect;
    numberToCollect = numberToCollect > getMaxSize() ? getMaxSize(): numberToCollect;
    
    OSM_Fabric prevFabric = null;
    OSM_Fabric currFabric = null;
    // how often does the perfmgr sweep? get it at that rate
    int sweepPeriod = 0;
    
      do
      {
        // grab an instance of the fabric (we need two to create a delta)
        OpenSmMonitorService oms = OpenSmMonitorService.getOpenSmMonitorService(ParentSession);
        currFabric = oms.getFabric();
        if (currFabric != null)
        {
          if(sweepPeriod == 0)
            sweepPeriod = currFabric.getPerfMgrSweepSecs();
          
          if(prevFabric != null)
          {
            put(new OSM_FabricDelta(prevFabric, currFabric));
            numCollected = getSize();  // actual size, let the collection keep track for us
            
            // if a file name was provided, save the results thus far (over write)
            if(fileName != null)
            {
              logger.info("Saving Collection Results thus far: " + numCollected + " of " + numberToCollect + " instances of the (delta) fabric");
              OSM_FabricDeltaCollection.writeFabricDeltaCollection(fileName, this);
            }
          }
          // save the current AS the previous, and go get another fabric instance when its available
          prevFabric = currFabric;
          try
          {
            TimeUnit.SECONDS.sleep(sweepPeriod -2); // sample slightly faster than sweep period, so we don't miss any
          }
          catch (InterruptedException e)
          {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      } while (numCollected < numberToCollect);

      logger.info("Done  Collecting " + numberToCollect + " instances of the (delta) fabric (end: " + new TimeStamp() + ")");
    }
    else
      logger.severe("Can't collect the fabric without a valid session to the service");
    return numCollected;
  }

  /************************************************************
   * Method Name:
   *  getMaxSize
   **/
  /**
   * Returns the value of maxSize, which represents the maximum
   * number of fabrics in this collection.  If fabrics are added
   * to a collection at its maximum size, then old ones are removed
   * to make room.
   *
   * @return the maxSize
   *
   ***********************************************************/
  
  public int getMaxSize()
  {
    return MaxSize;
  }

  public int getSize()
  {
    if(fabricsAll != null)
      return fabricsAll.size();
    return 0;
  }

  /************************************************************
   * Method Name:
   *  setMaxSize
   **/
  /**
   * Sets the value of maxSize, and enforces overall min and max
   *
   * @param maxSize the maxSize to set
   *
   ***********************************************************/
  public void setMaxSize(int maxSize)
  {
    maxSize = maxSize > MAX_COLLECTION_SIZE ? MAX_COLLECTION_SIZE: maxSize;
    MaxSize = maxSize < 2 ? MAX_COLLECTION_SIZE: maxSize;
    
    //TODO:  check to see if the "new" MaxSize is smaller than the current
    // collection size, and prune old fabrics to make sure the collection
    // is never too big.
  }
  
  public OSM_FabricDelta putAll(OSM_FabricDeltaCollection fabricHistory)
  {
    OSM_FabricDelta rF = null;
    
    // add this collection to the existing one, just append
    if((fabricHistory != null) && (fabricHistory.getSize() > 0))
    {
      OSM_FabricDelta hfabric = fabricHistory.getOldestOSM_FabricDelta();
      OSM_FabricDelta fabric  = this.getOldestOSM_FabricDelta();
      
      if((hfabric != null) && (hfabric.getFabricName() != null))
      {
        // they should be the same fabric, but its okay to append to an
        // empty one.
        if((fabric == null) || (hfabric.getFabricName().equals(fabric.getFabricName())))
        {
          // okay to proceed
          LinkedHashMap<String, OSM_FabricDelta> fh = fabricHistory.getOSM_FabricDeltas();
          // iterate through the supplied collection, and just add them
          for (Map.Entry<String, OSM_FabricDelta> deltaMapEntry : fh.entrySet())
          {
            rF = put(deltaMapEntry.getValue());
          }
        }
      }
    }
    return rF;
  }
  
  public OSM_FabricDelta put(OSM_FabricDelta fabric)
  {
    return put(getOSM_FabricDeltaKey(fabric), fabric);
  }
  
  public OSM_FabricDelta put(String key, OSM_FabricDelta fabric)
  {
    // keep a running total of the average delta time
    // update the average
    int prevSize = fabricsAll.size();
    OSM_FabricDelta fd = fabricsAll.put(key, fabric);
    int currSize = fabricsAll.size();
    
    // if the size changed, then it was added
    if((prevSize != 0) && (prevSize != currSize))
    {
      int totTime = (this.getAveDeltaSeconds() * prevSize) + fabric.getDeltaSeconds();
      AveDeltaSeconds = totTime/currSize; 
    }
    return fd;
  }
  
  public String getKeys()
  {
    return fabricsAll.keySet().toString();
  }
  

  public static OSM_FabricDeltaCollection readFabricDeltaCollection(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException
  {
    // support normal OSM_FabricDeltaCollection files, OMS_Collection, and normal FabricCollection files
    
    OSM_FabricDeltaCollection ofdc = readFabricDeltaCollectionOnly(fileName);
    
    // try OMS_Collection if previous failed
    if(ofdc == null)
    {
      OMS_Collection oc = OMS_Collection.readOMS_Collection(fileName);
      if(oc != null)
        ofdc = oc.getOSM_FabricDeltaCollection();
    }
    
    // try FabricCollection if previous failed
    if(ofdc == null)
    {
      OSM_FabricCollection fc = OSM_FabricCollection.readFabricCollection(fileName);
      if(fc != null)
        ofdc = fc.getOSM_FabricDeltaCollection();
    }
    
    return ofdc;
  }
  
  public static OSM_FabricDeltaCollection readFabricDeltaCollectionOnly(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException
  {
    // support only FabricDeltaCollection files (return null otherwise)
    FileInputStream fileInput = new FileInputStream(fileName);
    ObjectInputStream objectInputStream = null;
    GZIPInputStream in = null;
    
    if(useCompression)
    {
      in = new GZIPInputStream(fileInput);
      objectInputStream = new ObjectInputStream(in);
    }
    else
      objectInputStream = new ObjectInputStream(fileInput);
    
    OSM_FabricDeltaCollection ofdc = null;
    Object unknownObject = objectInputStream.readObject();
    if(unknownObject instanceof OSM_FabricDeltaCollection)
      ofdc = (OSM_FabricDeltaCollection) unknownObject;

    objectInputStream.close();
    if(useCompression)
      in.close();
    fileInput.close();

    return ofdc;
  }
  
  public static void writeFabricDeltaCollection(String fileName, OSM_FabricDeltaCollection fabricDeltaHistory) throws IOException
  {
    File fabricFile = new File(fileName);
    fabricFile.getParentFile().mkdirs();
    FileOutputStream fileOutput = new FileOutputStream(fabricFile);
    
    ObjectOutputStream objectOutput = null;
    GZIPOutputStream out = null;
    
    if(useCompression)
    {
      out =  new GZIPOutputStream(fileOutput);
      objectOutput = new ObjectOutputStream(out);
    }
    else
      objectOutput = new ObjectOutputStream(fileOutput);
    
    objectOutput.writeObject(fabricDeltaHistory);
    objectOutput.flush();
    objectOutput.close();
    if(useCompression)
      out.close();
    fileOutput.close();
    return;
  }
  
  public static void writeFabricDeltaCollection(String fileName, OSM_FabricDeltaCollection fabricDeltaHistory, boolean append) throws ClassNotFoundException, IOException
  {
    // appending a collection to an existing collection involves reading the existing collection,
    // adding the new stuff, then
    // writing the result back out.
    
    OSM_FabricDeltaCollection history = null;
    if(append)
    {
      // attempt to read the file
      try
      {
        history = readFabricDeltaCollection(fileName);
      }
      catch (FileNotFoundException e)
      {
        // if there is no file to append to, continue, just create it
        history = new OSM_FabricDeltaCollection();
      }
      catch (IOException e)
      {
        logger.severe(e.getLocalizedMessage());
        return;
      }
      // append our history to this
      history.putAll(fabricDeltaHistory);
    }
    else
      history = fabricDeltaHistory;
    
    // now write it all out
    writeFabricDeltaCollection(fileName, history);
    return;
  }
  
  private PFM_PortChangeRange calculatePortChangeRanges()
  {
    // loop through all the ports in the entire collection and calculate changes
    /**
     * portRanges() - for each port counter, its min and max over the entire
     * collection
     */

    // simply assume all ports change, just update the portRanges()
    /* keyed off port guid+port_num */
    LinkedHashMap<String, PFM_PortChange> portsWithChange = new LinkedHashMap<String, PFM_PortChange>();

    long counter = 0L;

    // iterate through the collection, find the min and max of every counter
    for (Map.Entry<String, OSM_FabricDelta> deltaMapEntry : fabricsAll.entrySet())
    {
      // only look through the ports with change
      LinkedHashMap<String, PFM_PortChange> portChanges = deltaMapEntry.getValue().getPortsWithChange();
      for (Map.Entry<String, PFM_PortChange> changeMapEntry : portChanges.entrySet())
      {
        portsWithChange.put(changeMapEntry.getKey(), changeMapEntry.getValue());
      }
      if (counter == 0)
      {
        portRanges = new PFM_PortChangeRange(portsWithChange);
      }
      counter++;
      portRanges.updateRange(portsWithChange);

    }
    return portRanges;
  }

  public void showRangeOfChanges()
  {
    System.err.println("Range of all changes:");
    System.err.println(getRangeOfChanges().toRangeOfChangeString());
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
  public static void main(String[] args) throws Exception
  {
    // establish a connection
    logger.info("Opening the OMS Session");
    OsmSession ParentSession = null;

    /* the one and only OsmServiceManager */
    OsmServiceManager OsmService = OsmServiceManager.getInstance();
    
    String fileName = "/home/meier3/.smt/DayFabricDeltaCollection.cache";

    try
    {
      ParentSession = OsmService.openSession("localhost", "10011", null, null);
    }
    catch (Exception e)
    {
      logger.severe(e.getStackTrace().toString());
      System.exit(-1);
    }

    if (ParentSession != null)
    {
      OSM_FabricDeltaCollection fabricHistory = new OSM_FabricDeltaCollection();
//      fabricHistory.collectFabricDelta(ParentSession, 3);
//      fabricHistory.collectFabricDelta(ParentSession, 5);
      fabricHistory.collectFabricDelta(ParentSession, 24, TimeUnit.HOURS, fileName);
//      fabricHistory.collectFabricDelta(ParentSession, 24, TimeUnit.MINUTES, fileName);
      
      /* done with session, so close it and return the info */
      try
      {
        OsmService.closeSession(ParentSession);
      }
      catch (Exception e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      System.err.println(fabricHistory.toString());
//      OSM_FabricDeltaCollection.writeFabricDeltaCollection(fileName, fabricHistory);
      System.err.println("done");
    }
    else
    {
      logger.severe("Could not establish an OMS session");
    }

  }

  /************************************************************
   * Method Name:
   *  toInfo
  **/
  /**
   * Describe the method here
   *
   * @see java.lang.Object#toString()
   *
   * @return
   ***********************************************************/
  
  public String toInfo()
  {
    StringBuffer stringValue = new StringBuffer();
    stringValue.append(OSM_FabricDeltaCollection.class.getSimpleName() + "\n");
    
    stringValue.append("fabric name:             "  + this.getOldestOSM_FabricDelta().getFabricName() + "\n");
    stringValue.append("first timestamp:         " + this.getOldestOSM_FabricDelta().getFabric1().toTimeString() + "\n");
    stringValue.append("last timestamp:          " + this.getCurrentOSM_FabricDelta().getFabric1().toTimeString() + "\n");
    stringValue.append("# secs between records:  " + this.getOldestOSM_FabricDelta().getAgeDifference(TimeUnit.SECONDS) + "\n");
    stringValue.append("# records in collection: " + getSize() + "\n");
    stringValue.append("# nodes:                 " + this.getOldestOSM_FabricDelta().getFabric1().getOSM_Nodes().size() + "\n");
    stringValue.append("# ports:                 " + this.getOldestOSM_FabricDelta().getFabric1().getOSM_Ports().size() + "\n");
    stringValue.append("# links:                 " + this.getOldestOSM_FabricDelta().getFabric1().getIB_Links().size());
  
    return stringValue.toString();
  }

  
  /************************************************************
   * Method Name:
   *  toTimeString
  **/
  /**
   * Returns a list of TimeStamps for this object
   *
   * @see     describe related java objects
   *
   * @return
   ***********************************************************/
  public String toTimeString()
  {
    StringBuffer buff = new StringBuffer();
    boolean initial = true;
    for (Map.Entry<String, OSM_FabricDelta> entry : fabricsAll.entrySet())
    {
      if(!initial)
        buff.append("\n");
      else
        initial = false;
      
      OSM_FabricDelta fd = entry.getValue();
      buff.append(fd.toTimeString());
    }
    return buff.toString();
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
  
  @Override
  public String toString()
  {
    StringBuffer stringValue = new StringBuffer();
    stringValue.append(OSM_FabricDeltaCollection.class.getSimpleName() + "\n");
    
    stringValue.append("fabric name:     "  + this.getOldestOSM_FabricDelta().getFabricName() + "\n");
    stringValue.append("first timestamp: " + this.getOldestOSM_FabricDelta().getFabric1().toTimeString() + "\n");
    stringValue.append("last timestamp:  " + this.getCurrentOSM_FabricDelta().getFabric1().toTimeString() + "\n");
    stringValue.append("number:          " + getSize());
  
    return stringValue.toString();
  }


}
