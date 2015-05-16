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

package com.magnet.mmx.server.plugin.mmxmgmt.db;

import com.magnet.mmx.protocol.APNS;
import com.magnet.mmx.protocol.AppRead;
import com.magnet.mmx.protocol.GCM;
import com.magnet.mmx.protocol.MMXStatus;
import com.magnet.mmx.protocol.MyAppsRead;
import com.magnet.mmx.server.common.data.AppEntity;
import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.openfire.auth.AuthFactory;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;

import static com.magnet.mmx.protocol.AppRead.Response;

@Deprecated
public class DatabaseHandler {
  private static final Logger Log = LoggerFactory.getLogger(DatabaseHandler.class);

  private static final String INSERT_USER =
      "INSERT INTO ofUser (username,plainPassword,encryptedPassword,name,email,creationDate,modificationDate) " +
          "VALUES (?,?,?,?,?, ?, ?)";

  private static final String DELETE_APP_BY_ID = "DELETE FROM mmxApp where serverUserId = ?";
  private static final String DELETE_APP = "DELETE FROM mmxApp WHERE appId = ?";
  private static final String DELETE_ALL_APPS = "DELETE FROM mmxApp";

  private static final String QUERY_APP_BY_APPKEY = AppEntity.APP_QUERY_STRING + " WHERE appId = ?";
  private static final String QUERY_MY_APPS = AppEntity.APP_QUERY_STRING + " WHERE ownerId = ?";
  private static final String QUERY_APP_BY_NAME = AppEntity.APP_QUERY_STRING + " WHERE UPPER(appName) = ?";

  private static final String QUERY_SERVER_USER = "SELECT serverUserId FROM mmxApp WHERE appId = ?";
  private static final String QUERY_CLIENT_BOOTSTRAP_USER = "SELECT guestUserId FROM mmxApp WHERE appId = ?";

  private static final String QUERY_APPID_BY_APIKEY = "SELECT appId FROM mmxApp WHERE apiKey = ?";
  private static final String QUERY_APP_USING_APPID = AppEntity.APP_QUERY_STRING + " WHERE appId = ?";

  private static DatabaseHandler instance = null;

  // For unit testing
  private Connection dbConnection = null;

  protected DatabaseHandler() {
  }

  public static DatabaseHandler getInstance() {
    if (instance == null) {
      instance = new DatabaseHandler();
    }
    return instance;
  }


  public boolean deleteAllApps() throws SQLException {
    Connection con = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    boolean isCompleted = false;
    try {
      con = getConnection();
      pstmt = con.prepareStatement(DELETE_ALL_APPS);
      System.out.println("pstmt.execute");
      pstmt.executeUpdate();
      pstmt.close();

    } catch (SQLException sqle) {
      System.out.println("exception thrown: " + sqle.getMessage());
      Log.error(sqle.getMessage(), sqle);
      isCompleted = false;
    } finally {
      closeConnection(rs, pstmt, con);
    }
    return isCompleted;
  }

  public String getBootstrapClientUser(String appId)
      throws AppDoesntExistException {
    Connection con = getConnection();
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    ResultSet resultSet = null;
    String clientUser = null;
    try {
      pstmt = con.prepareStatement(QUERY_CLIENT_BOOTSTRAP_USER);
      pstmt.setString(1, appId);

      resultSet = pstmt.executeQuery();
      if (!resultSet.first()) {
        throw new AppDoesntExistException("No app found with app key = " + appId);
      }

      clientUser = resultSet.getString(1);
      resultSet.close();
      pstmt.close();
    } catch (SQLException e) {
      Log.error(e.getMessage(), e);
      throw new AppDoesntExistException(e);
    } finally {
      closeConnection(rs, pstmt, con);
    }

    return clientUser;
  }

  public String getServerUser(String appId)
      throws AppDoesntExistException {
    Connection con = getConnection();
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    ResultSet resultSet = null;
    String serverUser = null;
    try {
      pstmt = con.prepareStatement(QUERY_SERVER_USER);
      pstmt.setString(1, appId);

      resultSet = pstmt.executeQuery();
      if (!resultSet.first()) {
        throw new AppDoesntExistException("No app found with app key = " + appId);
      }

      serverUser = resultSet.getString(1);
      resultSet.close();
      pstmt.close();
    } catch (SQLException e) {
      Log.error(e.getMessage(), e);
      throw new AppDoesntExistException(e);
    } finally {
      closeConnection(rs, pstmt, con);
    }

    return serverUser;
  }


  public MMXStatus updateAppGuestSecret(String appId, String secret) throws AppDoesntExistException {

    MMXStatus response = new MMXStatus();
    if (secret == null || appId == null) {
      response.setCode(HttpServletResponse.SC_BAD_REQUEST);
      response.setMessage("Invalid parameters for updating app " + appId);
      return response;
    }

    StringBuilder sb = new StringBuilder();
    sb.append("UPDATE mmxApp SET ");
    sb.append(AppEntity.COL_GUEST_SECRET + " = ?");
    sb.append(", modificationDate = ? WHERE appId = ?");
    int ind = 1;
    Connection con = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
      con = getConnection();
      pstmt = con.prepareStatement(sb.toString());
      pstmt.setString(ind++, secret);
      pstmt.setTimestamp(ind++, new Timestamp(new java.util.Date().getTime()));
      pstmt.setString(ind++, appId);
      int updated = pstmt.executeUpdate();
      pstmt.close();
      if (updated != 1) {
        throw new AppDoesntExistException("No app found with appId = " + appId);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeConnection(rs, pstmt, con);
    }

    response = new MMXStatus();
    response.setCode(HttpServletResponse.SC_OK);
    response.setMessage("Successfully updated app " + appId);
    return response;
  }

  public MyAppsRead.Response getAppByName(String appName) throws AppDoesntExistException {
    Connection con = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    MyAppsRead.Response responseList = new MyAppsRead.Response();
    try {
      con = getConnection();
      pstmt = con.prepareStatement(QUERY_APP_BY_NAME);
      pstmt.setString(1, appName);

      rs = pstmt.executeQuery();
      while (rs.next()) {
        AppRead.Response response = getAppProps(rs);
        responseList.addResponse(response);
      }
    } catch (SQLException e) {
      Log.error(e.getMessage(), e);
      throw new AppDoesntExistException(e);
    } finally {
      closeConnection(rs, pstmt, con);
    }

    return responseList;
  }
  public User createUser(String username, String password, String name, String email, boolean passwordEncrypted)
      throws Exception {
    Date now = new Date();
    Connection con = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      con = getConnection();
      pstmt = con.prepareStatement(INSERT_USER);
      pstmt.setString(1, username);
      if (password == null) {
        pstmt.setNull(2, Types.VARCHAR);
      } else {
        pstmt.setString(2, password);
      }
      if (!passwordEncrypted) {
        pstmt.setNull(3, Types.VARCHAR);
      } else {
        pstmt.setString(3, password);
      }
      if (name == null || name.matches("\\s*")) {
        pstmt.setNull(4, Types.VARCHAR);
      } else {
        pstmt.setString(4, name);
      }
      if (email == null || email.matches("\\s*")) {
        pstmt.setNull(5, Types.VARCHAR);
      } else {
        pstmt.setString(5, email);
      }
      pstmt.setString(6, StringUtils.dateToMillis(now));
      pstmt.setString(7, StringUtils.dateToMillis(now));
      pstmt.execute();
    } catch (Exception e) {
      Log.error("create user error", e);
      throw e;
    } finally {
      closeConnection(rs, pstmt, con);
    }
    return new User(username, name, email, now, now);
  }

  // Used by unit tests only
  public void setConnection(Connection conn) {
    dbConnection = conn;
  }

  private Connection getConnection() {
    Connection con = null;
    if (null == dbConnection) {
      try {
        con = DbConnectionManager.getConnection();
      } catch (SQLException sqle) {
      }
    } else {
      con = dbConnection;
    }
    return con;
  }

  private void closeConnection(ResultSet rs, PreparedStatement pstmt, Connection con) {
    if (null == dbConnection) {
      DbConnectionManager.closeConnection(rs, pstmt, con);
    }
  }

  private Response getAppProps(ResultSet rs) throws SQLException {
    AppRead.Response response = new AppRead.Response();

    String apiKey = rs.getString(AppEntity.COL_API_KEY);
    if (apiKey == null) {
      apiKey = getDecrypted(rs.getString(AppEntity.COL_ENCRYPTED_API_KEY));
    }
    response.setApiKey(apiKey);
    response.setAppName(rs.getString(AppEntity.COL_APP_NAME));
    response.setAppId(rs.getString(AppEntity.COL_APP_ID));

    response.setGuestUserId(rs.getString(AppEntity.COL_GUEST_USER_ID));
    String guestUserSecret = rs.getString(AppEntity.COL_GUEST_SECRET);
    if (guestUserSecret != null) {
      response.setGuestUserSecret(guestUserSecret);
    }
    //TODO format the timestamp to standard format
//    response.setCreationDate(rs.getDate(AppEntity.COL_CREATION_DATE).toString());
//    response.setModificationDate(rs.getDate(AppEntity.COL_MODIFICATION_DATE).toString());
    response.setOwnerId(rs.getString(AppEntity.COL_OWNER_ID));

    GCM gcm = new GCM(rs.getString(AppEntity.COL_GOOGLE_PROJECT_ID), 
                      rs.getString(AppEntity.COL_GOOGLE_API_KEY));
    response.setGcm(gcm);

    String certPwd = rs.getString(AppEntity.COL_APNS_CERT_PLAIN_PASSWORD);
    if (certPwd == null) {
      certPwd = getDecrypted(rs.getString(AppEntity.COL_APNS_CERT_ENCRYPTED_PASSWORD));
    }
    // TODO: why APNS certificate is not sent back?
    APNS apns = new APNS(null, certPwd);
    response.setApns(apns);

    return response;
  }

  private String getEncrypted(String value) {
    String encrypted = null;
    try {
      encrypted = AuthFactory.encryptPassword(value);
      // Set password to null so that it's inserted that way.
    } catch (UnsupportedOperationException uoe) {
      // Encrypting the apiKey may have failed. Therefore,

    }
    return encrypted;
  }

  private String getDecrypted(String value) {
    String decrypted = null;
    try {
      decrypted = AuthFactory.decryptPassword(value);
      // Set password to null so that it's inserted that way.
    } catch (UnsupportedOperationException uoe) {
      // Encrypting the apiKey may have failed. Therefore,

    }
    return decrypted;
  }
}
