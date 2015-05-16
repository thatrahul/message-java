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
import com.magnet.mmx.util.JSONifiable;

/**
 * Request for tagging a user, and a response for getting the tags from a user.
 */
public class UserTags extends JSONifiable {
  @SerializedName("tags")
  private List<String> mTags;
  @SerializedName("lastModTime")
  private Date mLastModTime;
  
  /**
   * @hide
   * Constructor for the request of setting the tags.  Setting an empty list
   * will remove all tags.
   * @param tags A list of tags or an empty list.
   */
  public UserTags(List<String> tags) {
    mTags = tags;
  }

  /**
   * @hide
   * Constructor for the response of getting the tags.
   * @param tags A list of tags or an empty list.
   * @param lastModTime The last modified time.
   */
  public UserTags(List<String> tags, Date lastModTime) {
    mTags = tags;
    mLastModTime = lastModTime;
  }

  /**
   * Get the tags.
   * @return A list of tags or an empty list.
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
    return "[modTime="+mLastModTime+", tags="+mTags+"]";
  }
  
  public static UserTags fromJson(String json) {
    return GsonData.getGson().fromJson(json, UserTags.class);
  }
}
