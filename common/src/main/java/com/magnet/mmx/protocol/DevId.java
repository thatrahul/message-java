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
 * A JSON device ID payload.
 */
public class DevId extends JSONifiable {
  @SerializedName("devId")
  protected String mDevId;

  /**
   * @hide
   * Default constructor with a device ID.
   * @param devId
   */
  public DevId(String devId) {
    mDevId = devId;
  }
  
  /**
   * Get the device ID.
   * @return A device ID.
   */
  public String getDevId() {
    return mDevId;
  }

  public static DevId fromJson(String json) {
    return GsonData.getGson().fromJson(json, DevId.class);
  }
}
