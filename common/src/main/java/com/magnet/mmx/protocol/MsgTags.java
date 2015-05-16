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
 * @hide
 * Message tags payload.  This is for MessageManager to get/set the tags for
 * message or push message.
 */
public class MsgTags extends MsgId {
  @SerializedName("tags")
  private List<String> mTags;
  @SerializedName("lastModTime")
  private Date mLastModTime;
  
  /**
   * @hide
   * Constructor for the request of message tags operation.  By specifying
   * a null or empty list to remove all tags.
   * @param type The message ID type.
   * @param msgId The message ID.
   * @param tags A list of tags or an empty list.
   */
  public MsgTags(IdType type, String msgId, List<String> tags) {
    super(type, msgId);
    mTags = tags;
  }

  /**
   * @hide
   * Constructor for the response of getting message tags.
   * @param type The message ID type.
   * @param msgId The message ID.
   * @param tags A list of tags or an empty list.
   * @param modTime The last modified time.
   */
  public MsgTags(IdType type, String msgId, List<String> tags, Date modTime) {
    super(type, msgId);
    mTags = tags;
    mLastModTime = modTime;
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
    return "[type="+mIdType+", msgId="+mMsgId+", modTime="+mLastModTime+
            ", tags="+mTags+"]";
  }
  
  public static MsgTags fromJson(String json) {
    return GsonData.getGson().fromJson(json, MsgTags.class);
  }
}
