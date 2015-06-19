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
import com.magnet.mmx.protocol.SearchAction.Match;
import com.magnet.mmx.protocol.SearchAction.MultiValues;
import com.magnet.mmx.protocol.SearchAction.Operator;
import com.magnet.mmx.protocol.SearchAction.SingleValue;
import com.magnet.mmx.util.GsonData;
import com.magnet.mmx.util.JSONifiable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class represents the PubSub protocols and operations.
 *
 */
public class TopicAction {
  /**
   * The role who can publish.
   */
  public static enum PublisherType {
    /**
     * Anyone can publish.
     */
    anyone,
    /**
     * Owner (as a sole publisher) can publish.
     */
    owner,
    /**
     * Owner and subscribers can publish.
     */
    subscribers;
    public static final String[] names=new String[values().length];
    static {
      PublisherType[] values=values();
      for(int i=0;i<values.length;i++)
        names[i]=values[i].name();
    }
  };

  /**
   * @hide
   * Filter for topic listings.
   */
  public static enum ListType {
    /**
     * List global topics only
     */
    global,
    /**
     * List personal topics only.
     */
    personal,
    /**
     * List both global and personal topics.
     */
    both,
  }

  /**
   * The topic tags.
   */
  public static class TopicTags extends JSONifiable {
    @SerializedName("userId")
    private String mUserId;
    @SerializedName("topicName")
    private String mTopic;
    @SerializedName("tags")
    private List<String> mTags;
    @SerializedName("lastModTime")
    private Date mLastModTime;
    private transient MMXTopic mMMXTopic;

    /**
     * @hide
     * Constructor for the request of setting the tags.  Setting an empty list
     * will remove all tags.
     * @param userId The user ID for a personal topic, null for global topic.
     * @param topic The topic name.
     * @param tags A list of tags or an empty list.
     */
    public TopicTags(String userId, String topic, List<String> tags) {
      mUserId = userId;
      mTopic = topic;
      mTags = tags;
    }

    /**
     * @hide
     * Constructor for the response of getting the tags.
     * @param userId The user ID for a personal topic, null for global topic.
     * @param topic The topic name.
     * @param tags A list of tags or an empty list.
     * @param lastModTime The last modified time.
     */
    public TopicTags(String userId, String topic, List<String> tags, Date lastModTime) {
      mUserId = userId;
      mTopic = topic;
      mTags = tags;
      mLastModTime = lastModTime;
    }

    /**
     * @hide
     * Get the user ID of the personal topic.
     * @return The user ID of personal topic, or null for global topic.
     */
    public String getUserId() {
      return mUserId;
    }

    /**
     * @hide
     * Get the topic name.
     * @return A topic name.
     */
    public String getTopicName() {
      return mTopic;
    }

    /**
     * Get the topic associated with these tags.
     * @return The topic.
     */
    public MMXTopic getTopic() {
      if (mMMXTopic == null)
        mMMXTopic = new MMXTopicId(mUserId, mTopic);
      return mMMXTopic;
    }
    
    /**
     * Get the tags.
     * @return A list of tags or an empty list.
     */
    public List<String> getTags() {
      return mTags;
    }

    /**
     * Get the last modified time.
     * @return Last modified time.
     */
    public Date getLastModTime() {
      return mLastModTime;
    }

    @Override
    public String toString() {
      return "[userId="+mUserId+", topic="+mTopic+", modTime="+mLastModTime+
              ", tags="+mTags+"]";
    }

    /**
     * @hide
     */
    public static TopicTags fromJson(String json) {
      return GsonData.getGson().fromJson(json, TopicTags.class);
    }
  }

  /**
   * @hide
   * Request payload for creating a topic.  The topic to be created can always
   * be published and subscribed.
   */
  public static class CreateRequest extends JSONifiable {
    @SerializedName("topicName")
    private String mTopic;
    @SerializedName("isPersonal")
    private boolean mPersonal;
    @SerializedName("isCollection")
    private boolean mCollection;
    @SerializedName("options")
    private MMXTopicOptions mOptions;

    /**
     * Default constructor.  A path-like topic name provides a simplified syntax
     * in hierarchical form.  The topic must not be started or ended with '/',
     * and each node in the path can only be alphanumeric or '_'.  All the
     * parent nodes will be created automatically and they can be subscribed.
     * @param topicName A path like topic name.
     * @param isPersonal True for personal topic; false for global topic.
     * @param options A creation options or null.
     */
    public CreateRequest(String topicName, boolean isPersonal,
                          MMXTopicOptions options) {
      mTopic = topicName;
      mPersonal = isPersonal;
      mCollection = false;
      mOptions = options;
    }

    /**
     * Get the topic name.
     * @return The topic name.
     */
    public String getTopicName() {
      return mTopic;
    }

    /**
     * Check if the creating topic is personal.
     * @return true for personal topic, false for global topic.
     */
    public boolean isPersonal() {
      return mPersonal;
    }

    /**
     * @hide
     * @return true to create this topic as a collection; false to create it as leaf node.
     */
    public boolean isCollection() {
      return mCollection;
    }

    /**
     * Get the topic creation options.
     * @return The creation options, or null.
     */
    public MMXTopicOptions getOptions() {
      return mOptions;
    }

    public static CreateRequest fromJson(String json) {
      return GsonData.getGson().fromJson(json, CreateRequest.class);
    }
  }

  /**
   * @hide
   * Delete a topic and its children if it is a collection.
   */
  public static class DeleteRequest extends JSONifiable {
    @SerializedName("topicName")
    private String mTopic;
    @SerializedName("isPersonal")
    private boolean mPersonal;

    /**
     * Default constructor for the topic deletion request.
     * @param topic A path like topic name.
     * @param isPersonal True for personal topic; false for global topic.
     */
    public DeleteRequest(String topic, boolean isPersonal) {
      mTopic = topic;
      mPersonal = isPersonal;
    }

    /**
     * Get the topic name.
     * @return The topic name.
     */
    public String getTopic() {
      return mTopic;
    }

    /**
     * Check if the deleting topic is personal.
     * @return true for personal topic, false for global topic.
     */
    public boolean isPersonal() {
      return mPersonal;
    }

    public static DeleteRequest fromJson(String json) {
      return GsonData.getGson().fromJson(json, DeleteRequest.class);
    }
  }

  /**
   * @hide
   * Request payload for retracting all published items from the topic owner.
   */
  public static class RetractAllRequest extends JSONifiable {
    @SerializedName("topicName")
    private String mTopic;
    @SerializedName("isPersonal")
    private boolean mPersonal;

    /**
     * Constructor to retract all published items from a personal topic or
     * global topic owned by the requester.
     * @param topic A path like topic name.
     * @param isPersonal true for a personal topic; false for global topic.
     */
    public RetractAllRequest(String topic, boolean isPersonal) {
      mTopic = topic;
      mPersonal = isPersonal;
    }

    public String getTopic() {
      return mTopic;
    }

    public boolean isPersonal() {
      return mPersonal;
    }

    public static RetractAllRequest fromJson(String json) {
      return GsonData.getGson().fromJson(json, RetractAllRequest.class);
    }
  }

  /**
   * @hide
   * Request payload for retracting published items from a topic.  The requester
   * must have proper permission to remove the items.
   */
  public static class RetractRequest extends JSONifiable {
    @SerializedName("userId")
    private String mUserId;
    @SerializedName("topicName")
    private String mTopic;
    @SerializedName("itemIds")
    private List<String> mItemIds;

    /**
     * Constructor to retract published items from a topic.  The requester must
     * have permission to retract the published items.
     * @param userId User ID of a personal topic or null for global topic.
     * @param topic A path like topic name.
     * @param itemIds Published item ID's to be retracted.
     */
    public RetractRequest(String userId, String topic, List<String> itemIds) {
      mUserId = userId;
      mTopic = topic;
      mItemIds = itemIds;
    }

    public String getUserId() {
      return mUserId;
    }

    public String getTopic() {
      return mTopic;
    }

    public List<String> getItemIds() {
      return mItemIds;
    }

    public static RetractRequest fromJson(String json) {
      return GsonData.getGson().fromJson(json, RetractRequest.class);
    }
  }

  /**
   * @hide
   * A request to access published items by ID's.
   */
  public static class ItemsByIdsRequest extends JSONifiable {
    @SerializedName("userId")
    private String mUserId;
    @SerializedName("topicName")
    private String mTopic;
    @SerializedName("itemIds")
    private List<String> mItemIds;
    
    /**
     * Constructor to get published items from a topic by item ID's.
     * @param userId User ID of a user topic or null for global topic.
     * @param topic A path like topic name.
     * @param itemIds Published item ID's.
     */
    public ItemsByIdsRequest(String userId, String topic, List<String> itemIds) {
      mUserId = userId;
      mTopic = topic;
      mItemIds = itemIds;
    }

    public String getUserId() {
      return mUserId;
    }

    public String getTopic() {
      return mTopic;
    }

    public List<String> getItemIds() {
      return mItemIds;
    }

    public static ItemsByIdsRequest fromJson(String json) {
      return GsonData.getGson().fromJson(json, ItemsByIdsRequest.class);
    }
  }
  
  /**
   * @hide
   * Request payload for listing all topics with a specified limit.  If no
   * limit is specified, no maximum number of topics will be imposed.
   */
  public static class ListRequest extends JSONifiable {
    @SerializedName("limit")
    private Integer mLimit;
    @SerializedName("recursive")
    private boolean mRecursive = true;
    @SerializedName("topicName")
    private String mStart;
    @SerializedName("type")
    private ListType mType;

    /**
     * Optionally set a limit on the returning topics.
     * @param limit A positive number.
     * @return This object.
     */
    public ListRequest setLimit(int limit) {
      if ((mLimit = limit) <= 0) {
        throw new IllegalArgumentException("Limit must be > 0");
      }
      return this;
    }

    /**
     * Get the specified limit.
     * @return A specified limit, or null.
     */
    public Integer getLimit() {
      return mLimit;
    }

    /**
     * Specify if the list is recursive down to its descendants.
     * @param recursive true for all descendants, false for the immediate children
     * @return This object.
     */
    public ListRequest setRecursive(boolean recursive) {
      mRecursive = recursive;
      return this;
    }

    public boolean isRecursive() {
      return mRecursive;
    }

    /**
     * Set the starting point.  Default is from the root.
     * @param start
     * @return This object.
     */
    public ListRequest setStart(String start) {
      mStart = start;
      return this;
    }

    public String getStart() {
      return mStart;
    }

    public ListType getType() {
      return mType;
    }

    public ListRequest setType(ListType type) {
      mType = type;
      return this;
    }

    public static ListRequest fromJson(String json) {
      return GsonData.getGson().fromJson(json, ListRequest.class);
    }
  }

  /**
   * @hide
   * Response payload for listing all topics.
   */
  public static class ListResponse extends ArrayList<TopicInfo> {
    private static final long serialVersionUID = -6398262288133254269L;

    /**
     * @hide
     */
    public ListResponse() {
      super();
    }

    /**
     * @hide
     * @param capacity
     */
    public ListResponse(int capacity) {
      super(capacity);
    }

    public static ListResponse fromJson(String json) {
      return GsonData.getGson().fromJson(json, ListResponse.class);
    }
  }

  /**
   * @hide
   */
  public static class SubscribeRequest extends JSONifiable {
    @SerializedName("userId")
    private String mUserId;
    @SerializedName("topicName")
    private String mTopic;
    @SerializedName("devId")
    private String mDevId;
    @SerializedName("errorOnDup")
    private boolean mErrorOnDup;

    /**
     * Subscription request for a global topic.
     * @param topic The topic name.
     * @param devId A device ID for this subscription or null for all devices.
     */
    public SubscribeRequest(String topic, String devId) {
      mTopic = topic;
      mDevId = devId;
    }

    /**
     * Subscription request for a personal topic.
     * @param userId The user ID of a personal topic.
     * @param topic The topic name.
     * @param devId A device ID for this subscription or null for all devices.
     */
    public SubscribeRequest(String userId, String topic, String devId) {
      mUserId = userId;
      mTopic = topic;
      mDevId = devId;
    }

    /**
     * Get the user ID of a personal topic.
     * @return A user ID of a personal topic, or null for global topic.
     */
    public String getUserId() {
      return mUserId;
    }

    /**
     * Get the topic name.
     * @return The topic name.
     */
    public String getTopic() {
      return mTopic;
    }

    /**
     * Get the device ID for this subscription.
     * @return A device ID for the subscription or null.
     */
    public String getDevId() {
      return mDevId;
    }

    /**
     * Check if MMX server should report an error for duplicated subscription.
     * @return false for not reporting, true for reporting an error.
     */
    public boolean isErrorOnDup() {
      return mErrorOnDup;
    }

    /**
     * Report an error if the subscription is a duplicate.  Default is false.
     * @param errorOnDup trie to report error; false otherwise.
     * @return This object.
     */
    public SubscribeRequest setErrorOnDup(boolean errorOnDup) {
      mErrorOnDup = errorOnDup;
      return this;
    }

    public static SubscribeRequest fromJson(String json) {
      return GsonData.getGson().fromJson(json, SubscribeRequest.class);
    }
  }

  /**
   * @hide
   */
  public static class SubscribeResponse extends JSONifiable {
    @SerializedName("subscriptionId")
    private String mSubId;
    @SerializedName("code")
    private int mCode;
    @SerializedName("msg")
    private String mMsg;

    /**
     * @hide
     * Default constructor.
     * @param subId The subscription ID.
     * @param code The status code.
     * @param msg A diagnostic message.
     */
    public SubscribeResponse(String subId, int code, String msg) {
      mSubId = subId;
      mCode = code;
      mMsg = msg;
    }

    /**
     * Get the subscription ID.
     * @return The subscription ID.
     */
    public String getSubId() {
      return mSubId;
    }

    /**
     * Get the status code.
     * @return The status code.
     */
    public int getCode() {
      return mCode;
    }

    /**
     * Get the diagnostic message.
     * @return The diagnostic message.
     */
    public String getMsg() {
      return mMsg;
    }

    public static SubscribeResponse fromJson(String json) {
      return GsonData.getGson().fromJson(json, SubscribeResponse.class);
    }
  }

  /**
   * @hide
   */
  public static class UnsubscribeRequest extends JSONifiable {
    @SerializedName("userId")
    private String mUserId;
    @SerializedName("topicName")
    private String mTopic;
    @SerializedName("subscriptionId")
    private String mSubId;

    /**
     * Constructor for unsubscribing a global topic.
     * @param topic The topic name.
     * @param subId A subscription ID or null for all subscriptions to the topic.
     */
    public UnsubscribeRequest(String topic, String subId) {
      mTopic = topic;
      mSubId = subId;
    }

    /**
     * Constructor for unsubscribing a personal topic.
     * @param userId The user ID of a personal topic.
     * @param topic The topic name.
     * @param subId A subscription ID or null for all subscriptions to the topic.
     */
    public UnsubscribeRequest(String userId, String topic, String subId) {
      mUserId = userId;
      mTopic = topic;
      mSubId = subId;
    }

    /**
     * Get the user ID of the personal topic.
     * @return The user ID of the personal topic, or null.
     */
    public String getUserId() {
      return mUserId;
    }

    /**
     * Get the topic name.
     * @return The topic name.
     */
    public String getTopic() {
      return mTopic;
    }

    /**
     * Get the subscription ID.
     * @return The subscription ID or null.
     */
    public String getSubId() {
      return mSubId;
    }

    public static UnsubscribeRequest fromJson(String json) {
      return GsonData.getGson().fromJson(json, UnsubscribeRequest.class);
    }
  }

  /**
   * @hide
   * Request payload to unsubscribe from all subscriptions for a device.
   */
  public static class UnsubscribeForDevRequest extends JSONifiable {
    @SerializedName("devId")
    private String mDevId;

    /**
     * Default constructor.
     * @param devId A non-null device ID.
     */
    public UnsubscribeForDevRequest(String devId) {
      mDevId = devId;
    }

    /**
     * The device ID for the unsubscription reuest.
     * @return The device ID.
     */
    public String getDevId() {
      return mDevId;
    }

    public static UnsubscribeForDevRequest fromJson(String json) {
      return GsonData.getGson().fromJson(json, UnsubscribeForDevRequest.class);
    }
  }

  /**
   * @hide
   * Request for the summary of a list of topics.
   */
  public static class SummaryRequest extends JSONifiable {
    @SerializedName("topicNodes")
    private List<MMXTopicId> mTopics;
    @SerializedName("since")
    private Date mSince;
    @SerializedName("until")
    private Date mUntil;

    public SummaryRequest(List<MMXTopicId> topics) {
      mTopics = topics;
    }

    public List<MMXTopicId> getTopicNodes() {
      return mTopics;
    }
    
    public Date getSince() {
      return mSince;
    }
    
    public SummaryRequest setSince(Date since) {
      mSince = since;
      return this;
    }
    
    public Date getUntil() {
      return mUntil;
    }
    
    public SummaryRequest setUntil(Date until) {
      mUntil = until;
      return this;
    }
    
    public static SummaryRequest fromJson(String json) {
      return GsonData.getGson().fromJson(json, SummaryRequest.class);
    }
  }

  /**
   * @hide
   * Response of the topic summary.
   */
  public static class SummaryResponse extends ArrayList<TopicSummary> {
    public SummaryResponse() {
      super();
    }

    public SummaryResponse(int capacity) {
      super(capacity);
    }
  }

  /**
   * Attributes for topic search.
   */
  public static class TopicSearch extends JSONifiable {
    @SerializedName("topicName")
    private SingleValue mDisplayName;
    @SerializedName("description")
    private SingleValue mDescription;
    @SerializedName("tags")
    private MultiValues mTags;

    /**
     * Get the searching topic name.
     * @return The topic name search value.
     */
    public String getTopicName() {
      return mDisplayName != null ? mDisplayName.getValue() : null;
    }

    /**
     * Get the match type of topic name.
     * @return
     */
    public Match getTopicNameMatch() {
      return mDisplayName != null ? mDisplayName.getMatch() : null;
    }

    /**
     * Set the search value for topic name using the default matching type.
     * @param topicName The topic name search value.
     * @return This object.
     */
    public TopicSearch setTopicName(String topicName) {
      return setTopicName(topicName, null);
    }

    /**
     * Specify the topic name to be searched.
     * @param topicName The topic name search value.
     * @param matchType The match type.
     * @return This object.
     */
    public TopicSearch setTopicName(String topicName, Match matchType) {
      mDisplayName = new SingleValue(topicName, matchType);
      return this;
    }

    /**
     * Get the searching topic description.
     * @return The description search value.
     */
    public String getDescription() {
      return mDescription != null ? mDescription.getValue() : null;
    }

    /**
     * Get the match type of description.
     * @return The description match type.
     */
    public Match getDescriptionMatch() {
      return mDescription != null ? mDescription.getMatch() : null;
    }

    /**
     * Set the search of description using the server default match.
     * @param description The description search value.
     * @return This object.
     */
    public TopicSearch setDescription(String description) {
      return setDescription(description, null);
    }

    /**
     * Specify the topic description to be searched.
     * @param description The description search value.
     * @param matchType The match type.
     * @return This object.
     */
    public TopicSearch setDescription(String description, Match matchType) {
      mDescription = new SingleValue(description, matchType);
      return this;
    }

    /**
     * Specify a list of tags to be searched.  Any matching values will be
     * considered as a match.
     * @return The tag search values.
     */
    public List<String> getTags() {
      return mTags != null ? mTags.getValues() : null;
    }

    /**
     * Get the match type of tags.
     * @return Always null.
     */
    public Match getTagsMatch() {
      return mTags != null ? mTags.getMatch() : null;
    }

    /**
     * Set the search values of topic tags using exact match in each value.
     * @param tags
     * @return This object.
     */
    public TopicSearch setTags(List<String> tags) {
      mTags = new MultiValues(tags, null);
      return this;
    }
  }

  /**
   * @hide
   * The topic search request protocol.
   */
  public static class TopicSearchRequest extends TopicSearch {
    @SerializedName("operator")
    private Operator mOperator;
    @SerializedName("offset")
    private int mOffset;
    @SerializedName("limit")
    private int mLimit;

    public TopicSearchRequest(Operator operator, TopicSearch attr, int offset,
                               int limit) {
      if (attr == null)
        throw new IllegalArgumentException("Search attribute cannot be null");
      if ((mOperator = operator) == null)
        throw new IllegalArgumentException("Operator cannot be null");
      if ((mLimit = limit) == 0)
        throw new IllegalArgumentException("Limit cannot be 0");
      setTopicName(attr.getTopicName());
      setDescription(attr.getDescription());
      setTags(attr.getTags());
    }

    public Operator getOperator() {
      return mOperator;
    }

    public int getOffset() {
      return mOffset;
    }

    public int getLimit() {
      return mLimit;
    }

    public static TopicSearchRequest fromJson(String json) {
      return GsonData.getGson().fromJson(json, TopicSearchRequest.class);
    }
  }

  /**
   * @hide
   * Topic search attributes.
   */
  public static enum TopicAttr {
    topicName,
    description,
  }

  /**
   * @hide
   * Request of the topic query.
   */
  public static class TopicQueryRequest extends JSONifiable {
    @SerializedName("criteria")
    private List<MMXAttribute<TopicAttr>> mCriteria = new ArrayList<MMXAttribute<TopicAttr>>();
    @SerializedName("offset")
    private int mOffset;
    @SerializedName("limit")
    private int mLimit;

    public TopicQueryRequest(List<MMXAttribute<TopicAttr>> criteria,
                              int offset, int limit) {
      if (criteria == null || criteria.size() != 1) {
        throw new IllegalArgumentException("Criteria must have one search type.");
      }
      mCriteria = criteria;
      mOffset = offset;
      mLimit = limit;
    }

    public List<MMXAttribute<TopicAttr>> getCriteria() {
      return mCriteria;
    }

    public int getLimit() {
      return mLimit;
    }

    public int getOffset() {
      return mOffset;
    }

    public static TopicQueryRequest fromJson(String json) {
      return GsonData.getGson().fromJson(json, TopicQueryRequest.class);
    }
  }

  /**
   * @hide
   * TopicInfo with subscription count.
   */
  public static class TopicInfoWithSubscriptionCount extends TopicInfo {
    private int subscriptionCount;
    /**
     * @hide
     * @param userId
     * @param topic
     * @param isCollection
     */
    public TopicInfoWithSubscriptionCount(String userId, String topic, boolean isCollection) {
      super(userId, topic, isCollection);
    }

    /**
     * Get the subscription count.
     * @return
     */
    public int getSubscriptionCount() {
      return subscriptionCount;
    }

    public TopicInfoWithSubscriptionCount setSubscriptionCount(int subscriptionCount) {
      this.subscriptionCount = subscriptionCount;
      return this;
    }
  }

  /**
   * Response of the topic search.
   */
  public static class TopicQueryResponse extends JSONifiable {
    @SerializedName("total")
    private int mTotal;
    @SerializedName("results")
    private List<TopicInfo> mResults;

    /**
     * @hide
     * @param total
     * @param results
     */
    public TopicQueryResponse(int total, List<TopicInfo> results) {
      mTotal = total;
      mResults = results;
    }

    /**
     * Return the total counts.  -1 if unknown.
     * @return
     */
    public int getTotal() {
      return mTotal;
    }

    /**
     * Get the result set.
     * @return
     */
    public List<TopicInfo> getResults() {
      return mResults;
    }

    /**
     * @hide
     * @param json
     * @return
     */
    public static TopicQueryResponse fromJson(String json) {
      return GsonData.getGson().fromJson(json, TopicQueryResponse.class);
    }
  }

  /**
   * Options on fetching the published items.
   */
  public static class FetchOptions implements Serializable {
    private static final long serialVersionUID = -1986734451547877221L;
    @SerializedName("subscriptionId")
    private String mSubId;
    @SerializedName("since")
    private Date mSince;
    @SerializedName("until")
    private Date mUntil;
    @SerializedName("ascending")
    private boolean mAscending;
    @SerializedName("maxItems")
    private int mMaxItems = -1;

    /**
     * Get an optional subscription ID.
     * @return Subscription ID, or null.
     */
    public String getSubId() {
      return mSubId;
    }

    /**
     * Set the subscription ID.
     * @param subId The subscription ID.
     * @return This object.
     */
    public FetchOptions setSubId(String subId) {
      mSubId = subId;
      return this;
    }

    /**
     * Get the start date/time of publishing date range.
     * @return The start date/time.
     */
    public Date getSince() {
      return mSince;
    }

    /**
     * Set the start date/time for the search of publishing date/time.
     * @param since The start date/time.
     * @return This object.
     */
    public FetchOptions setSince(Date since) {
      mSince = since;
      return this;
    }

    /**
     * Get the end date/time of publishing date range.
     * @return The end date/time.
     */
    public Date getUntil() {
      return mUntil;
    }

    /**
     * Set the end date/time for the search of publishing date/time.
     * @param until The end date/time.
     * @return This object.
     */
    public FetchOptions setUntil(Date until) {
      mUntil = until;
      return this;
    }

    /**
     * The sorting order of the published items.
     * @return true for ascending order; false for descending order.
     */
    public boolean isAscending() {
      return mAscending;
    }

    /**
     * Set the sort order of the published items.
     * @param ascending true for ascending (chronological order); false for descending order.
     * @return This object.
     */
    public FetchOptions setAscending(boolean ascending) {
      mAscending = ascending;
      return this;
    }

    /**
     * The max number of records to be returned.
     * @return Max number of records to be returned, -1 for using the server default.
     */
    public int getMaxItems() {
      return mMaxItems;
    }

    /**
     * Set the max number of records to be returned.
     * @param maxItems -1 for max records set by the server, or > 0.
     * @return This object.
     */
    public FetchOptions setMaxItems(int maxItems) {
      mMaxItems = maxItems;
      return this;
    }
  }

  /**
   * @hide
   * Request payload for fetching published items from a topic.
   */
  public static class FetchRequest extends JSONifiable {
    @SerializedName("userId")
    private String mUserId;
    @SerializedName("topicName")
    private String mTopic;
    @SerializedName("options")
    private FetchOptions mOptions;

    public FetchRequest(String userId, String topic, FetchOptions options) {
      mUserId = userId;
      mTopic = topic;
      mOptions = options;
    }

    public String getUserId() {
      return mUserId;
    }

    public String getTopic() {
      return mTopic;
    }

    public FetchOptions getOptions() {
      return mOptions;
    }

    public static FetchRequest fromJson(String json) {
      return GsonData.getGson().fromJson(json, FetchRequest.class);
    }
  }

  /**
   * @hide
   */
  public static class MMXPublishedItem {
    @SerializedName("itemId")
    private String mItemId;
    @SerializedName("publisher")
    private String mPublisher;
    @SerializedName("creationDate")
    private Date mCreationDate;
    @SerializedName("payloadXML")
    private String mPayloadXml;

    public MMXPublishedItem(String itemId, String publisher, Date creationDate,
                             String payloadXml) {
      mItemId = itemId;
      mPublisher = publisher;
      mCreationDate = creationDate;
      mPayloadXml = payloadXml;
    }

    public String getItemId() {
      return mItemId;
    }

    public String getPublisher() {
      return mPublisher;
    }

    public Date getCreationDate() {
      return mCreationDate;
    }

    public String getPayloadXml() {
      return mPayloadXml;
    }
  }

  /**
   * @hide
   * Response payload for fetching published items.
   */
  public static class FetchResponse extends JSONifiable {
    @SerializedName("userId")
    private String mUserId;
    @SerializedName("topicName")
    private String mTopic;
    @SerializedName("items")
    private List<MMXPublishedItem> mItems;

    /**
     * @hide
     * @param userId
     * @param topic
     * @param items
     */
    public FetchResponse(String userId, String topic, List<MMXPublishedItem> items) {
      mUserId = userId;
      mTopic = topic;
      mItems = items;
    }

    public String getUserId() {
      return mUserId;
    }

    public String getTopic() {
      return mTopic;
    }


    public List<MMXPublishedItem> getItems() {
      return mItems;
    }

    public static FetchResponse fromJson(String json) {
      return GsonData.getGson().fromJson(json, FetchResponse.class);
    }
  }
}
