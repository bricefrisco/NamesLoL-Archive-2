package com.nameslol.routes;

import com.nameslol.models.Region;
import com.nameslol.services.RiotAPI;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;

import javax.inject.Inject;

@RegisterForReflection
public class DataLoaderRoute extends RouteBuilder {

    @Inject
    RiotAPI riotAPI;

    @Override
    public void configure() {
        from("{{dataloader.input-file}}")
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


        from("seda:na-queue").throttle(3).setHeader("region", simple("NA")).to("direct:update-summoner");
        from("seda:br-queue").throttle(3).setHeader("region", simple("BR")).to("direct:update-summoner");
        from("seda:eune-queue").throttle(3).setHeader("region", simple("EUNE")).to("direct:update-summoner");
        from("seda:euw-queue").throttle(3).setHeader("region", simple("EUW")).to("direct:update-summoner");
        from("seda:kr-queue").throttle(3).setHeader("region", simple("KR")).to("direct:update-summoner");
        from("seda:lan-queue").throttle(3).setHeader("region", simple("LAN")).to("direct:update-summoner");
        from("seda:las-queue").throttle(3).setHeader("region", simple("LAS")).to("direct:update-summoner");
        from("seda:tr-queue").throttle(3).setHeader("region", simple("TR")).to("direct:update-summoner");

        from("direct:update-summoner")
                .to("bean:riotAPI?method=fetchSummonerName(${body}, ${headers.region})")
                .log("Region ${headers.region}: ${body}");
    }

    private Predicate regionIs(Region region) {
        return e -> e.getIn().getHeader("region", Region.class) == region;
    }
}
