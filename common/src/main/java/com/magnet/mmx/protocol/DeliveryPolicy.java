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

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;
import com.magnet.mmx.protocol.RetryPolicy.Backoff;
import com.magnet.mmx.util.JSONifiable;

/**
 * The delivery policy specified by the sender to inform MMX how a wake-up
 * message (if applicable) should be sent.
 *
 */
public class DeliveryPolicy extends JSONifiable implements Serializable {
  private static final long serialVersionUID = -8641142289952876320L;
  @SerializedName("wakeups")
  private ArrayList<WakeUp> mWakeUps = new ArrayList<WakeUp>();
  
  public enum WakeUp {
    /**
     * Push Message with 2 tries, 30s interval, linear backoff
     */
    PUSH_MSG(2, 30, Backoff.LINEAR),
    /**
     * SMS Message with 1 try, 15s interval, linear backoff
     */
    SMS_MSG(1, 15, Backoff.LINEAR),
    /**
     * Phone call with 2 tries, 60s interval, binary exponential backoff
     */
    PHONE_CALL(2, 60, Backoff.EXP);
    
    @SerializedName("policy")
    private RetryPolicy mPolicy;

    private WakeUp(int numTries, int retryInterval, Backoff backoff) {
      mPolicy = new RetryPolicy(numTries, retryInterval, backoff);
    }
    
    /**
     * Get the retry policy for the wake-up scheme.
     * @return
     */
    public RetryPolicy getRetryPolicy() {
      return mPolicy;
    }
  }
  
  /**
   * Constructor with different wake-up schemes.
   * @param wakeups
   */
  public DeliveryPolicy(WakeUp...wakeups) {
    for (WakeUp wakeup : wakeups) {
      mWakeUps.add(wakeup);
    }
  }
  
  public static void main(String[] args) {
    DeliveryPolicy dp = new DeliveryPolicy(WakeUp.PUSH_MSG, WakeUp.SMS_MSG, 
        WakeUp.PHONE_CALL);
  }
}
