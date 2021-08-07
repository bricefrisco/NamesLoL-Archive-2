package com.nameslol.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nameslol.models.exceptions.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.sql.Timestamp;

public final class ErrorResponseGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorResponseGenerator.class);
    public static ObjectMapper MAPPER = new ObjectMapper();

    public static String generate(String message, Response.Status status) {
        if (message == null) message = "Unknown exception occurred.";

        ErrorResponse errorResponse = new ErrorResponse(
                new Timestamp(System.currentTimeMillis()),
                status.getStatusCode(),
                status.getReasonPhrase(),
                message
        );

        try {
            return MAPPER.writeValueAsString(errorResponse);
        } catch (JsonProcessingException e) {
            LOGGER.warn("Failed to parse JSON error response message: " + e);
            return message;
        }
    }
}
