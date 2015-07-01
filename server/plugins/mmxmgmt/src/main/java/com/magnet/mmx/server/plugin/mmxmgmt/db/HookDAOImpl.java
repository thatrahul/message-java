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

import com.magnet.mmx.server.api.v1.protocol.HookCreateRequest;
import com.magnet.mmx.server.plugin.mmxmgmt.hook.HookType;
import com.magnet.mmx.util.GsonData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class HookDAOImpl implements HookDAO {
  private static final Logger LOGGER = LoggerFactory.getLogger(HookDAOImpl.class);

  private static final String INSERT_HOOK = "INSERT INTO mmxWebHook (appId,hookName,targetURL,eventType,eventConfig) " +
      " VALUES (?,?,?,?,?)";

  private static final String SELECT_HOOK_USING_APP_ID_TYPE = "SELECT id, appId, hookName, targetURL, eventType, " +
      "eventConfig, dateCreated, dateUpdated FROM mmxWebHook WHERE appId = ? AND eventType = ?";

  private static final String SELECT_HOOK_USING_ID = "SELECT id, appId, hookName, targetURL, eventType, " +
      "eventConfig, dateCreated, dateUpdated FROM mmxWebHook WHERE id = ?";

  private ConnectionProvider provider;

  public HookDAOImpl(ConnectionProvider provider) {
    this.provider = provider;
  }

  @Override
  public int addNewHook(String appId, HookCreateRequest details) throws DbInteractionException {
    LOGGER.debug("Creating a new hook for appId:{}", appId);
    Connection con = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    Integer rv = null;
    try {
      con = provider.getConnection();
      pstmt = con.prepareStatement(INSERT_HOOK, PreparedStatement.RETURN_GENERATED_KEYS);
      pstmt.setString(1, appId);
      pstmt.setString(2, details.getHookName());
      pstmt.setString(3, details.getTargetURL());
      pstmt.setString(4, details.getEventType());

      String eventConfigJSON = toJSON(details.getEventConfig());
      if (eventConfigJSON != null) {
        pstmt.setString(5, eventConfigJSON);
      } else {
        pstmt.setNull(5, Types.VARCHAR);
      }
      pstmt.executeUpdate();
      rs = pstmt.getGeneratedKeys();

      if (rs.next()) {
        rv = Integer.valueOf(rs.getInt(1));
      }
      rs.close();
      pstmt.close();
      con.close();
      return rv;
    } catch (SQLException e) {
      LOGGER.warn("SQL Exception in creating hook", e);
      throw new DbInteractionException(e);
    } finally {
      CloseUtil.close(LOGGER, rs, pstmt, con);
    }
  }

  @Override
  public List<HookEntity> getHooks(String appId, HookType hookType) throws DbInteractionException {
    Connection conn = null;
    PreparedStatement preparedStatement = null;
    ResultSet rs = null;
    List<HookEntity> entityList = new ArrayList<HookEntity>();
    try {
      conn = provider.getConnection();
      preparedStatement = conn.prepareStatement(SELECT_HOOK_USING_ID);
      preparedStatement.setString(1, appId);
      preparedStatement.setString(2, hookType.name());
      rs = preparedStatement.executeQuery();
      while(rs.next()) {
        entityList.add(new HookEntityBuilder().build(rs));
      }
      return entityList;
    } catch (SQLException e) {
      LOGGER.error("getHook : caught exception when retrieving hooks for appId:{} and hookType:{}", appId, hookType);
      throw new DbInteractionException(e);
    } finally {
      CloseUtil.close(LOGGER, rs, preparedStatement, conn);
    }
  }

  @Override
  public HookEntity getHook(int hookId) throws DbInteractionException {
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      conn = provider.getConnection();
      pstmt = conn.prepareStatement(SELECT_HOOK_USING_ID);
      pstmt.setInt(1, hookId);
      rs = pstmt.executeQuery();
      if(rs.next()) {
        return new HookEntityBuilder().build(rs);
      } else {
        return null;
      }
    } catch (SQLException e) {
      LOGGER.error("getHook : caught exception when retrieving hook for id:{}", hookId);
      throw new DbInteractionException(e);
    } finally {
      CloseUtil.close(LOGGER, pstmt, conn);
    }
  }

  /**
   * HookEntityBuilder.
   */
  public static class HookEntityBuilder {
    /**
     * Build HookEntity using the supplied result set.
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    public HookEntity build(ResultSet rs) throws SQLException {
      int id = rs.getInt("id");
      String name = rs.getString("hookName");
      String appId = rs.getString("appId");
      String eventType = rs.getString("eventType");
      String json = rs.getString("eventConfig");

      Map<String, String> config = null;
      if (json != null) {
         config = fromJSON(json);
      }
      String targetURL = rs.getString("targetURL");
      Date created = rs.getTimestamp("dateCreated");
      Date updated = rs.getTimestamp("dateUpdated");

      HookEntity entity = new HookEntity();
      entity.setId(id);
      entity.setAppId(appId);
      entity.setHookName(name);
      entity.setEventType(eventType);
      entity.setTargetURL(targetURL);
      entity.setEventConfig(config);
      entity.setDateCreated(created);
      entity.setDateUpdated(updated);
      return entity;
    }
  }


  public static String toJSON(Map <String, String> eventConfig) {
    if (eventConfig == null || eventConfig.isEmpty()) {
      return null;
    }
    String json = GsonData.getGson().toJson(eventConfig);
    return json;
  }

  public static Map<String, String> fromJSON(String json) {
    if (json == null || json.isEmpty()) {
      return null;
    }
    Map<String, String> dictionary = GsonData.getGson().fromJson(json, Map.class);
    return dictionary;
  }
}
