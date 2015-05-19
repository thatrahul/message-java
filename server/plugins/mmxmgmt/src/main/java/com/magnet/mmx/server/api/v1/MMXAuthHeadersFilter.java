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
package com.magnet.mmx.server.api.v1;

import com.google.common.base.Strings;
import com.magnet.mmx.server.common.data.AppEntity;
import com.magnet.mmx.server.plugin.mmxmgmt.api.ErrorCode;
import com.magnet.mmx.server.plugin.mmxmgmt.api.ErrorResponse;
import com.magnet.mmx.server.plugin.mmxmgmt.util.DBUtil;
import com.magnet.mmx.server.plugin.mmxmgmt.util.MMXServerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 */
@Provider
@MMXHeaderAuth
public class MMXAuthHeadersFilter implements ContainerRequestFilter {
  private static final Logger LOGGER = LoggerFactory.getLogger(MMXAuthHeadersFilter.class);
  private static String MISSING_HEADER = "Authentication failed : mandatory header %s is missing";
  private static String INVALID_HEADER_VALUE = "Authentication failed : header %s has an invalid value %s";

  @Override
  public void filter(ContainerRequestContext containerRequestContext) throws IOException {
    String appId = containerRequestContext.getHeaders().getFirst(MMXServerConstants.HTTP_HEADER_APP_ID);
    String apiKey = containerRequestContext.getHeaders().getFirst(MMXServerConstants.HTTP_HEADER_REST_API_KEY);
    LOGGER.trace("filter : appId={}, apiKey={}", appId, apiKey);

    if (Strings.isNullOrEmpty(appId)) {
      LOGGER.error("filter : appId is empty");
      Response response = buildMissingHeaderResponse(MMXServerConstants.HTTP_HEADER_APP_ID);
      containerRequestContext.abortWith(response);
      return;
    }

    if (Strings.isNullOrEmpty(apiKey)) {
      LOGGER.error("filter : apiKey is empty");
      Response response = buildMissingHeaderResponse(MMXServerConstants.HTTP_HEADER_REST_API_KEY);
      containerRequestContext.abortWith(response);
      return;
    }

    AppEntity appEntity = DBUtil.getAppDAO().getAppForAppKey(appId);
    if (appEntity == null) {
      LOGGER.error("filter : appId={} not found", appId);
      Response response = buildInvalidHeaderResponse(ErrorCode.AUTH_BAD_APP_ID,
                                                     MMXServerConstants.HTTP_HEADER_APP_ID, appId);
      containerRequestContext.abortWith(response);
      return;
    }

    String appApiKey = appEntity.getAppAPIKey();
    if (!appApiKey.equals(apiKey)) {
      LOGGER.error("filter : apiKey={} not valid for appId={}", apiKey, appId);
      Response response = buildInvalidHeaderResponse(ErrorCode.AUTH_APPID_APIKEY_MISMATCH,
              MMXServerConstants.HTTP_HEADER_REST_API_KEY, apiKey);
      containerRequestContext.abortWith(response);
      return;
    }

    containerRequestContext.setProperty(MMXServerConstants.MMX_APP_ENTITY_PROPERTY, appEntity);
  }

  private Response buildMissingHeaderResponse(String header) {
    ErrorResponse mmxErrorResponse = new ErrorResponse(ErrorCode.AUTH_MISSING,
            String.format(MISSING_HEADER, header));
    Response httpErrorResponse = Response.status(Response.Status.UNAUTHORIZED)
            .entity(mmxErrorResponse).build();
    return httpErrorResponse;
  }

  private Response buildInvalidHeaderResponse(ErrorCode code, String header, String value) {
    ErrorResponse mmxErrorResponse = new ErrorResponse(code,
            String.format(INVALID_HEADER_VALUE, header, value));
    Response httpErrorResponse = Response.status(Response.Status.UNAUTHORIZED)
            .entity(mmxErrorResponse).build();
    return httpErrorResponse;
  }
}