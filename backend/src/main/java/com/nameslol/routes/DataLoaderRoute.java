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
                    .filter().method("requestValidator", "isValid")
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

        from("quartz:year-refresh/na?cron={{dataloader.yearly-refresh-schedule}}")
                .routeId("na-year-refresh")
                .log("CRON job for na-year-refresh started.")
                .setHeader("region", constant("NA"))
                .setHeader("sedaRoute", constant("seda:na-queue"))
                .setHeader("shouldContinueQueryingLastYear", simple("${true}"))
                .to("bean:queryUtil?method=lastYearInMs")
                .setHeader("timestamp", simple("${body}"))
                .loopDoWhile(simple("${headers.shouldContinueQueryingLastYear} == true"))
                .to("direct:continue-year-refresh")
                .end();

        from("quartz:complete-refresh/na?cron={{dataloader.complete-refresh-schedule}}")
                .routeId("na-complete-refresh")
                .log("CRON job for na-complete-refresh started.")
                .setHeader("region", constant("NA"))
                .setHeader("sedaRoute", constant("seda:na-queue"))
                .setHeader("shouldContinueQueryingAll", simple("${true}"))
                .setHeader("timestamp", simple("1"))
                .loopDoWhile(simple("${headers.shouldContinueQueryingAll} == true"))
                .to("direct:continue-complete-refresh")
                .end();

        from("quartz:week-refresh/br?cron={{dataloader.weekly-refresh-schedule}}")
                .routeId("br-week-refresh")
                .log("CRON job for br-week-refresh started.")
                .setHeader("region", constant("BR"))
                .setHeader("sedaRoute", constant("seda:br-queue"))
                .to("direct:start-week-refresh");

        from("quartz:year-refresh/br?cron={{dataloader.yearly-refresh-schedule}}")
                .routeId("br-year-refresh")
                .log("CRON job for br-year-refresh started.")
                .setHeader("region", constant("BR"))
                .setHeader("sedaRoute", constant("seda:br-queue"))
                .setHeader("shouldContinueQueryingLastYear", simple("${true}"))
                .to("bean:queryUtil?method=lastYearInMs")
                .setHeader("timestamp", simple("${body}"))
                .loopDoWhile(simple("${headers.shouldContinueQueryingLastYear} == true"))
                .to("direct:continue-year-refresh")
                .end();

        from("quartz:complete-refresh/br?cron={{dataloader.complete-refresh-schedule}}")
                .routeId("br-complete-refresh")
                .log("CRON job for br-complete-refresh started.")
                .setHeader("region", constant("BR"))
                .setHeader("sedaRoute", constant("seda:br-queue"))
                .setHeader("shouldContinueQueryingAll", simple("${true}"))
                .setHeader("timestamp", simple("1"))
                .loopDoWhile(simple("${headers.shouldContinueQueryingAll} == true"))
                .to("direct:continue-complete-refresh")
                .end();

        from("quartz:week-refresh/eune?cron={{dataloader.weekly-refresh-schedule}}")
                .routeId("eune-week-refresh")
                .log("CRON job for eune-week-refresh started.")
                .setHeader("region", constant("EUNE"))
                .setHeader("sedaRoute", constant("seda:eune-queue"))
                .to("direct:start-week-refresh");

        from("quartz:year-refresh/eune?cron={{dataloader.yearly-refresh-schedule}}")
                .routeId("eune-year-refresh")
                .log("CRON job for eune-year-refresh started.")
                .setHeader("region", constant("EUNE"))
                .setHeader("sedaRoute", constant("seda:eune-queue"))
                .setHeader("shouldContinueQueryingLastYear", simple("${true}"))
                .to("bean:queryUtil?method=lastYearInMs")
                .setHeader("timestamp", simple("${body}"))
                .loopDoWhile(simple("${headers.shouldContinueQueryingLastYear} == true"))
                .to("direct:continue-year-refresh")
                .end();

        from("quartz:complete-refresh/eune?cron={{dataloader.complete-refresh-schedule}}")
                .routeId("eune-complete-refresh")
                .log("CRON job for eune-complete-refresh started.")
                .setHeader("region", constant("EUNE"))
                .setHeader("sedaRoute", constant("seda:eune-queue"))
                .setHeader("shouldContinueQueryingAll", simple("${true}"))
                .setHeader("timestamp", simple("1"))
                .loopDoWhile(simple("${headers.shouldContinueQueryingAll} == true"))
                .to("direct:continue-complete-refresh")
                .end();

        from("quartz:week-refresh/euw?cron={{dataloader.weekly-refresh-schedule}}")
                .routeId("euw-week-refresh")
                .log("CRON job for euw-week-refresh started.")
                .setHeader("region", constant("EUW"))
                .setHeader("sedaRoute", constant("seda:euw-queue"))
                .to("direct:start-week-refresh");

        from("quartz:year-refresh/euw?cron={{dataloader.yearly-refresh-schedule}}")
                .routeId("euw-year-refresh")
                .log("CRON job for euw-year-refresh started.")
                .setHeader("region", constant("EUW"))
                .setHeader("sedaRoute", constant("seda:euw-queue"))
                .setHeader("shouldContinueQueryingLastYear", simple("${true}"))
                .to("bean:queryUtil?method=lastYearInMs")
                .setHeader("timestamp", simple("${body}"))
                .loopDoWhile(simple("${headers.shouldContinueQueryingLastYear} == true"))
                .to("direct:continue-year-refresh")
                .end();

        from("quartz:complete-refresh/euw?cron={{dataloader.complete-refresh-schedule}}")
                .routeId("euw-complete-refresh")
                .log("CRON job for euw-complete-refresh started.")
                .setHeader("region", constant("EUW"))
                .setHeader("sedaRoute", constant("seda:euw-queue"))
                .setHeader("shouldContinueQueryingAll", simple("${true}"))
                .setHeader("timestamp", simple("1"))
                .loopDoWhile(simple("${headers.shouldContinueQueryingAll} == true"))
                .to("direct:continue-complete-refresh")
                .end();

        from("quartz:week-refresh/kr?cron={{dataloader.weekly-refresh-schedule}}")
                .routeId("kr-week-refresh")
                .log("CRON job for kr-week-refresh started.")
                .setHeader("region", constant("KR"))
                .setHeader("sedaRoute", constant("seda:kr-queue"))
                .to("direct:start-week-refresh");

        from("quartz:year-refresh/kr?cron={{dataloader.yearly-refresh-schedule}}")
                .routeId("kr-year-refresh")
                .log("CRON job for kr-year-refresh started.")
                .setHeader("region", constant("KR"))
                .setHeader("sedaRoute", constant("seda:kr-queue"))
                .setHeader("shouldContinueQueryingLastYear", simple("${true}"))
                .to("bean:queryUtil?method=lastYearInMs")
                .setHeader("timestamp", simple("${body}"))
                .loopDoWhile(simple("${headers.shouldContinueQueryingLastYear} == true"))
                .to("direct:continue-year-refresh")
                .end();

        from("quartz:complete-refresh/kr?cron={{dataloader.complete-refresh-schedule}}")
                .routeId("kr-complete-refresh")
                .log("CRON job for kr-complete-refresh started.")
                .setHeader("region", constant("KR"))
                .setHeader("sedaRoute", constant("seda:kr-queue"))
                .setHeader("shouldContinueQueryingAll", simple("${true}"))
                .setHeader("timestamp", simple("1"))
                .loopDoWhile(simple("${headers.shouldContinueQueryingAll} == true"))
                .to("direct:continue-complete-refresh")
                .end();

        from("quartz:week-refresh/lan?cron={{dataloader.weekly-refresh-schedule}}")
                .routeId("lan-week-refresh")
                .log("CRON job for lan-week-refresh started.")
                .setHeader("region", constant("LAN"))
                .setHeader("sedaRoute", constant("seda:lan-queue"))
                .to("direct:start-week-refresh");

        from("quartz:year-refresh/lan?cron={{dataloader.yearly-refresh-schedule}}")
                .routeId("lan-year-refresh")
                .log("CRON job for lan-year-refresh started.")
                .setHeader("region", constant("LAN"))
                .setHeader("sedaRoute", constant("seda:lan-queue"))
                .setHeader("shouldContinueQueryingLastYear", simple("${true}"))
                .to("bean:queryUtil?method=lastYearInMs")
                .setHeader("timestamp", simple("${body}"))
                .loopDoWhile(simple("${headers.shouldContinueQueryingLastYear} == true"))
                .to("direct:continue-year-refresh")
                .end();

        from("quartz:complete-refresh/lan?cron={{dataloader.complete-refresh-schedule}}")
                .routeId("lan-complete-refresh")
                .log("CRON job for lan-complete-refresh started.")
                .setHeader("region", constant("LAN"))
                .setHeader("sedaRoute", constant("seda:lan-queue"))
                .setHeader("shouldContinueQueryingAll", simple("${true}"))
                .setHeader("timestamp", simple("1"))
                .loopDoWhile(simple("${headers.shouldContinueQueryingAll} == true"))
                .to("direct:continue-complete-refresh")
                .end();

        from("quartz:week-refresh/las?cron={{dataloader.weekly-refresh-schedule}}")
                .routeId("las-week-refresh")
                .log("CRON job for las-week-refresh started.")
                .setHeader("region", constant("LAS"))
                .setHeader("sedaRoute", constant("seda:las-queue"))
                .to("direct:start-week-refresh");

        from("quartz:year-refresh/las?cron={{dataloader.yearly-refresh-schedule}}")
                .routeId("las-year-refresh")
                .log("CRON job for las-year-refresh started.")
                .setHeader("region", constant("LAS"))
                .setHeader("sedaRoute", constant("seda:las-queue"))
                .setHeader("shouldContinueQueryingLastYear", simple("${true}"))
                .to("bean:queryUtil?method=lastYearInMs")
                .setHeader("timestamp", simple("${body}"))
                .loopDoWhile(simple("${headers.shouldContinueQueryingLastYear} == true"))
                .to("direct:continue-year-refresh")
                .end();

        from("quartz:complete-refresh/las?cron={{dataloader.complete-refresh-schedule}}")
                .routeId("las-complete-refresh")
                .log("CRON job for las-complete-refresh started.")
                .setHeader("region", constant("LAS"))
                .setHeader("sedaRoute", constant("seda:las-queue"))
                .setHeader("shouldContinueQueryingAll", simple("${true}"))
                .setHeader("timestamp", simple("1"))
                .loopDoWhile(simple("${headers.shouldContinueQueryingAll} == true"))
                .to("direct:continue-complete-refresh")
                .end();

        from("quartz:week-refresh/tr?cron={{dataloader.weekly-refresh-schedule}}")
                .routeId("tr-week-refresh")
                .log("CRON job for tr-week-refresh started.")
                .setHeader("region", constant("TR"))
                .setHeader("sedaRoute", constant("seda:tr-queue"))
                .to("direct:start-week-refresh");

        from("quartz:year-refresh/tr?cron={{dataloader.yearly-refresh-schedule}}")
                .routeId("tr-year-refresh")
                .log("CRON job for tr-year-refresh started.")
                .setHeader("region", constant("TR"))
                .setHeader("sedaRoute", constant("seda:tr-queue"))
                .setHeader("shouldContinueQueryingLastYear", simple("${true}"))
                .to("bean:queryUtil?method=lastYearInMs")
                .setHeader("timestamp", simple("${body}"))
                .loopDoWhile(simple("${headers.shouldContinueQueryingLastYear} == true"))
                .to("direct:continue-year-refresh")
                .end();

        from("quartz:complete-refresh/tr?cron={{dataloader.complete-refresh-schedule}}")
                .routeId("tr-complete-refresh")
                .log("CRON job for tr-complete-refresh started.")
                .setHeader("region", constant("TR"))
                .setHeader("sedaRoute", constant("seda:tr-queue"))
                .setHeader("shouldContinueQueryingAll", simple("${true}"))
                .setHeader("timestamp", simple("1"))
                .loopDoWhile(simple("${headers.shouldContinueQueryingAll} == true"))
                .to("direct:continue-complete-refresh")
                .end();

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
                .to("bean:queryUtil?method=lastWeekInMs")
                .setHeader("t2", simple("${body}"))
                .to("bean:queryUtil?method=nextWeekInMs")
                .setHeader("t1", simple("${body}"))
                .to("direct:query-between")
                .to("bean:recordMapper?method=toSummonerResponseDTOs")
                .split().body()
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
                .to("bean:queryUtil?method=shouldContinueQueryingLastYear")
                .setHeader("shouldContinueQueryingLastYear", simple("${body}"))
                .setHeader("timestamp", simple("${headers.ySummoners.forwards}"))
                .split().simple("${headers.ySummoners.summoners}")
                    .setHeader("isDynamoSummonerName", simple("true"))
                    .setHeader("name", simple("${body.name}"))
                    .toD("${headers.sedaRoute}?blockWhenFull=true");

        from("direct:continue-complete-refresh")
                .routeId("continue-complete-refresh")
                .setHeader("backwards", simple("false"))
                .to("direct:query-range")
                .to("bean:recordMapper?method=toSummonerResponseDTOs")
                .to("bean:recordMapper?method=toSummonersResponseDTO")
                .setHeader("ySummoners", simple("${body}"))
                .to("bean:queryUtil?method=shouldContinueQueryingAll")
                .setHeader("shouldContinueQueryingAll", simple("${body}"))
                .setHeader("timestamp", simple("${headers.ySummoners.forwards}"))
                .split().simple("${headers.ySummoners.summoners}")
                    .setHeader("isDynamoSummonerName", simple("true"))
                    .setHeader("name", simple("${body.name}"))
                    .toD("${headers.sedaRoute}?blockWhenFull=true");
    }
}
