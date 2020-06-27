/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.keeperlink.bitfinex.api.util;

import java.time.Instant;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Sliva Co
 */
@Slf4j
public class TimeUtilTest {

    public TimeUtilTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of epochNanos method, of class TimeUtil.
     */
    @Test
    public void testEpochNanos() {
        log.info("epochNanos");
        long result = TimeUtil.epochNanos();
        assertTrue(result != 0);
    }

    /**
     * Test of epochNanosToEpochMillis method, of class TimeUtil.
     */
    @Test
    public void testEpochNanosToEpochMillis() {
        log.info("epochNanosToEpochMillis");
        long epochNanos = 1_000_000L;
        long expResult = 1L;
        long result = TimeUtil.epochNanosToEpochMillis(epochNanos);
        assertEquals(expResult, result);
    }

    /**
     * Test of systemNanosToEpochMillis method, of class TimeUtil.
     */
    @Test
    public void testSystemNanosToEpochMillis() {
        log.info("systemNanosToEpochMillis");
        long systemNanos = 0L;
        long result = TimeUtil.systemNanosToEpochMillis(systemNanos);
        assertTrue(result != 0);
    }

    /**
     * Test of epochMillisToSystemNanos method, of class TimeUtil.
     */
    @Test
    public void testEpochMillisToSystemNanos() {
        log.info("epochMillisToSystemNanos");
        long epochMillis = System.currentTimeMillis();
        long result = TimeUtil.epochMillisToSystemNanos(epochMillis);
        assertTrue(result != 0);
    }

    /**
     * Test of epochNanosToSystemNanos method, of class TimeUtil.
     */
    @Test
    public void testEpochNanosToSystemNanos() {
        log.info("epochNanosToSystemNanos");
        long epochNanos = 0L;
        long result = TimeUtil.epochNanosToSystemNanos(epochNanos);
        assertTrue(result != 0);
    }

    /**
     * Test of getInstantWithNanos method, of class TimeUtil.
     */
    @Test
    public void testGetInstantWithNanos() {
        log.info("getInstantWithNanos");
        Instant result = TimeUtil.getInstantWithNanos();
        assertNotNull(result);
    }

    /**
     * Test of systemNanosToInstant method, of class TimeUtil.
     */
    @Test
    public void testSystemNanosToInstant() {
        log.info("systemNanosToInstant");
        long systemNanos = 0L;
        Instant result = TimeUtil.systemNanosToInstant(systemNanos);
        assertNotNull(result);
    }

    /**
     * Test of epochNanosToInstant method, of class TimeUtil.
     */
    @Test
    public void testEpochNanosToInstant() {
        log.info("epochNanosToInstant");
        long epochNanos = 0L;
        Instant result = TimeUtil.epochNanosToInstant(epochNanos);
        assertNotNull(result);
    }

    /**
     * Test of toInstant method, of class TimeUtil.
     */
    @Test
    public void testToInstant() {
        log.info("toInstant");
        LocalDateTime dateTime = LocalDateTime.now();
        Instant result = TimeUtil.toInstant(dateTime);
        assertNotNull(result);
    }
}
