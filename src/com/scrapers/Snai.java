package com.scrapers;

import com.oddsmart.database.DataBase;
import com.oddsmart.database.Match;
import com.oddsmart.database.Odd;
import org.hibernate.Session;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.*;

public class Snai implements IScraper {
    public ChromeDriver driver;
    //public Session session;
    public DataBase dataBase;
    public static final int id = 1;

    // elementi sito snai
    public static final String MainTable = "SportTableScommesse_table__kizsk";
    public static final String RowClass = "ScommesseTableRow_container__6QF4F";
    public static final String TeamClass = "ScommesseTableCompetitors_name__Xn06E";
    public static final String DateClass = "ScommesseTableDateTime_listItem___IUK8";

    public static final int pageLoadingTime = 5; // con il mio computer bastano 5 secondi, potrebbero servirne di più

    public Snai(ChromeDriver driver, DataBase dataBase){
        this.driver = driver;
        //session = HibernateUtil.getSession();
        this.dataBase = dataBase;
    }

    public Snai(){
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--start-maximized");
        options.addArguments("--disable-blink-features=AutomationControlled"); // Evita rilevamento di headless
        options.addArguments("--disable-gpu");
        options.setExperimentalOption("excludeSwitches", List.of("enable-automation")); // Rimuovi flag di automazione
        options.setExperimentalOption("useAutomationExtension", false); // Disabilita estensioni di automazione
        driver = new ChromeDriver(options);
        //session = HibernateUtil.getSession();
        dataBase = new DataBase("localhost", "oddsmartbot", "root", "");
    }

    @Override
    public void scrape() {
        ArrayList<Pair<Integer, String>> endpoints = dataBase.getBookmakerEndpoints(id);
        if(!endpoints.isEmpty()){
            for (Pair<Integer, String> e : endpoints)
                pageScraper(e.getValue(), e.getKey());
        }else scrape_hard_coded();
    }

    @Override
    public void pageScraper(String url, int leagueId) {
        try {
            driver.get(url); // apre la pagina

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(pageLoadingTime)); // aspetta che la pagina carichi

            try{
                wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className(MainTable)));
            }catch (Exception e){
                System.out.println("Tabella non trovata - non sono attualmente disponibili scommesse per questo campionato");
                return;
            }

            Match match;

            // Trova tutte le righe delle partite
            List<WebElement> rows = driver.findElements(By.xpath("//div[@class='" + RowClass + "']"));

            for (WebElement row : rows) {

                match = new Match();
                match.leagueId = leagueId;
                try {
                    // data e ora del match
                    List<WebElement> dates = row.findElements(By.xpath(".//li[contains(@class, '" + DateClass +"')]"));
                    if (dates.size() == 2) {
                        String dateStr = dates.get(0).getText().trim();
                        String timeStr = dates.get(1).getText().trim();
                        match.date = ScrapersUtils.formatDate(dateStr, timeStr);
                    }else continue;

                    // squadre
                    List<WebElement> teams = row.findElements(By.xpath(".//span[contains(@class, '" + TeamClass +"')]"));
                    if (teams.size() == 2) {
                        match.homeTeam = teams.get(0).getText().trim();
                        match.awayTeam = teams.get(1).getText().trim();
                    }else continue;

                    Match existingMatch = dataBase.findMatch(match);
                    if (existingMatch != null)
                        match = existingMatch;
                    else{
                        match.id = dataBase.insertMatch("matches", match);
                    }

                    // quote 1x2
                    WebElement q1 = row.findElement(By.xpath(".//div[@data-testid][contains(@data-testid, '-1')]//button"));
                    if(q1 != null){
                        String homeWinQuote = q1.getText();
                        if(!ScrapersUtils.manageOdd(homeWinQuote, id, match.id, url,1, "1", dataBase))
                            System.out.println("League " + leagueId + " - Invalid odd 1");
                    }
                    WebElement qX = row.findElement(By.xpath(".//div[@data-testid][contains(@data-testid, '-2')]//button"));
                    if(qX != null){
                        String drawQuote = qX.getText();
                        if(!ScrapersUtils.manageOdd(drawQuote, id, match.id, url,1, "X", dataBase))
                            System.out.println("League " + leagueId + " - Invalid odd X");
                    }
                    WebElement q2 = row.findElement(By.xpath(".//div[@data-testid][contains(@data-testid, '-3')]//button"));
                    if(q2 != null){
                        String awayWinQuote = q2.getText();
                        if(!ScrapersUtils.manageOdd(awayWinQuote, id, match.id, url,1, "2", dataBase))
                            System.out.println("League " + leagueId + " - Invalid odd 2");
                    }

                } catch (Exception e) {
                    System.out.println("Errore nell'estrazione delle informazioni per una partita.");
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // più veloce perchè non accede al database, però ovviamente meno flessibile, più vulnerabile ai cambiamenti del sito
    public void scrape_hard_coded(){
        pageScraper("https://www.snai.it/sport/calcio/champions%20league", 1);
        pageScraper("https://www.snai.it/sport/calcio/europa%20league", 2);
        pageScraper("https://www.snai.it/sport/calcio/conference%20league", 3);
        pageScraper("https://www.snai.it/sport/calcio/serie%20a", 4);
        pageScraper("https://www.snai.it/sport/calcio/premier%20league", 5);
        pageScraper("https://www.snai.it/sport/calcio/bundesliga", 6);
        pageScraper("https://www.snai.it/sport/calcio/liga", 7);
        pageScraper("https://www.snai.it/sport/calcio/league%201", 8);
    }
}
