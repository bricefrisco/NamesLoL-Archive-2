package com.nameslol.util;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RESTUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(RESTUtil.class);

    public static void logRequest(Exchange exchange) {
        String method = exchange.getIn().getHeader("CamelHttpMethod", String.class);
        String path = exchange.getIn().getHeader("CamelHttpPath", String.class);
        String query = exchange.getIn().getHeader("CamelHttpRawQuery", String.class);
        String ip = toIP(exchange);

        StringBuilder log = new StringBuilder();
        log.append(method);
        log.append(" ").append(path);
        if (query != null && !query.isBlank()) {
            log.append("?").append(query);
        }
        log.append(" (").append(ip).append(")");

        LOGGER.info(log.toString());
    }

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
        return ip;
    }
}
