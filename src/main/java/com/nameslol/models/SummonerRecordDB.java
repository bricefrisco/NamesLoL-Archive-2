package com.nameslol.models;

import java.sql.Date;

public class SummonerRecordDB {
    // Availability date
    private Date ad;

    // Region
    private String r;

    // Account ID
    private String aid;

    // Revision date
    private long rd;

    // Level
    private int l;

    // Region + # + length of name
    private String nl;

    // Last updated
    private long ld;

    public Date getAd() {
        return ad;
    }

    public String getR() {
        return r;
    }

    public String getAid() {
        return aid;
    }

    public long getRd() {
        return rd;
    }

    public int getL() {
        return l;
    }

    public String getNl() {
        return nl;
    }

    public long getLd() {
        return ld;
    }

    public void setAd(Date ad) {
        this.ad = ad;
    }

    public void setR(String r) {
        this.r = r;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public void setRd(long rd) {
        this.rd = rd;
    }

    public void setL(int l) {
        this.l = l;
    }

    public void setNl(String nl) {
        this.nl = nl;
    }

    public void setLd(long ld) {
        this.ld = ld;
    }

    @Override
    public String toString() {
        return "SummonerRecordDB{" +
                "ad=" + ad +
                ", r='" + r + '\'' +
                ", aid='" + aid + '\'' +
                ", rd=" + rd +
                ", l=" + l +
                ", nl='" + nl + '\'' +
                ", ld=" + ld +
                '}';
    }
}
