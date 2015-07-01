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
package com.magnet.mmx.server.api.v1.protocol;

import java.util.Map;

/**
 * Object defining the payload for Hook creation request.
 */
public class HookCreateRequest {
  private String hookName;
  private String targetURL;
  private String eventType;
  private Map<String, String> eventConfig;

  public Map<String, String> getEventConfig() {
    return eventConfig;
  }

  public void setEventConfig(Map<String, String> eventConfig) {
    this.eventConfig = eventConfig;
  }

  public String getEventType() {
    return eventType;
  }

  public void setEventType(String eventType) {
    this.eventType = eventType;
  }

  public String getHookName() {
    return hookName;
  }

  public void setHookName(String hookName) {
    this.hookName = hookName;
  }

  public String getTargetURL() {
    return targetURL;
  }

  public void setTargetURL(String targetURL) {
    this.targetURL = targetURL;
  }
}
