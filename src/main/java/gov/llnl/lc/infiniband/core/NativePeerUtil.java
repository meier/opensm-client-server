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
 *        file: NativePeerUtil.java
 *
 *  Created on: Oct 28, 2014
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.core;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

/**********************************************************************
 * A <code>NativePeerUtil</code> is used as a tool to determine which classes
 * are native peer classes, and what version they support.
 * <p>
 * 
 * @see NativePeerClass
 *
 * @author meier3
 * 
 * @version Oct 28, 2014 10:41:49 AM
 **********************************************************************/
public class NativePeerUtil
{

  /************************************************************
   * Method Name: getClassNameFromEntryName
   **/
  /**
   * Converts the zip entry class name found within a JAR file, into
   * a normal class name.  This usually involves converting the
   * path seperator to dots, and trimming off the trailing .class
   *
   * @param eName the entry name
   * @return a string representing this entry's class name
   ***********************************************************/
  public static String getClassNameFromEntryName(String eName)
  {
    // strip off the trailing .class
    int end = eName.indexOf(".class");
    String name = eName.replace('/', '.');
    return name.substring(0, end);
  }

  /************************************************************
   * Method Name:
   *  getJarsInClassPath
  **/
  /**
   * Obtains and returns a list of JAR files found in the class path.
   *
    * @return  an ArrayList containing the names of the jar files
   *          found in the class path
   * @throws Exception
   ***********************************************************/
  public static ArrayList<String> getJarsInClassPath() throws Exception
  {
    // return the native peer classes within the jar file
    ArrayList<String> strings = new ArrayList<String>();

    for (String classpathEntry : System.getProperty("java.class.path").split(
        System.getProperty("path.separator")))
    {
      if (classpathEntry.endsWith(".jar"))
      {
        strings.add(classpathEntry);
      }
    }
    return strings;
  }

  /************************************************************
   * Method Name:
   *  getLoadedClasses
  **/
  /**
   * Obtains and returns the list of classes currently loaded by
   * the class loader.
   *
   * @return a list of the names of the classes that have been
   *         loaded.
   * @throws Exception
   ***********************************************************/
  public static ArrayList<String> getLoadedClasses() throws Exception
  {
    // return the native peer classes within the jar file
    ArrayList<String> strings = new ArrayList<String>();

    Reflections reflections = new Reflections("gov.llnl.lc", new SubTypesScanner(false));
    Set<String> allClasses = reflections.getStore().getSubTypesOf(Object.class.getName());
    strings.addAll(allClasses);
    return strings;
  }
  
  protected static String getAnnotationValue(Class<?> classObj)
  {
    if(classObj != null)
    {
      Annotation[] annotations = classObj.getAnnotations();
      if (annotations.length > 0)
        for (Annotation annotation : annotations)
        {
          if(annotation instanceof NativePeerClass)
          {
            NativePeerClass nativeAnnotation = (NativePeerClass) annotation;
            return nativeAnnotation.value();
          }
        }
    }
    return null;
  }
  
  public static String getNativePeerName(Class<?> classObj, boolean canonical)
  {
    if(canonical)
      return classObj.getCanonicalName();
    return classObj.getSimpleName();
  }
  
  public static String getNativePeerVersion(Class<?> classObj)
  {
    return getAnnotationValue(classObj);
  }
  
  public static String getNativePeerString(Class<?> classObj, boolean canonical)
  {
    return getNativePeerName(classObj, canonical) + ": has version " + getNativePeerVersion(classObj);
  }
  
  /************************************************************
   * Method Name:
   *  getLoadedClasses
  **/
  /**
   * Obtains and returns the list of classes currently loaded by
   * the class loader.
   *
   * @return a list of the names of the classes that have been
   *         loaded.
   * @throws Exception
   ***********************************************************/
  public static ArrayList<Class<?>> getNativePeersInMemory()
  {
    try
    {
      return getNativePeersFromClassList(getLoadedClasses());
    }
    catch (Exception e)
    {
    }
    return new ArrayList<Class<?>>();
  }



  /************************************************************
   * Method Name:
   *  getNativePeersFromClassList
  **/
  /**
   * Given a list of class names, returns a subset list of the ones
   * that are tagged as NativePeerClasses
   *
   * @see     #NativePeerClass
   *
   * @param allClasses - a list of names to be tested to see if
   *                     they are peer classes
   * @return            - a list of names that are native peer classes
   ***********************************************************/
  public static ArrayList<Class<?>> getNativePeersFromClassList(ArrayList<String> allClasses)
  {
    // return the native peer classes within the jar file
    ArrayList<Class<?>> strings = new ArrayList<Class<?>>();

    for (String className : allClasses)
    {
      try
      {
        Class<?> classObj = Class.forName(className);
        
        Annotation[] annotations = classObj.getAnnotations();
        if (annotations.length > 0)
          for (Annotation annotation : annotations)
          {
            if(annotation instanceof NativePeerClass)
              strings.add(classObj);
          }
      }
      catch (Exception e)
      {
      }
    }
    return strings;
  }

  /************************************************************
   * Method Name:
   *  getNativePeersFromJarFiles
  **/
  /**
   * Looks through all the jar files, and builds a list of class
   * names that are tagged as native peers.
   *
   * @see     #getJarsInClassPath()
   * @see     #getNativePeersFromJarFile(File)
   *
   * @return
   * @throws Exception
   ***********************************************************/
  public static ArrayList<Class<?>> getNativePeersFromJarFiles() throws Exception
  {
    // return the native peer classes within the jar files
    ArrayList<Class<?>> allPeers = new ArrayList<Class<?>>();
    ArrayList<String> fNames = getJarsInClassPath();

    for (String jarFileName : fNames)
    {
      File jar = new File(jarFileName);

      ArrayList<Class<?>> strings = getNativePeersFromJarFile(jar);
      allPeers.addAll(strings);
    }
    return allPeers;
  }

  /************************************************************
   * Method Name:
   *  getAllNativePeers
  **/
  /**
   * Searches for native peer classes that are loaded and in
   * jar files, and returns a complete list.
   *
   * @see     #getNativePeersInMemory()
   * @see     #getNativePeersFromJarFiles()
   *
   * @return
   * @throws Exception
   ***********************************************************/
  public static ArrayList<Class<?>> getAllNativePeers() throws Exception
  {
    // return the native peer classes from all sources
    ArrayList<Class<?>> allPeers = getNativePeersInMemory();
    ArrayList<Class<?>> nStrings = getNativePeersFromJarFiles();
    allPeers.addAll(nStrings);
    return allPeers;
  }

  /************************************************************
   * Method Name:
   *  getNativePeersFromJarFile
  **/
  /**
   * Returns a list (if any) of all the native peer classes
   * contained within the supplied jar file.
   *
   * @see     #getClassNameFromEntryName
   *
   * @param jar  - the name of the jar file
   * @return
   * @throws Exception
   ***********************************************************/
  public static ArrayList<Class<?>> getNativePeersFromJarFile(File jar) throws Exception
  {
    // return the native peer classes within the jar file
    ArrayList<Class<?>> strings = new ArrayList<Class<?>>();

    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(jar));
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    int count = 0;
    byte[] buffer = new byte[8182]; // some large number - pick one

    while ((count = bis.read(buffer)) > 0)
    {
      baos.write(buffer, 0, count);
    }
    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    bis.close();

    JarInputStream is = new JarInputStream(bais);

    JarEntry entry;
    while ((entry = is.getNextJarEntry()) != null)
    {
      if (entry.getName().endsWith(".class"))
      {
        if (entry.getName().startsWith("gov/llnl/lc/infiniband"))
        {
          // check to see if this is a native peer class
          try
          {
            Class<?> classObj = Class.forName(getClassNameFromEntryName(entry.getName()));

            Annotation[] annotations = classObj.getAnnotations();
            if (annotations.length > 0)
              for (Annotation annotation : annotations)
              {
                if(annotation instanceof NativePeerClass)
                  strings.add(classObj);
              }
          }
          catch (Exception e)
          {
          }
        }
      }
    }
    is.close();
    return strings;
  }

  /************************************************************
   * Method Name:
   *  main
  **/
  /**
   * Prints out the list of all of the native peer class names,
   * along with version numbers, that it can find in memory
   * or in jar files found in the class path. 
   *
   * @see     #getAllNativePeers()
   *
   * @param args
   * @throws Exception
   ***********************************************************/
  public static void main(String[] args)
  {
    ArrayList<Class<?>> nStrings = new ArrayList<Class<?>>();
    try
    {
      nStrings = getAllNativePeers();
    }
    catch (Exception e)
    {
    }

    for (Class<?> peer : nStrings)
    {
      System.out.println(getNativePeerString(peer, false));
    }
  }

}
