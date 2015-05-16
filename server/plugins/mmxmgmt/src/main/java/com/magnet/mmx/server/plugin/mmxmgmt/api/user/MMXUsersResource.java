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
package com.magnet.mmx.server.plugin.mmxmgmt.api.user;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.magnet.mmx.protocol.Constants;
import com.magnet.mmx.server.api.v1.MMXHeaderAuth;
import com.magnet.mmx.server.api.v1.RestUtils;
import com.magnet.mmx.server.common.data.AppEntity;
import com.magnet.mmx.server.plugin.mmxmgmt.api.AbstractBaseResource;
import com.magnet.mmx.server.plugin.mmxmgmt.api.ErrorCode;
import com.magnet.mmx.server.plugin.mmxmgmt.api.ErrorMessages;
import com.magnet.mmx.server.plugin.mmxmgmt.api.ErrorResponse;
import com.magnet.mmx.server.plugin.mmxmgmt.api.query.UserQuery;
import com.magnet.mmx.server.plugin.mmxmgmt.db.*;
import com.magnet.mmx.server.plugin.mmxmgmt.search.*;
import com.magnet.mmx.server.plugin.mmxmgmt.search.user.UserSearchOption;
import com.magnet.mmx.server.plugin.mmxmgmt.search.user.UserSortOption;
import com.magnet.mmx.server.plugin.mmxmgmt.util.DBUtil;
import com.magnet.mmx.server.plugin.mmxmgmt.util.Helper;
import com.magnet.mmx.server.plugin.mmxmgmt.util.MMXServerConstants;
import com.magnet.mmx.server.plugin.mmxmgmt.web.ValueHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 */
@Path("/users")
@MMXHeaderAuth
public class MMXUsersResource extends AbstractBaseResource {
  public static final String USERNAME_PARAM = "username";
  public static final String EMAIL_PARAM = "email";
  public static final String NAME_PARAM = "name";
  public static final String TAG_PARAM = "tag";

  private static final Logger LOGGER = LoggerFactory.getLogger(MMXUsersResource.class);

  @Context
  private HttpServletRequest servletRequest;

 // @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getUsers(@QueryParam(USERNAME_PARAM) String username,
                           @QueryParam(NAME_PARAM) String name,
                           @QueryParam(EMAIL_PARAM) String email,
                           @QueryParam(MMXServerConstants.SORT_BY_PARAM) String sortby,
                           @QueryParam(MMXServerConstants.SORT_ORDER_PARAM) String sortorder,
                           @QueryParam(MMXServerConstants.SIZE_PARAM) int size,
                           @QueryParam(MMXServerConstants.OFFSET_PARAM) int offset) {

    LOGGER.trace("getUsers : email={}, name={}, sortby={}, sortorder={}", new Object[]{email, name, sortby, sortorder});

    AppEntity appEntity = RestUtils.getAppEntity(servletRequest);
    if(appEntity == null) {
      return RestUtils.getInternalErrorJAXRSResp(new ErrorResponse(ErrorCode.UNKNOWN_ERROR, "App id not set"));
    }

    String appId = appEntity.getAppId();

    String searchBy = null;
    ValueHolder valueHolder = new ValueHolder();

    if(!Strings.isNullOrEmpty(username)) {
      searchBy = USERNAME_PARAM;
      valueHolder.setValue1(username);
    } else if(!Strings.isNullOrEmpty(name)) {
      searchBy = NAME_PARAM;
      valueHolder.setValue1(name);
    } else if(!Strings.isNullOrEmpty(email)) {
      searchBy = EMAIL_PARAM;
      valueHolder.setValue1(email);
    }

    UserSearchOption searchOption = UserSearchOption.find(searchBy);
    UserSortOption sortOptions = UserSortOption.build(sortby, sortorder).get(0);

    PaginationInfo pinfo = PaginationInfo.build(size, offset);
    UserDAO userDAO = DBUtil.getUserDAO();
    UserSearchResult searchResult = userDAO.searchUsers(appId, searchOption, valueHolder, sortOptions, pinfo);
    PostProcessor<UserEntity> postProcessor = new UserEntityPostProcessor();
    for (UserEntity me : searchResult.getResults()) {
      postProcessor.postProcess(me);
    }

    return Response.status(Response.Status.OK).entity(searchResult).build();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getUsers2(@QueryParam(USERNAME_PARAM) String username,
                           @QueryParam(NAME_PARAM) String name,
                           @QueryParam(EMAIL_PARAM) String email,
                           @QueryParam(MMXServerConstants.SORT_BY_PARAM) String sortby,
                           @QueryParam(MMXServerConstants.SORT_ORDER_PARAM) String sortorder,
                           @QueryParam(MMXServerConstants.SIZE_PARAM) int size,
                           @QueryParam(MMXServerConstants.OFFSET_PARAM) int offset,
                           @QueryParam(TAG_PARAM) List<String> tags) {

    LOGGER.trace("getUsers : email={}, name={}, sortby={}, sortorder={}", new Object[]{email, name, sortby, sortorder});

    try {
      AppEntity appEntity = RestUtils.getAppEntity(servletRequest);
      if(appEntity == null) {
        return RestUtils.getInternalErrorJAXRSResp(new ErrorResponse(ErrorCode.UNKNOWN_ERROR, "App id not set"));
      }

      if(size < 0)
        return RestUtils.getBadReqJAXRSResp(new ErrorResponse(ErrorCode.ILLEGAL_ARGUMENT,
                                                              "size should be non-zero positive value"));
      if(offset < 0)
        return RestUtils.getBadReqJAXRSResp(new ErrorResponse(ErrorCode.ILLEGAL_ARGUMENT,
                                                              "offset should be non-negative value"));
      String appId = appEntity.getAppId();
      UserQuery userQuery = new UserQuery();
      userQuery.setDisplayName(name);
      userQuery.setEmail(email);
      userQuery.setTags(tags);
      if(!Strings.isNullOrEmpty(username))
         userQuery.setUsername(Helper.getMMXUsername(username, appId));

      UserQueryBuilder.UserSortBy userSortBy = validateSortBy(sortby);

      if(userSortBy == null) {
        userSortBy = UserQueryBuilder.UserSortBy.USERNAME;
      }


      SortOrder sortOrder = Helper.validateSortOrder(sortorder);

      if (sortOrder == null) {
        sortOrder = SortOrder.ASCENDING;
      }

      SortInfo sortInfo = SortInfo.build(userSortBy.name(), sortOrder.name());
      PaginationInfo paginationInfo = PaginationInfo.build(size, offset);

      UserQueryBuilder userQueryBuilder = new UserQueryBuilder(true);
      QueryBuilderResult result = userQueryBuilder.buildSearchQuery(userQuery, appId, paginationInfo, sortInfo);
      SearchResult<UserEntity> userEntitySearchResult = DBUtil.getUserDAO().getUsersWithPagination(result, paginationInfo);
      UserSearchResult searchResult = new UserSearchResult();

      List<UserEntity> userEntities = userEntitySearchResult.getResults();

      Function<UserEntity, UserEntity> usernameTransformer =
              new Function<UserEntity,UserEntity>() {
                public UserEntity apply(UserEntity e) {
                  String username = Helper.removeSuffix(e.getUsername(), Character.toString(Constants.APP_ID_DELIMITER));
                  e.setUsername(username);
                  return e;
                }
              };


      List<UserEntity> userEntitiesXformed = Lists.transform(userEntities, usernameTransformer);

      searchResult.setResults(userEntitiesXformed);
      searchResult.setSize(userEntitiesXformed.size());
      searchResult.setOffset(userEntitySearchResult.getOffset());
      searchResult.setTotal(userEntitySearchResult.getTotal());
      Response response = Response
              .status(Response.Status.OK)
              .entity(searchResult)
              .build();
      return response;
    } catch (ValidationException e) {
      LOGGER.warn("ValidationException", e);
      throw new WebApplicationException(
              Response
                      .status(Response.Status.BAD_REQUEST)
                      .entity(e.getError())
                      .build()
      );
    }
  }

  private UserQueryBuilder.UserSortBy validateSortBy(String input) throws ValidationException {
    if (input == null || input.isEmpty()) {
      return null;
    }
    UserQueryBuilder.UserSortBy sortBy = UserQueryBuilder.UserSortBy.find(input);
    if (sortBy == null) {
      String message = String.format(ErrorMessages.ERROR_INVALID_SORT_BY_VALUE, input);
      LOGGER.warn(message);
      throw new ValidationException(new ErrorResponse(ErrorCode.INVALID_SORT_BY_VALUE, message));
    }
    return sortBy;
  }
}

