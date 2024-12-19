package com.database.objects;

import javax.persistence.*;

@Entity
@Table(name = "user_favorites")
public class UserFavorites {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    public Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "odd_id")
    public Odd odd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arbitrage_id")
    public ArbitrageOpportunity arbitrageOpportunity;

    @Column(name = "notify", nullable = false)
    public boolean notify;

    // Getters e Setters
}
