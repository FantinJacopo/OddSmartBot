package com.database.objects;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @Column(name = "home_team", nullable = false)
    public String homeTeam;

    @Column(name = "away_team", nullable = false)
    public String awayTeam;

    @Column(name = "start_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date startTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "league", nullable = false)
    public League league;
    public Date date;

    // Getters e Setters
}