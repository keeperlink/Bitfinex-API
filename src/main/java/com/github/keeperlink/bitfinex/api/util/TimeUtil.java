/*
 * Copyright 2019 Sliva Co.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.keeperlink.bitfinex.api.util;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 *
 * @author keeperlink
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TimeUtil {

    public static final ZoneOffset SYSTEM_ZONE_OFFSET = ZoneOffset.systemDefault().getRules().getOffset(Instant.now());
    private static final long initialSystemNanos = System.nanoTime();
    private static final long initialEpochMillis = System.currentTimeMillis();
    private static final long initialEpochNanos = TimeUnit.MILLISECONDS.toNanos(initialEpochMillis);
    private static final Instant initialInstant = Instant.ofEpochMilli(initialEpochMillis);

    public static long epochNanos() {
        return initialEpochNanos + (System.nanoTime() - initialSystemNanos);
    }

    public static long epochNanosToEpochMillis(long epochNanos) {
        return TimeUnit.NANOSECONDS.toMillis(epochNanos);
    }

    public static long systemNanosToEpochMillis(long systemNanos) {
        return initialEpochMillis + TimeUnit.NANOSECONDS.toMillis(systemNanos - initialSystemNanos);
    }

    public static long epochMillisToSystemNanos(long epochMillis) {
        return initialSystemNanos + TimeUnit.MILLISECONDS.toNanos(epochMillis - initialEpochMillis);
    }

    public static long epochNanosToSystemNanos(long epochNanos) {
        return initialSystemNanos + epochNanos - initialEpochNanos;
    }

    public static Instant getInstantWithNanos() {
        return initialInstant.plusNanos(System.nanoTime() - initialSystemNanos);
    }

    public static Instant systemNanosToInstant(long systemNanos) {
        return initialInstant.plusNanos(systemNanos - initialSystemNanos);
    }

    public static Instant epochNanosToInstant(long epochNanos) {
        return systemNanosToInstant(epochNanosToSystemNanos(epochNanos));
    }

    public static Instant toInstant(LocalDateTime dateTime) {
        return dateTime.toInstant(SYSTEM_ZONE_OFFSET);
    }

    public static class NanoTimer {

        private final long start = System.nanoTime();

        public long nanos() {
            return System.nanoTime() - start;
        }

        public Duration duration() {
            return Duration.ofNanos(nanos());
        }

        @Override
        public String toString() {
            return duration().toString();
        }
    }
}
