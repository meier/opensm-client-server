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
 *        file: IB_NodeNameMapParser.java
 *
 *  Created on: Nov 2, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.parser;

import gov.llnl.lc.infiniband.core.IB_Guid;
import gov.llnl.lc.infiniband.opensm.plugin.data.OSM_NodeNameMap;
import gov.llnl.lc.parser.ParserUtils;
import gov.llnl.lc.util.SystemConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;

public class IB_NodeNameMapParser  extends IB_AbstractParser
{

  // Maps a Name or Description to a GUID
  //
  // GUIDs are supposed to be unique, so there probably shouldn't be values repeated
  // in the file.  WARN if so.  It may be a cut and paste issue, or it may be that
  // the most current version should be used (over write previous).  In any case the
  // behavior is undefined.
  //

  private HashMap <String, String> NodeNameMap      = new HashMap<String, String>();  // Key is guid, Name is value
  private HashMap <String, String> DuplicateGuidMap = new HashMap<String, String>();  // Key is name, Guid is value
  
  protected void initParser()
  {
    // get rid of instance data
    super.initParser();
    NodeNameMap.clear();
    DuplicateGuidMap.clear();
  }
  
  public void parse(BufferedReader in) throws IOException
  {
    // usually, you have to override this method
    linesParsed = 0;
    String line;  // get one line at a time
    while ((line = in.readLine()) != null)
    {
      // parse the file, line by line
      linesParsed++;
      
     // save comments??
      //
      // simple format, guid "name"
      if(line.trim().startsWith("#"))
      {
        // just a comment, may want to save the header, it has info
      }
      else
      {
        Matcher m = ParserUtils.getMatching("0x\\w+", line);
        if(m != null)
        {
          // found a set of hex numbers
          String guid = m.group();
          String name = line.substring(m.end());
          String prev = null;
          m = ParserUtils.getMatching("\".+\"", name);
          
          if(m != null)
          {
            // this includes the quotes, so trim off the first and last char
            name = m.group().substring(1, m.end() -2).trim();         
            IB_Guid nodeGuid   = new IB_Guid(guid);
            prev = NodeNameMap.put(nodeGuid.toString(), name);
            
            // duplicates shouldn't exists, save for debugging
            if(prev != null)
            {
              logger.severe("Duplicate GUID and Name Pairs (" + nodeGuid + ": prev[" + prev + "]");
              logger.severe("Duplicate GUID and Name Pairs (" + nodeGuid + ": curr[" + name + "]");
              DuplicateGuidMap.put(prev, nodeGuid.toString());
              DuplicateGuidMap.put(name, nodeGuid.toString());
            }
          }
        }
      }
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

  @Override
  public String toString() {
    // TODO Auto-generated method stub
    StringBuffer stringValue = new StringBuffer();
    
    stringValue.append("File: " + this.getFileName() + SystemConstants.NEW_LINE);
    stringValue.append("Lines: " + this.getLinesParsed() + SystemConstants.NEW_LINE + SystemConstants.NEW_LINE);
    
    stringValue.append("Node/Name Pairs: " + this.getNodeNameMap().size() + SystemConstants.NEW_LINE);
    stringValue.append("Node/Name Duplicates: " + this.getDuplicateGuidMap().size() + SystemConstants.NEW_LINE);
    stringValue.append("Duplicates: " + this.getDuplicateGuidMap() + SystemConstants.NEW_LINE);
      
    return stringValue.toString();
  }
  
    @Override
    public String getSummary()
    {
      StringBuffer stringValue = new StringBuffer();
      
      stringValue.append(super.getSummary());
      
      //
      stringValue.append("Node/Name Pairs: " + this.getNodeNameMap().size() + SystemConstants.NEW_LINE);
      stringValue.append("Node/Name Duplicates: " + this.getDuplicateGuidMap().size() + SystemConstants.NEW_LINE);
      if(this.getDuplicateGuidMap().size() > 0)
        stringValue.append("Duplicates: " + this.getDuplicateGuidMap() + SystemConstants.NEW_LINE);
      
      return stringValue.toString();
    }
  
  /**
   * @param args
   */
  public static void main(String[] args) 
  {
    IB_NodeNameMapParser parser = new IB_NodeNameMapParser();
    try
    {
        parser.parseFile("/home/meier3/dev/ib-node-name-map");
    }
    catch (IOException ioe)
    {
      System.out.println("ParseFile exception: " + ioe.getMessage());
    }
    
    System.out.println(parser.toString());
    System.out.println("The Node name is: " + parser.getNodeName("0x0008f104003f15c0"));
    System.out.println("The Node Guid is: " + parser.getNodeGuid("ibt1 (R 5 L 8a) ISR9288/ISR9096 sLB-24D"));
    System.out.println("The Node Guid is: " + parser.getNodeGuid(parser.getNodeName("0x0008f104003f15c0")));
    
    System.out.println("");
    OSM_NodeNameMap nnm = new OSM_NodeNameMap(parser.getNodeNameMap(), parser.getDuplicateGuidMap());
    System.out.println(nnm.toString());
    System.out.println("The Node name is: " + nnm.getNodeName("0x0008f104003f15c0"));
    System.out.println("The Node Guid is: " + nnm.getNodeGuid("ibt1 (R 5 L 8a) ISR9288/ISR9096 sLB-24D"));
    System.out.println("The Node Guid is: " + nnm.getNodeGuid(nnm.getNodeName("0x0008f104003f15c0")));
  }

  @Override
  public void parseString(String string) throws IOException
  {
    // TODO Auto-generated method stub
    
  }
}
