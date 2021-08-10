package com.nameslol.models.exceptions;

import io.quarkus.arc.Unremovable;

@Unremovable
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
