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

//import com.magnet.mmx.client.Log;

/**
 * This abstract class allows the consumer of a typed file to delete the file
 * when the transaction is completed.  It relieves the burden for the provider
 * of the typed file when to remove the file in asynchronous environment.
 *
 * <pre>
 * DisposableFile tmpFile = new DisposableBinaryFile("tmppic.jpg", true);
 * mmxclient.sendMessage(to, null, new Payload("image/jpeg", "SENDIMG", tmpFile), null);
 * </pre>
 * 
 * @see DispableTextFile
 * @see DispableBinFile
 */
public abstract class DisposableFile extends File implements Finishable {
  private static final String TAG = "DisposableFile";
  private static final long serialVersionUID = 2839686773762141998L;
  private boolean mDeleteOnDone;

  /**
   * Constructor with the directory, file name and action upon done.
   * @param dir The directory.
   * @param name The file name.
   * @param deleteOnDone true to delete the file when done; otherwise, false.
   */
  public DisposableFile(File dir, String name, boolean deleteOnDone) {
    super(dir, name);
    mDeleteOnDone = deleteOnDone;
  }

  /**
   * Constructor with the file path and action upon done.
   * @param path The file path.
   * @param deleteOnDone true to delete the file when done; otherwise, false.
   */
  public DisposableFile(String path, boolean deleteOnDone) {
    super(path);
    mDeleteOnDone = deleteOnDone;
  }
  
  /**
   * Constructor with the directory path, file name and action upone done.
   * placing a path separator between the two.
   * @param dirPath The directory path.
   * @param name The file name.
   * @param deleteOnDone true to delete the file when done; otherwise, false.
   */
  public DisposableFile(String dirPath, String name, boolean deleteOnDone) {
    super(dirPath, name);
    if (mDeleteOnDone = deleteOnDone) {
      this.deleteOnExit();
    }
  }
  
  /**
   * Constructor with the file URI and action upon done.
   * @param uri The URI of the file.
   * @param deleteOnDone true to delete the file when done; otherwise, false.
   */
  public DisposableFile(URI uri, boolean deleteOnDone) {
    super(uri);
    if (mDeleteOnDone = deleteOnDone) {
      this.deleteOnExit();
    }
  }
  
  /**
   * Check if the file is set for deletion.
   * @return true to delete the file when done; otherwise, false.
   */
  public boolean isDeleteOnDone() {
    return mDeleteOnDone;
  }
  
  /**
   * The file is finished. Delete the file if it is set for deletion.
   */
  public void finish() {
    if (mDeleteOnDone) {
//      if (Log.isLoggable(TAG, Log.DEBUG)) {
//        Log.d(TAG, "disposing the file: "+this.getPath());
//      }
      this.delete();
    }
  }
  
  /**
   * Check if it is a binary file.
   * @return true for binary file, false for text file.
   */
  public abstract boolean isBinary();
}
