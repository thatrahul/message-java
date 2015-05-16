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

/**
 * Payload for ping-pong push messages.  This payload will be used in
 * pingpong, ping and pong custom IQ.
 *
 */
public class PingPong {
  @SerializedName("from")
  private String mFrom;
  @SerializedName("id")
  private String mId;
  @SerializedName("text")
  private String mText;
  @SerializedName("callbackURL")
  private String mUrl;
  @SerializedName("client")
  private String mClient;
  
  /**
   * A payload for two-way ping-pong.
   * @param from The full MMX ID of the sender.
   * @param id A tracking ID.
   * @param text Optional text message.
   */
  public PingPong(String from, String id, String text) {
    mFrom = from;
    mId = id;
    mText = text;
  }
  
  /**
   * A payload for one-way ping.
   * @param from The full MMX ID of the sender.
   * @param id A tracking ID.
   * @param text Optional text message.
   * @param url A callback URL.
   */
  public PingPong(String from, String id, String text, String url) {
    mFrom = from;
    mId = id;
    mText = text;
    mUrl = url;
  }
  
  public String getFrom() {
    return mFrom;
  }
  
  /**
   * Set the sender address using a full MMX ID.
   * @param from A full MMX ID.
   */
  public PingPong setFrom(String from) {
    mFrom = from;
    return this;
  }
  
  public String getId() {
    return mId;
  }
  
  /**
   * Set the tracking ID.
   * @param id
   */
  public PingPong setId(String id) {
    mId = id;
    return this;
  }
  
  public String getText() {
    return mText;
  }
  
  /**
   * Optional text for one-way ping, mandatory for pingpong and pong.
   * @param text
   */
  public PingPong setText(String text) {
    mText = text;
    return this;
  }
  
  public String getUrl() {
    return mUrl;
  }
  /**
   * A callback URL for one-way ping.
   * @param url A callback URL.
   */
  public PingPong setUrl(String url) {
    mUrl = url;
    return this;
  }

  public String getClient() {
    return mClient;
  }

  /**
   * Optional the client connection name.  It is only required if the client
   * supports multiple concurrent connections.
   * @param client
   * @return
   */
  public PingPong setClient(String client) {
    mClient = client;
    return this;
  }
}
