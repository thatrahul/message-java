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

package com.magnet.mmx.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Class for generating a unique device ID based on hardware or a combination of hardware and the app package name.
 * Requires the Android permission <code>READ_PHONE_STATE</code>.
 *
 */
class DeviceIdGenerator {
  private static final String SHARED_PREF_FILENAME = "com.magnet.mmx.device";
  private static final String KEY_DEVICE_ID = "DEVICE_ID";

  static AtomicReference<String> uniqueIdRef = new AtomicReference<String>(null);

  /**
   * Retrieves a unique ID based on the hardware ID of this device from the Android TelephonyManager.
   * This method randomly generates a new ID if the hardware ID is not available.
   * @param context The Android application context.
   * @return A unique device ID.
   */
  public static synchronized String getUniqueDeviceId(Context context) {
    String result = uniqueIdRef.get();
    if (result==null) {
      result = getOrCreateUniqueId(context);
    }
    return result;
  }

  /**
   * Retrieves a unique ID based on the hardware ID of this device and the application package name.
   * This method generates a new ID if hardware ID is not available.
   * @param context The Android application context.
   * @return A unique app ID.
   */
  public static synchronized String getUniqueIdForApp(Context context) {
    return getOrCreateUniqueIdForApp(context);
  }

  private static String getOrCreateUniqueId(Context context) {
    if (null==context) {
      return null;
    }

    SharedPreferences shared =
        context.getApplicationContext().
            getSharedPreferences(SHARED_PREF_FILENAME, Context.MODE_PRIVATE);

    String result = shared.getString(KEY_DEVICE_ID, null);
    if (TextUtils.isEmpty(result)) {
      String devId = getHardwareDeviceId(context);
      // emulator or rogue prototype devices that don't have dev ID
      if (isAndroidEmulator() ||
          TextUtils.isEmpty(devId) ) {
        UUID uuid = UUID.randomUUID();
        result = Long.toString(uuid.getMostSignificantBits(), 36) +
                 Long.toString(uuid.getLeastSignificantBits(), 36);
      } else {
        try {
          MessageDigest md = MessageDigest.getInstance("SHA-1");
          byte[] hash = md.digest(devId.getBytes());
          result = toBaseNString(hash, 36);
        } catch (NoSuchAlgorithmException e) {
          // convert to base36 string
          result = toBaseNString(devId.getBytes(), 36);
        }
      }
      // save it to shared preferences
      shared.edit()
          .putString(KEY_DEVICE_ID, result)
          .commit();
    }
    uniqueIdRef.set(result);
    return result;
  }

  private static String toBaseNString(byte[] bytes, int base) {
    //pad the byte array
    int remaining = bytes.length % 8;
    byte[] paddedArray = bytes;
    if (remaining != 0) {
      paddedArray = new byte[bytes.length + (8 - remaining)];
      for (int i=0;i<bytes.length;i++) {
        paddedArray[i] = bytes[i];
      }
    }

    ByteBuffer bb = ByteBuffer.wrap(paddedArray);
    StringBuffer result = new StringBuffer();
    long curLong;
    while (bb.position() != bb.limit()) {
      curLong = bb.getLong();
      result.append(Long.toString(curLong & Long.MAX_VALUE, base));
    }
    return result.toString();
  }

  private static String getOrCreateUniqueIdForApp(Context context) {
    if (null==context) {
      return null;
    }

    String deviceId = getOrCreateUniqueId(context);
    String appName = context.getPackageName();
    return (deviceId + "_" + appName);
  }

  /**
   * Get unique Phone hardware id
   * @return Build.SERIAL + TelephonyManager.getDeviceId, or null.
   * @throws IllegalStateException Unable to get hardware device ID because...
   */
  private static String getHardwareDeviceId(Context context) 
                                              throws IllegalStateException {
    StringBuilder result = new StringBuilder();

    if (!TextUtils.isEmpty(Build.SERIAL)) {
      result.append(Build.SERIAL).append("+");
    }

    try {
      TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
      String devId = tm.getDeviceId();
      if (TextUtils.isEmpty(devId)) {
        return null;
      }
      result.append(devId);
      return result.toString();
    } catch (Exception e) {
      throw new IllegalStateException("Unable to get hardware device ID", e);
    }
  }

  /**
   * Return true if running in emulator
   */
  private static boolean isAndroidEmulator() {
    return Build.FINGERPRINT.contains("generic");
  }

  private static String toHexStr(byte[] bytes) {
    String result = "";
    final String hex_digits = "0123456789ABCDEF";
    for (int i = 0; i < bytes.length; i++) {
      result += hex_digits.charAt((bytes[i] >> 4) & 0xf);
      result += hex_digits.charAt(bytes[i] & 0xf);
    }
    return result;
  }


}
