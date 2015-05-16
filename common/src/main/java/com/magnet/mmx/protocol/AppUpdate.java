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
 * The request for updating an application.
 * The response is MMXStatus.
 * 
 */
public class AppUpdate extends JSONifiable {
  @SerializedName("appId")
  private String mAppId;
  @SerializedName("appName")
  private String mAppName;
  @SerializedName("gcm")
  private GCM mGcm;
  @SerializedName("apns")
  private APNS mApns;
  @SerializedName("modificationDate")
  private String mModificationDate;

  public String getAppId() {
    return mAppId;
  }

  public void setAppId(String appId) {
    mAppId = appId;
  }

  public String getAppName() {
    return mAppName;
  }

  public void setAppName(String appName) {
    mAppName = appName;
  }

  public GCM getGcm() {
    return mGcm;
  }

  public void setGcm(GCM gcm) {
    mGcm = gcm;
  }

  public APNS getApns() {
    return mApns;
  }

  public void setApns(APNS apns) {
    mApns = apns;
  }

  public void setModificationDate(String date) {
    mModificationDate = date;
  }

  public String getModificationDate() {
    return mModificationDate;
  }

  public static AppUpdate fromJson(String json) {
    return GsonData.getGson().fromJson(json, AppUpdate.class);
  }
}
