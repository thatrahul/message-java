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

public class GCM extends JSONifiable {
  @SerializedName("googleApiKey")
  private String mGoogleApiKey;
  @SerializedName("googleProjectId")
  private String mGoogleProjectId; // aka Sender ID

  /**
   * Constructor with project ID and api key.
   * @param projectId Google project ID (aka Sender ID in Android)
   * @param apiKey Google api key
   */
  public GCM(String projectId, String apiKey) {
    mGoogleProjectId = projectId;
    mGoogleApiKey = apiKey;
  }
  public String getGoogleApiKey() {
    return mGoogleApiKey;
  }
  public GCM setGoogleApiKey(String googleApiKey) {
    this.mGoogleApiKey = googleApiKey;
    return this;
  }
  public String getGoogleProjectId() {
    return mGoogleProjectId;
  }
  public GCM setGoogleProjectId(String googleProjectId) {
    this.mGoogleProjectId = googleProjectId;
    return this;
  }

  public static GCM fromJson(String json) {
    return GsonData.getGson().fromJson(json, GCM.class);
  }
}
