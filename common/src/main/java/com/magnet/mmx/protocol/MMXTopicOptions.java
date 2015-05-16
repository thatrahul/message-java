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
import com.magnet.mmx.protocol.TopicAction.PublisherType;

import java.io.Serializable;

/**
 * The topic options.  If no options are specified, the topic is defaulted to
 * authorized access (owner or subscribers), published by anyone, one 
 * persistent published item and subscription is enabled.
 */
public class MMXTopicOptions implements Serializable {
  private static final long serialVersionUID = 5092103177218064923L;
  private static final Integer DEFAULT_MAX_ITEMS = -1;
  private static final PublisherType DEFAULT_PUB_TYPE = PublisherType.anyone;
  
  @SerializedName("maxItems")
  private Integer mMaxItems;
  @SerializedName("publisherType")
  private PublisherType mPublisherType;
  @SerializedName("enableSubscription")
  private Boolean mSubscriptionEnabled;
  @SerializedName("description")
  private String mDescription;

  /**
   * Get the publisher type.  Default will be {@link PublisherType#anyone}.
   * @return The publisher type.
   */
  public PublisherType getPublisherType() {
    return mPublisherType;
  }
  
  /**
   * Set the publisher type.  The publisher type states who can publish an
   * item to a topic.
   * @param type Publisher type.
   * @return This object.
   */
  public MMXTopicOptions setPublisherType(PublisherType type) {
    mPublisherType = type;
    return this;
  }

  /**
   * Check if the topic can be subscribed.
   * @return Subscription enabled flag.
   */
  public Boolean isSubscriptionEnabled() {
    return mSubscriptionEnabled;
  }
  
  /**
   * Allow this topic to be subscribed or not.  Default is true.
   * @param enable True to enable; false to disable.
   * @return This object.
   */
  public MMXTopicOptions setSubscriptionEnabled(boolean enable) {
    mSubscriptionEnabled = enable ? Boolean.TRUE : Boolean.FALSE;
    return this;
  }
  
  /**
   * Get the maximum number of persistent items.
   * @return Max number of persistent items.
   */
  public Integer getMaxItems() {
    return mMaxItems;
  }
  
  /**
   * Maximum number of items can be persisted.  If it is not set, the default
   * will be 1.  If the max number of items is 0, no published items will
   * be persisted.  The -1 means unlimited.
   * @param maxItems Max number of published items to be persisted. 
   * @return This object.
   */
  public MMXTopicOptions setMaxItems(int maxItems) {
    mMaxItems = maxItems;
    return this;
  }
  
  /**
   * Get an optional description of this topic.
   * @return The description of the topic, or null.
   */
  public String getDescription() {
    return mDescription;
  }
  
  /**
   * Set an optional description to this topic.
   * @param description The description of the topic.
   * @return This object.
   */
  public MMXTopicOptions setDescription(String description) {
    mDescription = description;
    return this;
  }
  
  /**
   * @hide
   * Fill the values with default values if they are not set.
   */
  public void fillWithDefaults() {
    if (mPublisherType == null) {
      mPublisherType = DEFAULT_PUB_TYPE;
    }
    if (mMaxItems == null) {
      mMaxItems = DEFAULT_MAX_ITEMS;
    }
    if (mSubscriptionEnabled == null) {
      mSubscriptionEnabled = Boolean.TRUE;
    }
  }
}
