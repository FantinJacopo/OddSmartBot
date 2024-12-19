package com.database.objects;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @Column(name = "username", nullable = false)
    public String username;

    @Column(name = "telegram_id", nullable = false)
    public long telegramId;

    // Getters e Setters
}