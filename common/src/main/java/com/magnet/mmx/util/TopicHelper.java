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

package com.magnet.mmx.util;

import com.magnet.mmx.protocol.Constants;
import com.magnet.mmx.protocol.MMXTopicId;
import com.magnet.mmx.protocol.OSType;
import com.magnet.mmx.protocol.TopicAction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @hide
 * A helper class for topics in PubSub.
 */
public class TopicHelper {
  private static boolean TOPIC_RESTRICTED_NAME = true;  // true for MOB-1423
  public final static char TOPIC_DELIM = '/';
  public final static char TOPIC_FOR_APP = '*';
  public final static String TOPIC_GEOLOC = "com.magnet.geoloc";  // a leaf node
  public final static String TOPIC_OS_ROOT = "com.magnet.os";
  public final static String TOPIC_OS = TOPIC_OS_ROOT+"/";        // a collection node
  public final static String TOPIC_LEAF_ALL = "_all_";            // a leaf node
  public final static String TOPIC_NAME_PATTERN_STRING = "^[a-zA-Z0-9_\\.\\-]*$";

  private static final Pattern TOPIC_NAME_PATTERN = Pattern.compile(TOPIC_NAME_PATTERN_STRING);


  // the node is formulated as follows:
  // /<appId>/*/com.magnet.os/<osType>

  /**
   * Generate the container topic name of devices based on OS type. This container topic can be used
   * as the parent node for creating leaf topics for publishing events to
   * @param osType ANDROID, IOS
   * @return
   */
  public static String generateDeviceTopicName(OSType osType) {
    StringBuilder stringBuilder = new StringBuilder()
        .append(TOPIC_OS)
        .append(osType.name());
    return stringBuilder.toString();
  }

  /**
   * Generate the publishable topic name of devices based on OS type where all event related this OS type
   * can be published to
   * @param osType ANDROID, IOS
   * @return
   */
  public static String generateDeviceAllLeafTopicName(OSType osType) {
    StringBuilder stringBuilder = new StringBuilder()
        .append(TOPIC_OS)
        .append(osType.name())
        .append(TOPIC_DELIM)
        .append(TOPIC_LEAF_ALL);
    return stringBuilder.toString();
  }

  /**
   * @hide
   * Assuming that <code>topicId</code> is in the form of "/appId/*" or
   * "/appId/userId", check if it is a user topic. 
   * @param topicId
   * @return
   */
  public static boolean isUserTopic(String topicId) {
    int index = topicId.indexOf(TOPIC_DELIM, 1);
    return (index > 1) && (topicId.charAt(index+1) != TOPIC_FOR_APP);
  }
  
  /**
   * Check if the topic with specified id is a topic for the passed in appId
   * @param topicId A path should be started with "/appId/".
   * @param appId
   * @return
   */
  public static boolean isAppTopic(String topicId, String appId) {
    if (topicId == null || topicId.isEmpty()) {
      return false;
    }
    try {
      return ((topicId.charAt(0) == TOPIC_DELIM) &&
               topicId.startsWith(appId, 1) &&
               (topicId.charAt(1+appId.length()) == TOPIC_DELIM));
    } catch (IndexOutOfBoundsException e) {
      return false;
    }
  }

  /**
   * Check if the topic represented by the topicId is a user topic
   * @param topicId A non-null full topic path
   * @param appId A non-null app ID
   * @return true if it represents a user topic false other wise.
   */
  public static boolean isUserTopic(String topicId, String appId) {
    String prefix = new StringBuilder().append(TOPIC_DELIM).append(appId).append(TOPIC_DELIM).append(TOPIC_FOR_APP).append(TOPIC_DELIM).toString();
    try {
      int index = topicId.indexOf(prefix);
      if (index == -1) {
        return true;
      } else {
        return false;
      }
    } catch (IndexOutOfBoundsException e) {
      return false;
    }
  }
  
  /**
   * Construct an application prefix for the topic.
   * @param appId
   * @return The prefix as "/appId/"
   */
  public static String makePrefix(String appId) {
    return TOPIC_DELIM + appId + TOPIC_DELIM;
  }
  
  /**
   * Parse an XMPP nodeID into a AppTopic object.  There are two formats:
   * /appID/*&#x002Ftopic for global topic and /appID/userID/topic for personal
   * topic.
   * @param topic A XMPP PubSub nodeID string.
   * @return An AppTopic object, or null if not an MMX topic.
   */
  public static AppTopic parseTopic(String topic) {
    if (topic.charAt(0) != TOPIC_DELIM)
      return null;
    int index1 = topic.indexOf(TOPIC_DELIM, 1);
    if (index1 < 0)
      return null;
    int index2 = topic.indexOf(TOPIC_DELIM, index1+1);
    if (index2 < 0)
      return null;
    String appId = topic.substring(1, index1);
    String userId = topic.substring(index1+1, index2);
    String topicName = topic.substring(index2+1);
    return new AppTopic(appId, (userId.charAt(0) == TOPIC_FOR_APP) ?
                      null : userId, topicName);
  }
  
  /**
   * Parse an XMPP nodeID into a Topic ID object.  There are two formats:
   * /appID/*&#x002Ftopic for global topic and /appID/userID/topic for personal
   * topic.
   * @param nodeId A XMPP PubSub nodeID string.
   * @return A MMXTopicId, or null if not an MMX topic.
   */
  public static MMXTopicId parseNode(String nodeId) {
    if (nodeId.charAt(0) != TOPIC_DELIM)
      return null;
    int index1 = nodeId.indexOf(TOPIC_DELIM, 1);
    if (index1 < 0)
      return null;
    int index2 = nodeId.indexOf(TOPIC_DELIM, index1+1);
    if (index2 < 0)
      return null;
    String userId = nodeId.substring(index1+1, index2);
    String topicName = nodeId.substring(index2+1);
    return new MMXTopicId((userId.charAt(0) == TOPIC_FOR_APP) ?
        null : userId, topicName);
  }
  
  /**
   * @hide
   * Make a complete topic path.  There is a special root path if both userId
   * and topic are null.  The path may be "appID", "/appID/*", "/appID/userID",
   * "/appID/*\u002atopic", or "/appID/userID/topic".
   * @param appId The app ID.
   * @param userId A user ID or null.
   * @param topic A topic name.
   * @return
   */
  public static String makeTopic(String appId, String userId, String topic) {
    if (userId == null && topic == null) {
      return appId;
    }
    
    int len = appId.length() + 2;
    if (userId == null || userId.isEmpty()) {
      ++len;
      userId = null;
    } else {
      len += userId.length();
      userId = userId.toLowerCase();
    }
    if (topic != null) {
      if (topic.charAt(0) != TOPIC_DELIM) {
        ++len;
      }
      len += topic.length();
    }
    
    StringBuilder sb = new StringBuilder(len);
    sb.append(TOPIC_DELIM).append(appId).append(TOPIC_DELIM);
    if (userId == null) {
      sb.append(TOPIC_FOR_APP);
    } else {
      sb.append(userId);
    }
    if (topic != null) {
      if (topic.charAt(0) != TOPIC_DELIM) {
        sb.append(TOPIC_DELIM);
      }
      sb.append(topic.toLowerCase());
    }
    return sb.toString();
  }
  
  /**
   * Construct the OS topic with an optional version.
   * @param os An OS type, or null for all OS's.
   * @param version A version string, or null for all versions.
   * @return
   */
  public static String makeOSTopic(OSType os, String version) {
    if (os == null)
      return TOPIC_OS_ROOT;
    if (version == null)
      return TOPIC_OS + os.toString();
    else
      return TOPIC_OS + os.toString() + TOPIC_DELIM + version;
  }
  
  /**
   * @hide
   * Find the length of the prefix in the topic.  It is the third slash in the
   * real topic.  The prefix is "/appId/userId/".
   * @param path The topic full path.
   * @return The length of the prefix.
   */
  public static int getPrefixLength(String path) {
    int offset;
    offset = path.indexOf(TOPIC_DELIM, 1);
    offset = path.indexOf(TOPIC_DELIM, offset+1);
    return ++offset;
  }
  
  /**
   * @hide
   * Get the parent full path of the topic.  The parent full path would be 
   * "/appId/userId/parent..." where the prefix is "/appId/userId/".
   * @param prefix The prefix length.
   * @param path The topic full path.
   * @return null if no parent is found (, or full path of the parent.
   */
  public static String getParent(int prefix, String path) {
    int offset = path.lastIndexOf(TOPIC_DELIM);
    if (offset < prefix)
      return null;
    return path.substring(0, offset);
  }
  
  /**
   * Get the base name of the path.
   * @param path The full topic path
   * @return
   */
  public static String getBaseName(String path) {
    int offset = path.lastIndexOf(TOPIC_DELIM);
    if (offset < 0)
      return path;
    return path.substring(offset+1);
  }
  
  /**
   * Get the root node ID from the path.  In the current implementation, it is
   * the app ID.
   * @param path The real path of the topic.
   * @return The root node ID.
   */
  public static String getRootNodeId(String path) {
    int start = (path.charAt(0) == TOPIC_DELIM) ? 1 : 0;
    int offset = path.indexOf(TOPIC_DELIM, start);
    if (offset < 0) {
      return path.substring(start);
    } else {
      return path.substring(start, offset);
    }
  }
  
  /**
   * @hide
   * Normalize the topic path by collapsing all contiguous '/' and make it lower
   * case.  It also makes sure that topic cannot be null, empty, start or end
   * with '/'. 
   * @param path A topic path.
   * @return A normalized topic path.
   * @throws IllegalArgumentException Topic cannot be null or empty.
   * @throws IllegalArgumentException Topic cannot start or end with '/'.
   */
  public static String normalizePath(String path) {
    if (path == null || path.isEmpty()) {
      throw new IllegalArgumentException("Topic cannot be null or empty");
    }
    if (path.charAt(0) == TopicHelper.TOPIC_DELIM ||
        path.charAt(path.length()-1) == TopicHelper.TOPIC_DELIM) {
      throw new IllegalArgumentException("Topic cannot start or end with '/'");
    }
    StringBuilder sb = new StringBuilder(path.length());
    char prev = '\0';
    for (char c : path.toCharArray()) {
      if (c == TopicHelper.TOPIC_DELIM) {
        if (prev == TopicHelper.TOPIC_DELIM) {
          continue;
        }
      } else {
        if (prev == TopicHelper.TOPIC_DELIM) {
          sb.append(TopicHelper.TOPIC_DELIM);
        }
        //MOB-2406:No longer lower case topic names
        sb.append(c);
      }
      prev = c;
    }
    return sb.toString();
  }
  
  /**
   * @hide
   * Validate the topic name. 
   * @param topic A non-null topic with path syntax.
   * @throws IllegalArgumentException Topic cannot contain '/'.
   */
  public static void checkPathAllowed(String topic) {
    if (topic == null || topic.isEmpty()) {
      throw new IllegalArgumentException("The topic name cannot be null or empty");
    }
    if (topic.length() > Constants.MMX_MAX_TOPIC_LEN) {
      throw new IllegalArgumentException("The length of topic name exceeds "+Constants.MMX_MAX_TOPIC_LEN);
    }
    if (TOPIC_RESTRICTED_NAME) {
      if (topic.indexOf(TopicHelper.TOPIC_DELIM) >= 0) {
        throw new IllegalArgumentException(
            "The path syntax is disabled; topic cannot contain '/'");
      }
    }
  }

  public static boolean validatePublisherType(String publisherType) {
    for (TopicAction.PublisherType type : TopicAction.PublisherType.values()) {
      if (type.name().equals(publisherType)) {
        return true;
      }
    }
    return false;
  }
  
  public static void restrictPathSyntax(boolean restrict) {
    TOPIC_RESTRICTED_NAME = restrict;
  }

  /**
   * Validate supplied application topic name
   * @param topicName
   * @return true if the topic name is valid. false if the topic name is invalid.
   */
  public static boolean validateApplicationTopicName (String topicName) {
    if (topicName == null || topicName.isEmpty()) {
      return false;
    }
    int length = topicName.length();
    if (length > Constants.MMX_MAX_TOPIC_LEN) {
      return false;
    }
    Matcher matcher = TOPIC_NAME_PATTERN.matcher(topicName);
    return matcher.matches();
  }
}
