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

import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.magnet.mmx.util.GsonData;

/**
 * Device tags payload for set request and get response.
 */
public class DevTags extends DevId {
  @SerializedName("tags")
  private List<String> mTags;
  @SerializedName("lastModTime")
  private Date mLastModTime;

  
  /**
   * @hide
   * Constructor for the request of setting device tags.  Setting an empty list
   * will remove all tags.
   * @param devId The device ID.
   * @param tags A list of tags or an empty list.
   */
  public DevTags(String devId, List<String> tags) {
    super(devId);
    if ((mDevId = devId) == null)
      throw new NullPointerException("Device ID cannot be null");
    if ((mTags = tags) == null)
      throw new NullPointerException("Device tags cannot be null");
  }

  /**
   * @hide
   * Constructor for the response of getting device tags.
   * @param devId The device ID.
   * @param tags A list of tags or an empty list.
   * @param lastModTime The last modifed time.
   */
  public DevTags(String devId, List<String> tags, Date lastModTime) {
    super(devId);
    if ((mDevId = devId) == null)
      throw new NullPointerException("Device ID cannot be null");
    if ((mTags = tags) == null)
      throw new NullPointerException("Device tags cannot be null");
    mLastModTime = lastModTime;
  }

  /**
   * Get the tags.
   * @return An list of the tags, or an empty list.
   */
  public List<String> getTags() {
    return mTags;
  }
  
  /**
   * Get the last modified time.
   * @return Last modified time.
   */
  public Date getLastModTime() {
    return mLastModTime;
  }

  @Override
  public String toString() {
    return "[devId="+mDevId+", modTime="+mLastModTime+", tags="+mTags+"]";
  }
  
  public static DevTags fromJson(String json) {
    return GsonData.getGson().fromJson(json, DevTags.class);
  }
}
