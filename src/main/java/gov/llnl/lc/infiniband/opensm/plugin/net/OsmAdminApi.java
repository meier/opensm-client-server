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
 *        file: OsmAdminApi.java
 *
 *  Created on: Jun 29, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.net;

import gov.llnl.lc.net.ObjectSession;
import gov.llnl.lc.system.CommandLineArguments;
import gov.llnl.lc.system.CommandLineResults;
import gov.llnl.lc.system.whatsup.WhatsUpInfo;

/**********************************************************************
 * The <code>OsmAdminApi</code> provides information and functionality that typically
 * requires elevated privileges.  This "administrative" interface can
 * obtain information about other users or processes, as well as
 * provide primitive "control" functionality.
 * <p>
 * @author meier3
 * 
 * @version Jun 29, 2011 12:36:48 PM
 **********************************************************************/
public interface OsmAdminApi
{
  /************************************************************
   * Method Name:
   *   invokePrivilagedCommand
  **/
  /**
   * If the user has elevated privileges, then invoke the provided
   * command and return the results.  The command is invoked as
   * the OpenSmMonitorService, which is running as root.
   *
   * @return  the results of the executing the command.
   ***********************************************************/
  public CommandLineResults invokePrivilegedCommand(CommandLineArguments command) throws Exception;
  
  /************************************************************
   * Method Name:
   *   isCommandAuthorized
  **/
  /**
   * Returns TRUE if the supplied command is supported and enabled.  Most
   * privileged commands and be selectively enabled or disabled.  This
   * method provides a mechanism to check a command, before attempting to
   * invoke it.
   *
   * @return  TRUE if a special commands is supported and enabled
   ***********************************************************/
  public boolean isCommandAuthorized(CommandLineArguments command) throws Exception;
  
  /************************************************************
   * Method Name:
   *   isUserAuthorized
  **/
  /**
   * Returns TRUE if the supplied user has elevated privileges.
   *
   * @return  TRUE if the user can invoke special commands
   ***********************************************************/
  public boolean isUserAuthorized(OsmClientUserInfo user) throws Exception;
  
  /************************************************************
   * Method Name:
   *   getWhatsUpInfo
  **/
  /**
   * Invokes the 'whatsup' command on the management node, and
   * returns the results in the WhatsUpInfo object.
   *
   * @return  WhatsUpInfo
   ***********************************************************/
  public WhatsUpInfo getWhatsUpInfo() throws Exception;
  
  /************************************************************
   * Method Name:
   *   getServerStatus
  **/
  /**
   * Obtains information about the remote service.
   *
   * @return  an object containing information about the remote service.
   ***********************************************************/
  public OsmServerStatus getServerStatus() throws Exception;
  
  /************************************************************
   * Method Name:
   *  getSessionStatus
  **/
  /**
   * Obtains information about the session that corresponds to the
   * provided threadId.  The threadId is typically obtained by
   * calling <code>getServerStatus</code>.
   *
   * @see     #getServerStatus()
   *
   * @param threadId - the Id used by the remote service to uniquely identify a session.
   * @return  an object describing a session.
   ***********************************************************/
  public ObjectSession getSessionStatus(long threadId) throws Exception;
  
  /************************************************************
   * Method Name:
   *  killSession
  **/
  /**
   * Kills a remote session that corresponds to the provided threadId.
   * The threadId is typically obtained by calling <code>getServerStatus</code>.
   *
   * @see     #getServerStatus()
   *
   * @param threadId - the Id used by the remote service to uniquely identify a session.
   * @return  true if the session was killed.
   ***********************************************************/
  public boolean killSession(long threadId) throws Exception;
  
  /************************************************************
   * Method Name:
   *  clearSessionHistory
  **/
  /**
   * The Osm Monitoring Service maintains a session log that
   * contains current and previous session information.  This method
   * removes old session information.  Only active sessions will
   * remain in the log after this method is invoked.
   *
   * @return true if the operation was successful
   ***********************************************************/
  public boolean clearSessionHistory() throws Exception;
}
