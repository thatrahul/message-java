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
package com.magnet.mmx.server.plugin.mmxmgmt.db;

import com.magnet.mmx.server.api.v1.protocol.HookCreateRequest;
import com.magnet.mmx.server.plugin.mmxmgmt.hook.HookType;

import java.util.List;

public interface HookDAO {

  /**
   * Create a new hook using the appId and the hook create request
   * @param appId
   * @param details
   * @return id of the created hook
   * @throws DbInteractionException
   */
  int addNewHook(String appId, HookCreateRequest details) throws DbInteractionException;

  /**
   * Get list of hooks for specified appId and type
   * @param appId
   * @param hookType
   * @return
   * @throws DbInteractionException
   */
  List<HookEntity> getHooks(String appId, HookType hookType) throws DbInteractionException;

  /**
   * Get a hook using id
   * @param hookId
   * @return
   * @throws DbInteractionException
   */
  HookEntity getHook (int hookId) throws DbInteractionException;
}
