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

import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel.MapMode;

/**
 * A CharSequence backing by a 16-bit Unicode file.  Caller can use
 * {@link FileUtil#copyToCharsFile(java.io.File, java.io.File, Converter)} to
 * convert a UTF-8 file to 16-bit Unicode file.
 */
public class FileCharSequence implements CharSequence, Appendable, Closeable {
  private DisposableTextFile mFile;
  private RandomAccessFile mRandAccFile;
  
  /**
   * Constructor with a disposable 16-bit Unicode text file.
   * @param file A 16-bit Unicode text file.
   * @throws IOException
   */
  public FileCharSequence(DisposableTextFile file) throws IOException {
    mFile = file;
    mRandAccFile = new RandomAccessFile(mFile, "rw");
  }

  /**
   * Get the number of characters.
   * @return Number of chars.
   */
  public int length() {
    return (int) mFile.length() / 2;
  }

  /**
   * Get a character at the zero-based <code>index</code>.
   * @param index A zero-based index
   */
  public char charAt(int index) {
    try {
      mRandAccFile.seek(index*2);
      return mRandAccFile.readChar();
    } catch (IOException e) {
      e.printStackTrace();
      return 0;
    }
  }

  /**
   * Get a sub-sequence from <code>start</code> (inclusive) to <code>end</code>
   * (exclusive) from the file.
   * @param start An inclusive starting index.
   * @param end An exclusive ending index.
   */
  public CharSequence subSequence(int start, int end) {
    try {
      int len = end - start;
      mRandAccFile.seek(start*2);
      byte[] buf = new byte[len*2];
      mRandAccFile.readFully(buf);
      return ByteBuffer.wrap(buf).asCharBuffer();
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Append the <code>csq</code> to end of the file.
   * @param csq A char sequence.
   */
  public Appendable append(CharSequence csq) throws IOException {
    if (mRandAccFile == null) {
      throw new IOException("File is closed");
    }
    mRandAccFile.seek(mRandAccFile.length());
    mRandAccFile.writeChars(csq.toString());
    return this;
  }

  /**
   * Append a sub-sequence from <code>csq</code> to the file.  The sub-sequence
   * starts from <code>start</code> (inclusive) to <code>end</code> (exclusive.)
   * @param csq A char sequence
   * @param start An inclusive starting index
   * @param end An exclusive ending index
   */
  public Appendable append(CharSequence csq, int start, int end)
      throws IOException {
    if (mRandAccFile == null) {
      throw new IOException("File is closed");
    }
    mRandAccFile.seek(mRandAccFile.length());
    mRandAccFile.writeChars(csq.subSequence(start, end).toString());
    return this;
  }

  /**
   * Append a character to the end of the file.
   * @param c The character to be appended.
   */
  public Appendable append(char c) throws IOException {
    if (mRandAccFile == null) {
      throw new IOException("File is closed");
    }
    mRandAccFile.seek(mRandAccFile.length());
    mRandAccFile.writeChar(c);
    return this;
  }

  /**
   * Map the 16-bit Unicode file content to memory.
   * @param mode
   * @param pos Starting position in the unit of char.
   * @param size Number of characters
   * @return A CharBuffer of the memory-mapped file content.
   * @throws IOException
   */
  public CharBuffer map(MapMode mode, long pos, long size)
                               throws IOException {
    if (mRandAccFile == null) {
      throw new IOException("File is closed");
    }
    return mRandAccFile.getChannel().map(mode, pos*2, size*2).asCharBuffer();
  }

  /**
   * Close the file if it is opened and invoke the {@link DisposableFile#finish()}.
   */
  public void close() throws IOException {
    if (mRandAccFile != null) {
      mRandAccFile.close();
      mRandAccFile = null;
    }
    mFile.finish();
  }

  /**
   * Show the first and last 128 chars if the sequence is longer than 8K.  In
   * case of large file, it protects blowing up the heap.  To dump out the full
   * content, use {@link #subSequence(int, int)}.
   */
  @Override
  public String toString() {
    int len = length();
    if (len > 8192) {
      return subSequence(0, 128)+"..."+subSequence(len-128, len);
    } else {
      return subSequence(0, len).toString();
    }
  }
}
