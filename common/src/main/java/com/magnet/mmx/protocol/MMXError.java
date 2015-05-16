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
 * The payload for the MMX error message.
 */
public class MMXError extends JSONifiable {
  public static enum Severity {
    /**
     * The severity is unknown.
     */
    UNKNOWN,
    /**
     * The error is trivial; typically it is a user error.
     */
    TRIVIAL,
    /**
     * The error is temporary; retry will solve the issue.
     */
    TEMPORARY,
    /**
     * The error is major that retry will not help.  Avoid this function.
     */
    MAJOR,
    /**
     * The error is critical; the client should abort or exit.
     */
    CRITICAL,
  }

  @SerializedName("message")
  private String mMessage;
  @SerializedName("code")
  private int mCode;
  @SerializedName("severity")
  private Severity mSeverity = Severity.UNKNOWN;
  @SerializedName("msgId")
  private String mMsgId;
  
  /**
   * Constructor with a status code.
   * @param code A status codes similar to HTTP status code.
   */
  public MMXError(int code) {
    mCode = code;
  }
  
  /**
   * Get the error code defined in {@link StatusCode}
   * @return A status code.
   * @see StatusCode
   */
  public int getCode() {
    return mCode;
  }
  
  /**
   * Get a diagnostic message.
   * @return A diagnostic message.
   */
  public String getMessage() {
    return mMessage;
  }
  
  /**
   * Set a diagnostic message.
   * @param msg The diagnostic message.
   * @return This object.
   */
  public MMXError setMessage(String msg) {
    mMessage = msg;
    return this;
  }

  /**
   * Get the severity.
   * @return A severity.
   */
  public Severity getSeverity() {
    return mSeverity;
  }
  
  /**
   * Set the severity.
   * @param severity
   * @return This object.
   */
  public MMXError setSeverity(Severity severity) {
    mSeverity = severity;
    return this;
  }
  
  /**
   * Get the original message ID.
   * @return The original message ID.
   */
  public String getMsgId() {
    return mMsgId;
  }

  /**
   * Set the original message ID.
   * @param messageId The original message ID.
   * @return This object.
   */
  public MMXError setMsgId(String messageId) {
    mMsgId = messageId;
    return this;
  }

  @Override
  public String toString() {
    return "[code="+mCode+", msg="+mMessage+", severity="+mSeverity+
            ", msgId="+mMsgId+"]";
  }
  
  /**
   * Get the type of this payload.
   * @return The message type of the MMX error payload.
   */
  public static String getType() {
    return Constants.MMX_MTYPE_ERROR;
  }
  
  public static MMXError fromJson(String json) {
    return GsonData.getGson().fromJson(json, MMXError.class);
  }
}
