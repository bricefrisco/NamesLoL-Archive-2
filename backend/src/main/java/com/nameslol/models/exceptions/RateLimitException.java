package com.nameslol.models.exceptions;

import io.quarkus.arc.Unremovable;

@Unremovable
public class RateLimitException extends RuntimeException {
    public RateLimitException(String message) {
        super(message);
    }
}
