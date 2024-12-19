package com.database.objects;

import javax.persistence.*;

@Entity
@Table(name = "leagues")
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @Column(name = "nation", nullable = false)
    public String nation;

    @Column(name = "name", nullable = false)
    public String name;

    // Getters e Setters
}
