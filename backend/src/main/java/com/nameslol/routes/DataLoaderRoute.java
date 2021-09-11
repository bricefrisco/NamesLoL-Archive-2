package com.nameslol.routes;

import com.nameslol.models.exceptions.SummonerNotFoundException;
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
                    .filter().method("requestValidator", "isValid")
                    .to("bean:riotAPI?method=format")
                    .setHeader("name", simple("${body}"))
                    .wireTap("seda:na-queue?blockWhenFull=true")
                    .wireTap("seda:eune-queue?blockWhenFull=true")
                    .wireTap("seda:euw-queue?blockWhenFull=true")
                    .wireTap("seda:las-queue?blockWhenFull=true")
                    .wireTap("seda:oce-queue?blockWhenFull=true");

        from("quartz:days-refresh/na?cron={{dataloader.days-refresh-schedule}}")
                .routeId("na-days-refresh")
                .log("CRON job for na-days-refresh started.")
                .setHeader("region", constant("NA"))
                .setHeader("sedaRoute", constant("seda:na-queue"))
                .to("direct:start-days-refresh");

        from("quartz:month-refresh/na?cron={{dataloader.month-refresh-schedule}}")
                .routeId("na-month-refresh")
                .log("CRON job for na-month-refresh started.")
                .setHeader("region", constant("NA"))
                .setHeader("sedaRoute", constant("seda:na-queue"))
                .setHeader("shouldContinueMonthRefresh", simple("${true}"))
                .to("bean:queryUtil?method=oneMonthAgo")
                .setHeader("timestamp", simple("${body}"))
                .loopDoWhile(simple("${headers.shouldContinueMonthRefresh} == true"))
                .to("direct:continue-month-refresh")
                .end();

        from("quartz:yearly-refresh/na?cron={{dataloader.yearly-refresh-schedule}}")
                .routeId("na-yearly-refresh")
                .log("CRON job for na-yearly-refresh started.")
                .setHeader("region", constant("NA"))
                .setHeader("sedaRoute", constant("seda:na-queue"))
                .setHeader("shouldContinueYearRefresh", simple("${true}"))
                .to("bean:queryUtil?method=oneYearAgo")
                .setHeader("timestamp", simple("${body}"))
                .loopDoWhile(simple("${headers.shouldContinueYearRefresh} == true"))
                .to("direct:continue-year-refresh")
                .end();

        from("quartz:days-refresh/oce?cron={{dataloader.days-refresh-schedule}}")
                .routeId("oce-days-refresh")
                .log("CRON job for oce-days-refresh started.")
                .setHeader("region", constant("OCE"))
                .setHeader("sedaRoute", constant("seda:oce-queue"))
                .to("direct:start-days-refresh");

        from("quartz:month-refresh/oce?cron={{dataloader.month-refresh-schedule}}")
                .routeId("oce-month-refresh")
                .log("CRON job for oce-month-refresh started.")
                .setHeader("region", constant("OCE"))
                .setHeader("sedaRoute", constant("seda:oce-queue"))
                .setHeader("shouldContinueMonthRefresh", simple("${true}"))
                .to("bean:queryUtil?method=oneMonthAgo")
                .setHeader("timestamp", simple("${body}"))
                .loopDoWhile(simple("${headers.shouldContinueMonthRefresh} == true"))
                .to("direct:continue-month-refresh")
                .end();

        from("quartz:yearly-refresh/oce?cron={{dataloader.yearly-refresh-schedule}}")
                .routeId("oce-yearly-refresh")
                .log("CRON job for oce-yearly-refresh started.")
                .setHeader("region", constant("OCE"))
                .setHeader("sedaRoute", constant("seda:oce-queue"))
                .setHeader("shouldContinueYearRefresh", simple("${true}"))
                .to("bean:queryUtil?method=oneYearAgo")
                .setHeader("timestamp", simple("${body}"))
                .loopDoWhile(simple("${headers.shouldContinueYearRefresh} == true"))
                .to("direct:continue-year-refresh")
                .end();

        from("quartz:days-refresh/br?cron={{dataloader.days-refresh-schedule}}")
                .routeId("br-days-refresh")
                .log("CRON job for br-days-refresh started.")
                .setHeader("region", constant("BR"))
                .setHeader("sedaRoute", constant("seda:br-queue"))
                .to("direct:start-days-refresh");

        from("quartz:month-refresh/br?cron={{dataloader.month-refresh-schedule}}")
                .routeId("br-month-refresh")
                .log("CRON job for br-month-refresh started.")
                .setHeader("region", constant("BR"))
                .setHeader("sedaRoute", constant("seda:br-queue"))
                .setHeader("shouldContinueMonthRefresh", simple("${true}"))
                .to("bean:queryUtil?method=oneMonthAgo")
                .setHeader("timestamp", simple("${body}"))
                .loopDoWhile(simple("${headers.shouldContinueMonthRefresh} == true"))
                .to("direct:continue-month-refresh")
                .end();

        from("quartz:yearly-refresh/br?cron={{dataloader.yearly-refresh-schedule}}")
                .routeId("br-yearly-refresh")
                .log("CRON job for br-yearly-refresh started.")
                .setHeader("region", constant("BR"))
                .setHeader("sedaRoute", constant("seda:br-queue"))
                .setHeader("shouldContinueYearRefresh", simple("${true}"))
                .to("bean:queryUtil?method=oneYearAgo")
                .setHeader("timestamp", simple("${body}"))
                .loopDoWhile(simple("${headers.shouldContinueYearRefresh} == true"))
                .to("direct:continue-year-refresh")
                .end();

        from("quartz:days-refresh/eune?cron={{dataloader.days-refresh-schedule}}")
                .routeId("eune-days-refresh")
                .log("CRON job for eune-days-refresh started.")
                .setHeader("region", constant("EUNE"))
                .setHeader("sedaRoute", constant("seda:eune-queue"))
                .to("direct:start-days-refresh");

        from("quartz:month-refresh/eune?cron={{dataloader.month-refresh-schedule}}")
                .routeId("eune-month-refresh")
                .log("CRON job for eune-month-refresh started.")
                .setHeader("region", constant("EUNE"))
                .setHeader("sedaRoute", constant("seda:eune-queue"))
                .setHeader("shouldContinueMonthRefresh", simple("${true}"))
                .to("bean:queryUtil?method=oneMonthAgo")
                .setHeader("timestamp", simple("${body}"))
                .loopDoWhile(simple("${headers.shouldContinueMonthRefresh} == true"))
                .to("direct:continue-month-refresh")
                .end();

        from("quartz:yearly-refresh/eune?cron={{dataloader.yearly-refresh-schedule}}")
                .routeId("eune-yearly-refresh")
                .log("CRON job for eune-yearly-refresh started.")
                .setHeader("region", constant("EUNE"))
                .setHeader("sedaRoute", constant("seda:eune-queue"))
                .setHeader("shouldContinueYearRefresh", simple("${true}"))
                .to("bean:queryUtil?method=oneYearAgo")
                .setHeader("timestamp", simple("${body}"))
                .loopDoWhile(simple("${headers.shouldContinueYearRefresh} == true"))
                .to("direct:continue-year-refresh")
                .end();

        from("quartz:days-refresh/euw?cron={{dataloader.days-refresh-schedule}}")
                .routeId("euw-days-refresh")
                .log("CRON job for euw-days-refresh started.")
                .setHeader("region", constant("EUW"))
                .setHeader("sedaRoute", constant("seda:euw-queue"))
                .to("direct:start-days-refresh");

        from("quartz:month-refresh/euw?cron={{dataloader.month-refresh-schedule}}")
                .routeId("euw-month-refresh")
                .log("CRON job for euw-month-refresh started.")
                .setHeader("region", constant("EUW"))
                .setHeader("sedaRoute", constant("seda:euw-queue"))
                .setHeader("shouldContinueMonthRefresh", simple("${true}"))
                .to("bean:queryUtil?method=oneMonthAgo")
                .setHeader("timestamp", simple("${body}"))
                .loopDoWhile(simple("${headers.shouldContinueMonthRefresh} == true"))
                .to("direct:continue-month-refresh")
                .end();

        from("quartz:yearly-refresh/euw?cron={{dataloader.yearly-refresh-schedule}}")
                .routeId("euw-yearly-refresh")
                .log("CRON job for euw-yearly-refresh started.")
                .setHeader("region", constant("EUW"))
                .setHeader("sedaRoute", constant("seda:euw-queue"))
                .setHeader("shouldContinueYearRefresh", simple("${true}"))
                .to("bean:queryUtil?method=oneYearAgo")
                .setHeader("timestamp", simple("${body}"))
                .loopDoWhile(simple("${headers.shouldContinueYearRefresh} == true"))
                .to("direct:continue-year-refresh")
                .end();

        from("quartz:days-refresh/las?cron={{dataloader.days-refresh-schedule}}")
                .routeId("las-days-refresh")
                .log("CRON job for las-days-refresh started.")
                .setHeader("region", constant("LAS"))
                .setHeader("sedaRoute", constant("seda:las-queue"))
                .to("direct:start-days-refresh");

        from("quartz:month-refresh/las?cron={{dataloader.month-refresh-schedule}}")
                .routeId("las-month-refresh")
                .log("CRON job for las-month-refresh started.")
                .setHeader("region", constant("LAS"))
                .setHeader("sedaRoute", constant("seda:las-queue"))
                .setHeader("shouldContinueMonthRefresh", simple("${true}"))
                .to("bean:queryUtil?method=oneMonthAgo")
                .setHeader("timestamp", simple("${body}"))
                .loopDoWhile(simple("${headers.shouldContinueMonthRefresh} == true"))
                .to("direct:continue-month-refresh")
                .end();

        from("quartz:yearly-refresh/las?cron={{dataloader.yearly-refresh-schedule}}")
                .routeId("las-yearly-refresh")
                .log("CRON job for las-yearly-refresh started.")
                .setHeader("region", constant("LAS"))
                .setHeader("sedaRoute", constant("seda:las-queue"))
                .setHeader("shouldContinueYearRefresh", simple("${true}"))
                .to("bean:queryUtil?method=oneYearAgo")
                .setHeader("timestamp", simple("${body}"))
                .loopDoWhile(simple("${headers.shouldContinueYearRefresh} == true"))
                .to("direct:continue-year-refresh")
                .end();

        from("seda:na-queue?concurrentConsumers={{dataloader.concurrent-consumers}}")
                .routeId("na-queue")
                .throttle(throttle)
                .setHeader("region", simple("NA"))
                .to("direct:update-summoner");

        from("seda:oce-queue?concurrentConsumers={{dataloader.concurrent-consumers}}")
                .routeId("oce-queue")
                .throttle(throttle)
                .setHeader("region", simple("OCE"))
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

        from("seda:las-queue?concurrentConsumers={{dataloader.concurrent-consumers}}")
                .routeId("las-queue")
                .throttle(throttle)
                .setHeader("region", simple("LAS"))
                .to("direct:update-summoner");

        from("direct:start-days-refresh")
                .routeId("start-days-refresh")
                .to("bean:queryUtil?method=threeDaysFromNow")
                .setHeader("t2", simple("${body}"))
                .to("bean:queryUtil?method=threeDaysAgo")
                .setHeader("t1", simple("${body}"))
                .to("direct:query-between")
                .to("bean:recordMapper?method=toSummonerResponseDTOs")
                .split().body()
                    .setHeader("isDynamoSummonerName", simple("true"))
                    .setHeader("name", simple("${body.name}"))
                    .toD("${headers.sedaRoute}?blockWhenFull=true");

        from("direct:continue-month-refresh")
                .routeId("continue-month-refresh")
                .setHeader("backwards", simple("false"))
                .to("direct:query-range")
                .to("bean:recordMapper?method=toSummonerResponseDTOs")
                .to("bean:recordMapper?method=toSummonersResponseDTO")
                .setHeader("ySummoners", simple("${body}"))
                .to("bean:queryUtil?method=shouldContinueMonthRefresh")
                .setHeader("shouldContinueMonthRefresh", simple("${body}"))
                .setHeader("timestamp", simple("${headers.ySummoners.forwards}"))
                .split().simple("${headers.ySummoners.summoners}")
                    .setHeader("isDynamoSummonerName", simple("true"))
                    .setHeader("name", simple("${body.name}"))
                    .toD("${headers.sedaRoute}?blockWhenFull=true");

        from("direct:continue-year-refresh")
                .routeId("continue-year-refresh")
                .setHeader("backwards", simple("false"))
                .to("direct:query-range")
                .to("bean:recordMapper?method=toSummonerResponseDTOs")
                .to("bean:recordMapper?method=toSummonersResponseDTO")
                .setHeader("ySummoners", simple("${body}"))
                .to("bean:queryUtil?method=shouldContinueYearRefresh")
                .setHeader("shouldContinueYearRefresh", simple("${body}"))
                .setHeader("timestamp", simple("${headers.ySummoners.forwards}"))
                .split().simple("${headers.ySummoners.summoners}")
                    .setHeader("isDynamoSummonerName", simple("true"))
                    .setHeader("name", simple("${body.name}"))
                    .toD("${headers.sedaRoute}?blockWhenFull=true");
    }
}
