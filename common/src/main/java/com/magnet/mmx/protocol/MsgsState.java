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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.google.gson.annotations.SerializedName;
import com.magnet.mmx.protocol.Constants.MessageState;
import com.magnet.mmx.util.GsonData;
import com.magnet.mmx.util.JSONifiable;

/**
 * The request and response for querying the states of messages.
 *
 */
public class MsgsState {
  public static class Request extends ArrayList<String> {
    private static final long serialVersionUID = -4718535747665171508L;

    public Request() {
      super();
    }
    
    public Request(int size) {
      super(size);
    }
    
    public Request(Collection<String> ids) {
      super(ids);
    }

    public static Request fromJson(String json) {
      return GsonData.getGson().fromJson(json, Request.class);
    }

    public String toJson() {
      return GsonData.getGson().toJson(this);
    }
  }

  /**
   * @hide
   */
  public static class MessageStatus extends JSONifiable {
    @SerializedName("state")
    private MessageState mState;
    @SerializedName("recipient")
    private String mRecipient;
    
    public MessageState getState() {
      return mState;
    }
    public MessageStatus setState(MessageState state) {
      mState = state;
      return this;
    }
    public String getRecipient() {
      return mRecipient;
    }
    public MessageStatus setRecipient(String recipient) {
      mRecipient = recipient;
      return this;
    }
  }
  
  public static class MessageStatusList extends ArrayList<MessageStatus> {
    public MessageStatusList() {
      super();
    }
    
    public MessageStatusList(int capacity) {
      super(capacity);
    }
    
    public static MessageStatusList fromJson(String json) {
      return GsonData.getGson().fromJson(json, MessageStatusList.class);
    }
  }
  
  /**
   * @hide
   */
  public static class Response extends HashMap<String, MessageStatusList> {
    public Response() {
      super();
    }
    
    public Response(int capacity) {
      super(capacity);
    }
    
    public String toJson() {
      return GsonData.getGson().toJson(this);
    }
    
    public static Response fromJson(String json) {
      return GsonData.getGson().fromJson(json, Response.class);
    }
  }
}
