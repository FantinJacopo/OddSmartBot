package com.scrapers;

import org.hibernate.Session;
import org.openqa.selenium.chrome.ChromeDriver;

public interface IScraper {
    void scrape();
    void pageScraper(String url, int leagueId);
}
