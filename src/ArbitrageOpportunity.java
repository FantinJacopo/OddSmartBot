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
}
