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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * This class customizes the Date to be ISO-8601 format.  A typical usage is:
 * <pre>
 * public class MyClass {
 *    public int ival;
 *    public String sval;
 *    ...
 * };
 *  
 * Note, the field member can be private or public or protected.
 * 
 * MyClass myObj = new MyClass(...);
 * String content = GsonData.getGson().toJson(myObj);
 * MyClass obj = GsonData.getGson().fromJson(content, MyClass.class);
 * </pre>
 */
public class GsonData {
  public final static String CONTENT_TYPE_JSON = "application/json";
  
  private static Gson sGson;

  static {
//    TimeZone utc = TimeZone.getTimeZone("UTC");
//    sDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//    sDateTimeFormat.setTimeZone(utc);
    GsonBuilder builder = new GsonBuilder();
    builder.registerTypeAdapter(Date.class, new DateSerializer());
    builder.registerTypeAdapter(Date.class, new DateDeserializer());
    sGson = builder.create();
  }

  /**
   * Get the customized Gson singleton.
   * @return
   */
  public static Gson getGson() {
    return sGson;
  }
  
  private static class DateSerializer implements JsonSerializer<Date> {
    @Override
    public JsonElement serialize(Date date, Type typeOfDate,
        JsonSerializationContext context) {
      return new JsonPrimitive(TimeUtil.toString(date));
    }
  }

  private static class DateDeserializer implements JsonDeserializer<Date> {
    @Override
    public Date deserialize(JsonElement json, Type typeOfT,
      JsonDeserializationContext context) throws JsonParseException {
      String datestr = json.getAsJsonPrimitive().getAsString();
      Date date = TimeUtil.toDate(datestr);
      if (date == null) {
        new JsonParseException("Invalid date: "+datestr);
      }
      return date;
    }
  }
}
