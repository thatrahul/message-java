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
import com.magnet.mmx.protocol.Constants.UserCreateMode;
import com.magnet.mmx.util.GsonData;
import com.magnet.mmx.util.JSONifiable;
import com.magnet.mmx.util.TagUtil;

import java.util.List;
import java.util.Map;

/**
 * @hide
 * The request for the user/account creation.
 * The response is MMXStatus.
 */
public class UserCreate extends JSONifiable {
  @SerializedName("priKey")
  private String mPriKey;
  @SerializedName("apiKey")
  private String mApiKey;
  @SerializedName("appId")
  private String mAppId;
  @SerializedName("userId")
  private String mUserId;
  @SerializedName("password")
  private String mPassword;
  @SerializedName("email")
  private String mEmail;
  @SerializedName("displayName")
  private String mDisplayName;
  @SerializedName("createMode")
  private UserCreateMode mCreateMode;
  @SerializedName("extras")
  private Map<String, String> mExtras;
  @SerializedName("tags")
  private List<String> mTags;

  /**
   * Get the privilege key.
   * @return The privilege key.
   */
  public String getPriKey() {
    return mPriKey;
  }
  
  /**
   * Specify the privilege key to create user account.
   * @param priKey The privilege key.
   * @return This object.
   */
  public UserCreate setPriKey(String priKey) {
    mPriKey = priKey;
    return this;
  }
  
  /**
   * Get the authorized api key.
   * @return The API key.
   */
  public String getApiKey() {
    return mApiKey;
  }
  
  /**
   * Specify the authorized api key.
   * @param apiKey The API key.
   * @return This object.
   */
  public UserCreate setApiKey(String apiKey) {
    mApiKey = apiKey;
    return this;
  }
  
  /**
   * Get the app ID.
   * @return
   */
  public String getAppId() {
    return mAppId;
  }

  /**
   * Specify the app ID to be validated.
   * @param appId The app ID.
   */
  public UserCreate setAppId(String appId) {
    mAppId = appId;
    return this;
  }

  /**
   * Get the user ID.
   * @return The user ID.
   */
  public String getUserId() {
    return mUserId;
  }
  
  /**
   * Specify the user ID.  A user ID cannot be null or empty.
   * @param userId The user ID.
   * @return This object.
   * @throws IllegalArgumentException
   */
  public UserCreate setUserId(String userId) {
    if ((mUserId = userId) == null || mUserId.isEmpty()) {
      throw new IllegalArgumentException("User ID cannot be null or empty");
    }
    return this;
  }
  
  /**
   * Get the user password.
   * @return The user password.
   */
  public String getPassword() {
    return mPassword;
  }
  
  /**
   * The user password.
   * @param password
   * @return This object.
   */
  public UserCreate setPassword(String password) {
    mPassword = password;
    return this;
  }
  
  /**
   * Get the user email address.
   * @return An email address or null.
   */
  public String getEmail() {
    return mEmail;
  }
  
  /**
   * The email address.
   * @param email
   * @return This object.
   */
  public UserCreate setEmail(String email) {
    mEmail = email;
    return this;
  }
  
  /**
   * Get the display name for the user.
   * @return The display name or null.
   */
  public String getDisplayName() {
    return mDisplayName;
  }
  
  /**
   * Set the display name.
   * @param displayName
   * @return This object.
   */
  public UserCreate setDisplayName(String displayName) {
    mDisplayName = displayName;
    return this;
  }

  /**
   * Get the user creation type.
   * @return
   */
  public UserCreateMode getCreateMode() {
    return mCreateMode;
  }

  /**
   * The type of user to be created.
   * @param createMode {@link UserCreateMode#GUEST} for anonymous user,
   *     {@link UserCreateMode#UPGRADE_USER} for normal user.
   * @return This object.
   */
  public UserCreate setCreateMode(UserCreateMode createMode) {
    this.mCreateMode = createMode;
    return this;
  }

  /**
   * Get the extra properties.
   * @return Null or extra properties.
   */
  public Map<String, String> getExtras() {
    return mExtras;
  }
  
  /**
   * Set extra properties for the user.
   * @param extras Extra properties.
   * @return This object.
   */
  public UserCreate setExtras(Map<String, String> extras) {
    mExtras = extras;
    return this;
  }
  
  /**
   * Get the tags.
   * @return The tag list, or null.
   */
  public List<String> getTags() {
    return mTags;
  }
  
  /**
   * Set the tags for the new user.  The tag cannot be empty and its length
   * cannot exceed {@link Constants#MMX_MAX_TAG_LEN}.
   * @param tags A list of tags.
   * @return This object.
   * @throws IllegalArgumentException
   */
  public UserCreate setTags(List<String> tags) {
    if ((mTags = tags) != null) {
      TagUtil.validateTags(tags);
    }
    return this;
  }
  
  public static UserCreate fromJson(String json) {
    return GsonData.getGson().fromJson(json, UserCreate.class);
  }
  
}
