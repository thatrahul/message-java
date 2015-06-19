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

import com.magnet.mmx.protocol.Constants;
import com.magnet.mmx.server.plugin.mmxmgmt.api.ErrorCode;
import com.magnet.mmx.server.plugin.mmxmgmt.api.ErrorMessages;
import com.magnet.mmx.server.plugin.mmxmgmt.api.ErrorResponse;
import com.magnet.mmx.server.plugin.mmxmgmt.db.AppDAO;
import com.magnet.mmx.server.plugin.mmxmgmt.db.AppDAOImpl;
import com.magnet.mmx.server.plugin.mmxmgmt.db.ConnectionProvider;
import com.magnet.mmx.server.plugin.mmxmgmt.db.OpenFireDBConnectionProvider;
import com.magnet.mmx.server.plugin.mmxmgmt.util.JIDUtil;
import com.magnet.mmx.server.plugin.mmxmgmt.util.MMXServerConstants;
import com.magnet.mmx.server.plugin.mmxmgmt.util.MMXUserInfo;
import com.magnet.mmx.server.plugin.mmxmgmt.util.ServerNotInitializedException;
import com.magnet.mmx.server.plugin.mmxmgmt.util.UserManagerService;
import org.jivesoftware.openfire.user.UserAlreadyExistsException;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;

/**
 */

@Path("user/")
@Consumes(MediaType.APPLICATION_JSON)
public class MMXUserResource extends AbstractAdminResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(MMXUserResource.class);

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public Response createUsers(@Context HttpHeaders headers, MMXUserInfo userCreationInfo) {
    LOGGER.trace("createUsers : userCreationInfo={}", userCreationInfo);
    try {
      String appId = userCreationInfo.getAppId();
      AppDAO appDAO = new AppDAOImpl(getConnectionProvider());
      ErrorResponse authCheck = this.isAuthorized(headers, appDAO, appId);
      if (authCheck != null) {
        return Response
            .status(Response.Status.FORBIDDEN)
            .entity(authCheck)
            .build();
      }
      {
        ErrorResponse validationResponse = validateUserName(userCreationInfo);
        if (validationResponse != null) {
          return Response
              .status(Response.Status.BAD_REQUEST)
              .entity(validationResponse)
              .build();
        }
      }
      {
        ErrorResponse validationResponse = validatePassword(userCreationInfo);
        if (validationResponse != null) {
          return Response
              .status(Response.Status.BAD_REQUEST)
              .entity(validationResponse)
              .build();
        }
      }
      UserManagerService.createUser(userCreationInfo);
    } catch (UserAlreadyExistsException e) {
      LOGGER.info("createUser : exception caught userCreationInfo={}", userCreationInfo, e);
      String message = String.format(ErrorMessages.ERROR_USERNAME_EXISTS, userCreationInfo.getUsername());
      throw new WebApplicationException(
          Response
              .status(Response.Status.CONFLICT)
              .entity(new ErrorResponse(ErrorCode.INVALID_USER_NAME, message))
              .build()
      );
    } catch (ServerNotInitializedException e) {
      LOGGER.error("createUser : exception caught userCreationInfo={}", userCreationInfo, e);
      throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
    } catch (IllegalArgumentException e) {
      LOGGER.error("createUser : exception caught userCreationInfo={}", userCreationInfo, e);
      throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
    }
    return Response.status(Response.Status.CREATED).build();
  }

  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  public Response createOrReplace(@Context HttpHeaders headers, MMXUserInfo userCreationInfo) {
    LOGGER.trace("createOrReplace : userCreationInfo={}", userCreationInfo);
    boolean created = false;
    try {
      String appId = userCreationInfo.getAppId();
      AppDAO appDAO = new AppDAOImpl(getConnectionProvider());
      ErrorResponse authCheck = isAuthorized(headers, appDAO, appId);
      if (authCheck != null) {
        return Response
            .status(Response.Status.FORBIDDEN)
            .entity(authCheck)
            .build();
      }
      {
        ErrorResponse validationResponse = validateUserName(userCreationInfo);
        if (validationResponse != null) {
          return Response
              .status(Response.Status.BAD_REQUEST)
              .entity(validationResponse)
              .build();
        }
      }
      created = UserManagerService.updateUser(userCreationInfo);
      if (created)
        return Response.status(Response.Status.CREATED).build();
      else
        return Response.status(Response.Status.OK).build();
    } catch (ServerNotInitializedException e) {
      LOGGER.error("createOrReplace : exception caught userCreationInfo={}", userCreationInfo, e);
      throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
    } catch (UserNotFoundException e) {
      LOGGER.error("createOrReplace : exception caught userCreationInfo={}", userCreationInfo, e);
      throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
    } catch (IllegalArgumentException e) {
      LOGGER.error("createOrReplace : exception caught userCreationInfo={}", userCreationInfo, e);
      throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
    }
  }

  @DELETE
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response deleteUser(@Context HttpHeaders headers, MMXUserInfo userInfo) {
    LOGGER.trace("deleteUser : userInfo={}", userInfo);
    try {
      String appId = userInfo.getAppId();
      AppDAO appDAO = new AppDAOImpl(getConnectionProvider());
      ErrorResponse authCheck = this.isAuthorized(headers, appDAO, appId);
      if (authCheck != null) {
        return Response
            .status(Response.Status.FORBIDDEN)
            .entity(authCheck)
            .build();
      }
      UserManagerService.deleteUser(userInfo);
    } catch (UserNotFoundException e) {
      LOGGER.error("deleteUser : exception caught userInfo={}", userInfo, e);
      throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
    } catch (ServerNotInitializedException e) {
      LOGGER.error("deleteUser : exception caught userInfo={}", userInfo, e);
      throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
    }
    return Response.status(Response.Status.OK).build();
  }

  @DELETE
  @Path("{username}/app/{appId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response deleteUser(@Context HttpHeaders headers, @PathParam("username") String username, @PathParam("appId") String appId) {
    LOGGER.trace("deleteUser : username={}, appId={}", username, appId);
    MMXUserInfo userInfo = new MMXUserInfo();
    userInfo.setUsername(username);
    userInfo.setAppId(appId);
    try {
      AppDAO appDAO = new AppDAOImpl(getConnectionProvider());
      ErrorResponse authCheck = this.isAuthorized(headers, appDAO, appId);
      if (authCheck != null) {
        return Response
            .status(Response.Status.FORBIDDEN)
            .entity(authCheck)
            .build();
      }
      UserManagerService.deleteUser(userInfo);
    } catch (UserNotFoundException e) {
      LOGGER.error("deleteUser : exception caught userInfo={}", userInfo, e);
      throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
    } catch (ServerNotInitializedException e) {
      LOGGER.error("deleteUser : exception caught userInfo={}", userInfo, e);
      throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
    }
    return Response.status(Response.Status.OK).build();
  }

  protected ConnectionProvider getConnectionProvider() {
    return new OpenFireDBConnectionProvider();
  }


  protected ErrorResponse validateUserName(MMXUserInfo info) {
    String username = info.getUsername();
    ErrorResponse error;
    if (username == null || username.isEmpty()) {
      error = new ErrorResponse(ErrorCode.INVALID_USER_NAME.getCode(), ErrorMessages.ERROR_INVALID_USERNAME_VALUE);
      return error;
    }
    {
      int length = -1;
      try {
        length = username.getBytes(Constants.UTF8_CHARSET).length;
      } catch (UnsupportedEncodingException e) {
        LOGGER.warn("UnsupportedEncodingException", e);
      }
      if (length < Constants.MMX_MIN_USERID_LEN || length > Constants.MMX_MAX_USERID_LEN) {
        error = new ErrorResponse(ErrorCode.INVALID_USER_NAME.getCode(), String.format(ErrorMessages.ERROR_USERNAME_INVALID_LENGTH, Constants.MMX_MIN_USERID_LEN, Constants.MMX_MAX_USERID_LEN));
        return error;
      }
    }
    {
      boolean hasInvalidChars = JIDUtil.checkUsernameForInvalidCharacters(username);
      if (hasInvalidChars) {
        error = new ErrorResponse(ErrorCode.INVALID_USER_NAME.getCode(), ErrorMessages.ERROR_USERNAME_INVALID_CHARACTERS);
        return error;
      }
    }
    return null;
  }

  protected ErrorResponse validatePassword(MMXUserInfo info) {
    ErrorResponse error;
    //check password
    String password = info.getPassword();
    if (password == null || password.isEmpty()) {
      error = new ErrorResponse(ErrorCode.INVALID_USER_PASSWORD.getCode(), ErrorMessages.ERROR_INVALID_PASSWORD_VALUE);
      return error;
    }
    {
      int length = -1;
      try {
        length = password.getBytes(Constants.UTF8_CHARSET).length;
      } catch (UnsupportedEncodingException e) {
        LOGGER.warn("UnsupportedEncodingException", e);
      }
      if (length > MMXServerConstants.MMX_MAX_PASSWORD_LEN) {
        error = new ErrorResponse(ErrorCode.INVALID_USER_NAME.getCode(), String.format(ErrorMessages.ERROR_PASSWORD_INVALID_LENGTH, MMXServerConstants.MMX_MAX_PASSWORD_LEN));
        return error;
      }
    }
    return null;
  }


}
