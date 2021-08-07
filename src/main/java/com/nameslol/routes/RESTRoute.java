package com.nameslol.routes;

import com.nameslol.models.exceptions.BadRequestException;
import com.nameslol.util.ErrorResponseGenerator;
import com.nameslol.util.RESTUtil;
import com.nameslol.util.RecordMapper;
import com.nameslol.util.Validator;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
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
                .component("netty-http")
                .clientRequestValidation(Boolean.TRUE)
                .bindingMode(RestBindingMode.auto)
                .enableCORS(Boolean.TRUE);

        rest().get("/{region}/summoners")
                .param().name("timestamp").type(RestParamType.query).required(Boolean.TRUE).dataType("long").endParam()
                .param().name("backwards").type(RestParamType.query).required(Boolean.FALSE).defaultValue("false").dataType("bool").endParam()
                .param().name("nameLength").type(RestParamType.query).required(Boolean.FALSE).dataType("int").endParam()
                .to("direct:get-summoners")
                .outType(List.class);

        from("direct:get-summoners")
                .routeId("get-summoners")
                .bean(RESTUtil.class, "logRequest(*)")
                .bean(Validator.class, "validateRegion(${headers.region})")
                .bean(Validator.class, "validateTimestamp(${headers.timestamp})")
                .choice().when(simple("${headers.nameLength} == null"))
                    .log("Querying range")
                    .to("direct:query-range")
                .otherwise()
                    .bean(Validator.class, "validateNameLength(${headers.nameLength})")
                    .to("direct:query-by-name-size")
                .end()
                .bean(RecordMapper.class, "toSummonerResponseDTOs");
    }
}
