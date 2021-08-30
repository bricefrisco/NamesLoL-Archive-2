package com.nameslol.models;

public enum Region {
    NA,
    BR,
    EUNE,
    EUW,
    KR,
    LAN,
    LAS,
    TR,
    OCE;

    public String toRiotFormat() {
        switch(this) {
            case BR:
                return "br1";
            case EUNE:
                return "eun1";
            case EUW:
                return "euw1";
            case LAN:
                return "la1";
            case LAS:
                return "la2";
            case NA:
                return "na1";
            case TR:
                return "tr1";
            case KR:
                return "kr";
            case OCE:
                return "oc1";
            default:
                throw new IllegalArgumentException("Region is invalid!");
        }
    }
}
