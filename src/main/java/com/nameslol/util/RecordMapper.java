package com.nameslol.util;

import com.nameslol.models.Region;
import com.nameslol.models.SummonerRecordDTO;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public final class RecordMapper {
    public static long toAvailabilityDate(long revisionDate, int level) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(revisionDate);
        if (level <= 6) {
            calendar.add(Calendar.MONTH, 6);
        } else {
            calendar.add(Calendar.MONTH, Math.min(level, 30));
        }

        return calendar.toInstant().toEpochMilli();
    }

    public static Map<String, AttributeValue> toAttributeValues(SummonerRecordDTO dto, Region region) {
        Map<String, AttributeValue> result = new HashMap<>();
        result.put("ad", toAttributeNumber(toAvailabilityDate(dto.getRevisionDate(), dto.getSummonerLevel())));
        result.put("n", toAttributeString(region.name() + "#" + dto.getName().trim().toUpperCase()));
        result.put("r", toAttributeString(region.name()));
        result.put("aid", toAttributeString(dto.getAccountId()));
        result.put("rd", toAttributeNumber(dto.getRevisionDate()));
        result.put("l", toAttributeNumber(dto.getSummonerLevel()));
        result.put("nl", toAttributeString(region.name() + "#" + dto.getName().trim().length()));
        result.put("ld", toAttributeNumber(System.currentTimeMillis()));
        result.put("pid", toAttributeNumber(dto.getProfileIconId()));
        return result;
    }

    public static AttributeValue toAttributeString(String s) {
        return AttributeValue.builder().s(s).build();
    }

    public static AttributeValue toAttributeNumber(long n) {
        return AttributeValue.builder().n(String.valueOf(n)).build();
    }
}
