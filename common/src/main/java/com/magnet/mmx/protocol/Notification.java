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

/**
 * A payload for non-actionable Notification (UI).
 */
public class Notification {
  @SerializedName("title")
  private String mTitle;
  @SerializedName("text")
  private String mText;
  @SerializedName("icon")
  private String mIconResName;
  @SerializedName("sound")
  private String mSound;
  @SerializedName("badge")
  private int mBadge;
  
  public Notification(String title, String text) {
    mTitle = title;
    mText = text;
  }
  public String getTitle() {
    return mTitle;
  }
  public Notification setTitle(String title) {
    mTitle = title;
    return this;
  }
  public String getText() {
    return mText;
  }
  public Notification setText(String text) {
    mText = text;
    return this;
  }
  public String getIconResName() {
    return mIconResName;
  }
  public Notification setIconResName(String iconResName) {
    mIconResName = iconResName;
    return this;
  }
  public String getSound() {
    return mSound;
  }
  public Notification setSound(String sound) {
    mSound = sound;
    return this;
  }
  public int getBadge() {
    return mBadge;
  }
  public Notification setBadge(int badge) {
    mBadge = badge;
    return this;
  }
}
