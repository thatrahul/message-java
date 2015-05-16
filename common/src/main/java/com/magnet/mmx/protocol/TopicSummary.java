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

import com.google.gson.annotations.SerializedName;

/**
 * A topic summary.  The summary only provides a very condensed information.
 * There is an intention to expand this class in the future.
 */
public class TopicSummary {
  @SerializedName("topicNode")
  private MMXTopicId mTopic;
  @SerializedName("count")
  private int mCount;
  @SerializedName("lastPubTime")
  private Date mLastPubTime;

  /**
   * @hide
   * @param topic
   */
  public TopicSummary(MMXTopicId topic) {
    mTopic = topic;
  }

  /**
   * @hide
   * @param count
   * @return
   */
  public TopicSummary setCount(int count) {
    mCount = count;
    return this;
  }

  /**
   * @hide
   * @param lastPubTime
   * @return
   */
  public TopicSummary setLastPubTime(Date lastPubTime) {
    mLastPubTime = lastPubTime;
    return this;
  }

  /**
   * Get the topic name.
   * @return
   */
  public MMXTopic getTopicNode() {
    return mTopic;
  }

  /**
   * Get the number of published items in this topic.
   * @return
   */
  public int getCount() {
    return mCount;
  }

  /**
   * Get the last published date/time.
   * @return
   */
  public Date getLastPubTime() {
    return mLastPubTime;
  }

  @Override
  public String toString() {
    return "[node="+mTopic.toString()+", count="+mCount+
            ", lastpub="+mLastPubTime+"]";
  }
}
