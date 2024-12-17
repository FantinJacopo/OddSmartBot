import javax.persistence.*;

@Entity
@Table(name = "bookmakers")
public class Bookmaker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @Column(name = "name", nullable = false)
    public String name;

    @Column(name = "website")
    public String website;

    // Getters e Setters
}