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

package com.magnet.mmx.client.common;

import com.magnet.mmx.protocol.MMXTopicId;

/**
 * A user topic that is under the current user name-space.
 */
public class MMXPersonalTopic extends MMXTopicId implements MMXVisibleTopic {
  private static final long serialVersionUID = 3341064906640623185L;

  /**
   * Constructor for a topic under the current user name-space.
   * @param topic A topic name.
   */
  public MMXPersonalTopic(String topic) {
    super(null, topic);
  }

  /**
   * Determine if this topic is under the user name-space.
   * @return Always true.
   */
  @Override
  public boolean isUserTopic() {
    return true;
  }

  MMXPersonalTopic setUserId(String userId) {
    mUserId = userId;
    return this;
  }

  @Override
  public String toString() {
    if (mUserId == null) {
      return "?/"+getName();
    } else {
      return super.toString();
    }
  }
}
