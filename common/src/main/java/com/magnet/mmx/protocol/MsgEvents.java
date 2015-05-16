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
 * Message events payload.  This is for MessageManager to set/get event for a
 * message or push message.
 */
public class MsgEvents extends MsgId {
  @SerializedName("events")
  private List<String> mEvents;
  @SerializedName("lastModTime")
  private Date mLastModTime;
  
  /**
   * @hide
   * Constructor for the request of message events operation.  By specifying
   * a null or empty list to remove all events.
   * @param type The message ID type.
   * @param msgId The message ID.
   * @param events A list of events or an empty list.
   */
  public MsgEvents(IdType type, String msgId, List<String> events) {
    super(type, msgId);
    mEvents = events;
  }

  /**
   * @hide
   * Constructor for the response of getting message events.
   * @param type The message ID type.
   * @param msgId The message ID.
   * @param events A list of events or an empty list.
   * @param modTime The last modified time.
   */
  public MsgEvents(IdType type, String msgId, List<String> events, Date modTime) {
    super(type, msgId);
    mEvents = events;
    mLastModTime = modTime;
  }

  /**
   * Get the events.
   * @return A list of events or an empty list.
   */
  public List<String> getEvents() {
    return mEvents;
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
            ", events="+mEvents+"]";
  }
  
  public static MsgEvents fromJson(String json) {
    return GsonData.getGson().fromJson(json, MsgEvents.class);
  }
}
