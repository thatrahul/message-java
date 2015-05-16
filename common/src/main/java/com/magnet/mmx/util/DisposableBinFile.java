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

import java.io.File;
import java.net.URI;

/**
 * This class allows the consumer of the binary file to delete the file when the 
 * transaction is completed.  It relieves the burden for the provider of the
 * file when to remove the file in asynchronous environment.
 * @see DisposableFile
 */
public class DisposableBinFile extends DisposableFile {
  private static final long serialVersionUID = 8700382538487768778L;

  /**
   * Constructor with the directory, file name and action upon done.
   * @param dir The directory.
   * @param name The file name.
   * @param deleteOnDone true to delete the file when done; otherwise, false.
   */
  public DisposableBinFile(File dir, String name, boolean deleteOnDone) {
    super(dir, name, deleteOnDone);
  }

  /**
   * Constructor with a file path and action upon done.
   * @param path The file path.
   * @param deleteOnDone true to delete the file when done; otherwise, false.
   */
  public DisposableBinFile(String path, boolean deleteOnDone) {
    super(path, deleteOnDone);
  }

  /**
   * Constructor with a directory path, file name and action upon done.
   * @param dirPath The directory path.
   * @param name The file name.
   * @param deleteOnDone true to delete the file when done; otherwise, false.
   */
  public DisposableBinFile(String dirPath, String name, boolean deleteOnDone) {
    super(dirPath, name, deleteOnDone);
  }

  /**
   * Constructor with the file URI and action upon done.
   * @param uri The URI of the file.
   * @param deleteOnDone true to delete the file when done; otherwise, false.
   */
  public DisposableBinFile(URI uri, boolean deleteOnDone) {
    super(uri, deleteOnDone);
  }

  /**
   * Is a binary file?
   * @return Always return true.
   */
  @Override
  final public boolean isBinary() {
    return true;
  }
}
