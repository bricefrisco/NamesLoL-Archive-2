package com.nameslol.routes;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;

@RegisterForReflection
@ApplicationScoped
public class DataLoaderRoute extends RouteBuilder {

    @ConfigProperty(name = "dataloader.throttle-per-second")
    Integer throttle;

    @Override
    public void configure() {
        from("{{dataloader.input-file}}")
                .routeId("input-loader")
                .split().tokenize("\n")
                .to("bean:riotAPI?method=format")
                .setHeader("name", simple("${body}"))
                .wireTap("seda:na-queue")
                .wireTap("seda:br-queue")
                .wireTap("seda:eune-queue")
                .wireTap("seda:euw-queue")
                .wireTap("seda:kr-queue")
                .wireTap("seda:lan-queue")
                .wireTap("seda:las-queue")
                .wireTap("seda:tr-queue");


        from("seda:na-queue")
                .routeId("na-queue")
                .throttle(throttle)
                .setHeader("region", simple("NA"))
                .to("direct:update-summoner");

        from("seda:br-queue")
                .throttle(throttle)
                .setHeader("region", simple("BR"))
                .to("direct:update-summoner");

        from("seda:eune-queue")
                .throttle(throttle)
                .setHeader("region", simple("EUNE"))
                .to("direct:update-summoner");

        from("seda:euw-queue")
                .throttle(throttle)
                .setHeader("region", simple("EUW"))
                .to("direct:update-summoner");

        from("seda:kr-queue")
                .throttle(throttle)
                .setHeader("region", simple("KR"))
                .to("direct:update-summoner");

        from("seda:lan-queue")
                .throttle(throttle)
                .setHeader("region", simple("LAN"))
                .to("direct:update-summoner");

        from("seda:las-queue")
                .throttle(throttle)
                .setHeader("region", simple("LAS"))
                .to("direct:update-summoner");

        from("seda:tr-queue")
                .throttle(throttle)
                .setHeader("region", simple("TR"))
                .to("direct:update-summoner");
    }
}
