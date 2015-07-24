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
 *        file: OSM_EventSync.java
 *
 *  Created on: Jul 29, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.event;

import gov.llnl.lc.logging.CommonLogger;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**********************************************************************
 * The OSM Event Synchronization mechanism.  It is intended to implement
 * the one to many event pattern.  By design, one entity "set"s this
 * EventSync, while one or more entities can wait for the EventSync.
 * Each entity waiting (blocked) will receive the OsmEvent object, which
 * may be a timeout.
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Jul 29, 2011 2:07:46 PM
 **********************************************************************/
public class OSM_EventSync implements CommonLogger
{
  private OsmEvent event;
  
  private static final int MAX_AVAILABLE = 100;
  private final Semaphore available = new Semaphore(MAX_AVAILABLE, true);

  /************************************************************
   * Method Name:
   *  waitForEventSync
  **/
  /**
   * Blocks execution until an OsmEvent is available or the timeout elapses,
   * which ever comes first.  Many different objects in different threads
   * can "pend" on this "Sync" object, and all be released at once.
   *
   * @see     #setEventSync(OsmEvent)
   *
   * @param timeout  the number of SECONDS to wait for an event, before
   *                 an OSM_EVENT_TIMEOUT event is generated and returned
   * @return  the OsmEvent value set via the <code>setEventSync</code>
   * @throws InterruptedException
   ***********************************************************/
  public OsmEvent waitForEventSync(long timeout) throws InterruptedException 
  {
    /* return the object, or a timeout */
    if( available.tryAcquire(timeout, TimeUnit.SECONDS))
        return this.event;
    return OsmEvent.OSM_EVENT_TIMEOUT;
  }

  public void setEventSync(OsmEvent x) 
  {
    // broadcast this event out to everyone wait for it
      this.event = x;
      int num_waiting = available.getQueueLength();
      
//      if(num_waiting > 1)
//        classLogger.info("Num Waiting is: " + num_waiting);
      available.release(num_waiting > 1 ? num_waiting: 1);
  }


  /************************************************************
   * Method Name:
   *  OSM_EventSync
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param   describe the parameters if any
   *
   ***********************************************************/
  public OSM_EventSync()
  {
    this.event = OsmEvent.OSM_EVENT_MAX;
    this.available.drainPermits();
  }

}
