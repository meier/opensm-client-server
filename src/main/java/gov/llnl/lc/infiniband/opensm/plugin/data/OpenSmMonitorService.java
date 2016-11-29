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
 *        file: OpenSmMonitorService.java
 *
 *  Created on: Nov 18, 2014
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import gov.llnl.lc.infiniband.opensm.plugin.event.OSM_EventStats;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmAdminApi;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmClientApi;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmEventApi;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmServerStatus;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmServiceManager;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmSession;
import gov.llnl.lc.net.ObjectSession;
import gov.llnl.lc.time.TimeStamp;
import gov.llnl.lc.util.filter.WhiteAndBlackListFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class OpenSmMonitorService implements Serializable, gov.llnl.lc.logging.CommonLogger
{
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = 7732530205792310214L;
  
  /** compress the object when serializing it to a file? **/
  private static boolean useCompression = true;
  
  /** from the admin interface **/
  private ObjectSession ParentSessionStatus  = null;
  private OsmServerStatus RemoteServerStatus = null;
  private OSM_Fabric Fabric                  = null;

  /************************************************************
   * Method Name:
   *  OpenSmMonitorService
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param parentSessionStatus
   * @param remoteServerStatus
   * @param fabricName
   ***********************************************************/
  public OpenSmMonitorService(ObjectSession parentSessionStatus, OsmServerStatus remoteServerStatus, OSM_Fabric fabric)
  {
    super();
    ParentSessionStatus = parentSessionStatus;
    RemoteServerStatus = remoteServerStatus;
    Fabric = fabric;
  }

  public static OpenSmMonitorService getOpenSmMonitorService(OpenSmMonitorService oms, WhiteAndBlackListFilter filter)
  {
    // the fabric is the only thing that is filtered
    return new OpenSmMonitorService(oms.getParentSessionStatus(), oms.getRemoteServerStatus(), OSM_Fabric.getOSM_Fabric(oms.getFabric(), filter));
        
  }

  public static OpenSmMonitorService getOpenSmMonitorService(OsmSession ParentSession)
  {
    // establish a connection
    ObjectSession SessionStatus  = null;
    OpenSmMonitorService oms = null;

    if (ParentSession != null)
    {
      SessionStatus                = ParentSession.getSessionStatus();
      OsmClientApi clientInterface = ParentSession.getClientApi();
      
      try
      {
        if(clientInterface == null)
          logger.severe("The client interface could not be obtained, NULL");
        
        oms = clientInterface.getOsmMonitorService();
       }
      catch (Exception e)
      {
        logger.severe(e.getMessage());
        logger.severe("Could not get an OMS from the interface");
        logger.severe(SessionStatus.toString());
        logger.severe("Returning NULL, nothing can be done without a connection (could be a serialization error)");
      }
    }
    else
    {
      logger.severe("Could not establish an OMS session");
    }
    return oms;
  }
  
  public static OMS_List getOMS_List(OsmSession ParentSession)
  {
    // establish a connection
    ObjectSession SessionStatus  = null;
    OMS_List omsHistory = null;

    if (ParentSession != null)
    {
      SessionStatus                = ParentSession.getSessionStatus();
      OsmClientApi clientInterface = ParentSession.getClientApi();
      
      try
      {
        if(clientInterface == null)
          logger.severe("The client interface could not be obtained, NULL");
        
        omsHistory = clientInterface.getOsmHistory();
       }
      catch (Exception e)
      {
        logger.severe(e.getMessage());
        logger.severe("Could not get an OMS_List from the interface");
        logger.severe(SessionStatus.toString());
        logger.severe("Returning NULL, nothing can be done without a connection (could be a serialization error)");
      }
    }
    else
    {
      logger.severe("Could not establish an OMS session");
    }
    return omsHistory;
  }
  
  public static OpenSmMonitorService getOpenSmMonitorServiceV1(OsmSession ParentSession)
  {
    // establish a connection
    ObjectSession SessionStatus  = null;

    OpenSmMonitorService service = null;

    if (ParentSession != null)
    {
      logger.info("Getting the OMS Interfaces");
      SessionStatus                = ParentSession.getSessionStatus();
      OsmClientApi clientInterface = ParentSession.getClientApi();
      OsmAdminApi adminInterface   = ParentSession.getAdminApi();
      OsmEventApi eventInterface = ParentSession.getEventApi();
      
      try
      {
        logger.info("Getting raw data from the OMS Interfaces");
        if((clientInterface == null) || (adminInterface == null))
          logger.severe("The client or admin interface could not be obtained, NULL");

        OsmServerStatus RemoteServerStatus = adminInterface.getServerStatus();
        String name                        = "Unknown Host";
        if(RemoteServerStatus != null)
          name = RemoteServerStatus.Server.getHost();
        
        OSM_Nodes osmNodes     = clientInterface.getOsmNodes();
        OSM_Ports osmPorts     = clientInterface.getOsmPorts();
        OSM_Stats osmStats     = clientInterface.getOsmStats();
        OSM_Subnet osmSubnet   = clientInterface.getOsmSubnet();
        OSM_SysInfo osmSysInfo = clientInterface.getOsmSysInfo();
        OSM_EventStats osmEventStats = eventInterface.getOsmEventStats();
        
        logger.info("Constructing the Fabric now");
        OSM_Fabric fabric = new OSM_Fabric(name, osmNodes, osmPorts, osmStats, osmSubnet, osmSysInfo, osmEventStats);
        logger.info("Done constructing the Fabric");
        
        // have everything I need to construct the OpenSmMonitorService
        service = new OpenSmMonitorService(SessionStatus, RemoteServerStatus, fabric);
        
       }
      catch (Exception e)
      {
        logger.severe(e.getMessage());
        logger.severe("Could not construct the OpenSmMonitorService object, check the constructor, and initializer?");
        logger.severe(SessionStatus.toString());
        logger.severe("Returning NULL, nothing can be done without a connection (could be a serialization error)");
      }
    }
    else
    {
      logger.severe("Could not establish an OMS session");
    }
    return service;
  }
  
  public static OpenSmMonitorService getOpenSmMonitorServiceOld(String hostName, String portNumber)
  {
    // establish a connection
    logger.info("OMS_SOld: Opening the OMS Session");
    OsmSession ParentSession = null;
    ObjectSession SessionStatus  = null;

    /* the one and only OsmServiceManager */
    OsmServiceManager OsmService = OsmServiceManager.getInstance();

    try
    {
      ParentSession = OsmService.openSession(hostName, portNumber, null, null);
    }
    catch (Exception e)
    {
      logger.severe(e.getStackTrace().toString());
      System.exit(-1);
    }
    OpenSmMonitorService service = null;

    if (ParentSession != null)
    {
      logger.info("Getting the OMS Interfaces");
      SessionStatus                = ParentSession.getSessionStatus();
      OsmClientApi clientInterface = ParentSession.getClientApi();
      OsmAdminApi adminInterface   = ParentSession.getAdminApi();
      OsmEventApi eventInterface = ParentSession.getEventApi();
      
      try
      {
        logger.info("Getting raw data from the OMS Interfaces");
        String name                        = adminInterface.getServerStatus().Server.getHost();
        OsmServerStatus RemoteServerStatus = adminInterface.getServerStatus();
        
        OSM_Nodes osmNodes     = clientInterface.getOsmNodes();
        OSM_Ports osmPorts     = clientInterface.getOsmPorts();
        OSM_Stats osmStats     = clientInterface.getOsmStats();
        OSM_Subnet osmSubnet   = clientInterface.getOsmSubnet();
        OSM_SysInfo osmSysInfo = clientInterface.getOsmSysInfo();
        OSM_EventStats osmEventStats = eventInterface.getOsmEventStats();
        
        logger.info("Constructing the Fabric now");
        OSM_Fabric fabric = new OSM_Fabric(name, osmNodes, osmPorts, osmStats, osmSubnet, osmSysInfo, osmEventStats);
        logger.info("Done constructing the Fabric");
        
        // have everything I need to construct the OpenSmMonitorService
        service = new OpenSmMonitorService(SessionStatus, RemoteServerStatus, fabric);
        
        OsmService.closeSession(ParentSession);
       }
      catch (Exception e)
      {
        logger.severe(e.getMessage());
        logger.severe(e.getStackTrace().toString());
        logger.severe("Could not construct the OpenSmMonitorService object, check the constructor, and initializer??");
      }
    }
    else
    {
      logger.severe("Could not establish an OMS session");
    }
    return service;
  }
  
  public static OMS_List getOMS_List(String hostName, String portNumber) throws IOException
  {
    // establish a connection
    OsmSession ParentSession = null;

    /* the one and only OsmServiceManager */
    OsmServiceManager OsmService = OsmServiceManager.getInstance();

    try
    {
      ParentSession = OsmService.openSession(hostName, portNumber, null, null);
    }
    catch (Exception e)
    {
      logger.severe("Couldn't get I/O for the connection to: " + hostName + " on port " + portNumber);
      throw new IOException("Couldn't get I/O for the connection to: " + hostName + " on port " + portNumber);
    }

    if (ParentSession != null)
    {
      OMS_List history = getOMS_List(ParentSession);
      
      /* done with session, so close it and return the info */
      try
      {
        OsmService.closeSession(ParentSession);
      }
      catch (Exception e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return history;
    }
    else
    {
      logger.severe("Could not establish an OMS session");
    }
    return null;
  }
  
  public static OpenSmMonitorService getOpenSmMonitorService(String hostName, String portNumber) throws IOException
  {
    // establish a connection
    logger.info("OMS_S: Opening the OMS Session");
    OsmSession ParentSession = null;

    /* the one and only OsmServiceManager */
    OsmServiceManager OsmService = OsmServiceManager.getInstance();

    try
    {
      ParentSession = OsmService.openSession(hostName, portNumber, null, null);
    }
    catch (Exception e)
    {
      logger.severe("Couldn't get I/O for the connection to: " + hostName + " on port " + portNumber);
      // check to see if portNumber is valid, and bail if not
      if(portNumber != null)
      {
        int pn = Integer.parseInt(portNumber);
        if(pn < 1)
        {
          System.err.println("Couldn't get I/O for the connection to: " + hostName + " on port " + portNumber);
          System.exit(-1);
        }
      }
      throw new IOException("Couldn't get I/O for the connection to: " + hostName + " on port " + portNumber);
    }

    if (ParentSession != null)
    {
      OpenSmMonitorService oms = getOpenSmMonitorService(ParentSession);
      
      /* done with session, so close it and return the info */
      try
      {
        OsmService.closeSession(ParentSession);
      }
      catch (Exception e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return oms;
    }
    else
    {
      logger.severe("Could not establish an OMS session");
    }
    return null;
  }
  

  public static OpenSmMonitorService getOpenSmMonitorServiceV1(String hostName, String portNumber) throws IOException
  {
    // establish a connection
    logger.info("OMS_S: Opening the OMS Session");
    OsmSession ParentSession = null;

    /* the one and only OsmServiceManager */
    OsmServiceManager OsmService = OsmServiceManager.getInstance();

    try
    {
      ParentSession = OsmService.openSession(hostName, portNumber, null, null);
    }
    catch (Exception e)
    {
      logger.severe("Couldn't get I/O for the connection to: " + hostName + " on port " + portNumber);
      throw new IOException("Couldn't get I/O for the connection to: " + hostName + " on port " + portNumber);
    }

    if (ParentSession != null)
    {
      OpenSmMonitorService oms = getOpenSmMonitorService(ParentSession);
      
      /* done with session, so close it and return the info */
      try
      {
        OsmService.closeSession(ParentSession);
      }
      catch (Exception e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return oms;
    }
    else
    {
      logger.severe("Could not establish an OMS session");
    }
    return null;
  }
  

  /************************************************************
   * Method Name:
   *  discoverOpenSmMonitorService
  **/
  /**
   * This method will ping a group of consecutive ports on the same
   * host, in an attempt to obtain a collection of OMS instances.
   * In this way, an active port can be found, or if a variety of 
   * tunnels are set up, with port forwarding, then many different
   * subnets/fabrics/clusters can be found.
   * 
   * This process can be time consuming, and multiple ports will
   * occur sequentially.  Since static, consider concurrent operation,
   * with each thread starting at the desired port, and only doing one port.
   * Use this method for non-sequential ports also.
   *
   * @see     describe related java objects
   *
   * @param hostNam   the name of the host (localhost)
   * @param startPort the initial portnumber for the service (10011)
   * @param nPorts    the number of consecutive ports to ping (at least 1)
   * @return
   ***********************************************************/
  public static LinkedHashMap<String, OpenSmMonitorService> discoverOpenSmMonitorService(String hostNam, String startPort, String nPorts)
  {
    LinkedHashMap<String, OpenSmMonitorService> omsFabrics = new LinkedHashMap<String, OpenSmMonitorService>();
    
    int pn = Integer.parseInt(startPort);
    int numPorts = Integer.parseInt(nPorts);
    
    logger.info("Discovering Fabrics Now");
    logger.info("using " + hostNam + ", and starting at port:" + startPort );
    logger.info("continuing for " + numPorts + " ports" );
       
    for(int n=0; n<numPorts; n++)
    {
      // attempt to connect, and get some info
      String portNum = Integer.toString(pn);
      
      OpenSmMonitorService OMS = null;
      try
      {
        OMS = OpenSmMonitorService.getOpenSmMonitorService(hostNam, portNum);
      }
      catch(Exception e)
      {
        logger.info("Nothing on port: " + portNum);
      }
      omsFabrics.put(portNum, OMS);
      pn++;
    }
    return omsFabrics;
  }
  
  public TimeStamp getPFM_TimeStamp()
  {
    if(Fabric != null)
      return OSM_Fabric.getPFM_TimeStamp(Fabric);
    return null;
  }
  
  public String getKey()
  {
    return getOMS_Key(getFabricName(), getPFM_TimeStamp());      
  }
  
  public static String getFabricNameFromKey(String key)
  {
    return OSM_Fabric.getFabricNameFromFabricKey(key);     
  }

  public static TimeStamp getTimeStampFromKey(String key)
  {
    return OSM_Fabric.getTimeStampFromFabricKey(key);     
  }

  public static String getOMS_Key(String name, TimeStamp ts)
  {
    return OSM_Fabric.getOSM_FabricKey(name, ts);      
  }

  public TimeStamp getTimeStamp()
  {
    // There shouldn't be an OMS without a timestamp, but test anyway
    if(Fabric == null)
      return null;
    return Fabric.getTimeStamp();
  }

  public ObjectSession getParentSessionStatus()
  {
    return ParentSessionStatus;
  }

  public OsmServerStatus getRemoteServerStatus()
  {
    return RemoteServerStatus;
  }

  public String getFabricName()
  {
    return getFabricName(false);
  }

  public String getFabricName(boolean trim)
  {
    if((Fabric != null) && (Fabric.getFabricName() != null))
      return Fabric.getFabricName(trim);
    return "";
  }

  public OSM_Fabric getFabric()
  {
    return Fabric;
  }
  
  public static OpenSmMonitorService readOMS(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException
  {
    FileInputStream fileInput = new FileInputStream(fileName);
    ObjectInputStream objectInputStream = null;
    GZIPInputStream in = null;
    
    if(useCompression)
    {
      in = new GZIPInputStream(fileInput);
      objectInputStream = new ObjectInputStream(in);
    }
    else
      objectInputStream = new ObjectInputStream(fileInput);
    
    OpenSmMonitorService obj = (OpenSmMonitorService) objectInputStream.readObject();
    
    objectInputStream.close();
    if(useCompression)
      in.close();
    fileInput.close();

    return obj;
  }
  
  public static void writeOMS(String fileName, OpenSmMonitorService OMS) throws IOException
  {
    File outFile = new File(fileName);
    outFile.getParentFile().mkdirs();
    FileOutputStream fileOutput = new FileOutputStream(outFile);
    ObjectOutputStream objectOutput = null;
    GZIPOutputStream out = null;
    
    if(useCompression)
    {
      out =  new GZIPOutputStream(fileOutput);
      objectOutput = new ObjectOutputStream(out);
    }
    else
      objectOutput = new ObjectOutputStream(fileOutput);
    
    objectOutput.writeObject(OMS);
    objectOutput.flush();
    objectOutput.close();
    if(useCompression)
      out.close();
    fileOutput.close();

    return;
  }

  /************************************************************
   * Method Name:
   *  toString
  **/
  /**
   * Describe the method here
   *
   * @see java.lang.Object#toString()
   *
   * @return
   ***********************************************************/
  
  public String toInfo()
  {
    StringBuffer stringValue = new StringBuffer();
    stringValue.append(OpenSmMonitorService.class.getSimpleName() + "\n");
    
    stringValue.append("fabric name:             "  + this.getFabricName() + "\n");
    stringValue.append("timestamp:               " + this.getFabric().toTimeString() + "\n");
    stringValue.append("# nodes:                 " + this.getFabric().getOSM_Nodes().size() + "\n");
    stringValue.append("# ports:                 " + this.getFabric().getOSM_Ports().size() + "\n");
    stringValue.append("# links:                 " + this.getFabric().getIB_Links().size());
  
    return stringValue.toString();
  }
 
  /************************************************************
   * Method Name:
   *  toTimeString
  **/
  /**
   * Returns a list of TimeStamps for this object
   *
   * @see     describe related java objects
   *
   * @return
   ***********************************************************/
  public String toTimeString()
  {
    return Fabric.toTimeString();
  }


  /************************************************************
   * Method Name:
   *  main
   **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects
   *
   * @param args
   ***********************************************************/
  public static void main(String[] args)
  {
    OpenSmMonitorService OMS = OpenSmMonitorService.getOpenSmMonitorServiceOld("localhost", "10011");
    if(OMS != null)
    {
      System.out.println("got a good one");
    }

  }

}
