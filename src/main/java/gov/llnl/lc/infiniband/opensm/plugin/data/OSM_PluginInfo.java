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
 *        file: OSM_PluginInfo.java
 *
 *  Created on: Nov 14, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import gov.llnl.lc.infiniband.core.NativePeerClass;

import java.io.Serializable;

/**********************************************************************
 * A peer object that contains OSM_PluginInfo
 * <p>
 * @see NativePeerClass
 *
 * @author meier3
 * 
 * @version Nov 14, 2011 11:00:05 AM
 **********************************************************************/
@NativePeerClass("v1.0")
public class OSM_PluginInfo implements Serializable
{
  /*
   * This is the developer and diagnostic data structure, intended to
   * provide heartbeat counters as well as any other information
   * associated with the event plugin.
   * 
typedef struct jpi_plugin_info
{
  jint update_period;
  jint report_period;
  jint event_timeout_ms;
  jlong update_count;
  jlong event_count;
} jpi_Plugin_t;

   */
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = -7513626028055627885L;

  public int NativeUpdatePeriodSecs;
  public int NativeReportPeriodSecs;
  public int NativeEventTimeoutMsecs;
  
  public long NativeUpdateCount;
  public long NativeEventCount;

  /************************************************************
   * Method Name:
   *  OSM_PluginInfo
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param   describe the parameters if any
   *
   ***********************************************************/
  public OSM_PluginInfo()
  {
  }

  /************************************************************
   * Method Name:
   *  OSM_PluginInfo
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param   describe the parameters if any
   *
   * @param nativeUpdatePeriodSecs
   * @param nativeHearbeatPeriodSecs
   * @param nativeEventTimeoutMsecs
   * @param nativeHeartbeatCount
   * @param nativeEventCount
   ***********************************************************/
  public OSM_PluginInfo(int nativeUpdatePeriodSecs, int nativeReportPeriodSecs,
      int nativeEventTimeoutMsecs, long nativeUpdateCount, long nativeEventCount)
  {
    super();
    NativeUpdatePeriodSecs = nativeUpdatePeriodSecs;
    NativeReportPeriodSecs = nativeReportPeriodSecs;
    NativeEventTimeoutMsecs = nativeEventTimeoutMsecs;
    NativeUpdateCount = nativeUpdateCount;
    NativeEventCount = nativeEventCount;
  }

  /************************************************************
   * Method Name:
   *  toString
  **/
  /**
   * Describe the method here
   *
   * @see java.lang.Object#toString()
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @return
   ***********************************************************/
  
  @Override
  public String toString()
  {
    return "OSM_PluginInfo [NativeUpdatePeriodSecs=" + NativeUpdatePeriodSecs
        + ", NativeReportPeriodSecs=" + NativeReportPeriodSecs + ", NativeEventTimeoutMsecs="
        + NativeEventTimeoutMsecs + ", NativeUpdateCount=" + NativeUpdateCount
        + ", NativeEventCount=" + NativeEventCount + "]";
  }
}
