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
 *        file: WhiteAndBlackListFilter.java
 *
 *  Created on: Sep 18, 2013
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.util.filter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

/**********************************************************************
 * A WhiteAndBlackListFilter is a simple filter using the common "white"
 * (include) and "black" (exclude) rules.  This filter contains an additional
 * "file" list, which simply allows a hierarchy of rules.  Typically, a
 * common set of rules would be placed in the initial file, and links to
 * additional files containing other rules would be listed.  In this way,
 * the original file does not need to be modified, while other files can
 * be changed for special situations.
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Sep 18, 2013 1:25:05 PM
 **********************************************************************/
public class WhiteAndBlackListFilter implements gov.llnl.lc.logging.CommonLogger
{
  // if empty, include all results.  if not empty, only include results that contain a string in this list
  protected java.util.ArrayList<String> WhiteList     = new java.util.ArrayList<String>();
  // if empty, include all results.  if not empty, only reject results that contain a string in this list
  protected java.util.ArrayList<String> BlackList     = new java.util.ArrayList<String>();
  
  // the files used to create the Filter
  protected java.util.ArrayList<String> FileList     = new java.util.ArrayList<String>();

  // an optional name for this filter
  private String FilterName = "unknown";
  
  // a unique identifier, for this filter (name isn't good enough)
  private UUID FilterID = null;

   /************************************************************
   * Method Name:
   *  WhiteAndBlackListFilter
  **/
  /**
   * An empty filter.  Everything should pass through this filter.
   *
   * @see     describe related java objects
   *
   * @param filterFileName
   ***********************************************************/
  public WhiteAndBlackListFilter()
  {
    super();
  }

  /************************************************************
   * Method Name:
   *  WhiteAndBlackListFilter
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param filterFileName
   * @throws IOException 
   ***********************************************************/
  public WhiteAndBlackListFilter(String filterFileName) throws IOException
  {
    super();
    
    // initialize the filter via the file
    WhiteAndBlackListFilter f = readFilter(filterFileName);
    if(f != null)
    {
      setWhiteList(f.getWhiteList());
      setBlackList(f.getBlackList());
      setFileList(f.getFileList());
      setFilterName(f.getFilterName());
    }
    setFilterID();
  }
  
  /************************************************************
   * Method Name:
   *  WhiteAndBlackListFilter
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param configMap
   * @throws IOException 
   ***********************************************************/
  public WhiteAndBlackListFilter(Map<String, String> configMap, String filterPropertyName) throws IOException
  {
    super();
    
    // initialize the filter via the file
    WhiteAndBlackListFilter f = initFilter(configMap, filterPropertyName);
    if(f != null)
    {
      setWhiteList(f.getWhiteList());
      setBlackList(f.getBlackList());
      setFileList(f.getFileList());
      setFilterName(f.getFilterName());
    }
    setFilterID();
  }

  /************************************************************
   * Method Name:
   *  WhiteAndBlackListFilter
  **/
  /**
   * Describe the constructor here
   *
   * @see     describe related java objects
   *
   * @param whiteList
   * @param blackList
   ***********************************************************/
  public WhiteAndBlackListFilter(String name, ArrayList<String> whiteList, ArrayList<String> blackList, ArrayList<String> fileList)
  {
    super();
    FilterName = name;
    WhiteList = whiteList;
    BlackList = blackList;
    FileList  = fileList;
    int wls = WhiteList == null ? 0: WhiteList.size();
    int bls = BlackList == null ? 0: BlackList.size();
    int fls = FileList  == null ? 0: FileList.size();
    logger.info("Filter - WL:" + wls + ", BL:" + bls + ", FL:" + fls);
    setFilterID();
  }

  protected static WhiteAndBlackListFilter initFilter(Map<String,String> map, String filterPropertyName) throws IOException
  {
    // check to see if anything needs to be initialized
    if(map == null)
      return null;

    // the name of the filter file should be in the map
    String filterFile = map.get(filterPropertyName);
    
    if(filterFile != null)
      return readFilter(filterFile);
    return null;
  }
  
  private void setFilterID()
  {
    FilterID = UUID.randomUUID();
  }
  
  /************************************************************
   * Method Name:
   *  getWhiteList
   **/
  /**
   * Returns the value of whiteList
   *
   * @return the whiteList
   *
   ***********************************************************/
  
  public java.util.ArrayList<String> getWhiteList()
  {
    return WhiteList;
  }

  /************************************************************
   * Method Name:
   *  getFilterName
  **/
  /**
   * Returns the value of filterName
   *
   * @return the filterName
   *
   ***********************************************************/
  
  public String getFilterName()
  {
    return FilterName;
  }

  public void setFilterName(String name)
  {
    FilterName = name;
  }

  /************************************************************
   * Method Name:
   *  getFilterID
  **/
  /**
   * Returns the value of filterID
   *
   * @return the filterID
   *
   ***********************************************************/
  
  public UUID getFilterID()
  {
    return FilterID;
  }

  /************************************************************
   * Method Name:
   *  setWhiteList
   **/
  /**
   * Sets the value of whiteList
   *
   * @param whiteList the whiteList to set
   *
   ***********************************************************/
  public void setWhiteList(java.util.ArrayList<String> whiteList)
  {
    WhiteList     = new java.util.ArrayList<String>();
    addWhiteList(whiteList);
  }

  /************************************************************
   * Method Name:
   *  getBlackList
   **/
  /**
   * Returns the value of blackList
   *
   * @return the blackList
   *
   ***********************************************************/
  
  public java.util.ArrayList<String> getBlackList()
  {
    return BlackList;
  }

  /************************************************************
   * Method Name:
   *  setBlackList
   **/
  /**
   * Sets the value of blackList
   *
   * @param blackList the blackList to set
   *
   ***********************************************************/
  public void setBlackList(java.util.ArrayList<String> blackList)
  {
    BlackList     = new java.util.ArrayList<String>();
    addBlackList(blackList);
  }
  
  /************************************************************
   * Method Name:
   *  getFileList
   **/
  /**
   * Returns the value of FileList
   *
   * @return the FileList
   *
   ***********************************************************/
  
  public java.util.ArrayList<String> getFileList()
  {
    return FileList;
  }

  /************************************************************
   * Method Name:
   *  setFileList
   **/
  /**
   * Sets the value of fileList
   *
   * @param fileList the fileList to set
   *
   ***********************************************************/
  public void setFileList(java.util.ArrayList<String> fileList)
  {
    FileList     = new java.util.ArrayList<String>();
    addFileList(fileList);
  }
  
  protected boolean passListCheck(String test)
  {
    return (WhiteAndBlackListFilter.passListCheck(test, WhiteList, BlackList));
  }

  public boolean isFiltered(String test)
  {
    return WhiteAndBlackListFilter.isFiltered(test, WhiteList, BlackList);
  }

  protected static boolean passListCheck(String test, java.util.ArrayList<String> whiteList, java.util.ArrayList<String> blackList)
  {
    // will this String pass both list checks
    //    return true only if
    //  IS  in the WhiteList and
    //  NOT in the BlackList
    return (WhiteAndBlackListFilter.isInWhiteList(test, whiteList) && !WhiteAndBlackListFilter.isInBlackList(test, blackList));
  }

  public static boolean isFiltered(String test, java.util.ArrayList<String> whiteList, java.util.ArrayList<String> blackList)
  {
    // will this String pass both list checks
    //    return true only if
    //  IS  in the WhiteList and
    //  NOT in the BlackList
    return !(WhiteAndBlackListFilter.passListCheck(test, whiteList, blackList));
  }

  public boolean isInWhiteList(String test)
  {
    return isInWhiteList(test, WhiteList);
  }

  public static boolean isInWhiteList(String test, java.util.ArrayList<String> whiteList)
  {
    // true if the WhiteList is empty
    if((whiteList == null) || (whiteList.size() == 0))
      return true;

    return isInStringList(test, whiteList);
  }
  
  public static String convertSpecialFileName(String fname)
  {
    if (fname == null)
      return null;

    String fileName = fname;

    if (fname.startsWith("%h"))
      fileName = System.getProperty("user.home") + fname.substring(2);
    if (fname.startsWith("%t"))
      fileName = System.getProperty("java.io.tmpdir") + fname.substring(2);
    return fileName;
  }


  public boolean isInBlackList(String test)
  {
    return isInBlackList(test, BlackList);
  }

  public static boolean isInBlackList(String test, java.util.ArrayList<String> blackList)
  {
    // false if the BlackList is empty
    if((blackList == null) || (blackList.size() == 0))
      return false;

    return isInStringList(test, blackList);
  }

  protected static boolean isInStringList(String string, java.util.ArrayList<String> stringList)
  {
    // return true if the test string contains
    // any string in the stringList
    if((string == null) || (string.length() == 0))
      return false;
    
    // iterate through the list, and return true ASAP
    for(String s: stringList)
      if(string.indexOf(s) > -1)
        return true;

    return false;
  }
  
  protected static void writeHeader(PrintWriter out, String fileName, WhiteAndBlackListFilter filter, String description)
  {
    out.println("#");
    out.println("$ " + filter.getFilterName());
    out.println("#");
    out.println("# filter ID: " + filter.getFilterID().toString());
    out.println("# file:      " + fileName);
    out.println("# date:      " + new gov.llnl.lc.time.TimeStamp().toString());
    out.println("#");
    out.println("# author:    " + gov.llnl.lc.system.useraccount.User.getUserName());
    if(description != null)
      out.println("# " + description);
    out.println("#");
    writeFormatHelp(out);
    out.println("#");

    return;
  }
  
  protected static void writeFormatHelp(PrintWriter out)
  {
    out.println("###################################################################");
    out.println("# Beginning of Line characters for B&W Filter");
    out.println("#");
    out.println("# $ <name of this filter>");
    out.println("# # <comment>");
    out.println("# * White - b/w toggle, following lines are included in white list");
    out.println("# * Black - b/w toggle, following lines are included in black list");
    out.println("# @ <filename> - should be the name of a B&W Filter file to include");
    out.println("###################################################################");
    return;
  }
  
  public static File writeFilter(String fileName, WhiteAndBlackListFilter filter) throws IOException
  {
    return writeFilter(fileName, filter, null);
  }
 
  public static File writeFilter(String fileName, WhiteAndBlackListFilter filter, String description) throws IOException
  {
    if(filter == null)
    {
      logger.severe("NULL filter, not writing");
      return null;
    }
    
    String fName = convertSpecialFileName(fileName);
    File outFile = new File(fName);
    if (outFile.exists()) 
    {
      logger.severe("File: (" + fName +") already exists, overwriting");
    }
    else
    {
      outFile.createNewFile();
    }
    // create a simple human readable text file
    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outFile)));
    writeHeader(out, fileName, filter, description);
    
    if(filter.hasWhiteList())
    {
      out.println("* White");
      for(String string: filter.getWhiteList())
      {
        out.println(string);
      }
      out.println("#");
    }
    if(filter.hasBlackList())
    {
      out.println("* Black");
      for(String string: filter.getBlackList())
      {
        out.println(string);
      }
      out.println("#");
    }
    out.close();
    return outFile;
}
  /************************************************************
   * Method Name:
   *  hasEmptyFilter
  **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects
   *
   * @return
   ***********************************************************/
  public boolean hasEmptyFilter()
  {
    return !hasBlackList() && !hasWhiteList();
  }

  /************************************************************
   * Method Name:
   *  hasBlackList
  **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects
   *
   * @return
   ***********************************************************/
  public boolean hasBlackList()
  {
    return (BlackList != null) && !(BlackList.isEmpty());
  }

  /************************************************************
   * Method Name:
   *  hasWhiteList
  **/
  /**
   * Describe the method here
   *
   * @see     describe related java objects
   *
   * @return
   ***********************************************************/
  public boolean hasWhiteList()
  {
    return (WhiteList != null) && !(WhiteList.isEmpty());
  }
  
  protected static WhiteAndBlackListFilter readFilter(String fileName) throws IOException
  {
    String fName = convertSpecialFileName(fileName);
    File inFile = new File(fName);
    if (!inFile.exists()) 
    {
      logger.severe("Could not find file: (" + fName +") for reading");
      System.err.println("Could not find file: (" + fName +") for reading");
      return null;
    }
    
    java.util.ArrayList<String> WL     = new java.util.ArrayList<String>();
    java.util.ArrayList<String> BL     = new java.util.ArrayList<String>();
    java.util.ArrayList<String> FL     = new java.util.ArrayList<String>();
    FL.add(fName);

    BufferedReader br = new BufferedReader( new FileReader( inFile )) ;
    String readString = null;
    String fname = null;
    String filterName = null;
    boolean isWhiteListType = true;   // the default
    
    // don't include leading and trailing white space
    while(( readString = br.readLine())  != null)
    {
      String str = readString.trim();
      // skip empty lines
      if(str.length() > 1)
      {
        switch( str.charAt(0))
        {
          case '$':
            // name of the filter
            String n = str.substring(1);
            // don't replace an existing name, first one wins!
            if((n == null) || (n.length() < 1) || (n.trim().length() < 1))
            {
              logger.severe("illegal filter name");
              break;
            }
            filterName =n.trim();
            logger.info("Filter Name is: (" + filterName + ")");
            break;
            
          case '*':
            // change list type
            if(str.indexOf("White") > -1)
              isWhiteListType = true;
            if(str.indexOf("Black") > -1)
              isWhiteListType = false;
            break;
            
          case '@':
            // is a name of a file??
            fname = str.substring(1);
            // don't allow direct recursion (can't stop circular recursion)
            if(fName.equalsIgnoreCase(fname) || fileName.equalsIgnoreCase(fname))
            {
              logger.severe("file recursion not allowed");
              break;
            }
            WhiteAndBlackListFilter ff = new WhiteAndBlackListFilter(fname);
            if(ff != null)
            {
              // add this filter to this one
              WL.addAll(ff.getWhiteList());
              BL.addAll(ff.getBlackList());
              FL.addAll(ff.getFileList());
              
              // If I don't already have a filter name, use from file
              if((filterName == null) || (filterName.length() < 1))
                filterName = ff.getFilterName();
            }
             break;
            
          case '#':
            // do nothing with comments
            logger.fine(str);
            break;
            
          default:
            // put this in one of the two lists
            if(isWhiteListType)
              WL.add(str);
            else
              BL.add(str);
            break;
         }
      }
     }
    br.close(  ) ;
    
    // make sure the filter has a name
    if((filterName == null) || (filterName.length() < 1))
      filterName = fName;
    
    return new WhiteAndBlackListFilter(filterName, WL, BL, FL);
   }
  
  public WhiteAndBlackListFilter addWhiteList(ArrayList<String> list)
  {
    if(list != null)
      WhiteList.addAll(list);
    return this;
  }
  
  public WhiteAndBlackListFilter addBlackList(ArrayList<String> list)
  {
    if(list != null)
      BlackList.addAll(list);
    return this;
  }
  
  public WhiteAndBlackListFilter addFileList(ArrayList<String> list)
  {
    if(list != null)
      FileList.addAll(list);
    return this;
  }
  
  public WhiteAndBlackListFilter addFilter(WhiteAndBlackListFilter filter)
  {
    if(filter != null)
    {
      addWhiteList(filter.getWhiteList());
      addBlackList(filter.getBlackList());
      addFileList(filter.getFileList());
      
      // If I don't already have a filter name, use from file
      if((FilterName == null) || (FilterName.length() < 1))
        FilterName = filter.getFilterName();
    }
    return this;
  }
  
//  protected static void writeStrings(String fileName, java.util.ArrayList<String> stringList) throws IOException
//  {
//    if((stringList == null) || (stringList.size() == 0))
//      return;
//    
//    String fName = convertSpecialFileName(fileName);
//    File outFile = new File(fName);
//    
//      if (!outFile.exists()) 
//      {
//        outFile.createNewFile();
//      }
//      PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outFile)));
//      
//      // iterate through the list
//      for(String string: stringList)
//      {
//        out.println(string);
//      }
//      out.close();
//    return;
//  }

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
   * @throws IOException 
   ***********************************************************/
  public static void main(String[] args) throws IOException
  {
    if((args != null) && (args.length > 0))
    {
      System.out.println("This is argument 0: " + args[0]);
      WhiteAndBlackListFilter filter = new WhiteAndBlackListFilter(args[0]);
      
      System.out.println("The white list is: " + filter.getWhiteList().size());
      System.out.println("The black list is: " + filter.getBlackList().size());
      System.out.println("The file list is: " + filter.getFileList().size());
      System.out.println("\nThe filter name is: " + filter.getFilterName());
      System.out.println("The filter ID is: " + filter.getFilterID().toString());
    }
  }

}
