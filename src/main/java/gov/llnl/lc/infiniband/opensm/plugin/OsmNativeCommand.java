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
 *        file: OsmNativeCommand.java
 *
 *  Created on: Jul 28, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin;

import gov.llnl.lc.system.CommandLineArguments;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;


/**********************************************************************
 * Describe purpose and responsibility of OsmNativeCommand
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Dec 3, 2014 4:08:24 PM
 **********************************************************************/
public enum OsmNativeCommand implements Serializable
{
  OSM_NATIVE_ECHO(        0, "native-echo"),    
  OSM_NATIVE_LSWEEP(      1, "light-sweep"),    
  OSM_NATIVE_HSWEEP(      2, "heavy-sweep"),    
  OSM_NATIVE_REROUTE(     3, "reroute"),    
  OSM_NATIVE_LOGLEVEL(    4, "loglevel"),    
  OSM_NATIVE_UPDATE_DESC( 5, "update-desc"),    
  OSM_NATIVE_PSWEEP(      6, "psweep"),    
  OSM_NATIVE_PPERIOD(     7, "psweep-period"),    
  OSM_NATIVE_PCLEAR(      8, "clear-counters"),    
  OSM_NATIVE_MAX(         9, "final");
  
  public static final EnumSet<OsmNativeCommand> OSM_ALL_NATIVE_COMMANDS = EnumSet.allOf(OsmNativeCommand.class);
  
  private static final Map<Integer,OsmNativeCommand> lookup = new HashMap<Integer,OsmNativeCommand>();

  static 
  {
    for(OsmNativeCommand s : OSM_ALL_NATIVE_COMMANDS)
         lookup.put(s.getNativeCommand(), s);
  }

  private int CommandNum;
  private String CommandName;

private OsmNativeCommand(int CommandNum, String Name)
{
    this.CommandNum = CommandNum;
    this.CommandName = Name;
}

public int getNativeCommand()
{
  return CommandNum;
}

public String getCommandName()
{
  return CommandName;
}

public static OsmNativeCommand get(int CommandNum)
{ 
    return lookup.get(CommandNum); 
}

public static OsmNativeCommand get(String CommandName)
{
  // given the command name, return the command
  for(OsmNativeCommand s : OSM_ALL_NATIVE_COMMANDS)
  {
    // return on the first match
    if(s.getCommandName().equalsIgnoreCase(CommandName))
      return s;
  }
  return null; 
}

public static OsmNativeCommand get(CommandLineArguments command)
{
  // given a CommandLineArgument object, return the command
  if((command == null) || (command.getCommandLine() == null) || (command.getCommandLine().length() < 2))
    return null;
 
 // check the first word in the command line, and see if it matches one of the native commands
 String[] cmdArgs = command.getCommandLine().split(" ");
 if((cmdArgs == null) || (cmdArgs.length < 1))
   return null;
 
 return OsmNativeCommand.get(cmdArgs[0]);
}

public static boolean isNativeCommand(CommandLineArguments command)
{
  if(OsmNativeCommand.get(command) != null)
    return true;
  return false;
}

}
