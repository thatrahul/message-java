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
package com.magnet.mmx.server.plugin.mmxmgmt.util;

import com.magnet.mmx.protocol.PushType;
import com.magnet.mmx.server.common.data.AppEntity;
import com.magnet.mmx.server.plugin.mmxmgmt.db.*;
import com.magnet.mmx.server.plugin.mmxmgmt.push.MMXPushAPNSPayloadBuilder;
import com.magnet.mmx.server.plugin.mmxmgmt.push.MMXPushGCMPayloadBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class WakeupUtil {
  private static final Logger LOGGER = LoggerFactory.getLogger(WakeupUtil.class);

  public static void queueWakeup(AppEntity appEntity, DeviceEntity deviceEntity, String messageId) {
    LOGGER.trace("queueWakeup : messageId={}", messageId);
    WakeupEntity wakeupEntity = new WakeupEntity();
    wakeupEntity.setToken(deviceEntity.getClientToken());
    wakeupEntity.setDeviceId(deviceEntity.getDeviceId());
    wakeupEntity.setType(deviceEntity.getTokenType());
    wakeupEntity.setSenderIdentifier(appEntity.getGoogleAPIKey());

    /**
     * Set the payload based on the Device type.
     * The wakeup payloads are different for iOS and Android devices
     */
    if (deviceEntity.getTokenType() == PushType.APNS) {
      String payload = MMXPushAPNSPayloadBuilder.wakeupPayload();
      wakeupEntity.setPayload(payload);
    } else if (deviceEntity.getTokenType() == PushType.GCM) {
      String payload = MMXPushGCMPayloadBuilder.wakeupPayload();
      wakeupEntity.setPayload(payload);
    }
    wakeupEntity.setMessageId(messageId);
    wakeupEntity.setAppId(appEntity.getAppId());
    WakeupEntityDAO wakeupEntityDAO = DBUtil.getWakeupEntityDAO();
    wakeupEntityDAO.offer(wakeupEntity);
  }
}
