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
 *        file: OsmServerStatus.java
 *
 *  Created on: Nov 14, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.net;

import gov.llnl.lc.logging.CommonLogger;
import gov.llnl.lc.net.MultiSSLServerStatus;
import gov.llnl.lc.time.TimeStamp;
import gov.llnl.lc.util.SystemConstants;

import java.io.Serializable;

/**********************************************************************
 * Describe purpose and responsibility of OsmServerStatus
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Dec 14, 2014 9:54:27 AM
 **********************************************************************/
public class OsmServerStatus implements CommonLogger, SystemConstants, Serializable
{

  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = 2415274927282067901L;

  public MultiSSLServerStatus Server;
  
  public int MaxParentSessions;
  public int MaxChildSessions;
  
  public int NativeUpdatePeriodSecs;
  public int NativeReportPeriodSecs;
  public int ServerUpdatePeriodSecs;
  public int NativeEventTimeoutMsecs;
  
  public long NativeHeartbeatCount;
  public long ServerHeartbeatCount;
  
  public long NativeEventCount;
  
  public long TimeInMillis;
  
  public boolean FollowNativeUpdates;
  public boolean AllowLocalHost;
  
  public String Version;
  public String BuildDate;
  
/************************************************************
   * Method Name:
   *  OsmServerStatus
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param server
   * @param maxParentSessions
   * @param maxChildSessions
   * @param nativeUpdatePeriodSecs
   * @param nativeReportPeriodSecs
   * @param serverUpdatePeriodSecs
   * @param nativeEventTimeoutMsecs
   * @param nativeHeartbeatCount
   * @param serverHeartbeatCount
   * @param nativeEventCount
   * @param timeInMillis
   * @param followNativeUpdates
   * @param allowLocalHost
   * @param version
   * @param buildDate
   ***********************************************************/
  public OsmServerStatus(MultiSSLServerStatus server, int maxParentSessions, int maxChildSessions,
      int nativeUpdatePeriodSecs, int nativeReportPeriodSecs, int serverUpdatePeriodSecs,
      int nativeEventTimeoutMsecs, long nativeHeartbeatCount, long serverHeartbeatCount,
      long nativeEventCount, long timeInMillis, boolean followNativeUpdates,
      boolean allowLocalHost, String version, String buildDate)
  {
    super();
    Server = server;
    MaxParentSessions = maxParentSessions;
    MaxChildSessions = maxChildSessions;
    NativeUpdatePeriodSecs = nativeUpdatePeriodSecs;
    NativeReportPeriodSecs = nativeReportPeriodSecs;
    ServerUpdatePeriodSecs = serverUpdatePeriodSecs;
    NativeEventTimeoutMsecs = nativeEventTimeoutMsecs;
    NativeHeartbeatCount = nativeHeartbeatCount;
    ServerHeartbeatCount = serverHeartbeatCount;
    NativeEventCount = nativeEventCount;
    TimeInMillis = timeInMillis;
    FollowNativeUpdates = followNativeUpdates;
    AllowLocalHost = allowLocalHost;
    Version = version;
    BuildDate = buildDate;
  }
  
  
  /************************************************************
   * Method Name:
   *  OsmServerStatus
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param   describe the parameters if any
   *
   ***********************************************************/
  public OsmServerStatus()
  {
  }
  
  public TimeStamp getServerTime()
  {
    return new TimeStamp(TimeInMillis);
  }

  public long getServerTimeDiffFromNowInMillis()
  {
    return (new TimeStamp()).getTimeInMillis() - TimeInMillis;
  }


  /************************************************************
   * Method Name:
   *  OsmServerStatus
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param server
   * @param maxParentSessions
   * @param maxChildSessions
   * @param nativeUpdatePeriodSecs
   * @param nativeReportPeriodSecs
   * @param serverUpdatePeriodSecs
   * @param nativeEventTimeoutMsecs
   * @param nativeHeartbeatCount
   * @param serverHeartbeatCount
   * @param nativeEventCount
   * @param timeInMillis
   * @param followNativeUpdates
   * @param allowLocalHost
   ***********************************************************/
  public OsmServerStatus(MultiSSLServerStatus server, int maxParentSessions, int maxChildSessions,
      int nativeUpdatePeriodSecs, int nativeReportPeriodSecs, int serverUpdatePeriodSecs,
      int nativeEventTimeoutMsecs, long nativeHeartbeatCount, long serverHeartbeatCount,
      long nativeEventCount, long timeInMillis, boolean followNativeUpdates, boolean allowLocalHost)
  {
    this(server, maxParentSessions, maxChildSessions,
        nativeUpdatePeriodSecs, nativeReportPeriodSecs, serverUpdatePeriodSecs,
        nativeEventTimeoutMsecs, nativeHeartbeatCount, serverHeartbeatCount,
        nativeEventCount, timeInMillis, followNativeUpdates,
        allowLocalHost, "unknown", "unknown");
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
    return "OsmServerStatus [Server=" + Server + ", MaxParentSessions=" + MaxParentSessions
        + ", MaxChildSessions=" + MaxChildSessions + ", NativeUpdatePeriodSecs="
        + NativeUpdatePeriodSecs + ", NativeReportPeriodSecs=" + NativeReportPeriodSecs
        + ", ServerUpdatePeriodSecs=" + ServerUpdatePeriodSecs + ", NativeEventTimeoutMsecs="
        + NativeEventTimeoutMsecs + ", NativeHeartbeatCount=" + NativeHeartbeatCount
        + ", ServerHeartbeatCount=" + ServerHeartbeatCount + ", NativeEventCount="
        + NativeEventCount + ", TimeInMillis=" + TimeInMillis + ", FollowNativeUpdates="
        + FollowNativeUpdates + ", AllowLocalHost=" + AllowLocalHost + ", Version=" + Version
        + ", BuildDate=" + BuildDate + "]";
  }

}
