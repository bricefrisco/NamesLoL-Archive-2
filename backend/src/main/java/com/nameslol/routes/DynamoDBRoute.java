package com.nameslol.routes;

import com.nameslol.util.QueryUtil;
import com.nameslol.util.RecordMapper;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.apache.camel.builder.RouteBuilder;

@RegisterForReflection
public class DynamoDBRoute extends RouteBuilder {

    @Override
    public void configure() {
        errorHandler(noErrorHandler());

        from("direct:update-summoner")
                .routeId("update-summoner")
                .end()
                .to("bean:riotAPI?method=fetchSummonerName(${headers.name}, ${headers.region})")
                .setHeader("summoner", simple("${body}"))
                .to("bean:recordMapper?method=toAttributeValues(${body}, ${headers.region})")
                .setHeader("CamelAwsDdbItem", simple("${body}"))
                .to("bean:recordMapper?method=toSummonerResponseDTO(${headers.summoner}, ${headers.region})")
                .choice().when(simple("${headers.hideSearch} != true"))
                    .log("Updating summoner: ${headers.name} (${headers.region})")
                    .wireTap("aws2-ddb://lol-summoners-test" +
                        "?operation=PutItem" +
                        "&accessKey=RAW({{aws.access-key}})" +
                        "&secretKey=RAW({{aws.secret-key}})" +
                        "&region={{aws.region}}")
                    .choice().when(simple("${headers.isDynamoSummonerName} != null"))
                        .to("bean:queryUtil?method=summonerNameIsDifferent(${headers.name}, ${headers.summoner.name})")
                        .choice().when(simple("${body} == true")).wireTap("direct:delete-summoner").end()
                    .end()
                .end();


        from("direct:delete-summoner")
                .routeId("delete-summoner")
                .end()
                .to("bean:recordMapper?method=toAttributeMap(${headers.name}, ${headers.region})")
                .setHeader("CamelAwsDdbKey", simple("${body}"))
                .to("aws2-ddb://lol-summoners-test" +
                        "?operation=DeleteItem" +
                        "&accessKey=RAW({{aws.access-key}})" +
                        "&secretKey=RAW({{aws.secret-key}})" +
                        "&region={{aws.region}}")
                .log("Deleted summoner: ${headers.name} (${headers.region})");


        from("direct:query-by-name")
                .routeId("query-by-name")
                .to("bean:queryUtil?method=byName(${headers.name}, ${headers.region})")
                .setHeader("CamelAwsDdbKeyConditions", simple("${body}"))
                .to("direct:query");

        from("direct:query-by-name-size")
                .routeId("query-by-name-size")
                .to("bean:queryUtil?method=byNameSize(${headers.region}, ${headers.timestamp}, ${headers.backwards}, ${headers.nameLength})")
                .setHeader("CamelAwsDdbKeyConditions", simple("${body}"))
                .setHeader("CamelAwsDdbLimit", simple("{{aws.dynamodb.limit}}"))
                .setHeader("CamelAwsDdbIndexName", simple("name-length-availability-date-index"))
                .choice().when(simple("${headers.backwards} == true"))
                    .setHeader("CamelAwsDdbScanIndexForward", simple("false"))
                .otherwise()
                    .setHeader("CamelAwsDdbScanIndexForward", simple("true"))
                .end()
                .to("direct:query");

        from("direct:query-between")
                .routeId("query-between")
                .to("bean:queryUtil?method=between(${headers.region}, ${headers.t1}, ${headers.t2})")
                .setHeader("CamelAwsDdbKeyConditions", simple("${body}"))
                .setHeader("CamelAwsDdbLimit", simple("{{dataloader.query-range-limit}}"))
                .setHeader("CamelAwsDdbIndexName", simple("region-activation-date-index"))
                .to("direct:query");

        from("direct:query-range")
                .routeId("query-range")
                .to("bean:queryUtil?method=range(${headers.region}, ${headers.timestamp}, ${headers.backwards})")
                .setHeader("CamelAwsDdbKeyConditions", simple("${body}"))
                .setHeader("CamelAwsDdbLimit", simple("{{aws.dynamodb.limit}}"))
                .setHeader("CamelAwsDdbIndexName", simple("region-activation-date-index"))
                .choice().when(simple("${headers.backwards} == true"))
                    .setHeader("CamelAwsDdbScanIndexForward", simple("false"))
                .otherwise()
                    .setHeader("CamelAwsDdbScanIndexForward", simple("true"))
                .end()
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
