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
 * Status code from MMX server.
 */
public interface StatusCode {
  public final static int SUCCESS = 200;
  public final static int BAD_REQUEST = 400;
  public final static int FORBIDDEN = 403;
  public final static int NOT_FOUND = 404;
  public final static int NOT_ACCEPTABLE = 406;
  public final static int CONFLICT = 409;
  public final static int GONE = 410;
  public final static int INTERNAL_ERROR = 500;
  public final static int NOT_IMPLEMENTED = 501;
}
