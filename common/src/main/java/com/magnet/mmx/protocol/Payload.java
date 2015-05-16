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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringBufferInputStream;
import java.util.Date;

import com.magnet.mmx.util.DisposableFile;
import com.magnet.mmx.util.FileUtil;

/**
 * @hide
 * The payload of a message.  It is application specific and it must be
 * encoded in text format (e.g. JSON, Base64, or plain text.)
 */
public class Payload implements Serializable {
  /**
   * Maximum payload (raw) size.
   */
  public static boolean sLargePayloadAllowed = false;
  private static final int MAX_SIZE = 200 * 1024;
  private static final long serialVersionUID = 7105698073192456468L;
  private Date mSentTime;
  private String mMsgType;
  private String mCid;
  private int mDataOffset;
  private int mDataLen;
  private int mDataSize;
  private CharSequence mData;
  private DisposableFile mFile;

  /**
   * Get the allowable payload size.  If large payload is allowed, the size will
   * be 2MB.  Otherwise, it will be 200KB.
   * @return The allowable payload size.
   */
  public static int getMaxSizeAllowed() {
    return sLargePayloadAllowed ? Constants.MAX_PAYLOAD_SIZE : MAX_SIZE;
  }
  
  /**
   * Constructor with encoded text data.  If the data is huge, it is more
   * efficient to use FileCharSequence.
   * @param msgType The payload type.
   * @param data The text encoded data.
   * @see com.magnet.mmx.util.FileCharSequence
   */
  public Payload(String msgType, CharSequence data) {
    if ((mMsgType = msgType) == null || mMsgType.isEmpty()) {
      mMsgType = Constants.MMX_MTYPE_UNKNOWN;
    }
    mData = data;
    mDataOffset = 0;
    mDataSize = mDataLen = ((data != null) ? data.length() : 0);
  }
  
  /**
   * Constructor with raw context stored in a file.  If the context is binary,
   * it will be encoded by the SDK using "base64".
   * @param msgType The payload type.
   * @param file The file containing the text encoded data.
   */
  public Payload(String msgType, DisposableFile file) {
    if ((mMsgType = msgType) == null || mMsgType.isEmpty()) {
      mMsgType = Constants.MMX_MTYPE_UNKNOWN;
    }
    mFile = file;
    mDataOffset = 0;
    mDataSize = mDataLen = ((file != null) ? (int) file.length() : 0);
  }
  
  /**
   * @hide
   * Constructor for a received payload.
   * @param msgType
   * @param file
   * @param offset
   * @param len
   * @param cid
   */
  Payload(String msgType, DisposableFile file, int offset, int len, String cid) {
    mMsgType = msgType;
    mFile = file;
    mCid = cid;
    mDataOffset = offset;
    mDataLen = len;
    mDataSize = (int) file.length();
  }

  /**
   * Get the sent time.  The time is based on the system clock of the sending
   * device, so it may not be accurate.
   * @return The sent time.
   */
  public Date getSentTime() {
    return mSentTime;
  }

  /**
   * @hide
   * Set the sent time.
   * @param sentTime
   */
  public void setSentTime(Date sentTime) {
    mSentTime = sentTime;
  }
  
  /**
   * Get the message type or a sub type.  The value is application specific.
   * For structured data, it may be a class name or structure name.  
   * @return The message type.
   */
  public String getMsgType() {
    return mMsgType;
  }

  /**
   * @hide
   * Get the offset of the current chunk.
   * @return The offset for the current chunk.
   */
  public int getDataOffset() {
    return mDataOffset;
  }

  /**
   * @hide
   * Get the size of the current chunk.
   * @return The size of the current chunk.
   */
  public int getDataLen() {
    return mDataLen;
  }

  /**
   * Get the overall total size of the payload.
   * @return The total size of the payload.
   */
  public int getDataSize() {
    return mDataSize;
  }

  /**
   * Get the payload that is back by a CharSequence.
   * @return null if not set as CharSequence, or a CharSequence.
   */
  public CharSequence getData() {
    return mData;
  }

  /**
   * Get the payload that is back by a file.
   * @return null if not back by a file, or a File.
   */
  public DisposableFile getFile() {
    return mFile;
  }

  /**
   * Get the text encoded payload as String.  If the payload is huge (e.g.
   * greater than 1MB), use {@link #getDataAsInputStream()} 
   * @return The text
   */
  public String getDataAsString() {
    if (mFile != null) {
      StringBuilder sb = new StringBuilder((int) mFile.length());
      if (FileUtil.copyFromFile(mFile, sb, null) < 0) {
        return null;
      }
      return sb.toString();
    } else if (mData != null) {
      return mData.toString();
    } else {
      return null;
    }
  }

  /**
   * Open the input stream of the payload.  It is the caller's responsibility to
   * close the input stream.
   * @return An InputStream of the string encoded payload.
   * @throws IOException
   */
  public InputStream getDataAsInputStream() throws IOException {
    if (mFile != null) {
      return new FileInputStream(mFile);
    } else if (mData != null) {
      return new StringBufferInputStream(mData.toString());
    } else {
      return null;
    }
  }

  /**
   * The String representative of this object for debug purpose.
   */
  @Override
  public String toString() {
    return "[ mtype="+getMsgType()+", stamp="+getSentTime()+", cid="+getCid()+
            ", chunk="+formatChunk()+", file="+mFile+", data="+getDataAsString()+
            " ]";
  }

  /**
   * @hide
   * Set the chunk ID.
   * @param cid
   */
  public void setCid(String cid) {
    mCid = cid;
  }

  /**
   * @hide
   * Get the optional chunk ID.
   * @return
   */
  public String getCid() {
    return mCid;
  }

  /**
   * @hide
   * Format for the chunk attribute.
   * @return
   */
  public String formatChunk() {
    return mDataOffset+"/"+mDataLen+"/"+mDataSize;
  }

  /**
   * @hide
   * Parse the chunk attribute.
   * @param chunk
   */
  public void parseChunk(String chunk) {
    String[] tokens = chunk.split("/");
    if (tokens != null && tokens.length == 3) {
      mDataOffset = Integer.parseInt(tokens[0]);
      mDataLen = Integer.parseInt(tokens[1]);
      mDataSize = Integer.parseInt(tokens[2]);
     }
  }

  static int[] parseChunkAttribute(String chunk) {
    String[] tokens = chunk.split("/");
    if (tokens == null || tokens.length != 3) {
      return null;
    } else {
      return new int[] { Integer.parseInt(tokens[0]),
                           Integer.parseInt(tokens[1]),
                           Integer.parseInt(tokens[2]) };
    }
  }
}
