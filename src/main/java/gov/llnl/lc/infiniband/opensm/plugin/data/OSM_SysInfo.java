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
import java.util.Arrays;

/**********************************************************************
 * The top level object which contains system level information about
 * the subnet.
 * <p>
 * Java Peer Class, to the native interface.  All peer classes contain
 * public members which can be directly accessed.  All peer classes
 * have fully parameterized constructors that are used by the native
 * layer to create an instance of the class.  Instances of peer classes
 * are not intended to be created from the java environment.
 * <p>
 * @see  OsmClientInterface#getOsmSysInfo()
 * @see  SBN_NodePortStatus
 * @see NativePeerClass
 *
 * @author meier3
 * 
 * @version Aug 26, 2011 2:43:55 PM
 **********************************************************************/
@NativePeerClass("v1.0")
public class OSM_SysInfo implements Serializable
{
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = -6765527306078675233L;
/*
 * refer to jni_SysInfo.h, jni_SysInfo.c, and sr_getSystemInfo()
 * 
  typedef struct jsi_system_info{
    jint SM_Priority;
    jint PM_SweepTime;
    jint PM_OutstandingQueries;
    jint PM_MaximumQueries;
    jint numPlugins;

    char OpenSM_Version[MAX_STRING_SIZE];
    char OsmJpi_Version[MAX_STRING_SIZE];
    char SM_State[MAX_STRING_SIZE];
    char SA_State[MAX_STRING_SIZE];
    char PM_State[MAX_STRING_SIZE];
    char PM_SweepState[MAX_STRING_SIZE];
    char RoutingEngine[MAX_STRING_SIZE];
    char EventPlugins[MAX_NUM_PLUGINS][MAX_STRING_SIZE];
} jsi_SystemInfo_t;

*/  
  /**  describe SM_Priority here **/
  public int                       SM_Priority;

  /**  describe PM_SweepTime here **/
  public int                       PM_SweepTime;
  
  /**  describe PM_OutstandingQueries here **/
  public int                       PM_OutstandingQueries;
  
  /**  describe PM_MaximumQueries here **/
  public int                       PM_MaximumQueries;

  /**  describe OpenSM_Version here **/
  public String                    OpenSM_Version;
  
  /**  describe OsmJpi_Version here **/
  public String                    OsmJpi_Version;
  
  /**  describe SM_State here **/
  public String                    SM_State;
  
  /**  describe SA_State here **/
  public String                    SA_State;
  
  /**  describe PM_State here **/
  public String                    PM_State;
  
  /**  describe PM_SweepState here **/
  public String                    PM_SweepState;
  
  /**  describe RoutingEngine here **/
  public String                    RoutingEngine;
  
  /**  describe EventPlugins here **/
  public String[]                  EventPlugins;

  /**  describe CA_PortStatus here **/
  public SBN_NodePortStatus        CA_PortStatus;
  
  /**  describe SW_PortStatus here **/
  public SBN_NodePortStatus        SW_PortStatus;
  
  /**  describe RT_PortStatus here **/
  public SBN_NodePortStatus        RT_PortStatus;

  /************************************************************
   * Method Name:
   *  OSM_SysInfo
   */
   /** Default constructor.
   *
   ***********************************************************/
  public OSM_SysInfo()
  {
  }

  /************************************************************
   * Method Name:
   *  OSM_SysInfo
   */
  /** The fully parameterized constructor used by the native layer
  * to create an instance of this peer class.
   *
   *
   * @see     SBN_NodePortStatus
   *
   * @param sM_Priority the subnet managers priority
   * @param pM_SweepTime
   * @param pM_OutstandingQueries
   * @param pM_MaximumQueries
   * @param openSM_Version
   * @param osmJpi_Version
   * @param sM_State
   * @param sA_State
   * @param pM_State
   * @param pM_SweepState
   * @param routingEngine
   * @param eventPlugins
   * @param cA_PortStatus
   * @param sW_PortStatus
   * @param rT_PortStatus
   ***********************************************************/
  public OSM_SysInfo(int sM_Priority, int pM_SweepTime, int pM_OutstandingQueries,
      int pM_MaximumQueries, String openSM_Version, String osmJpi_Version, String sM_State,
      String sA_State, String pM_State, String pM_SweepState, String routingEngine,
      String[] eventPlugins, SBN_NodePortStatus cA_PortStatus, SBN_NodePortStatus sW_PortStatus,
      SBN_NodePortStatus rT_PortStatus)
  {
    super();
    SM_Priority = sM_Priority;
    PM_SweepTime = pM_SweepTime;
    PM_OutstandingQueries = pM_OutstandingQueries;
    PM_MaximumQueries = pM_MaximumQueries;
    OpenSM_Version = openSM_Version;
    OsmJpi_Version = osmJpi_Version;
    SM_State = sM_State;
    SA_State = sA_State;
    PM_State = pM_State;
    PM_SweepState = pM_SweepState;
    RoutingEngine = routingEngine;
    EventPlugins = eventPlugins;
    CA_PortStatus = cA_PortStatus;
    SW_PortStatus = sW_PortStatus;
    RT_PortStatus = rT_PortStatus;
  }

  @Override
  public String toString()
  {
    return "OSM_SysInfo [SM_Priority=" + SM_Priority + ", PM_SweepTime=" + PM_SweepTime
        + ", PM_OutstandingQueries=" + PM_OutstandingQueries + ", PM_MaximumQueries="
        + PM_MaximumQueries + ", OpenSM_Version=" + OpenSM_Version + ", OsmJpi_Version="
        + OsmJpi_Version + ", SM_State=" + SM_State + ", SA_State=" + SA_State + ", PM_State="
        + PM_State + ", PM_SweepState=" + PM_SweepState + ", RoutingEngine=" + RoutingEngine
        + ", EventPlugins=" + Arrays.toString(EventPlugins) + ",\n CA_PortStatus=" + CA_PortStatus
        + ",\n SW_PortStatus=" + SW_PortStatus + ",\n RT_PortStatus=" + RT_PortStatus + "]";
  }
}
