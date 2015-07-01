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

import com.magnet.mmx.server.api.v1.protocol.HookCreateRequest;
import com.magnet.mmx.server.common.data.AppEntity;
import com.magnet.mmx.server.plugin.mmxmgmt.api.ErrorCode;
import com.magnet.mmx.server.plugin.mmxmgmt.api.ErrorResponse;
import com.magnet.mmx.server.plugin.mmxmgmt.db.HookDAO;
import com.magnet.mmx.server.plugin.mmxmgmt.db.HookDAOImpl;
import com.magnet.mmx.server.plugin.mmxmgmt.db.HookEntity;
import com.magnet.mmx.server.plugin.mmxmgmt.db.OpenFireDBConnectionProvider;
import com.magnet.mmx.server.plugin.mmxmgmt.util.MMXServerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.TimeUnit;

@Path("/apps/hooks")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class HookResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(HookResource.class);

  @Context
  private HttpServletRequest servletRequest;

  @POST
  public Response createHook(HookCreateRequest request) {
    final String methodName = "createHook";
    LOGGER.debug("Creating a hook");

    try {
      long startTime = System.nanoTime();
      AppEntity appEntity;
      Object o = servletRequest.getAttribute(MMXServerConstants.MMX_APP_ENTITY_PROPERTY);
      if (o instanceof AppEntity) {
        appEntity = (AppEntity) o;
        LOGGER.debug("createHook : retrieving appEntity from servletRequestContext entity={}", appEntity);
      } else {
        LOGGER.error("createHook : appEntity is not set");
        return Response
            .status(Response.Status.INTERNAL_SERVER_ERROR)
            .build();
      }
      String appId = appEntity.getAppId();
      HookDAO hookDAO = new HookDAOImpl(new OpenFireDBConnectionProvider());
      int id = hookDAO.addNewHook(appId, request);
      long endTime = System.nanoTime();
      LOGGER.info("{}: Completed processing in {} milliseconds", methodName,
          TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS));
      return Response.status(Response.Status.OK).build();
    } catch (Throwable t) {
      LOGGER.warn("Throwable in resource", t);
      throw new WebApplicationException(
          Response
              .status(Response.Status.INTERNAL_SERVER_ERROR)
              .entity(new ErrorResponse(ErrorCode.ISE_HOOK_RESOURCE, t.getMessage()))
              .build()
      );
    }
  }

  @GET
  @Path("{hookId}")
  public Response getHookUsingId(@PathParam("hookId") int hookId) {
    final String methodName = "getHookUsingId";
    try {
      long startTime = System.nanoTime();
      HookDAO hookDAO = new HookDAOImpl(new OpenFireDBConnectionProvider());
      HookEntity entity = hookDAO.getHook(hookId);
      long endTime = System.nanoTime();
      LOGGER.info("{}: Completed processing in {} milliseconds", methodName,
          TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS));

      if (entity == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      } else {
        return Response.status(Response.Status.OK).entity(entity).build();
      }
    } catch (Throwable t) {
      LOGGER.warn("Throwable in resource", t);
      throw new WebApplicationException(
          Response
              .status(Response.Status.INTERNAL_SERVER_ERROR)
              .entity(new ErrorResponse(ErrorCode.ISE_HOOK_RESOURCE, t.getMessage()))
              .build()
      );
    }
  }


}
