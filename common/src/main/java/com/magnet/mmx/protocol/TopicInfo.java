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

import java.util.Date;

/**
 * @hide
 * Protocol topic information from a topic search or list.
 */
public class TopicInfo extends MMXTopicId implements MMXTopic {
  private static final long serialVersionUID = -5212539242296015590L;
  @SerializedName("isCollection")
  private boolean mCollection;
  @SerializedName("description")
  private String mDescription;
  @SerializedName("isPersistent")
  private boolean mPersistent;
  @SerializedName("maxItems")
  private int mMaxItems;
  @SerializedName("maxPayloadSize")
  private int mMaxPayloadSize;
  @SerializedName("creationDate")
  private Date mCreationDate;
  @SerializedName("modificationDate")
  private Date mModifiedDate;
  @SerializedName("publisherType")
  private PublisherType mPublisherType;
  @SerializedName("creator")
  private String mCreator;
  @SerializedName("subscriptionEnabled")
  private boolean mSubscriptionEnabled;
  
  /**
   * @hide
   * @param userId
   * @param topic A topic name.
   * @param isCollection
   */
  public TopicInfo(String userId, String topic, boolean isCollection) {
    super(userId, topic);
    mCollection = isCollection;
  }

  /**
   * Check if this topic is a collection (i.e. for subscription only.)
   * @return true for subscribe only; false for publishing and subscription.
   */
  public boolean isCollection() {
    return mCollection;
  }
  
  /**
   * Get the topic description.
   * @return The description, or null.
   */
  public String getDescription() {
    return mDescription;
  }

  /**
   * Set the topic description.
   * @param description
   * @return This object.
   */
  public TopicInfo setDescription(String description) {
    mDescription = description;
    return this;
  }

  /**
   * Are the published items in this topic persistent?
   * @return true if this topic holds persistent items; otherwise, false.
   */
  public boolean isPersistent() {
    return mPersistent;
  }

  /**
   * @param persistent
   * @return
   */
  public TopicInfo setPersistent(boolean persistent) {
    mPersistent = persistent;
    return this;
  }

  /**
   * Max number of persisted published items to be held in this topic.
   * @return Maximum number of persisted published items.
   */
  public int getMaxItems() {
    return mMaxItems;
  }

  /**
   * @hide
   * @param maxItems
   * @return
   */
  public TopicInfo setMaxItems(int maxItems) {
    mMaxItems = maxItems;
    return this;
  }

  /**
   * Get the max payload size of published items.
   * @return The configured maximum payload size.
   */
  public int getMaxPayloadSize() {
    return mMaxPayloadSize;
  }

  /**
   * @hide
   * @param maxPayloadSize
   * @return
   */
  public TopicInfo setMaxPayloadSize(int maxPayloadSize) {
    mMaxPayloadSize = maxPayloadSize;
    return this;
  }

  /**
   * Get the topic creation date/time.
   * @return The topic creation date/time.
   */
  public Date getCreationDate() {
    return mCreationDate;
  }

  /**
   * @param creationDate
   * @return
   */
  public TopicInfo setCreationDate(Date creationDate) {
    mCreationDate = creationDate;
    return this;
  }

  /**
   * Get the last modified date/time of this topic.
   * @return The last modified date/time.
   */
  public Date getModifiedDate() {
    return mModifiedDate;
  }

  /**
   * @param modifiedDate
   * @return
   */
  public TopicInfo setModifiedDate(Date modifiedDate) {
    mModifiedDate = modifiedDate;
    return this;
  }

  /**
   * Get the publishing role to this topic.
   * @return The publisher type.
   */
  public PublisherType getPublisherType() {
    return mPublisherType;
  }

  /**
   * @param publisherType
   * @return
   */
  public TopicInfo setPublisherType(PublisherType publisherType) {
    mPublisherType = publisherType;
    return this;
  }

  /**
   * Get the creator JID.
   * @return
   */
  public String getCreator() {
    return mCreator;
  }

  public TopicInfo setCreator(String creator) {
    mCreator = creator;
    return this;
  }

  /**
   * Is subscription enabled for this topic?
   * @return
   */
  public boolean isSubscriptionEnabled() {
    return mSubscriptionEnabled;
  }

  /**
   * @param subscriptionEnabled
   * @return
   */
  public TopicInfo setSubscriptionEnabled(boolean subscriptionEnabled) {
    mSubscriptionEnabled = subscriptionEnabled;
    return this;
  }

  /**
   * Get the topic information in string format for debug purpose.
   * @return Informative data about the topic.
   */
  @Override
  public String toString() {
    return "[topic="+super.toString()+", desc="+mDescription+", sub="+mSubscriptionEnabled+
        ", maxItems="+mMaxItems+", maxSize="+mMaxPayloadSize+", pubtype="+mPublisherType+
        ", create="+mCreationDate+", mod="+mModifiedDate+"]";
  }
}
