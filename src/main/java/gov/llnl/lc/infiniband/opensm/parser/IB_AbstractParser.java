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

import gov.llnl.lc.logging.CommonLogger;
import gov.llnl.lc.parser.Parser;
import gov.llnl.lc.time.TimeStamp;
import gov.llnl.lc.util.SystemConstants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public abstract class IB_AbstractParser implements Parser, CommonLogger
{
  protected String FileName;
  protected TimeStamp FileTimeStamp;
  protected int linesParsed;
  
  protected void initParser()
  {
    // get rid of instance data
    FileName = "";
    linesParsed = 0;
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
      if ( logger.isLoggable(java.util.logging.Level.FINEST) )
      {
        logger.finest(line);
      }
    }
  }
  
  public void parseFile(File file) throws IOException
  {
    BufferedReader fr = new BufferedReader(new FileReader(file));
    setFileName(file.getAbsolutePath());
    FileTimeStamp = new TimeStamp(file.lastModified());
    
    if ( logger.isLoggable(java.util.logging.Level.INFO) )
    {
      logger.info("Parsing File: " + this.getFileName());
    }
    parse(fr);
  }

  public void parseFile(String filename) throws IOException
  {
      parseFile(new File(filename));
  }

  public String getFileName() 
  {
    return FileName;
  }

  public void setFileName(String fileName) 
  {
    // clear previous, prepare for a new file
    initParser();
    FileName = fileName;
  }

    public String getSummary() 
    {
       StringBuffer stringValue = new StringBuffer();
      
       stringValue.append(this.getClass().getName() + SystemConstants.NEW_LINE);
       stringValue.append(this.getFileName() + SystemConstants.NEW_LINE);
       stringValue.append("Date: " + this.getFileTimeStamp() + SystemConstants.NEW_LINE);
       stringValue.append("Lines: " +this.getLinesParsed() + SystemConstants.NEW_LINE);
          
      return stringValue.toString();
  }
  
  public TimeStamp getFileTimeStamp()
  {
    return FileTimeStamp;
  }

  public void setFileTimeStamp(TimeStamp fileTimeStamp)
  {
    FileTimeStamp = fileTimeStamp;
  }

  public int getLinesParsed() 
  {
    return linesParsed;
  }
}
