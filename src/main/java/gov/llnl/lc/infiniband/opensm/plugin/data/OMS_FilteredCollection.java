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
 *        file: OMS_FilteredCollection1.java
 *
 *  Created on: Nov 23, 2016
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import gov.llnl.lc.infiniband.opensm.plugin.OsmConstants;

/**********************************************************************
 * Describe purpose and responsibility of OMS_FilteredCollection1
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Nov 23, 2016 12:25:24 PM
 **********************************************************************/
public class OMS_FilteredCollection extends OMS_Collection implements Serializable, OsmConstants, gov.llnl.lc.logging.CommonLogger
{
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = 1L;
  
  private String FilterFileName  = null;
  private String HistoryFileName = null;


  /************************************************************
   * Method Name:
   *  OMS_FilteredCollection
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param maxSize
   ***********************************************************/
  public OMS_FilteredCollection(int maxSize)
  {
    super(maxSize);
    // TODO Auto-generated constructor stub
  }

  /************************************************************
   * Method Name:
   *  getFilterFileName
  **/
  /**
   * Returns the value of filterFileName
   *
   * @return the filterFileName
   *
   ***********************************************************/
  
  public String getFilterFileName()
  {
    return FilterFileName;
  }

  /************************************************************
   * Method Name:
   *  setFilterFileName
  **/
  /**
   * Sets the value of filterFileName
   *
   * @param filterFileName the filterFileName to set
   *
   ***********************************************************/
  public void setFilterFileName(String filterFileName)
  {
    FilterFileName = filterFileName;
  }

  /************************************************************
   * Method Name:
   *  getHistoryFileName
  **/
  /**
   * Returns the value of historyFileName
   *
   * @return the historyFileName
   *
   ***********************************************************/
  
  public String getHistoryFileName()
  {
    return HistoryFileName;
  }

  /************************************************************
   * Method Name:
   *  setHistoryFileName
  **/
  /**
   * Sets the value of historyFileName
   *
   * @param historyFileName the historyFileName to set
   *
   ***********************************************************/
  public void setHistoryFileName(String historyFileName)
  {
    HistoryFileName = historyFileName;
  }
  
  public static OMS_FilteredCollection readOMS_FilteredCollection(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException
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
    
    OMS_FilteredCollection obj = null;
    Object unknownObject = objectInputStream.readObject();
    if(unknownObject instanceof OMS_FilteredCollection)
      obj = (OMS_FilteredCollection) unknownObject;

    objectInputStream.close();
    if(useCompression)
      in.close();
    fileInput.close();
    return obj;
  }
  
  public static void writeOMS_FilteredCollection(String fileName, OMS_Collection omsHistory) throws IOException
  {
    OMS_Collection.writeOMS_Collection(fileName, omsHistory);
    return;
  }


  
  /************************************************************
   * Method Name:
   *  toInfo
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
    stringValue.append(OMS_FilteredCollection.class.getSimpleName() + "\n");
    
    stringValue.append("original history file name:  " + this.getHistoryFileName() + "\n");
    stringValue.append("filter file name:            " + this.getFilterFileName() + "\n");
    stringValue.append("fabric name:                 " + this.getFabricName() + "\n");
    stringValue.append("first timestamp:             " + this.getOldestOSM_Fabric().toTimeString() + "\n");
    stringValue.append("last timestamp:              " + this.getCurrentOSM_Fabric().toTimeString() + "\n");
    stringValue.append("ave secs between records:    " + this.getAveDeltaSeconds() + "\n");
    stringValue.append("# secs between pfmgr sweeps: " + this.getOldestOSM_Fabric().getPerfMgrSweepSecs() + "\n");
    stringValue.append("# records in collection:     " + getSize() + "\n");
    stringValue.append("# nodes:                     " + this.getOldestOSM_Fabric().getOSM_Nodes().size() + "\n");
    stringValue.append("# ports:                     " + this.getOldestOSM_Fabric().getOSM_Ports().size() + "\n");
    stringValue.append("# links:                     " + this.getOldestOSM_Fabric().getIB_Links().size());
  
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
    StringBuffer buff = new StringBuffer();
    boolean initial = true;
    for (Map.Entry<String, OpenSmMonitorService> entry : omsHistory.entrySet())
    {
      OpenSmMonitorService f = entry.getValue();
      if(!initial)
        buff.append("\n");
      else
        initial = false;
      
      buff.append(f.toTimeString());
    }
    return buff.toString();
  }


  /************************************************************
   * Method Name:
   *  OMS_FilteredCollection
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   ***********************************************************/
  public OMS_FilteredCollection()
  {
    // TODO Auto-generated constructor stub
  }

}
