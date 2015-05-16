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

package com.magnet.mmx.protocol;

import com.google.gson.annotations.SerializedName;
import com.magnet.mmx.util.GsonData;
import com.magnet.mmx.util.JSONifiable;

/**
 * JSON payload for APNS.  The certificate in .p12 file format is base64 
 * encoded which is more compact than byte array in JSON.
 */
public class APNS extends JSONifiable {
  @SerializedName("apnsCert")
  private String mApnsCert;        // base64-encoded
  @SerializedName("apnsPwd")
  private String mApnsPwd;

  /**
   * Constructor with APNS certificate and its password.
   * @param apnsCert Base64 encoded .p12 file format.
   * @param apnsPwd The password for the .p12 file.
   */
  public APNS(String apnsCert, String apnsPwd) {
    mApnsCert = apnsCert;
    mApnsPwd = apnsPwd;
  }
  
  /**
   * Get the base64-encoded certificate.
   * @return
   */
  public String getCert() {
    return mApnsCert;
  }
  
  /**
   * The APNS certificate in .p12 file format encoded in base64.
   * @param apnsCert Base64 encoded certificate.
   */
  public APNS setCert(String apnsCert) {
    this.mApnsCert = apnsCert;
    return this;
  }

  public String getPwd() {
    return mApnsPwd;
  }

  public APNS setPwd(String pwd) {
    mApnsPwd = pwd;
    return this;
  }

  public static APNS fromJson(String json) {
    return GsonData.getGson().fromJson(json, APNS.class);
  }
}
