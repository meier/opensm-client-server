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
 *        file: OsmSearchManager.java
 *
 *  Created on: Jul 16, 2012
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.search;

import gov.llnl.lc.search.Query;
import gov.llnl.lc.search.QueryResults;
import gov.llnl.lc.search.SearchInterface;

import java.util.concurrent.TimeUnit;

/**********************************************************************
 * A Server-side object that is responsible for performing queries on
 * the opensm data, and returning appropriate results.
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Jul 16, 2012 11:21:58 AM
 **********************************************************************/
public class OsmSearchManager implements SearchInterface, Runnable, gov.llnl.lc.logging.CommonLogger
{
  /** the synchronization object **/
  private static Boolean semaphore            = new Boolean( true );
  
  /** the Managers search counter **/
  private static long OsmHeartbeat = 0;

  /** the Managers search period in seconds**/
  private static int OsmSearchPeriod = 600;
  
  /** the one and only <code>OsmSearchManager</code> Singleton **/
  private volatile static OsmSearchManager globalOsmSearchManager  = null;

  /** boolean specifying whether the thread should continue **/
  private volatile static boolean Continue_Thread = true;
  
  /** thread responsible for updating the data **/
  private static java.lang.Thread Update_Thread;
  
  private OsmSearchManager()
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
    
    logger.info("Initializing the SearchManager");
    
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
      logger.info("Terminating the SearchManager");
      stopThread();
    }
    
   /**************************************************************************
  *** Method Name:
  ***     getInstance
  **/
  /**
  *** Get the singleton OsmEventManager. This can be used if the application wants
  *** to share one manager across the whole JVM.  Currently I am not sure
  *** how this ought to be used.
  *** <p>
  ***
  *** @return       the GLOBAL (or shared) OsmSearchManager
  **************************************************************************/

  public static OsmSearchManager getInstance()
  {
    synchronized( OsmSearchManager.semaphore )
    {
      if ( globalOsmSearchManager == null )
      {
        globalOsmSearchManager = new OsmSearchManager( );
      }
      return globalOsmSearchManager;
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
    
    // check the Thread Termination Flag, and continue if Okay
    while(Continue_Thread)
    {
      try
      {
        incrementHeartbeat();
        TimeUnit.SECONDS.sleep(getSearchPeriod());  // wait for new data to become available

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

  public int getSearchPeriod()
  {
    synchronized (OsmSearchManager.semaphore)
    {
      return OsmSearchPeriod;
    }
  }

  public void setSearchPeriod(int secs)
  {
    synchronized (OsmSearchManager.semaphore)
    {
      /* enforce min/max value here */
      secs = (secs > 0)&& (secs < 120) ? secs: 30;
      OsmSearchPeriod = secs;
    }
  }

  public long getHeartbeat()
  {
    synchronized (OsmSearchManager.semaphore)
    {
      return OsmHeartbeat;
    }
  }

  public void incrementHeartbeat()
  {
    synchronized (OsmSearchManager.semaphore)
    {
      OsmHeartbeat++;
    }
  }

  /************************************************************
   * Method Name:
   *  search
   **/
  /**
   * Describe the method here
   *
   * @see gov.llnl.lc.search.SearchInterface#search(gov.llnl.lc.search.Query)

   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @param query
   * @return
   ***********************************************************/

  @Override
  public QueryResults search(Query query)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /************************************************************
   * Method Name:
   *  search
   **/
  /**
   * Describe the method here
   *
   * @see gov.llnl.lc.search.SearchInterface#search(gov.llnl.lc.search.Query, int)

   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @param query
   * @param maxResults
   * @return
   ***********************************************************/

  @Override
  public QueryResults search(Query query, int maxResults)
  {
    // TODO Auto-generated method stub
    return null;
  }

}
