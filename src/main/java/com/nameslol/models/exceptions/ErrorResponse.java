package com.nameslol.models.exceptions;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.sql.Timestamp;

@RegisterForReflection
public class ErrorResponse {
    private Timestamp timestamp;
    private int status;
    private String message;

    public ErrorResponse(Timestamp timestamp, int status, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
