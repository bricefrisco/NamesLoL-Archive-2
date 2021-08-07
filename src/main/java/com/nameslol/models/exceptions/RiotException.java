package com.nameslol.models.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
public class RiotException {
    private RiotExceptionStatus status;

    public RiotExceptionStatus getStatus() {
        return status;
    }

    public void setStatus(RiotExceptionStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "RiotException{" +
                "status=" + status +
                '}';
    }
}
