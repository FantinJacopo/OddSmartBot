package com.scrapers;

import org.hibernate.Session;

public interface IScraper {
    void scrape(Session session);
}
