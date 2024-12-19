package com.database.objects;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "arbitrage_opportunities")
public class ArbitrageOpportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "odd_type", nullable = false)
    private OddType oddType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "odd1", nullable = false)
    private Odd odd1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "odd2", nullable = false)
    private Odd odd2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "odd3")
    private Odd odd3;

    @Column(name = "profit_margin", nullable = false)
    private BigDecimal profitMargin;

    @Column(name = "identified_at", nullable = false)
    private Timestamp identifiedAt;

    // Getters e Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Match getMatch() {
        return match;
    }
    public void setMatch(Match match) {
        this.match = match;
    }
    public OddType getOddType() {
        return oddType;
    }
    public void setOddType(OddType oddType) {
        this.oddType = oddType;
    }
    public Odd getOdd1() {
        return odd1;
    }
    public void setOdd1(Odd odd1) {
        this.odd1 = odd1;
    }
    public Odd getOdd2() {
        return odd2;
    }
    public void setOdd2(Odd odd2) {
        this.odd2 = odd2;
    }
    public Odd getOdd3() {
        return odd3;
    }
    public void setOdd3(Odd odd3) {
        this.odd3 = odd3;
    }
    public BigDecimal getProfitMargin() {
        return profitMargin;
    }
    public void setProfitMargin(BigDecimal profitMargin) {
        this.profitMargin = profitMargin;
    }
    public Timestamp getIdentifiedAt() {
        return identifiedAt;
    }
    public void setIdentifiedAt(Timestamp identifiedAt) {
        this.identifiedAt = identifiedAt;
    }
}
