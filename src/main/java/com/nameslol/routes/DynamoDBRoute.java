package com.nameslol.routes;

import com.nameslol.util.QueryUtil;
import com.nameslol.util.RecordMapper;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

@RegisterForReflection
public class DynamoDBRoute extends RouteBuilder {

    @Override
    public void configure() {
        errorHandler(noErrorHandler());

        from("direct:update-summoner")
                .routeId("update-summoner")
                .onException(Exception.class)
                    .handled(Boolean.TRUE)
                    .log(LoggingLevel.WARN, "${exception.message}")
                .end()
                .to("bean:riotAPI?method=fetchSummonerName(${headers.name}, ${headers.region})")
                .setHeader("summoner", simple("${body}"))
                .bean(RecordMapper.class, "toAttributeValues(${body}, ${headers.region})")
                .setHeader("CamelAwsDdbItem", simple("${body}"))
                .bean(RecordMapper.class, "toSummonerResponseDTO(${headers.summoner}, ${headers.region})")
                .log("Updating summoner: ${headers.name} (${headers.region})")
                .wireTap("aws2-ddb://lol-summoners-test" +
                        "?operation=PutItem" +
                        "&accessKey=RAW({{aws.access-key}})" +
                        "&secretKey=RAW({{aws.secret-key}})" +
                        "&region={{aws.region}}")
                .choice().when(simple("${headers.isDynamoSummonerName} != null"))
                    .bean(QueryUtil.class, "summonerNameIsDifferent(${headers.name}, ${headers.summoner.name})")
                    .choice().when(simple("${body} == true")).wireTap("direct:delete-summoner").end()
                .end();


        from("direct:delete-summoner")
                .routeId("delete-summoner")
                .onException(Exception.class)
                    .handled(Boolean.TRUE)
                    .log(LoggingLevel.WARN, "${exception.message}")
                .end()
                .bean(RecordMapper.class, "toAttributeMap(${headers.name}, ${headers.region})")
                .setHeader("CamelAwsDdbKey", simple("${body}"))
                .to("aws2-ddb://lol-summoners-test" +
                        "?operation=DeleteItem" +
                        "&accessKey=RAW({{aws.access-key}})" +
                        "&secretKey=RAW({{aws.secret-key}})" +
                        "&region={{aws.region}}")
                .log("Deleted summoner: ${headers.name} (${headers.region})");


        from("direct:query-by-name")
                .routeId("query-by-name")
                .bean(QueryUtil.class, "byName(${headers.name}, ${headers.region})")
                .setHeader("CamelAwsDdbKeyConditions", simple("${body}"))
                .to("direct:query");

        from("direct:query-by-name-size")
                .routeId("query-by-name-size")
                .bean(QueryUtil.class, "byNameSize(${headers.region}, ${headers.timestamp}, ${headers.backwards}, ${headers.nameLength})")
                .setHeader("CamelAwsDdbKeyConditions", simple("${body}"))
                .setHeader("CamelAwsDdbLimit", simple("{{aws.dynamodb.limit}}"))
                .setHeader("CamelAwsDdbIndexName", simple("name-length-availability-date-index"))
                .setHeader("CamelAwsDdbScanIndexForward", simple("!${headers.backwards}"))
                .to("direct:query");

        from("direct:query-between")
                .routeId("query-between")
                .bean(QueryUtil.class, "between(${headers.region}, ${headers.t1}, ${headers.t2})")
                .setHeader("CamelAwsDdbKeyConditions", simple("${body}"))
                .setHeader("CamelAwsDdbLimit", simple("{{dataloader.query-range-limit}}"))
                .setHeader("CamelAwsDdbIndexName", simple("region-activation-date-index"))
                .to("direct:query");

        from("direct:query-range")
                .routeId("query-range")
                .bean(QueryUtil.class, "range(${headers.region}, ${headers.timestamp}, ${headers.backwards})")
                .setHeader("CamelAwsDdbKeyConditions", simple("${body}"))
                .setHeader("CamelAwsDdbLimit", simple("{{aws.dynamodb.limit}}"))
                .setHeader("CamelAwsDdbIndexName", simple("region-activation-date-index"))
                .setHeader("CamelAwsDdbScanIndexForward", simple("!${headers.backwards}"))
                .to("direct:query");

        from("direct:query")
                .routeId("query")
                .to("aws2-ddb://lol-summoners-test" +
                        "?operation=Query" +
                        "&accessKey=RAW({{aws.access-key}})" +
                        "&secretKey=RAW({{aws.secret-key}})" +
                        "&region={{aws.region}}")
                .setBody(simple("${headers.CamelAwsDdbItems}"));
    }
}
