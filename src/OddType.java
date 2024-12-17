import javax.persistence.*;

@Entity
@Table(name = "odd_types")
public class OddType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @Column(name = "name", nullable = false)
    public String name;

    @Column(name = "odd_description")
    public String oddDescription;

    // Getters e Setters
}