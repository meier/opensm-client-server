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
 *        file: OSM_FabricCollection.java
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
 * The OSM_FabricCollection can contain several instances of OSM_Fabric,
 * and provides mechanisms to compare instances with each other for
 * analysis purposes.  The primary intended use is to detect changes
 * over time, or compare an existing fabric with an ideal fabric.
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Mar 7, 2013 9:09:06 AM
 **********************************************************************/
public class OSM_FabricCollection implements Serializable, gov.llnl.lc.logging.CommonLogger
{
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = -3239444822451610727L;

  /** compress the object when serializing it to a file? **/
  private static boolean useCompression = true;
  
  /************************************************************
   * Method Name:
   *  OSM_FabricCollection
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param maxSize
   ***********************************************************/
  public OSM_FabricCollection(int maxSize)
  {
    super();
    setMaxSize(maxSize);
  }

  public OSM_FabricCollection()
  {
    this(0);
  }


  /* don't allow the collection to grow infinitely large, sliding window */
  public static final int MAX_COLLECTION_SIZE = 2000;
  
  private int MaxSize = MAX_COLLECTION_SIZE;

  /* keyed off fabric name and timestamp (insertion order is important) */
  private LinkedHashMap<String, OSM_Fabric> fabricsAll = new LinkedHashMap<String, OSM_Fabric>(MaxSize+1, .75F, false)
     {  
    /**  describe serialVersionUID here **/
      private static final long serialVersionUID = 5427649718728192370L;

    protected boolean removeEldestEntry(Map.Entry<String, OSM_Fabric> eldest)  
    {  
      return size() > MaxSize;                                    
    }  
     };    
  
//     public static String getOSM_FabricKey1(OSM_Fabric f)
//     {
//       // use the name and timestamp
//       if((f == null) || (f.getFabricName() == null) || (f.getTimeStamp() == null))
//         return null;
//       
//       return getOSM_FabricKey1(f.getFabricName(), f.getTimeStamp());      
//     }
//     
//  public static String getOSM_FabricKey1(String name, TimeStamp timeStamp)
//  {
//    return name + ": " + timeStamp.toString();   
//  }
//  
//  public static String getFabricNameFromKey1(String key)
//  {
//    if((key != null) && (key.length() > 20))
//      return key.substring(0, key.indexOf(": "));
//    return null;
//  }
//
//  public static TimeStamp getTimeStampFromKey1(String key)
//  {
//    if((key != null) && (key.length() > 20))
//      return new TimeStamp(key.substring(key.indexOf(": ") + 2, key.length()));
//    return null;
//  }
//
  public OSM_FabricDeltaCollection getOSM_FabricDeltaCollection()
  {
    // since an OSM_FabricDelta is simply the difference between two fabrics
    // a collection of deltas can be derived from a collection of fabrics
    
    if((fabricsAll != null) && (fabricsAll.size() > 1))
    {
      // iterate through the fabrics, and build a HashMap for the FabricDeltas
      OSM_FabricDeltaCollection fabricHistory = new OSM_FabricDeltaCollection();
      
      OSM_Fabric prevFabric = null;
      OSM_Fabric currFabric = null;
      for (Map.Entry<String, OSM_Fabric> entry : fabricsAll.entrySet())
      {
        currFabric = entry.getValue();
        if(prevFabric != null)
        {
          fabricHistory.put(new OSM_FabricDelta(prevFabric, currFabric));
        }
        prevFabric = currFabric;
      }
      return fabricHistory;
    }
    return null;
  }

  public LinkedHashMap<String, OSM_FabricDelta> getOSM_FabricDeltas()
  {
    return getOSM_FabricDeltaCollection().getOSM_FabricDeltas();
  }

  
  public LinkedHashMap<String, OSM_Fabric> getOSM_Fabrics()
  {
    return fabricsAll;
  }

  public OSM_Fabric getOSM_Fabric(String name, TimeStamp timeStamp)
  {
    return fabricsAll.get(OSM_Fabric.getOSM_FabricKey(name, timeStamp));
  }
  
  public int collectFabric(OsmSession ParentSession, int duration, TimeUnit durationUnitOfTime)
  {
    // return the number collected
    long durationInSeconds = TimeUnit.SECONDS.convert(duration, durationUnitOfTime);
    OSM_Fabric fabric = null;
    int sweepPeriod = 0;
    
    // if the collection is empty, get a new fabric
    // if the collection has at least one element, get the most current fabric
    
    if(getSize() > 0)
    {
      fabric = this.getCurrentOSM_Fabric();
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
      put(fabric);
      return collectFabric(ParentSession, (int)(durationInSeconds/sweepPeriod));
    }
    return 0;
  }
  
  public int collectFabric(OsmSession ParentSession, int numberToCollect)
  {
    // return the number collected
    int numCollected       = 0;
    
    if(ParentSession != null)
    {
    numCollected = getSize();
    numberToCollect = numberToCollect < numCollected ? numCollected: numberToCollect;
    numberToCollect = numberToCollect > getMaxSize() ? getMaxSize(): numberToCollect;
    
    OSM_Fabric fabric = null;
    // how often does the perfmgr sweep? get it at that rate
    int sweepPeriod = 0;
    
    logger.info("Start Collecting " + numberToCollect + " instances of the fabric (start: " + new TimeStamp() + ")");
    
      do
      {
        // grab an instance of the fabric
        OpenSmMonitorService oms = OpenSmMonitorService.getOpenSmMonitorService(ParentSession);
        fabric = oms.getFabric();
        if (fabric != null)
        {
          sweepPeriod = fabric.getPerfMgrSweepSecs();
          put(fabric);
          numCollected = getSize();  // actual size, let the collection keep track for us
          System.err.println(" " + numCollected);
          // collect another instance after the designated sweep period
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

      logger.info("Done  Collecting " + numberToCollect + " instances of the fabric (end: " + new TimeStamp() + ")");
    }
    else
      logger.severe("Can't collect the fabric without a valid session to the service");
    return numCollected;
  }
  
  public OSM_Fabric getOldestOSM_Fabric()
  {
    // return the oldest, or bottom of the stack
    Object [] fa = fabricsAll.values().toArray();
    return (OSM_Fabric)fa[0];
  }
  
  public OSM_Fabric getCurrentOSM_Fabric()
  {
    // return the most recent, or top of the stack
    Object [] fa = fabricsAll.values().toArray();
    return (OSM_Fabric)fa[fa.length-1];
  }
  
  public OSM_FabricDelta getCurrentOSM_FabricDelta()
  {
    // construct and return an OSM_FabricDelta
    // using the two most recent OSM_Fabrics
    
    // a delta needs at least two Fabrics, if two do not
    // exits in this collection, then null is returned.
    OSM_Fabric [] fa = this.getRecentOSM_Fabrics(2);
    if((fa == null) || (fa.length != 2))
      return null;
    
    return new OSM_FabricDelta(fa[0], fa[1]);
  }
  
  public OSM_Fabric [] getRecentOSM_Fabrics(int num)
  {
    // return the most recent ones, or top of the stack
    Object [] oa = fabricsAll.values().toArray();
    if((oa == null) || (oa.length < 1))
      return null;
    
    int size = (oa.length > num) ? num: oa.length;
    OSM_Fabric [] ffa = new OSM_Fabric[size];
    
    // keep the order
    for(int n=0; n < size; n++)
      ffa[n] = (OSM_Fabric)oa[(oa.length -size)+n];
    return ffa;
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
  
  public OSM_Fabric put(OSM_Fabric fabric)
  {
    return put(OSM_Fabric.getOSM_FabricKey(fabric), fabric);
  }
  
  public OSM_Fabric put(String key, OSM_Fabric fabric)
  {
    return fabricsAll.put(key, fabric);
  }
  
  public String getKeys()
  {
    return fabricsAll.keySet().toString();
  }
  

  public static OSM_FabricCollection readFabricCollection(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException
  {
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
    
    OSM_FabricCollection obj = (OSM_FabricCollection) objectInputStream.readObject();
    
    objectInputStream.close();
    if(useCompression)
      in.close();
    fileInput.close();
    return obj;
  }
  
  public static void writeFabricCollection(String fileName, OSM_FabricCollection fabricHistory) throws IOException
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
    
    objectOutput.writeObject(fabricHistory);
    objectOutput.flush();
    objectOutput.close();
    if(useCompression)
      out.close();
    fileOutput.close();
    return;
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
   * @throws IOException 
   ***********************************************************/
  public static void main(String[] args) throws IOException
  {
    // establish a connection
    logger.info("Opening the OMS Session");
    OsmSession ParentSession = null;

    /* the one and only OsmServiceManager */
    OsmServiceManager OsmService = OsmServiceManager.getInstance();

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
      OSM_FabricCollection fabricHistory = new OSM_FabricCollection();
//      OSM_FabricCollection fabricHistory = new OSM_FabricCollection(3);
//      fabricHistory.collectFabric(ParentSession, 3);
//      fabricHistory.collectFabric(ParentSession, 5);
      fabricHistory.collectFabric(ParentSession, 10, TimeUnit.MINUTES);
      
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
      OSM_FabricCollection.writeFabricCollection("/home/meier3/.smt/BigFabic.cache", fabricHistory);

      System.err.println("done");
    }
    else
    {
      logger.severe("Could not establish an OMS session");
    }

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
  
  public String toInfo()
  {
    StringBuffer stringValue = new StringBuffer();
    stringValue.append(OSM_FabricCollection.class.getSimpleName() + "\n");
    
    stringValue.append("fabric name:             " + this.getOldestOSM_Fabric().getFabricName() + "\n");
    stringValue.append("first timestamp:         " + this.getOldestOSM_Fabric().toTimeString() + "\n");
    stringValue.append("last timestamp:          " + this.getCurrentOSM_Fabric().toTimeString() + "\n");
    stringValue.append("# secs between records:  " + this.getOldestOSM_Fabric().getPerfMgrSweepSecs() + "\n");
    stringValue.append("# records in collection: " + getSize() + "\n");
    stringValue.append("# nodes:                 " + this.getOldestOSM_Fabric().getOSM_Nodes().size() + "\n");
    stringValue.append("# ports:                 " + this.getOldestOSM_Fabric().getOSM_Ports().size() + "\n");
    stringValue.append("# links:                 " + this.getOldestOSM_Fabric().getIB_Links().size());
  
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
    for (Map.Entry<String, OSM_Fabric> entry : fabricsAll.entrySet())
    {
      if(!initial)
        buff.append("\n");
      else
        initial = false;
      
      OSM_Fabric f = entry.getValue();
      buff.append(f.toTimeString());
    }
    return buff.toString();
  }

}
