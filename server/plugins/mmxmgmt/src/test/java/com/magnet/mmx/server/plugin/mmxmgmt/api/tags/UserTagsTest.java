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
package com.magnet.mmx.server.plugin.mmxmgmt.api.tags;

import com.cedarsoftware.util.io.JsonWriter;
import com.magnet.mmx.server.plugin.mmxmgmt.util.BaseJerseyJacksonSerializationTest;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 */
public class UserTagsTest extends BaseJerseyJacksonSerializationTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserTagsTest.class);

  @Test
  public void testSerializeThenDeSerialize() throws Exception {
    UserTagMap tagMap = new UserTagMap();

    tagMap.addTag("user1", "tag1");
    tagMap.addTag("user1", "tag2");
    tagMap.addTag("user1", "tag3");
    tagMap.addTag("user1", "tag4");
    tagMap.addTag("user2", "tag1");
    tagMap.addTag("user2", "tag2");
    tagMap.addTag("user2", "tag3");
    tagMap.addTag("user2", "tag4");

    String marshalledString = toJsonString(tagMap.getTagInfoList());
    LOGGER.trace("testSerializeThenSerialize : marshalledString=\n{}", JsonWriter.formatJson(marshalledString));
    Object obj = fromJson(marshalledString, new TypeReference<List<UserTagInfo>>(){});
    LOGGER.trace("testSerializeThenSerialize : unmarshalled object={}", obj);
    List<UserTagInfo> unmarshalledTags = ( List<UserTagInfo>) obj;
    assertEquals(tagMap.getTagInfoList(), unmarshalledTags);
  }

  @Test
  public void testDeSerializeThenSerialize() throws Exception {
    String jsonString = "[\n" +
            "    {\n" +
            "      \"username\":\"user2\"\n," +
            "      \"tags\":[\n" +
            "        \"tag1\",\n" +
            "        \"tag2\",\n" +
            "        \"tag3\",\n" +
            "        \"tag4\"\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"username\":\"user1\"\n,"+
            "      \"tags\":[\n" +
            "        \"tag1\",\n" +
            "        \"tag2\",\n" +
            "        \"tag3\",\n" +
            "        \"tag4\"\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n";

    Object obj = fromJson(jsonString, new TypeReference<List<UserTagInfo>>(){});
    LOGGER.trace("testSerializeThenSerialize : unmarshalled object={}", obj);
    List<UserTagInfo> unmarshalledTags = (List<UserTagInfo>) obj;
    LOGGER.trace("testDeSerializeThenSerialize : unmarshalledTags={}", unmarshalledTags);
    String marshalledString = toJsonString(unmarshalledTags);
    assertEquals(JsonWriter.formatJson(jsonString), JsonWriter.formatJson(marshalledString));
  }
}
