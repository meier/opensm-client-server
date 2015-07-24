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
 *        file: OsmPluginConstants.java
 *
 *  Created on: Jul 7, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin;

public interface OsmPluginConstants
{
  /*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*/
  /*~~~     Static Finals                                               !!!*/
  /*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*/

  /** OSM_EVENT_TYPES - refer to MASTER_SYNC_TYPES int jpi_synchronization.h **/

  static final int OSM_START_CAPTURE           =  0;  //
  static final int OSM_PACKET_FOR_FILTER       =  1;  //
  static final int OSM_NEW_DATA_READY          =  2;  //
  static final int OSM_MESSAGE_LOCK            =  3;  //
  static final int OSM_STOP_CAPTURE            =  4;  //
  static final int OSM_ALERT_FOR_PROCESSING    =  5;  //
  static final int OSM_ALERT_EVENT             =  6;  //
  static final int OSM_ALARM_EVENT             =  7;  //
  static final int OSM_WATCH_DOG_EVENT         =  8;  //
  static final int OSM_MSG_EVENT               =  9;  //
  static final int OSM_COUNTER_LOCK            = 10;  //
  static final int OSM_TIME_FILTER_LOCK        = 11;  //
  static final int OSM_PROBE_QUEUE_PARAMS_LOCK = 12;  //
  static final int OSM_PROBE_QUEUE_LOCK        = 13;  //
  static final int OSM_ALERT_ATTRIB_LOCK       = 14;  //
  static final int OSM_ALERT_LOCK              = 15;  //
  static final int OSM_ALARM_LOCK              = 16;  //
  static final int OSM_PROTO_STATS_LOCK        = 17;  //
  static final int OSM_CAPTURE_STATS_LOCK      = 18;  //
  static final int OSM_WATCHDOG_STATS_LOCK     = 19;  //
  static final int OSM_SYNC_UNKNOWN            = 20;  //
  
  static final int OSM_WAIT_TIMEOUT            = -8;  //
}
