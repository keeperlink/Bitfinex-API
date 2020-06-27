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

//import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import java.io.IOException;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.util.Arrays;
//import java.util.Map;
/**
 *
 * @author sergei
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class JsonUtil {

    private static final Gson gson = new Gson();

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T fromJson(String str, Class<T> clazz) {
        return gson.fromJson(str, clazz);
    }

    public static String compact(String str) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readValue(str, JsonNode.class);
            return jsonNode.toString();
        } catch (IOException e) {
            log.debug(str, e);
            return str;
        }
    }

    public static String unquote(String s) {
        return StringUtils.strip(s, " \"");
    }

    /**
     * Split string with comma delimited values in one of two formats into array
     * of strings.
     *
     * [[101164522,1511757215416,1.851147,0.17559],[101164479,1511757212838,2.55347782,0.17574],[101164206,1511757193056,-0.3083,0.17526]]
     * [101164614,1511757223053,-0.04,0.17534]
     *
     * @param msg
     * @return
     */
    public static String[] splitArrays(String msg) {
        int n1 = 0, n2 = msg.length();
        if (msg.startsWith("[[")) {
            n1 += 2;
        } else if (msg.startsWith("[")) {
            n1++;
        }
        if (msg.endsWith("]]")) {
            n2 -= 2;
        } else if (msg.endsWith("]")) {
            n2--;
        }
        return msg.substring(n1, n2).split("\\]?\\,\\[?");
    }

    public static String[] parseArray(String s) {
        return new Gson().fromJson(s.replaceAll("(\\{[^\\{\\}]*\\})", "0"), String[].class);
    }

    public static String[][] parse2DArray(String s) {
        return new Gson().fromJson(s.replaceAll("(\\{[^\\{\\}]*\\})", "0"), String[][].class);
    }

    public static BigDecimal readBigDecimal(String s) {
        return s == null || "null".equals(s)
                ? null
                : "0".equals(s)
                ? BigDecimal.ZERO
                : "1".equals(s)
                ? BigDecimal.ONE
                : "10".equals(s)
                ? BigDecimal.TEN
                : new BigDecimal(s);
    }

    public static String asString(Object s) {
        return s == null || "null".equals(s) ? null : unquote(s.toString());
    }

    public static Long asLong(Object s, Long defaultValue) {
        return s == null || "null".equals(s) ? defaultValue
                : s instanceof Integer
                        ? Long.valueOf((Integer) s)
                        : s instanceof Long
                                ? (Long) s
                                : Long.valueOf(s.toString());
    }

    public static BigDecimal asBigDecimal(Object s) {
        return s == null || "null".equals(s)
                ? null
                : s instanceof BigDecimal
                        ? (BigDecimal) s
                        : s instanceof Long
                                ? BigDecimal.valueOf((Long) s)
                                : s instanceof Integer
                                        ? BigDecimal.valueOf((Integer) s)
                                        : readBigDecimal(s.toString());
    }

    public static long readLong(String s) {
        return s == null || "null".equals(s) ? 0 : Long.parseLong(s);
    }
}
