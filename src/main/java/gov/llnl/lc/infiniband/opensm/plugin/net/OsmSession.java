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
 *        file: OsmSession.java
 *
 *  Created on: Jul 7, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.net;

import gov.llnl.lc.net.ObjectSessionApi;

/**********************************************************************
 * An OsmSession represents a transaction based connection between a
 * client and the OSM Monitoring Service on a remote host.  A session
 * can provide information about itself, as well the various API's that
 * are supported by the service.
 * <p>
 * @see  OsmApiSession
 *
 * @author meier3
 * 
 * @version Jul 7, 2011 7:47:24 AM
 **********************************************************************/
public interface OsmSession extends ObjectSessionApi
{
  /**  the maximum number of child (or clone) sessions from a single Parent **/
  public static final int MaxClones = 8;
  
  /************************************************************
   * Method Name:
   *  getClientApi
  **/
  /**
   * Returns an application programming interface (API) specifying
   * how to obtain information about the subnet.  This is the primary
   * interface that client applications will use to interrogate the
   * fabric.
   *
   * @return the interface
   ***********************************************************/
  public OsmClientApi getClientApi();
  
  /************************************************************
   * Method Name:
   *  getAdminApi
  **/
  /**
   * Returns an application programming interface (API) specifying
   * how to obtain privileged information, or to cause changes.
   * Currently, this interface primarily covers session control.
   *
   * @return the interface
   ***********************************************************/
  public OsmAdminApi getAdminApi();
  
  /************************************************************
   * Method Name:
   *  getEventApi
  **/
  /**
   * Returns an application programming interface (API) specifying
   * how to obtain information about the subnet events.
   *
   * @return the interface
   ***********************************************************/
  public OsmEventApi getEventApi();
  
  /************************************************************
   * Method Name:
   *  getTestApi
  **/
  /**
   * Returns an application programming interface (API) specifying
   * how to perform diagnostic tests and/or obtain debug information.
   * It may also be used for new or undocumented features.
   *
   * @return the interface
   ***********************************************************/
  public OsmTestApi getTestApi();
  
  /************************************************************
   * Method Name:
   *  cloneSession
  **/
  /**
   * Creates a new session using this sessions attributes.  Only
   * A parent session can be cloned.
   *
   * @see     OsmApiSession
   * @return a new session that is a "child" or clone of this one.
   * @throws Exception
   ***********************************************************/
  public OsmSession cloneSession() throws Exception;
}
