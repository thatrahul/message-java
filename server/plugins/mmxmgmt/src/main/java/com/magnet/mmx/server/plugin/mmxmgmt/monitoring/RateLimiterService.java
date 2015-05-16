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
package com.magnet.mmx.server.plugin.mmxmgmt.monitoring;

import com.google.common.util.concurrent.RateLimiter;
import com.magnet.mmx.server.plugin.mmxmgmt.util.MMXConfigKeys;
import com.magnet.mmx.server.plugin.mmxmgmt.util.MMXConfiguration;
import com.magnet.mmx.server.plugin.mmxmgmt.util.MMXServerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class RateLimiterService {
  private static RateLimiter inAppMsgLimiter = null;
  private static RateLimiter pushMsgRateLimiter = null;
  private static boolean initialized = false;
  private static final Logger LOGGER = LoggerFactory.getLogger(RateLimiterService.class);

  public static synchronized void init() {
    int inAppRate = MMXConfiguration.getConfiguration().getInt(MMXConfigKeys.MAX_INAPP_MESSAGE_RATE, MMXServerConstants.DEFAULT_MAX_INAPP_MSG_RATE);
    if(inAppRate > 0)
      inAppMsgLimiter = RateLimiter.create(inAppRate);

    int pushRate = MMXConfiguration.getConfiguration().getInt(MMXConfigKeys.MAX_PUSH_MESSAGE_RATE, MMXServerConstants.DEFAULT_MAX_PUSH_MSG_RATE);
    if(pushRate > 0)
      pushMsgRateLimiter = RateLimiter.create(pushRate);
  }

  public static boolean getInAppPermit() {
    if(inAppMsgLimiter != null)
      return inAppMsgLimiter.tryAcquire();
    return true;
  }

  public static boolean getPushPermit() {
    if(pushMsgRateLimiter != null)
      return pushMsgRateLimiter.tryAcquire();
    return true;
  }

  public synchronized static void setInAppMsgRate(int rate) {
    if(rate > 0) {
      LOGGER.trace("setInAppMsgRate : rate={}", rate);
      if(inAppMsgLimiter == null)
        inAppMsgLimiter = RateLimiter.create(rate);
      else
        inAppMsgLimiter.setRate(rate);
    } else {
      inAppMsgLimiter = null;
    }
  }

  public synchronized static void setPushMsgRate(int rate) {
    if(rate > 0) {
      LOGGER.trace("setPushMsgRate : rate={}", rate);
      if(pushMsgRateLimiter == null)
        pushMsgRateLimiter = RateLimiter.create(rate);
      else
        pushMsgRateLimiter.setRate(rate);
    } else {
      pushMsgRateLimiter = null;
    }
  }
}
