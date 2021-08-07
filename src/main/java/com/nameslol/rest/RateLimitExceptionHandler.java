package com.nameslol.rest;

import com.nameslol.models.exceptions.RateLimitException;
import com.nameslol.util.ErrorResponseGenerator;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RateLimitExceptionHandler implements ExceptionMapper<RateLimitException> {
    @Override
    public Response toResponse(RateLimitException e) {
        return Response.status(Response.Status.TOO_MANY_REQUESTS).entity(ErrorResponseGenerator.generate(e.getMessage(), Response.Status.TOO_MANY_REQUESTS)).build();
    }
}
