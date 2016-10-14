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
 *        file: OSM_Configuration.java
 *
 *  Created on: Nov 18, 2014
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import gov.llnl.lc.infiniband.core.IB_Guid;
import gov.llnl.lc.infiniband.core.IB_Link;
import gov.llnl.lc.infiniband.opensm.plugin.OsmConstants;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmClientApi;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmServiceManager;
import gov.llnl.lc.infiniband.opensm.plugin.net.OsmSession;
import gov.llnl.lc.infiniband.opensm.xml.IB_FabricConf;
import gov.llnl.lc.infiniband.opensm.xml.IB_LinkListElement;
import gov.llnl.lc.infiniband.opensm.xml.IB_PortElement;
import gov.llnl.lc.logging.CommonLogger;
import gov.llnl.lc.net.ObjectSession;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**********************************************************************
 * Describe purpose and responsibility of OSM_Configuration
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Nov 18, 2014 3:35:53 PM
 **********************************************************************/
public class OSM_Configuration implements Serializable, OsmConstants, CommonLogger
{
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = 6047523426873130713L;
  
  /** compress the object when serializing it to a file? **/
  private static boolean useCompression = true;
  
  private OSM_NodeNameMap nodeNameMap;
  private IB_FabricConf   fabricConfig;

  /************************************************************
   * Method Name:
   *  OSM_Configuration
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param nodeNameMap
   * @param fabricConfig
   ***********************************************************/
  public OSM_Configuration(String nodeNameMapFile, String ibFabricConfFile)
  {
    // one or more arguments may be null, thats okay
    nodeNameMap  = new OSM_NodeNameMap(nodeNameMapFile);
    fabricConfig = new IB_FabricConf(ibFabricConfFile);
  }

  /************************************************************
   * Method Name:
   *  OSM_Configuration
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param nodeNameMap
   * @param fabricConfig
   ***********************************************************/
  public OSM_Configuration(OSM_NodeNameMap nodeNameMap, IB_FabricConf fabricConfig)
  {
    super();
    this.nodeNameMap = nodeNameMap;
    this.fabricConfig = fabricConfig;
  }
  
  
  public static OSM_Configuration getOsmConfig(OsmSession ParentSession)
  {
    // establish a connection
    ObjectSession SessionStatus  = null;
    OSM_Configuration config = null;

    if (ParentSession != null)
    {
      SessionStatus                = ParentSession.getSessionStatus();
      OsmClientApi clientInterface = ParentSession.getClientApi();
      
      try
      {
        if(clientInterface == null)
          logger.severe("The client interface could not be obtained, NULL");
        
        config = clientInterface.getOsmConfig();
       }
      catch (Exception e)
      {
        logger.severe(e.getMessage());
        logger.severe("Could not get an OSM_Configuration from the interface");
        logger.severe(SessionStatus.toString());
        logger.severe("Returning NULL, nothing can be done without a connection (could be a serialization error)");
      }
    }
    else
    {
      logger.severe("Could not establish an OMS session");
    }
    return config;
  }
  
  public static OSM_Configuration getOsmConfig(String hostName, String portNumber) throws IOException
  {
    // establish a connection
    logger.info("CONFIG_S: Opening the OMS Session");
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
      OSM_Configuration config = getOsmConfig(ParentSession);
      
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
      return config;
    }
    else
    {
      logger.severe("Could not establish an OMS session");
    }
    return null;
  }
  
  public static String getCacheFileName(String fabricName)
  {
    if(fabricName != null)
    {
      // the name should be a combination of the cache location and the fabric name
      String fNam = fabricName + ".cfg";
      String cNam = OMS_DEFAULT_DIR  + OMS_CACHE_DIR;
      return cNam + fNam;
     
//      return "/home/meier3/.smt/cache/fabric.his";
    }
    return null;
  }
  
  public static OSM_Configuration cacheOSM_Configuration(String fabricName, OSM_Configuration config)
  {
    // write the configuration to a cache file (create the path if necessary)
    // and over write if necessary
    if(config != null)
    {
      String fName = config.getFabricConfig().getFabricName();
      if(fabricName != null)
        fName = fabricName;
      String cacheName = getCacheFileName(fName);
      logger.info("Saving the Cache file: ("+ cacheName + ")");
      try
      {
        OSM_Configuration.writeConfig(cacheName, config);
      }
      catch (IOException e)
      {
        logger.severe("Could not save the Cache file: ("+ cacheName + ")");
        logger.severe(e.getMessage());
       }
     }
    return config;
  }

  /************************************************************
   * Method Name:
   *  getNodeNameMap
   **/
  /**
   * Returns the value of nodeNameMap
   *
   * @return the nodeNameMap
   *
   ***********************************************************/
  
  public OSM_NodeNameMap getNodeNameMap()
  {
    return nodeNameMap;
  }

  /************************************************************
   * Method Name:
   *  getFabricConfig
   **/
  /**
   * Returns the value of fabricConfig
   *
   * @return the fabricConfig
   *
   ***********************************************************/
  
  public IB_FabricConf getFabricConfig()
  {
    return fabricConfig;
  }

  public static OSM_Configuration readConfig(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException
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
    
    OSM_Configuration obj = (OSM_Configuration) objectInputStream.readObject();
    
    objectInputStream.close();
    if(useCompression)
      in.close();
    fileInput.close();

    return obj;
  }
  
  public static void writeConfig(String fileName, OSM_Configuration config) throws IOException
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
    
    objectOutput.writeObject(config);
    objectOutput.flush();
    objectOutput.close();
    if(useCompression)
      out.close();
    fileOutput.close();

    return;
  }

  public String toInfo(OSM_Node node)
  {
    // print out the fabric conf for this node
    
    boolean nodeFound = false;
    
    for (IB_LinkListElement lle : fabricConfig.getNodeElements())
    {
      String nodeName = lle.getName(); // name of the node
      
      // if node is null, then check ALL nodes, otherwise limit the test to the one provided
      if((node != null) && !(nodeName.equalsIgnoreCase(node.sbnNode.description)) && !(nodeName.equalsIgnoreCase(node.pfmNode.getNode_name())))
          continue;  // skip the check, not the node we are interested in

      // if here, I think we found a LinkListElement that matches our node, so build a return string
      nodeFound = true;
      return lle.toXMLString(0);
//      for (IB_PortElement pe : lle.getPortElements())
//      {
//        String portNumber = pe.getNumber(); // this port number
//        String rNodeName = pe.getIB_RemoteNodeElement().getName();
//        String rPortNumber = pe.getIB_RemotePortElement().getNumber();
//
//        // attempt to find this "ideal" link in the link map (keyed off guid +
//        // portnum
//        IB_Guid lGuid = osmNodes.getGuidFromName(nodeName);
//        IB_Guid rGuid = osmNodes.getGuidFromName(rNodeName);
//        short pNum = Short.parseShort(portNumber);
//        short rPNum = Short.parseShort(rPortNumber);
//        long lguid = 0L;
//        long rguid = 0L;
//        if (lGuid != null)
//          lguid = lGuid.getGuid();
//        if (rGuid != null)
//          rguid = rGuid.getGuid();
//        String llKey = OSM_Port.getOSM_PortKey(lguid, pNum);
//        String rlKey = OSM_Port.getOSM_PortKey(rguid, rPNum);
//
//        IB_Link llink = linkMap.get(llKey);
//        if (llink != null)
//        {
//          /*
//           * the lLink.Endpoint1 matches the ideal Link, endpoint1, so make sure
//           * lLink.Endopoint2 matches endpoint2
//           */
//          String eKey = OSM_Port.getOSM_PortKey(llink.Endpoint2.getNodeGuid().getGuid(),
//              (short) llink.Endpoint2.getPortNumber());
//          if (rlKey.equals(eKey))
//            foundIt = true;
//        }
//
//        IB_Link rlink = linkMap.get(rlKey);
//        if (rlink != null)
//        {
//          /*
//           * the rLink.Endpoint1 matches the ideal Link, endpoint2, so make sure
//           * rLink.Endopoint2 matches endpoint1
//           */
//          String eKey = OSM_Port.getOSM_PortKey(rlink.Endpoint2.getNodeGuid().getGuid(),
//              (short) rlink.Endpoint2.getPortNumber());
//          if (llKey.equals(eKey))
//            foundIt = true;
//        }
//
//        // show the problems (not founds)
//        if (((llKey == null) && (rlKey == null)) || !foundIt)
//        {
//          // is either side of this link down?
//          OSM_Port p = getOSM_Port(getOSM_PortKey(lguid, pNum));
//          if (p != null)
//            if (p.isActive())
//            {
//              // invalid
//              String ivl = getLinkDescription(lguid, pNum, linkMap);
//              String ils = fabricConfig.getLinkDescription(nodeName, portNumber, false);
//              System.out.println("");
//              System.out.println("ERR: invalid link : " + ivl);
//              System.out.println("     Should be    : " + ils);
//            }
//            else if (includeDownedPorts)
//            {
//              // down
//              String ils = fabricConfig.getLinkDescription(nodeName, portNumber, false);
//              System.out.println("");
//              System.out.println("ERR: port down: " + ils);
//            }
//
//          p = getOSM_Port(getOSM_PortKey(rguid, rPNum));
//          if (p != null)
//            if (p.isActive())
//            {
//              // invalid
//              String ivl = getLinkDescription(rguid, rPNum, linkMap);
//              String ils = fabricConfig.getLinkDescription(nodeName, portNumber, false);
//              System.out.println("");
//              System.out.println("ERR: invalid link : " + ivl);
//              System.out.println("     Should be    : " + ils);
//
//            }
//            else if (includeDownedPorts)
//            {
//              // down
//              String ils = fabricConfig.getLinkDescription(nodeName, portNumber, false);
//              System.out.println("");
//              System.out.println("ERR: port down: " + ils);
//            }
//        }
//        if(nodeFound && foundIt)
//          status = true;
//        foundIt = false;
//      }
    }
    if(!nodeFound)
      System.err.println("Could not find a matching node to display: " + node.sbnNode.description + ", " + node.getNodeGuid().toColonString());

    
    
    
    return null;
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
    stringValue.append(OSM_Configuration.class.getSimpleName() + "\n");
    
    if(fabricConfig != null)
      stringValue.append(" " + fabricConfig.toInfo());
     
    if(nodeNameMap != null)
      stringValue.append(" " + nodeNameMap.toInfo());
  
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
    return null;
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
    OSM_NodeNameMap nnm = new OSM_NodeNameMap("/home/meier3/dev/ib-node-name-map");
    IB_FabricConf fc    = new IB_FabricConf("/home/meier3/dev/ibfabricconf.xml");

    System.out.println(nnm.toString());
    System.out.println("The Node name is: " + nnm.getNodeName("0x0008f104003f15c0"));
    System.out.println("The Node Guid is: " + nnm.getNodeGuid("ibt1 (R 5 L 8a) ISR9288/ISR9096 sLB-24D"));
    System.out.println("The Node Guid is: " + nnm.getNodeGuid(nnm.getNodeName("0x0008f104003f15c0")));

    System.out.println(fc.toXMLString(0));
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
  
  @Override
  public String toString()
  {
    return "OSM_Configuration [nodeNameMap=" + nodeNameMap + ", fabricConfig=" + fabricConfig + "]";
  }

  

}
