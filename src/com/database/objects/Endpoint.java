package com.database.objects;

import javax.persistence.*;

@Entity
@Table(name = "endpoints")
public class Endpoint {

    @EmbeddedId
    public EndpointId id;

    @Column(name = "url", nullable = false)
    public String url;

    // Getters e Setters
    public EndpointId getId() {
        return id;
    }

    public void setId(EndpointId id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}