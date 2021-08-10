package com.nameslol.util;

import com.nameslol.models.Region;
import com.nameslol.models.SummonerRecordDTO;
import com.nameslol.models.SummonerResponseDTO;
import com.nameslol.models.SummonersResponseDTO;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
@Named("recordMapper")
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

    public static SummonerResponseDTO toSummonerResponseDTO(SummonerRecordDTO summoner, Region region) {
        SummonerResponseDTO response = new SummonerResponseDTO();
        response.setProfileIconId(summoner.getProfileIconId());
        response.setName(summoner.getName());
        response.setRegion(region.name());
        response.setLevel(summoner.getSummonerLevel());
        response.setRevisionDate(summoner.getRevisionDate());
        response.setAvailabilityDate(toAvailabilityDate(summoner.getRevisionDate(), summoner.getSummonerLevel()));
        response.setAccountId(summoner.getAccountId());
        response.setLastUpdated(System.currentTimeMillis());
        return response;
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

    public static SummonersResponseDTO toSummonersResponseDTO(List<SummonerResponseDTO> summoners) {
        summoners = summoners.stream().sorted(Comparator.comparing(SummonerResponseDTO::getAvailabilityDate)).collect(Collectors.toList());

        SummonersResponseDTO response = new SummonersResponseDTO();
        response.setSummoners(summoners);
        if (summoners.size() == 0) return response;
        response.setForwards(summoners.get(summoners.size() - 1).getAvailabilityDate());
        response.setBackwards(summoners.get(0).getAvailabilityDate());
        return response;
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
