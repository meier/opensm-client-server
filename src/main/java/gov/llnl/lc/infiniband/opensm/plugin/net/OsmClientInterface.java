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
 *        file: OsmClientInterface.java
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
import gov.llnl.lc.logging.CommonLogger;
import gov.llnl.lc.net.ObjectProtocolConstants;

import java.io.EOFException;

/**********************************************************************
 * The <code>OsmClientInterface</code> provides detailed information
 * about the Infiniband Fabric as seen from the Subnet Manager.  In this
 * case, "client" refers to the typical user of this interface, and not
 * the information and functionality it provides.
 * <p>
 * Most "client" application that attach to the OSM Monitoring Service
 * would use this interface to obtain subnet information.
 * <p>
 * @see  OsmApiSession#getClientApi()
 * @see  OsmObjectClient
 *
 * @author meier3
 * 
 * @version Jun 27, 2011 12:16:30 PM
 **********************************************************************/
public class OsmClientInterface implements OsmClientApi, CommonLogger
{
  private OsmApiSession Session;
  private OsmObjectClient Protocol;

  public OsmClientInterface(OsmApiSession session)
  {
    Session = session;
    Protocol = Session.ClientProtocol;
  }

  @Override
  public OSM_Nodes getOsmNodes() throws Exception
  {
    Object inObj = null;
    try
    {
      inObj = Protocol.getOsmNodes();
    }
    catch (Exception e)
    {
      logger.severe(e.getMessage());
      Session.setConnected(false);
      throw new EOFException(ObjectProtocolConstants.CONNECTION_CLOSED);
    }
    
    if (inObj instanceof OSM_Nodes)
      return (OSM_Nodes)inObj;
    return (OSM_Nodes)inObj;
  }

  
  @Override
  public OSM_SysInfo getOsmSysInfo() throws Exception
  {
      Object inObj = null;
      try
      {
        inObj = Protocol.getOsmSysInfo();
      }
      catch (Exception e)
      {
        logger.severe(e.getMessage());
        Session.setConnected(false);
        throw new EOFException(ObjectProtocolConstants.CONNECTION_CLOSED);
      }
      
      if (inObj instanceof OSM_SysInfo)
        return (OSM_SysInfo)inObj;
      return (OSM_SysInfo)inObj;
  }

  
  @Override
  public OSM_Ports getOsmPorts() throws Exception
  {
      Object inObj = null;
      try
      {
        inObj = Protocol.getOsmPorts();
      }
      catch (Exception e)
      {
        logger.severe(e.getMessage());
        Session.setConnected(false);
        throw new EOFException(ObjectProtocolConstants.CONNECTION_CLOSED);
      }
      
      if (inObj instanceof OSM_Ports)
        return (OSM_Ports)inObj;
      return (OSM_Ports)inObj;
  }

  @Override
  public OSM_Stats getOsmStats() throws Exception
  {
      Object inObj = null;
      try
      {
        inObj = Protocol.getOsmStats();
      }
      catch (Exception e)
      {
        logger.severe(e.getMessage());
        Session.setConnected(false);
        throw new EOFException(ObjectProtocolConstants.CONNECTION_CLOSED);
      }
      
      if (inObj instanceof OSM_Stats)
        return (OSM_Stats)inObj;
      return (OSM_Stats)inObj;
    }

  
  @Override
  public OSM_Subnet getOsmSubnet() throws Exception
  {
    Object inObj = null;
    try
    {
      inObj = Protocol.getOsmSubnet();
    }
    catch (Exception e)
    {
      logger.severe(e.getMessage());
      Session.setConnected(false);
      throw new EOFException(ObjectProtocolConstants.CONNECTION_CLOSED);
    }
    
    if (inObj instanceof OSM_Subnet)
      return (OSM_Subnet)inObj;
    return (OSM_Subnet)inObj;
  }

  @Override
  public OSM_Fabric getOsmFabric() throws Exception
  {
    Object inObj = null;
    try
    {
      inObj = Protocol.getOMS_Fabric();
    }
    catch (Exception e)
    {
      logger.severe(e.getMessage());
      Session.setConnected(false);
      throw new EOFException(ObjectProtocolConstants.CONNECTION_CLOSED);
    }
    
    if (inObj instanceof OSM_Fabric)
      return (OSM_Fabric)inObj;
    return (OSM_Fabric)inObj;
  }

  @Override
  public OSM_Configuration getOsmConfig() throws Exception
  {
    Object inObj = null;
    try
    {
      inObj = Protocol.getOsmConfiguration();
    }
    catch (Exception e)
    {
      logger.severe(e.getMessage());
      Session.setConnected(false);
      throw new EOFException(ObjectProtocolConstants.CONNECTION_CLOSED);
    }
    
    if (inObj instanceof OSM_Configuration)
      return (OSM_Configuration)inObj;
    return (OSM_Configuration)inObj;
  }

  @Override
  public OMS_List getOsmHistory() throws Exception
  {
    Object inObj = null;
    try
    {
      inObj = Protocol.getOMS_List();
    }
    catch (Exception e)
    {
      logger.severe(e.getMessage());
      Session.setConnected(false);
      throw new EOFException(ObjectProtocolConstants.CONNECTION_CLOSED);
    }
    
    if (inObj instanceof OMS_List)
      return (OMS_List)inObj;
    return (OMS_List)inObj;
  }

  @Override
  public OpenSmMonitorService getOsmMonitorService() throws Exception
  {
    Object inObj = null;
    try
    {
      inObj = Protocol.getOMS();
    }
    catch (Exception e)
    {
      logger.severe(e.getMessage());
      Session.setConnected(false);
      throw new EOFException(ObjectProtocolConstants.CONNECTION_CLOSED);
    }
    
    if (inObj instanceof OpenSmMonitorService)
      return (OpenSmMonitorService)inObj;
    return (OpenSmMonitorService)inObj;
  }
}
