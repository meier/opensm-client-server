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
 *        file: OsmObjectProtocol.java
 *
 *  Created on: Jun 27, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.net;

import gov.llnl.lc.infiniband.opensm.plugin.OsmInterface;
import gov.llnl.lc.infiniband.opensm.plugin.OsmNativeCommand;
import gov.llnl.lc.infiniband.opensm.plugin.OsmPluginMain;
import gov.llnl.lc.infiniband.opensm.plugin.data.OMS_List;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_Fabric;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_PluginInfo;
import gov.llnl.lc.infiniband.opensm.plugin.data.OpenSmMonitorService;
import gov.llnl.lc.infiniband.opensm.plugin.event.OsmSessionEventSet;
import gov.llnl.lc.infiniband.opensm.security.OMS_AuthorizationManager;
import gov.llnl.lc.logging.CommonLogger;
import gov.llnl.lc.net.AbstractObjectProtocol;
import gov.llnl.lc.net.MultiThreadSSLServer;
import gov.llnl.lc.net.ObjectCmdArgs;
import gov.llnl.lc.net.ObjectSession;
import gov.llnl.lc.net.SerialObjectProtocol;
import gov.llnl.lc.system.CommandLineArguments;
import gov.llnl.lc.system.CommandLineExecutor;
import gov.llnl.lc.system.CommandLineResults;

/**********************************************************************
 * The OpenSM primitive transport protocol.  It is a simple state machine
 * which in general processes serialized input objects, which are usually
 * simple command queries, and produces serialized output objects, which
 * are usually complex objects containing the query results.
 * 
 * Normally this objects state is not needed, however complex queries or
 * commands may require a handshake sequence or additional information
 * which requires state.
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Jun 27, 2011 9:41:24 AM
 **********************************************************************/
public class OsmObjectProtocol extends AbstractObjectProtocol implements SerialObjectProtocol, OsmObjectProtocolConstants, CommonLogger
{
  /** logger for the class **/
  private final java.util.logging.Logger classLogger =
      java.util.logging.Logger.getLogger( getClass().getName() );

  private int              state          = WAITING;
  private int              mode           = STRING_MODE;
  
  private volatile static gov.llnl.lc.infiniband.opensm.plugin.data.OsmUpdateManager dataMgr;
  private volatile static gov.llnl.lc.infiniband.opensm.plugin.event.OsmEventManager eventMgr;
  private volatile static MultiThreadSSLServer multiServer;
  private volatile static OMS_AuthorizationManager authMgr;

  /************************************************************
   * Method Name:
   *  processInput
   **/
  /**
   * The Server side of this protocol.  It receives requests, processes them,
   * and returns the result.
   * 
   * It has a mode and a state, which can probably be combined... TODO
   * STATE is either WAITING or ACTIVE
   * MODE is either STRING_MODE or CMD_ARGS_MODE (subset of ACTIVE state)
   *
   * @see gov.llnl.lc.net.SerialObjectProtocol#processInput(java.lang.Object)
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @param inObj
   * @return
   ***********************************************************/

  @Override
  public Object processInput(Object inObj)
  {
    String theOutput = null;
    String theInput  = null;
    Object argObj    = null;;
    
    if (inObj instanceof String)
    {
      theInput = new String((String) inObj);
      mode = STRING_MODE;
    }
 
    if (inObj instanceof ObjectCmdArgs)
    {
      theInput = new String(((ObjectCmdArgs) inObj).getProtocolCommand());
      argObj   = ((ObjectCmdArgs) inObj).getProtocolArguments();
      mode = CMD_ARGS_MODE;
    }
 
    if (state == WAITING)
    {
      theOutput = INITIAL_PROMPT;
      state = ACTIVE;
    }
    else if (state == ACTIVE)
    {
      if(mode == STRING_MODE)
      {
        if (theInput.equalsIgnoreCase(CLOSE_SESSION))
        {
          theOutput = FINAL_PROMPT;
          state = WAITING;
        }
        else if (theInput.equalsIgnoreCase(GET_OSM_NODES))
        {
          /* get this out of the data manager */
          return dataMgr.getNativeNodes();
        }
        else if (theInput.equalsIgnoreCase(GET_OSM_PORTS))
        {
          /* get this out of the data manager */
          return dataMgr.getNativePorts();
        }
        else if (theInput.equalsIgnoreCase(GET_OSM_STATS))
        {
          /* get this out of the data manager */
          return dataMgr.getNativeStats();
        }
        else if (theInput.equalsIgnoreCase(GET_OSM_TESTDATA))
        {
          /* get this out of the data manager */
          return dataMgr.getNativeTestData();
        }
        else if (theInput.equalsIgnoreCase(GET_OSM_SYSINFO))
        {
          /* get this out of the data manager */
          return dataMgr.getNativeSystemInfo();
        }
        else if (theInput.equalsIgnoreCase(GET_OSM_SUBNET))
        {
          /* get this out of the data manager */
          return dataMgr.getNativeSubnet();
        }
        else if (theInput.equalsIgnoreCase(GET_OMS_COLLECTION))
        {
          OMS_List oList =  dataMgr.getOmsHistory();
          OMS_List nList =  new OMS_List(2);
           // replace the generic cached OMS with the current
           // one from THIS session
          nList.putCurrentOMS(oList.getOldestOMS());
           OpenSmMonitorService osm = getOpenSmMonitorService();
           if(osm != null)
              oList.putCurrentOMS(osm, true);
          return oList;
        }
        else if (theInput.equalsIgnoreCase(GET_OSM_MONITOR_SERVICE))
        {
          return getOpenSmMonitorService();
        }
        else if (theInput.equalsIgnoreCase(GET_OSM_FABRIC))
        {
          /* get this out of the data manager */
          return dataMgr.getOSM_Fabric();
        }
        else if (theInput.equalsIgnoreCase(GET_SERVER_STATUS))
        {
          /* construct the status from the parent server and the plugin data */
          OSM_PluginInfo pi = dataMgr.getNativePlugin();
          if(pi == null)
            pi = new OSM_PluginInfo(666, 666, 666, 666, 666);
          return dataMgr.getServerStatus(pi);
        }
        else if (theInput.equalsIgnoreCase(GET_SESSION_STATUS))
        {
          /* get this threads status from the parent server */
          return multiServer.getSession(getId());
        }
        else if (theInput.equalsIgnoreCase(CLEAR_SESSION_HISTORY))
        {
          return new Boolean(multiServer.removeHistory());
        }
        else if (theInput.equalsIgnoreCase(GET_OSM_EVENT_STATS))
        {
          /* get this out of the event manager */
          return eventMgr.getEventStatistics();
        }
        else if (theInput.equalsIgnoreCase(GET_WHATSUP_INFO))
        {
           /* get this out of the data manager */
          return dataMgr.getWhatsUpInfo();
        }
        else if (theInput.equalsIgnoreCase(GET_OSM_CONFIGURATION))
        {
          /* do this on demand, it is relatively static */
          return dataMgr.getOSM_Configuration();
        }

        else
        {
          theOutput = UNRECOGNIZED_PROMPT + " (" + theInput + ")";
        }
      }
      else if (mode == CMD_ARGS_MODE)
      {        
        if (theInput.equalsIgnoreCase(KILL_SESSION))
        {
          if (argObj instanceof Long)
          {
            return new Boolean(multiServer.killSession(((Long)argObj).longValue()));
          }
        }
        else if (theInput.equalsIgnoreCase(ADD_SESSION))
        {
          if (argObj instanceof Long)
          {
             /* add this session to the EventManagers list of listeners */
            classLogger.info("Attempting to add a session to the Event managers Listener List ("+ ((Long)argObj) + ")");
            return new Boolean(eventMgr.addSessionListener(((Long)argObj)));
          }
        }
        else if (theInput.equalsIgnoreCase(REMOVE_SESSION))
        {
          if (argObj instanceof Long)
          {
             /* remove this session from the EventManagers list of listeners */
            classLogger.info("Attempting to remove a session from the Event managers Listener List ("+ ((Long)argObj) + ")");
            return new Boolean(eventMgr.removeSessionListener(((Long)argObj)));
          }
        }
        else if (theInput.equalsIgnoreCase(IS_USER_AUTHORIZED))
        {
          if (argObj instanceof OsmClientUserInfo)
          {
             // return true/false if the supplied user is a "privileged" user
            return new Boolean(authMgr.isAuthorized(((OsmClientUserInfo)argObj)));
          }
        }
        else if (theInput.equalsIgnoreCase(IS_COMMAND_AUTHORIZED))
        {
          if (argObj instanceof CommandLineArguments)
          {
             // return true/false if the supplied command is supported and enabled
            return new Boolean(authMgr.isAuthorizedCommand(((CommandLineArguments)argObj)));
          }
        }
        else if (theInput.equalsIgnoreCase(INVOKE_PRIV_COMMAND))
        {
          if (argObj instanceof CommandLineArguments)
          {
             // return the results of invoking the command
            try
            {
              return executeCommand((CommandLineArguments)argObj);
            }
            catch (Exception e)
            {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          }
        }
        else if (theInput.equalsIgnoreCase(SET_CLIENT_INFO))
        {
          if (argObj instanceof OsmClientUserInfo)
          {
             /* save the users info with this session so subsequent "privileged" commands will work */
            return new Boolean(authMgr.setSessionUser(((OsmClientUserInfo)argObj), multiServer.getSession(getId())));
          }
        }
        else if (theInput.equalsIgnoreCase(GET_OSM_EVENT_LISTENERS))
        {
          if (argObj instanceof Long)
          {
             /* remove this session from the EventManagers list of listeners */
            return new Integer(eventMgr.numSessionListeners());
          }
        }
        else if (theInput.equalsIgnoreCase(GET_OSM_EVENTS))
        {
          if (argObj instanceof OsmSessionEventSet)
          {
             /* wait for an event from this sessions event queue */
            try
            {
              return eventMgr.waitForOsmEvents(300, (OsmSessionEventSet)argObj);
            }
            catch (InterruptedException e)
            {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          }
        }

        else if (theInput.equalsIgnoreCase(GET_SESSION_STATUS))
        {
          if (argObj instanceof Long)
          {
            /* get this threads status from the parent server */
            return multiServer.getSession(((Long)argObj).longValue());
          }
        }
 
        theOutput = "CmdArgsMode";
      }
      else
      {
        /* probably object mode */
        theOutput = "ObjectMode";
        
      }
    }
    return theOutput;
  }
  
  // TODO put this in the dataMgr
  public OpenSmMonitorService getOpenSmMonitorService()
  {
    /* get the cached values out of the data manager, but then
     * build up the latest, using this session, and add it to
     * the list */

    OSM_PluginInfo pi = dataMgr.getNativePlugin();
    if(pi == null)
      pi = new OSM_PluginInfo(666, 666, 666, 666, 666);
    OsmServerStatus rss = dataMgr.getServerStatus(pi);

    /* get this threads status from the parent server */
    ObjectSession ss = multiServer.getSession(getId());
     
    OSM_Fabric fabric = dataMgr.getOSM_Fabric();
     
    // have everything I need to construct the OpenSmMonitorService
    return new OpenSmMonitorService(ss, rss, fabric);
  }

  // TODO put this in the authMgr
  public CommandLineResults executeCommand(CommandLineArguments command) throws Exception
  {
    /*
     * attempt to execute the provided command and return the results
     * 
     * Req:  must be authorized user
     *       must be authorized command
     * 
     *       returns NULL if not authorized
     */
    
    logger.warning("Attempting to invoke a privilaged command: " + command.getCommandLine());
    
    if(authMgr.isAuthorizedCommand(command) )
    {
      logger.warning("Privilaged command: " + command.getCommandLine() + ", is allowed.");
      String userName = (authMgr.getSessionUser(multiServer.getSession(getId()))).getClientUser().UserName;
      logger.warning("User: " + userName + " being checked for authorization");
      if(authMgr.isAuthorizedSession(multiServer.getSession(getId())))
      {
        // allow only a single command, truncate anything after ;
        String cmdLine = command.getCommandLine();
        String cl = null;
        int ndex = cmdLine.indexOf(';');
        if(ndex > -1)
        {
          logger.severe("User: " + userName + " attempting to invoke multiple commands: " + command.getCommandLine());
          cl = cmdLine.substring(0, ndex).trim();
        }
        else
          cl = cmdLine.trim();
        
        command.setCommandLine(cl);
        
        // it looks like both the command and user (via session history) is authorized, so invoke
        logger.warning("User: " + userName + " allowed to invoke command: " + command.getCommandLine());
        
        // determine if the command is a Native OMS command, or a shell command
        if(OsmNativeCommand.isNativeCommand(command))
          return invokeNativeCommand(command);
        
        // if we fall through, assume its a normal shell command
        CommandLineExecutor cmdExecutor1 = new CommandLineExecutor(command);
        cmdExecutor1.runCommand();
        return cmdExecutor1.getResults();
      }
      else
      {
        logger.warning("User: " + userName + " denied");
      }
    }
    else
    {
      logger.warning("Privilaged command: " + command.getCommandLine() + " denied");
    }
    return null;
  }

  private CommandLineResults invokeNativeCommand(CommandLineArguments command)
  {
    // get the native interface
    OsmPluginMain jServ = OsmPluginMain.getInstance();
    OsmInterface osmInt = jServ.getInterface();
    
    // invoke the appropriate command
    OsmNativeCommand cmd = OsmNativeCommand.get(command);
    CommandLineResults results = new CommandLineResults(null, null, 0);
    
    // invoke these separately if you want to handle the results differently
    switch(cmd)
    {
      case OSM_NATIVE_ECHO:
      case OSM_NATIVE_LSWEEP:
      case OSM_NATIVE_HSWEEP:
      case OSM_NATIVE_REROUTE:
      case OSM_NATIVE_LOGLEVEL:
      case OSM_NATIVE_UPDATE_DESC:
      case OSM_NATIVE_PSWEEP:
      case OSM_NATIVE_PPERIOD:
      case OSM_NATIVE_PCLEAR:
          String r = osmInt.invokeCommand(cmd.getNativeCommand(), command.getCommandLine());
          logger.severe(r);
          results = new CommandLineResults(r, null, 0);
         break;
        
      case OSM_NATIVE_MAX:
      default:
        logger.severe("default");
        break;
        
    }
    return results;
  }

  /************************************************************
   * Method Name:
   *  getClientProtocolName
  **/
  /**
   * Describe the method here
   *
   * @see gov.llnl.lc.net.SerialObjectProtocol#getClientProtocolName()
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @return
   ***********************************************************/
  
  @Override
  public String getClientProtocolName()
  {
    return new OsmObjectClient().getClass().getCanonicalName();
  }

  /************************************************************
   * Method Name:
   *  OsmObjectProtocol
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   ***********************************************************/
  public OsmObjectProtocol()
  {
    // this is where the data is stashed
    dataMgr = gov.llnl.lc.infiniband.opensm.plugin.data.OsmUpdateManager.getInstance();
    
    // this is where the events are managed
    eventMgr = gov.llnl.lc.infiniband.opensm.plugin.event.OsmEventManager.getInstance();
    
    // this is the server
    multiServer = MultiThreadSSLServer.getInstance();
    
    // this is the authorization manager, for controlling privileged commands
    authMgr = OMS_AuthorizationManager.getInstance();
  }
  
}
