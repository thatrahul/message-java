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

import com.magnet.mmx.protocol.APNS;
import com.magnet.mmx.protocol.AppCreate;
import com.magnet.mmx.protocol.AppDelete;
import com.magnet.mmx.protocol.AppRead;
import com.magnet.mmx.protocol.AppUpdate;
import com.magnet.mmx.protocol.Constants;
import com.magnet.mmx.protocol.GCM;
import com.magnet.mmx.protocol.MyAppsRead;

import java.util.Random;
import java.util.UUID;

public class AppHelper {
  public static final String COMMAND_CREATE = "create";
  public static final String COMMAND_READ = "read";
  public static final String COMMAND_READ_MINE = "readMine";
  public static final String COMMAND_UPDATE = "update";
  public static final String COMMAND_DELETE = "delete";
  private static Random sRandom = new Random();

  /**
   * 
   * @param command
   * @param appName
   * @param appId
   * @param apiKey
   * @param serverUser
   * @param serverKey
   * @param guestUser This parameter is deprecated.
   * @param guestKey This parameter is deprecated.
   * @param googleApiKey
   * @param googleProjectId
   * @param apnsCert
   * @param apnsPwd
   * @return
   */
  public static String createRequest(String command, String appName, String appId,
                                     String apiKey, String serverUser, String serverKey,
                                     String guestUser, String guestKey,
                                     String googleApiKey, String googleProjectId,
                                     String apnsCert, String apnsPwd) {
    String elementText = null;
    if (command.equalsIgnoreCase(COMMAND_CREATE) && appName != null) {
      AppCreate.Request request = new AppCreate.Request();
      request.setAppName(appName);
      request.setServerUserId(serverUser);
      request.setServerUserKey(serverKey);
      GCM gcm = new GCM(googleProjectId, googleApiKey);
      request.setGcm(gcm);
      APNS apns = new APNS(apnsCert, apnsPwd);
      request.setApns(apns);
      elementText = request.toJson();
    } else if (command.equalsIgnoreCase(COMMAND_UPDATE) && appId != null) {
      AppUpdate request = new AppUpdate();
      request.setAppName(appName);
      GCM gcm = new GCM(googleProjectId, googleApiKey);
      request.setGcm(gcm);
      APNS apns = new APNS(apnsCert, apnsPwd);
      request.setApns(apns);
      elementText = request.toJson();
    } else if (command.equalsIgnoreCase(COMMAND_DELETE) && appId != null) {
      AppDelete.Request request = new AppDelete.Request();
      request.setAppId(appId);
      elementText = request.toJson();
    } else if (command.equalsIgnoreCase(COMMAND_READ) && appId != null) {
      AppRead.Request request = new AppRead.Request();
      request.setAppId(appId);
      elementText = request.toJson();
    } else if (command.equalsIgnoreCase(COMMAND_READ_MINE)) {
      MyAppsRead.Request request = new MyAppsRead.Request();
      elementText = request.toJson();
    }
    return elementText;
  }

  /**
   * Generate a user ID (with appId) with an optional prefix.
   * @param prefix null or a prefix.
   * @param userId A non-null user ID.
   * @param appId null if userID is shared from LDAP, or an app ID.
   * @return
   * @throws IllegalArgumentException
   */
  public static String generateUser(String prefix, String userId, String appId) 
                                      throws IllegalArgumentException {
    StringBuilder sb = new StringBuilder();
    if (prefix != null) {
      sb.append(prefix);
    }
    sb.append(normalizeUserId(userId));
    if (appId != null) {
      sb.append(Constants.APP_ID_DELIMITER)
        .append(appId);
    }
    if (sb.length() >= 64) {
      throw new IllegalArgumentException("user name "+sb.toString()+" too long.");
    }
    return sb.toString();
  }

  public static String generateApiKey() {
    return UUID.randomUUID().toString();
  }
  
  /**
   * Generate a random 8-bytes password encoded in Base36.
   * @return
   */
  public static String generateRandomKey() {
    return Long.toString(sRandom.nextLong(), 36);
  }

  /**
   * Generate a positive random 8-bytes password encoded in Base36.
   * @return
   */
  public static String generateRandomPositiveKey() {
    return Long.toString(Math.abs(sRandom.nextLong()), 36);
  }

  public static boolean validateAppName(String name) {
    // verifies the appName is valid
    boolean result = (name != null && name.length() > 0 && name.length() < 100);
    return result;
  }

  private static String normalizeUserId(String userId) {
    char[] ca = userId.toCharArray();
    StringBuilder sb = new StringBuilder(ca.length);
    for (char c : ca) {
      if (Character.isLetterOrDigit(c)) {
        sb.append(Character.isUpperCase(c) ? Character.toLowerCase(c) : c);
      } else if ((c == '.' || c == '_' || c == '-')) {
        sb.append(c);
      }
    }
    return sb.toString();
  }
}
