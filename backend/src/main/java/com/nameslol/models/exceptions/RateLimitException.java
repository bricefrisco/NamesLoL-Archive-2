package com.nameslol.models.exceptions;

import io.quarkus.arc.Unremovable;
import io.quarkus.runtime.annotations.RegisterForReflection;

@Unremovable
@RegisterForReflection
public class RateLimitException extends RuntimeException {
    public RateLimitException(String message) {
        super(message);
    }
}
