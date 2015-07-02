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
package com.magnet.mmx.server.plugin.mmxmgmt.hook;

import com.magnet.mmx.server.plugin.mmxmgmt.db.HookEntity;

/**
 * HookProcessor defines the interface for processing Hooks.
 */
public interface HookProcessor {

  /**
   * API for processing the hook. HookContext should provide all the information for processing the Hook
   * defined by the HookEntity.
   * @param hook
   * @param context
   */
  void process(HookEntity hook, HookContext context);

}
