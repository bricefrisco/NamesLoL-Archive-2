package com.nameslol.services;

import com.nameslol.util.IPUtil;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.vertx.core.http.HttpServerRequest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;

@ApplicationScoped
@RegisterForReflection
public class RESTRateLimiter {
    private static final HashMap<String, Long> requests = new HashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(RESTRateLimiter.class);

    @ConfigProperty(name = "rest.throttle-ms")
    Integer throttle;

    public boolean isLimited(HttpServerRequest request) {
        return isLimited(IPUtil.toIP(request));
    }

    public boolean isLimited(String ip) {
        Long lastReq = requests.get(ip);

        if (lastReq == null) {
            requests.put(ip, System.currentTimeMillis());
            return false;
        }

        if (System.currentTimeMillis() - lastReq < throttle) {
            LOGGER.info("Request from IP '" + ip + "' is being limited.");
            return true;
        }

        requests.put(ip, System.currentTimeMillis());
        return false;
    }
}
