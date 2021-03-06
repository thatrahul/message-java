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

package com.magnet.mmx.client;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.magnet.mmx.client.common.MMXConnection;
import com.magnet.mmx.client.common.MMXErrorMessage;
import com.magnet.mmx.client.common.MMXException;
import com.magnet.mmx.client.common.MMXGlobalTopic;
import com.magnet.mmx.client.common.MMXMessage;
import com.magnet.mmx.client.common.MMXMessageStatus;
import com.magnet.mmx.client.common.MMXPayload;
import com.magnet.mmx.client.common.MMXTopicInfo;
import com.magnet.mmx.client.common.MMXid;
import com.magnet.mmx.client.common.Options;
import com.magnet.mmx.client.common.PubSubManager;
import com.magnet.mmx.client.common.TopicExistsException;
import com.magnet.mmx.client.common.TopicNotFoundException;
import com.magnet.mmx.protocol.Constants;
import com.magnet.mmx.protocol.MMXStatus;
import com.magnet.mmx.protocol.Payload;
import com.magnet.mmx.protocol.SearchAction;
import com.magnet.mmx.protocol.MMXTopic;
import com.magnet.mmx.protocol.UserInfo;
import com.magnet.mmx.protocol.UserQuery;
import com.magnet.mmx.protocol.UserTags;

import android.test.InstrumentationTestCase;
import android.util.Log;

public class MMXClientTest extends InstrumentationTestCase {
  private static final String TAG = MMXClientTest.class.getSimpleName();
  private MMXClient mmxClient;
  private MMXClientConfig mClientConfig;
  private static final String USER1 = "MMXClientTest1";
  private static final String USER2 = "MMXClientTest2";
  private static final byte[] PASSWORD = "test".getBytes();
  private static final String TEST_TOPIC_NAME = "topic-foo-";

  private class ClientTestListener implements MMXClient.MMXListener {
    MMXMessage receivedMessage;
    MMXMessage pubSubMessage;
    AtomicBoolean isPubSubItemReceived = new AtomicBoolean(false);
    AtomicBoolean isConnected = new AtomicBoolean(false);
    AtomicBoolean isReceiptReceived = new AtomicBoolean(false);

    public void onConnectionEvent(MMXClient client, MMXClient.ConnectionEvent event) {
      Log.d(TAG, "connection event=" + event);
      boolean notify = false;
      switch (event) {
        case CONNECTED:
          notify = isConnected.compareAndSet(false, true);
          break;
        case DISCONNECTED:
          notify = isConnected.compareAndSet(true, false);
          break;
      }
      if (notify) {
        synchronized (isConnected) {
          isConnected.notify();
        }
      }
    }

    public void onMessageReceived(MMXClient client, MMXMessage message, String receiptId) {
      Log.d(TAG, "onMessageReceived message=" + message.toString());
      receivedMessage = message;
      synchronized (this) {
        notify();
      }
    }

    public void onSendFailed(MMXClient client, String msgId) {
      Log.d(TAG, "onSendFailed message Id=" + msgId);

    }

    public void onMessageDelivered(MMXClient client, MMXid recipient, String messageId) {
      Log.d(TAG, "onMessageDelivered message=" + messageId.toString());
      isReceiptReceived.set(true);
      synchronized (isReceiptReceived) {
        isReceiptReceived.notify();
      }
    }

    public void onPubsubItemReceived(MMXClient client, MMXTopic topic, MMXMessage message) {
      Log.d(TAG, "onPubsubItemReceived topic=" + topic.getName() + ";message=" + message.getPayload().getDataAsText());
      isPubSubItemReceived.set(true);
      pubSubMessage = message;
      synchronized (isPubSubItemReceived) {
        isPubSubItemReceived.notify();
      }
    }

    @Override
    public void onErrorReceived(MMXClient client, MMXErrorMessage error) {
      Log.d(TAG, "onErrorReceived error=" + error.toString());
      
    }
  }


  @Override
  public void setUp() {
    com.magnet.mmx.client.common.Log.setLoggable(null, com.magnet.mmx.client.common.Log.VERBOSE);
    mClientConfig = new ClientTestConfigImpl(this.getInstrumentation().getTargetContext());
    mmxClient = MMXClient.getInstance(this.getInstrumentation().getTargetContext(), mClientConfig);
  }

  private void connect(String user, ClientTestListener listener, MMXClient.ConnectionOptions options) {
    listener.isConnected.set(false);
    mmxClient.connectWithCredentials(user, PASSWORD, listener, options);
    synchronized (listener.isConnected) {
      try {
        listener.isConnected.wait(10000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private void connect(String user, ClientTestListener listener) {
    MMXClient.ConnectionOptions options = new MMXClient.ConnectionOptions().setAutoCreate(true);
    connect(user, listener, options);
  }

  private void disconnect(boolean isComplete, ClientTestListener listener) {
    mmxClient.disconnect(isComplete);
    synchronized (listener.isConnected) {
      try {
        listener.isConnected.wait(10000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public void testConnect() {
    ClientTestListener listener = new ClientTestListener();
    assertFalse(mmxClient.isConnected());
    connect(USER1, listener);
    assertTrue(mmxClient.isConnected());
    disconnect(false, listener);
    assertFalse(mmxClient.isConnected());
  }

  public void testConnectionInfo() {
    ClientTestListener listener = new ClientTestListener();
    connect(USER1, listener);
    assertTrue(mmxClient.isConnected());
    MMXClient.ConnectionInfo info = mmxClient.getConnectionInfo();
    assertEquals(info.clientConfig.getHost(), mClientConfig.getHost());
    assertEquals(info.clientConfig.getPort(), mClientConfig.getPort());
    assertEquals(info.clientConfig.getSecurityLevel(), mClientConfig.getSecurityLevel());
    assertEquals(info.username, USER1);
    assertEquals(info.authMode, MMXConnection.AUTH_AUTO_CREATE);

    //switch users
    disconnect(false, listener);  //not necessary since a connect with credentials call will disconnect first internally
    connect(USER2, listener);
    assertTrue(mmxClient.isConnected());
    info = mmxClient.getConnectionInfo();
    assertEquals(info.username, USER2);

    //disconnect hard
    disconnect(true, listener);
    assertFalse(mmxClient.isConnected());
    info = mmxClient.getConnectionInfo();
    assertNull(info.username);
  }

  public void testReconnect() {
    ClientTestListener listener = new ClientTestListener();
    connect(USER1, listener);
    assertTrue(mmxClient.isConnected());
    MMXClient.ConnectionInfo connectionInfo = mmxClient.getConnectionInfo();
    assertEquals(USER1, connectionInfo.username);

    disconnect(false, listener);
    assertFalse(mmxClient.isConnected());

    //try a complete disconnect
    connect(USER1, listener);
    assertTrue(mmxClient.isConnected());
    disconnect(true, listener);
    assertFalse(mmxClient.isConnected());
    connectionInfo = mmxClient.getConnectionInfo();
    assertNull(connectionInfo.username);
  }

  public void testMessageSend() throws MMXException {
    //setup client1
    final String runId = String.valueOf(System.currentTimeMillis());
    ClientTestListener listener1 = new ClientTestListener();
    MMXClient client1 = MMXClient.getInstance("client1",
            this.getInstrumentation().getTargetContext(),
            new ClientTestConfigImpl(this.getInstrumentation().getTargetContext()) {
              @Override
              public String getDeviceId() {
                return "test-client1-devId-" + runId;
              }
            });
    MMXClient.ConnectionOptions options = new MMXClient.ConnectionOptions().setAutoCreate(true);
    client1.connectWithCredentials(USER1, PASSWORD, listener1, options);
    synchronized (listener1.isConnected) {
      try {
        listener1.isConnected.wait(10000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    assertTrue(client1.isConnected());

    //setup client2
    ClientTestListener listener2 = new ClientTestListener();
    MMXClient client2 = MMXClient.getInstance("client2",
            this.getInstrumentation().getTargetContext(),
            new ClientTestConfigImpl(this.getInstrumentation().getTargetContext()) {
              @Override
              public String getDeviceId() {
                return "test-client2-devId-" + runId;
              }
            });
    client2.connectWithCredentials(USER2, PASSWORD, listener2, options);
    synchronized (listener2.isConnected) {
      try {
        listener2.isConnected.wait(10000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    assertTrue(client2.isConnected());

    // Test for invalid message ID
    List<MMXMessageStatus> list = client1.getMessageManager().getMessageState("NoSuchMsgId");
    assertEquals(Constants.MessageState.UNKNOWN, list.get(0).getState());
    
    // Test for invalid message ID's
    Map<String, List<MMXMessageStatus>> map = client1.getMessageManager().
        getMessagesState(Arrays.asList(new String[] { "NoSuchMsgId1", "NoSuchMsgId2" }));
    assertNotNull(map);
    assertEquals(Constants.MessageState.UNKNOWN, map.get("NoSuchMsgId1").get(0).getState());
    assertEquals(Constants.MessageState.UNKNOWN, map.get("NoSuchMsgId2").get(0).getState());
    
    //send message
    MMXPayload payload = new MMXPayload("foobar")
      .setMetaData("meta1", "value1");

    //do the send
    String messageId = client1.getMessageManager().sendPayload(new MMXid(USER2), payload,
            new Options().enableReceipt(true));

    //wait for the message
    synchronized (listener2) {
      try {
        listener2.wait(10000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    //check listener2
    MMXMessage receivedMessage = listener2.receivedMessage;
    assertNotNull(receivedMessage);
    Map<String, String> receivedMeta = receivedMessage.getAllMetaData();
    assertNotNull(receivedMeta);
    assertEquals("value1", receivedMeta.get("meta1"));
    assertEquals("foobar", receivedMessage.getPayload().getDataAsText());

    //wait for ack to finish
    synchronized (this) {
      try {
        wait(10000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    List<MMXMessageStatus> users = client1.getMessageManager().getMessageState(messageId);
    MMXMessageStatus status = findRecipient(users, USER2);
    assertNotNull(status);
    assertEquals(Constants.MessageState.DELIVERED, status.getState());

    String receiptId = listener2.receivedMessage.getReceiptId();
    client2.getMessageManager().sendReceipt(receiptId);

    synchronized(listener1.isReceiptReceived) {
      try {
        listener1.isReceiptReceived.wait(5000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    //wait for the receipt to be updated in the database
    synchronized (this) {
      try {
        wait(5000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    assertTrue(listener1.isReceiptReceived.get());
    users = client1.getMessageManager().getMessageState(messageId);
    status = findRecipient(users, USER2);
    assertNotNull(status);
    assertEquals(Constants.MessageState.RECEIVED, status.getState());

    //disconnect client 2
    client2.disconnect(true);
    synchronized (listener2.isConnected) {
      try {
        listener2.isConnected.wait(10000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    assertFalse(client2.isConnected());

    //disconnect client 1
    client1.disconnect(true);
    synchronized (listener1.isConnected) {
      try {
        listener1.isConnected.wait(5000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
  
  private MMXMessageStatus findRecipient(List<MMXMessageStatus> list, String userId) {
    for (MMXMessageStatus recipient: list) {
      if (userId.equalsIgnoreCase(recipient.getRecipient().getUserId())) {
        return recipient;
      }
    }
    return null;
  }

  public void testMessageSize() throws MMXException {
    ClientTestListener listener = new ClientTestListener();
    connect(USER1, listener);
    assertTrue(mmxClient.isConnected());
    
    int maxSize = MMXPayload.getMaxSizeAllowed();
    CharBuffer data = CharBuffer.allocate(maxSize);
    Arrays.fill(data.array(), 'A');
    MMXPayload payload = new MMXPayload(data)
        .setMetaData("Header1", "0000000")
        .setMetaData("Header2", "1111111");
    try {
      mmxClient.getMessageManager().sendPayload(mmxClient.getClientId(), payload, null);
      fail("Should fail because payload is too large");
    } catch (MMXException e) {
      assertEquals(MMXException.REQUEST_TOO_LARGE, e.getCode());
    } finally {
      disconnect(false, listener);
    }
  }
  
  public void testMessageCancel() throws MMXException {
    ClientTestListener listener = new ClientTestListener();
    connect(USER1, listener);
    assertTrue(mmxClient.isConnected());

    disconnect(false, listener);
    assertFalse(mmxClient.isConnected());

    String messageId = mmxClient.getMessageManager().sendText(new MMXid(USER1),
            "Test message", null);
    assertNotNull(messageId);

    List<MMXMessageStatus> users = mmxClient.getMessageManager().getMessageState(messageId);
    MMXMessageStatus status = findRecipient(users, USER1);
    assertNotNull(status);
    assertEquals(Constants.MessageState.CLIENT_PENDING, status.getState());

    boolean cancelSuccess = mmxClient.getMessageManager().cancelMessage(messageId);
    assertTrue(cancelSuccess);

    users = mmxClient.getMessageManager().getMessageState(messageId);
    assertEquals(Constants.MessageState.UNKNOWN, users.get(0).getState());

    //shoudn't be able to cancel the message again
    cancelSuccess = mmxClient.getMessageManager().cancelMessage(messageId);
    assertFalse(cancelSuccess);

    disconnect(false, listener);
  }

  public void testMessageCancelFail() throws MMXException {
    ClientTestListener listener = new ClientTestListener();
    connect(USER1, listener);
    assertTrue(mmxClient.isConnected());

    disconnect(false, listener);
    assertFalse(mmxClient.isConnected());

    String messageId = mmxClient.getMessageManager().sendText(new MMXid(USER1),
            "Test message", null);
    assertNotNull(messageId);

    connect(USER1, listener); //this should deliver the message
    boolean cancelSuccess = mmxClient.getMessageManager().cancelMessage(messageId);
    assertFalse(cancelSuccess);

    disconnect(false, listener);
  }

  public void testPublishLocation() {
    ClientTestListener listener = new ClientTestListener();
    connect(USER1, listener);
    assertTrue(mmxClient.isConnected());

    try {
      mmxClient.updateLocation();
    } catch (Throwable e) {
      Log.e(TAG, "testPublishLocation(): caught exception", e);
      fail();
    }
    disconnect(false, listener);
  }

  public void testUserUpdate() {
    String EMAIL = "user1@localhost.com";
    String DISPLAYNAME = "User1 At LocalHost";

    ClientTestListener listener = new ClientTestListener();
    connect(USER1, listener);
    assertTrue(mmxClient.isConnected());

    try {
      MMXAccountManager acctMgr = mmxClient.getAccountManager();
      UserInfo origAccountInfo = acctMgr.getUserInfo();
      assertNotNull(origAccountInfo);

      UserInfo accountInfo = new UserInfo()
          .setEmail(EMAIL)
          .setDisplayName(DISPLAYNAME);
      acctMgr.updateAccount(accountInfo);

      UserInfo newAccountInfo = acctMgr.getUserInfo();
      assertNotNull(newAccountInfo);
      assertEquals(EMAIL, newAccountInfo.getEmail());
      assertEquals(DISPLAYNAME, newAccountInfo.getDisplayName());
      
      // Restore to the original email and display name.
      acctMgr.updateAccount(origAccountInfo);
    } catch (MMXException e) {
      Log.e(TAG, "testUserUpdate(): caught exception", e);
      fail();
    }
    disconnect(false, listener);
  }

  public void testUserQuery() {
    ClientTestListener listener = new ClientTestListener();
    connect(USER1, listener);
    assertTrue(mmxClient.isConnected());

    try {
      MMXAccountManager acctMgr = mmxClient.getAccountManager();
      UserInfo accountInfo = acctMgr.getUserInfo();
      accountInfo.setEmail(USER1.toLowerCase() + "@magnet.com");
      acctMgr.updateAccount(accountInfo);
      
      accountInfo = acctMgr.getUserInfo();
      assertNotNull(accountInfo.getEmail());
      
      UserQuery.Search search = new UserQuery.Search();
      search.setEmail(accountInfo.getEmail(), SearchAction.Match.EXACT);

      UserQuery.Response response = acctMgr.searchBy(SearchAction.Operator.OR, search, null);
      Log.d(TAG, "testUserQuery(): Found " + response.getTotalCount() + " users matching this request.");
      boolean foundUser = false;
      for (UserInfo user : response.getUsers()) {
        Log.d(TAG, "testUserQuery(): found user: " + user.getDisplayName());
        foundUser = foundUser || accountInfo.getEmail().equalsIgnoreCase(user.getEmail());
      }
      assertTrue(foundUser);
    } catch (MMXException ex) {
      Log.e(TAG, "testUserQuery(): ", ex);
      fail();
    }
    disconnect(false, listener);
  }
  
  public void testAnonymous() {
    assertFalse(mmxClient.isConnected());
    ClientTestListener listener = new ClientTestListener();
    MMXClient.ConnectionOptions options = new MMXClient.ConnectionOptions().setAutoCreate(true);
    mmxClient.connectAnonymous(listener, options);
    synchronized(listener.isConnected) {
      try {
        listener.isConnected.wait(10000);
      } catch (InterruptedException e) {
      }
    }
    assertTrue(mmxClient.isConnected());
    mmxClient.disconnect();
    synchronized(listener.isConnected) {
      try {
        listener.isConnected.wait(10000);
      } catch (InterruptedException e) {
      }
    }
    assertFalse(mmxClient.isConnected());
  }

  public void testCreateUser() {
    String randomUser = "random" + System.currentTimeMillis();
    ClientTestListener listener = new ClientTestListener();
    connect(randomUser, listener);
    assertTrue(mmxClient.isConnected());
    MMXClient.ConnectionInfo info = mmxClient.getConnectionInfo();
    assertEquals(randomUser, info.username);
    disconnect(true, listener);
  }

  public void testPropFileConfig() {
    FileBasedClientConfig config = new FileBasedClientConfig(
        this.getInstrumentation().getTargetContext(), com.magnet.mmx.test.R.raw.testapp);
    assertEquals("testapp-appId", config.getAppId());
    assertEquals("testapp-apiKey", config.getApiKey());
    assertEquals("testapp-gcmSenderId", config.getGcmSenderId());
    assertEquals("testapp-serverUser", config.getServerUser());
    assertEquals("testapp-anonymousSecret", config.getAnonymousSecret());
    assertEquals("testapp-host", config.getHost());
    assertEquals(9999, config.getPort());
    assertEquals(MMXClient.SecurityLevel.NONE, config.getSecurityLevel());
    assertEquals("testapp-domainName", config.getDomainName());
  }

  public void testRelaxedSecurity() {
    MMXClient client = MMXClient.getInstance("relaxed",
            this.getInstrumentation().getTargetContext(),
            new ClientTestConfigImpl(this.getInstrumentation().getTargetContext()) {
              public MMXClient.SecurityLevel getSecurityLevel() {
                return MMXClient.SecurityLevel.STRICT;
              }
            });
    ClientTestListener listener = new ClientTestListener();
    //this connection should fail
    MMXClient.ConnectionOptions options = new MMXClient.ConnectionOptions().setAutoCreate(true);
    client.connectWithCredentials(USER1, PASSWORD, listener, options);
    synchronized(listener.isConnected) {
      try {
        listener.isConnected.wait(10000);
      } catch (InterruptedException e) {
      }
    }
    assertFalse(client.isConnected());
    client.applyConfig(new ClientTestConfigImpl(this.getInstrumentation().getTargetContext()) {
      public MMXClient.SecurityLevel getSecurityLevel() {
        return MMXClient.SecurityLevel.RELAXED;
      }
    });
    //this connection should succeed
    client.connectWithCredentials(USER1, PASSWORD, listener, options);
    synchronized(listener.isConnected) {
      try {
        listener.isConnected.wait(10000);
      } catch (InterruptedException e) {
      }
    }
    assertTrue(client.isConnected());
    client.disconnect(false);
    synchronized(listener.isConnected) {
      try {
        listener.isConnected.wait(10000);
      } catch (InterruptedException e) {
      }
    }
  }

  public void testGoAnonymous() {
    ClientTestListener listener = new ClientTestListener();
    connect(USER1, listener);
    assertTrue(mmxClient.isConnected());
    MMXClient.ConnectionInfo info = mmxClient.getConnectionInfo();
    assertEquals(0, info.authMode & MMXConnection.AUTH_ANONYMOUS);

    mmxClient.goAnonymous();
    //First a disconnect happens
    synchronized (listener.isConnected) {
      try {
        listener.isConnected.wait(10000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    //Then wait for the connect
    synchronized (listener.isConnected) {
      try {
        listener.isConnected.wait(10000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    assertTrue(mmxClient.isConnected());

    info = mmxClient.getConnectionInfo();
    assertNotSame(0, info.authMode & MMXConnection.AUTH_ANONYMOUS);
    disconnect(false, listener);
  }

  public void testOfflinePublish() {
    try {
      ClientTestListener listener = new ClientTestListener();
      assertFalse(mmxClient.isConnected());
      connect(USER1, listener);
      String topicName = TEST_TOPIC_NAME + System.currentTimeMillis();
      MMXTopic topic = new MMXGlobalTopic(topicName);
      try {
        topic = mmxClient.getPubSubManager().createTopic(topic, null);
      } catch (TopicExistsException tex) {
        //topic already exists it's ok
        fail("Topic already exists: " + topicName);
      }
      assertNotNull(topic);
      
      MMXTopicInfo info = mmxClient.getPubSubManager().getTopic(topic);
      assertNotNull(info);
      assertEquals(USER1.toLowerCase(), info.getCreator().getUserId());
      
      try {
        mmxClient.getPubSubManager().getTopic(new MMXGlobalTopic("ThisTopicShouldNotExist"));
        assertFalse(false);
      } catch (MMXException ex) {
        assertTrue(ex instanceof TopicNotFoundException);
      }
      
      mmxClient.getPubSubManager().subscribe(topic, false);
      disconnect(false, listener);

      String payloadString = String.valueOf(System.currentTimeMillis());
      MMXPayload payload = new MMXPayload(payloadString);
      String id = mmxClient.getPubSubManager().publish(topic, payload);
      assertNotNull(id);

      Map<String, MMXTopic> msgs = mmxClient.getPubSubManager().getPendingItems();
      assertNotNull(msgs.containsKey(id));

      connect(USER1, listener);
      assertTrue(mmxClient.isConnected());

      //message should be published when connect
      msgs = mmxClient.getPubSubManager().getPendingItems();
      assertFalse(msgs.containsKey(id));

      //at this point, the message is processed, but need to verify that it was sent...
      synchronized (listener.isPubSubItemReceived) {
        try {
          listener.isPubSubItemReceived.wait(10000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      assertTrue(listener.isPubSubItemReceived.get());
      assertEquals(payloadString, listener.pubSubMessage.getPayload().getDataAsText());

      MMXStatus status = mmxClient.getPubSubManager().deleteTopic(topic);
      assertEquals(MMXStatus.SUCCESS, status.getCode());

      disconnect(false, listener);
      assertFalse(mmxClient.isConnected());
    } catch (MMXException e) {
      e.printStackTrace();
      fail("Caught exception: " + e.getMessage());
    }
  }

  public void testConnectSuspended() throws MMXException {
    //setup client1
    final String runId = String.valueOf(System.currentTimeMillis());
    ClientTestListener listener1 = new ClientTestListener();
    MMXClient client1 = MMXClient.getInstance("client1",
            this.getInstrumentation().getTargetContext(),
            new ClientTestConfigImpl(this.getInstrumentation().getTargetContext()) {
              @Override
              public String getDeviceId() {
                return "test-client1-devId-" + runId;
              }
            });
    MMXClient.ConnectionOptions options = new MMXClient.ConnectionOptions().setAutoCreate(true);
    client1.connectWithCredentials(USER1, PASSWORD, listener1, options);
    synchronized (listener1.isConnected) {
      try {
        listener1.isConnected.wait(10000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    assertTrue(client1.isConnected());

    //setup client2
    MMXClient.ConnectionOptions options2 = new MMXClient.ConnectionOptions().setAutoCreate(true).setSuspendDelivery(true);
    ClientTestListener listener2 = new ClientTestListener();
    MMXClient client2 = MMXClient.getInstance("client2",
            this.getInstrumentation().getTargetContext(),
            new ClientTestConfigImpl(this.getInstrumentation().getTargetContext()) {
              @Override
              public String getDeviceId() {
                return "test-client1-devId-" + runId;
              }
            });
    client2.connectWithCredentials(USER2, PASSWORD, listener2, options2);
    synchronized (listener2.isConnected) {
      try {
        listener2.isConnected.wait(10000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    assertTrue(client2.isConnected());

    //send message

    //do the send
    String messageId = client1.getMessageManager().sendPayload(new MMXid(USER2),
        new MMXPayload("foobar")
            .setMetaData("meta1", "value1"), null);

    //wait for the message
    synchronized (listener2) {
      try {
        listener2.wait(10000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    //check listener2
    MMXMessage receivedMessage = listener2.receivedMessage;
    //should not receive message until we resume delivery
    assertNull(receivedMessage);
    try {
      client2.resumeDelivery();
      //wait for the message
      synchronized (listener2) {
        try {
          listener2.wait(10000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    } catch (MMXException e) {
      e.printStackTrace();
    }
    receivedMessage = listener2.receivedMessage;
    assertNotNull(receivedMessage);
    Map<String, String> receivedMeta = receivedMessage.getPayload().getAllMetaData();
    assertNotNull(receivedMeta);
    assertEquals("value1", receivedMeta.get("meta1"));
    assertEquals("foobar", receivedMessage.getPayload().getDataAsText());

    //disconnect client 2
    client2.disconnect(true);
    synchronized (listener2.isConnected) {
      try {
        listener2.isConnected.wait(10000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    assertFalse(client2.isConnected());

    //disconnect client 1
    client1.disconnect(true);
    synchronized (listener1.isConnected) {
      try {
        listener1.isConnected.wait(5000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public void testMMXCall() {
    ClientTestListener listener = new ClientTestListener();
    assertFalse(mmxClient.isConnected());
    connect(USER1, listener);
    assertTrue(mmxClient.isConnected());

    //do something
    final AtomicBoolean isResultReceived = new AtomicBoolean(false);
    MMXTask<UserInfo> task = new MMXTask<UserInfo>(mmxClient) {
      @Override
      public void onResult(UserInfo result) {
        isResultReceived.set(true);
        synchronized (this) {
          this.notifyAll();
        }
      }

      @Override
      public UserInfo doRun(MMXClient client) throws Throwable {
        MMXAccountManager am = client.getAccountManager();
        return am.getUserInfo();
      }
    };
    task.execute();
    UserInfo result = null;
    synchronized (task) {
      try {
        task.wait(10000);
        result = task.get();
      } catch (Throwable throwable) {
        throwable.printStackTrace();
        fail(throwable.getMessage());
      }
    }
    assertNotNull(result);

    disconnect(false, listener);
    assertFalse(mmxClient.isConnected());
  }

  public void testMMXCallException() {
    ClientTestListener listener = new ClientTestListener();
    assertFalse(mmxClient.isConnected());
    connect(USER1, listener);
    assertTrue(mmxClient.isConnected());

    //do something
    final AtomicBoolean isResultReceived = new AtomicBoolean(false);
    MMXTask<UserInfo> task = new MMXTask<UserInfo>(mmxClient) {
      @Override
      public void onResult(UserInfo result) {
        isResultReceived.set(true);
        synchronized (this) {
          this.notifyAll();
        }
      }

      @Override
      public void onException(Throwable ex) {
        isResultReceived.set(true);
        synchronized (this) {
          this.notifyAll();
        }
      }

      @Override
      public UserInfo doRun(MMXClient client) throws Throwable {
        throw new IllegalStateException("foo");
      }
    };
    task.execute();
    UserInfo result = null;
    synchronized (task) {
      try {
        task.wait(10000);
        result = task.get();
        fail("call.get() should have thrown an exception");
      } catch (InterruptedException e) {
        e.printStackTrace();
        fail(e.getMessage());
      } catch (Throwable throwable) {
        assertEquals(IllegalStateException.class, throwable.getClass());
        assertEquals("foo", throwable.getMessage());
      }
    }
    assertNull(result);

    disconnect(false, listener);
    assertFalse(mmxClient.isConnected());

  }

  public void testAddRemoveUserTags() {
    ClientTestListener listener = new ClientTestListener();
    assertFalse(mmxClient.isConnected());
    connect(USER1, listener);
    assertTrue(mmxClient.isConnected());

    MMXAccountManager am = mmxClient.getAccountManager();

    String tagPrefix = System.currentTimeMillis() + "";
    ArrayList<String> newTags = new ArrayList<String>();
    newTags.add(tagPrefix + "-1");
    newTags.add(tagPrefix + "-2");
    try {
      MMXStatus result = am.addTags(newTags);
      assertEquals(MMXStatus.SUCCESS, result.getCode());

      //make sure the tags are all added
      UserTags userTags = am.getAllTags();
      List<String> tags = userTags.getTags();
      for (int i=newTags.size(); --i>=0;) {
        boolean isTagFound = false;
        for (int j=tags.size(); --j>=0;) {
          if (tags.get(j).equals(newTags.get(i))) {
            isTagFound = true;
            break;
          }
        }
        assertTrue(isTagFound);
      }

      result = am.removeTags(newTags);
      assertEquals(MMXStatus.SUCCESS, result.getCode());
      //make sure the tags are all removed
      userTags = am.getAllTags();
      tags = userTags.getTags();
      for (int i=newTags.size(); --i>=0;) {
        boolean isTagFound = false;
        for (int j=tags.size(); --j>=0;) {
          if (tags.get(j).equals(newTags.get(i))) {
            isTagFound = true;
            break;
          }
        }
        assertTrue(!isTagFound);
      }
    } catch (MMXException e) {
      fail("Caught exception: " + e.getMessage());
    }
    disconnect(false, listener);
    assertFalse(mmxClient.isConnected());
  }

  public void testSetAllUserTags() {
    ClientTestListener listener = new ClientTestListener();
    assertFalse(mmxClient.isConnected());
    connect(USER1, listener);
    assertTrue(mmxClient.isConnected());

    MMXAccountManager am = mmxClient.getAccountManager();

    String tagPrefix = System.currentTimeMillis() + "";
    ArrayList<String> newTags = new ArrayList<String>();
    newTags.add(tagPrefix + "-1");
    newTags.add(tagPrefix + "-2");
    try {
      MMXStatus result = am.addTags(newTags);
      assertEquals(MMXStatus.SUCCESS, result.getCode());

      //make sure the tags are all added
      UserTags userTags = am.getAllTags();
      List<String> tags = userTags.getTags();
      for (int i=newTags.size(); --i>=0;) {
        boolean isTagFound = false;
        for (int j=tags.size(); --j>=0;) {
          if (tags.get(j).equals(newTags.get(i))) {
            isTagFound = true;
            break;
          }
        }
        assertTrue(isTagFound);
      }

      newTags.remove(newTags.size() - 1);
      result = am.setAllTags(newTags);
      assertEquals(MMXStatus.SUCCESS, result.getCode());
      //make sure the tags got replaced
      userTags = am.getAllTags();
      tags = userTags.getTags();
      assertEquals(newTags.size(), tags.size());
      for (int i=newTags.size(); --i>=0;) {
        boolean isTagFound = false;
        for (int j=tags.size(); --j>=0;) {
          if (tags.get(j).equals(newTags.get(i))) {
            isTagFound = true;
            break;
          }
        }
        assertTrue(isTagFound);
      }

      result = am.setAllTags(null);
      assertEquals(MMXStatus.SUCCESS, result.getCode());
      //make sure the tags got removed
      userTags = am.getAllTags();
      tags = userTags.getTags();
      assertEquals(0, tags.size());
    } catch (MMXException e) {
      fail("Caught exception: " + e.getMessage());
    }
    disconnect(false, listener);
    assertFalse(mmxClient.isConnected());
  }
}
