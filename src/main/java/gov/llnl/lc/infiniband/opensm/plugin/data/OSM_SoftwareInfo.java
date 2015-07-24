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
 *        file: OSM_SoftwareInfo.java
 *
 *  Created on: Aug 14, 2012
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import gov.llnl.lc.system.SoftwareComponent;
import gov.llnl.lc.system.rpm.RpmQuery;

import java.util.HashMap;
import java.util.Set;

/**********************************************************************
 * Describe purpose and responsibility of OSM_SoftwareInfo
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Aug 14, 2012 4:44:39 PM
 **********************************************************************/
public class OSM_SoftwareInfo
{
  private HashMap <String, SoftwareComponent> SoftwareComponents = new HashMap<String, SoftwareComponent>();  // Key is name, Component is value

  static final String OPENSM_PKG_NAME            = "opensm";
  static final String LDAPOTP_PKG_NAME           = "llnl-ldapotp-clt-java";
  static final String LDAPOTP_JNI_PKG_NAME       = "llnl-ldapotp-clt-jni-auth-libs";
  static final String OSM_SERVER_PKG_NAME        = "opensm-client-server-java";
  static final String OSM_CLIENT_PKG_NAME        = "opensm-client-server-java";
  static final String OSM_PLUGIN_PKG_NAME        = "opensm-jni-plugin-libs";
  static final String CURSES_JNI_PKG_NAME        = "llnl-curses-jni-libs";
  static final String OMS_SMT_PKG_NAME           = "opensm-smt-java";

  /************************************************************
   * Method Name:
   *  getSoftwareComponents
   **/
  /**
   * Returns the value of softwareComponents
   *
   * @return the softwareComponents
   *
   ***********************************************************/
  
  public HashMap<String, SoftwareComponent> getSoftwareComponents()
  {
    return SoftwareComponents;
  }

  /************************************************************
   * Method Name:
   *  getKeySet
   **/
  /**
   * Returns the keys for this set of information
   *
   * @return the set of keys
   *
   ***********************************************************/
  
  public Set<String> getKeySet()
  {
    return SoftwareComponents.keySet();
  }

  /************************************************************
   * Method Name:
   *  getSoftwareComponent
  **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @param key
   * @return
   ***********************************************************/
  public SoftwareComponent getSoftwareComponent(String key)
  {
    return SoftwareComponents.get(key);
  }

  /************************************************************
   * Method Name:
   *  OSM_SoftwareInfo
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param   describe the parameters if any
   * @throws Exception 
   *
   ***********************************************************/
  public OSM_SoftwareInfo() throws Exception
  {
    super();
    //  collect as much info as possible
    SoftwareComponents.put(OPENSM_PKG_NAME,      (new RpmQuery(OPENSM_PKG_NAME)).getRpmInfo());
    SoftwareComponents.put(LDAPOTP_PKG_NAME,     (new RpmQuery(LDAPOTP_PKG_NAME)).getRpmInfo());
    SoftwareComponents.put(LDAPOTP_JNI_PKG_NAME, (new RpmQuery(LDAPOTP_JNI_PKG_NAME)).getRpmInfo());
    SoftwareComponents.put(OSM_SERVER_PKG_NAME,  (new RpmQuery(OSM_SERVER_PKG_NAME)).getRpmInfo());
    SoftwareComponents.put(OSM_CLIENT_PKG_NAME,  (new RpmQuery(OSM_CLIENT_PKG_NAME)).getRpmInfo());
    SoftwareComponents.put(OSM_PLUGIN_PKG_NAME,  (new RpmQuery(OSM_PLUGIN_PKG_NAME)).getRpmInfo());
    SoftwareComponents.put(CURSES_JNI_PKG_NAME,  (new RpmQuery(CURSES_JNI_PKG_NAME)).getRpmInfo());
    SoftwareComponents.put(OMS_SMT_PKG_NAME,     (new RpmQuery(OMS_SMT_PKG_NAME)).getRpmInfo());
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
   * @throws Exception 
   ***********************************************************/
  public static void main(String[] args)
  {
    try
    {
      OSM_SoftwareInfo si = new OSM_SoftwareInfo();
      for(String key: si.SoftwareComponents.keySet())
      {
        System.out.println("Key ("+key+") :" + si.SoftwareComponents.get(key).toString());
      }
    }
    catch (Exception ioe)
    {
      System.out.println("OSM_SoftwareInfo exception: " + ioe.getMessage());
    }
   
  }
  

}
