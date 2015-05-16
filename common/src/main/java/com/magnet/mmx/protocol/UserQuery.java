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

import java.util.List;

/**
 * Request and response for user search.
 */
public class UserQuery {  
  /**
   * @hide
   */
  public static class SearchRequest extends Search {
    @SerializedName("operator")
    private Operator mOperator;
    @SerializedName("offset")
    private int mOffset;
    @SerializedName("limit")
    private int mLimit;
    
    public SearchRequest(Operator operator, Search attr, int offset, int limit) {
      if (attr == null)
        throw new IllegalArgumentException("Search attribute cannot be null");
      if ((mOperator = operator) == null)
        throw new IllegalArgumentException("Operator cannot be null");
      if ((mOffset = offset) < 0)
        throw new IllegalArgumentException("Offset cannot be less than 0");
      if ((mLimit = limit) == 0)
        throw new IllegalArgumentException("Limit cannot be 0");
      setDisplayName(attr.getDisplayName());
      setEmail(attr.getEmail());
      setPhone(attr.getPhone());
      setTags(attr.getTags());
    }
    
    public Operator getOperator() {
      return mOperator;
    }

    public int getLimit() {
      return mLimit;
    }

    public int getOffset() {
      return mOffset;
    }

    public static SearchRequest fromJson(String json) {
      return GsonData.getGson().fromJson(json, SearchRequest.class);
    }
  }
  
  /**
   * The attributes for user search.
   */
  public static class Search extends JSONifiable {
    @SerializedName("displayName")
    private SingleValue mDisplayName;
    @SerializedName("email")
    private SingleValue mEmail;
    @SerializedName("phone")
    private SingleValue mPhone;
    @SerializedName("tags")
    private MultiValues mTags;

    /**
     * Get the search value of display name.
     * @return The display name search value.
     */
    public String getDisplayName() {
      return mDisplayName!= null ? mDisplayName.getValue() : null;
    }
    
    public Match getDisplayNameMatch() {
      return mDisplayName!= null ? mDisplayName.getMatch():null;
    }
    /**
     * Set the search of display name using the default matching type defined by
     * the server.
     * @param displayName A display name for search.
     * @return This object.
     */
    public Search setDisplayName(String displayName) {
      return setDisplayName(displayName, null);
    }
    /**
     * Set the search of display name using a match type.
     * @param displayName A display name for search.
     * @param matchType A match type, or null to use server default.
     * @return This object.
     */
    public Search setDisplayName(String displayName, Match matchType) {
      mDisplayName = new SingleValue(displayName, matchType);
      return this;
    }
    /**
     * Get the search value of email.
     * @return The email search value.
     */
    public String getEmail() {
      return mEmail != null ? mEmail.getValue() : null;
    }
    
    public Match getEmailMatch() {
      return mEmail != null ? mEmail.getMatch() : null;
    }
    /**
     * Set the search of email using the default matching type defined by the
     * server.
     * @param email An email address.
     * @return This object.
     */
    public Search setEmail(String email) {
      return setEmail(email, null);
    }
    /**
     * Set the search of email using a match type.
     * @param email An email address.
     * @param matchType A match type, or null to use server default.
     * @return
     */
    public Search setEmail(String email, Match matchType) {
      mEmail = new SingleValue(email, matchType);
      return this;
    }
    public String getPhone() {
      return mPhone != null ? mPhone.getValue() : null;
    }
    public Match getPhoneMatch() {
      return mPhone.getMatch();
    }
    /**
     * Use the prefix wild-card for phone number search.
     * @param phone
     * @return
     */
    public Search setPhone(String phone) {
      mPhone = new SingleValue(phone, null);
      return this;
    }
    /**
     * Get the search values of tags.
     * @return The tag search values.
     */
    public List<String> getTags() {
      return mTags != null ? mTags.getValues() : null;
    }
    public Match getTagsMatch() {
      return mTags != null ? mTags.getMatch() : null;
    }
    /**
     * Use the exact match in each tag of any existing multi-value tags.
     * @param tags
     * @return
     */
    public Search setTags(List<String> tags) {
      mTags = new MultiValues(tags, null);
      return this;
    }
    
    /**
     * @hide
     */
    public static Search fromJson(String json) {
      return GsonData.getGson().fromJson(json, Search.class);
    }
  }
  
  /**
   * @hide
   * Search attribute for user.
   */
  public static enum Type {
    /**
     * The user ID.
     */
    userId,
    /**
     * The display name.
     */
    displayName,
    /**
     * The email address.
     */
    email,
    /**
     * The phone number.
     */
    phone,
  }

  /**
   * @hide
   * The request of the users query.  Multiple users can be searched in a single
   * request.  If limit is not specified, no max results will be imposed.
   * Typically each element in the list of criteria corresponds to a person in
   * the Contacts list.  However, the caller may specify multiple criteria for
   * the same person, but it will be the caller's responsibility to resolve
   * duplicated users.
   */
  public static class BulkSearchRequest extends JSONifiable {
    @SerializedName("criteria")
    private List<MMXAttribute<Type>> mCriteria;
    @SerializedName("limit")
    private Integer mLimit;

    public BulkSearchRequest(List<MMXAttribute<Type>> criteria, Integer limit) {
      mCriteria = criteria;
      mLimit = limit;
    }
    
    /**
     * Get the search criteria for a list of people.
     * @return
     */
    public List<MMXAttribute<Type>> getCriteria() {
      return mCriteria;
    }

    /**
     * Get the specified limit.
     * @return null if no limit is specified, or a specified limit.
     */
    public Integer getLimit() {
      return mLimit;
    }

    /**
     * @hide
     * @param json
     * @return
     */
    public static BulkSearchRequest fromJson(String json) {
      return GsonData.getGson().fromJson(json, BulkSearchRequest.class);
    }
  }
  
  /**
   * The result from the user search.
   */
  public static class Response extends JSONifiable {
    @SerializedName("totalCount")
    private int mTotalCount;
    @SerializedName("users")
    private List<UserInfo> mUsers;
    
    /**
     * @hide
     * Set the number of found entries.
     * @param totalCount
     * @return
     */
    public Response setTotalCount(int totalCount) {
      mTotalCount = totalCount;
      return this;
    }

    /**
     * @hide
     * Set the found user list.
     * @param users
     * @return
     */
    public Response setUsers(List<UserInfo> users) {
      mUsers = users;
      return this;
    }

    /**
     * Get the matched count.  It may exceed the limit.
     * @return The total matched count.
     */
    public int getTotalCount() {
      return mTotalCount;
    }

    /**
     * Get the matched users.
     * @return A list of matched users.
     */
    public List<UserInfo> getUsers() {
      return mUsers;
    }

    /**
     * @hide
     * @param json
     * @return
     */
    public static Response fromJson(String json) {
      return GsonData.getGson().fromJson(json, Response.class);
    }
  }
}
