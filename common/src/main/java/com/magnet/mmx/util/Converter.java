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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
//import java.util.LinkedList;
//import java.util.Random;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

/**
 * This interface defines the conversion interface.  Currently only XML encoder
 * is the only provided converter.
 */
public interface Converter {
  
  /**
   * The XML encoder.
   */
  public static Converter XmlEncoder = new Converter() {
    @Override
    public CharSequence convert(CharSequence text) {
      return Utils.escapeForXML(text.toString());
    }
  };
  
//  public static Converter XmlDecoder = new Converter() {
//    @Override
//    public CharSequence convert(CharSequence text) {
//      return StringEscapeUtils.unescapeXml(text.toString());
//    }
//  };
  
  /**
   * Compress text and encoded to base64.
   */
  public static Converter Deflator = new Converter() {
    public CharSequence convert(CharSequence text) {
      try {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(text.length());
        DeflaterOutputStream dos = new DeflaterOutputStream(bos);
        dos.write(text.toString().getBytes());
        dos.close();
        return Base64.encodeBytes(bos.toByteArray());
      } catch (IOException e) {
        return null;
      }
    }
  };
  
  /**
   * Decode the base64 text and uncompress the decoded text.
   */
  public static Converter Inflator = new Converter() {
    public CharSequence convert(CharSequence base64Text) {
      try {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(512);
        InflaterOutputStream ios = new InflaterOutputStream(bos);
        ios.write(Base64.decode(base64Text.toString()));
        ios.close();
        return new String(bos.toByteArray());
      } catch (IOException e) {
        return null;
      }
    }
  };
  
  /**
   * Reversible scrambler for alpha-numeric 7-bit text.
   */
  public static Converter Scrambler = new Converter() {
    //   0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz
    private byte[] SCRAMBLE = 
        "J8eIcQSY1Bn9GkXwCj30RLqZov5K6VUTmE7Nsb4u2itrfHDlWAOyMhagdPFzpx".getBytes();

    public CharSequence convert(CharSequence text) {
      byte[] scrambled = text.toString().getBytes();
      for (int i = 0; i < scrambled.length; i++) {
        byte b = scrambled[i];
        if (b >= '0' && b <= '9') {
          scrambled[i] = SCRAMBLE[b - '0'];
        } else if (b >= 'A' && b <= 'Z') {
          scrambled[i] = SCRAMBLE[b - 'A' + 10];
        } else if (b >= 'a' && b <= 'z') {
          scrambled[i] = SCRAMBLE[b - 'a' + 36];
        }
      }
      return new String(scrambled);
    }
  };
  
  /**
   * Convert text into another text..
   * @param text The source text data.
   * @return
   */
  public CharSequence convert(CharSequence text);
}

// It is to generate SCRAMBLE string above.
//class ScramblerGenerator {
//  public static String generate() {
//    Random random = new Random();
//    String cs = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
//    byte[] sb = cs.getBytes();
//    LinkedList<Byte> list = new LinkedList<Byte>();
//    for (int i = 0; i < cs.length(); i++) {
//      list.add(new Byte((byte) i));
//    }
//    while (list.size() > 1) {
//      int i = random.nextInt(list.size());
//      byte v1 = list.get(i);
//      int j = random.nextInt(list.size());
//      byte v2 = list.get(j);
//      if (i > j) {
//        list.remove(i);
//        list.remove(j);
//      } else if (i < j) {
//        list.remove(j);
//        list.remove(i);
//      } else {
//        list.remove(i);
//      }
//      byte b = sb[v1];
//      sb[v1] = sb[v2];
//      sb[v2] = b;
//    }
//    return new String(sb);
//  }
//}
