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

package com.magnet.mmx.client.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;

import javax.crypto.NoSuchPaddingException;
import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnection.FromMode;
import org.jivesoftware.smack.debugger.ConsoleDebugger;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.sasl.SASLError;
import org.jivesoftware.smack.sasl.SASLErrorException;

import com.magnet.mmx.client.common.GlobalAddress.User;
import com.magnet.mmx.protocol.Constants;
import com.magnet.mmx.protocol.Constants.UserCreateMode;
import com.magnet.mmx.protocol.MMXStatus;
import com.magnet.mmx.protocol.UserCreate;
import com.magnet.mmx.util.BinCodec;
import com.magnet.mmx.util.DefaultEncryptor;
import com.magnet.mmx.util.MMXQueue;
import com.magnet.mmx.util.QueueExecutor;
import com.magnet.mmx.util.XIDUtil;

/**
 * @hide
 * MMXConnection con = new MMXConnection(context);
 * {@link MMXConnection#setMessageListener(MMXMessageListener)}
 * {@link #connect(MMXSettings, listener)} {@link #authenticate(user, password,
 * deviceId, 0)}
 * {@link MMXConnection#sendMessage(User[], String, Message, Options)}; ...
 * {@link SessionManager#getInstance(MMXConnection)};
 * {@link SessionManager#create(com.magnet.mmx.client.SessionManager.SessionType, String, SessionListener)}
 * {@link Session#sendInvitation(String, User)}; ...
 * {@link SessionManager#join(String, String, Date, SessionListener)}
 * {@link #disconnect()}
 *
 */
public class MMXConnection implements ConnectionListener {
  private final static String TAG = "MMXConnection";
  private final HashMap<String, Object> mManagers = new HashMap<String, Object>();
  private MMXContext mContext;
  private MagnetXMPPConnection mCon;
  private MMXConnectionListener mConListener;
  private MMXSettings mSettings;
  private MMXMessageListener mMsgListener;
  private MMXQueue mQueue;
  private QueueExecutor mExecutor;
  private AnonyAccount mAnonyAcct;
  private String mPubSubServiceName;
  private String mAppId;
  private String mApiKey;
  private MMXid mXID;     // caching the MMX ID (userID/deviceID)
  private String mUserId; // caching the user ID (without appId)
  private String mConToken; // MD5 of host-port-userID
  private String mUUID;
  private long mSeq;

  /**
   * Auto create the account if the account does not exist.
   */
  public final static int AUTH_AUTO_CREATE = 0x1;
  /**
   * The account being created is an anonymous account.
   */
  public final static int AUTH_ANONYMOUS = 0x2;

  static {
    // Register the Message Provider, so it can parse unsolicited messages.
    MMXPayloadMsgHandler.registerMsgProvider();
  }

  /**
   * Constructor with an application context.
   *
   * @param context
   * @see #destroy()
   */
  public MMXConnection(MMXContext context, MMXSettings settings) {
    this(context, null, settings);
  }

  /**
   * Constructor with an application context and a queue if desired (may be null).
   * If the queue parameter is null, offline operations (i.e. PubSubManager.publishToTopic())
   * will NOT be queued when offline.
   *
   * @param context the application context
   * @param queue the queue for this connection
   * @see #destroy()
   */
  public MMXConnection(MMXContext context, MMXQueue queue, MMXSettings settings) {
    mContext = context;
    mQueue = queue;
    mExecutor = new QueueExecutor("CallbackThread", true);
    mExecutor.start();
    mSettings = settings.clone();
    initId();
  }

  /**
   * Retrieves the thread in which all messaging callback will be run.
   * @return The thread for all messaging callback will be run in.
   */
  QueueExecutor getExecutor() {
    return mExecutor;
  }

  /**
   * Retrieves the queue associated with this connection or null if not specified.
   * @return the queue associated with this connection or null of no queue
   */
  MMXQueue getQueue() {
    return mQueue;
  }

  /**
   * Destroy this object and free up the resources. This is the counterpart of
   * the constructor.
   */
  public void destroy() {
    disconnect();

    mExecutor.quit();
    mExecutor = null;
    mSettings = null;
    mConListener = null;
    mMsgListener = null;

    mContext = null;
  }

  // Get a singleton manager by name, or register a new instance by name.
  public Object getManager(String name, Creator creator) {
    Object mgr = mManagers.get(name);
    if (mgr == null) {
      synchronized (mManagers) {
        if ((mgr = mManagers.get(name)) == null) {
          mgr = creator.newInstance(this);
          mManagers.put(name, mgr);
        }
      }
    }
    return mgr;
  }

  public MMXContext getContext() {
    return mContext;
  }

  public synchronized String genId() {
    return mUUID + '-' + Long.toString((++mSeq), 36);
  }

  // 16-byte UUID in private encoding.
  private synchronized void initId() {
    byte[] dst = new byte[16];
    UUID uuid = UUID.randomUUID();
    BinCodec.longToBytes(uuid.getMostSignificantBits(), dst, 0);
    BinCodec.longToBytes(uuid.getLeastSignificantBits(), dst, 8);
    mUUID = BinCodec.encodeToString(dst, false).substring(0, 22);
    mSeq = 0;
  }

  /**
   * Put this client (session) online so it can start receiving messages. If the
   * session is offline, no messages will be delivered to this client.
   *
   * @param online
   *          true for online; false for offline.
   * @throws MMXException
   * @deprecated {@link #setMessageFlow(int)}
   */
  @Deprecated
  public void setOnline(boolean online) throws MMXException {
    Presence presence = new Presence(online ? Presence.Type.available
        : Presence.Type.unavailable);
    try {
      mCon.sendPacket(presence);
    } catch (Throwable e) {
      throw new MMXException(e.getMessage(), e);
    }
  }

  /**
   * Control the message flow. Messages targeting to bared JID will be delivered
   * to the highest priority connected device. If multiple connected devices
   * have the same priority, messages will be delivered to all of them. If the
   * priority is negative, messages will be queued in MMX server until one of
   * the connected device has a non-negative priority.
   *
   * @param priority
   *          between -128 and 128.
   * @throws MMXException
   */
  public void setMessageFlow(int priority) throws MMXException {
    Presence presence;
    if (priority >= 0) {
      presence = new Presence(Presence.Type.available, "Online", priority,
          Mode.available);
    } else {
      // Type.unavailable will make the connection invisible.
      presence = new Presence(Presence.Type.available, "Blocking", priority,
          Mode.dnd);
    }
    try {
      mCon.sendPacket(presence);
    } catch (Throwable e) {
      throw new MMXException(e.getMessage(), e);
    }
  }

  /**
   * Establish a connection to the MMX server without any authenticating the
   * user. The settings {@link MMXSettings#PROP_ENABLE_ONLINE} allows the caller
   * to control if messages should be delivered automatically upon successful
   * connection and authentication.
   *
   * @param settings
   * @param listener
   * @throws ConnectionException
   * @throws MMXException
   * @see {@link #authenticate()}
   * @see #disconnect()
   * @see #setOnline(boolean)
   */
  public void connect(MMXConnectionListener listener)
      throws ConnectionException, MMXException {
    connect(listener, null, null, null, false);
  }

  public void connect(MMXConnectionListener listener,
                      SSLContext sslContext)
          throws ConnectionException, MMXException {
    connect(listener, null, null, sslContext, false);
  }

  public void connect(MMXConnectionListener listener,
      HostnameVerifier hostnameVerifier,
      SocketFactory socketFactory,
      SSLContext sslContext,
      boolean isConnectOffline)
      throws ConnectionException, MMXException {
    if (mCon != null && mCon.isConnected()) {
      throw new MMXException("client is still connected");
    }

    String host = mSettings.getString(MMXSettings.PROP_HOST, "localhost");
    int port = mSettings.getInt(MMXSettings.PROP_PORT, 5222);
    String serviceName = mSettings.getString(MMXSettings.PROP_SERVICE_NAME,
                                             MMXSettings.DEFAULT_SERVICE_NAME);
    ConnectionConfiguration config;
    if (serviceName == null) {
      config = new ConnectionConfiguration(host, port);
    } else {
      config = new ConnectionConfiguration(host, port, serviceName);
    }
    config.setReconnectionAllowed(mSettings.getBoolean(
            MMXSettings.PROP_ENABLE_RECONNECT, false));
    config.setRosterLoadedAtLogin(mSettings.getBoolean(
            MMXSettings.PROP_ENABLE_SYNC, false));
    //TODO:  mSettings should be moved back to connect-time instead of instatiation (was needed for android offline pub)
    //TODO:  Once that happens, we should do these things through settings instead of as parameters
//    config.setSendPresence(mSettings.getBoolean(MMXSettings.PROP_ENABLE_ONLINE,
//            true));
    config.setSendPresence(!isConnectOffline);
    config.setCompressionEnabled(mSettings.getBoolean(
            MMXSettings.PROP_ENABLE_COMPRESSION, true));
    config.setSecurityMode(mSettings.getBoolean(MMXSettings.PROP_ENABLE_TLS,
            false) ? ConnectionConfiguration.SecurityMode.required
            : ConnectionConfiguration.SecurityMode.disabled);
    if (hostnameVerifier != null) {
      config.setHostnameVerifier(hostnameVerifier);
    }
    if (socketFactory != null) {
      config.setSocketFactory(socketFactory);
    }
    if (sslContext != null) {
      config.setCustomSSLContext(sslContext);
    }
    // MAGNET extension
    // SASLAuthentication.setApiKey(getApiKey());

    switch (Log.getLoggable(null)) {
    case Log.VERBOSE:
      config.setDebuggerEnabled(true);
      ConsoleDebugger.printInterpreted = true;
      break;
    // case Log.DEBUG:
    // config.setDebuggerEnabled(true);
    // ConsoleDebugger.printInterpreted = false;
    // break;
    default:
      config.setDebuggerEnabled(false);
      ConsoleDebugger.printInterpreted = false;
      break;
    }

    mConListener = listener;
    mManagers.clear();

    mCon = new MagnetXMPPConnection(config);
    mCon.setFromMode(FromMode.USER);
    mCon.addConnectionListener(this);

    // add the packet listeners MMX payload message or error messages.
    MessageManager.getInstance(this).initPacketListener();

    try {
      mCon.connect();
    } catch (SmackException.ConnectionException e) {
      throw new ConnectionException(
          "Unable to connect to " + host + ":" + port, e);
    } catch (Throwable e) {
      throw new MMXException(e.getMessage(), e);
    }
  }

  /**
   * Manual reconnect after the connection was abruptly lost. If the user has
   * logged out, reconnection will not be allowed.
   *
   * @return true if reconnect successfully, false if already connected.
   * @throws MMXException
   *           Unable to reconnect due to error, or logged out explicitly.
   */
  public boolean reconnect() throws MMXException {
    if (mCon == null) {
      throw new MMXException("Connection has been terminated explicitly");
    }
    if (mCon.isConnected()) {
      return false;
    }
    if (!mCon.wasAuthenticated()) {
      throw new MMXException("User has logged out explicitly");
    }
    try {
      mCon.connect();
      return true;
    } catch (Throwable e) {
      throw new MMXException("Unable to reconnect", e);
    }
  }

  /**
   * Tear down the connection and remove the instances of all managers tied to
   * this connection.
   *
   * @see #connect(MMXSettings, MMXConnectionListener)
   */
  public void disconnect() {
    if (mCon != null) {
      if (mCon.isConnected()) {
        try {
          mCon.disconnect();
          mUserId = null;
          mXID = null;
          mAnonyAcct = null;
          mManagers.clear();
        } catch (NotConnectedException e) {
          e.printStackTrace();
        }
      }
      mCon = null;
    }
  }

  private static class AnonyAccount {
    private final static String ANONYMOUS_PREFIX = "_anon-";
    private final static String ANONYMOUS_FILE = "com.magnet.sec-anonymous.bin";
    private final static String PROP_USER_ID = "userId";
    private final static String PROP_PASSWORD = "password";
    private String mUserId;
    private String mPassword;
    private final DefaultEncryptor mEncryptor;

    private AnonyAccount(byte[] passcode) throws InvalidKeyException,
        NoSuchAlgorithmException, NoSuchPaddingException,
        InvalidKeySpecException, UnsupportedEncodingException {
      byte[] fkey = new byte[32];
      byte[] key = "zpdi3901!)939v91a{F{#>@['d.JBBs?".getBytes();
      int minLen = Math.min(fkey.length, key.length);
      System.arraycopy(key, 0, fkey, 0, minLen);
      minLen = Math.min(fkey.length, passcode.length);
      for (int i = 0; i < minLen; i++) {
        fkey[i] ^= passcode[i];
      }
      mEncryptor = new DefaultEncryptor(fkey);
    }

    public boolean load(MMXContext context) throws IOException {
      FileInputStream fis = null;
      InputStream is = null;
      try {
        fis = new FileInputStream(new File(context.getFilePath(ANONYMOUS_FILE)));
        is = mEncryptor.decodeStream(fis);
        Properties props = new Properties();
        props.load(is);
        mUserId = props.getProperty(PROP_USER_ID);
        mPassword = props.getProperty(PROP_PASSWORD);
        return true;
      } catch (IOException e) {
        return false;
      } finally {
        if (is != null) {
          is.close();
        } else if (fis != null) {
          fis.close();
        }
      }
    }

    public void generate(MMXContext context) {
      mUserId = generateAnonymousUser(context);
      mPassword = generateRandomPassword();
    }

    public void save(MMXContext context) throws IOException {
      FileOutputStream fos = null;
      OutputStream os = null;
      try {
        fos = new FileOutputStream(
            new File(context.getFilePath(ANONYMOUS_FILE)));
        os = mEncryptor.encodeStream(fos);
        Properties props = new Properties();
        props.setProperty(PROP_USER_ID, mUserId);
        props.setProperty(PROP_PASSWORD, mPassword);
        props.save(os, "DO NOT MODIFY; IT IS A GENERATED FILE");
      } catch (IOException e) {
        Log.e(
            TAG,
            "Did you get InvalidKeyException: Illegal key size or default parameters?\n"
                + "You may need to install a Java Cryptography Extension Unlimited Strength "
                + "Jurisdiction Policy Files 7 Download from "
                + "http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html\n"
                + "Read the README from the zip about installation.");
        throw e;
      } finally {
        if (os != null) {
          os.close();
        } else if (fos != null) {
          fos.close();
        }
      }
    }

    private String generateAnonymousUser(MMXContext context) {
      UUID uuid = UUID.randomUUID();
      String result = Long.toString(Math.abs(uuid.getMostSignificantBits()), 32) +
               Long.toString(Math.abs(uuid.getLeastSignificantBits()), 32);
      return ANONYMOUS_PREFIX + result;
    }

    private String generateRandomPassword() {
      SecureRandom random = new SecureRandom();
      return new BigInteger(130, random).toString(32);
    }
  }

  /**
   * Login anonymously. The semi-anonymous account is generated based on the
   * device ID and persistent that it can be used in PubSub. Only one login is
   * allowed per process.
   *
   * @throws MMXException
   */
  public void loginAnonymously() throws MMXException {
    if (mAnonyAcct == null) {
      try {
        AnonyAccount anonyAcct = new AnonyAccount(getAppId().getBytes());
        if (!anonyAcct.load(mContext)) {
          anonyAcct.generate(mContext);
          anonyAcct.save(mContext);
        }
        mAnonyAcct = anonyAcct;
      } catch (Throwable e) {
        throw new MMXException(e.getMessage(), e);
      }
    }
    authenticate(mAnonyAcct.mUserId, mAnonyAcct.mPassword,
        mContext.getDeviceId(), AUTH_ANONYMOUS | AUTH_AUTO_CREATE);
  }

  // Logout from an account, but keep the connection. Since Smack does not
  // support keeping the connection, we fake it by disconnect and connect.
  protected void logout() throws MMXException {
    try {
      mCon.disconnect();
      mUserId = null;
      mAnonyAcct = null;
      mCon.resetAuthFailure();
      mCon.connect();
    } catch (Throwable e) {
      throw new MMXException(e.getMessage(), e);
    }
  }

  /**
   * Authenticate a user with an option to create the account. The appId (if
   * available) will be appended to the userID before passing to the XMPP server
   * in multi-tenant environment. If there is authentication failure, the
   * connection is still retained.
   *
   * @param userId
   *          The user ID without appId.
   * @param password
   * @param resource
   * @param flags
   *          Combination of {@link #AUTH_AUTO_CREATE}, {@link #AUTH_ANONYMOUS}
   * @throws MMXException
   *           Unable to create account.
   * @see MMXConnectionListener#onAuthenticated(String)
   * @see MMXConnectionListener#onAuthFailed(String)
   */
  public void authenticate(String userId, String password, String resource,
      int flags) throws MMXException {
    authenticateRaw(makeUserId(userId), password, resource, flags);
  }

  /**
   * Authenticate a user with an option to create the account. The userID will
   * be passed to XMPP server as-is. If there is an authentication failure, the
   * connection is still retained.
   *
   * @param userId
   *          The node of the JID.
   * @param password
   * @param resource
   * @param flags
   *          Combination of {@link #AUTH_AUTO_CREATE}, {@link #AUTH_ANONYMOUS}
   * @throws MMXException
   *           unable to create the account.
   * @see MMXConnectionListener#onAuthenticated(String)
   * @see MMXConnectionListener#onAuthFailed(String)
   */
  public void authenticateRaw(String userId, String password, String resource,
      int flags) throws MMXException {
    if (mCon.isAuthenticated()) {
      return;
    }
    try {
      resetAuthFailure();
      // mCon.login(userId, password, resource, getApiKey());
      mCon.login(userId, password, resource);
    } catch (SASLErrorException e) {
      if (!e.getSASLFailure().getSASLError().equals(SASLError.not_authorized)) {
        // Some SASL errors.
        throw new MMXException(e.getMessage(), e);
      }
      // Not authorized: invalid password or account does not exist.
      if ((flags & AUTH_AUTO_CREATE) == 0) {
        if (mConListener != null) {
          mConListener.onAuthFailed(XIDUtil.getUserId(userId));
        }
        return;
      }

      // Try in-band user creation. If not supported, try custom IQ to create
      // the account. If authentication failed, return.
      // int ret = createAccount(userId, password);
      // if ((ret != 0) && ((ret >= 0) || !customCreateAccount(userId, password,
      // flags & AUTH_ANONYMOUS))) {
      // return;
      // }
      UserCreateMode mode = ((flags & AUTH_ANONYMOUS) != 0) ? UserCreateMode.GUEST
          : UserCreateMode.UPGRADE_USER;
      if (!customCreateAccount(userId, password, mode)) {
        return;
      }

      // Callback if the auto account creation is success.
      if (mConListener != null) {
        mConListener.onAccountCreated(XIDUtil.getUserId(userId));
      }

      // Account is created, now try to log in again.
      try {
        resetAuthFailure();
        // mCon.login(userId, password, resource, getApiKey());
        mCon.login(userId, password, resource);
        return;
      } catch (Throwable t) {
        throw new MMXException(t.getMessage(), t);
      }
    } catch (Throwable e) {
      throw new MMXException(e.getMessage(), e);
    }
  }

  /**
   * Use custom IQ to create an account.
   *
   * @param userId
   *          The user ID whose user name portion must not contain '/' or '%'.
   * @param password
   * @param mode
   *          Guest user or actual user.
   * @return true for success; false for authentication failure.
   * @throws MMException
   *           Unable to create account.
   */
  protected boolean customCreateAccount(String userId, String password,
      UserCreateMode mode) throws MMXException {
    String userName = XIDUtil.getUserId(userId);
    if (!XIDUtil.validateUserId(userName)) {
      throw new MMXException("User name '" + userName + "' cannot contain "
          + XIDUtil.INVALID_CHARS);
    }
    UserCreate account = new UserCreate();
    account.setPriKey(mSettings.getString(MMXSettings.PROP_GUESTSECRET, null));
    account.setApiKey(getApiKey());
    account.setAppId(getAppId());
    account.setCreateMode(mode);
    account.setUserId(userName);
    account.setPassword(password);
    account.setDisplayName(mSettings.getString(MMXSettings.PROP_NAME, userName));
    account.setEmail(mSettings.getString(MMXSettings.PROP_EMAIL, null));
    try {
      MMXStatus status = AccountManager.getInstance(this).createAccount(account);
      if (status.getCode() == Constants.STATUS_CODE_200) {
        return true;
      }
      throw new MMXException(status.getMessage(), status.getCode());
    } catch (MMXException e) {
      // userId is taken
      if (e.getCode() == Constants.STATUS_CODE_400) {
        if (mConListener != null) {
          mConListener.onAuthFailed(userName);
        }
        return false;
      }
      throw e;
    } catch (Throwable e) {
      throw new MMXException(e.getMessage(), e);
    }
  }

  /**
   * Set or remove a listener for invitations, messages, pubsub and presences.
   *
   * @param listener
   *          A listener or null.
   * @see MMXMessageHandler
   */
  public void setMessageListener(MMXMessageListener listener) {
    mMsgListener = listener;
  }

  /**
   * Get the listener for invitation, messages, pubsub and presences.
   *
   * @return
   */
  public MMXMessageListener getMessageListener() {
    return mMsgListener;
  }

  /**
   * Broadcast a message to a group of users chosen through a filter.
   *
   * @param filter
   * @param msgType
   * @param payload
   * @param options
   * @return
   */
  // public String broadcastMessage(Filter filter, Msg<?> payload,
  // ReliableOptions options) {
  // return null;
  // }

  /**
   * Check if the current connection is using an anonymous account.
   *
   * @return
   */
  public boolean isAnonymous() {
    if (mCon == null) {
      return false;
    }
    return (mAnonyAcct != null) && getUserId().equals(mAnonyAcct.mUserId);
  }

  /**
   * Check if the current connection is authenticated (not using anonymous
   * account.)
   *
   * @return
   */
  public boolean isAuthenticated() {
    if (mCon == null) {
      return false;
    }
    return mCon.isAuthenticated() && !isAnonymous();
  }

  /**
   * Check if the current connection is alive.
   *
   * @return
   */
  public boolean isConnected() {
    if (mCon == null) {
      return false;
    }
    return mCon.isConnected();
  }

  /**
   * Get the MMX ID (userID and deviceID) of the login user.
   * @return
   */
  public MMXid getXID() {
    if (mCon == null) {
      return null;
    }
    if (mXID == null) {
      mXID = XIDUtil.toXid(getUser());
    }
    return mXID;
  }
  /**
   * Get the full XID of the login user.
   * @return
   */
  public String getUser() {
    if (mCon == null) {
      return null;
    }
    return mCon.getUser();
  }

  /**
   * Get the userID (without appID) of the login user.
   *
   * @return
   */
  public String getUserId() {
    if (mCon == null) {
      return null;
    }
    if (mUserId == null) {
      mUserId = XIDUtil.getUserId(mCon.getUser());
    }
    return mUserId;
  }

  /**
   * Make a MMX multi-tenant user ID.
   *
   * @param userId A user ID without appId.
   * @return An escaped node with "userID%appId".
   */
  public String makeUserId(String userId) {
    return XIDUtil.makeEscNode(userId, getAppId());
  }

  /**
   * Make a bared XID or full XID from a User object.
   * @param user
   * @return
   */
  public String makeXID(MMXid user) {
    return XIDUtil.makeXID(user.getUserId(), getAppId(), getDomain(), user.getDeviceId());
  }

  String getAppId() {
    if (mAppId == null) {
      mAppId = mSettings.getString(MMXSettings.PROP_APPID, null);
    }
    return mAppId;
  }

  String getApiKey() {
    if (mApiKey == null) {
      mApiKey = mSettings.getString(MMXSettings.PROP_APIKEY, null);
    }
    return mApiKey;
  }

  String getPubSubService() {
    if (mPubSubServiceName == null && mCon != null) {
      mPubSubServiceName = "pubsub." + mCon.getServiceName();
    }
    return mPubSubServiceName;
  }

  String getDomain() {
    if (mCon == null) {
      return null;
    }
    return mCon.getServiceName();
  }

  MagnetXMPPConnection getXMPPConnection() {
    return mCon;
  }

  // Get a MD5 token to represent the connection. This token is part of a file
  // name to persist some state information.
  String getConnectionToken() {
    if (mConToken == null) {
      String token = mCon.getHost() + '-' + mCon.getPort() + '-'
          + mCon.getUser();
      try {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(token.getBytes());
        byte[] digest = md5.digest();
        mConToken = BinCodec.encodeToString(digest, false).substring(0, 22);
      } catch (Throwable e) {
        Log.w(TAG, "Cannot hash the connection token; use plain text", e);
        mConToken = token;
      }
    }
    return mConToken;
  }

  void resetAuthFailure() {
    mCon.resetAuthFailure();
  }

  MMXSettings getSettings() {
    return mSettings;
  }

  @Override
  public void authenticated(XMPPConnection con) {
    if (mConListener != null) {
      mConListener.onAuthenticated(con.getUser());
    }

    // If it is not a MMX user, skip asking for missed published items.
    if (XIDUtil.getAppId(con.getUser()) == null) {
      return;
    }

    // After authenticated, ask MMX to send the very last published item from
    // each subscribed topic since the last receiving time. TODO: some last
    // published items may have been delivered to other devices before; how
    // smart is the PubSub implementation?  Should it be a settings per topic
    // or per subscription in the server?
    int maxItems = mSettings.getInt(MMXSettings.PROP_MAX_LAST_PUB_ITEMS, 1);
    if (maxItems != 0) {
      try {
        Date lastDeliveryTime = PubSubManager.getInstance(this)
            .getLastDelivery();
        MMXStatus status = PubSubManager.getInstance(this)
            .requestLastPublishedItems(maxItems, lastDeliveryTime);
        if (Log.isLoggable(TAG, Log.DEBUG)) {
          Log.d(TAG, "sendLastPublishedItems(): " + status.getMessage() + ", code="
              + status.getCode());
        }
      } catch (MMXException e) {
        Log.e(TAG, "sendLastPublishedItems() failed", e);
      }
    }
  }

  @Override
  public void connected(XMPPConnection con) {
    if (mConListener != null) {
      mConListener.onConnectionEstablished();
    }
  }

  @Override
  public void connectionClosed() {
    if (mConListener != null) {
      mConListener.onConnectionClosed();
    }
  }

  @Override
  public void connectionClosedOnError(Exception cause) {
    if (mConListener != null) {
      mConListener.onConnectionFailed(cause);
    }
  }

  @Override
  public void reconnectingIn(int interval) {
    // Log.d(TAG, "reconnectingIn: interval="+interval);
  }

  @Override
  public void reconnectionFailed(Exception cause) {
    if (mConListener != null) {
      mConListener.onConnectionFailed(cause);
    }
  }

  @Override
  public void reconnectionSuccessful() {
    if (mConListener != null) {
      mConListener.onConnectionEstablished();
    }
  }
}
