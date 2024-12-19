package com.database.objects;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "odds")
public class Odd {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    public Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookmaker_id", nullable = false)
    public Bookmaker bookmaker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "odd_type_id", nullable = false)
    public OddType oddType;

    @Column(name = "odd_option", nullable = false)
    public String oddOption;

    @Column(name = "value", nullable = false)
    public BigDecimal value;

    @Column(name = "retrieved_at", nullable = false)
    public Timestamp retrievedAt;

    // Getters e Setters
}