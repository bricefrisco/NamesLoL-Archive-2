package com.nameslol.routes;

import com.nameslol.models.SummonerResponseDTO;
import com.nameslol.models.exceptions.BadRequestException;
import com.nameslol.models.exceptions.RateLimitException;
import com.nameslol.models.exceptions.RiotAPIException;
import com.nameslol.models.exceptions.SummonerNotFoundException;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;

import java.util.List;

@RegisterForReflection
public class RESTRoute extends RouteBuilder {

    @Override
    public void configure() {
        onException(SummonerNotFoundException.class)
                .handled(Boolean.TRUE)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant("404"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .log("SummonerNotFound exception handled: ${exception.message}")
                .to("bean:errorResponseGenerator?method=generate(${exception.message}, 404)");

        onException(BadRequestException.class)
                .handled(Boolean.TRUE)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant("400"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .log("InvalidRequest exception handled: ${exception.message}")
                .to("bean:errorResponseGenerator?method=generate(${exception.message}, 400)");

        onException(RateLimitException.class)
                .handled(Boolean.TRUE)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant("429"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .log("RateLimit exception handled: ${exception.message}")
                .to("bean:errorResponseGenerator?method=generate(${exception.message}, 429)");

        onException(RiotAPIException.class)
                .handled(Boolean.TRUE)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant("500"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .log("RiotAPIException handled: ${exception.message}")
                .to("bean:errorResponseGenerator?method=generate(${exception.message}, 500)");

        restConfiguration()
                .host("{{rest.host}}").port("{{rest.port}}")
                .component("netty-http")
                .clientRequestValidation(Boolean.TRUE)
                .bindingMode(RestBindingMode.auto)
                .enableCORS(Boolean.TRUE);

        rest()
                .get("/health")
                    .to("direct:health-check")
                    .outType(String.class)
                .get("/{region}/summoners")
                    .param().name("timestamp").type(RestParamType.query).required(Boolean.TRUE).dataType("long").endParam()
                    .param().name("backwards").type(RestParamType.query).required(Boolean.FALSE).defaultValue("false").dataType("bool").endParam()
                    .param().name("nameLength").type(RestParamType.query).required(Boolean.FALSE).dataType("int").endParam()
                    .to("direct:get-summoners")
                    .outType(List.class)
                .get("/{region}/summoners/{name}")
                    .to("direct:get-summoner")
                    .outType(SummonerResponseDTO.class);

        from("direct:health-check")
                .setBody(constant("OK"));


        from("direct:get-summoners")
                .routeId("get-summoners")
                .to("bean:restUtil?method=logRequest(*)")
                .to("bean:requestValidator?method=validateRegion(${headers.region})")
                .to("bean:requestValidator?method=validateTimestamp(${headers.timestamp})")
                .choice().when(simple("${headers.nameLength} == null"))
                    .to("direct:query-range")
                .otherwise()
                    .to("bean:requestValidator?method=validateNameLength(${headers.nameLength})")
                    .to("direct:query-by-name-size")
                .end()
                .to("bean:recordMapper?method=toSummonerResponseDTOs")
                .to("bean:recordMapper?method=toSummonersResponseDTO");

        from("direct:get-summoner")
                .routeId("get-summoner")
                .to("bean:restUtil?method=logRequest(*)")
                .to("bean:restUtil?method=toIP(*)")
                .to("bean:restRateLimiter?method=checkIsLimited(${body})")
                .to("bean:requestValidator?method=validateRegion(${headers.region})")
                .to("bean:requestValidator?method=validateName(${headers.name})")
                .to("direct:update-summoner");
    }
}
