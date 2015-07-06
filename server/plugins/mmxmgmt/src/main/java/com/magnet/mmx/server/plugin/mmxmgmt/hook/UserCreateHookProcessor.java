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

import com.magnet.mmx.server.plugin.mmxmgmt.db.HookEntity;
import com.magnet.mmx.util.JSONifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserCreateHookProcessor implements HookProcessor {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserCreateHookProcessor.class);

  @Override
  public void process(HookEntity hook, HookContext context) {
      if (context instanceof UserCreateHookContext) {
        processInternal(hook, (UserCreateHookContext) context);
      }
  }

  private void processInternal (HookEntity hook, UserCreateHookContext context) {
    UserInfo info = new UserInfo();
    info.setAppId(context.getAppId());
    info.setDisplayName(context.getDisplayName());
    info.setUserName(context.getUserName());
    String payload = info.toJson();
    int responseCode = MessageWithMetaHookProcessor.processHookPost(hook, context.getAppId(), payload);
    if (responseCode == 200) {
      LOGGER.info("hook information posted");
    } else {
      LOGGER.warn("Response code:{} when posting payload:{} to {}", responseCode, payload, hook.getTargetURL());
    }
  }


  private static class UserInfo extends JSONifiable {
    String userName;
    String displayName;
    String appId;

    public String getAppId() {
      return appId;
    }

    public UserInfo setAppId(String appId) {
      this.appId = appId;
      return this;
    }

    public String getDisplayName() {
      return displayName;
    }

    public UserInfo setDisplayName(String displayName) {
      this.displayName = displayName;
      return this;
    }

    public String getUserName() {
      return userName;
    }

    public UserInfo setUserName(String userName) {
      this.userName = userName;
      return this;
    }
  }

}
