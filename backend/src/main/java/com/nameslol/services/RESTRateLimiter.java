package com.nameslol.services;

import com.nameslol.models.exceptions.RateLimitException;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.HashMap;

@ApplicationScoped
@Named("restRateLimiter")
@RegisterForReflection
public class RESTRateLimiter {
    private static final HashMap<String, Long> requests = new HashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(RESTRateLimiter.class);

    @ConfigProperty(name = "rest.throttle-ms")
    Integer throttle;

    public void checkIsLimited(String ip) {
        Long lastReq = requests.get(ip);

        if (lastReq == null) {
            requests.put(ip, System.currentTimeMillis());
            return;
        }

        if (System.currentTimeMillis() - lastReq < throttle) {
            LOGGER.info("Request from IP '" + ip + "' is being limited.");
            throw new RateLimitException("Please wait before sending more requests.");
        }

        requests.put(ip, System.currentTimeMillis());
    }
}
