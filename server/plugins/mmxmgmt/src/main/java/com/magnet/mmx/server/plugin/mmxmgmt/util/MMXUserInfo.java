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
package com.magnet.mmx.server.plugin.mmxmgmt.util;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class MMXUserInfo {
  private static final Logger LOGGER = LoggerFactory.getLogger(MMXUserInfo.class);
  private String username;
  private String appId;
  private String password;
  private String name;
  private String email;
  private Boolean isAdmin;

  public MMXUserInfo() {}

  public MMXUserInfo(String appId, String username, String password, String name, String email, Boolean isAdmin) {
    this.username = username;
    this.appId = appId;
    this.password = password;
    this.name = name;
    this.email = email;
    this.isAdmin = isAdmin;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Boolean getIsAdmin() {
    return isAdmin;
  }

  public void setIsAdmin(Boolean isAdmin) {
    this.isAdmin = isAdmin;
  }

  public String getMMXUsername() {
    if(Strings.isNullOrEmpty(appId))
      return username;
    else
      return Helper.getMMXUsername(username, appId);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof MMXUserInfo)) return false;

    MMXUserInfo that = (MMXUserInfo) o;

    if (appId != null ? !appId.equals(that.appId) : that.appId != null) return false;
    if (email != null ? !email.equals(that.email) : that.email != null) return false;
    if (isAdmin != null ? !isAdmin.equals(that.isAdmin) : that.isAdmin != null) return false;
    if (name != null ? !name.equals(that.name) : that.name != null) return false;
    if (password != null ? !password.equals(that.password) : that.password != null) return false;
    if (username != null ? !username.equals(that.username) : that.username != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = username != null ? username.hashCode() : 0;
    result = 31 * result + (appId != null ? appId.hashCode() : 0);
    result = 31 * result + (password != null ? password.hashCode() : 0);
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (email != null ? email.hashCode() : 0);
    result = 31 * result + (isAdmin != null ? isAdmin.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "MMXUserInfo{" +
            "username='" + username + '\'' +
            ", appId='" + appId + '\'' +
            ", password='" + password + '\'' +
            ", name='" + name + '\'' +
            ", email='" + email + '\'' +
            ", isAdmin=" + isAdmin +
            '}';
  }
}
