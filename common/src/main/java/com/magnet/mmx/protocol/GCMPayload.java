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

import java.util.Map;



public class GCMPayload {
  @SerializedName(Constants.PAYLOAD_PUSH_TITLE)
  private String title;
  @SerializedName(Constants.PAYLOAD_PUSH_BODY)
  private String body;
  @SerializedName(Constants.PAYLOAD_PUSH_ICON)
  private String icon;
  @SerializedName(Constants.PAYLOAD_PUSH_SOUND)
  private String sound;
  @SerializedName(Constants.PAYLOAD_MMX_KEY)
  private Map<String, ? super Object> mmx;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public String getSound() {
    return sound;
  }

  public void setSound(String sound) {
    this.sound = sound;
  }

  public Map<String, ? super Object> getMmx() {
    return mmx;
  }

  public void setMmx(Map<String, ? super Object> mmx) {
    this.mmx = mmx;
  }
}
