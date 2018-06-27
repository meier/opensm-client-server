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
 *        file: OsmPluginMain.java
 *
 *  Created on: Jul 7, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;


  public class OsmPluginMain implements Runnable, gov.llnl.lc.logging.CommonLogger
  {
    /** the synchronization object **/
    private static Boolean semaphore            = new Boolean( true );

    /** the one and only <code>OsmPluginMain</code> Singleton **/
    private volatile static OsmPluginMain globalOsmPluginMain  = null;

    private volatile static gov.llnl.lc.infiniband.opensm.plugin.event.OsmEventManager eventMgr;

    private volatile static gov.llnl.lc.infiniband.opensm.plugin.data.OsmUpdateManager dataMgr;

    private volatile static gov.llnl.lc.net.MultiThreadSSLServer osmServer;
    
    private volatile static gov.llnl.lc.infiniband.opensm.plugin.OsmPluginMonitor globalMonitor;

    /** The specific type of OsmInterface (various types?) **/
    private static gov.llnl.lc.infiniband.opensm.plugin.OsmInterface Osm_Interface;
    
    /** boolean specifying whether the thread should continue **/
    private volatile static boolean Continue_Thread = true;
    
    private static final int MainThreadSleepSecs = 900;

    /** thread responsible for managing the plugin **/
    private static java.lang.Thread PluginMain_Thread;
    
    private static String Version;
    
    private static String BuildDate;
        
    /** logger for the class **/
    private final java.util.logging.Logger classLogger =
        java.util.logging.Logger.getLogger( getClass().getName() );

    private OsmPluginMain()
    {
      // set up the thread to listen
      PluginMain_Thread = new Thread(this);
      PluginMain_Thread.setDaemon(true);
      PluginMain_Thread.setName("OsmPluginMainThread");
      getVersionInfo();
    }
    

    public OsmInterface getInterface()
    {
      return Osm_Interface;
    }
    
    /**************************************************************************
    *** Method Name:
    ***     setInterface
    ***
    *** Class & Instance Variables Used:
    ***     Osm_Interface
    **/
    /**
    *** Sets (specifies) the OSM Interface used by OsmPluginMain.
    *** This method is intended for unit testing only.
    *** <p>
    ***
    *** <dl><dt><b>Side Effects:</b></dt>
    ***     <dd>None
    *** </dd></dl>
    ***
    *** @param    osmInterface   a reference to an
    ***                             <code>gov.llnl.lc.infiniband.opensm.plugin.OsmInterface</code>
    ***                             object
    ***
    *** @see      gov.llnl.lc.infiniband.opensm.plugin.OsmInterface
    **************************************************************************/
      public void setInterface(OsmInterface osmInterface)
      {
        /* TODO does it need to be synchronized? */
        Osm_Interface = osmInterface;
      }
      /*-----------------------------------------------------------------------*/

    
    
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
      OsmInterface osmInt;
      
      logger.info("Initializing the Osm Java Plugin");
      
      // ** Create one of the various OsmInterfaces **
      if (true)
      {
        osmInt = new OsmNativeInterface();
      }
      else
      {
        osmInt = new OsmSimulatedInterface();
      }
      
      // provide the interface to the server
      globalOsmPluginMain.setInterface(osmInt);

      /* get the other managers, and start them up */
      eventMgr = gov.llnl.lc.infiniband.opensm.plugin.event.OsmEventManager.getInstance();
      eventMgr.init();
      
      dataMgr = gov.llnl.lc.infiniband.opensm.plugin.data.OsmUpdateManager.getInstance();
      dataMgr.init();
      
      /* the remote secure Osm Service */
      osmServer = gov.llnl.lc.net.MultiThreadSSLServer.getInstance();
      osmServer.init();
          
      /* start the monitor last, its not critical, just a watchdog, but all the things it
       * needs to watch should exist first
       */
      globalMonitor = gov.llnl.lc.infiniband.opensm.plugin.OsmPluginMonitor.getInstance();      
      globalMonitor.init();
      
      /* do whatever it takes to initialize the service */
      startThread();
      
      return success;
    }
    /*-----------------------------------------------------------------------*/
    
    private boolean getVersionInfo()
    {
      // need to get the version and date from the manifest file (use brute force)
      try
      {
        Enumeration<URL> resources = this.getClass().getClassLoader().getResources("META-INF/MANIFEST.MF");
        while (resources.hasMoreElements())
        {
          Manifest man = new Manifest(resources.nextElement().openStream());
          Attributes attr1 = man.getMainAttributes();
          String title = attr1.getValue("Implementation-Title");
          if("OsmClientServer".equalsIgnoreCase(title))
          {
            for(Object str: attr1.keySet())
            {
              if(str instanceof Name)
              {
                String str1 = ((Name)str).toString();
                if("Implementation-Version".equalsIgnoreCase(str1))
                  Version = attr1.getValue(str1);

                if("Built-Date".equalsIgnoreCase(str1))
                  BuildDate = attr1.getValue(str1);
              }
            }
          }
        }
      }
      catch (IOException E)
      {
        // handle
      }
      return true;
    }


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
       logger.info("Starting the OSM Java Plugin");
       PluginMain_Thread.start();
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
        logger.info("Stopping the " + PluginMain_Thread.getName() + " Thread");
        Continue_Thread = false;
      }
      /*-----------------------------------------------------------------------*/


      public static void destroy()
      {
        /* get the other managers, and shut them down too, typically in reverse order
         * from how they were initialized, see init() */
       globalMonitor.destroy();
       osmServer.destroy();
       dataMgr.destroy();
       eventMgr.destroy();
            
        logger.info("Terminating the OSM Java Plugin");
        globalOsmPluginMain.stopThread();
      }
      
    /**************************************************************************
    *** Method Name:
    ***     getInstance
    **/
    /**
    *** Get the singleton OsmPluginMain. This can be used if the application wants
    *** to share one manager across the whole JVM.  Currently I am not sure
    *** how this ought to be used.
    *** <p>
    ***
    *** @return       the GLOBAL (or shared) OsmPluginMain
    **************************************************************************/

    public static OsmPluginMain getInstance()
    {
      synchronized( OsmPluginMain.semaphore )
      {
        if ( globalOsmPluginMain == null )
        {
          globalOsmPluginMain = new OsmPluginMain( );
        }
        return globalOsmPluginMain;
      }
    }
    /*-----------------------------------------------------------------------*/
    public Object clone() throws CloneNotSupportedException 
    {
      throw new CloneNotSupportedException(); 
    }
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
      System.out.println("OsmPluginMain main() routine executed");
      gov.llnl.lc.infiniband.opensm.plugin.OsmPluginMain jPluginMain = gov.llnl.lc.infiniband.opensm.plugin.OsmPluginMain.getInstance();
      jPluginMain.init();
    }

    /**
     * @param args
     */
    public static void mainTest(String[] args)
    {
      System.out.println("OsmPluginMain main() routine executed");
      gov.llnl.lc.infiniband.opensm.plugin.OsmPluginMain jPluginMain = gov.llnl.lc.infiniband.opensm.plugin.OsmPluginMain.getInstance();
      System.err.println(jPluginMain.getVersion());
      System.err.println(jPluginMain.getBuildDate());
    }

    /************************************************************
     * Method Name:
     *  getVersion
     **/
    /**
     * Returns the value of version
     *
     * @return the version
     *
     ***********************************************************/
    
    public String getVersion()
    {
      return Version;
    }


    /************************************************************
     * Method Name:
     *  getBuildDate
     **/
    /**
     * Returns the value of buildDate
     *
     * @return the buildDate
     *
     ***********************************************************/
    
    public String getBuildDate()
    {
      return BuildDate;
    }


    /************************************************************
     * Method Name:
     *  run
    **/
    /**
     * This is the "MAIN" thread, that is responsible for monitoring the
     * status of all the other threads.  This would be where any sort
     * of system wide condition would be detected or reported.
     *
     * @see java.lang.Runnable#run()
     *     
     * @param   describe the parameters
     *
     * @return  describe the value returned
     ***********************************************************/
    @Override
    public void run()
    {
      long eventMgrHB   = 0L;
      long updateMgrHB  = 0L;
      long monitorMgrHB = 0L;
      long serverHB     = 0L;
      
      long prevEventMgrHB   = 0L;
      long prevUpdateMgrHB  = 0L;
      long prevMonitorMgrHB = 0L;
      long prevServerHB     = 0L;
      
      
      // check the Thread Termination Flag, and continue if Okay
      while(Continue_Thread)
      {
        //dwell here until I implement this
        try
        {
          // dwell, while the other threads are working
          TimeUnit.SECONDS.sleep(MainThreadSleepSecs);
          
          logger.info("Looping inside the " + PluginMain_Thread.getName() + " Thread");
          // wake up, and check the status of everything
          //  notify if there is a problem
          //  go back to sleep, and let other threads work
          
          updateMgrHB = dataMgr.getHeartbeat();
          eventMgrHB  = eventMgr.getTotalEvents();
          monitorMgrHB = globalMonitor.getHeartbeat();
          serverHB = (long)osmServer.numCumulativeSessions();
          
          // check to see if the various heartbeats are running (changed)
          if(eventMgrHB == prevEventMgrHB)
            logger.warning("The number of events has not changed since the last period (" + eventMgrHB + ")" );
          if(updateMgrHB == prevUpdateMgrHB)
            logger.warning("The number of data updates have not changed since the last period (" + updateMgrHB + ")" );
          if(monitorMgrHB == prevMonitorMgrHB)
            logger.warning("The number of times sessions have been monitored has not changed since the last period (" + monitorMgrHB + ")" );
          if(serverHB == prevServerHB)
            logger.warning("The number of OMS sessions has not changed since the last period (" + serverHB + ")" );
          
          prevEventMgrHB   = eventMgrHB;
          prevUpdateMgrHB  = updateMgrHB;
          prevMonitorMgrHB = monitorMgrHB;
          prevServerHB     = serverHB;
          
        }
        catch (Exception e)
        {
          // nothing to do yet
        }
      }
      logger.info("Terminating the " + PluginMain_Thread.getName() + " Thread");
      /* fall through, must be done! */
    }

  }

