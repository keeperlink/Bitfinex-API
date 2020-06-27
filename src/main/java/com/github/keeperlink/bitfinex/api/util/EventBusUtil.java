/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.keeperlink.bitfinex.api.util;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Sliva Co
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class EventBusUtil {

    public static EventBus checkCreateEventBus(EventBus eventBus, String busName) {
        return eventBus == null ? createEventBus(busName) : eventBus;
    }

    public static EventBus createEventBus(String busName) {
        log.trace("MiniUtils.createEventBus: {}", busName);
        return new EventBus(busName);
    }

    public static EventBus createAsyncEventBus(String busName, int numThreads) {
        Executor executor = Executors.newFixedThreadPool(numThreads, new ThreadFactoryBuilder().setDaemon(true).setNameFormat(busName + "-%d").build());
        log.trace("MiniUtils.createAsyncEventBus: {}", busName);
        return new AsyncEventBus(busName, executor);
    }

}
