package com.nameslol.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nameslol.models.exceptions.ErrorResponse;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.sql.Timestamp;

@ApplicationScoped
@Named("errorResponseGenerator")
@RegisterForReflection
public class ErrorResponseGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorResponseGenerator.class);
    public static ObjectMapper MAPPER = new ObjectMapper();

    public String generate(String message, int statusCode) {
        if (message == null) message = "Unknown exception occurred.";

        ErrorResponse errorResponse = new ErrorResponse(
                new Timestamp(System.currentTimeMillis()),
                statusCode,
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
