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
package com.magnet.mmx.protocol;

/**
 * The enumeration of operating system types that may be assigned
 * to a device during device registration.
 */
public enum OSType {
  /**
   * None of the supported types.
   */
  OTHER,
  /**
   * Apple iOS
   */
  IOS,
  /**
   * Android, AOSP or its derivative.
   */
  ANDROID,
  /**
   * Windows Phone
   */
  WINPHONE,
  /**
   * Unix or its derivative (e.g. Linux, Solaris, AIX, HP/UX...)
   */
  UNIX,
  /**
   * Apple OS/X
   */
  OSX,
  /**
   * Microsoft Windows
   */
  WINDOWS;
}
