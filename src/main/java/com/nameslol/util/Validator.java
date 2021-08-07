package com.nameslol.util;

import com.nameslol.models.Region;
import com.nameslol.models.exceptions.BadRequestException;

public final class Validator {
    public static void validateRegion(String region) {
        if (region == null || region.isBlank()) throw new BadRequestException("Region cannot be null or blank.");
        try {
            Region r = Region.valueOf(region.toUpperCase());
        } catch (Exception e) {
            throw new BadRequestException("'" + region + "' is not a valid region.");
        }
    }

    public static void validateTimestamp(long timestamp) {
        if (timestamp == 0) throw new BadRequestException("Timestamp cannot be 0.");
        if (timestamp < 1) throw new BadRequestException("Timestamp cannot be negative.");
    }
}
