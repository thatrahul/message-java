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

import java.util.Hashtable;

/**
 * @hide
 * A convenient class for the optional MMXMessage headers (meta data.)  All
 * headers are optional.
 */
public class Headers extends Hashtable<String, String> {
  private static final long serialVersionUID = 5090333410521907673L;
  /**
   * A special header used by MMX SDK to specify whom the message should be
   * replied to.  The value is either a user ID or a full XID which can be
   * constructed from MMXClient.
   */
  public final static String REPLY_TO = "Reply-To";
  /**
   * An optional header name for the message payload MIME content type.
   */
  public final static String CONTENT_TYPE = "Content-Type";
  /**
   * An optional header name for the message payload encoding type.  Although
   * the value is application specific, one standard value is "base64".
   */
  public final static String CONTENT_ENCODING = "Content-Encoding";

  /**
   * A default constructor.
   */
  public Headers() {
    super();
  }
  
  /**
   * A constructor with a default capacity.
   * @param capacity The initial capacity.
   */
  public Headers(int capacity) {
    super(capacity);
  }
  
  /**
   * A convenient method to set or remove a header.
   * @param key A key name.
   * @param value A value to set or null to remove the key.
   * @return
   */
  public Headers setHeader(String key, String value) {
    if (key == null)
      throw new IllegalArgumentException("The key cannot be null");
    if (value == null)
      remove(key);
    else
      put(key, value);
    return this;
  }
  
  /**
   * Get a header value with default.
   * @param key  A key name.
   * @param defVal A default value to be returned if the key does not exist.
   * @return A header value or the default value.
   */
  public String getHeader(String key, String defVal) {
    if (key == null)
      throw new IllegalArgumentException("The key cannot be null");
    String value;
    if ((value = this.get(key)) == null)
      return defVal;
    else
      return value;
  }
  
  /**
   * A convenient method to set the Reply-To.  Use the <code>makeXID</code>
   * method in MMXClient to construct the user or end-point address.
   * @param value The XID address.
   * @return This object.
   */
  public Headers setReplyTo(String value) {
    return setHeader(REPLY_TO, value);
  }
  
  /**
   * A convenient method to get the Reply-To XID address.
   * @param defVal A default value to be returned if the header does not exist.
   * @return The value or the default value.
   */
  public String getReplyTo(String defVal) {
    return getHeader(REPLY_TO, defVal);
  }
  
  /**
   * A convenient method to set the Content-Type.
   * @param value The MIME content-type of the payload.
   * @return This object.
   */
  public Headers setContentType(String value) {
    return setHeader(CONTENT_TYPE, value);
  }
  
  /**
   * A convenient method to get the Content-Type.
   * @param defVal A default value to be returned if the header does not exist.
   * @return The value or the default value.
   */
  public String getContentType(String defVal) {
    return getHeader(CONTENT_TYPE, defVal);
  }
  
  /**
   * A convenient method to set the Content-Encoding.  A commonly used for
   * binary data is "base64."
   * @param value The content encoding type.
   * @return This object.
   */
  public Headers setContentEncoding(String value) {
    return setHeader(CONTENT_ENCODING, value);
  }
  
  /**
   * A convenient method to get the Content-Encoding.
   * @param defVal A default value to be returned if the header does not exist.
   * @return The value or the default value.
   */
  public String getContentEncoding(String defVal) {
    return getHeader(CONTENT_ENCODING, defVal);
  }
}
