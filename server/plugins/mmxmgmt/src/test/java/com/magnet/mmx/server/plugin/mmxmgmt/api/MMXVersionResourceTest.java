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
package com.magnet.mmx.server.plugin.mmxmgmt.api;

import com.google.gson.Gson;
import com.magnet.mmx.server.plugin.mmxmgmt.MMXVersion;
import com.magnet.mmx.server.plugin.mmxmgmt.servlet.BaseJAXRSTest;
import mockit.Mock;
import mockit.MockUp;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

/**
*/
public class MMXVersionResourceTest extends BaseJAXRSTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(MMXVersionResourceTest.class);
  private static final String baseUri = "http://localhost:8086/mmxmgmt/api/v1/mmx/version";
  private static final String testVersion = "testVersion";

  public MMXVersionResourceTest() {
    super(baseUri);
  }

  @Before
  public void setup() {
    setupMocks();
  }

  public void setupMocks() {
    new MockUp<MMXVersionResource>() {
      @Mock
      private Response getAuthResponse(HttpHeaders headers) {
        return null;
      }
    };

    new MockUp<MMXVersion>() {
      @Mock
      public String getVersion() {
        return testVersion;
      }
    };
  }

  @Test
  public void getVersion() {
    WebTarget getService = getClient().target(getBaseURI());
    String jsonString = getService.request().get(String.class);
    //String jsonString = response.getEntity(String.class);

    Gson gson = new Gson();

    MMXVersionResource.Version version = gson.fromJson(jsonString,  MMXVersionResource.Version.class);

    assertEquals(version.getVersion(), testVersion);


    LOGGER.trace("getVersion : response={}", jsonString);

  }
}
