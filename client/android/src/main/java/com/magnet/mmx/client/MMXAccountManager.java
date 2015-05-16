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
package com.magnet.mmx.client;

import android.os.Handler;

import com.magnet.mmx.client.common.AccountManager;
import com.magnet.mmx.client.common.MMXException;
import com.magnet.mmx.protocol.MMXStatus;
import com.magnet.mmx.protocol.SearchAction;
import com.magnet.mmx.protocol.UserInfo;
import com.magnet.mmx.protocol.UserQuery;
import com.magnet.mmx.protocol.UserTags;

import java.util.List;

/**
 * Account Manager allows user to change the password, update the display
 * name and email address, and query for users.
 */
public class MMXAccountManager extends MMXManager {
  private static final String TAG = MMXAccountManager.class.getSimpleName();
  private AccountManager mAccountManager = null;

  MMXAccountManager(MMXClient mmxClient, Handler handler) {
    super(mmxClient, handler);
    onConnectionChanged();
  }

  /**
   * Add tags to the current user.  The tags must not be null or empty, and
   * each tag cannot be longer than 25 characters; otherwise MMXException with
   * BAD_REQUEST status code will be thrown.
   *
   * @param tags the tags to add for the current user
   * @return the status of the call
   * @throws MMXException
   */
  public MMXStatus addTags(List<String> tags) throws MMXException {
    checkDestroyed();
    return mAccountManager.addTags(tags);
  }

  /**
   * Change the password for the current authenticated user.
   *
   * @param newPassword the new password for the user
   * @throws MMXException
   */
  public void	changePassword(String newPassword) throws MMXException {
    checkDestroyed();
    mAccountManager.changePassword(newPassword);
  }

  /**
   * Get the tags for the current user.
   *
   * @return the user tags for the current user
   * @throws MMXException
   */
  public UserTags getAllTags() throws MMXException {
    checkDestroyed();
    return mAccountManager.getAllTags();
  }

  /**
   * Get the account information of the current user.
   *
   * @return the user info for the current user
   * @throws MMXException
   */
  public UserInfo getUserInfo() throws MMXException {
    checkDestroyed();
    return mAccountManager.getUserInfo();
  }

  /**
   * Remove tags from the current user.  The tags must not be null or empty, and
   * each tag cannot be longer than 25 characters; otherwise MMXException with
   * BAD_REQUEST status code will be thrown.
   *
   * @param tags the tags to remove
   * @return the status for this request
   * @throws MMXException
   */
  public MMXStatus removeTags(List<String> tags) throws MMXException {
    checkDestroyed();
    return mAccountManager.removeTags(tags);
  }

  /**
   * Set the tags for the current user.  If the list is null or empty, all tags
   * will be removed.
   *
   * @param tags A list of tags, or null.
   * @return The status.
   * @throws MMXException
   */
  public MMXStatus setAllTags(List<String> tags) throws MMXException {
    checkDestroyed();
    return mAccountManager.setAllTags(tags);
  }

  /**
   * Search for users with the matching attributes.
   *
   * @param operator the operator for this search
   * @param attributes the attributes for this search
   * @param maxRows the maximum number of rows to return
   * @return the users matching the search criteria
   * @throws MMXException
   */
  public UserQuery.Response	searchBy(SearchAction.Operator operator,
                                      UserQuery.Search attributes, Integer maxRows) throws MMXException {
    checkDestroyed();
    return mAccountManager.searchBy(operator, attributes, maxRows);
  }

  /**
   * Update the current user's account info.  The userId in <code>info</code>
   * cannot be changed and it is ignored.
   *
   * @param info the updated information
   * @return the status for the current operation
   * @throws MMXException
   */
  public MMXStatus updateAccount(UserInfo info) throws MMXException {
    checkDestroyed();
    return mAccountManager.updateAccount(info);
  }

  @Override
  void onConnectionChanged() {
    mAccountManager = AccountManager.getInstance(getMMXClient().getMMXConnection());
  }
}
