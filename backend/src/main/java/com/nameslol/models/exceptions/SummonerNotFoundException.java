package com.nameslol.models.exceptions;

import io.quarkus.arc.Unremovable;

@Unremovable
public class SummonerNotFoundException extends RuntimeException {
    public SummonerNotFoundException(String message) {
        super(message);
    }
}
