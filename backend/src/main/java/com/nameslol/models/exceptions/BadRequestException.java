package com.nameslol.models.exceptions;

import io.quarkus.arc.Unremovable;
import io.quarkus.runtime.annotations.RegisterForReflection;

@Unremovable
@RegisterForReflection
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
