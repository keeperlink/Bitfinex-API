/*
 * Copyright 2018 Sliva Co.
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

import com.google.common.io.Resources;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.zip.GZIPOutputStream;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 *
 * @author whost
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class MiniUtils {

    static {
        SLF4JBridgeHandler.install();
    }

    public static String splitPair(String pair) {
        return pair.substring(0, 3) + '/' + pair.substring(3);
    }

    public static String readFromURL(String url) throws IOException {
        try (InputStream in = new URL(url).openStream()) {
            return IOUtils.toString(in, StandardCharsets.UTF_8);
        }
    }

    public static File compressFile(File f) {
        if (!f.exists()) {
            return null;
        }
        File zipFile = new File(f.getAbsolutePath() + ".gz");
        if (zipFile.exists()) {
            zipFile.delete();
        }
        try (GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(zipFile));
                InputStream in = new FileInputStream(f)) {
            IOUtils.copyLarge(in, out);
        } catch (IOException e) {
            throw new RuntimeException("compressFile: File: " + f, e);
        }
        f.delete();
        return zipFile;
    }

    public static void sleepQuiet(long timeMsec) {
        try {
            Thread.sleep(timeMsec);
        } catch (InterruptedException e) {
            log.error(null, e);
            throw new RuntimeException(e);
        }
    }

    public static <T> Set<Class<? extends T>> toSet(Class<? extends T>... events) {
        return events == null || events.length == 0 ? Collections.emptySet()
                : events.length == 1 ? Collections.singleton(events[0])
                        : Collections.unmodifiableSet(new HashSet<>(Arrays.asList(events)));
    }

    public static <T> Set<Class<? extends T>> toSet(Set<Class<? extends T>> evSet, Class<? extends T>... events) {
        Set<Class<? extends T>> result = new HashSet<>(Arrays.asList(events));
        result.addAll(evSet);
        return Collections.unmodifiableSet(result);
    }

    public static <T> Set<Class<? extends T>> merge(Set<Class<? extends T>> set1, Set<Class<? extends T>> set2) {
        Set<Class<? extends T>> result = new HashSet<>(set1);
        result.addAll(set2);
        return Collections.unmodifiableSet(result);
    }

    public static <T> Collection<T> addElement(Collection<T> collection, T element) {
        collection.add(element);
        return collection;
    }

    public static String resourceToString(String resourceName) {
        try {
            URL url = Resources.getResource(resourceName);
            return Resources.toString(url, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T defaultValue(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    public static <T> T defaultValue(T value, Supplier<T> defaultValue) {
        return value != null ? value : defaultValue.get();
    }

    public static <T> boolean callback(Consumer<T> callback, T event) {
        if (callback != null) {
            try {
                callback.accept(event);
                return true;
            } catch (Exception ex) {
                log.error(null, ex);
            }
        }
        return false;
    }

    public static <T, S> boolean callback(BiConsumer<T, S> callback, T param1, S param2) {
        if (callback != null) {
            try {
                callback.accept(param1, param2);
                return true;
            } catch (Exception ex) {
                log.error(null, ex);
            }
        }
        return false;
    }

    public static <T> boolean callbackSup(Consumer<T> callback, Supplier<T> event) {
        if (callback != null) {
            try {
                callback.accept(event.get());
                return true;
            } catch (Exception ex) {
                log.error(null, ex);
            }
        }
        return false;
    }

    public static BigDecimal toBigDecimalOrZero(String str) {
        return toBigDecimal(str, BigDecimal.ZERO);
    }

    public static BigDecimal toBigDecimalOrNull(String str) {
        return toBigDecimal(str, null);
    }

    public static BigDecimal toBigDecimal(String str, BigDecimal defaultValue) {
        if (!StringUtils.isBlank(str)) {
            try {
                return new BigDecimal(str);
            } catch (Exception ex) {
                log.debug("Error parsing into BigDecimal: \"" + str + '"', ex);
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }
}
