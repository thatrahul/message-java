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

import java.util.Map;

import com.google.gson.annotations.SerializedName;
import com.magnet.mmx.util.GsonData;
import com.magnet.mmx.util.JSONifiable;

/**
 * @hide
 * Payload for password reset request.  It is for internal use only.  The 
 * required fields are apiKey and userId.  Currently anyone can reset other
 * user's password.  In the future, the secret question/answer will be used to
 * validate initiator.
 */
public class UserReset extends JSONifiable {
  @SerializedName("priKey")
  private String mPriKey;
  @SerializedName("apiKey")
  private String mApiKey;
  @SerializedName("userId")
  private String mUserId;
  @SerializedName("extras")
  private Map<String, String> mExtras;
  
  public String getPriKey() {
    return mPriKey;
  }
  
  /**
   * Specify the privilege key for reset password.
   * @param priKey
   * @return
   */
  public UserReset setPriKey(String priKey) {
    mPriKey = priKey;
    return this;
  }
  
  public String getApiKey() {
    return mApiKey;
  }
  
  /**
   * Use the authorized api key.
   * @param apiKey
   */
  public UserReset setApiKey(String apiKey) {
    mApiKey = apiKey;
    return this;
  }
  
  public String getUserId() {
    return mUserId;
  }
  
  /**
   * The user ID without appId.
   * @param userId The user ID.
   */
  public UserReset setUserId(String userId) {
    mUserId = userId;
    return this;
  }
  
  /**
   * Get the extra properties.
   * @return
   */
  public Map<String, String> getExtras() {
    return mExtras;
  }
  
  /**
   * Optional extra properties.  For example, secret questions and answers.
   * @param extras The extra properties.
   */
  public UserReset setExtras(Map<String, String> extras) {
    mExtras = extras;
    return this;
  }
  
  public static UserReset fromJson(String json) {
    return GsonData.getGson().fromJson(json, UserReset.class);
  }
}
