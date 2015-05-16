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
package com.magnet.mmx.server.plugin.mmxmgmt.servlet;

import com.google.common.base.Strings;
import com.google.gson.GsonBuilder;
import com.magnet.mmx.server.plugin.mmxmgmt.util.MMXUserInfo;
import com.magnet.mmx.server.plugin.mmxmgmt.util.ServerNotInitializedException;
import com.magnet.mmx.server.plugin.mmxmgmt.util.UserManagerService;
import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.jivesoftware.openfire.user.UserAlreadyExistsException;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
*/
//TODO: This needs refactoring to using JAXRS API for invoking the endpoints
@Ignore
@RunWith(JMockit.class)
public class MMXUserResourceTest extends BaseJAXRSTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(MMXUserResourceTest.class);
  private static final String baseUri = "http://localhost:8888/plugins/mmxmgmt/rest/v1/user";

  private boolean throwServerNotInitializedException = false;
  private boolean throwUserNotFoundException = false;

  private static ConcurrentHashMap<String, MMXUserInfo> map = new ConcurrentHashMap<String, MMXUserInfo>();
  private static ArrayList<String> adminList = new ArrayList<String>();

  public MMXUserResourceTest() {
    super(baseUri);
  }

  @Before
  public void setup() {
    setupMocks();
    map.clear();
    resetFlags();
  }

  public void setupMocks() {
    new MockUp<UserManagerService>() {
      @Mock
      public void createUser(MMXUserInfo userInfo) throws UserAlreadyExistsException, ServerNotInitializedException {
        LOGGER.trace("mockCreateUser : createUser called userInfo={}", userInfo);
        if(Strings.isNullOrEmpty(userInfo.getUsername()))
          throw new IllegalArgumentException("Illegal username");
        if(Strings.isNullOrEmpty(userInfo.getPassword()))
          throw new IllegalArgumentException("Illegal password");
        if(throwServerNotInitializedException)
          throw new ServerNotInitializedException();
        if(map.containsKey(userInfo.getMMXUsername())) {
          throw new UserAlreadyExistsException();
        }
        map.put(userInfo.getMMXUsername(), userInfo);
        if(userInfo.getIsAdmin() != null && userInfo.getIsAdmin() == true) {
          adminList.add(userInfo.getMMXUsername());
        }
      }

      @Mock
      public boolean updateUser(MMXUserInfo userInfo)  throws ServerNotInitializedException, UserNotFoundException  {
        LOGGER.trace("updateUser : update user called userInfo={}", userInfo);
        if(throwServerNotInitializedException)
          throw new ServerNotInitializedException();
        if(throwUserNotFoundException)
          throw new UserNotFoundException();

        MMXUserInfo info = map.get(userInfo.getMMXUsername());
        if(info == null) {
          map.put(userInfo.getMMXUsername(), userInfo);
          if(userInfo.getIsAdmin() != null && userInfo.getIsAdmin() == true) {
            adminList.add(userInfo.getMMXUsername());
          }
          return true;
        }
        MMXUserInfo userInfo2 = from(info);
        String password = userInfo.getPassword();
        String name = userInfo.getName();
        String email = userInfo2.getEmail();
        Boolean isAdmin = userInfo.getIsAdmin();
        if (password != null) userInfo2.setPassword(password);
        if (name != null) userInfo2.setName(name);
        if (email != null) userInfo2.setEmail(email);
        if(isAdmin != null && isAdmin == true) {
          userInfo2.setIsAdmin(true);
          adminList.add(userInfo2.getMMXUsername());
        }
        else {
          userInfo2.setIsAdmin(false);
          adminList.remove(userInfo2.getMMXUsername());
        }
        map.put(userInfo2.getMMXUsername(), userInfo2);
        return false;
      }

      @Mock
      public void deleteUser(MMXUserInfo userInfo) throws UserNotFoundException, ServerNotInitializedException {
        LOGGER.trace("deleteUser : mock delete user called userInfo={}", userInfo);
        if(throwServerNotInitializedException)
          throw new ServerNotInitializedException();
        if(throwUserNotFoundException)
          throw new UserNotFoundException();
        MMXUserInfo userInfo2 = map.get(userInfo.getMMXUsername());
        if(userInfo2 == null)
          throw new UserNotFoundException();
        else {
          map.remove(userInfo2.getMMXUsername());
          if(adminList.contains(userInfo2.getMMXUsername())) {
            adminList.remove(userInfo2.getMMXUsername());
          }
        }
      }
    };
  }

  @Test
  public void testCreateUser() {
//    WebResource service = getClient().resource(getBaseURI());
//    String userInfoStr = getUserJsonString();
//    LOGGER.trace("testCreateUser : userInfo=\n{}", userInfoStr);
//    ClientResponse resp = service.type("application/json").post(ClientResponse.class, userInfoStr);
//    LOGGER.trace("testCreateUser : resp = {}", resp.getStatus());
//    assertEquals(201, resp.getStatus());
//    ClientResponse resp2 = service.type("application/json").post(ClientResponse.class, userInfoStr);
//    LOGGER.trace("testCreateUser : resp = {}", resp.getStatus());
//    assertEquals(409, resp2.getStatus());
//    String userInfo2 = getUserJsonString();
//    ClientResponse resp3 = service.type("application/json").post(ClientResponse.class, userInfo2);
//    assertEquals(201, resp3.getStatus());
//    ClientResponse resp4 = service.type("application/json").post(ClientResponse.class, userInfo2);
//    assertEquals(409, resp4.getStatus());
//
//    MMXUserInfo userInfo = getUserInfo(false);
//    userInfo.setUsername(null);
//    String userInfoStr2 = getUserInfoJsonString(userInfo);
//    ClientResponse resp6 = service.type("application/json").post(ClientResponse.class, userInfoStr2);
//    assertEquals(400, resp6.getStatus());
//
//    userInfo = getUserInfo(false);
//    userInfo.setPassword("");
//    String userInfoStr3 = getUserInfoJsonString(userInfo);
//    ClientResponse resp7 = service.type("application/json").post(ClientResponse.class, userInfoStr3);
//    assertEquals(400, resp7.getStatus());
//
//    throwServerNotInitializedException = true;
//    ClientResponse resp5 = service.type("application/json").post(ClientResponse.class, userInfoStr);
//    assertEquals(500, resp5.getStatus());
  }

  @Test
  public void testUpdateUser() {
//    WebResource service = getClient().resource(getBaseURI());
//    MMXUserInfo info = getUserInfo(false);
//    String userInfo = getUserInfoJsonString(info);
//    LOGGER.trace("testUpdateUser : userInfo=\n{}", userInfo);
//    ClientResponse resp = service.type("application/json").put(ClientResponse.class, userInfo);
//    LOGGER.trace("testUpdateUser : resp = {}", resp.getStatus());
//    assertEquals(201, resp.getStatus());
//
//    info.setName("AA BB");
//    userInfo = getUserInfoJsonString(info);
//
//    ClientResponse resp2 = service.type("application/json").put(ClientResponse.class, userInfo);
//    LOGGER.trace("testUpdateUser : resp = {}", resp.getStatus());
//    assertEquals(200, resp2.getStatus());
//
//    String userInfo2 = getUserJsonString();
//    ClientResponse resp3 = service.type("application/json").post(ClientResponse.class, userInfo2);
//    assertEquals(201, resp3.getStatus());
//
//    throwServerNotInitializedException = true;
//    ClientResponse resp5 = service.type("application/json").post(ClientResponse.class, userInfo);
//    assertEquals(500, resp5.getStatus());
  }

  @Test
  public void testDeleteUser() {
//    WebResource service = getClient().resource(getBaseURI());
//    MMXUserInfo userInfo = getUserInfo(false);
//    String userInfoStr = getUserInfoJsonString(userInfo);
//    LOGGER.trace("testDeleteUser : userInfoStr=\n{}", userInfoStr);
//    ClientResponse resp = service.type("application/json").put(ClientResponse.class, userInfo);
//    LOGGER.trace("testDeleteUser : service={}",service);
//    assertEquals(201, resp.getStatus());
//
//    ClientResponse resp2 = service.path(userInfo.getUsername() + "/app/" + userInfo.getAppId()).type("application/json").delete(ClientResponse.class);
//    assertEquals(200, resp2.getStatus());
//
//    ClientResponse resp3 = service.path(userInfo.getUsername() + "/app/" + userInfo.getAppId()).type("application/json").delete(ClientResponse.class);
//    assertEquals(400, resp3.getStatus());
//
//    throwServerNotInitializedException = true;
//
//    ClientResponse resp4 = service.path(userInfo.getUsername() + "/app/" + userInfo.getAppId()).type("application/json").delete(ClientResponse.class);
//    assertEquals(500, resp4.getStatus());
//
//    throwServerNotInitializedException = false;
//
//    userInfo = getUserInfo(true);
//    userInfoStr = getUserInfoJsonString(userInfo);
//    LOGGER.trace("testDeleteUser : userInfoStr=\n{}", userInfoStr);
//    resp = service.type("application/json").put(ClientResponse.class, userInfo);
//    LOGGER.trace("testDeleteUser : service={}",service);
//    assertEquals(201, resp.getStatus());
//    assertEquals(true, adminList.contains(userInfo.getMMXUsername()));
//    resp2 = service.path(userInfo.getUsername() + "/app/" + userInfo.getAppId()).type("application/json").delete(ClientResponse.class);
//    assertEquals(200, resp2.getStatus());
//    assertEquals(false, adminList.contains(userInfo.getMMXUsername()));
  }

  private String getUserJsonString() {
    MMXUserInfo userInfo = new MMXUserInfo();
    userInfo.setAppId(RandomStringUtils.randomAlphabetic(10));
    userInfo.setEmail(RandomStringUtils.randomAlphabetic(5) + "@magnet.com");
    userInfo.setName(RandomStringUtils.randomAlphabetic(5) + " " + RandomStringUtils.randomAlphabetic(5));
    userInfo.setUsername(RandomStringUtils.randomAlphabetic(10));
    userInfo.setPassword(RandomStringUtils.randomAlphanumeric(4 + RandomUtils.nextInt(6)));
    return new GsonBuilder().setPrettyPrinting().create().toJson(userInfo);
  }

  private String getUserInfoJsonString(MMXUserInfo userInfo) {
    return new GsonBuilder().setPrettyPrinting().create().toJson(userInfo);
  }

  private MMXUserInfo getUserInfo(boolean isAdmin) {
    MMXUserInfo userInfo = new MMXUserInfo();
    userInfo.setAppId(RandomStringUtils.randomAlphabetic(10));
    userInfo.setEmail(RandomStringUtils.randomAlphabetic(5) + "@magnet.com");
    userInfo.setName(RandomStringUtils.randomAlphabetic(5) + " " + RandomStringUtils.randomAlphabetic(5));
    userInfo.setUsername(RandomStringUtils.randomAlphabetic(10));
    userInfo.setPassword(RandomStringUtils.randomAlphanumeric(4 + RandomUtils.nextInt(6)));
    userInfo.setIsAdmin(isAdmin);
    return userInfo;
  }

  private MMXUserInfo from(MMXUserInfo userInfo) {
    String appId = userInfo.getAppId();
    String password = userInfo.getPassword();
    String email = userInfo.getEmail();
    String name = userInfo.getName();
    String username = userInfo.getUsername();

    MMXUserInfo userInfo2 = new MMXUserInfo();
    userInfo2.setAppId(appId);
    userInfo2.setEmail(email);
    userInfo2.setName(name);
    userInfo2.setUsername(username);
    userInfo2.setPassword(password);
    return userInfo2;
  }

  private void resetFlags() {
    throwUserNotFoundException = false;
    throwServerNotInitializedException = false;
  }
}
