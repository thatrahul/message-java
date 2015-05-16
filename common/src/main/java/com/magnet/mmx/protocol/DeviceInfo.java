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

import java.util.List;
import java.util.Map;

/**
 * Device information.
 */
public class DeviceInfo extends JSONifiable {
  @SerializedName("devId")
  //TODO: check if this should be renamed to deviceId
  private String devId;
  @SerializedName("displayName")
  private String displayName;
  @SerializedName("modelInfo")
  private String modelInfo;
  @SerializedName("osType")
  private String osType;
  @SerializedName("osVersion")
  private String osVersion;
  @SerializedName("versionMajor")
  private int versionMajor;
  @SerializedName("versionMinor")
  private int versionMinor;
  @SerializedName("pushType")
  private String pushType;
  @SerializedName("pushToken")
  private String pushToken;
  @SerializedName("phoneNumber")
  private String phoneNumber;
  @SerializedName("carrierInfo")
  private String carrierInfo;
  @SerializedName("extras")
  private Map<String, String> extras;
  @SerializedName("tags")
  private List<String> tags;

  /**
   * The device identifier
   * @return the device identifier
   */
  public String getDevId() {
    return devId;
  }

  /**
   * Set the device identifier
   * @param devId the device identifier
   * @return this DeviceInfo instance
   */
  public DeviceInfo setDevId(String devId) {
    this.devId = devId;
    return this;
  }

  /**
   * The display name
   * @return the display name
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * Set the display name
   * @param displayName display name
   * @return this DeviceInfo instance
   */
  public DeviceInfo setDisplayName(String displayName) {
    this.displayName = displayName;
    return this;
  }

  /**
   * The operating system type
   * @return the OS Type
   */
  public String getOsType() {
    return osType;
  }

  /**
   * Set the operating system type.
   * @param osType One of the String value of {@link OSType}
   */
  public DeviceInfo setOsType(String osType) {
    this.osType = osType;
    return this;
  }

  /**
   * The operating system version
   * @return the OS version
   */
  public String getOsVersion() {
    return osVersion;
  }

  /**
   * Set the operating system version.
   * @param osVersion the OS version
   */
  public DeviceInfo setOsVersion(String osVersion) {
    this.osVersion = osVersion;
    return this;
  }

  /**
   * The push type
   * @return the push type
   */
  public String getPushType() {
    return pushType;
  }

  /**
   * The optional native push type.  If a device does not have any push support,
   * leave it as null.
   * @param pushType The String value of {@link PushType#GCM} or {@link PushType#APNS}
   */
  public DeviceInfo setPushType(String pushType) {
    this.pushType = pushType;
    return this;
  }

  /**
   * The push token for this device
   * @return the push token
   */
  public String getPushToken() {
    return pushToken;
  }

  /**
   * Set the push token for this device
   *
   * @param token the push token
   * @return this DeviceInfo instance
   */
  public DeviceInfo setPushToken(String token) {
    this.pushToken = token;
    return this;
  }

  /**
   * The phone number for this device is available
   * @return the phone number
   */
  public String getPhoneNumber() {
    return phoneNumber;
  }

  /**
   * Associate a phone number with this mobile device.
   * @param phoneNumber The phone number.
   */
  public DeviceInfo setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
    return this;
  }

  /**
   * The carrier info for this device if available
   * @return the carrier info
   */
  public String getCarrierInfo() {
    return carrierInfo;
  }

  /**
   * The carrier for the device if applicable.
   * @param mCarrierInfo the carrier info
   * @return This object.
   * @see CarrierEnum#name()
   */
  public DeviceInfo setCarrierInfo(String mCarrierInfo) {
    this.carrierInfo = mCarrierInfo;
    return this;
  }

  /**
   * Get the client version major number.
   * @return The major version number.
   */
  public int getVersionMajor() {
    return versionMajor;
  }

  /**
   * Set the client version major number.
   * @param versionMajor A major version number.
   * @return This object.
   */
  public DeviceInfo setVersionMajor(int versionMajor) {
    this.versionMajor = versionMajor;
    return this;
  }

  /**
   * Get the client version minor number.
   * @return The minor version number.
   */
  public int getVersionMinor() {
    return versionMinor;
  }

  /**
   * Set the client version minor number.
   * @param versionMinor A minor version number.
   * @return This object.
   */
  public DeviceInfo setVersionMinor(int versionMinor) {
    this.versionMinor = versionMinor;
    return this;
  }

  /**
   * Get the extra properties in this device.
   * @return Any extra properties, or null.
   */
  public Map<String, String> getExtras() {
    return extras;
  }

  /**
   * Set the extra properties in this device.  An empty set will remove any
   * existing extra properties.
   * @param extras Extra properties.
   * @return This object.
   */
  public DeviceInfo setExtras(Map<String, String> extras) {
    this.extras = extras;
    return this;
  }
  
  /**
   * Get the optional tags in this device.
   * @return Any tags, or null.
   */
  public List<String> getTags() {
    return tags;
  }

  /**
   * Set the optional tags in this device.  An empty set will remove any
   * existing tags.
   * @param tags A list of tags.
   * @return This object.
   */
  public DeviceInfo setTags(List<String> tags) {
    this.tags = tags;
    return this;
  }

  /**
   * The model info for this device
   * @return the model info
   */
  public String getModelInfo() {
    return modelInfo;
  }

  /**
   * Set the model info for this device
   *
   * @param modelInfo the model info
   * @return this DeviceInfo instance
   */
  public DeviceInfo setModelInfo(String modelInfo) {
    this.modelInfo = modelInfo;
    return this;
  }
  
  public static DeviceInfo fromJson(String json) {
    return GsonData.getGson().fromJson(json, DeviceInfo.class);
  }
  
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("{devId='").append(devId)
      .append("', displayName='").append(displayName)
      .append("', modelInfo='").append(modelInfo)
      .append("', osType='").append(osType)
      .append("', osVersion='").append(osVersion)
      .append("', pushType='").append(pushType)
      .append("', pushToken='").append(pushToken)
      .append("', version='").append(versionMajor).append('.').append(versionMinor)
      .append("', carrier='").append((carrierInfo == null) ?
          "null" : carrierInfo.toString())
      .append("', phone='").append((phoneNumber == null) ?
          "null" : phoneNumber.length()+" digits")
      .append("', tags='").append((tags == null) ?
          "null" : tags.toString())
      .append("', extras='").append((extras == null) ?
          "null" : extras.toString())
      .append('}');
    return sb.toString();
  }
}
