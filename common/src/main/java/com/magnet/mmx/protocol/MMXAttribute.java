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

import com.google.gson.annotations.SerializedName;

/**
 * The attribute-value pair can be used for the search criteria, or property.
 */
public class MMXAttribute<T> implements Serializable {
  private static final long serialVersionUID = -6576399681215810856L;
  @SerializedName("type")
  private T mType;
  @SerializedName("value")
  private String mValue;
  
  /**
   * Constructor with type and value.
   * @param type A non-null type.
   * @param value A non-null value.
   */
  public MMXAttribute(T type, String value) {
    if ((mType = type) == null)
      throw new IllegalArgumentException("Type cannot be null");
    if ((mValue = value) == null)
      throw new IllegalArgumentException("Value cannot be null");
  }
  
  /**
   * Get the attribute name.
   * @return The attribute name.
   */
  public T getType() {
    return mType;
  }
  
  /**
   * Get the value.
   * @return The value.
   */
  public String getValue() {
    return mValue;
  }
}
