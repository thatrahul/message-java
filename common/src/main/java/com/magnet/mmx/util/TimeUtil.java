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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EmptyStackException;
import java.util.Stack;
import java.util.TimeZone;

/**
 * Utility to convert between Date to ISO-8601 UTC Date/Time for XMPP.  Since
 * the time formatter is not thread safe, a pool of formatter objects is used
 * instead of using synchronized block for performance reason.  The pool has no
 * upper limit because the execution time is short, but it may have many
 * concurrent uses.  In the client side, it is likely to be one object per
 * connection.  In the server side, the number of objects is higher, but it is
 * bounded by the number of concurrent requests/responses among the connected
 * sessions.
 */
public class TimeUtil {
  private static Stack<SimpleDateFormat> sPool;
  
  static {
    sPool = new Stack<SimpleDateFormat>();
    sPool.push(newInstance());
  }
  
  private static SimpleDateFormat newInstance() {
    SimpleDateFormat fmtr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    fmtr.setTimeZone(TimeZone.getTimeZone("UTC"));
    return fmtr;
  }

  /**
   * Convert ISO-8601 UTC Date/Time to Date.
   * @param isoDate YYYY-MM-DD'T'hh:mm:ss.SSS'Z'
   * @return
   */
  public static Date toDate(String isoDate) {
    SimpleDateFormat fmtr;
    try {
      fmtr = sPool.pop();
    } catch (EmptyStackException e) {
      fmtr = newInstance();
    }
    try {
      return fmtr.parse(isoDate);
    } catch (Throwable e) {
      //    System.err.println("TimeUtil.toDate() failed: "+isoDate);
      //    e.printStackTrace();
      // Invalid ISO date format.
      return null;
    } finally {
      sPool.push(fmtr);
    }
  }
  
  /**
   * Convert Date to ISO-8601 UTC Date/Time with milliseconds.
   * @param date
   * @return YYYY-MM-DD'T'hh:mm:ss.SSS'Z'
   */
  public static String toString(Date date) {
    SimpleDateFormat fmtr;
    try {
      fmtr = sPool.pop();
    } catch (EmptyStackException e) {
      fmtr = newInstance();
    }
    String isoDate = fmtr.format(date);
    sPool.push(fmtr);
    return isoDate;
  }
}
