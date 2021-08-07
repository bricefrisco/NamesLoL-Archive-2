package com.nameslol.models.exceptions;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class RiotExceptionStatus {
    private String message;
    private String status_code;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus_code() {
        return status_code;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }

    @Override
    public String toString() {
        return "RiotExceptionStatus{" +
                "message='" + message + '\'' +
                ", status_code='" + status_code + '\'' +
                '}';
    }
}
