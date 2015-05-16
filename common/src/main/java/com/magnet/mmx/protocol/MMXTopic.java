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

import java.io.Serializable;

/**
 * A generic topic identifier interface for the topic.  Typically, a topic
 * resides in the global name-space that the topic name must be unique within
 * the application.
 */
public interface MMXTopic extends Serializable {
  /**
   * @hide
   * Return the user ID under which the topic resides.  A topic in the user 
   * name-space must have a unique name under the user.
   * @return A user ID for user topic, or null for global topic.
   */
  public String getUserId();
  /**
   * Get the topic name.
   * @return A topic name.
   */
  public String getName();
  /**
   * @hide
   * Check if this topic is under the user name-space.
   * @return true if a user topic; otherwise, false.
   */
  public boolean isUserTopic();
  
  /**
   * Check if two topics are equal.  The topic name is case insensitive.
   * @param topic A topic to be compared.
   * @return true if they are equals; otherwise, false.
   */
  public boolean equals(MMXTopic topic);
}
