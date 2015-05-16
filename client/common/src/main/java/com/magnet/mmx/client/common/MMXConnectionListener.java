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

package com.magnet.mmx.client.common;

public interface MMXConnectionListener {
  /**
   * Connection and authentication to the server is done.  It may be the first
   * connection or any subsequent reconnection.
   */
  public void onConnectionEstablished();
  
  /**
   * Connection to the server is closed unexpectedly.
   */
  public void onConnectionClosed();
  
  /**
   * Fail connecting to the server, or there is an authentication error.
   */
  public void onConnectionFailed(Exception cause);
  
  /**
   * A user is authenticated.
   * @param user
   */
  public void onAuthenticated(String user);
  
  /**
   * Authentication failure.
   * @param user
   */
  public void onAuthFailed(String user);
  
  /**
   * The user account is created automatically and successfully.
   * @param user
   */
  public void onAccountCreated(String user);
}
