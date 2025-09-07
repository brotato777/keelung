package com.example.keelungapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogTest {
    private static final Logger logger = LoggerFactory.getLogger(LogTest.class);

    public static void main(String[] args) {
        logger.trace("這是 trace");
        logger.debug("這是 debug");
        logger.info("這是 info");
        logger.warn("這是 warn");
        logger.error("這是 error");
    }
}
