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
 *        file: OSM_NodeNameMap.java
 *
 *  Created on: Nov 18, 2014
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import gov.llnl.lc.infiniband.core.IB_Guid;
import gov.llnl.lc.infiniband.opensm.parser.IB_NodeNameMapParser;
import gov.llnl.lc.util.SystemConstants;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**********************************************************************
 * Describe purpose and responsibility of OSM_NodeNameMap
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Nov 18, 2014 10:36:30 AM
 **********************************************************************/
public class OSM_NodeNameMap implements Serializable, gov.llnl.lc.logging.CommonLogger
{
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = -6188255541176198353L;
  
  // Maps a Name or Description to a GUID
  //
  // GUIDs are supposed to be unique, so there probably shouldn't be values repeated
  // in the file.  WARN if so.  It may be a cut and paste issue, or it may be that
  // the most current version should be used (over write previous).  In any case the
  // behavior is undefined.
  //

  private HashMap <String, String> NodeNameMap      = new HashMap<String, String>();  // Key is guid, Name is value
  private HashMap <String, String> DuplicateGuidMap = new HashMap<String, String>();  // Key is name, Guid is value
  private String fileName = "unknown";
  
  /************************************************************
   * Method Name:
   *  OSM_NodeNameMap
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param nodeNameMap
   * @param duplicateGuidMap
   ***********************************************************/
  public OSM_NodeNameMap(HashMap<String, String> nodeNameMap, HashMap<String, String> duplicateGuidMap)
  {
    super();
    NodeNameMap = nodeNameMap;
    DuplicateGuidMap = duplicateGuidMap;
  }

  /************************************************************
   * Method Name:
   *  OSM_NodeNameMap
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param fileName
   ***********************************************************/
  public OSM_NodeNameMap(String fileName)
  {
    super();
    this.fileName = fileName;
    
    if(fileName != null)
    {
      IB_NodeNameMapParser parser = new IB_NodeNameMapParser();
      try
      {
          parser.parseFile(fileName);
      }
      catch (Exception e)
      {
        logger.severe("Could not parse: " + fileName);
        logger.severe(e.getMessage());
      }
      NodeNameMap      = parser.getNodeNameMap();
      DuplicateGuidMap = parser.getDuplicateGuidMap();      
    }    
  }

  public String getNodeName(String sGuid)
  {
    return (getNodeName(new IB_Guid(sGuid)));
  }

  public String getNodeName(IB_Guid portGuid)
  {
    return NodeNameMap.get(portGuid.toString());
  }

  public HashMap<String, String> getNodeNameMap()
  {
    return NodeNameMap;
  }

  public HashMap<String, String> getDuplicateGuidMap()
  {
    return DuplicateGuidMap;
  }

  /************************************************************
   * Method Name:
   *  getFileName
   **/
  /**
   * Returns the value of fileName
   *
   * @return the fileName
   *
   ***********************************************************/
  
  public String getFileName()
  {
    return fileName;
  }

  public IB_Guid getNodeGuid(String name)
  {
    // given a name, return the first guid that matches
    // walk through the Map, and find a Guid for the name
    //
    // return null if no match
    
    IB_Guid nodeGuid = null;

    for(String guid : NodeNameMap.keySet())
    {
      if(name != null && name.equalsIgnoreCase(NodeNameMap.get(guid)))
      {
        nodeGuid = new IB_Guid(guid);
        break;
      }
    }
    return nodeGuid;
  }
  
  public String toContent()
  {
    StringBuffer stringValue = new StringBuffer();
    stringValue.append(OSM_NodeNameMap.class.getSimpleName() + SystemConstants.NEW_LINE);
    
    stringValue.append(" filename: " + this.getFileName() + SystemConstants.NEW_LINE);
    
    stringValue.append(" map entries: " + getNodeNameMap().size() + SystemConstants.NEW_LINE);
    for(Map.Entry<String, String> entry : getNodeNameMap().entrySet())
    {
      IB_Guid g = new IB_Guid(entry.getKey());
      String  n = entry.getValue();
      stringValue.append("   " + g.toColonString() + "  " + n + SystemConstants.NEW_LINE);
    }
    
    // duplicates are mapped opposite, to identify potential over-writes
    if(getDuplicateGuidMap().size() > 0)
    {
      stringValue.append(" duplicate entries: " + getDuplicateGuidMap().size() + SystemConstants.NEW_LINE);

      for(Map.Entry<String, String> entry : getDuplicateGuidMap().entrySet())
      {
        IB_Guid g = new IB_Guid(entry.getValue());
        String  n = entry.getKey();
        stringValue.append("   " + g.toColonString() + "  " + n + SystemConstants.NEW_LINE);
      }
    }
    return stringValue.toString();
  }

  @Override
  public String toString() {
    // TODO Auto-generated method stub
    StringBuffer stringValue = new StringBuffer();
    
    stringValue.append("Node/Name Pairs: " + this.getNodeNameMap().size() + SystemConstants.NEW_LINE);
    stringValue.append("Node/Name Duplicates: " + this.getDuplicateGuidMap().size() + SystemConstants.NEW_LINE);
    stringValue.append("Duplicates: " + this.getDuplicateGuidMap() + SystemConstants.NEW_LINE);
      
    return stringValue.toString();
  }

  public String toInfo()
  {
    StringBuffer stringValue = new StringBuffer();
    stringValue.append(OSM_NodeNameMap.class.getSimpleName() + SystemConstants.NEW_LINE);
    
    stringValue.append("   filename:                 " + this.getFileName() + SystemConstants.NEW_LINE);
    stringValue.append("   # map entries:            " + getNodeNameMap().size() + SystemConstants.NEW_LINE);
    stringValue.append("   # duplicates in map:      " + this.getDuplicateGuidMap().size() + SystemConstants.NEW_LINE);
  
    return stringValue.toString();
  }
  
}
