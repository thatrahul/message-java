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
import com.magnet.mmx.util.JSONifiable;

/**
 * @hide
 * An ack payload for reliable message.  Upon receiving this payload, MMX server
 * changes the state of a message identified by <code>msgId</code> to 
 * {@link com.magnet.mmx.protocol.Constants.MessageState#DELIVERED}.
 */
public class MsgAck extends JSONifiable {
  @SerializedName("from")
  private String mFrom;
  @SerializedName("to")
  private String mTo;
  @SerializedName("msgId")
  private String mMsgId;
  
  /**
   * Constructor of a message ack.
   * @param from Full JID of the sender of the original message.
   * @param to Full JID of the message receiver (i.e. the current user.)
   * @param msgId
   */
  public MsgAck(String from, String to, String msgId) {
    mFrom = from;
    mTo = to;
    mMsgId = msgId;
  }

  /**
   * Get the sender of the original message.
   * @return
   */
  public String getFrom() {
    return mFrom;
  }

  /**
   * Get the receiver of the original message.
   * @return
   */
  public String getTo() {
    return mTo;
  }

  public String getMsgId() {
    return mMsgId;
  }
}
