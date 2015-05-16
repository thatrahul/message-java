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
import com.magnet.mmx.util.JSONifiable;

/**
 * @hide
 * A JSON message ID payload.
 */
public class MsgId extends JSONifiable {
  /**
   * @hide
   */
  public static enum IdType {
    /**
     * Normal message ID.
     */
    message,
    /**
     * Push message ID.
     */
    pushMessage,
  }
  
  @SerializedName("idType")
  protected IdType mIdType;
  @SerializedName("msgId")
  protected String mMsgId;

  /**
   * @hide
   * Default constructor with a message ID.
   * @param idType
   * @param msgId
   */
  public MsgId(IdType idType, String msgId) {
    mIdType = idType;
    mMsgId = msgId;
  }
  
  /**
   * Get the type of message.
   * @return A message type.
   */
  public IdType getIdType() {
    return mIdType;
  }
  
  /**
   * Get the msg ID.
   * @return A message ID.
   */
  public String getMsgId() {
    return mMsgId;
  }

  public static MsgId fromJson(String json) {
    return GsonData.getGson().fromJson(json, MsgId.class);
  }
}
