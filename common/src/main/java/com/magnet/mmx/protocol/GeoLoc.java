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

import com.google.gson.annotations.SerializedName;
import com.magnet.mmx.util.GsonData;
import com.magnet.mmx.util.JSONifiable;

/**
 * The payload for geo-tracking.
 */
public class GeoLoc extends JSONifiable {
  @SerializedName("accuracy")
  private Integer mAccuracy;    // e.g. 50 meters (horizontal accuracy)
  @SerializedName("lat")
  private Float mLat;           // e.g. 37.425
  @SerializedName("lng")
  private Float mLng;           // e.g. -122.135
  @SerializedName("altaccuracy")
  private Integer mAltAccuracy;   // e.g. 3 meters (vertical accuracy)
  @SerializedName("alt")
  private Float mAlt;           // e.g. 10.4 meters
  @SerializedName("locality")
  private String mLocality;     // e.g. city as "Palo Alto"
  @SerializedName("subloc")
  private String mSubLocality;  // e.g. district as "Mid Town"
  @SerializedName("subadmin")
  private String mSubAdminArea; // e.g. county as "Santa Clara County"
  @SerializedName("adminarea")
  private String mAdminArea;    // e,g. province or state as "CA"
  @SerializedName("postal")
  private String mPostal;       // e.g. postal code as "94301"
  @SerializedName("country")
  private String mCountry;      // e.g. country as "US"
  
  /**
   * Get the optional horizontal accuracy.
   * @return An accuracy in meters, or null.
   */
  public Integer getAccuracy() {
    return mAccuracy;
  }
  
  /**
   * Set the optional horizontal accuracy in meters.  Smaller value means more
   * accurate.
   * @param accuracy The accuracy in meters.
   * @return This object.
   */
  public GeoLoc setAccuracy(int accuracy) {
    mAccuracy = accuracy;
    return this;
  }
  
  /**
   * Get the optional latitude.
   * @return The latitude, or null.
   */
  public Float getLat() {
    return mLat;
  }
  
  /**
   * Set the optional latitude.
   * @param lat The latitude.
   * @return This object.
   */
  public GeoLoc setLat(float lat) {
    mLat = lat;
    return this;
  }
  
  /**
   * Get the optional longitude.
   * @return The longitude, or null.
   */
  public Float getLng() {
    return mLng;
  }
  
  /**
   * Set the optional longitude.
   * @param lng The longitude.
   * @return This object.
   */
  public GeoLoc setLng(float lng) {
    mLng = lng;
    return this;
  }
  
  /**
   * Get the optional altitude accuracy.
   * @return The altitude accuracy, or null.
   */
  public Integer getAltAccuracy() {
    return mAltAccuracy;
  }

  /**
   * Set the altitude (vertical) accuracy in meters.
   * @param altAccuracy The altitude accuracy.
   * @return This object.
   */
  public GeoLoc setAltAccuracy(int altAccuracy) {
    mAltAccuracy = altAccuracy;
    return this;
  }

  /**
   * Get the altitude in meters.
   * @return The altitude or null.
   */
  public Float getAlt() {
    return mAlt;
  }

  /**
   * Set the altitude in meters.
   * @param alt The altitude.
   * @return This object.
   */
  public GeoLoc setAlt(Float alt) {
    mAlt = alt;
    return this;
  }

  /**
   * Get the sub-locality (e.g. Mid Town)
   * @return The sub-locality (district in a city), or null.
   */
  public String getSubLocality() {
    return mSubLocality;
  }

  /**
   * Specify the sub-locality.  If specified, the locality must be specified.
   * @param subLocality The sub-locality (district in a city.)
   * @return This object.
   */
  public GeoLoc setSubLocality(String subLocality) {
    mSubLocality = subLocality;
    return this;
  }

  /**
   * Get the locality of the address (e.g. Palo Alto)
   * @return The locality (city), or null.
   */
  public String getLocality() {
    return mLocality;
  }

  /**
   * Specify the locality of the address.  If specified, the administration
   * area must be specified.
   * @param locality The locality (city.)
   * @return This object.
   */
  public GeoLoc setLocality(String locality) {
    mLocality = locality;
    return this;
  }

  /**
   * Get the sub-administration area (e.g. Santa Clara County)
   * @return The sub-administration area (county), or null.
   */
  public String getSubAdminArea() {
    return mSubAdminArea;
  }

  /**
   * Specify the sub-administration area.  If specified, the administration
   * area must be specified.
   * @param subAdminArea The sub-administration area (county.)
   * @return This object.
   */
  public GeoLoc setSubAdminArea(String subAdminArea) {
    mSubAdminArea = subAdminArea;
    return this;
  }

  /**
   * Get the administration area (e.g. CA).
   * @return The administration area (state), or null.
   */
  public String getAdminArea() {
    return mAdminArea;
  }

  /**
   * Specify the administration area.  If specified, the country must be
   * specified.
   * @param adminArea The administration area (state.)
   * @return This object.
   */
  public GeoLoc setAdminArea(String adminArea) {
    mAdminArea = adminArea;
    return this;
  }

  /**
   * Get the postal code (e.g. 94301)
   * @return The postal code (ZIP code), or null.
   */
  public String getPostal() {
    return mPostal;
  }

  /**
   * Specify the postal code.  If specified, the country must be specified.
   * @param postal The postal code (ZIP code.)
   * @return This object.
   */
  public GeoLoc setPostal(String postal) {
    mPostal = postal;
    return this;
  }

  /**
   * Get the country (e.g. US)
   * @return The country.
   */
  public String getCountry() {
    return mCountry;
  }
  
  /**
   * Specify the required country.
   * @param country The country.
   * @return This object.
   */
  public GeoLoc setCountry(String country) {
    mCountry = country;
    return this;
  }
  
  /**
   * Get the type of this geo-location payload.
   * @return The message type for geo-location payload.
   */
  public static String getType() {
    return Constants.MMX_MTYPE_GEOLOC;
  }
  
  /**
   * @hide
   * Convert a JSON into this payload object.
   * @param json The JSON string of this payload.
   * @return The GeoLoc object of this payload.
   */
  public static GeoLoc fromJson(String json) {
    return GsonData.getGson().fromJson(json, GeoLoc.class);
  }
}
