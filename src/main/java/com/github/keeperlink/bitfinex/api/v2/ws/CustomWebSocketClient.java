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
package com.github.keeperlink.bitfinex.api.v2.ws;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import static com.github.keeperlink.bitfinex.api.util.MiniUtils.callback;
import com.github.keeperlink.bitfinex.api.util.ThreadUtil;
import com.github.keeperlink.bitfinex.api.util.TimeUtil.NanoTimer;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpClientTransport;
import org.eclipse.jetty.client.http.HttpClientTransportOverHTTP;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

/**
 *
 * @author whost
 */
@Slf4j
public class CustomWebSocketClient {

    private static final int MIN_OUTPUT_BUFFER_SIZE = 64 * 1024;
    private static final ScheduledExecutorScheduler scheduler = new ScheduledExecutorScheduler("WSClientScheduler", true);
    private static final Executor executor = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("WSClientExec-%02d").build());
    private static final AtomicInteger instanceCounter = new AtomicInteger();

    private final int instanceId = instanceCounter.getAndIncrement();
    private final String instanceName;
    private final URI uri;
    private final int maxMessageSize;
    private final SimpleWebSocket socket;
    private final HttpClient httpClient;
    private WebSocketClient client;
    private Consumer<String> onMessage;
    private Consumer<Session> onConnect;
    private BiConsumer<Integer, String> onDisconnect;
    private Consumer<Throwable> onError;

    @SuppressWarnings("SleepWhileInLoop")
    public static void main(String[] args) throws Exception {
        String wsUrl = "wss://api.bitfinex.com/ws/2";
        CustomWebSocketClient c = new CustomWebSocketClient(wsUrl, 8 * 1024)
                .onMessage(msg -> log.info("onMessage: {}", msg))
                .onConnect(sess -> log.info("onConnect: {}", sess))
                .onDisconnect((code, reason) -> log.info("onDisconnect: {} {}", code, reason))
                .onError(ex -> log.info("onError", ex))
                .start();
        log.info("isConnected: {}", c.isConnected());
        for (;;) {
            TimeUnit.SECONDS.sleep(120);
            log.info("isConnected: {}", c.isConnected());
            log.info("Sending STOP...");
            c.stop();
            TimeUnit.SECONDS.sleep(120);
            log.info("isConnected: {}", c.isConnected());
            log.info("Sending START...");
            c.start();
            log.info("isConnected: {}", c.isConnected());
        }
    }

    public CustomWebSocketClient(String wsUrl, int maxMessageSize) {
        try {
            this.uri = new URI(wsUrl);
        } catch (URISyntaxException ex) {
            throw new RuntimeException(wsUrl, ex);
        }
        this.maxMessageSize = maxMessageSize;
        this.instanceName = getClass().getSimpleName() + this.instanceId;
        if (log.isDebugEnabled()) {
            log.debug("{}.NEW: url:{}, maxMessageSize:{}.  {}", this.instanceName, wsUrl, maxMessageSize, ThreadUtil.stackTrace());
        }
        this.socket = new SimpleWebSocket();
        HttpClientTransport transport = new HttpClientTransportOverHTTP(1);
        SslContextFactory sslContextFactory = new SslContextFactory.Client();
        sslContextFactory.setTrustAll(false);
        sslContextFactory.setEndpointIdentificationAlgorithm("HTTPS");
        this.httpClient = new HttpClient(transport, sslContextFactory);
        Executor executor2 = command -> {
            if (command.getClass().getName().startsWith("org.eclipse.jetty.io.ChannelEndPoint")) {
                command.run();
            } else {
                if (log.isTraceEnabled()) {
                    log.trace("{}.executor2: {}  {}", this.instanceName, command.getClass(), ThreadUtil.fullStackTrace());
                }
                executor.execute(command);
            }
        };
        httpClient.setExecutor(executor2);
        httpClient.setScheduler(scheduler);
        httpClient.setResponseBufferSize(Math.max(maxMessageSize, MIN_OUTPUT_BUFFER_SIZE));
    }

    public CustomWebSocketClient onMessage(Consumer<String> onMessage) {
        this.onMessage = onMessage;
        return this;
    }

    public CustomWebSocketClient onConnect(Consumer<Session> onConnect) {
        this.onConnect = onConnect;
        return this;
    }

    public CustomWebSocketClient onDisconnect(BiConsumer<Integer, String> onDisconnect) {
        this.onDisconnect = onDisconnect;
        return this;
    }

    public CustomWebSocketClient onError(Consumer<Throwable> onError) {
        this.onError = onError;
        return this;
    }

    public CustomWebSocketClient start() throws IOException {
        NanoTimer timer = new NanoTimer();
        try {
            log.debug("{}.start: Starting httpClient...", instanceName);
            httpClient.start();
            log.trace("{}.start: httpClient Started", instanceName);
            this.client = new WebSocketClient(httpClient);
            client.start();
            log.trace("{}.start: WebSocketClient Started", instanceName);

            ClientUpgradeRequest request = new ClientUpgradeRequest();
            client.connect(socket, uri, request);
            return this;
        } catch (IOException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IOException(ex);
        } finally {
            log.debug("{}.start: FINISHED. Runtime: {}", instanceName, timer);
        }
    }

    public void stop() {
        NanoTimer timer = new NanoTimer();
        try {
            log.debug("{}.stop: Called", instanceName);
            socket.close();
            httpClient.stop();
            if (client != null) {
                client.stop();
            }
        } catch (Exception ex) {
            log.error(null, ex);
        } finally {
            log.debug("{}.stop: FINISHED. Runtime: {}", instanceName, timer);
        }
    }

    public void send(String msg) throws IOException {
        socket.send(msg);
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    @Override
    public String toString() {
        return instanceName;
    }

    private class SimpleWebSocket implements WebSocketListener {

        private Session session;

        @Override
        public void onWebSocketBinary(byte[] payload, int offset, int len) {
            log.info("{}.onWebSocketBinary: {}", instanceName, new String(payload, offset, len));
        }

        @Override
        public void onWebSocketText(String message) {
            callback(onMessage, message);
        }

        @Override
        public void onWebSocketClose(int statusCode, String reason) {
            log.debug("{}.onWebSocketClose: {} {}", instanceName, statusCode, reason);
            session = null;
            callback(onDisconnect, statusCode, reason);
        }

        @Override
        public void onWebSocketConnect(Session session) {
            this.session = session;
            WebSocketPolicy policy = session.getPolicy();
            policy.setMaxBinaryMessageBufferSize(maxMessageSize);
            policy.setMaxBinaryMessageSize(maxMessageSize);
            policy.setMaxTextMessageBufferSize(maxMessageSize);
            policy.setMaxTextMessageSize(maxMessageSize);
            log.debug("{}.onWebSocketConnect: {}", instanceName, session);
            callback(onConnect, session);
        }

        @Override
        public void onWebSocketError(Throwable cause) {
            log.debug(null, cause);
            callback(onError, cause);
        }

        void send(String msg) throws IOException {
            log.trace("{}.send: {}", instanceName, msg);
            Session s = session;
            if (s != null) {
                synchronized (sendSync) {
                    s.getRemote().sendString(msg);
                }
            } else {
                throw new IOException("Session is null");
            }
        }
        private final Object sendSync = new Object();

        boolean isConnected() {
            Session s = session;
            return s != null && s.isOpen();
        }

        void close() {
            Session s = session;
            if (s != null) {
                s.close();
                session = null;
            }
        }
    }
}
