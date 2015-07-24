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
 *        file: OsmClientApi.java
 *
 *  Created on: Jun 27, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.net;

import gov.llnl.lc.infiniband.opensm.plugin.data.OMS_List;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Configuration;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Fabric;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Nodes;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Ports;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Stats;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Subnet;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_SysInfo;
import gov.llnl.lc.infiniband.opensm.plugin.data.OpenSmMonitorService;

/**********************************************************************
 * The <code>OsmClientApi</code> provides detailed information
 * about the Infiniband Fabric as seen from the Subnet Manager.  In this
 * case, "client" refers to the typical user of this interface, and not
 * the information and functionality it provides.
 * <p>
 * Most "client" application that attach to the OSM Monitoring Service
 * would use this interface to obtain subnet information.
 * <p>
 *
 * @author meier3
 * 
 * @version Jun 27, 2011 11:51:58 AM
 **********************************************************************/
public interface OsmClientApi
{
  /************************************************************
   * Method Name:
   *  getOsmFabric
  **/
  /**
   * Obtains all of the subnet information.  An OSM_Fabric object
   * is simply a container of nodes, ports, and other subnet objects.
   *
   * @return  an object containing information about the fabric
   ***********************************************************/
  public OSM_Fabric getOsmFabric() throws Exception;
  
  /************************************************************
   * Method Name:
   *  getOsmMonitorService
  **/
  /**
   * Obtains all of the subnet information, plus misc. information
   * about the service itself (client session, server info, etc).
   *
   * @return  an object containing information about the service
   *          and fabric.
   ***********************************************************/
  public OpenSmMonitorService getOsmMonitorService() throws Exception;
  
  /************************************************************
   * Method Name:
   *  getOsmConfig
  **/
  /**
   * Obtains fabric configuration information from a variety of
   * sources, most noteably the ibfabricconfig.xml file, and the
   * ib-node-name-map file.
   *
   * @return  an object containing fabric configuration info.
   ***********************************************************/
  public OSM_Configuration getOsmConfig() throws Exception;
  
  /************************************************************
   * Method Name:
   *  getOsmHistory
  **/
  /**
   * Obtains a cache of 'OpenSmMonitorService' objects.  The cache
   * is between 0 and 2 in size.  It typically contains the two
   * most recent instances of the OMS.
   *
   * @return  an object containing the last two OMS instances.
   ***********************************************************/
  public OMS_List getOsmHistory() throws Exception;
  
  /************************************************************
   * Method Name:
   *  getOsmNodes
  **/
  /**
   * Obtains information about the Nodes in the subnet.
   *
   * @return  an object containing information about the Nodes
   ***********************************************************/
  public OSM_Nodes getOsmNodes() throws Exception;
  
  /************************************************************
   * Method Name:
   *  getOsmSysInfo
  **/
  /**
   * Obtains high level system information about the subnet.
   *
   * @return  an object containing system level information.
   ***********************************************************/
  public OSM_SysInfo getOsmSysInfo() throws Exception;

  /************************************************************
   * Method Name:
   *  getOsmPorts
  **/
  /**
   * Obtains information abou the Ports in the subnet.
   *
   * @return  an object containing information about the Ports.
   ***********************************************************/
  public OSM_Ports getOsmPorts() throws Exception;
  
  /************************************************************
   * Method Name:
   *  getOsmStats
  **/
  /**
   * Obtains statistical information about the subnets management
   * datagrams, or MADs.
   *
   * @return  an object containing mad statistics
   ***********************************************************/
  public OSM_Stats getOsmStats() throws Exception;
  
  /************************************************************
   * Method Name:
   *  getOsmSubnet
  **/
  /**
   * Obtains subnet information in the form of the opensm data
   * structure known as <code>osm_subn</code>.
   *
   * @return  an object that mirrors the struct osm_subn
   ***********************************************************/
  public OSM_Subnet getOsmSubnet() throws Exception;
}
