package com.nameslol.util;

import com.nameslol.models.Region;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ComparisonOperator;
import software.amazon.awssdk.services.dynamodb.model.Condition;
import java.util.HashMap;
import java.util.Map;

public class QueryUtil {
    public static Map<String, Condition> byName(String name, String region) {
        Map<String, Condition> query = new HashMap<>();
        AttributeValue nameAttr = RecordMapper.toAttributeString(Region.valueOf(region).name() + "#" + name.trim().toUpperCase());
        Condition cond = Condition.builder().attributeValueList(nameAttr).comparisonOperator(ComparisonOperator.EQ).build();
        query.put("n", cond);
        return query;
    }

    public static Map<String, Condition> between(String region, long t1, long t2) {
        Map<String, Condition> query = new HashMap<>();
        AttributeValue r = RecordMapper.toAttributeString(Region.valueOf(region).name());
        AttributeValue t1v = RecordMapper.toAttributeNumber(t1);
        AttributeValue t2v = RecordMapper.toAttributeNumber(t2);
        Condition cond1 = Condition.builder().attributeValueList(r).comparisonOperator(ComparisonOperator.EQ).build();
        Condition cond2 = Condition.builder().attributeValueList(t1v, t2v).comparisonOperator(ComparisonOperator.BETWEEN).build();
        query.put("r", cond1);
        query.put("ad", cond2);
        return query;
    }

    public static Map<String, Condition> range(String region, long timestamp, boolean backwards) {
        Map<String, Condition> query = new HashMap<>();
        AttributeValue r = RecordMapper.toAttributeString(Region.valueOf(region).name());
        AttributeValue t = RecordMapper.toAttributeNumber(timestamp);
        Condition cond1 = Condition.builder().attributeValueList(r).comparisonOperator(ComparisonOperator.EQ).build();
        Condition cond2 = Condition.builder().attributeValueList(t).comparisonOperator(backwards ? ComparisonOperator.LE : ComparisonOperator.GE).build();
        query.put("r", cond1);
        query.put("ad", cond2);
        return query;
    }

    public static Map<String, Condition> byNameSize(String region, long timestamp, boolean backwards, int nameSize) {
        Map<String, Condition> query = new HashMap<>();
        AttributeValue nl = RecordMapper.toAttributeString(Region.valueOf(region).name() + "#" + nameSize);
        AttributeValue t = RecordMapper.toAttributeNumber(timestamp);
        Condition cond1 = Condition.builder().attributeValueList(nl).comparisonOperator(ComparisonOperator.EQ).build();
        Condition cond2 = Condition.builder().attributeValueList(t).comparisonOperator(backwards ? ComparisonOperator.LE : ComparisonOperator.GE).build();
        query.put("nl", cond1);
        query.put("ad", cond2);
        return query;
    }


}
