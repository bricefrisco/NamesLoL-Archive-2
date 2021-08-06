package com.nameslol.routes;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

@RegisterForReflection
public class DynamoDBRoute extends RouteBuilder {

    @Override
    public void configure() {
        onException(Exception.class)
                .handled(Boolean.TRUE)
                .log(LoggingLevel.ERROR, "${exception.message}")
                .log(LoggingLevel.ERROR, "${exception.stacktrace}");

        from("direct:test")
                .to("aws-ddb://arn:aws:dynamodb:us-east-1:870654197380:table/lol-summoners-test" +
                        "?accessKey={{aws.access-key}}" +
                        "&secretKey={{aws.secret-key}}" +
                        "&region={{aws.region}}");
    }
}
