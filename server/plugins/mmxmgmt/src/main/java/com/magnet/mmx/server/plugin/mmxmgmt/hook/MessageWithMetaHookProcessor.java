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

import com.magnet.mmx.protocol.Constants;
import com.magnet.mmx.server.plugin.mmxmgmt.db.HookEntity;
import com.magnet.mmx.server.plugin.mmxmgmt.util.JIDUtil;
import com.magnet.mmx.server.plugin.mmxmgmt.util.MMXServerConstants;
import com.magnet.mmx.util.GsonData;
import com.magnet.mmx.util.JSONifiable;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageWithMetaHookProcessor implements HookProcessor {
  private static final Logger LOGGER = LoggerFactory.getLogger(MessageWithMetaHookProcessor.class);
  public static final String METHOD_POST = "POST";
  public static final String CONTENT_TYPE = "Content-Type";


  @Override
  public void process(HookEntity hook, HookContext context) {
    if (context instanceof MessageWithMetaHookContext) {
      processInternal(hook, (MessageWithMetaHookContext) context);
    }
  }


  protected void processInternal(HookEntity hook, MessageWithMetaHookContext context) {
    Element mmx = context.getMmxElement();
    Element meta = mmx.element(Constants.MMX_META);
    if (meta != null && !meta.getStringValue().isEmpty()) {
      String metaJSON = meta.getStringValue();
      Map<String, String> metaDictionary = GsonData.getGson().fromJson(metaJSON, Map.class);
      LOGGER.debug("Message meta dictionary:{}", metaDictionary);
      Map<String, String> eventConfig = hook.getEventConfig();
      boolean isApplicable = hookApplicable(metaDictionary, eventConfig);
      if (isApplicable) {
        MessageInfo info = new MessageInfo();
        Element payload = mmx.element(Constants.MMX_PAYLOAD);
        String content = payload.getStringValue();
        info.setContent(content);
        info.setDeviceId(context.getDeviceId());
        info.setMetadata(metaDictionary);
        info.setDeviceId(context.getDeviceId());
        info.setRecipientUsernames(Collections.singletonList(JIDUtil.getUserId(context.getToJID())));
        String jsonPayload = info.toJson();
        int responseCode = processHookPost(hook, context.getAppId(), jsonPayload);
        if (responseCode == 200) {
          LOGGER.info("hook information posted");
        } else {
          LOGGER.warn("Response code:{} when posting payload:{} to {}", responseCode, payload, hook.getTargetURL());
        }
      }
    }
  }


  protected static int processHookPost(HookEntity entity, String appId, String payload) {
    String targetURL = entity.getTargetURL();
    LOGGER.debug("Sending POST to " + targetURL);

    try {
      HttpURLConnection urlConnection = getConnection(targetURL);
      urlConnection.setDoOutput(true);
      urlConnection.setUseCaches(false);
      urlConnection.setRequestMethod(METHOD_POST);
      urlConnection.setRequestProperty(CONTENT_TYPE, MediaType.APPLICATION_JSON_TYPE.toString());
      urlConnection.setRequestProperty(MMXServerConstants.HTTP_HEADER_APP_ID, appId);
      OutputStream out = urlConnection.getOutputStream();
      OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new BufferedOutputStream(out), Constants.UTF8_CHARSET);
      outputStreamWriter.write(payload);
      outputStreamWriter.flush();
      outputStreamWriter.close();
      out.close();
      int responseCode = urlConnection.getResponseCode();
      urlConnection.disconnect();
      return responseCode;
    } catch (IOException e) {
      LOGGER.warn("IOException in posting to target URL", e);
      return -1;
    }
  }


  /**
   * Gets an {@link HttpURLConnection} given an URL.
   */
  protected static HttpURLConnection getConnection(String url) throws IOException {
    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
    return conn;
  }


  /**
   * Check if the message meta dictionary contains atleast one key value that matches corresponding key value in eventconfig
   *
   * @param messageMeta
   * @param eventConfig
   * @return
   */
  public static boolean hookApplicable(Map<String, String> messageMeta, Map<String, String> eventConfig) {
    if (messageMeta == null || eventConfig == null) {
      return false;
    }
    for (String key : eventConfig.keySet()) {
      String value = eventConfig.get(key);
      if (messageMeta.containsKey(key)) {
        if (messageMeta.get(key).equals(value)) {
          return true;
        }
      }
    }
    return false;
  }


  private static class MessageInfo extends JSONifiable {
    private List<String> recipientUsernames;
    private String deviceId;
    private String content;
    private Map<String, String> metadata = new HashMap<String, String>();

    public String getContent() {
      return content;
    }

    public MessageInfo setContent(String content) {
      this.content = content;
      return this;
    }

    public String getDeviceId() {
      return deviceId;
    }

    public MessageInfo setDeviceId(String deviceId) {
      this.deviceId = deviceId;
      return this;
    }

    public Map<String, String> getMetadata() {
      return metadata;
    }

    public MessageInfo setMetadata(Map<String, String> metadata) {
      this.metadata = metadata;
      return this;
    }

    public List<String> getRecipientUsernames() {
      return recipientUsernames;
    }

    public MessageInfo setRecipientUsernames(List<String> recipientUsernames) {
      this.recipientUsernames = recipientUsernames;
      return this;
    }
  }

}
