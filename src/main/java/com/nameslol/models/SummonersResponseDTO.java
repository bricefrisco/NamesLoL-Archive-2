package com.nameslol.models;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;

@RegisterForReflection
public class SummonersResponseDTO {
    private List<SummonerResponseDTO> summoners;
    private long backwards;
    private long forwards;

    public List<SummonerResponseDTO> getSummoners() {
        return summoners;
    }

    public long getBackwards() {
        return backwards;
    }

    public long getForwards() {
        return forwards;
    }

    public void setSummoners(List<SummonerResponseDTO> summoners) {
        this.summoners = summoners;
    }

    public void setBackwards(long backwards) {
        this.backwards = backwards;
    }

    public void setForwards(long forwards) {
        this.forwards = forwards;
    }

    @Override
    public String toString() {
        return "SummonersResponseDTO{" +
                "summoners=" + summoners +
                ", backwards=" + backwards +
                ", forwards=" + forwards +
                '}';
    }
}
