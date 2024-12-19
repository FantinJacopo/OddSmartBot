package com.oddsmart.database;

import java.sql.Timestamp;

public class Odd {
    public int id;
    public int bookmakerId;
    public int matchId;
    public String website;
    public int oddTypeId;
    public String oddOption;
    public double value;
    public Timestamp retrievedAt;

    public Odd(int bookmakerId, int matchId, String website, int oddTypeId, String oddOption, double value, Timestamp retrievedAt) {
        this.bookmakerId = bookmakerId;
        this.matchId = matchId;
        this.website = website;
        this.oddTypeId = oddTypeId;
        this.oddOption = oddOption;
        this.value = value;
        this.retrievedAt = retrievedAt;
    }

    public Odd(int id, int bookmakerId, int matchId, String website, int oddTypeId, String oddOption, double value, Timestamp retrievedAt) {
        this.id = id;
        this.bookmakerId = bookmakerId;
        this.matchId = matchId;
        this.website = website;
        this.oddTypeId = oddTypeId;
        this.oddOption = oddOption;
        this.value = value;
        this.retrievedAt = retrievedAt;
    }


    public Odd(){}
}
