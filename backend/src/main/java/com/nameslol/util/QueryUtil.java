package com.nameslol.util;

import com.nameslol.models.Region;
import com.nameslol.models.SummonersResponseDTO;
import io.quarkus.runtime.annotations.RegisterForReflection;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ComparisonOperator;
import software.amazon.awssdk.services.dynamodb.model.Condition;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
@Named("queryUtil")
@RegisterForReflection
public class QueryUtil {
    public Map<String, Condition> byName(String name, String region) {
        Map<String, Condition> query = new HashMap<>();
        AttributeValue nameAttr = RecordMapper.toAttributeString(toRegion(region).name() + "#" + name.trim().toUpperCase());
        Condition cond = Condition.builder().attributeValueList(nameAttr).comparisonOperator(ComparisonOperator.EQ).build();
        query.put("n", cond);
        return query;
    }

    public Map<String, Condition> between(String region, long t1, long t2) {
        Map<String, Condition> query = new HashMap<>();
        AttributeValue r = RecordMapper.toAttributeString(toRegion(region).name());
        AttributeValue t1v = RecordMapper.toAttributeNumber(t1);
        AttributeValue t2v = RecordMapper.toAttributeNumber(t2);
        Condition cond1 = Condition.builder().attributeValueList(r).comparisonOperator(ComparisonOperator.EQ).build();
        Condition cond2 = Condition.builder().attributeValueList(t1v, t2v).comparisonOperator(ComparisonOperator.BETWEEN).build();
        query.put("r", cond1);
        query.put("ad", cond2);
        return query;
    }

    public Map<String, Condition> range(String region, long timestamp, boolean backwards) {
        Map<String, Condition> query = new HashMap<>();
        AttributeValue r = RecordMapper.toAttributeString(toRegion(region).name());
        AttributeValue t = RecordMapper.toAttributeNumber(timestamp);
        Condition cond1 = Condition.builder().attributeValueList(r).comparisonOperator(ComparisonOperator.EQ).build();
        Condition cond2 = Condition.builder().attributeValueList(t).comparisonOperator(backwards ? ComparisonOperator.LE : ComparisonOperator.GE).build();
        query.put("r", cond1);
        query.put("ad", cond2);
        return query;
    }

    public Map<String, Condition> byNameSize(String region, long timestamp, boolean backwards, int nameSize) {
        Map<String, Condition> query = new HashMap<>();
        AttributeValue nl = RecordMapper.toAttributeString(toRegion(region).name() + "#" + nameSize);
        AttributeValue t = RecordMapper.toAttributeNumber(timestamp);
        Condition cond1 = Condition.builder().attributeValueList(nl).comparisonOperator(ComparisonOperator.EQ).build();
        Condition cond2 = Condition.builder().attributeValueList(t).comparisonOperator(backwards ? ComparisonOperator.LE : ComparisonOperator.GE).build();
        query.put("nl", cond1);
        query.put("ad", cond2);
        return query;
    }

    public long lastWeekInMs() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 7);
        return cal.toInstant().toEpochMilli();
    }

    public long nextWeekInMs() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        return cal.toInstant().toEpochMilli();
    }

    public long lastYearInMs() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);
        return cal.toInstant().toEpochMilli();
    }

    public boolean summonerNameIsDifferent(String dynamoName, String riotName) {
        if (dynamoName == null || riotName == null) return true;
        if (dynamoName.isBlank() || riotName.isBlank()) return true;
        return !dynamoName.equalsIgnoreCase(riotName.trim());
    }

    public boolean shouldContinueQueryingLastYear(SummonersResponseDTO response) {
        return response.getSummoners() != null &&
                response.getSummoners().size() > 1 &&
                response.getForwards() < System.currentTimeMillis();
    }

    public boolean shouldContinueQueryingAll(SummonersResponseDTO response) {
        return response.getSummoners() != null && response.getSummoners().size() > 1;
    }

    private Region toRegion(String r) {
        if (r == null || r.isBlank()) throw new IllegalArgumentException("'" + r + "' is in invalid region.");
        try {
            return Region.valueOf(r.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("'" + r + "' is an invalid region.");
        }
    }
}
