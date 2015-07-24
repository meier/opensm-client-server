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
 *        file: OSM_EventObject.java
 *
 *  Created on: Aug 3, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import gov.llnl.lc.infiniband.core.IB_Port;
import gov.llnl.lc.infiniband.core.NativePeerClass;
import gov.llnl.lc.infiniband.opensm.plugin.OsmNativeInterface;

import java.io.Serializable;

/**********************************************************************
 * An <code>OSM_EventObject</code> object contains relevant information
 * about a fabric event.
  * <p>
 * Java Peer Class, to the native interface.  All peer classes contain
 * public members which can be directly accessed.  All peer classes
 * have fully parameterized constructors that are used by the native
 * layer to create an instance of the class.  Instances of peer classes
 * are not intended to be created from the java environment.
* <p>
 * @see OsmNativeInterface#wait_for_event()
 * @see NativePeerClass
 *
 * @author meier3
 * 
 * @version Aug 29, 2011 2:47:35 PM
 **********************************************************************/
@NativePeerClass("v1.0")
public class OSM_EventObject implements Serializable
{  
  /* this is the a simplified event, constructed from various types
   * of events contained in osm_event_plugin.h.
   * 
   *  refer to osmJniPi.c for details
   *  
   *  also jsi_PortDesc_t is similar to the IB_Port java object 
   *
typedef struct OsmEventType
{
  int EventId;
  int trapType;
  int trapNum;
  int trapLID;
  jsi_PortDesc_t PortDescription;
}jsr_OsmEvent;

   */
  
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = -3158818468737315077L;
  
  /**  describe EventId here **/
  public int EventId;
  /**  describe TrapType here **/
  public int TrapType;
  /**  describe TrapNum here **/
  public int TrapNum;
  /**  describe TrapLID here **/
  public int TrapLID;
  /**  describe Port here **/
  public IB_Port Port;

  /************************************************************
   * Method Name:
   *  OSM_EventObject
   */
   /**
   * Default constructor
   *
   ***********************************************************/
  public OSM_EventObject()
  {
  }

  /************************************************************
   * Method Name:
   *  OSM_EventObject
   */
   /**
 *  The fully parameterized constructor used by the native layer
 * to create an instance of this peer class.
   *
   * @param eventId the type of an event
   * @param trapType if a trap event, the type of the trap
   * @param trapNum if a trap event, the number of the trap
   * @param trapLID the LID of the trap
   * @param port the port number, if relevent
   ***********************************************************/
  public OSM_EventObject(int eventId, int trapType, int trapNum, int trapLID, IB_Port port)
  {
    super();
    EventId = eventId;
    TrapType = trapType;
    TrapNum = trapNum;
    TrapLID = trapLID;
    Port = port;
  }
  
  @Override
  public String toString()
  {
    return "OSM_EventObject [EventId=" + EventId + ", TrapType=" + TrapType + ", TrapNum="
        + TrapNum + ", TrapLID=" + TrapLID + ", Port=" + Port + "]";
  }

}
