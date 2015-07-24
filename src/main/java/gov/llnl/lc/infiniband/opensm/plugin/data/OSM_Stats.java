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
 *        file: OSM_SysInfo.java
 *
 *  Created on: Jun 30, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import gov.llnl.lc.infiniband.core.NativePeerClass;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmClientInterface;

import java.io.Serializable;

/**********************************************************************
 * An <code>OSM_Stats</code> object contains statistical information
 * about the subnet management datagrams, or MADs.
 * <p>
 * Java Peer Class, to the native interface.  All peer classes contain
 * public members which can be directly accessed.  All peer classes
 * have fully parameterized constructors that are used by the native
 * layer to create an instance of the class.  Instances of peer classes
 * are not intended to be created from the java environment.
 * <p>
 * @see  OsmClientInterface#getOsmStats()
 * @see NativePeerClass
 *
 * @author meier3
 * 
 * @version Aug 29, 2011 12:33:22 PM
 **********************************************************************/
@NativePeerClass("v1.0")
public class OSM_Stats implements Serializable
{
  /*
   * defined in header osm_stats.h and below;
   * 
typedef struct osm_stats {
  atomic32_t qp0_mads_outstanding;
  atomic32_t qp0_mads_outstanding_on_wire;
  atomic32_t qp0_mads_rcvd;
  atomic32_t qp0_mads_sent;
  atomic32_t qp0_unicasts_sent;
  atomic32_t qp0_mads_rcvd_unknown;
  atomic32_t sa_mads_outstanding;
  atomic32_t sa_mads_rcvd;
  atomic32_t sa_mads_sent;
  atomic32_t sa_mads_rcvd_unknown;
  atomic32_t sa_mads_ignored;
#ifdef HAVE_LIBPTHREAD
  pthread_mutex_t mutex;
  pthread_cond_t cond;
#else
  cl_event_t event;
#endif
} osm_stats_t;

   * the actual peer class in jni_SharedResources.h
   * 
typedef struct jst_stats
{
  // natively 32 bits, should they be 64?
  jlong qp0_mads_outstanding;
  jlong qp0_mads_outstanding_on_wire;
  jlong qp0_mads_rcvd;
  jlong qp0_mads_sent;
  jlong qp0_unicasts_sent;
  jlong qp0_mads_rcvd_unknown;
  jlong sa_mads_outstanding;
  jlong sa_mads_rcvd;
  jlong sa_mads_sent;
  jlong sa_mads_rcvd_unknown;
  jlong sa_mads_ignored;
} jst_Stats_t;

   */
  
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = 9098069761912408483L;

  /* natively 32 bits, should they be 64? */
  public long qp0_mads_outstanding;
  public long qp0_mads_outstanding_on_wire;
  public long qp0_mads_rcvd;
  public long qp0_mads_sent;
  public long qp0_unicasts_sent;
  public long qp0_mads_rcvd_unknown;
  public long sa_mads_outstanding;
  public long sa_mads_rcvd;
  public long sa_mads_sent;
  public long sa_mads_rcvd_unknown;
  public long sa_mads_ignored;

  /************************************************************
   * Method Name:
   *  OSM_Stats
   */
   /**
   * Default constructor
   *
   ***********************************************************/
  public OSM_Stats()
  {
  }

  /************************************************************
   * Method Name:
   *  OSM_Stats
   */
   /**
 *  The fully parameterized constructor used by the native layer
 * to create an instance of this peer class.
   *
   * @param qp0_mads_outstanding
   * @param qp0_mads_outstanding_on_wire
   * @param qp0_mads_rcvd
   * @param qp0_mads_sent
   * @param qp0_unicasts_sent
   * @param qp0_mads_rcvd_unknown
   * @param sa_mads_outstanding
   * @param sa_mads_rcvd
   * @param sa_mads_sent
   * @param sa_mads_rcvd_unknown
   * @param sa_mads_ignored
   ***********************************************************/
  public OSM_Stats(long qp0_mads_outstanding, long qp0_mads_outstanding_on_wire,
      long qp0_mads_rcvd, long qp0_mads_sent, long qp0_unicasts_sent, long qp0_mads_rcvd_unknown,
      long sa_mads_outstanding, long sa_mads_rcvd, long sa_mads_sent, long sa_mads_rcvd_unknown,
      long sa_mads_ignored)
  {
    super();
    this.qp0_mads_outstanding = qp0_mads_outstanding;
    this.qp0_mads_outstanding_on_wire = qp0_mads_outstanding_on_wire;
    this.qp0_mads_rcvd = qp0_mads_rcvd;
    this.qp0_mads_sent = qp0_mads_sent;
    this.qp0_unicasts_sent = qp0_unicasts_sent;
    this.qp0_mads_rcvd_unknown = qp0_mads_rcvd_unknown;
    this.sa_mads_outstanding = sa_mads_outstanding;
    this.sa_mads_rcvd = sa_mads_rcvd;
    this.sa_mads_sent = sa_mads_sent;
    this.sa_mads_rcvd_unknown = sa_mads_rcvd_unknown;
    this.sa_mads_ignored = sa_mads_ignored;
  }

  @Override
  public String toString()
  {
    return "OSM_Stats [qp0_mads_outstanding=" + qp0_mads_outstanding
        + ", qp0_mads_outstanding_on_wire=" + qp0_mads_outstanding_on_wire + ", qp0_mads_rcvd="
        + qp0_mads_rcvd + ", qp0_mads_sent=" + qp0_mads_sent + ", qp0_unicasts_sent="
        + qp0_unicasts_sent + ", qp0_mads_rcvd_unknown=" + qp0_mads_rcvd_unknown
        + ", sa_mads_outstanding=" + sa_mads_outstanding + ", sa_mads_rcvd=" + sa_mads_rcvd
        + ", sa_mads_sent=" + sa_mads_sent + ", sa_mads_rcvd_unknown=" + sa_mads_rcvd_unknown
        + ", sa_mads_ignored=" + sa_mads_ignored + "]";
  }
  
}
