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
package com.magnet.mmx.server.plugin.mmxmgmt.hook;

public class UserCreateHookContext implements HookContext {
  private String userName;
  private String displayName;
  private String appId;


  public String getAppId() {
    return appId;
  }

  public UserCreateHookContext setAppId(String appId) {
    this.appId = appId;
    return this;
  }

  public String getDisplayName() {
    return displayName;
  }

  public UserCreateHookContext setDisplayName(String displayName) {
    this.displayName = displayName;
    return this;
  }

  public String getUserName() {
    return userName;
  }

  public UserCreateHookContext setUserName(String userName) {
    this.userName = userName;
    return this;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("UserCreateHookContext{");
    sb.append("appId='").append(appId).append('\'');
    sb.append(", userName='").append(userName).append('\'');
    sb.append(", displayName='").append(displayName).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
