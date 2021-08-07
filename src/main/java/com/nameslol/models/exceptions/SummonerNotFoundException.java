package com.nameslol.models.exceptions;

public class SummonerNotFoundException extends RuntimeException {
    public SummonerNotFoundException(String message) {
        super(message);
    }
}
