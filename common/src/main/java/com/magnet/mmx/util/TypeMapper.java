/*   Copyright (c) 2015 Magnet Systems, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.magnet.mmx.util;

import java.util.HashMap;
import java.util.Map;

/**
 * This abstract class maps a named type string to a class.  This utility
 * class can be used to map a message payload type to a class.  The application
 * developer creates a derived class and implements the 
 * {@link #getClassNameByType(String)} which maps a type string to a class name.
 * The {@link #getClassByType(String)} returns the class for the type string.
 * <p>
 * In the future, a tool can generate an implementation of 
 * {@link #getClassNameByType(String)} using an annotation in the payload
 * classes.
 *
 */
public abstract class TypeMapper {
  private static Map<String, Class<?>> sClassMap = new HashMap<String, Class<?>>();
  
  /**
   * Map a type to a class name.
   * @param type A user defined type string.
   * @return null if the type is not mappable, or a Java class name.
   */
  protected abstract String getClassNameByType(String type);
  
  /**
   * Map a type to a class.
   * @param type A user defined type string.
   * @return null if tye type is not mappable, or a Java Class.
   */
  public Class<?> getClassByType(String type) {
    Class<?> clz = sClassMap.get(type);
    if (clz == null) {
      synchronized(sClassMap) {
        if ((clz = sClassMap.get(type)) == null) {
          String clzName = getClassNameByType(type);
          if (clzName != null) {
            try {
              clz = Class.forName(clzName);
              sClassMap.put(type, clz);
            } catch (Throwable e) {
              return null;
            }
          }
        }
      }
    }
    return clz;
  }
  
}
