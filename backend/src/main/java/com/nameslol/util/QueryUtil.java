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
    Long SECOND = 1000L;
    Long MINUTE = SECOND * 60;
    Long HOUR = MINUTE * 60;
    Long DAY = HOUR * 24;
    Long MONTH = DAY * 30;
    Long YEAR = MONTH * 12;

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

    public long threeDaysFromNow() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 3);
        return cal.toInstant().toEpochMilli();
    }

    public long threeDaysAgo() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -3);
        return cal.toInstant().toEpochMilli();
    }

    public long oneMonthAgo() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        return cal.toInstant().toEpochMilli();
    }

    public long oneYearAgo() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);
        return cal.toInstant().toEpochMilli();
    }

    public boolean summonerNameIsDifferent(String dynamoName, String riotName) {
        if (dynamoName == null || riotName == null) return true;
        if (dynamoName.isBlank() || riotName.isBlank()) return true;
        return !dynamoName.equalsIgnoreCase(riotName.trim());
    }

    public boolean shouldContinueMonthRefresh(SummonersResponseDTO response) {
        return response.getSummoners() != null &&
                response.getSummoners().size() > 1 &&
                response.getForwards() < (System.currentTimeMillis() + MONTH);
    }

    public boolean shouldContinueYearRefresh(SummonersResponseDTO response) {
        return response.getSummoners() != null &&
                response.getSummoners().size() > 1 &&
                response.getForwards() < (System.currentTimeMillis() + YEAR);
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
