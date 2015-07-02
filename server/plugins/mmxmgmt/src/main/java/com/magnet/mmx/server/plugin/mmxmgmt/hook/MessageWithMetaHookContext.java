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

import org.dom4j.Element;

public class MessageWithMetaHookContext implements HookContext {
  private Element mmxElement;
  private String appId;
  private String fromJID;
  private String toJID;
  private String deviceId;
  private String messageId;

  public String getAppId() {
    return appId;
  }

  public MessageWithMetaHookContext setAppId(String appId) {
    this.appId = appId;
    return this;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public MessageWithMetaHookContext setDeviceId(String deviceId) {
    this.deviceId = deviceId;
    return this;
  }

  public String getFromJID() {
    return fromJID;
  }

  public MessageWithMetaHookContext setFromJID(String fromJID) {
    this.fromJID = fromJID;
    return this;
  }

  public Element getMmxElement() {
    return mmxElement;
  }

  public MessageWithMetaHookContext setMmxElement(Element mmxElement) {
    this.mmxElement = mmxElement;
    return this;
  }

  public String getToJID() {
    return toJID;
  }

  public MessageWithMetaHookContext setToJID(String toJID) {
    this.toJID = toJID;
    return this;
  }

  public String getMessageId() {
    return messageId;
  }

  public MessageWithMetaHookContext setMessageId(String messageId) {
    this.messageId = messageId;
    return this;
  }
}
