/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.keeperlink.bitfinex.api.v1;

import static com.github.keeperlink.bitfinex.api.util.MiniUtils.readFromURL;
import com.github.keeperlink.bitfinex.api.util.TimeUtil;
import com.google.gson.Gson;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Sliva Co
 */
@Slf4j
public class BitfinexCalls {

    public static String[] loadBitfinexPairs() throws IOException {
        TimeUtil.NanoTimer timer = new TimeUtil.NanoTimer();
        log.trace("MiniUtils.loadBitfinexPairs() Called");
        try {
            String s = readFromURL("https://api.bitfinex.com/v1/symbols");
            return new Gson().fromJson(s.toUpperCase(), String[].class);
        } finally {
            log.trace("MiniUtils.loadBitfinexPairs() Finished. Runtime: {}", timer);
        }
    }
//
//    public static BitfinexSymbolDetails[] loadBitfinexPairDetails() throws IOException {
//        NanoTimer timer = new NanoTimer();
//        log.trace("MiniUtils.loadBitfinexPairDetails() Called");
//        try {
//            String s = readFromURL("https://api.bitfinex.com/v1/symbols_details");
//            return new Gson().fromJson(s, BitfinexSymbolDetails[].class);
//        } finally {
//            log.trace("MiniUtils.loadBitfinexPairDetails() Finished. Runtime: {}", timer);
//        }
//    }

    public static String[][] loadBitfinexTickers() throws IOException {
        TimeUtil.NanoTimer timer = new TimeUtil.NanoTimer();
        log.trace("MiniUtils.loadBitfinexTickers() Called");
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
            headers.set("accept-language", "en-CA,en;q=0.9");
            headers.set("cache-control", "max-age=0");
            headers.set("upgrade-insecure-requests", "1");
            headers.set("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
            HttpEntity entity = new HttpEntity<>(headers);
            ResponseEntity<String[][]> out = new RestTemplate().exchange("https://api-pub.bitfinex.com/v2/tickers?symbols=ALL", HttpMethod.GET, entity, String[][].class);
            //[["tBTCUSD",5428,34.7009749,5428.1,45.38812333,19.2,0.0035,5428,4600.25455625,5458,5351],...],
            //SYMBOL,    BID,     BID_SIZE,     ASK,     ASK_SIZE,     DAILY_CHANGE,     DAILY_CHANGE_PERC,     LAST_PRICE,     VOLUME,     HIGH,     LOW
            return out.getBody();
        } finally {
            log.trace("MiniUtils.loadBitfinexTickers() Finished. Runtime: {}", timer);
        }
    }

}
