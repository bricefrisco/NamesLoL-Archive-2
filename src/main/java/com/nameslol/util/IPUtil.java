package com.nameslol.util;

import io.vertx.core.http.HttpServerRequest;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IPUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(IPUtil.class);

    public static String toIP(Exchange exchange) {
        String ip = exchange.getIn().getHeader("X-Forwarded-For", String.class);
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = exchange.getIn().getHeader("Proxy-Client-IP", String.class);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = exchange.getIn().getHeader("WL-Proxy-Client-IP", String.class);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = exchange.getIn().getHeader("HTTP_CLIENT_IP", String.class);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = exchange.getIn().getHeader("HTTP_X_FORWARDED_FOR", String.class);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = exchange.getIn().getHeader("CamelNettyRemoteAddress", String.class);
        }

        LOGGER.info("IP: " + ip);
        return ip;
    }
}
