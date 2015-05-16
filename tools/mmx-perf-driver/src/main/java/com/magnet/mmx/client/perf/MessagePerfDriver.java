/**
 * Copyright (c) 2014-2015 Magnet Systems, Inc.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.magnet.mmx.client.perf;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import com.magnet.mmx.client.MMXClient;
import com.magnet.mmx.client.MMXContext;
import com.magnet.mmx.client.MMXSettings;
import com.magnet.mmx.client.common.AdminManager;
import com.magnet.mmx.client.common.Invitation;
import com.magnet.mmx.client.common.Log;
import com.magnet.mmx.client.common.MMXConnection;
import com.magnet.mmx.client.common.MMXConnectionListener;
import com.magnet.mmx.client.common.MMXErrorMessage;
import com.magnet.mmx.client.common.MMXException;
import com.magnet.mmx.client.common.MMXMessage;
import com.magnet.mmx.client.common.MMXMessageListener;
import com.magnet.mmx.client.common.MMXPayload;
import com.magnet.mmx.client.common.MMXid;
import com.magnet.mmx.client.common.MessageManager;
import com.magnet.mmx.client.common.Options;
import com.magnet.mmx.protocol.APNS;
import com.magnet.mmx.protocol.AppCreate;
import com.magnet.mmx.protocol.AuthData;
import com.magnet.mmx.protocol.GCM;
import com.magnet.mmx.protocol.MMXTopic;
import com.magnet.mmx.util.Base64;


enum Event {
  CONNECTED,
  NOTCONNECTED,
  DONE,
  DISCONNECTED,
}

interface EventListener {
  public void onEvent(Event event, MessagePerfDriver.TestClient client);
}

public class MessagePerfDriver implements EventListener{
  private final static String TAG = "MessagePerfDriver";
  private final static String SERVER_USER = "server-user";
  private final static String SERVER_PWD = "test435";
  private Thread mThread;
  private DriverConfig mConfig;
  private ArrayList<TestClient> mConClients;
  private ArrayList<TestClient> mDiscClients;
  private MMXSettings mSettings;
  private String mHeader;
  private String mDiscHeader;
  private Random mRand = new Random();
  private Options OPTIONS_RCPT_ENABLED = new Options().enableReceipt(true);

  private MMXConnectionListener mConListener = new MMXConnectionListener() {
    @Override
    public void onConnectionEstablished() {
      // Ignored.
    }

    @Override
    public void onConnectionClosed() {
      // Ignored.
    }

    @Override
    public void onConnectionFailed(Exception cause) {
      System.err.print("onConnectionFailed: "+cause.getMessage());
//      cause.printStackTrace();
    }

    @Override
    public void onAuthenticated(String user) {
      System.out.println("onAuthenticated: "+user);
    }

    @Override
    public void onAuthFailed(String user) {
      System.err.println("onAuthFailed: "+user);
    }

    @Override
    public void onAccountCreated(String user) {
      // Ignored
    }
  };
  
  class TestClient extends Thread implements MMXConnectionListener, MMXMessageListener {
    // Statistics
    public int mPayloadSize;
    public long mWaitTime;
    public long mSentTime;
    public long mSentTotal;
    public long mRcvTotal;
    public int mMsgRcved;
    public int mMsgSent;
    public int mMsgSendFailed;
    public int mReceiptSent;
    public int mReceiptFailed;
    public int mMsgDelivered;
    public int mMsgError;
    public int mConError;
    public int mDelayMsg;
    
    private boolean mDone;
    private boolean mAbort;
    private CharBuffer mText;
    private MessageManager mMsgMgr;
    private MessagePerfDriver mDriver;
    private EventListener mListener;
    private MMXContext mContext;
    private MMXid mClientId;
    private MMXClient mClient;
    private String mUserId;
    
    public TestClient(String userId, String devId, MessagePerfDriver driver,
                       EventListener listener) {
      super(userId);
      mDriver = driver;
      mListener = listener;
      mUserId = userId;
      mContext = new MMXContext(".", "0.9", devId);
      mWaitTime = mDriver.getRandomWaitTime();
      mPayloadSize = mDriver.getRandomPayloadSize();
      mText = CharBuffer.allocate(mPayloadSize);
      Arrays.fill(mText.array(), 'x');
    }
    
    public void getReport(StringBuilder sb) {
      long avgSentTime = (mMsgRcved == 0) ? 0 : mSentTime / mMsgRcved;
      sb.append(Utils.pad(mClientId.toString(), 20))
        .append(Utils.pad(mPayloadSize, 8))
        .append(Utils.pad(mConError, 8))
        .append(Utils.pad(mMsgSent, 10))
        .append(Utils.pad(mSentTotal, 15))
        .append(Utils.pad(mMsgRcved, 10))
        .append(Utils.pad(mRcvTotal, 15))
        .append(Utils.pad(avgSentTime, 10))
        .append(Utils.pad(mDelayMsg, 10))
        .append(Utils.pad(mReceiptSent, 8))
        .append(Utils.pad(mReceiptFailed, 8))
        .append(Utils.pad(mMsgDelivered, 8))
        .append('\n');
    }
    
    public void halt() {
      mAbort = true;
    }
    
    public boolean isDoneSending() {
      return mDone;
    }
    
    public void run() {
      // Create the client and connect
      mClient = new MMXClient(mContext, mDriver.getSettings());
      try {
        mClient.connect(mUserId, "test435".getBytes(), this, this, true);
        mClientId = mClient.getClientId();
        mListener.onEvent(Event.CONNECTED, this);
      } catch (Throwable e) {
        System.err.println("Client "+mUserId+" is not ready");
        e.printStackTrace();
        mListener.onEvent(Event.NOTCONNECTED, this);
        return;
      }
      
      // Wait for a signal to start sending messages
      synchronized(mDriver) {
        try {
          mDriver.wait();
        } catch (InterruptedException e) {
          // Ignored.
        }
      }

      // Start sending messages.
      mDone = false;
      mAbort = false;
      try {
        mMsgMgr = mClient.getMessageManager();
        long endTime = System.currentTimeMillis() + mDriver.getConfig().duration;
        while (!mAbort && System.currentTimeMillis() < endTime) {
          Thread.sleep(mWaitTime);
          MMXPayload payload = new MMXPayload(mText);
          MMXid to = mDriver.getRandomRecipient();
          if (to == null) {
            break;
          }
          Options options = mDriver.getRandomReceipt();
          mMsgMgr.sendPayload(new MMXid[] { to }, payload, options);
        }
        mDone = true;
        if (!mAbort) {
          mListener.onEvent(Event.DONE, this);
        }
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }

    @Override
    public void onMessageReceived(MMXMessage message, String receiptId) {
      mMsgRcved++;
      Date sentTime = message.getPayload().getSentTime();
      long tod = System.currentTimeMillis();
      long elapsed = tod - sentTime.getTime();
      if (elapsed <= 10000)
        mSentTime += elapsed;
      else {
        mDelayMsg++;
      }
      mRcvTotal += message.getPayload().getDataSize();
      
      if (receiptId != null) {
        try {
          mMsgMgr.sendDeliveryReceipt(receiptId);
          mReceiptSent++;
        } catch (MMXException e) {
          mReceiptFailed++;
        }
      }
    }

    @Override
    public void onMessageSent(String msgId) {
      mMsgSent++;
      mSentTotal += mPayloadSize;
    }

    @Override
    public void onMessageFailed(String msgId) {
      mMsgSendFailed++;
    }

    @Override
    public void onMessageDelivered(MMXid recipient, String msgId) {
      mMsgDelivered++;
    }

    @Override
    public void onInvitationReceived(Invitation invitation) {
      // Ignored.
    }

    @Override
    public void onAuthReceived(AuthData auth) {
      // Ignored.
    }

    @Override
    public void onItemReceived(MMXMessage msg, MMXTopic topic) {
      // Ignored.
    }

    @Override
    public void onErrorMessageReceived(MMXErrorMessage message) {
      mMsgError++;
    }

    @Override
    public void onConnectionEstablished() {
      System.out.println("onConnected");
    }

    @Override
    public void onConnectionClosed() {
      mAbort = true;
      mListener.onEvent(Event.DISCONNECTED, this);
    }

    @Override
    public void onConnectionFailed(Exception cause) {
      mConError++;
      mListener.onEvent(Event.DISCONNECTED, this);
    }

    @Override
    public void onAuthenticated(String user) {
      System.out.println("onAuthenticated: "+user);
    }

    @Override
    public void onAuthFailed(String user) {
      System.out.println("onAuthFailed: "+user);
    }

    @Override
    public void onAccountCreated(String user) {
      System.out.println("onAccountCreated: "+user);      
    }
  }

  public MessagePerfDriver(DriverConfig config) throws MMXException {
    Log.setLoggable(TAG, config.logLevel);
    MMXContext appContext = new MMXContext(".", "0.9", "instance-1");
    if (config.registerApp) {
      mSettings = registerApp(appContext, config.host, "admin", "admin",
                              config.appName);
    } else {
      mSettings = new MMXSettings(appContext, config.appName+".props");
      if (!mSettings.load()) {
        throw new IllegalArgumentException(
            config.appName+" is not registered yet; please specify -r");
      }
    }
    mThread = Thread.currentThread();
    mConfig = config;
    mHeader = new StringBuilder(128).append('\n')
        .append(Utils.pad("Live Client", 20))
        .append(Utils.pad("Size", 8))
        .append(Utils.pad("ConErr", 8))
        .append(Utils.pad("#MsgSent", 10))
        .append(Utils.pad("BytesSent", 15))
        .append(Utils.pad("#MsgRcvd", 10))
        .append(Utils.pad("BytesRcvd", 15))
        .append(Utils.pad("AvgSentTm", 10))
        .append(Utils.pad("#MsgDelay", 10))
        .append(Utils.pad("RcptSnt", 8))
        .append(Utils.pad("RcptErr", 8))
        .append(Utils.pad("RcptRtn", 8))
        .append('\n').toString();
    mDiscHeader = new StringBuilder(128).append('\n')
        .append(Utils.pad("Dead Client", 20))
        .append(Utils.pad("Size", 8))
        .append(Utils.pad("ConErr", 8))
        .append(Utils.pad("#MsgSent", 10))
        .append(Utils.pad("BytesSent", 15))
        .append(Utils.pad("#MsgRcvd", 10))
        .append(Utils.pad("BytesRcvd", 15))
        .append(Utils.pad("AvgSentTm", 10))
        .append(Utils.pad("#MsgDelay", 10))
        .append(Utils.pad("RcptSnt", 8))
        .append(Utils.pad("RcptErr", 8))
        .append(Utils.pad("RcptRtn", 8))
        .append('\n').toString();
  }

  public void onEvent(Event event, TestClient client) {
    synchronized(mConClients) {
      switch (event) {
      case CONNECTED:
        mConClients.add(client);
        break;
      case NOTCONNECTED:
        mDiscClients.add(client);
        break;
      case DONE:
        mThread.interrupt();
        break;
      case DISCONNECTED:
        if (mConClients.remove(client)) {
          mDiscClients.add(client);
        }
        break;
      }
    }
  }
  
  public DriverConfig getConfig() {
    return mConfig;
  }
  
  // Get the application settings.
  public MMXSettings getSettings() {
    return mSettings;
  }

  // Register a new application with bogus APNS Certificate and GCM Project ID.
  // Assume that the service name is "mmx".
  public MMXSettings registerApp(MMXContext context, String host, 
                                String adminUser, String adminPwd, String appName)
                                throws MMXException {
    MMXSettings settings = new MMXSettings(context, appName+".props");
    settings.setString(MMXSettings.PROP_HOST, host);
    settings.setInt(MMXSettings.PROP_PORT, 5222);
    
    MMXConnection con = new MMXConnection(context, settings);
    try {
      con.connect(mConListener);
      con.authenticateRaw(adminUser, adminPwd, "smack", 0);
      byte[] apnsCert = { 0x1, 0x2, 0x3, 0x4, 0x0, (byte) 0xff, 
                          (byte) 0xfe, (byte) 0x80, 0x7f };
      byte[] paddedCert = new byte[1000];
      System.arraycopy(apnsCert, 0, paddedCert, 0, apnsCert.length);
      AdminManager appMgr = AdminManager.getInstance(con);
      AppCreate.Request rqt = new AppCreate.Request()
        .setAppName(appName)
        .setAuthByAppServer(false)
        .setServerUserId(SERVER_USER)
        .setServerUserKey(SERVER_PWD)
        .setGcm(new GCM("Google Project ID 999", "Google API Key 1234"))
        .setApns(new APNS(Base64.encodeBytes(paddedCert), "dummyPasscode"));
      AppCreate.Response result = appMgr.createApp(rqt);
      System.out.println("app registration: apiKey="+result.getApiKey()+
                         ", appId="+result.getAppId());
      
      settings.setString(MMXSettings.PROP_SERVERUSER, SERVER_USER);
      settings.setString(MMXSettings.PROP_APPID, result.getAppId());
      settings.setString(MMXSettings.PROP_APIKEY, result.getApiKey());
      settings.setString(MMXSettings.PROP_GUESTSECRET, result.getGuestSecret());
      settings.save();
      return settings;
    } finally {
      con.disconnect();
    }
  }
  
  // Create the clients and let them connect first.  Each client will then wait
  // for a signal to start sending messages among the clients.
  public MessagePerfDriver init() {
    mDiscClients = new ArrayList<TestClient>();
    mConClients = new ArrayList<TestClient>(mConfig.numClients);
    for (int i = 0; i < mConfig.numClients; i++) {
      int j = 1000 + i;
      new TestClient("u-"+j, "d-"+j, this, this).start();
    }
    return this;
  }
  
  // Wait for all clients connected
  public MessagePerfDriver waitForConnected() {
    if (mConClients == null) {
      throw new IllegalStateException("Not call init() yet");
    }
    System.out.println("Waiting for all clients connected: "+
        mDiscClients.size()+" not connected, "+mConClients.size()+" connected");
    while ((mDiscClients.size() + mConClients.size()) < mConfig.numClients) {
      try {
        Thread.sleep(1000L);
        System.out.print(mDiscClients.size()+" not connected, "+
            mConClients.size()+" connected\r");
        System.out.flush();
      } catch (InterruptedException e) {
        // Ignored.
      }
    }
    if (mConClients.size() == 0) {
      throw new RuntimeException("No clients are connected.  Aborted.");
    }
    return this;
  }
  
  // Send a signal to all connected clients to start sending messages.
  public MessagePerfDriver start() {
    if (mConClients == null) {
      throw new IllegalStateException("Not call init() yet");
    }
    if ((mDiscClients.size() + mConClients.size()) < mConfig.numClients) {
      throw new IllegalStateException("Not call waitForConnected() yet");
    }
//    System.out.println("start()");
    
    // Notify all connected clients to start sending messages.
    synchronized(this) {
      notifyAll();
    }
    return this;
  }
  
  // Wait for all clients done.
  public MessagePerfDriver waitForDone() {
    if (mConClients == null) {
      throw new IllegalStateException("Not call start() yet");
    }
//    System.out.println("waitForDone()");

    while (!isAllDone()) {
      try {
        Thread.sleep(15000L);
      } catch (InterruptedException e) {
        // One client is done.
      }
      report();
    }
    return this;
  }
  
  // Generate a report
  public MessagePerfDriver report() {
    StringBuilder sb = new StringBuilder(2048);
    sb.append(MessagePerfDriver.this.mHeader);
    for (TestClient client : mConClients) {
      client.getReport(sb);
    }
    if (!mDiscClients.isEmpty()) {
      sb.append(MessagePerfDriver.this.mDiscHeader);
      for (TestClient client : mDiscClients) {
        client.getReport(sb);
      }
    }
    System.out.println(sb);
    return this;
  }
  
  // Close down all clients.
  public MessagePerfDriver shutdown(long delay) {
    if (mConClients == null) {
      throw new IllegalStateException("Not call init() yet");
    }
//    System.out.println("shutdown()");
    if (delay > 0) {
      try {
        Thread.sleep(delay);
      } catch (InterruptedException e) {
        // Ignored.
      }
    }
    for (TestClient client : mConClients) {
      client.halt();
    }
    return this;
  }
  
  int getRandomPayloadSize() {
    int diff = mConfig.maxSize - mConfig.minSize;
    if (diff <= 0)
      return mConfig.minSize;
    return mRand.nextInt(diff) + mConfig.minSize;
  }
  
  // Even the client is done (sending), it still can receive messages.
  MMXid getRandomRecipient() {
    if (mConClients.size() == 0)
      return null;
    int index = mRand.nextInt(mConClients.size());
    return mConClients.get(index).mClientId;
  }
  
  Options getRandomReceipt() {
    if (mConfig.receiptPercent == 0) {
      return null;
    } else if (mConfig.receiptPercent == 100) {
      return OPTIONS_RCPT_ENABLED;
    } else {
      return (mRand.nextInt(100) <= mConfig.receiptPercent) ? OPTIONS_RCPT_ENABLED : null;
    }
  }
  
  long getRandomWaitTime() {
    long diff = mConfig.maxWaitTime - mConfig.minWaitTime;
    if (diff <= 0)
      return mConfig.minWaitTime;
    return mRand.nextInt((int) diff) + mConfig.minWaitTime;
  }
  
  private boolean isAllDone() {
    for (TestClient client : mConClients) {
      if (!client.isDoneSending()) {
        return false;
      }
    }
    return true;
  }
  
  /**
   * The main entry point.
   */
  public static void main(String[] args) {
    DriverConfig config = DriverConfig.parseOptions(args);
    try {
      new MessagePerfDriver(config)
          .init()
          .waitForConnected()
          .start()
          .waitForDone()
          .shutdown(5L);
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }
}

class DriverConfig {
  public String appName = "PerfDrvApp";
  public String host = "52.8.32.38";
  public int logLevel = Log.DEBUG;
  public int numClients = 1;        // # clients plus one driver.
  public int minSize = 2000;        // in bytes
  public int maxSize = 200000;
  public long minWaitTime = 250;    // in msec
  public long maxWaitTime = 500;    // in msec
  public long duration = 60 * 1000;        // in msec
  public int receiptPercent = 0;
  public boolean registerApp = false;

  public static DriverConfig parseOptions(String[] args) {
    DriverConfig drvOpts = new DriverConfig();
    for (int i = 0; i < args.length; i++) {
      String opt = args[i];
      if (opt.equals("-?")) {
        System.out.println("[-h host] [-r] -n AppName [-l s|v|d|i|w|e] "+
            "[-c numClients] [-t 0..100] [-s minSize] [-S maxSize] "+
            "[-w minWait] [-W maxWait] [-d duration] [-?]");
        System.out.println("size has M|m|K|k, wait/duration has w|d|h|m|s|M");
        System.out.println("-t for receipt enable probability");
        System.exit(0);
      } else if (opt.equals("-r")) {
        drvOpts.registerApp = true;
      } else if (opt.equals("-n")) {
        String arg = args[++i];
        drvOpts.appName = arg;
      } else if (opt.equals("-c")) {
        String arg = args[++i];
        drvOpts.numClients = parseInt(arg);
      } else if (opt.equals("-s")) {
        String arg = args[++i];
        drvOpts.minSize = parseSize(arg);
      } else if (opt.equals("-S")) {
        String arg = args[++i];
        drvOpts.maxSize = parseSize(arg);
      } else if (opt.equals("-w")) {
        String arg = args[++i];
        drvOpts.minWaitTime = parseTime(arg);
      } else if (opt.equals("-W")) {
        String arg = args[++i];
        drvOpts.maxWaitTime = parseTime(arg);
      } else if (opt.equals("-d")) {
        String arg = args[++i];
        drvOpts.duration = parseTime(arg);
      } else if (opt.equals("-l")) {
        String arg = args[++i];
        drvOpts.logLevel = parseLogLevel(arg);
      } else if (opt.equalsIgnoreCase("-t")) {
        String arg = args[++i];
        drvOpts.receiptPercent = Math.abs(parseInt(arg)) % 101;
      } else {
        throw new IllegalArgumentException("[-h host] [-r] -n appName "+
            "[-l s|v|d|i|w|e] [-c numClients] [-s minSize] [-S maxSize] "+
            "[-w minWait] [-W maxWait] [-d duration] [-t 0..100] [-?]; unknown opt: "+opt);
      }
    }
    return drvOpts;
  }

  public static int parseSize(String arg) {
    int unit = 1;
    char c = arg.charAt(arg.length()-1);
    switch(c) {
    case 'K':
      unit = 1024; break;
    case 'k':
      unit = 1000; break;
    case 'M':
      unit = 1024*1024; break;
    case 'm':
      unit = 1000*1000; break;
    default:
      if (c < '0' || c > '9') {
        throw new IllegalArgumentException("Invalid unit (M|m|K|k): "+arg);
      }
      return Integer.parseInt(arg);
    }
    return Integer.parseInt(arg.substring(0, arg.length()-1)) * unit;
  }

  public static long parseTime(String arg) {
    long unit = 1;
    char c = arg.charAt(arg.length()-1);
    switch(c) {
    case 'w':
      unit = 7 * 24 * 3600 * 1000; break;
    case 'd':
      unit = 24 * 3600 * 1000; break;
    case 'h':
      unit = 3600 * 1000; break;
    case 'm':
      unit = 60 * 1000; break;
    case 's':
      unit = 1000; break;
    case 'M':
      unit = 1; break;
    default:
      throw new IllegalArgumentException("Invalid unit (w|d|h|m|s|M): "+arg);
    }
    return Long.parseLong(arg.substring(0, arg.length()-1)) * unit;
  }
  
  public static int parseLogLevel(String arg) {
    if (arg.equalsIgnoreCase("s")) {
      return Log.SUPPRESS;
    } else if (arg.equalsIgnoreCase("v")) {
      return Log.VERBOSE;
    } else if (arg.equalsIgnoreCase("d")) {
      return Log.DEBUG;
    } else if (arg.equalsIgnoreCase("i")) {
      return Log.INFO;
    } else if (arg.equalsIgnoreCase("w")) {
      return Log.WARN;
    } else if (arg.equalsIgnoreCase("e")) {
      return Log.ERROR;
    }
    throw new IllegalArgumentException("Invalid log level (s|v|d|i|w|e): "+arg);
  }

  public static int parseInt(String arg) {
    return Integer.parseInt(arg);
  }
  
  public static boolean parseBool(String arg) {
    return Boolean.parseBoolean(arg);
  }
  
  public static String[] parseCommaList(String arg) {
    return arg.split(",");
  }
}
