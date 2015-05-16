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
package com.magnet.mmx.server.plugin.mmxmgmt.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 */

public class RateLimiterServiceTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(RateLimiterServiceTest.class);

  final AtomicInteger inAppSuccess = new AtomicInteger(0);
  final AtomicInteger inAppFailure = new AtomicInteger(0);

  final AtomicInteger pushSuccess = new AtomicInteger(0);
  final AtomicInteger pushFailure = new AtomicInteger(0);

  public void testMessageRatesBurst() {
    resetCounters();
    final long testDuration = 10L;
     /*
     * Cap 100 permits per second
     */
    final int cappedRateLimit = 50;

    /**
     *  Attempted burst
     */
    final int burst = 100;

    final double expFailPercent = (burst - cappedRateLimit) * 100.0 / (double)burst;

    RateLimiterService.init();

    RateLimiterService.setInAppMsgRate(cappedRateLimit);
    RateLimiterService.setPushMsgRate(cappedRateLimit);

    ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    final ScheduledExecutorService msgExecutor = Executors.newScheduledThreadPool(burst);

    ScheduledFuture<?> rateLimitFuture = executor.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        for (int i = 0; i < burst; i++) {
          msgExecutor.submit(new Runnable() {
            @Override
            public void run() {
              if (RateLimiterService.getInAppPermit())
                inAppSuccess.incrementAndGet();
              else
                inAppFailure.incrementAndGet();
              if (RateLimiterService.getPushPermit())
                pushSuccess.incrementAndGet();
              else
                pushFailure.incrementAndGet();
            }
          });
        }
      }
    }, 0L, 10, TimeUnit.SECONDS);

    List<ScheduledFuture<?>> list = new ArrayList<ScheduledFuture<?>>();
    list.add(rateLimitFuture);
    executor.schedule(new StopTestTask(list), testDuration, TimeUnit.MINUTES);

    while(!rateLimitFuture.isDone());
    LOGGER.trace("testMessageRates : calculatedInAppFailure={}, expectedInAppFailure={}", getInAppFailure(), expFailPercent);
    LOGGER.trace("testMessageRates : calculatePushFailure={}, expectedPushFailure={}", getPushFailure(), expFailPercent);
  }

  public void testMessageRates() {
    ScheduledFuture<?> inAppFuture;
    ScheduledFuture<?> pushFuture;
    resetCounters();

    /*
     * Cap 100 permits per second
     */
    final int cappedRateLimit = 50;

    /**
     * Attempt 200 permits per second
     */
    final int attemptedRate = 55;

    /**
     * expected failure percentage
     */

    final double expFailPercent = round(((attemptedRate - cappedRateLimit) * 100.0) / (double) attemptedRate);

    /**
     * Run the test for 5 minutes;
     */
    final long testDuration = 10L;


    final long period = 1000L / attemptedRate;

    ScheduledExecutorService inAppExecutor = Executors.newSingleThreadScheduledExecutor();
    ScheduledExecutorService pushExecutor = Executors.newSingleThreadScheduledExecutor();
    ScheduledExecutorService stopTestExecutor = Executors.newSingleThreadScheduledExecutor();

    RateLimiterService.init();

    RateLimiterService.setInAppMsgRate(cappedRateLimit);
    RateLimiterService.setPushMsgRate(cappedRateLimit);

    /**
     * Try getting a permit every 200 times a second. I.e in 1 minute (1000 milliseconds) try acquiring a permit
     * every 5 milliseconds; And run this test for 2 minutes
     *
     */

    inAppFuture = inAppExecutor.scheduleAtFixedRate(new Runnable() {

      public void run() {
        if (RateLimiterService.getInAppPermit())
          inAppSuccess.incrementAndGet();
        else
          inAppFailure.incrementAndGet();
      }
    }, 0L, period, TimeUnit.MILLISECONDS);

    pushFuture = pushExecutor.scheduleAtFixedRate(new Runnable() {

      public void run() {
        if (RateLimiterService.getPushPermit())
          pushSuccess.incrementAndGet();
        else
          pushFailure.incrementAndGet();
      }
    }, 0L, period, TimeUnit.MILLISECONDS);

    List<ScheduledFuture<?>> futuresList = new ArrayList<ScheduledFuture<?>>();
    futuresList.add(inAppFuture);
    futuresList.add(pushFuture);
    stopTestExecutor.schedule(new StopTestTask(futuresList), testDuration, TimeUnit.MINUTES);

    while (!inAppFuture.isDone() && !pushFuture.isDone()) ;

    LOGGER.trace("testMessageRates : calculatedInAppFailure={}, expectedInAppFailure={}", getInAppFailure(), expFailPercent);
    LOGGER.trace("testMessageRates : calculatePushFailure={}, expectedPushFailure={}", getPushFailure(), expFailPercent);
  }

  private class StopTestTask implements Runnable {
    private final List<ScheduledFuture<?>> futuresList;

    public StopTestTask(List<ScheduledFuture<?>> futuresList) {
      this.futuresList = futuresList;
    }

    @Override
    public void run() {
      for(ScheduledFuture<?> f : futuresList) {
        if(f != null)
          f.cancel(true);
      }
    }
  }

  private void resetCounters() {
    inAppFailure.set(0);
    inAppFailure.set(0);
    pushFailure.set(0);
    pushSuccess.set(0);
  }

  private double getInAppFailure() {
    return getFailurePercent(inAppFailure, inAppSuccess);
  }

  private double getPushFailure() {
    return getFailurePercent(pushFailure, pushSuccess);
  }

  private double getFailurePercent(AtomicInteger failure, AtomicInteger success) {
    double retVal = ((double) failure.get() * 100.0) / ((double) failure.get() + (double) success.get());
    return round(retVal);
  }

  private double round(double value) {
    return Double.valueOf(new DecimalFormat("#.##").format(value));
  }
}
