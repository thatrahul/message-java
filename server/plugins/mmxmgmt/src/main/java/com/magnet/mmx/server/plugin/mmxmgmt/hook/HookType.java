package com.magnet.mmx.server.plugin.mmxmgmt.hook;

/**
 * Created by rphadnis on 6/30/15.
 */
public enum HookType {

  /**
   * Hook for a message with specific meta key and value
   */
  MESSAGE_WITH_META,
  /**
   * Hook for notification when message state changes to received
   */
  MESSAGE_RECEIVED,
  /**
   * Hook for a push acknowledgement
   */
  PUSH_ACKNOWLEDGED,
  /**
   * Hook for a case where user is created
   */
  USER_CREATED
}
