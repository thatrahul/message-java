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
import com.magnet.mmx.util.GsonData;
import com.magnet.mmx.util.JSONifiable;

/**
 * Request payload for search by tags.
 */
public class TagSearch extends JSONifiable {
  /**
   * Logical operator for the tags.
   */
  public static enum Operator {
    AND,
    OR,
  }
  
  @SerializedName("op")
  private Operator mOp;
  @SerializedName("tags")
  private List<String> mTags;
  
  public TagSearch(Operator op, List<String> tags) {
    if ((mOp = op) == null) {
      throw new IllegalArgumentException("Operator cannot be null");
    }
    if (((mTags = tags) == null) || mTags.isEmpty()) {
      throw new IllegalArgumentException("Tags cannot be null or empty");
    }
  }

  public Operator getOperator() {
    return mOp;
  }

  public List<String> getTags() {
    return mTags;
  }

  public static TagSearch fromJson(String json) {
    return GsonData.getGson().fromJson(json, TagSearch.class);
  }
}
