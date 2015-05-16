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

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.magnet.mmx.util.GsonData;
import com.magnet.mmx.util.JSONifiable;

/**
 * An internal payload to request for the last published items from
 * the subscriptions.
 */
public class SendLastPublishedItems extends JSONifiable {
  @SerializedName("since")
  private Date mSince;
  @SerializedName("maxItems")
  private int mMaxItems;
  
  /**
   * Constructor to request for published items of all topics since a given 
   * date/time.
   * @param maxItems -1 for all items; otherwise a positive number.
   * @param since A datetime.
   */
  public SendLastPublishedItems(int maxItems, Date since) {
    mSince = since;
    mMaxItems = maxItems;
  }

  public Date getSince() {
    return mSince;
  }
  
  public int getMaxItems() {
    return mMaxItems;
  }

  public static SendLastPublishedItems fromJson(String json) {
    return GsonData.getGson().fromJson(json, SendLastPublishedItems.class);
  }
}
