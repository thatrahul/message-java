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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jivesoftware.smack.util.StringUtils;

import com.magnet.mmx.client.common.MMXid;
import com.magnet.mmx.protocol.Constants;

/**
 * A client side utility to parse MMX ID (XID.)  A normal full XID has a
 * format of "node@domain/resource", and a multi-tenant XID partitioned by
 * applications has a raw format of "userID@appId@domain/resource".  Using
 * the Jabber ID escaping, the node consists of "userID@appId" and the userID
 * still allows email address format.  The multi-tenant delimiter is
 * subject to change without further notice.
 */
public class XIDUtil {
  public final static boolean AT_SIGN_NOT_SUPPORTED = true;  // asmack specific
  public final static String INVALID_CHARS = AT_SIGN_NOT_SUPPORTED ? "'/', '%', or '@'" : "'/' or '%'";
  private static boolean sNoEsc;

  /**
   * Validate a user ID (the node part without %appId).  Current embedded '@'
   * is not allowed; asmack library does not work with or without JID escaping.
   * @param userId The user ID without %appId.
   * @return true if valid; otherwise, false.
   */
  public static boolean validateUserId(String userId) {
    char[] buf = userId.toCharArray();
    for (char c : buf) {
      if (c == '/' || c == '%') {
        return false;
      }
      if (AT_SIGN_NOT_SUPPORTED && c == '@') {
        return false;
      }
    }
    return true;
  }
  /**
   * Disable XEP-0106 node escaping because asmack does it already but smack
   * does not.
   * @param enable
   */
  public static void disableEsc(boolean disable) {
    sNoEsc = disable;
  }

  /**
   * Get the bared MMX ID.
   * @param xid
   * @return
   */
  public static String getBaredXID(String xid) {
    int index;
    if ((index = xid.lastIndexOf('/')) < 0) {
      return xid;
    }
    return xid.substring(0, index);
  }

  /**
   * @hide
   * Create an escape node of the JID with userId and appId.  The node will
   * consist of userID%appId and it will be escaped according to XEP-0106.
   * @param userId
   * @param appId
   * @return
   */
  public static String makeEscNode(String userId, String appId) {
    String node = makeNode(userId, appId);
    return sNoEsc ? node : StringUtils.escapeNode(node);
  }

  /**
   * @hide
   * Create a raw node of the JID with userName and appId.  The node will
   * consist of userId and appId, but it is not escaped according to XEP-0106
   * yet.
   * @param userId
   * @param appId
   * @return A node in XID.
   */
  public static String makeNode(String userId, String appId) {
    if (appId == null || appId.isEmpty()) {
      return userId;
    } else {
      return userId + Constants.APP_ID_DELIMITER + appId;
    }
  }

  /**
   * Get the escaped node which consists of userId and appId (maybe.)  The node
   * conforms with XEP-0106.
   * @param xid A MMX ID conforming with XEP-0106.
   * @return A node conforming with XEP-0106.
   */
  public static String getEscNode(String xid) {
    if (xid == null) {
      return null;
    }
    int atpos = xid.lastIndexOf('@');
    if (atpos <= 0) {
      return xid;
    }
    return xid.substring(0, atpos);
  }

  /**
   * Get the escape node which consists of userId and appId.  The node conforms
   * with XEP-0106.
   * @param jid A JID conforming with XEP-0106.
   * @return A node conforming with XEP-0106.
   */
//  public static String getEscNode(JID jid) {
//    return jid.getNode();
//  }

  /**
   * @hide
   * Parse a node for userID and appId.
   * @param node
   * @return An array of userID and appId which may be null.
   */
  public static String[] parseNode(String node) {
    int sep = node.lastIndexOf(Constants.APP_ID_DELIMITER);
    if (sep < 0) {
      return new String[] { node, null };
    } else {
      return new String[] { node.substring(0, sep), node.substring(sep+1) };
    }
  }

  /**
   * @hide
   * Get the unescaped (raw) node which consists of userId and appId.
   * @param xid A MMX ID conforming with XEP-0106.
   * @return A raw node of "userId%appId".
   */
  public static String getNode(String xid) {
    String node = getEscNode(xid);
    if (node == null) {
      return null;
    }
    return sNoEsc ? node : StringUtils.unescapeNode(node);
  }

  /**
   * Get the unescaped (raw) node which consists of userId and appId.
   * @param jid A JID conforming with XEP-0106.
   * @return A raw node of "userId%appId".
   */
//  public static String getNode(JID jid) {
//    return JID.unescapeNode(jid.getNode());
//  }

  /**
   * @hide
   * Parse a MMX ID for userId and appId.
   * @param xid A MMX ID conforming with XEP-0106.
   * @return null, or an array with userId and appId.
   */
  public static String[] parseJID(String xid) {
    String node = getNode(xid);
    if (node == null) {
      return null;
    }
    return parseNode(node);
  }

  /**
   * Convert an MMX ID  (XID) into an end-point object.
   * @param xid A string form of MMX ID.
   * @return A end-point object, or null if it is malformed.
   */
  public static MMXid toXid(String xid) {
    String node = getNode(xid);
    if (node == null) {
      return null;
    }
    String resource = getResource(xid);
    int sep = node.lastIndexOf(Constants.APP_ID_DELIMITER);
    String userId = (sep < 0) ? node : node.substring(0, sep);
    return new MMXid(userId, resource);
  }

  /**
   * Parse the node from JID into a userId and appId.
   * @param jid A JID conforming with XEP-0106.
   * @return null, or an array with userId and appId.
   */
//  public static String[] parseNode(JID jid) {
//    String node = getNode(jid);
//    if (node == null) {

//      return null;
//    }
//    int sep = node.lastIndexOf(Constants.MMX_APPDOMAIN_SEP);
//    if (sep <= 0) {
//      return new String[] { null, node.substring(0, sep+1) };
//    } else {
//      return new String[] { node.substring(0, sep), node.substring(sep+1) };
//    }
//  }

  /**
   * Get the unescaped user ID (without appId) from the node part.
   * @param xid A MMX ID conforming with XEP-0106.
   * @return A user ID (without appId).
   */
  public static String getUserId(String xid) {
    if (xid == null) {
      return null;
    }
    String rawNode = getNode(xid);
    String node = sNoEsc ? rawNode : StringUtils.unescapeNode(rawNode);
    int sep = node.lastIndexOf(Constants.APP_ID_DELIMITER);
    if (sep < 0) {
      return node;
    }
    return node.substring(0, sep);
  }

  /**
   * Get the unescaped user ID's (without appId) from a list of XID's.
   * @param xids A list of MMX ID conforming with XEP-0106.
   * @return A list of user ID's (without appId).
   */
  public static List<String> getUserIds(List<String> xids) {
    if (xids == null) {
      return null;
    }
    ArrayList<String> userIds = new ArrayList<String>(xids.size());
    for (String xid : xids) {
      userIds.add(getUserId(xid));
    }
    return userIds;
  }

  /**
   * Get the domain from the MMX ID (userId%appId@domain/resource).
   * @param xid A bared XID or full XID.
   * @return null if no domain; otherwise, a domain in the XID.
   */
  public static String getDomain(String xid) {
    int atpos = xid.lastIndexOf('@');
    if (atpos < 0) {
      return null;
    }
    int slashpos = xid.lastIndexOf('/');
    if (slashpos > atpos) {
      return xid.substring(atpos+1, slashpos);
    }
    return xid.substring(atpos+1);
  }

  /**
   * Get the resource from the full MMX ID (userId%appId@domain/resource).
   * @param xid A full XID.
   * @return null if no resource; otherwise, a resource
   */
  public static String getResource(String xid) {
    int slashpos = xid.lastIndexOf('/');
    if (slashpos < 0) {
      return null;
    }
    return xid.substring(slashpos+1);
  }

  /**
   * Split a MMX ID into four tokens.  Any missing parts will have
   * the null value.  The first part is the userID, the second part is the
   * appId, the third part is the domain, the fourth part is the resource.
   * @param xid An MMX ID.
   * @return Array of four parts.
   */
  public static String[] parseXID(String xid) {
    String[] tokens = new String[4];
    int appos = xid.indexOf(Constants.APP_ID_DELIMITER);
    int dompos = xid.lastIndexOf('@');
    int respos = xid.lastIndexOf('/');
    StringBuilder sb = new StringBuilder(xid);
    if (respos < 0 || respos < dompos) {
      tokens[3] = null;
    } else {
      tokens[3] = sb.substring(respos+1);
      sb.setLength(respos);
    }
    if (dompos > 0) {
      tokens[2] = sb.substring(dompos+1);
      sb.setLength(dompos);
    }
    if (appos > 0) {
      tokens[1] = sb.substring(appos+1);
      sb.setLength(appos);
    }
    tokens[0] = sb.toString();
    return tokens;
  }

  /**
   * @hide
   * Convert from MMX ID (with appID) to XMPP bared JID (without appID.)  That
   * is, "john.doe%appId@domain/resource" will be "john.doe@domain".
   * @param xid A MMX ID.
   * @return An XMPP bared JID.
   */
  public static String toBaredJID(String xid) {
    String[] tokens = parseXID(xid);
    return tokens[0] + '@' + tokens[2];
  }

  /**
   * Get the app ID from the node part of MMX ID.
   * @param xid A MMX ID conforming with XEP-0106.
   * @return The App ID.
   */
  public static String getAppId(String xid) {
    if (xid == null) {
      return null;
    }
    String rawNode = getNode(xid);
    String node = sNoEsc ? rawNode : StringUtils.unescapeNode(rawNode);
    int sep = node.lastIndexOf(Constants.APP_ID_DELIMITER);
    if (sep < 0) {
      return null;
    }
    return node.substring(sep+1);
  }

	/**
	 * Check if the <code>from</code> has admin capability.  Only the escaped
	 * node part (userID) is validate.  We assume that the openfire stores the
	 * node in unescape format.
	 * @param from A full JID or bare JID conforming with XEP-0106.
	 * @return true or false.
	 */
//	public static boolean isAdmin(JID from) {
//		XMPPServer server = XMPPServer.getInstance();
//		if (server.isLocal(from)) {
//	    String userId = JID.unescapeNode(from.getNode());
//			for (JID admin : server.getAdmins()) {
//				if (JID.equals(admin.getNode(), userId)) {
//					return true;
//				}
//			}
//		}
//		return false;
//	}

  /**
   * @hide
   * Convert an MMX ID to an external format.
   * @param xid An MMX ID.
   * @param appId An app ID.
   * @param domain A domain name.
   * @return A bared or full XID.
   */
  public static String makeXID(MMXid xid, String appId, String domain) {
    return makeXID(xid.getUserId(), appId, domain, xid.getDeviceId());
  }

  /**
   * @hide
   * Convert an array of addressable objects to XID's.
   * @param xids Array of MMXUser or MMXEndpoint.
   * @param appId An app ID.
   * @param domain A domain name.
   * @return An array of bared or full XID's.
   */
  public static String[] makeXIDs(MMXid[] xids, String appId,
                                    String domain) {
    int i = 0;
    String[] array = new String[xids.length];
    for (MMXid xid : xids) {
      array[i++] = makeXID(xid.getUserId(), appId, domain, xid.getDeviceId());
    }
    return array;
  }

  /**
   * @hide
   * Convert a collection of MMX ID objects to their external format.
   * @param xids A collection of MMX ID's.
   * @param appId An app ID.
   * @param domain A domain name.
   * @return A list of bared or full XID's.
   */
  public static List<String> makeXIDs(Collection<MMXid> xids, String appId,
                                        String domain) {
    ArrayList<String> list = new ArrayList<String>(xids.size());
    for (MMXid xid : xids) {
      list.add(makeXID(xid.getUserId(), appId, domain, xid.getDeviceId()));
    }
    return list;
  }

  /**
   * @hide
   * Convert an array of user ID's to bared MMX ID's if they are not in full
   * XID format.  If the userID is a full XID, no conversion will be performed.
   * If the userID is a bared XID, it will mess up the conversion.
   * @param userIds Array of user ID (assuming no appId) or a full XID.
   * @param appId null or an app ID.
   * @param domain A domain name.
   * @return An array of bared XID.
   */
  public static String[] makeXIDs(String[] userIds, String appId,
                                    String domain) {
    int i = 0;
    String[] xids = new String[userIds.length];
    for (String userId : userIds) {
      if (userId.indexOf('/') < 0) {
        xids[i++] = makeEscNode(userId, appId)+'@'+domain;
      } else {
        xids[i++] = userId;
      }
    }
    return xids;
  }

  /**
   * @hide
   * Same as {@link #makeXIDs(String[], String, String)}, except it is a list.
   * @param userIds A list of user ID (assuming no appId) or a full XID.
   * @param appId null or an app ID.
   * @param domain A domain name.
   * @return A list of bared XID's.
   */
  public static List<String> makeXIDs(List<String> userIds, String appId,
                                        String domain) {
    ArrayList<String> xids = new ArrayList<String>(userIds.size());
    for (String userId : userIds) {
      if (userId.indexOf('/') < 0) {
        xids.add(makeEscNode(userId, appId)+'@'+domain);
      } else {
        xids.add(userId);
      }
    }
    return xids;
  }

  /**
   * @hide
   * Convert a user ID into a bared XID.
   * @param userId
   * @param appId null or an app ID.
   * @param domain A domain name.
   * @return A bared XID.
   */
  public static String makeXID(String userId, String appId, String domain) {
    return makeEscNode(userId, appId)+'@'+domain;
  }

  /**
   * @hide
   * Convert a user ID into a full MMX ID.
   * @param userId A user ID.
   * @param appId null or an app ID.
   * @param domain A domain name (aka service name)
   * @param resource A resource name.
   * @return A full XID.
   */
  public static String makeXID(String userId, String appId, String domain,
                                 String resource) {
    if (resource != null && !resource.isEmpty()) {
      return makeEscNode(userId, appId)+'@'+domain+'/'+resource;
    } else {
      return makeXID(userId, appId, domain);
    }
  }
}
