package com.nameslol.models.exceptions;

import io.quarkus.arc.Unremovable;
import io.quarkus.runtime.annotations.RegisterForReflection;

@Unremovable
@RegisterForReflection
public class RiotAPIException extends RuntimeException {
    public RiotAPIException(String message) {
        super(message);
    }
}
