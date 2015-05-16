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
 * @hide
 * The custom request to authenticate a user by an app server.
 */
public class AuthData extends JSONifiable {
  @SerializedName("apiKey")
  private String mApiKey;
  @SerializedName("userId")
  private String mUserId;
  @SerializedName("authToken")
  private String mAuthToken;
  @SerializedName("password")
  private String mPassword;
  
  public String getApiKey() {
    return mApiKey;
  }
  
  public AuthData setApiKey(String apiKey) {
    mApiKey = apiKey;
    return this;
  }
  
  public String getUserId() {
    return mUserId;
  }
  
  public AuthData setUserId(String userId) {
    mUserId = userId;
    return this;
  }
  
  public String getAuthToken() {
    return mAuthToken;
  }
  
  public AuthData setAuthToken(String authToken) {
    mAuthToken = authToken;
    return this;
  }
  
  public String getPassword() {
    return mPassword;
  }
  
  /**
   * Specify the new password.
   * @param password The password should be encrypted with a key known only
   *                between the authenticator and the client.
   */
  public AuthData setPassword(String password) {
    mPassword = password;
    return this;
  }
  
  public static AuthData fromJson(String json) {
    return GsonData.getGson().fromJson(json, AuthData.class);
  }
}
