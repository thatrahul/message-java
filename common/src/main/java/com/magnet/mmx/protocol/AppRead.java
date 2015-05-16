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

public class AppRead extends JSONifiable {

  /**
   * {
   * "appId": "...",
   * }
   *
   * @author hrana
   */
  public static class Request extends JSONifiable {
    @SerializedName("appId")
    private String mAppId;

    public String getAppId() {
      return mAppId;
    }

    public void setAppId(String appId) {
      mAppId = appId;
    }

    public static Request fromJson(String json) {
      return GsonData.getGson().fromJson(json, Request.class);
    }
  }

  /**
   * {
   * "appName": "...",
   * "apiKey": "...",
   * "authByAppServer": true|false,
   * "serverUser": "...",
   * "gcm":
   * {
   * "googleApiKey" : "...",
   * "googleProjectId" : "..."
   * },
   * "apns":
   * {
   * "pwd" : "..."
   * }
   * }
   */
  public static class Response extends JSONifiable {
    @SerializedName("appName")
    private String mAppName;
    @SerializedName("appId")
    private String mAppId;
    @SerializedName("apiKey")
    private String mApiKey;
    @SerializedName("authByAppServer")
    private boolean mAuthByAppServer;
    @SerializedName("serverUserId")
    private String mServerUserId;
    @SerializedName("serverKey")
    private String mServerKey;
    @SerializedName("serverSecret")
    private String mServerSecret;
    @SerializedName("guestUserId")
    private String mGuestUserId;
    @SerializedName("guestUserSecret")
    private String mGuestUserSecret;
    @SerializedName("gcm")
    private GCM mGcm;
    @SerializedName("apns")
    private APNS mApns;
    @SerializedName("creationDate")
    private String mCreationDate;
    @SerializedName("modificationDate")
    private String mModificationDate;
    @SerializedName("ownerId")
    private String mOwnerId;

    public GCM getGcm() {
      return mGcm;
    }

    public APNS getApns() {
      return mApns;
    }

    public String getAppName() {
      return mAppName;
    }

    public String getAppId() {
      return mAppId;
    }

    public String getApiKey() {
      return mApiKey;
    }

    public String getServerUserId() {
      return mServerUserId;
    }

    public String getCreationDate() {
      return mCreationDate;
    }

    public String getModificationDate() {
      return mModificationDate;
    }

    public boolean isAuthByAppServer() {
      return mAuthByAppServer;
    }

    public void setAuthByAppServer(boolean authByAppServer) {
      mAuthByAppServer = authByAppServer;
    }

    public String getServerKey() {
      return mServerKey;
    }

    public String getGuestUserSecret() {
      return mGuestUserSecret;
    }

    public void setGuestUserSecret(String guestUserSecret) {
      mGuestUserSecret = guestUserSecret;
    }

    public String getGuestUserId() {
      return mGuestUserId;
    }

    public void setGuestUserId(String guestUserId) {
      mGuestUserId = guestUserId;
    }

    public void setServerKey(String serverKey) {
      mServerKey = serverKey;
    }

    public String getServerSecret() {
      return mServerSecret;
    }

    public void setServerSecret(String serverSecret) {
      mServerSecret = serverSecret;
    }

    public String getOwnerId() {
      return mOwnerId;
    }

    public void setOwnerId(String ownerId) {
      mOwnerId = ownerId;
    }

    public void setAppName(String appName) {
      mAppName = appName;
    }

    public void setApiKey(String apiKey) {
      mApiKey = apiKey;
    }

    public void setAppId(String appId) {
      mAppId = appId;
    }

    public void setServerUserId(String serverUserId) {
      mServerUserId = serverUserId;
    }

    public void setGcm(GCM gcm) {
      mGcm = gcm;
    }

    public void setApns(APNS apns) {
      mApns = apns;
    }

    public void setCreationDate(String creationDate) {
      mCreationDate = creationDate;
    }

    public void setModificationDate(String modificationDate) {
      mModificationDate = modificationDate;
    }

    public static Response fromJson(String json) {
      return GsonData.getGson().fromJson(json, Response.class);
    }
  }
}
