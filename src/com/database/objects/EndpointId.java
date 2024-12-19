package com.database.objects;

import java.io.Serializable;
import javax.persistence.*;

@Embeddable
public class EndpointId implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookmaker", nullable = false)
    public Bookmaker bookmaker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "league", nullable = false)
    public League league;

    // Getters, setters, equals() e hashCode() per l'ID composto

    public Bookmaker getBookmaker() {
        return bookmaker;
    }

    public void setBookmaker(Bookmaker bookmaker) {
        this.bookmaker = bookmaker;
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EndpointId that = (EndpointId) o;
        return bookmaker.equals(that.bookmaker) && league.equals(that.league);
    }

    @Override
    public int hashCode() {
        return 31 * bookmaker.hashCode() + league.hashCode();
    }
}