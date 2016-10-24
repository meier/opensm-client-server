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
 *        file: OsmUpdateManager.java
 *
 *  Created on: Aug 26, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import gov.llnl.lc.infiniband.opensm.plugin.OsmInterface;
import gov.llnl.lc.infiniband.opensm.plugin.OsmPluginMain;
import gov.llnl.lc.infiniband.opensm.plugin.event.OSM_EventStats;
import gov.llnl.lc.infiniband.opensm.plugin.event.OsmEventManager;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmServerStatus;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmServiceManager;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmSession;
import gov.llnl.lc.logging.CommonLogger;
import gov.llnl.lc.net.MultiSSLServerStatus;
import gov.llnl.lc.net.MultiThreadSSLServer;
import gov.llnl.lc.net.ObjectSession;
import gov.llnl.lc.system.hostmachine.LocalHost;
import gov.llnl.lc.system.whatsup.WhatsUpInfo;
import gov.llnl.lc.time.TimeStamp;

import java.util.concurrent.TimeUnit;


/**********************************************************************
 * The OsmUpdateManager is a server-side daemon (singleton) that
 * obtains data primarily through the native interfaces.  It is the
 * intermediary that caches the information as java objects that will
 * be provided to (remote) clients of the service.
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Feb 11, 2013 3:09:13 PM
 **********************************************************************/
public class OsmUpdateManager implements Runnable, CommonLogger
{
  /** the synchronization object **/
  private static Boolean semaphore            = new Boolean( true );
  
  /** the Managers update counter **/
  private static long OsmHeartbeat = 0;

  /** the Managers update period in seconds**/
  private static int OsmUpdatePeriod = 60;
  
  private static boolean FollowNativeUpdates = true;

  /** the one and only <code>OsmUpdateManager</code> Singleton **/
  private volatile static OsmUpdateManager globalOsmUpdateManager  = null;

  /** boolean specifying whether the thread should continue **/
  private volatile static boolean Continue_Thread = true;
  
  /** thread responsible for updating the data **/
  private static java.lang.Thread Update_Thread;
  
  /** small cache of previous data **/
  private static OMS_List omsHistory = null;
  
  /** the current fabric **/
  private static OSM_Fabric osmFabric = null;

  /** the current fabric **/
  private static OSM_Configuration osmConfiguration = null;

  /** all of the nodes from the native layer **/
  private static OSM_Nodes nativeNodes = null;

  /** all of the ports from the native layer **/
  private static OSM_Ports nativePorts = null;

  /** all of the statistics from the native layer **/
  private static OSM_Stats nativeStats = null;

  /** all of the relevant OpenSm system information (refer to console) **/
  private static OSM_SysInfo nativeSystemInfo = null;
  
  /** all of the relevant Subnet information **/
  private static OSM_Subnet nativeSubnet = null;
  
  /** all of the information for the plugin **/
  private static OSM_PluginInfo nativePluginInfo = null;
  
  /** all of the information for testing (under development) **/
  private static OSM_TestData nativeTestData = null;
  
  /** all of the event counters **/
  private static OSM_EventStats nativeEventStats = null;
  
  /** all of the installed software packages **/
  private static OSM_SoftwareInfo remoteRpmPackages = null;
  
  /** the "up/down" status of the nodes **/
  private static WhatsUpInfo whatsUpInfo = null;
  
  /** logger for the class **/
  private final java.util.logging.Logger classLogger =
      java.util.logging.Logger.getLogger( getClass().getName() );
  
  private static int OMS_UpdateSkipValue = 3;
  private static int OMS_UpdateSkipCount = 0;

  private OsmUpdateManager()
  {
    // set up the thread to listen
    Update_Thread = new Thread(this);
    Update_Thread.setDaemon(true);
    Update_Thread.setName("OsmUpdateThread");
  }
  
  /**************************************************************************
  *** Method Name:
  ***     init
  **/
  /**
  *** Summary_Description_Of_What_init_Does.
  *** <p>
  ***
  *** @see          Method_related_to_this_method
  ***
  *** @param        Parameter_name  Description_of_method_parameter__Delete_if_none
  ***
  *** @return       Description_of_method_return_value__Delete_if_none
  ***
  *** @throws       Class_name  Description_of_exception_thrown__Delete_if_none
  **************************************************************************/

  public synchronized boolean init()
  {
    boolean success = false;
    
    logger.info("Initializing the UpdateManager");
    
    /* do whatever it takes to initialize the update manager */
    startThread();
    
    return success;
  }
  /*-----------------------------------------------------------------------*/


  /**************************************************************************
   *** Method Name:
   ***     startThread
   **/
   /**
   *** Summary_Description_Of_What_startThread_Does.
   *** <p>
   ***
   *** @see          Method_related_to_this_method
   ***
   *** @param        Parameter_name  Description_of_method_parameter__Delete_if_none
   ***
   *** @return       Description_of_method_return_value__Delete_if_none
   ***
   *** @throws       Class_name  Description_of_exception_thrown__Delete_if_none
   **************************************************************************/

   private boolean startThread()
   {
     logger.info("Starting the " + Update_Thread.getName() + " Thread");
     Update_Thread.start();
     return true;
   }
   /*-----------------------------------------------------------------------*/

   /**************************************************************************
    *** Method Name:
    ***     stopThread
    **/
    /**
    *** Summary_Description_Of_What_startThread_Does.
    *** <p>
    ***
    *** @see          Method_related_to_this_method
    ***
    *** @param        Parameter_name  Description_of_method_parameter__Delete_if_none
    ***
    *** @return       Description_of_method_return_value__Delete_if_none
    ***
    *** @throws       Class_name  Description_of_exception_thrown__Delete_if_none
    **************************************************************************/

    private void stopThread()
    {
      logger.info("Stopping the " + Update_Thread.getName() + " Thread");
      Continue_Thread = false;
    }
    /*-----------------------------------------------------------------------*/


    public void destroy()
    {
      logger.info("Terminating the UpdateManager");
      stopThread();
    }
    
   /**************************************************************************
  *** Method Name:
  ***     getInstance
  **/
  /**
  *** Get the singleton OsmUpdateManager. This can be used if the application wants
  *** to share one manager across the whole JVM.  Currently I am not sure
  *** how this ought to be used.
  *** <p>
  ***
  *** @return       the GLOBAL (or shared) OsmUpdateManager
  **************************************************************************/

  public static OsmUpdateManager getInstance()
  {
    synchronized( OsmUpdateManager.semaphore )
    {
      if ( globalOsmUpdateManager == null )
      {
        globalOsmUpdateManager = new OsmUpdateManager( );
      }
      return globalOsmUpdateManager;
    }
  }
  /*-----------------------------------------------------------------------*/

  public Object clone() throws CloneNotSupportedException
  {
    throw new CloneNotSupportedException(); 
  }

  
  @Override
  public void run()
  {
    OsmPluginMain jServ = OsmPluginMain.getInstance();
    // get the native interface
    OsmInterface osmInt = jServ.getInterface();
    
    // thread local storage
    OSM_Nodes osmNodes = null;
    OSM_SysInfo osmSysInfo = null;
    OSM_Stats osmStats = null;
    OSM_Ports osmPorts = null;
    OSM_Subnet osmSubn = null;
    OSM_PluginInfo osmPlugin = null;
    OSM_SoftwareInfo rpmPackages = null;
    OSM_EventStats osmEventStats = null;
    omsHistory     = new OMS_List(2);
    nativeTestData = new OSM_TestData();
    
    // check the Thread Termination Flag, and continue if Okay
    while(Continue_Thread)
    {
      try
      {
        incrementHeartbeat();
        TimeUnit.SECONDS.sleep(getUpdatePeriod());  // wait for new data to become available
        
        osmEventStats = new OSM_EventStats(OsmEventManager.getInstance().getEventStatistics().getCounterArray());
        if(osmEventStats != null)
          setNativeEventStats(osmEventStats);
        else
          logger.info("The returned OSM_EventStats object appears to be null\n");

        osmNodes = osmInt.getOsmNodes();
        if(osmNodes != null)
           setNativeNodes(osmNodes);
        else
          logger.info("The returned OSM_Nodes object appears to be null\n");

        osmPorts = osmInt.getOsmPorts();
        if(osmPorts != null)
          this.setNativePorts(osmPorts);
        else
          logger.info("The returned OSM_Ports object appears to be null\n");

        osmStats = osmInt.getOsmStats();
        if(osmStats != null)
          setNativeStats(osmStats);
        else
          logger.info("The returned OSM_Stats object appears to be null\n");

        osmSysInfo = osmInt.getOsmSysInfo();
        if(osmSysInfo != null)
          setNativeSystemInfo(osmSysInfo);
        else
          logger.info("The returned OSM_SysInfo object appears to be null\n");

        osmSubn = osmInt.getOsmSubnet();
        if(osmSubn != null)
          setNativeSubnet(osmSubn);
        else
          logger.info("The returned OSM_Subnet object appears to be null\n");

        osmPlugin = osmInt.getPluginInfo();
        if(osmPlugin == null)
        {
          logger.warning("The returned OSM_Plugin object appears to be null\n");
          osmPlugin = new OSM_PluginInfo(11, 22, 33, 44, 55);
        }
        setNativePlugin(osmPlugin);
        
          if(false)
          {
         // rpm packages
          try
          {
            rpmPackages = new OSM_SoftwareInfo();

            if(rpmPackages != null)
              this.setRemoteRpmPackages(rpmPackages);
            else
              logger.warning("The returned SoftwarePackages object appears to be null\n");
          }
          catch (Exception ioe)
          {
            logger.severe("RPM Query exception: " + ioe.getMessage());
          }

        }
        
        // everything seemed to work, so update the OMS_List
//          updateOmsHistory(osmNodes, osmPorts, osmStats, osmSubn, osmSysInfo, osmPlugin, osmEventStats);
          updateOmsHistory(getNativeNodes(), getNativePorts(), getNativeStats(), getNativeSubnet(), getNativeSystemInfo(), getNativePlugin(), getNativeEventStats());

       }
      catch (Exception e)
      {
        logger.severe("Crap, the " + Update_Thread.getName() + " Thread had an unknown exception.");
        logger.severe(e.getMessage());
      }
    }
    logger.info("Terminating the " + Update_Thread.getName() + " Thread");
    /* fall through, must be done! */
  }
  
  public OSM_EventStats getNativeEventStats()
  {
    synchronized (OsmUpdateManager.semaphore)
    {
      return nativeEventStats;
    }
  }

  public void setNativeEventStats(OSM_EventStats osmEventStats)
  {
    synchronized (OsmUpdateManager.semaphore)
    {
      nativeEventStats = new OSM_EventStats(osmEventStats.getCounterArray());
    }
  }

  public OMS_List getOmsHistory()
  {
    synchronized (OsmUpdateManager.semaphore)
    {
      return omsHistory;
    }
  }
  
  public OSM_Fabric getOSM_Fabric()
  {
    synchronized (OsmUpdateManager.semaphore)
    {
      return osmFabric;
    }
  }
  
  public OSM_Configuration getOSM_Configuration()
  {
    synchronized (OsmUpdateManager.semaphore)
    {
      return osmConfiguration;
    }
  }
  
  public void setOSM_Configuration(OSM_Configuration config)
  {
    synchronized (OsmUpdateManager.semaphore)
    {
      osmConfiguration = config;
    }
  }

  
  private void updateOmsHistory(OSM_Nodes osmNodes, OSM_Ports osmPorts, OSM_Stats osmStats,
      OSM_Subnet osmSubn, OSM_SysInfo osmSysInfo, OSM_PluginInfo osmPlugin,
      OSM_EventStats osmEventStats)
  {
    // construct and maintain an OMS_List, which is a queue of OMS instances.
    // since this method is called much more often than the OMS will
    // change, we can skip the body most of the time, thereby providing a bit
    // of time savings.

    if (OMS_UpdateSkipCount++ < OMS_UpdateSkipValue)
      return;
    OMS_UpdateSkipCount = 0;

    /***********************************************************************************************/
    // whats up
    WhatsUpInfo whatsUp = null;
    OSM_Configuration osmConfig = null;

    try
    {
      whatsUp = WhatsUpInfo.createWhatsUpInfo();
      if (whatsUp != null)
        setWhatsUpInfo(whatsUp);
      else
        logger.warning("The returned WhatsUp object appears to be null\n");
    }
    catch (Exception ioe)
    {
      logger.severe("WhatsUp Query exception: " + ioe.getMessage());
    }

    // configuration
    try
    {
      // the location of these files, if they exist, is in /etc/opensm.conf
      // see attributes "node_name_map_name"
      //
      // just fail silently if they don't exist
      String nodeMapFileName = null;
      String fabConfFileName = "/etc/infiniband-diags/ibfabricconf.xml";
      if(osmSubn != null && osmSubn.Options != null && osmSubn.Options.node_name_map_name != null)
        nodeMapFileName = osmSubn.Options.node_name_map_name;
      
      osmConfig = new OSM_Configuration(nodeMapFileName, fabConfFileName);

      if (osmConfig != null)
        setOSM_Configuration(osmConfig);
      else
        logger.warning("The returned OSM_Configuration object appears to be null\n");
    }
    catch (Exception e)
    {
      logger.severe("file exception: " + e.getMessage());
    }
    /***********************************************************************************************/

    OsmServerStatus rss = getServerStatus(osmPlugin);

    String name = LocalHost.getHostName();
    if ((rss != null) && (rss.Server.getHost() != null))
      name = rss.Server.getHost();

    ObjectSession ss = new ObjectSession();

    // ObjectSession SessionStatus = multiServer.getCurrent_Sessions().get(0);

    OSM_Fabric fabric = new OSM_Fabric(name, osmNodes, osmPorts, osmStats, osmSubn, osmSysInfo, osmEventStats);

    /* recalculate the # of times to skip, based on the perfmgrs sweep rate */
    if (fabric != null)
    {
      synchronized (OsmUpdateManager.semaphore)
      {
        osmFabric = fabric;
      }
      int sweepTime = fabric.getPerfMgrSweepSecs();
      OMS_UpdateSkipValue = (sweepTime / getUpdatePeriod()) / 2 - 1;
      /*
       * Nyquist sampling (twice as fast as change, so as not to miss anything, and
       *                   to keep the latency reasonably low )
       */
      OMS_UpdateSkipValue = OMS_UpdateSkipValue < 1 ? 1 : OMS_UpdateSkipValue;

      // have everything I need to construct the OpenSmMonitorService
      OpenSmMonitorService service = new OpenSmMonitorService(ss, rss, fabric);

      // add it to the new cache, or list
      synchronized (OsmUpdateManager.semaphore)
      {
        // this will NOT replace the previous service, if it has the same timestamp
        omsHistory.putCurrentOMS(service);
      }
    }
    if (omsHistory.isEmpty())
      logger.info("The OMS in Cache is empty");
  }

  public OsmServerStatus getServerStatus(OSM_PluginInfo pi)
  {
    MultiThreadSSLServer multiServer = MultiThreadSSLServer.getInstance();
    OsmPluginMain OPM                = OsmPluginMain.getInstance();
    if(pi == null)
      pi = new OSM_PluginInfo(666, 666, 666, 666, 666);
    
    return  new OsmServerStatus(new MultiSSLServerStatus(multiServer), OsmServiceManager.MaxParents, OsmSession.MaxClones,
        pi.NativeUpdatePeriodSecs, pi.NativeReportPeriodSecs, getUpdatePeriod(),
        pi.NativeEventTimeoutMsecs, pi.NativeUpdateCount, getHeartbeat(),
        pi.NativeEventCount, new TimeStamp().getTimeInMillis(), isFollowNativeUpdates(), multiServer.isAllowLocalHost(),
        OPM.getVersion(), OPM.getBuildDate());
  }

  public int getUpdatePeriod()
  {
    synchronized (OsmUpdateManager.semaphore)
    {
      return OsmUpdatePeriod;
    }
  }

  public void setUpdatePeriod(int secs)
  {
    synchronized (OsmUpdateManager.semaphore)
    {
      /* enforce min/max value here */
      secs = (secs > 0)&& (secs < 120) ? secs: 30;
      OsmUpdatePeriod = secs;
    }
  }

  public long getHeartbeat()
  {
    synchronized (OsmUpdateManager.semaphore)
    {
      return OsmHeartbeat;
    }
  }

  public void incrementHeartbeat()
  {
    synchronized (OsmUpdateManager.semaphore)
    {
      OsmHeartbeat++;
    }
  }

  public OSM_PluginInfo getNativePlugin()
  {
    synchronized (OsmUpdateManager.semaphore)
    {
      return nativePluginInfo;
    }
  }

  public void setNativePlugin(OSM_PluginInfo NativePlugin)
  {
    synchronized (OsmUpdateManager.semaphore)
    {
      nativePluginInfo = NativePlugin;
      
      if(FollowNativeUpdates)
        OsmUpdatePeriod = NativePlugin.NativeUpdatePeriodSecs;
    }
  }

  public OSM_Nodes getNativeNodes()
  {
    synchronized (OsmUpdateManager.semaphore)
    {
      return nativeNodes;
    }
  }

  public void setNativeNodes(OSM_Nodes NativeNodes)
  {
    synchronized (OsmUpdateManager.semaphore)
    {
      nativeNodes = NativeNodes;
    }
  }

  /************************************************************
   * Method Name:
   *  getNativePorts
   **/
  /**
   * Returns the value of nativePorts
   *
   * @return the nativePorts
   *
   ***********************************************************/
  
  public OSM_Ports getNativePorts()
  {
    synchronized (OsmUpdateManager.semaphore)
    {
    return nativePorts;
    }
  }

  /************************************************************
   * Method Name:
   *  setNativePorts
   **/
  /**
   * Sets the value of nativePorts
   *
   * @param nativePorts the nativePorts to set
   *
   ***********************************************************/
  public void setNativePorts(OSM_Ports NativePorts)
  {
    synchronized (OsmUpdateManager.semaphore)
    {
      nativePorts = NativePorts;
    }
  }

  /************************************************************
   * Method Name:
   *  getNativeSystemInfo
   **/
  /**
   * Returns the value of nativeSystemInfo
   *
   * @return the nativeSystemInfo
   *
   ***********************************************************/
  
  public OSM_SysInfo getNativeSystemInfo()
  {
    synchronized (OsmUpdateManager.semaphore)
    {
    return nativeSystemInfo;
    }
  }

  /************************************************************
   * Method Name:
   *  setNativeSystemInfo
   **/
  /**
   * Sets the value of nativeSystemInfo
   *
   * @param nativeSystemInfo the nativeSystemInfo to set
   *
   ***********************************************************/
  public void setNativeSystemInfo(OSM_SysInfo NativeSystemInfo)
  {
    synchronized (OsmUpdateManager.semaphore)
    {
      nativeSystemInfo = NativeSystemInfo;
    
    /* currently just pull the testdata from the systeminfo */
      nativeTestData.OsmVersion    = NativeSystemInfo.OpenSM_Version;
      nativeTestData.PluginVersion = NativeSystemInfo.OsmJpi_Version;
    }
  }

  /************************************************************
   * Method Name:
   *  isFollowNativeUpdates
   **/
  /**
   * Returns the value of followNativeUpdates
   *
   * @return the followNativeUpdates
   *
   ***********************************************************/
  
  public static boolean isFollowNativeUpdates()
  {
    synchronized (OsmUpdateManager.semaphore)
    {
    return FollowNativeUpdates;
    }
  }

  /************************************************************
   * Method Name:
   *  setFollowNativeUpdates
   **/
  /**
   * Sets the value of followNativeUpdates
   *
   * @param followNativeUpdates the followNativeUpdates to set
   *
   ***********************************************************/
  public static void setFollowNativeUpdates(boolean followNativeUpdates)
  {
    synchronized (OsmUpdateManager.semaphore)
    {
    FollowNativeUpdates = followNativeUpdates;
    }
  }

  /************************************************************
   * Method Name:
   *  getNativeStats
   **/
  /**
   * Returns the value of nativeStats
   *
   * @return the nativeStats
   *
   ***********************************************************/
  
  public OSM_Stats getNativeStats()
  {
    synchronized (OsmUpdateManager.semaphore)
    {
    return nativeStats;
    }
  }

  /************************************************************
   * Method Name:
   *  setNativeStats
   **/
  /**
   * Sets the value of nativeStats
   *
   * @param nativeStats the nativeStats to set
   *
   ***********************************************************/
  public void setNativeStats(OSM_Stats NativeStats)
  {
    synchronized (OsmUpdateManager.semaphore)
    {
      nativeStats = NativeStats;
    }
  }

  /************************************************************
   * Method Name:
   *  getNativeSubnet
   **/
  /**
   * Returns the value of nativeSubnet
   *
   * @return the nativeSubnet
   *
   ***********************************************************/
  
  public OSM_Subnet getNativeSubnet()
  {
    synchronized (OsmUpdateManager.semaphore)
    {
    return nativeSubnet;
    }
  }

  /************************************************************
   * Method Name:
   *  setNativeSubnet
   **/
  /**
   * Sets the value of nativeSubnet
   *
   * @param nativeSubnet the nativeSubnet to set
   *
   ***********************************************************/
  public void setNativeSubnet(OSM_Subnet NativeSubnet)
  {
    synchronized (OsmUpdateManager.semaphore)
    {
      nativeSubnet = NativeSubnet;
    }
  }

  /************************************************************
   * Method Name:
   *  getNativeTestData
   **/
  /**
   * Returns the value of nativeTestData
   *
   * @return the nativeTestData
   *
   ***********************************************************/
  
  public OSM_TestData getNativeTestData()
  {
    synchronized (OsmUpdateManager.semaphore)
    {
    return nativeTestData;
    }
  }

  /************************************************************
   * Method Name:
   *  getRemoteRpmPackages
   **/
  /**
   * Returns the value of remoteRpmPackages
   *
   * @return the remoteRpmPackages
   *
   ***********************************************************/
  
  public static OSM_SoftwareInfo getRemoteRpmPackages()
  {
    synchronized (OsmUpdateManager.semaphore)
    {
    return remoteRpmPackages;
    }
  }

  /************************************************************
   * Method Name:
   *  setRemoteRpmPackages
   **/
  /**
   * Sets the value of remoteRpmPackages
   *
   * @param remoteRpmPackages the remoteRpmPackages to set
   *
   ***********************************************************/
  public static void setRemoteRpmPackages(OSM_SoftwareInfo remoteRpmPackages)
  {
    synchronized (OsmUpdateManager.semaphore)
    {
    OsmUpdateManager.remoteRpmPackages = remoteRpmPackages;
    }
  }

  /************************************************************
   * Method Name:
   *  getWhatsUpInfo
   **/
  /**
   * Returns the value of whatsUpInfo
   *
   * @return the whatsUpInfo
   *
   ***********************************************************/
  
  public static WhatsUpInfo getWhatsUpInfo()
  {
    synchronized (OsmUpdateManager.semaphore)
    {
    return whatsUpInfo;
    }
  }

  /************************************************************
   * Method Name:
   *  setWhatsUpInfo
   **/
  /**
   * Sets the value of whatsUpInfo
   *
   * @param whatsUpInfo the whatsUpInfo to set
   *
   ***********************************************************/
  public static void setWhatsUpInfo(WhatsUpInfo whatsUpInfo)
  {
    synchronized (OsmUpdateManager.semaphore)
    {
      OsmUpdateManager.whatsUpInfo = whatsUpInfo;
    }
  }
}
