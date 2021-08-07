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
        from("direct:update-summoner")
                .routeId("update-summoner")
                .onException(Exception.class)
                    .handled(Boolean.TRUE)
                    .log(LoggingLevel.ERROR, "${exception.message}")
                    .log(LoggingLevel.ERROR, "${exception.stacktrace}")
                .end()
                .setHeader("name", simple("${body}"))
                .to("bean:riotAPI?method=fetchSummonerName(${headers.name}, ${headers.region})")
                .setHeader("summoner", simple("${body}"))
                .bean(RecordMapper.class, "toAttributeValues(${body}, ${headers.region})")
                .setHeader("CamelAwsDdbItem", simple("${body}"))
                .to("aws2-ddb://lol-summoners-test" +
                        "?operation=PutItem" +
                        "&accessKey=RAW({{aws.access-key}})" +
                        "&secretKey=RAW({{aws.secret-key}})" +
                        "&region={{aws.region}}")
                .log("Inserted summoner: ${headers.name} (${headers.region})")
                .setBody(simple("${headers.summoner}"));

        from("direct:query-by-name")
                .routeId("query-by-name")
                .bean(QueryUtil.class, "byName(${body}, ${headers.region})")
                .setHeader("CamelAwsDdbKeyConditions", simple("${body}"))
                .to("direct:query");

        from("direct:query-by-name-size")
                .routeId("query-by-name-size")
                .bean(QueryUtil.class, "byNameSize(${headers.region}, ${headers.timestamp}, ${headers.backwards}, ${body})")
                .setHeader("CamelAwsDdbKeyConditions", simple("${body}"))
                .to("direct:query");

        from("direct:query-between")
                .routeId("query-between")
                .bean(QueryUtil.class, "between(${body}, ${headers.t1}, ${headers.t2})")
                .setHeader("CamelAwsDdbKeyConditions", simple("${body}"))
                .to("direct:query");

        from("direct:query-range")
                .routeId("query-range")
                .bean(QueryUtil.class, "range(${body}, ${headers.timestamp}, ${headers.backwards}")
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
