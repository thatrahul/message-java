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
package com.magnet.mmx.server.plugin.mmxmgmt.db;

import java.util.Date;
import java.util.Map;

public class HookEntity {
  private int id;
  private String hookName;
  private String targetURL;
  private String eventType;
  private Map<String, String> eventConfig;
  private String appId;
  private Date dateCreated;
  private Date dateUpdated;

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public Date getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(Date dateCreated) {
    this.dateCreated = dateCreated;
  }

  public Date getDateUpdated() {
    return dateUpdated;
  }

  public void setDateUpdated(Date dateUpdated) {
    this.dateUpdated = dateUpdated;
  }

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

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getTargetURL() {
    return targetURL;
  }

  public void setTargetURL(String targetURL) {
    this.targetURL = targetURL;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    HookEntity that = (HookEntity) o;

    if (id != that.id) return false;
    if (!hookName.equals(that.hookName)) return false;
    if (!targetURL.equals(that.targetURL)) return false;
    if (!eventType.equals(that.eventType)) return false;
    if (eventConfig != null ? !eventConfig.equals(that.eventConfig) : that.eventConfig != null) return false;
    if (!appId.equals(that.appId)) return false;
    if (!dateCreated.equals(that.dateCreated)) return false;
    return !(dateUpdated != null ? !dateUpdated.equals(that.dateUpdated) : that.dateUpdated != null);

  }

  @Override
  public int hashCode() {
    int result = id;
    result = 31 * result + hookName.hashCode();
    result = 31 * result + targetURL.hashCode();
    result = 31 * result + eventType.hashCode();
    result = 31 * result + (eventConfig != null ? eventConfig.hashCode() : 0);
    result = 31 * result + appId.hashCode();
    result = 31 * result + dateCreated.hashCode();
    result = 31 * result + (dateUpdated != null ? dateUpdated.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("HookEntity{");
    sb.append("appId='").append(appId).append('\'');
    sb.append(", id=").append(id);
    sb.append(", hookName='").append(hookName).append('\'');
    sb.append(", targetURL='").append(targetURL).append('\'');
    sb.append(", eventType='").append(eventType).append('\'');
    sb.append(", eventConfig=").append(eventConfig);
    sb.append(", dateCreated=").append(dateCreated);
    sb.append(", dateUpdated=").append(dateUpdated);
    sb.append('}');
    return sb.toString();
  }
}
