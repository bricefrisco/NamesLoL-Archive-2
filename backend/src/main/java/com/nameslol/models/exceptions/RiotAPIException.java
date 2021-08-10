package com.nameslol.models.exceptions;

import io.quarkus.arc.Unremovable;

@Unremovable
public class RiotAPIException extends RuntimeException {
    public RiotAPIException(String message) {
        super(message);
    }
}
