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
 *        file: OsmAdminInterface.java
 *
 *  Created on: Jun 29, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.net;

import gov.llnl.lc.logging.CommonLogger;
import gov.llnl.lc.net.ObjectProtocolConstants;
import gov.llnl.lc.net.ObjectSession;
import gov.llnl.lc.system.CommandLineArguments;
import gov.llnl.lc.system.CommandLineResults;
import gov.llnl.lc.system.whatsup.WhatsUpInfo;

import java.io.EOFException;


/**********************************************************************
 * The OsmAdminInterface provides information and functionality that typically
 * requires elevated privileges.  This "administrative" interface can
 * obtain information about other users or processes, as well as
 * provide primitive "control" functionality.
 * <p>
 * @see  OsmApiSession#getAdminApi()
 * @see  OsmApiSession
 * @see  OsmObjectClient
 *
 * @author meier3
 * 
 * @version Jun 29, 2011 1:24:19 PM
 **********************************************************************/
public class OsmAdminInterface implements OsmAdminApi, CommonLogger
{
  private OsmApiSession Session;
  private OsmObjectClient Protocol;

  public OsmAdminInterface(OsmApiSession session)
  {
  Session = session;
  Protocol = Session.ClientProtocol;
  }

  @Override
  public OsmServerStatus getServerStatus() throws Exception
  {
    Object inObj = null;
    try
    {
      inObj = Protocol.getServerStatus();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      Session.setConnected(false);
      throw new EOFException(ObjectProtocolConstants.CONNECTION_CLOSED);
    }
    
    if (inObj instanceof OsmServerStatus)
      return (OsmServerStatus)inObj;
    return (OsmServerStatus)inObj;
  }

  
  @Override
  public ObjectSession getSessionStatus(long threadId) throws Exception
  {
    Object inObj = null;
    try
    {
      inObj = Protocol.getSessionStatus(threadId);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      Session.setConnected(false);
      throw new EOFException(ObjectProtocolConstants.CONNECTION_CLOSED);
    }
    
    if (inObj instanceof ObjectSession)
      return (ObjectSession)inObj;
    return (ObjectSession)inObj;
  }

  
  @Override
  public boolean killSession(long threadId) throws Exception
  {
    Object inObj = null;
    try
    {
      inObj = Protocol.killSession(threadId);
    }
    catch (Exception e)
    {
      logger.severe(e.getMessage());
      Session.setConnected(false);
      throw new EOFException(ObjectProtocolConstants.CONNECTION_CLOSED);
    }
    
    if (inObj instanceof Boolean)
      return ((Boolean)inObj).booleanValue();
    return false;
  }

  
  @Override
  public boolean clearSessionHistory() throws Exception
  {
    Object inObj = null;
    try
    {
      inObj = Protocol.clearSessionHistory();
    }
    catch (Exception e)
    {
      logger.severe(e.getMessage());
      Session.setConnected(false);
      throw new EOFException(ObjectProtocolConstants.CONNECTION_CLOSED);
    }
    
    if (inObj instanceof Boolean)
      return ((Boolean)inObj).booleanValue();
    return false;
  }

  @Override
  public boolean isCommandAuthorized(CommandLineArguments command) throws Exception
  {
    Object inObj = null;
    try
    {
      inObj = Protocol.isCommandAuthorized(command);
    }
    catch (Exception e)
    {
      logger.severe(e.getMessage());
      Session.setConnected(false);
      throw new EOFException(ObjectProtocolConstants.CONNECTION_CLOSED);
    }
    
    if (inObj instanceof Boolean)
      return ((Boolean)inObj).booleanValue();
    return false;
  }

  @Override
  public boolean isUserAuthorized(OsmClientUserInfo user) throws Exception
  {
    Object inObj = null;
    try
    {
      inObj = Protocol.isUserAuthorized(user);
    }
    catch (Exception e)
    {
      logger.severe(e.getMessage());
      Session.setConnected(false);
      throw new EOFException(ObjectProtocolConstants.CONNECTION_CLOSED);
    }
    
    if (inObj instanceof Boolean)
      return ((Boolean)inObj).booleanValue();
    return false;
  }

  @Override
  public WhatsUpInfo getWhatsUpInfo() throws Exception
  {
    Object inObj = null;
    try
    {
      inObj = Protocol.getWhatsUpInfo();
    }
    catch (Exception e)
    {
      logger.severe(e.getMessage());
      Session.setConnected(false);
      throw new EOFException(ObjectProtocolConstants.CONNECTION_CLOSED);
    }
    
    if (inObj instanceof WhatsUpInfo)
      return (WhatsUpInfo)inObj;
    return (WhatsUpInfo)inObj;
  }

  @Override
  public CommandLineResults invokePrivilegedCommand(CommandLineArguments command) throws Exception
  {
    // only going to work, if this is an authorized user and command.  Both
    // are checked and enforced by the Server
    Object inObj = null;
    try
    {
      inObj = Protocol.invokePrivilegedCommand(command);
    }
    catch (Exception e)
    {
      logger.severe(e.getMessage());
      Session.setConnected(false);
      throw new EOFException(ObjectProtocolConstants.CONNECTION_CLOSED);
    }
    
    if (inObj instanceof CommandLineResults)
      return (CommandLineResults)inObj;
    return (CommandLineResults)inObj;
  }

}
