package com.nameslol.util;

import com.nameslol.models.Region;
import com.nameslol.models.SummonerRecordDTO;
import com.nameslol.models.SummonerResponseDTO;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public static List<SummonerResponseDTO> toSummonerResponseDTOs(List<Map<String, AttributeValue>> req) {
        return req.stream().map(RecordMapper::toSummonerResponseDTO).collect(Collectors.toList());
    }

    public static SummonerResponseDTO toSummonerResponseDTO(Map<String, AttributeValue> req) {
        SummonerResponseDTO res = new SummonerResponseDTO();
        res.setRegion(req.get("r").s());
        res.setAccountId(req.get("aid").s());
        res.setRevisionDate(Long.parseLong(req.get("rd").n()));
        res.setLevel(Integer.parseInt(req.get("l").n()));
        res.setAvailabilityDate(Long.parseLong(req.get("ad").n()));
        res.setName(req.get("n").s().split("#")[1].toLowerCase());
        res.setLastUpdated(Long.parseLong(req.get("ld").n()));
        res.setProfileIconId(Integer.parseInt(req.get("pid").n()));
        return res;
    }

    public static Map<String, AttributeValue> toAttributeMap(String name, String region) {
        Map<String, AttributeValue> result = new HashMap<>();
        result.put("n", toAttributeString(region.toUpperCase() + "#" + name));
        return result;
    }

    public static AttributeValue toAttributeString(String s) {
        return AttributeValue.builder().s(s).build();
    }

    public static AttributeValue toAttributeNumber(long n) {
        return AttributeValue.builder().n(String.valueOf(n)).build();
    }
}
