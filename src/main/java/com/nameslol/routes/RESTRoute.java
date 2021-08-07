package com.nameslol.routes;

import com.nameslol.models.exceptions.BadRequestException;
import com.nameslol.util.ErrorResponseGenerator;
import com.nameslol.util.RecordMapper;
import com.nameslol.util.Validator;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;

import java.util.List;

@RegisterForReflection
public class RESTRoute extends RouteBuilder {

    @Override
    public void configure() {
        onException(Exception.class)
                .handled(Boolean.TRUE)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant("500"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .log(LoggingLevel.ERROR, "${exception.message}")
                .log(LoggingLevel.ERROR, "${exception.stacktrace}")
                .bean(ErrorResponseGenerator.class, "generate(${exception.message})");

        onException(BadRequestException.class)
                .handled(Boolean.TRUE)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant("400"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .log("InvalidRequest exception handled: ${exception.message}")
                .bean(ErrorResponseGenerator.class, "generate(${exception.message})");

        restConfiguration()
                .host("{{rest.host}}").port("{{rest.port}}")
                // .clientRequestValidation(Boolean.TRUE)
                .bindingMode(RestBindingMode.auto)
                .enableCORS(Boolean.TRUE);

        rest().get("/{region}/summoners")
                .param().name("timestamp").type(RestParamType.query).required(Boolean.TRUE).dataType("long").endParam()
                .param().name("backwards").type(RestParamType.query).required(Boolean.FALSE).defaultValue("false").dataType("bool").endParam()
                .to("direct:get-region-summoners")
                .outType(List.class);

        from("direct:get-region-summoners")
                .routeId("get-region-summoners")
                .bean(Validator.class, "validateRegion(${headers.region})")
                .bean(Validator.class, "validateTimestamp(${headers.timestamp})")
                .to("direct:query-range")
                .bean(RecordMapper.class, "toSummonerResponseDTOs");
    }
}
