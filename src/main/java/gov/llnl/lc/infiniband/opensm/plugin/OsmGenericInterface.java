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
 *        file: OsmGenericInterface.java
 *
 *  Created on: Jul 7, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin;

import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Nodes;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_PluginInfo;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Ports;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Stats;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Subnet;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_SysInfo;
import gov.llnl.lc.logging.CommonLogger;

public class OsmGenericInterface implements OsmInterface, CommonLogger, OsmPluginConstants
{

  
  /** the name of the Interface **/
  private static String InterfaceName = "OsmGenericInterface";
  
  /**************************************************************************
   *** Method Name:
   ***     getVersion
   **/
   /**
   *** Returns a String that represents the version of the plugin.
   *** <p>
   ***
   *** @return       the version String
   **************************************************************************/

  public String getVersion()
  {
    return "Version Generic";
  }
  /*-----------------------------------------------------------------------*/

  /**************************************************************************
  *** Method Name:
  ***     wait_for_event
  **/
  /**
  *** Blocks until an event occurs, or the timeout expires.  The nature of the
  *** event is indicated by the return value.
  *** <p>
  ***
  *** @see          Method_related_to_this_method
  ***
  *** @param        timeout  the maximum number of milliseconds to wait before
  ***                        timing out.  If zero, then wait forever.
  ***
  *** @return       the nature of the interrupt
  **************************************************************************/

  public int wait_for_event(int timeout)
  {
    try
    {
      java.lang.Thread.sleep( 300);  // wait for the specified number of millisecs
    } catch ( InterruptedException e )
    {
    }
    return(OSM_SYNC_UNKNOWN);
  }

  /**************************************************************************
  *** Method Name:
  ***     getInterfaceName
  **/
  /**
  *** Returns the actual name of the concrete object or class that implements this
  *** interface.
  *** <p>
  ***
  *** @return       the name of the actual OsmInterface
  **************************************************************************/

  public String getInterfaceName()
  {
    return InterfaceName;
  }
  /*-----------------------------------------------------------------------*/

  @Override
  public OSM_Nodes getOsmNodes()
  {
    // TODO Auto-generated method stub
    return null;
  }


  /************************************************************
   * Method Name:
   *  getOsmPorts
  **/
  /**
   * Describe the method here
   *
   * @see gov.llnl.lc.infiniband.opensm.plugin.OsmInterface#getOsmPorts()
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @return
   ***********************************************************/
  
  @Override
  public OSM_Ports getOsmPorts()
  {
    // TODO Auto-generated method stub
    return null;
  }

  /************************************************************
   * Method Name:
   *  getOsmStats
  **/
  /**
   * Describe the method here
   *
   * @see gov.llnl.lc.infiniband.opensm.plugin.OsmInterface#getOsmStats()
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @return
   ***********************************************************/
  
  @Override
  public OSM_Stats getOsmStats()
  {
    // TODO Auto-generated method stub
    return null;
  }

  /************************************************************
   * Method Name:
   *  getOsmSysInfo
  **/
  /**
   * Describe the method here
   *
   * @see gov.llnl.lc.infiniband.opensm.plugin.OsmInterface#getOsmSysInfo()
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @return
   ***********************************************************/
  
  @Override
  public OSM_SysInfo getOsmSysInfo()
  {
    // TODO Auto-generated method stub
    return null;
  }

  /************************************************************
   * Method Name:
   *  getOsmSubnet
  **/
  /**
   * Describe the method here
   *
   * @see gov.llnl.lc.infiniband.opensm.plugin.OsmInterface#getOsmSubnet()
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @return
   ***********************************************************/
  
  @Override
  public OSM_Subnet getOsmSubnet()
  {
    // TODO Auto-generated method stub
    return null;
  }

  /************************************************************
   * Method Name:
   *  getPluginInfo
  **/
  /**
   * Describe the method here
   *
   * @see gov.llnl.lc.infiniband.opensm.plugin.OsmInterface#getPluginInfo()
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @return
   ***********************************************************/
  
  @Override
  public OSM_PluginInfo getPluginInfo()
  {
    // TODO Auto-generated method stub
    return null;
  }

  /**************************************************************************
   *** Method Name:
   ***     invokeCommand
   **/
   /**
   *** Invoke a native command and return the result status in the form of a
   *** string.  The type of command to invoke is specified by an integer, and
   *** can be modified by command arguments provided through a single string.
   *** <p>
   ***
   * @see gov.llnl.lc.infiniband.opensm.plugin.OsmInterface#invokeCommand()
   * 
   *** @return       the results of invoking the command, in string form
   **************************************************************************/

  @Override
   public String invokeCommand(int cmdNum, String cmdArgs)
  {
    return "Command #: " + cmdNum + ", Command Args: " + cmdArgs;
  }

}
