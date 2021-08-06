package com.nameslol.util;

import com.nameslol.models.Region;
import com.nameslol.models.SummonerRecordDB;
import com.nameslol.models.SummonerRecordDTO;

import java.sql.Date;

public final class NameMapper {
    public static long toAvailabilityDate(long revisionDate, int level) {
        return System.currentTimeMillis();
    }

    public static SummonerRecordDB toSummonerRecordDB(SummonerRecordDTO dto, Region region) {
        SummonerRecordDB result = new SummonerRecordDB();
        result.setN(region.name() + "#" + dto.getName().trim().toUpperCase());
        result.setR(region.name());
        result.setAid(result.getAid());
        result.setRd(dto.getRevisionDate());
        result.setL(dto.getSummonerLevel());
        result.setNl(region.name() + "#" + dto.getName().trim().length());
        result.setLd(System.currentTimeMillis());
        return result;
    }
}
