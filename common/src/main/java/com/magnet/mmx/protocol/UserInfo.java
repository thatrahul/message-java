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
 * This class holds the user information.
 */
public class UserInfo extends JSONifiable {
  @SerializedName("email")
  private String email;
  @SerializedName("displayName")
  private String displayName;
  @SerializedName("userId")
  private String userId;
  @SerializedName("phone")
  private String phone;


  // TODO: it should include the extras.
  
  /**
   * Get the email address to be updated.
   * @return An email address or null.
   */
  public String getEmail() {
    return email;
  }
  
  /**
   * Set the new email address for the user account.  Setting to null is no-op.
   * @param email The email address of the user.
   * @return This object.
   */
  public UserInfo setEmail(String email) {
    this.email = email;
    return this;
  }
  
  /**
   * Get the display name to be updated.
   * @return A display name or null.
   */
  public String getDisplayName() {
    return displayName;
  }
  
  /**
   * Set the new display name for the user account.  Setting to null is no-op.
   * @param displayName The display name of the user.
   * @return This object.
   */
  public UserInfo setDisplayName(String displayName) {
    this.displayName = displayName;
    return this;
  }

  /**
   * Get userId for the user represented by this object.
   * @return
   */
  public String getUserId() {
    return userId;
  }

  /**
   * Set the userId for the user.
   * @param userId
   * @return
   */
  public UserInfo setUserId(String userId) {
    this.userId = userId;
    return this;
  }

  /**
   * Get the phone for the user
   * @return
   */
  public String getPhone() {
    return phone;
  }

  /**
   * Set the phone for the user.
   * @param phone
   * @return This object.
   */
  public UserInfo setPhone(String phone) {
    this.phone = phone;
    return this;
  }

  @Override
  public String toString() {
    return "UserInfo{" +
        "email='" + email + '\'' +
        ", displayName='" + displayName + '\'' +
        ", userId='" + userId + '\'' +
        ", phone='" + phone + '\'' +
        '}';
  }

  /**
   * @hide
   * Encode the payload to JSON.
   */
  public static UserInfo fromJson(String json) {
    return GsonData.getGson().fromJson(json, UserInfo.class);
  }
}
