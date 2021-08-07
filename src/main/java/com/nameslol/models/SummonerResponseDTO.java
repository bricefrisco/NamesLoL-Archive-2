package com.nameslol.models;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class SummonerResponseDTO {
    private String name;
    private String region;
    private String accountId;
    private Long revisionDate;
    private Integer level;
    private Long availabilityDate;
    private Long lastUpdated;
    private Integer profileIconId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Long getRevisionDate() {
        return revisionDate;
    }

    public void setRevisionDate(Long revisionDate) {
        this.revisionDate = revisionDate;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Long getAvailabilityDate() {
        return availabilityDate;
    }

    public void setAvailabilityDate(Long availabilityDate) {
        this.availabilityDate = availabilityDate;
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Integer getProfileIconId() {
        return profileIconId;
    }

    public void setProfileIconId(Integer profileIconId) {
        this.profileIconId = profileIconId;
    }

    @Override
    public String toString() {
        return "SummonerResponseDTO{" +
                "name='" + name + '\'' +
                ", region='" + region + '\'' +
                ", accountId='" + accountId + '\'' +
                ", revisionDate=" + revisionDate +
                ", level=" + level +
                ", availabilityDate=" + availabilityDate +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
