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

/**
 * The request for device and the optional push registration.
 * 
 * The response is MMXStatus.
 *
 */
public class DevReg extends DeviceInfo {
  @SerializedName("apiKey")
  private String mApiKey;

  public String getApiKey() {
    return mApiKey;
  }

  public DevReg setApiKey(String apiKey) {
    mApiKey = apiKey;
    return this;
  }

  public static DevReg fromJson(String json) {
    return GsonData.getGson().fromJson(json, DevReg.class);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder()
      .append("apiKey='").append(mApiKey)
      .append("', ").append(super.toString());
    return sb.toString();
  }
}
