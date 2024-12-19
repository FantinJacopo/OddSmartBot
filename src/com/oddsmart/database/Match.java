package com.oddsmart.database;

import java.sql.Time;
import java.util.Date;

public class Match {
    public int id;
    public int leagueId;
    public String homeTeam;
    public String awayTeam;
    public Date date;

    public Match(int id, int leagueId, String homeTeam, String awayTeam, Date date) {
        this.id = id;
        this.leagueId = leagueId;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.date = date;
    }

    public Match() {}

    public Match(int leagueId, String homeTeam, String awayTeam, Date date) {
        this.leagueId = leagueId;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.date = date;
    }
}
