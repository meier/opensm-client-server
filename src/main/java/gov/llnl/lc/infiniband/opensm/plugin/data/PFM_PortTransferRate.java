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
 *        file: PFM_PortTransferRate.java
 *
 *  Created on: Jul 27, 2012
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.data;

import java.math.BigInteger;

/**********************************************************************
 * Describe purpose and responsibility of PFM_PortTransferRate
 * <p>
   * @deprecated  Use PFM_PortRate instead
   * 
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Jul 27, 2012 9:48:39 AM
 **********************************************************************/
@Deprecated
public class PFM_PortTransferRate
{
  
  /************************************************************
   * Method Name:
   *  getChangeRate
  **/
  /**
   * Calculates the rate of change of the named counter.
   *
   * @deprecated  Use PFM_PortRate instead
   * 
   * @see     describe related java objects
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @param portChange
   * @param name
   * @return
   ***********************************************************/
  public static long getChangeRate(PFM_PortChange portChange, PFM_Port.PortCounterName name)
  {
    if((portChange != null) && (portChange.getDelta_counter_ts() > 0))
    {
      // the rate is just the difference counter divided by the timestamp
       
      // use BigIntegers, because divides can become problematic, but only where absolutely necessary
      BigInteger pc = PFM_Port.convertUnsignedLongLongToBigInteger(portChange.getDelta_port_counter(name));
      
      // multiply by the scale, normally just 1
      pc.multiply(BigInteger.valueOf(name.getScale()));
      
      // only use BigIntegers when absolutely necessary
      return pc.divide(BigInteger.valueOf(portChange.getDelta_counter_ts())).longValue();
    }
    return 0L;
  }

  /************************************************************
   * Method Name:
   *  getTransmitRate
  **/
  /**
   * Calculates the data transfer rate of the "xmit_data"
   *
   * @deprecated  Use PFM_PortRate instead
   * 
   * @see     describe related java objects
   * @param n1
   * @param n2
   * @return
   ***********************************************************/
  public static long getTransmitRate(PFM_PortChange portChange)
  {
    return PFM_PortTransferRate.getChangeRate(portChange, PFM_Port.PortCounterName.xmit_data);
  }

  /************************************************************
   * Method Name:
   *  getReceiveRate
  **/
  /**
   * Calculates the data transfer rate of the "xmit_data"
   *
   * @deprecated  Use PFM_PortRate instead
   * 
   * @see     describe related java objects
   * @param n1
   * @param n2
   * @return
   ***********************************************************/
  public static long getReceiveRate(PFM_PortChange portChange)
  {
    return PFM_PortTransferRate.getChangeRate(portChange, PFM_Port.PortCounterName.rcv_data);
  }

  /************************************************************
   * Method Name:
   *  toVerboseDiagnosticString
  **/
  /**
   * Return a string that shows everything about xmit and rcv data
   *
   * @deprecated  Use PFM_PortRate instead
   * 
   * @see java.lang.Object#toString()
  
   * @param   describe the parameters
   *
   * @return
   ***********************************************************/
  
  public static String toVerboseDiagnosticString(PFM_PortChange portChange)
  {
    StringBuffer sbuff = new StringBuffer();
    
    if(portChange == null)
      return "The PFM_PortChange object was null";
    
//    sbuff.append(portChange.toString());
    sbuff.append("\n Receive  Rate= " + PFM_PortTransferRate.getReceiveRate(portChange));
    sbuff.append("\n Transmit Rate= " + PFM_PortTransferRate.getTransmitRate(portChange));
    sbuff.append("\n");

    return sbuff.toString();
  }


}
