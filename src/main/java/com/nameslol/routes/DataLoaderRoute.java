package com.nameslol.routes;

import com.nameslol.models.exceptions.SummonerNotFoundException;
import com.nameslol.util.QueryUtil;
import com.nameslol.util.RecordMapper;
import com.nameslol.util.RequestValidator;
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
        onException(SummonerNotFoundException.class)
                .handled(Boolean.TRUE)
                .log("Summoner ${headers.name} was not found (${headers.region}).");

        from("{{dataloader.input-file}}")
                .routeId("input-loader")
                .split().tokenize("\n")
                    .filter().method(RequestValidator.class, "isValid")
                    .to("bean:riotAPI?method=format")
                    .setHeader("name", simple("${body}"))
                    .wireTap("seda:na-queue?blockWhenFull=true")
                    .wireTap("seda:br-queue?blockWhenFull=true")
                    .wireTap("seda:eune-queue?blockWhenFull=true")
                    .wireTap("seda:euw-queue?blockWhenFull=true")
                    .wireTap("seda:kr-queue?blockWhenFull=true")
                    .wireTap("seda:lan-queue?blockWhenFull=true")
                    .wireTap("seda:las-queue?blockWhenFull=true")
                    .wireTap("seda:tr-queue?blockWhenFull=true");

        from("quartz:week-refresh/na?cron={{dataloader.weekly-refresh-schedule}}")
                .routeId("na-week-refresh")
                .log("CRON job for na-week-refresh started.")
                .setHeader("region", constant("NA"))
                .setHeader("sedaRoute", constant("seda:na-queue"))
                .to("direct:start-week-refresh");

        from("quartz:week-refresh/br?cron={{dataloader.weekly-refresh-schedule}}")
                .routeId("br-week-refresh")
                .log("CRON job for br-week-refresh started.")
                .setHeader("region", constant("BR"))
                .setHeader("sedaRoute", constant("seda:br-queue"))
                .to("direct:start-week-refresh");

        from("quartz:week-refresh/eune?cron={{dataloader.weekly-refresh-schedule}}")
                .routeId("eune-week-refresh")
                .log("CRON job for eune-week-refresh started.")
                .setHeader("region", constant("EUNE"))
                .setHeader("sedaRoute", constant("seda:eune-queue"))
                .to("direct:start-week-refresh");

        from("quartz:week-refresh/euw?cron={{dataloader.weekly-refresh-schedule}}")
                .routeId("euw-week-refresh")
                .log("CRON job for euw-week-refresh started.")
                .setHeader("region", constant("EUW"))
                .setHeader("sedaRoute", constant("seda:euw-queue"))
                .to("direct:start-week-refresh");

        from("quartz:week-refresh/kr?cron={{dataloader.weekly-refresh-schedule}}")
                .routeId("kr-week-refresh")
                .log("CRON job for kr-week-refresh started.")
                .setHeader("region", constant("KR"))
                .setHeader("sedaRoute", constant("seda:kr-queue"))
                .to("direct:start-week-refresh");

        from("quartz:week-refresh/lan?cron={{dataloader.weekly-refresh-schedule}}")
                .routeId("lan-week-refresh")
                .log("CRON job for lan-week-refresh started.")
                .setHeader("region", constant("LAN"))
                .setHeader("sedaRoute", constant("seda:lan-queue"))
                .to("direct:start-week-refresh");

        from("quartz:week-refresh/las?cron={{dataloader.weekly-refresh-schedule}}")
                .routeId("las-week-refresh")
                .log("CRON job for las-week-refresh started.")
                .setHeader("region", constant("LAS"))
                .setHeader("sedaRoute", constant("seda:las-queue"))
                .to("direct:start-week-refresh");

        from("quartz:week-refresh/tr?cron={{dataloader.weekly-refresh-schedule}}")
                .routeId("tr-week-refresh")
                .log("CRON job for tr-week-refresh started.")
                .setHeader("region", constant("TR"))
                .setHeader("sedaRoute", constant("seda:tr-queue"))
                .to("direct:start-week-refresh");

        from("seda:na-queue?concurrentConsumers={{dataloader.concurrent-consumers}}")
                .routeId("na-queue")
                .throttle(throttle)
                .setHeader("region", simple("NA"))
                .to("direct:update-summoner");

        from("seda:br-queue?concurrentConsumers={{dataloader.concurrent-consumers}}")
                .routeId("br-queue")
                .throttle(throttle)
                .setHeader("region", simple("BR"))
                .to("direct:update-summoner");

        from("seda:eune-queue?concurrentConsumers={{dataloader.concurrent-consumers}}")
                .routeId("eune-queue")
                .throttle(throttle)
                .setHeader("region", simple("EUNE"))
                .to("direct:update-summoner");

        from("seda:euw-queue?concurrentConsumers={{dataloader.concurrent-consumers}}")
                .routeId("euw-queue")
                .throttle(throttle)
                .setHeader("region", simple("EUW"))
                .to("direct:update-summoner");

        from("seda:kr-queue?concurrentConsumers={{dataloader.concurrent-consumers}}")
                .routeId("kr-queue")
                .throttle(throttle)
                .setHeader("region", simple("KR"))
                .to("direct:update-summoner");

        from("seda:lan-queue?concurrentConsumers={{dataloader.concurrent-consumers}}")
                .routeId("lan-queue")
                .throttle(throttle)
                .setHeader("region", simple("LAN"))
                .to("direct:update-summoner");

        from("seda:las-queue?concurrentConsumers={{dataloader.concurrent-consumers}}")
                .routeId("las-queue")
                .throttle(throttle)
                .setHeader("region", simple("LAS"))
                .to("direct:update-summoner");

        from("seda:tr-queue?concurrentConsumers={{dataloader.concurrent-consumers}}")
                .routeId("tr-queue")
                .throttle(throttle)
                .setHeader("region", simple("TR"))
                .to("direct:update-summoner");

        from("direct:start-week-refresh")
                .routeId("start-week-refresh")
                .bean(QueryUtil.class, "lastWeekInMs")
                .setHeader("t2", simple("${body}"))
                .bean(QueryUtil.class, "nextWeekInMs")
                .setHeader("t1", simple("${body}"))
                .to("direct:query-between")
                    .bean(RecordMapper.class, "toSummonerResponseDTOs")
                    .split().body()
                    .setHeader("isDynamoSummonerName", simple("true"))
                    .setHeader("name", simple("${body.name}"))
                    .toD("${headers.sedaRoute}?blockWhenFull=true");

    }
}
