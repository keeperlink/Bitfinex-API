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

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author whost
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ThreadUtil {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4, new ThreadFactoryBuilder().setDaemon(true).setNameFormat("ThreadUtil.SchedulerExec-%d").build());

    public static ScheduledExecutorService getScheduler() {
        return scheduler;
    }

    public static String stackTrace() {
        return Stream.of(Thread.currentThread().getStackTrace())
                .filter(s -> s.getClassName().startsWith("com.github.keeperlink")
                && !s.getClassName().equals(ThreadUtil.class.getName())
                && !(s.getClassName().equals(MiniUtils.class.getName()) && "callback".equals(s.getMethodName())))
                .map(s -> shortenTraceName(s)).collect(Collectors.joining(" <= "));
    }

    public static String fullStackTrace() {
        return Stream.of(Thread.currentThread().getStackTrace())
                .filter(s -> !s.getClassName().equals(ThreadUtil.class.getName())
                && !(s.getClassName().equals(Thread.class.getName()) && "getStackTrace".equals(s.getMethodName())))
                .map(s -> shortenTraceName(s)).collect(Collectors.joining(" <= "));
    }

    public static String shortenTraceName(StackTraceElement e) {
        return e.getClassName().substring(e.getClassName().lastIndexOf('.') + 1) + '.' + e.getMethodName() + ':' + e.getLineNumber();
    }

    public static String shortenTraceName(String s) {
        return s.substring(s.lastIndexOf('.', s.lastIndexOf('.', s.lastIndexOf('.') - 1) - 1) + 1);
    }

    public static Thread newThread(Consumer<Thread> consumer) {
        return new Thread() {
            @Override
            public void run() {
                consumer.accept(Thread.currentThread());
            }
        };
    }
}
