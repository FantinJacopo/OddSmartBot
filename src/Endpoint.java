import javax.persistence.*;

@Entity
@Table(name = "endpoints")
public class Endpoint {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookmaker", nullable = false)
    public Bookmaker bookmaker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "league", nullable = false)
    public League league;

    @Column(name = "url", nullable = false)
    public String url;

    // Getters e Setters
}
