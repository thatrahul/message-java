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
 * A JSON user ID payload.
 */
public class UserId extends JSONifiable {
  @SerializedName("userId")
  protected String mUserId;

  /**
   * @hide
   * Default constructor with a user ID.
   * @param userId A user ID without app ID.
   */
  public UserId(String userId) {
    mUserId = userId;
  }
  
  /**
   * Get the user ID.
   * @return A device ID.
   */
  public String getUserId() {
    return mUserId;
  }

  public static UserId fromJson(String json) {
    return GsonData.getGson().fromJson(json, UserId.class);
  }
}
