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
import com.magnet.mmx.util.GsonData;

/**
 * @hide
 * This class represents an identifier for a topic under the global name-space
 * or under a user name-space.  Under a global name-space, the topic name is
 * unique within the application.  Under a user name-space, topic name is unique 
 * under a user ID within the application.
 */
public class MMXTopicId implements MMXTopic {
  private static final long serialVersionUID = -6889781636342385244L;
  @SerializedName("userId")
  protected String mUserId;
  @SerializedName("topicName")
  protected String mTopic;

  /**
   * @hide
   * Constructor for a global topic.
   * @param topic The topic name.
   */
  public MMXTopicId(String topic) {
    mTopic = topic;
  }

  /**
   * @hide
   * Constructor for a user topic.
   * @param userId The user ID of the user topic.
   * @param topic The topic name.
   */
  public MMXTopicId(String userId, String topic) {
    mUserId = userId;
    mTopic = topic;
  }

  /**
   * Get the user ID of the personal topic.
   * @return A user ID of the personal topic or null for global topic.
   */
  public String getUserId() {
    return mUserId;
  }

  /**
   * Get the topic name.
   * @return The topic name.
   */
  public String getName() {
    return mTopic;
  }
  
  /**
   * Check if this topic is under a user name-space.
   * @return true if it is a user topic, false if it is a global topic.
   */
  public boolean isUserTopic() {
    return mUserId != null;
  }

  /**
   * Check if two topics are equals.  The topic name is case insensitive.
   * @param topic A topic to be matched
   * @return true if they are equals; otherwise, false.
   */
  public boolean equals(MMXTopic topic) {
    if (topic == this)
      return true;
    if ((topic == null) || (mUserId == null ^ topic.getUserId() == null))
      return false;
    if ((mUserId != null) && !mUserId.equalsIgnoreCase(topic.getUserId()))
      return false;
    return mTopic.equalsIgnoreCase(topic.getName());
  }
  
  /**
   * Get a string representation of this topic identifier.
   * @return A string in "&#42/topic" for global topic or "userID/topic" for user topic.
   * @see #parse(String)
   */
  @Override
  public String toString() {
    return (mUserId == null) ? ("*/"+mTopic) : (mUserId+'/'+mTopic);
  }
  
  /**
   * Convert a string representation of topic identifier to the object.
   * @param topicId The value from {@link #toString()}
   * @return A MMXTopic object.
   * @see #toString()
   */
  public static MMXTopicId parse(String topicId) {
    int slash = topicId.indexOf('/');
    if ((slash == 1) && (topicId.charAt(0) == '*')) {
      return new MMXTopicId(topicId.substring(slash+1));
    } else if (slash >= 1) {
      return new MMXTopicId(topicId.substring(0, slash), topicId.substring(slash+1));
    }
    throw new IllegalArgumentException("Not a valid topic format: "+topicId);
  }
  
  /**
   * @hide
   */
  public static MMXTopicId fromJson(String json) {
    return GsonData.getGson().fromJson(json, MMXTopicId.class);
  }
}
