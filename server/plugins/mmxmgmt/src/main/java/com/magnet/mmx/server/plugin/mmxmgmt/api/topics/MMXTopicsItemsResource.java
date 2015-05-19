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
package com.magnet.mmx.server.plugin.mmxmgmt.api.topics;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.magnet.mmx.server.plugin.mmxmgmt.api.AbstractBaseResource;
import com.magnet.mmx.server.plugin.mmxmgmt.api.ErrorCode;
import com.magnet.mmx.server.plugin.mmxmgmt.api.ErrorResponse;
import com.magnet.mmx.server.plugin.mmxmgmt.db.TopicItemEntity;
import com.magnet.mmx.server.plugin.mmxmgmt.message.MMXPubSubItem;
import com.magnet.mmx.server.plugin.mmxmgmt.message.PubSubItemResult;
import com.magnet.mmx.server.plugin.mmxmgmt.util.DBUtil;
import com.magnet.mmx.server.plugin.mmxmgmt.util.MMXServerConstants;
import com.magnet.mmx.util.TopicHelper;
import org.jivesoftware.util.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

/**
 */

@Path("/topics/{"+MMXServerConstants.TOPICNAME_PATH_PARAM + "}/items")
public class MMXTopicsItemsResource extends AbstractBaseResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(MMXTopicsItemsResource.class);
  public static final String SORT_PARAM = "sort";
  public static final String SINCE_PARAM = "since";
  public static final String UNTIL_PARAM = "until";
  public static final String TOPIC_NAME_PARAM = "topic";
  private static final String DEFAULT_MAX_ITEMS = "200";

  @GET
  @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
  public Response getItems(@Context HttpHeaders headers,
                       @PathParam(MMXServerConstants.TOPICNAME_PATH_PARAM) String topicName,
                       @QueryParam(SORT_PARAM) String order,
                       @QueryParam(SINCE_PARAM) String since,
                       @QueryParam(UNTIL_PARAM) String until,
                       @QueryParam(MMXServerConstants.SIZE_PARAM)
                       @DefaultValue(DEFAULT_MAX_ITEMS) int size) {
    ErrorResponse errorResponse = isAuthenticated(headers, DBUtil.getAppDAO());

    if(errorResponse != null) {
      return errorResponse.toJaxRSResponse();
    }

    String appId = headers.getRequestHeaders().getFirst(MMXServerConstants.HTTP_HEADER_APP_ID);

    boolean hasSince = !Strings.isNullOrEmpty(since);
    boolean hasUntil = !Strings.isNullOrEmpty(until);
    boolean hasTopicName = !Strings.isNullOrEmpty(topicName);
    boolean hasOrder = !Strings.isNullOrEmpty(order);

    if(!hasTopicName) {
      return new ErrorResponse(ErrorCode.ILLEGAL_ARGUMENT.getCode(), "Topic parameter not set").toJaxRSResponse();
    }

    Date dateSince = null;
    Date dateUntil = null;

    if(hasSince) {
      try {
        dateSince = new DateTime(since).toDate();
      } catch (Exception e) {
        return new ErrorResponse(ErrorCode.ILLEGAL_ARGUMENT.getCode(),
                "\"since\" param is not a valid ISO-8601 date").toJaxRSResponse();
      }
    }

    if(hasUntil) {
      try {
        dateUntil = new DateTime(until).toDate();
      } catch (Exception e) {
        return new ErrorResponse(ErrorCode.ILLEGAL_ARGUMENT.getCode(),
                "\"until\" date param is not a valid ISO-8601 date").toJaxRSResponse();
      }
    }

    if(hasOrder) {
      if(!order.equals(MMXServerConstants.SORT_ORDER_ASCENDING) &&
         !order.equals(MMXServerConstants.SORT_ORDER_DESCENDING)) {
        return new ErrorResponse(ErrorCode.ILLEGAL_ARGUMENT.getCode(),
                "\"order\" param " + "\"order\"" + " is not valid").toJaxRSResponse();
      }
    } else {
      order = MMXServerConstants.SORT_ORDER_ASCENDING;
    }

    String nodeId = TopicHelper.makeTopic(appId, null, topicName);

    LOGGER.trace("getItems : constructed nodeId={}", nodeId);

    List<TopicItemEntity> topicItemEntities = null;

    if(!hasSince && !hasUntil) {
        topicItemEntities = getItems(nodeId, size, order);
    } else if(hasSince && !hasUntil) {
      topicItemEntities = getItemsSince(nodeId, size, dateSince);
    } else if(!hasSince && hasUntil) {
      topicItemEntities = getItemsUntil(nodeId, size, dateUntil);
    } else if(hasSince && hasUntil) {
      topicItemEntities = getItems(nodeId, size, dateSince, dateUntil, order);
    }

    List<MMXPubSubItem> items = getPublishedItems(appId, topicItemEntities);
    PubSubItemResult pubsubItemResult = getPubsubItemResult(nodeId, items);

    return Response.status(Response.Status.OK).entity(pubsubItemResult).build();
  }

  public List<TopicItemEntity> getItems(String nodeId, int maxItems, String order) {
    return DBUtil.getTopicItemDAO().getItems(MMXServerConstants.DEFAULT_PUBSUB_SERVICE_ID, nodeId, maxItems, order);
  }

  public List<TopicItemEntity> getItemsSince(String nodeId, int maxItems, Date since) {
    String sinceDateStr = StringUtils.dateToMillis(since);
    return DBUtil.getTopicItemDAO().getItemsSince(MMXServerConstants.DEFAULT_PUBSUB_SERVICE_ID,
                                                  nodeId, maxItems, sinceDateStr);
  }

  public List<TopicItemEntity> getItemsUntil(String nodeId, int maxItems, Date until) {
    String untilDateStr = StringUtils.dateToMillis(until);
    return DBUtil.getTopicItemDAO().getItemsUntil(MMXServerConstants.DEFAULT_PUBSUB_SERVICE_ID,
            nodeId, maxItems, untilDateStr);
  }

  public List<TopicItemEntity> getItems(String nodeId, int maxItems, Date since, Date until, String order) {
    String sinceDateStr = StringUtils.dateToMillis(since);
    String untilDateStr = StringUtils.dateToMillis(until);
    return DBUtil.getTopicItemDAO().getItems(MMXServerConstants.DEFAULT_PUBSUB_SERVICE_ID,
                                              nodeId, maxItems, sinceDateStr, untilDateStr, order);
  }

  private List<MMXPubSubItem> getPublishedItems(final String appId, List<TopicItemEntity> entityList) {
    Function<TopicItemEntity, MMXPubSubItem> entityToItem =
            new Function<TopicItemEntity, MMXPubSubItem>() {
              public MMXPubSubItem apply(TopicItemEntity i) {
                return new MMXPubSubItem(appId, TopicHelper.parseNode(i.getNodeId()).getName(), i.getId(), new JID(i.getJid()), i.getPayload());
              };
            };

    return Lists.transform(entityList, entityToItem);
  }

  private PubSubItemResult getPubsubItemResult(String nodeId, List<MMXPubSubItem> items) {
    int count = DBUtil.getTopicItemDAO().getCount(MMXServerConstants.DEFAULT_PUBSUB_SERVICE_ID, nodeId);
    return new PubSubItemResult(count, items);
  }

}