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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

/**
 * File Utilities to encode binary file to base64 file, or vice versa, convert
 * file content using a specified converter to a buffer, convert file content
 * to XML buffer with an XML encoder.
 *
 */
public class FileUtil {
  private final static String TAG = "FileUtil";

  /**
   * Encode a binary file to a base64 UTF-8 file.
   * @param src The file holding binary data.
   * @param dst The file to hold the base64 encoded data.
   * @return The encoded content length.
   */
  public static int encodeFile(File src, File dst) throws IOException {
    FileInputStream fis = null;
    FileOutputStream fos = null;
    try {
      int total = 0;
      ByteBuffer ibbuf = ByteBuffer.allocate(8190); // must be multiple of 3
      ByteBuffer obbuf = ByteBuffer.allocate((ibbuf.capacity()+2)/3*4);  // must be 3:4 ratio
      fis = new FileInputStream(src);
      FileChannel fic = fis.getChannel();
      fos = new FileOutputStream(dst);
      while (fic.read(ibbuf) > 0) {
        ibbuf.limit(ibbuf.position());
        ibbuf.rewind();
        Base64.encode(ibbuf, obbuf);
        fos.write(obbuf.array(), 0, obbuf.position());
        total += obbuf.position();
        ibbuf.clear();
        obbuf.clear();
      }
      return total;
    } finally {
      if (fos != null) {
        fos.close();
      }
      if (fis != null) {
        fis.close();
      }
    }
  }

  /**
   * Decode a base64 UTF-8 file to binary file.
   * @param src The file holding base64 encoded data.
   * @param dst The file to hold binary data.
   * @return The decoded content length.
   */
  public static int decodeFile(File src, File dst) throws IOException {
    FileInputStream fis = null;
    FileOutputStream fos = null;
    try {
      int n, total = 0;
      byte[] ibuf = new byte[8192];       // must be multiple of 4
      fis = new FileInputStream(src);
      fos = new FileOutputStream(dst);
      while ((n = fis.read(ibuf, 0, ibuf.length)) > 0) {
        byte[] obuf = Base64.decode(ibuf, 0, n, Base64.NO_OPTIONS);
        fos.write(obuf, 0, obuf.length);
        total += obuf.length;
      }
      return total;
    } finally {
      if (fos != null) {
        fos.close();
      }
      if (fis != null) {
        fis.close();
      }
    }
  }

  /**
   * Copy a UTF-8 file content into an appendable object (e.g. StringBuilder.)
   * @param file A text source file.
   * @param sb An output char buffer.
   * @param codec A converter.
   * @return Number of characters copied.
   */
  public static int copyFromFile(File file, Appendable sb, Converter codec) {
    FileReader reader = null;
    int n, total = 0;
    try {
      reader = new FileReader(file);
      char[] buf = new char[8192];
      while ((n = reader.read(buf)) >= 0) {
        total += n;
        CharSequence cs = CharBuffer.wrap(buf, 0, n);
        if (codec != null) {
          cs = codec.convert(cs);
        }
        sb.append(cs);
      }
      return total;
    } catch (Throwable e) {
      e.printStackTrace();
      return -1;
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (Throwable e) {
          // Ignored.
        }
      }
    }
  }

  /**
   * Copy a UTF-8 file to another UTF-8 file.
   * @param infile Source file.
   * @param outfile Destination file.
   * @param codec null or a converter.
   * @return Number of bytes copied.
   * @throws IOException
   */
  public static int copyFromFile(File infile, File outfile, Converter codec)
                                  throws IOException {
    FileReader reader = null;
    FileWriter writer = null;
    int n, total = 0;
    try {
      reader = new FileReader(infile);
      writer = new FileWriter(outfile);
      char[] buf = new char[8192];
      while ((n = reader.read(buf)) >= 0) {
        total += n;
        CharSequence cs = CharBuffer.wrap(buf, 0, n);
        if (codec != null) {
          cs = codec.convert(cs);
        }
        writer.write(cs.toString());
      }
      return total;
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (Throwable e) {
          // Ignored.
        }
      }
      if (writer != null) {
        writer.close();
      }
    }
  }

  /**
   * Copy a UTF-8 file to a 16-bit Unicode file that can be mapped as
   * CharSequence.
   * @param infile A UTF-8 input file.
   * @param outfile A char-based output file.
   * @param codec null or a text encoder.
   * @return Number of bytes copied.
   * @throws IOException
   * @see FileCharSequence
   */
  public static int copyToCharsFile(File infile, File outfile, Converter codec)
      throws IOException {
    FileReader reader = null;
    DataOutputStream dos = null;
    int n, total = 0;
    try {
      reader = new FileReader(infile);
      dos = new DataOutputStream(new FileOutputStream(outfile));
      char[] buf = new char[8192];
      while ((n = reader.read(buf)) >= 0) {
        total += n;
        CharSequence cs = CharBuffer.wrap(buf, 0, n);
        if (codec != null) {
          cs = codec.convert(cs);
        }
        dos.writeChars(cs.toString());
      }
      return total;
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (Throwable e) {
          // Ignored.
        }
      }
      if (dos != null) {
        dos.close();
      }
    }
  }

  /**
   * @hide
   * Copy a UTF-8 file content to the XML StringBuilder.  The inserted payload
   * will be XML escaped.
   * @param file A UTF-8 input file
   * @param xsb A lazy string builder.
   * @return Number of characters copied.
   */
  public static int copyFromFile(File file, Appendable xsb) {
    FileReader reader = null;
    int n, total = 0;
    try {
      reader = new FileReader(file);
      char[] buf = new char[8192];
      while ((n = reader.read(buf)) >= 0) {
        total += n;
        xsb.append(Utils.escapeForXML(new String(buf, 0, n)));
      }
      return total;
    } catch (Throwable e) {
      e.printStackTrace();
      return -1;
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (Throwable e) {
          // Ignored.
        }
      }
    }
  }

  /**
   * @hide
   * Encode the content in <code>file</code> for XML using memory-mapped I/O and
   * append the encoded content to the XML StringBuilder.  If the file content
   * is binary, it will be base64 encoded; otherwise, it will be XML escaped.
   * Note, the caller is responsible to dispose the <code>file</code>.
   * @param file
   * @param xsb A lazy string builder
   * @return
   */
  public static int encodeForXml(DisposableFile file, Appendable xsb) {
    CharBuffer cb = encodeForXml(file);
    if (cb == null) {
      return -1;
    }
    try {
      xsb.append(cb);
      return cb.limit();
    } catch (IOException e) {
      e.printStackTrace();
      return -1;
    }
  }
  
  /**
   * @hide
   * Encode the content in <code>file</code> for XML using memory-mapped I/O.
   * If the file content is binary, it will be base64 encoded; otherwise, it
   * will be XML escaped.  Note, the caller is responsible to dispose the 
   * <code>file</code>.
   * @param file A UTF-8 or binary file.
   * @return Memory-mapped encoded content.
   */
  public static CharBuffer encodeForXml(DisposableFile file) {
    return encodeFile(file, true);
  }
  
  /**
   * @hide
   * Convert UTF-8 or binary content to a Char file with an optional XML
   * encoding using memory-mapped I/O. The <code>xmlEsc</code> is only
   * applicable to UTF-8 content.  For binary content, it uses Base64 encoding.
   * @param file A UTF-8 or binary file.
   * @param xmlEsc true to do XML escape.
   * @return Memory-mapped encoded content.
   */
  public static CharBuffer encodeFile(DisposableFile file, boolean xmlEsc) {
    File outfile = null;
    try {
      outfile = File.createTempFile("tmpEncXmlFile", ".dat");
      outfile.deleteOnExit();
      if (!file.isBinary()) {
        copyToCharsFile(file, outfile, xmlEsc ? Converter.XmlEncoder : null);
      } else {
        Base64.encodeFileToCharsFile(file.getPath(), outfile.getPath());
      }
      // Memory-map the chars file.
      CharBuffer cb = mmapCharsFile(outfile);
      return cb;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    } finally {
      if (outfile != null) {
        outfile.delete();
      }
    }
  }

  /**
   * @hide
   * Encode large <code>cs</code> to XML-safe content using temporary
   * memory-mapped I/O and append the encoded content to a lazy XML
   * StringBuilder.
   * @param cs A large text char sequence.
   * @param xsb A lazy string builder.
   * @return
   * @see MMXXmlStringBuilder
   */
  public static int encodeForXml(CharSequence cs, Appendable xsb) {
    CharBuffer cb = encodeForXml(cs);
    if (cb == null) {
      return -1;
    }
    try {
      xsb.append(cb);
      return cb.limit();
    } catch (IOException e) {
      e.printStackTrace();
      return -1;
    }
  }
  
  /**
   * @hide
   * Encode large <code>cs</code> to XML-safe content using temporary
   * memory-mapped I/O.
   * @param cs A large text char sequence.
   * @return Memory-mapped I/O char buffer.
   */
  public static CharBuffer encodeForXml(CharSequence cs) {
    FileCharSequence fcsq = null;
    try {
      File tmpfile = File.createTempFile("tmpEncXmlChars", ".dat");
      tmpfile.deleteOnExit();
      fcsq = new FileCharSequence(new DisposableTextFile(tmpfile.getPath(), true));
      int total = 0;
      int len = cs.length();
      while (len > 0) {
        int n = Math.min(8192, len);
        fcsq.append(Converter.XmlEncoder.convert(
            cs.subSequence(total, total+n)).toString());
        total += n;
        len -= n;
      }
      // Memory-map the chars file.
      CharBuffer cb = fcsq.map(MapMode.READ_ONLY, 0, total);
      return cb;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    } finally {
      if (fcsq != null) {
        try {
          fcsq.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  // Memory map a chars file that it can be used as in-memory CharSequence.
  private static CharBuffer mmapCharsFile(File file) throws IOException {
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(file);
      FileChannel fch = fis.getChannel();
      MappedByteBuffer bbuf = fch.map(MapMode.READ_ONLY, 0, file.length());
      return bbuf.asCharBuffer();
    } finally {
      if (fis != null) {
        fis.close();
      }
    }
  }
}
