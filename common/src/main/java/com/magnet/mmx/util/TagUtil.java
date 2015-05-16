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

package com.magnet.mmx.util;

import java.util.List;

import com.magnet.mmx.protocol.Constants;

/**
 * @hide
 * A utility class for tags.
 */
public class TagUtil {
  
  /**
   * Validate the tag for its length.
   * @param tag A tag.
   * @throws IllegalArgumentException The length of tag name is 0 or too long.
   */
  public static void validateTag(String tag) {
    if (tag != null) {
      int len = tag.length();
      if (len == 0) {
        throw new IllegalArgumentException("The tag name cannot be empty");
      }
      if (len > Constants.MMX_MAX_TAG_LEN) {
        throw new IllegalArgumentException("The length of tag name exceeds "+Constants.MMX_MAX_TAG_LEN);
      }
    }
  }
  
  /**
   * Validate the tags for its length.
   * @param tags A list of tags.
   * @throws IllegalArgumentException The length of tag name is too long.
   */
  public static void validateTags(List<String> tags) {
    for (String tag : tags) {
      validateTag(tag);
    }
  }
}
