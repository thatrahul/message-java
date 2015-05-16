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

import java.util.HashMap;
import java.util.Map;

import com.magnet.mmx.util.TypeMapper;

/**
 * @hide
 * Implement the built-in MMX type and class mapper.  Currently it supports the
 * ping/pong and notification message types to their corresponding Java classes.
 * Typical usage is:
 * <pre>
 *  String msgType = message.getPayload().getMsgType();
 *  Class<MyPayloadData> payloadClz = MMXTypeMapper.getInstance().getClassByType(msgType);
 * </pre>
 */
public class MMXTypeMapper extends TypeMapper {
  private final static Map<String, String> sMap = new HashMap<String, String>() {{
    put(Constants.PingPongCommand.ping.toString(), PingPong.class.getName());
    put(Constants.PingPongCommand.pong.toString(), PingPong.class.getName());
    put(Constants.PingPongCommand.pingpong.toString(), PingPong.class.getName());
    put(Constants.PingPongCommand.notify.toString(), Notification.class.getName());
    put(Constants.PingPongCommand.retrieve.toString(), Void.class.getName());
    put(MMXError.getType(), MMXError.class.getName());
    put(GeoLoc.getType(), GeoLoc.class.getName());
  }};
  
  private static MMXTypeMapper sInstance = new MMXTypeMapper();

  private MMXTypeMapper() {
    super();
  }
  
  protected String getClassNameByType(String type) {
    return sMap.get(type);
  }
  
  public static MMXTypeMapper getInstance() {
    return sInstance;
  }
}
