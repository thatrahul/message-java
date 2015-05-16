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

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * Encapsulates the search actions when performing
 * searches on users or topics.
 */
public class SearchAction {
  /**
   * Logical operator among the search attributes.
   */
  public static enum Operator {
    AND,
    OR,
  }
  
  /**
   * Value matching operation: exact match, wild-card in the prefix, wild-card
   * in the suffix.
   */
  public static enum Match {
    EXACT,
    PREFIX,
    SUFFIX,
  }
  
  /**
   * @hide
   */
  public static class SingleValue {
    @SerializedName("match")
    private Match mMatch;
    @SerializedName("value")
    private String mValue;
    
    public SingleValue(String value, Match matchType) {
      mValue = value;
      mMatch = matchType;
    }

    public Match getMatch() {
      return mMatch;
    }

    public String getValue() {
      return mValue;
    }
  }
  
  /**
   * @hide
   */
  public static class MultiValues {
    @SerializedName("match")
    private Match mMatch;
    @SerializedName("values")
    private List<String> mValues;
    
    public MultiValues(List<String> values, Match matchType) {
      mValues = values;
      mMatch = matchType;
    }

    public Match getMatch() {
      return mMatch;
    }

    public List<String> getValues() {
      return mValues;
    }
  }
}
