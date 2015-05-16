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
 * The status payload of a request/response operation to MMX server.
 */
public class MMXStatus extends JSONifiable implements StatusCode {
  @SerializedName("message")
  private String mMessage;
  @SerializedName("code")
  private int mCode;

  /**
   * Get a diagnostic message of the operation.
   * @return A diagnostic message.
   */
  public String getMessage() {
    return mMessage;
  }

  /**
   * @hide
   * Set a diagnostic message of the operation.
   * @param message A diagnostic message.
   * @return This object.
   */
  public MMXStatus setMessage(String message) {
    mMessage = message;
    return this;
  }

  /**
   * Get the status code of the operation.
   * @return
   */
  public int getCode() {
    return mCode;
  }

  /*
   * @hide
   * Set the status code.
   * @param code A status code.
   * @return This object.
   */
  public MMXStatus setCode(int code) {
    mCode = code;
    return this;
  }
  
  /**
   * @hide
   * @param json
   * @return
   */
  public static MMXStatus fromJson(String json) {
    return GsonData.getGson().fromJson(json, MMXStatus.class);
  }
  
  @Override
  public String toString() {
    return "[code="+mCode+", msg="+mMessage+"]";
  }
}
