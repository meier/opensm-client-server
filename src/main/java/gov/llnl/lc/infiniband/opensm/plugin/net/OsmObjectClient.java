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
 *        file: OsmObjectClient.java
 *
 *  Created on: Jun 27, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.net;

import gov.llnl.lc.infiniband.opensm.plugin.event.OsmSessionEventSet;
import gov.llnl.lc.logging.CommonLogger;
import gov.llnl.lc.net.AbstractObjectClientProtocol;
import gov.llnl.lc.net.ObjectCmdArgs;
import gov.llnl.lc.system.CommandLineArguments;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketTimeoutException;

/**********************************************************************
 * Describe purpose and responsibility of OsmObjectClient
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Jun 27, 2011 10:45:36 AM
 **********************************************************************/
public class OsmObjectClient extends AbstractObjectClientProtocol implements OsmObjectProtocolConstants, CommonLogger
{
  private java.io.ObjectOutputStream outStream = null;
  private java.io.ObjectInputStream inStream = null;
  
  /************************************************************
   * Method Name:
   *  openSession
  **/
  /**
   * Assumes a secure connection has already been established, and 
   * authentication has already occurred.  This just servers to identify
   * the initial handshake that should occur before the normal protocol
   * processing.
   *
   * @see     describe related java objects
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @param is
   * @param os
   * @return
   * @throws Exception
   ***********************************************************/
  public String openSession(java.io.ObjectInputStream is, java.io.ObjectOutputStream os) throws Exception
  {
    Object inObj;
    String fromServer = BAD_CONNECTION;

    inStream = null;
    outStream = null;

    if (is != null && os != null)
    {
      /* read something in, or timeout */
      inObj = is.readObject();
      if (inObj instanceof String)
      {
        fromServer = new String((String) inObj);
        inStream = is;
        outStream = os;
      }
      else
      {
        logger.severe("Did not recieve a string as expected");
      }
    }
    else
      logger.severe("Must supply valid IO");

    // after opening, send the server the connection (users) info 
    if(!BAD_CONNECTION.equalsIgnoreCase(fromServer))
      putClientInfo(new OsmClientUserInfo());
    return fromServer;
  }
  
  private Object getObject(String objType) throws IOException, ClassNotFoundException
  {
    Object inObj = null;
    if((inStream == null) || (outStream == null))
    {
      throw new EOFException(CONNECTION_CLOSED);      
    }
    try
    {
    outStream.writeObject(objType);
    inObj = inStream.readObject();
    }
    catch (SocketTimeoutException te)
    {
      // connection terminated - or timed out
      logger.severe(CONNECTION_TIMEOUT);
      inStream = null;
      outStream = null;

      throw new EOFException(CONNECTION_CLOSED);
     }
    catch (EOFException e)
    {
      // connection terminated - or timed out
      logger.severe(CONNECTION_CLOSED);
      inStream = null;
      outStream = null;

      throw new EOFException(CONNECTION_CLOSED);
    }
    return inObj;    
  }

  private Object getObject(Object objType) throws IOException, ClassNotFoundException
  {
    Object inObj = null;
    if((inStream == null) || (outStream == null))
    {
      throw new EOFException(CONNECTION_CLOSED);      
    }
    try
    {
      outStream.writeObject(objType);
      inObj = inStream.readObject();
    }
    catch (SocketTimeoutException te)
    {
      // connection terminated - or timed out
      logger.severe(CONNECTION_TIMEOUT);
      inStream = null;
      outStream = null;

      throw new EOFException(CONNECTION_CLOSED);
     }
    catch (EOFException e)
    {
      // connection terminated - or timed out
      logger.severe(CONNECTION_CLOSED);
      inStream = null;
      outStream = null;

      throw new EOFException(CONNECTION_CLOSED);
    }
    return inObj;    
  }

  public String closeSession() throws Exception
  {
    Object inObj = getObject(CLOSE_SESSION);
    String fromServer = BAD_CONNECTION;
    if (inObj instanceof String)
    {
      fromServer = new String((String) inObj);
    }
    else
    {
      logger.severe("Did not recieve a string as expected");
    }
    inStream = null;
    outStream = null;

    return fromServer;
  }

  public Object getOsmNodes() throws Exception
  {
    return getObject(GET_OSM_NODES);
  }

  public Object getOsmPorts() throws Exception
  {
    return getObject(GET_OSM_PORTS);
  }

  public Object getOsmStats() throws Exception
  {
    return getObject(GET_OSM_STATS);
  }

  public Object getOsmTestData() throws Exception
  {
    return getObject(GET_OSM_TESTDATA);
  }

  public Object getOsmSysInfo() throws Exception
  {
    return getObject(GET_OSM_SYSINFO);
  }

  public Object getOsmSubnet() throws Exception
  {
    return getObject(GET_OSM_SUBNET);
  }
    
  public Object getServerStatus() throws Exception
  {
    return getObject(GET_SERVER_STATUS);
  }
  
  public Object getSessionStatus() throws Exception
  {
    return getObject(GET_SESSION_STATUS);
  }
  
  public Object getSessionStatus(long id) throws Exception
  {
    /* wrap the command and argument together, and send it */    
    return getObject(new ObjectCmdArgs(GET_SESSION_STATUS, new Long(id)));
  }
  
  public Object clearSessionHistory() throws Exception
  {
    return getObject(CLEAR_SESSION_HISTORY);
  }
  
  public Object killSession(long id) throws Exception
  {
    /* wrap the command and argument together, and send it */    
    return getObject(new ObjectCmdArgs(KILL_SESSION, new Long(id)));
  }
  
  public Object putClientInfo(OsmClientUserInfo info) throws Exception
  {
    /* wrap the command and argument together, and send it */    
    return getObject(new ObjectCmdArgs(SET_CLIENT_INFO, info));
  }
  
  public Object isUserAuthorized(OsmClientUserInfo info) throws Exception
  {
    /* wrap the command and argument together, and send it */    
    return getObject(new ObjectCmdArgs(IS_USER_AUTHORIZED, info));
  }
  
  public Object isCommandAuthorized(CommandLineArguments command) throws Exception
  {
    /* wrap the command and argument together, and send it */    
    return getObject(new ObjectCmdArgs(IS_COMMAND_AUTHORIZED, command));
  }
  
  public Object invokePrivilegedCommand(CommandLineArguments command) throws Exception
  {
    /* wrap the command and argument together, and send it */    
    return getObject(new ObjectCmdArgs(INVOKE_PRIV_COMMAND, command));
  }
  
  public Object getWhatsUpInfo() throws Exception
  {
    return getObject(GET_WHATSUP_INFO);
  }
  
  public Object getOsmConfiguration() throws Exception
  {
    return getObject(GET_OSM_CONFIGURATION);
  }
  
  public Object getOsmEventStats() throws Exception
  {
    return getObject(GET_OSM_EVENT_STATS);
  }
  
  public Object getOMS_Fabric() throws Exception
  {
    return getObject(GET_OSM_FABRIC);
  }
  
  public Object getOMS() throws Exception
  {
    return getObject(GET_OSM_MONITOR_SERVICE);
  }
  
  public Object getOMS_List() throws Exception
  {
    return getObject(GET_OMS_COLLECTION);
  }
  
  public Object waitForEvents(OsmSessionEventSet Events) throws Exception
  {
    /* wrap the command and argument together, and send it */    
    return getObject(new ObjectCmdArgs(GET_OSM_EVENTS, Events));
  }
  
  public Object addEventListenerSession(long id) throws Exception
  {
    /* wrap the command and argument together, and send it */    
    return getObject(new ObjectCmdArgs(ADD_SESSION, new Long(id)));
  }
  
  public Object removeEventListenerSession(long id) throws Exception
  {
    /* wrap the command and argument together, and send it */    
    return getObject(new ObjectCmdArgs(REMOVE_SESSION, new Long(id)));
  }
  
  public Object getNumEventListenerSessions(long id) throws Exception
  {
    /* wrap the command and argument together, and send it */    
    return getObject(new ObjectCmdArgs(GET_OSM_EVENT_LISTENERS, new Long(id)));
  }
  

  /************************************************************
   * Method Name:
   *  main
   **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @param args
   ***********************************************************/
  public static void main(String[] args)
  {
    // TODO Auto-generated method stub
  }
}
