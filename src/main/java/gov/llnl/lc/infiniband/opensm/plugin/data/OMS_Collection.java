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
 *        file: OMS_Collection.java
 *
 *  Created on: June 27, 2013
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import gov.llnl.lc.infiniband.opensm.plugin.OsmConstants;
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
 * Describe purpose and responsibility of OMS_Collection
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Jun 28, 2013 4:38:18 PM
 **********************************************************************/
public class OMS_Collection implements Serializable, OsmConstants, gov.llnl.lc.logging.CommonLogger
{

  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = 8489153020653218925L;

  /************************************************************
   * Method Name:
   *  OMS_Collection
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param maxSize
   ***********************************************************/
  public OMS_Collection(int maxSize)
  {
    super();
    setMaxSize(maxSize);
  }

  public OMS_Collection()
  {
    this(0);
  }


  /* don't allow the collection to grow infinitely large, sliding window */
  public static final int MAX_COLLECTION_SIZE = 500;
  
  private int MaxSize = MAX_COLLECTION_SIZE;
  
  private static boolean stopAppending = false;
  
  /** compress the object when serializing it to a file? **/
  private static boolean useCompression = true;
    
  /** a list of Listeners, interested in knowing when the size of the collection changes**/
  private static java.util.ArrayList <OMS_CollectionChangeListener> Collection_Listeners =
    new java.util.ArrayList<OMS_CollectionChangeListener>();
    
    private OpenSmMonitorService NewestOMS = null;
    private OpenSmMonitorService InitialOMS = null;
    private String FabricName = null;
 
    /************************************************************
     * Method Name:
     *  getNewestOMS
     **/
    /**
     * Returns the value of newestOMS
     *
     * @return the newestOMS
     *
     ***********************************************************/
    
    public OpenSmMonitorService getNewestOMS()
    {
      return NewestOMS;
    }

    /************************************************************
     * Method Name:
     *  getInitialOMS
     **/
    /**
     * Returns the value of initialOMS
     *
     * @return the initialOMS
     *
     ***********************************************************/
    
    public OpenSmMonitorService getInitialOMS()
    {
      return InitialOMS;
    }


    private int AveDeltaSeconds = 0;

    
    /************************************************************
     * Method Name:
     *  getFabricName
     **/
    /**
     * Returns the value of fabricName
     *
     * @return the fabricName
     *
     ***********************************************************/
    
    public String getFabricName()
    {
      return FabricName;
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


  //
  /* keyed off fabric name and timestamp (insertion order is important) */
  private LinkedHashMap<String, OpenSmMonitorService> omsHistory = new LinkedHashMap<String, OpenSmMonitorService>(MaxSize+1, .75F, false)
  {  
    /**  describe serialVersionUID here **/
    private static final long serialVersionUID = 5427649718728192370L;

    protected boolean removeEldestEntry(Map.Entry<String, OpenSmMonitorService> eldest)  
    {  
      return size() > MaxSize;                                    
    }  
  }; 
     
     
     
     public synchronized int getNumListeners()
     {
       return Collection_Listeners.size();
     }
      
  public synchronized boolean addOMS_CollectionChangeListener(OMS_CollectionChangeListener listener)
  {
    // add the listener, and its set of events
    if(listener != null)
    {
      Collection_Listeners.add(listener);
      
      // update it with the first one
      try
      {
        listener.osmCollectionUpdate(this, getOldestOMS(), false);
      }
      catch (Exception e)
      {
        // TODO Auto-generated catch block
        System.err.println("Could not update the collection listener after adding it");
      }
    }
    return true;
  }


  public synchronized boolean removeOMS_CollectionChangeListener(OMS_CollectionChangeListener listener)
  {
    if (Collection_Listeners.remove(listener))
    {
     }
    return true;
  }

  /**************************************************************************
  *** Method Name:
  ***     updateAllListeners
  ***
  **/
  /**
  *** Notifies all listeners that the collection has changed.
  *** <p>
   * @throws Exception 
  ***
  **************************************************************************/
  public synchronized void updateAllListeners(OpenSmMonitorService oms) throws Exception
  {
    for( int i = 0; i < Collection_Listeners.size(); i++ )
    {
      OMS_CollectionChangeListener listener = (OMS_CollectionChangeListener)Collection_Listeners.get( i );
      if(listener != null)
        listener.osmCollectionUpdate(this, oms, true);
    }
  }
 
  
     public static String getOMS_Key(OpenSmMonitorService oms)
     {
       // use the name and timestamp
       if((oms == null) || (oms.getFabric() == null))
         return null;
       
       return oms.getKey();
       
//       return getOMS_Key(oms.getFabric().getFabricName(), oms.getFabric().getTimeStamp());      
     }
     
//     public static String getOMS_Key(String name, TimeStamp ts)
//     {
//       return OSM_FabricCollection.getOSM_FabricKey(name, ts);      
//     }
//     
     public LinkedHashMap<String, OSM_FabricDelta> getOSM_FabricDeltas()
     {
          return getOSM_FabricDeltaCollection().getOSM_FabricDeltas();
     }

     
     public OSM_FabricDeltaCollection getOSM_FabricDeltaCollection()
     {
       // since an OSM_FabricDelta is simply the difference between two fabrics
       // a collection of deltas can be derived from a collection of fabrics
       
       if((omsHistory != null) && (omsHistory.size() > 1))
       {
         // iterate through the fabrics, and build a HashMap for the FabricDeltas
         OSM_FabricDeltaCollection fabricHistory = new OSM_FabricDeltaCollection();
         
         OSM_Fabric prevFabric = null;
         OSM_Fabric currFabric = null;
         for (Map.Entry<String, OpenSmMonitorService> entry : omsHistory.entrySet())
         {
           currFabric = entry.getValue().getFabric();
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

     
  public LinkedHashMap<String, OpenSmMonitorService> getOSM_History()
  {
    return omsHistory;
  }

  public OpenSmMonitorService getOMS(String name, TimeStamp timeStamp)
  {
    return omsHistory.get(OpenSmMonitorService.getOMS_Key(name, timeStamp));
  }
  
  public int collectOMS(OsmSession ParentSession, int duration, TimeUnit durationUnitOfTime)
  {
    // return the number collected
    long durationInSeconds = TimeUnit.SECONDS.convert(duration, durationUnitOfTime);
    OpenSmMonitorService oms = null;
    int sweepPeriod = 0;
    
    // if the collection is empty, get a new fabric
    // if the collection has at least one element, get the most current fabric
    
    if(getSize() > 0)
    {
      oms = this.getCurrentOMS();
    }
    else
    {
      oms = OpenSmMonitorService.getOpenSmMonitorService(ParentSession);
    }
    
    // from the fabric, calculate the number to collect, and then collect
    if (oms != null)
    {
      sweepPeriod = oms.getFabric().getPerfMgrSweepSecs();
      put(oms);
      return collectOMS(ParentSession, (int)(durationInSeconds/sweepPeriod));
    }
    return 0;
  }
  
  public int collectOMS(OsmSession ParentSession, int duration, TimeUnit durationUnitOfTime, String fileName) throws IOException
  {
    // return the number collected
    
    // if the unit of time is null, assume duration is really number of records to collect
    if(durationUnitOfTime == null)
      return collectOMS(ParentSession, duration, fileName);
    
    long durationInSeconds = TimeUnit.SECONDS.convert(duration, durationUnitOfTime);
    OpenSmMonitorService oms    = null;
    OSM_Fabric           fabric = null;
    int sweepPeriod = 0;
    
    // if the collection is empty, get a new OMS
    // if the collection has at least one element, get the most current OMS
    
    if(getSize() < 1)
    {
      oms = OpenSmMonitorService.getOpenSmMonitorService(ParentSession);
    }
    else
    {
      oms = this.getCurrentOMS();
    }
    fabric = oms.getFabric();
    
    // from the fabric, calculate the number to collect, and then collect
    if (fabric != null)
    {
      sweepPeriod = fabric.getPerfMgrSweepSecs();
      return collectOMS(ParentSession, (int)(durationInSeconds/sweepPeriod), fileName);
    }
    return 0;
  }
  
  /************************************************************
   * Method Name:
   *  appendOMS
  **/
  /**
   * Add the provided OMS instance to the end of the file, if and only
   * if the instance is from the same fabric, and has a later time than
   * the what is already in the file.
   * 
   * If the file does not exist, it will be created with a single OMS instance.
   *
   * @see     describe related java objects
   *
   * @param oms
   * @param fileName
   * @return
   * @throws IOException
   * @throws ClassNotFoundException
   ***********************************************************/
  public static int appendOMS(OpenSmMonitorService oms, String fileName) throws IOException, ClassNotFoundException
  {
    // return the number collected
    OMS_Collection history = null;
    int numCollected       = 0;
    
    if(oms != null)
    {
      // open the file, or create it if it doesn't exist
      try
      {
        history = OMS_Collection.readOMS_Collection(fileName);
      }
      catch (FileNotFoundException e)
      {
        
        // make the file, then continue
        System.err.println("creating");
        history = new OMS_Collection();
        history.put(oms);
        numCollected = history.getSize();
        OMS_Collection.writeOMS_Collection(fileName, history);
        return numCollected;
      }
      
      // how big is the file, get the oldest one ?
      numCollected = history.getSize();
      OpenSmMonitorService currentOms = history.getCurrentOMS();
      
      TimeStamp t1 = oms.getTimeStamp();
      String n1    = oms.getFabricName();
      TimeStamp t0 = currentOms.getTimeStamp();
      String n0    = currentOms.getFabricName();

      
      // if this is older, append it to the end, and close the file
      if(t1.after(t0) && (n1.equals(n0)))
      {
        System.err.println("appending");
        history.put(oms);
        numCollected = history.getSize();
        OMS_Collection.writeOMS_Collection(fileName, history);
        return numCollected;
      }
      else
      {
        System.err.println("Not appending - same time, or different fabric");
      }
      
      // return the number in the file, or the current OMS???
    
     }
    else
      logger.severe("Can't append null to the OMS file");
    return numCollected;
  }

  /************************************************************
   * Method Name:
   *  collectOMS
  **/
  /**
   * Collect and record OMS snapshots to a file.  This is the "flight recorder"
   * function.
   * 
   * This is the main collection method.
   *
   * @see     describe related java objects
   *
   * @param ParentSession  a previously established connection to the OMS
   * @param numberToCollect the desired number of snapshots to save
   * @param fileName the path to the destination file
   * @return  number of snapshots collected
   * @throws IOException
   ***********************************************************/
  public int collectOMS(OsmSession ParentSession, int numberToCollect, String fileName) throws IOException
  {
    // return the number collected
    int numCollected       = 0;
    
    if(ParentSession != null)
    {
    numCollected = getSize();
    numberToCollect = numberToCollect < numCollected ? numCollected: numberToCollect;
    numberToCollect = numberToCollect > getMaxSize() ? getMaxSize(): numberToCollect;
    
    OpenSmMonitorService currFabric = null;
    // how often does the perfmgr sweep? get it at that rate
    int sweepPeriod = getAveDeltaSeconds();
    
      do
      {
        // grab an instance of the fabric (we need two to create a delta)
        OpenSmMonitorService oms = OpenSmMonitorService.getOpenSmMonitorService(ParentSession);
        currFabric = oms;
        if (currFabric != null)
        {
          if(sweepPeriod == 0)
          {
            sweepPeriod = currFabric.getFabric().getPerfMgrSweepSecs();
            AveDeltaSeconds = sweepPeriod;
          }
          
            put( currFabric);
            numCollected = getSize();  // actual size, let the collection keep track for us
            
            // if a file name was provided, save the results thus far (over write)
            if(fileName != null)
            {
              logger.info("Saving Collection Results thus far: " + numCollected + " of " + numberToCollect + " instances of the OMS");
              OMS_Collection.writeOMS_Collection(fileName, this);
            }
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
        else
        {
          // returned NULL, assume connection is lost, try again, but not too quickly
          logger.severe("Could not obtain an OMS snapshot (connection lost??), trying again in 5 seconds");
          try
          {
            TimeUnit.SECONDS.sleep(5);
          }
          catch (InterruptedException e)
          {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      } while (numCollected < numberToCollect);

      logger.info("Done  Collecting " + numberToCollect + " instances of the OMS (end: " + new TimeStamp() + ")");
    }
    else
      logger.severe("Can't collect the OMS without a valid session to the service");
    return numCollected;
  }

  public int collectOMS(OsmSession ParentSession, int numberToCollect)
  {
    // return the number collected
    int numCollected       = 0;
    
    if(ParentSession != null)
    {
    numCollected = getSize();
    numberToCollect = numberToCollect < numCollected ? numCollected: numberToCollect;
    numberToCollect = numberToCollect > getMaxSize() ? getMaxSize(): numberToCollect;
    
    OpenSmMonitorService oms = null;
     // how often does the perfmgr sweep? get it at that rate
    int sweepPeriod = 0;
    
    logger.info("Start Collecting " + numberToCollect + " instances of the OMS (start: " + new TimeStamp() + ")");
    
      do
      {
        // grab an instance of the fabric
        oms = OpenSmMonitorService.getOpenSmMonitorService(ParentSession);
        if (oms != null)
        {
          sweepPeriod = oms.getFabric().getPerfMgrSweepSecs();
          put(oms);
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

      logger.info("Done  Collecting " + numberToCollect + " instances of the OMS (end: " + new TimeStamp() + ")");
    }
    else
      logger.severe("Can't collect the OMS without a valid session to the service");
    return numCollected;
  }
  
  public OpenSmMonitorService getOldestOMS1()
  {
    // return the oldest, or bottom of the stack
    return getOMS(0);
  }
  
  public OpenSmMonitorService getOldestOMS()
  {
    return getInitialOMS();
  }
  
  public OMS_List getOldestOMS_List()
  {
    return new OMS_List(this, true);
  }
  
  public OMS_List getOMS_List()
  {
    return new OMS_List(this, false);
  }
  
  public OpenSmMonitorService getOMS(int ndex)
  {
    // return the specified index
    Object [] omsa = omsHistory.values().toArray();
    
    // throw ArrayIndexOutOfBounds exception, or return null?
    if((omsa != null) && (omsa.length > ndex))
      return (OpenSmMonitorService)omsa[ndex];
    return null;
  }
  
  public OpenSmMonitorService getCurrentOMSOrig()
  {
    // return the most recent, or top of the stack
    Object [] omsa = omsHistory.values().toArray();
    
    // throw ArrayIndexOutOfBounds exception, or return null?
    if((omsa != null) && (omsa.length > 0))
      return (OpenSmMonitorService)omsa[omsa.length-1];
    return null;
  }
  
  public OpenSmMonitorService getCurrentOMS1()
  {
    // return the most recent, or top of the stack
    return getOMS(getSize() -1);
  }
  
  public OpenSmMonitorService getCurrentOMS()
  {
    return getNewestOMS();
  }
  
  public OSM_Fabric getCurrentOSM_Fabric()
  {
    return getCurrentOMS().getFabric();
  }
  
  public OSM_Fabric getOldestOSM_Fabric()
  {
    return getOldestOMS().getFabric();
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
    OpenSmMonitorService [] omsa = getRecentOSMs(num);
    if((omsa == null) || (omsa.length < 1))
      return null;
    
    int size = (omsa.length > num) ? num: omsa.length;
    OSM_Fabric [] ffa = new OSM_Fabric[size];
    
    // keep the order
    for(int n=0; n < size; n++)
      ffa[n] = (OSM_Fabric)omsa[n].getFabric();
    return ffa;
  }
  
  public OpenSmMonitorService [] getRecentOSMs(int num)
  {
    // return the most recent ones, or top of the stack
    Object [] oa = omsHistory.values().toArray();
    if((oa == null) || (oa.length < 1))
      return null;
    
    int size = (oa.length > num) ? num: oa.length;
    OpenSmMonitorService [] ffa = new OpenSmMonitorService[size];
    
    // keep the order
    for(int n=0; n < size; n++)
      ffa[n] = (OpenSmMonitorService)oa[(oa.length -size)+n];
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
    if(omsHistory != null)
      return omsHistory.size();
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
  
  public OpenSmMonitorService put(OpenSmMonitorService oms)
  {
    // always goes at the end, but if this key already exists, then don't replace
    return put(getOMS_Key(oms), oms, false);
  }
  
  public OpenSmMonitorService put(OpenSmMonitorService oms, boolean replace)
  {
    // always goes at the end, but if this key already exists, conditionally replace
    return put(getOMS_Key(oms), oms, replace);
  }
  
  /************************************************************
   * Method Name:
   *  put
  **/
  /**
   * Add this OMS to the collection.  If this OMS already exists in the
   * collection, the existing one will be replaced, but the collection
   * will not grow.  If the collection does grow, and if there are
   * collection listeners, they will be notified of the change.
   * 
   * TODO: fix if the collection size reaches max, then it will NOT
   *       grow.  What to do, notify still?
   *
   * @see     describe related java objects
   *
   * @param key
   * @param oms
   * @return
   ***********************************************************/
  public OpenSmMonitorService put(String key, OpenSmMonitorService oms, boolean replace)
  {
    if((oms != null) && (oms.getFabric() != null) && (omsHistory != null))
    {
      int prevSize = omsHistory.size();
      
      // keep the first put, and always the current one
      if((prevSize == 0) && (InitialOMS == null))
      {
        InitialOMS = oms;
        NewestOMS  = oms;
        FabricName = oms.getFabricName();
        AveDeltaSeconds = oms.getFabric().getPerfMgrSweepSecs();  // use sweep time, for first data point
      }
      OpenSmMonitorService prevOMS = getNewestOMS();
      NewestOMS                    = oms;
      
      // conditionally replace an existing oms, otherwise assume previous with same timestamp is fine to keep
      if(!replace && (omsHistory.containsKey(OMS_Collection.getOMS_Key(oms))))
        return oms;

      OpenSmMonitorService replacedVal = omsHistory.put(key, oms);
      int currSize = omsHistory.size();
      
      // update the average
      if((prevSize != 0) && (prevSize != currSize))
      {
        int totTime = (this.getAveDeltaSeconds() * prevSize) + OSM_FabricDelta.getDeltaSeconds(prevOMS.getFabric(), NewestOMS.getFabric());
        AveDeltaSeconds = totTime/currSize; 
      }
 
      // notify the listeners if the size has changed
      if((prevSize != currSize) && (Collection_Listeners.size() > 0))
      {
        try
        {
          updateAllListeners(oms);
        }
        catch (Exception e)
        {
          logger.severe("Couldn't notify OMS_Collection listeners that the collection has grown");
        }
        return replacedVal;
      }
    }
    return null;
  }
  
  public String getKeys()
  {
    return omsHistory.keySet().toString();
  }
  
  public String getCacheFileName()
  {
    return getCacheFileName(this);
  }
  
  public static String getCacheFileName(OMS_Collection osmHistory)
  {
    if((osmHistory != null) && (osmHistory.getSize() > 0))
      return getCacheFileName(osmHistory.getOldestOMS());
    return null;
  }
  
  public static String getCacheFileName(OpenSmMonitorService osmService)
  {
    if((osmService != null) && (osmService.getFabricName() != null))
    {
      // the name should be a combination of the cache location and the fabric name
      String fNam = osmService.getFabricName() + ".his";
      String cNam = OMS_DEFAULT_DIR  + OMS_CACHE_DIR;
      return cNam + fNam;
     
//      return "/home/meier3/.smt/cache/fabric.his";
    }
    return null;
  }
  

  public static boolean recordHistory(String host, String port, int duration, TimeUnit durationUnitOfTime, String fileName, boolean showInfo) throws IOException 
  {
    // establish a connection
    logger.info("OMS_C: Opening the OMS Session for recording");
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
      OMS_Collection omsHistory = new OMS_Collection();
      omsHistory.collectOMS(ParentSession, duration, durationUnitOfTime, fileName);
      
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
        System.out.println(omsHistory.toInfo());
    }
    else
    {
      logger.severe("Could not establish an OMS session");
    }
    return true;    
  }


  public static boolean recordHistory(String host, String port, int updatePeriodSecs, String fileName) throws IOException, ClassNotFoundException 
  {
    // establish a connection
    logger.info("OMS_R: Opening the OMS Session for apending");
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
      // get a single instance, and append it every update period
      OpenSmMonitorService oms = null;
      int num = 0;
      do
      {
        // grab an instance of the fabric (we need two to create a delta)
        oms = OpenSmMonitorService.getOpenSmMonitorService(ParentSession);
        if (oms != null)
        {
          int curr_num = OMS_Collection.appendOMS(oms, fileName);
          if(curr_num != num)
          {
            num = curr_num;
            System.err.println("Num: " + num);
          }
          try
          {
            TimeUnit.SECONDS.sleep(updatePeriodSecs -2); // sample slightly faster than sweep period, so we don't miss any
          }
          catch (InterruptedException e)
          {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      } while (!stopAppending);  // don't end for now

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
    }
    else
    {
      logger.severe("Could not establish an OMS session");
    }
    return true;    
  }


  public static OMS_Collection readOMS_Collection(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException
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
    
    OMS_Collection obj = null;
    Object unknownObject = objectInputStream.readObject();
    if(unknownObject instanceof OMS_Collection)
      obj = (OMS_Collection) unknownObject;

    objectInputStream.close();
    if(useCompression)
      in.close();
    fileInput.close();
    return obj;
  }
  
  public static void writeOMS_Collection(String fileName, OMS_Collection omsHistory) throws IOException
  {
    File omsFile = new File(fileName);
    omsFile.getParentFile().mkdirs();
    FileOutputStream fileOutput = new FileOutputStream(omsFile);
    ObjectOutputStream objectOutput = null;
    GZIPOutputStream out = null;
    
    if(useCompression)
    {
      out =  new GZIPOutputStream(fileOutput);
      objectOutput = new ObjectOutputStream(out);
    }
    else
      objectOutput = new ObjectOutputStream(fileOutput);
    
    objectOutput.writeObject(omsHistory);
    objectOutput.flush();
    objectOutput.close();
    if(useCompression)
      out.close();
    fileOutput.close();
    return;
  }

  public static OMS_Collection cacheOMS_Collection(OpenSmMonitorService osmService, OMS_Collection omsHistory)
  {
    // append the OMS to the collection and return it
    // if the collection doesn't exist, create it with the OMS as its single
    // element
    //
    // before returning, write the collection to a cache file (create the path
    // if necessary)
    if (omsHistory == null)
    {
      logger.info("creating the Cache collection");
      omsHistory = new OMS_Collection();
    }
    if(osmService != null)
    {
      omsHistory.put(osmService);
      String cacheName = getCacheFileName(osmService);
      logger.info("Saving the Cache file: ("+ cacheName + ")");
      try
      {
        OMS_Collection.writeOMS_Collection(cacheName, omsHistory);
      }
      catch (IOException e)
      {
        logger.severe("Could not save the Cache file: ("+ cacheName + ")");
        logger.severe(e.getMessage());
       }
     }
    return omsHistory;
  }

  public static OMS_Collection cacheOMS_Collection(OMS_List omsCache, OMS_Collection omsHistory)
  {
    // append the OMS_List to the collection and return it
    // if the collection doesn't exist, create it with the OMS_List as the first set
    // of elements
    //
    // before returning, write the collection to a cache file (create the path
    // if necessary)
    if (omsHistory == null)
    {
      logger.info("creating the Cache collection");
      omsHistory = new OMS_Collection();
    }
    if((omsCache != null) && (omsCache.size() > 0))
    {
      OpenSmMonitorService [] omsArray = omsCache.getRecentOMSs(2);

      for(OpenSmMonitorService oms: omsArray)
      {
        omsHistory.put(oms);
      }
      String cacheName = getCacheFileName(omsArray[0]);
      logger.info("Saving the Cache file: ("+ cacheName + ")");
      try
      {
        OMS_Collection.writeOMS_Collection(cacheName, omsHistory);
      }
      catch (IOException e)
      {
        logger.severe("Could not save the Cache file: ("+ cacheName + ")");
        logger.severe(e.getMessage());
       }
     }
    return omsHistory;
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
      OMS_Collection.recordHistory("localhost", "10011", 120, "/home/meier3/.smt/testFile");
//      ParentSession = OsmService.openSession("localhost", "10011", null, null);
    }
    catch (Exception e)
    {
      logger.severe(e.getStackTrace().toString());
      System.exit(-1);
    }

    if (ParentSession != null)
    {
      OMS_Collection omsHistory = new OMS_Collection();
      omsHistory.collectOMS(ParentSession, 5);
//      omsHistory.collectOMS(ParentSession, 10, TimeUnit.MINUTES);
      
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
      System.err.println(omsHistory.toString());
      OMS_Collection.writeOMS_Collection("/home/meier3/.smt/NewOMS.cache", omsHistory);

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
    stringValue.append(OMS_Collection.class.getSimpleName() + "\n");
    
    stringValue.append("fabric name:                 " + this.getFabricName() + "\n");
    stringValue.append("first timestamp:             " + this.getOldestOSM_Fabric().toTimeString() + "\n");
    stringValue.append("last timestamp:              " + this.getCurrentOSM_Fabric().toTimeString() + "\n");
    stringValue.append("ave secs between records:    " + this.getAveDeltaSeconds() + "\n");
    stringValue.append("# secs between pfmgr sweeps: " + this.getOldestOSM_Fabric().getPerfMgrSweepSecs() + "\n");
    stringValue.append("# records in collection:     " + getSize() + "\n");
    stringValue.append("# nodes:                     " + this.getOldestOSM_Fabric().getOSM_Nodes().size() + "\n");
    stringValue.append("# ports:                     " + this.getOldestOSM_Fabric().getOSM_Ports().size() + "\n");
    stringValue.append("# links:                     " + this.getOldestOSM_Fabric().getIB_Links().size());
  
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
    for (Map.Entry<String, OpenSmMonitorService> entry : omsHistory.entrySet())
    {
      OpenSmMonitorService f = entry.getValue();
      if(!initial)
        buff.append("\n");
      else
        initial = false;
      
      buff.append(f.toTimeString());
    }
    return buff.toString();
  }

}
