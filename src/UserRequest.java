import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "user_requests")
public class UserRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    @Column(name = "query")
    public String query;

    @Column(name = "response")
    public String response;

    @Column(name = "requested_at", nullable = false)
    public Timestamp requestedAt;


}