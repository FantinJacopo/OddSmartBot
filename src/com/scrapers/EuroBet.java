package com.scrapers;

import com.oddsmart.database.DataBase;
import com.oddsmart.database.Match;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EuroBet implements IScraper {
    public ChromeDriver driver;
    //public Session session;
    public DataBase dataBase;
    public static final int id = 2;

    // elementi sito eurobet
    public static final String euroBetRowClass = "event-row"; // Classe della riga
    public static final String euroBetDateClass = "time-box";
    public static final String euroBetTeamClass = "event-players"; // Classe dei nomi delle squadre
    public static final String euroBetQuoteClass = "quota-new"; // Classe delle quote

    public static final int pageLoadingTime = 5; // con il mio computer bastano 5 secondi, potrebbero servirne di più

    public EuroBet(ChromeDriver driver, DataBase dataBase){
        this.driver = driver;
        //session = HibernateUtil.getSession();
        this.dataBase = dataBase;
    }

    public EuroBet(){
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
            driver.get(url);
            try {
                Thread.sleep(500); // necessario per evitare che le righe si carichino ma senza i valori
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(pageLoadingTime));
            try{
                wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className(euroBetRowClass)));
            }catch (Exception e){
                System.out.println("Tabella non trovata - non sono attualmente disponibili scommesse per questo campionato");
                return;
            }

            Match match;

            // Trova tutte le righe delle partite
            List<WebElement> rows = driver.findElements(By.xpath("//div[contains(@class, '" + euroBetRowClass + "')]"));
            List<String> quote_option_1x2 = List.of(new String[]{"1", "X", "2"});
            for (WebElement row : rows) {

                match = new Match();
                match.leagueId = leagueId;
                try {
                    // data e ora del match
                    List<WebElement> dates = row.findElements(By.xpath(".//div[contains(@class, '" + euroBetDateClass + "')]//p"));
                    if (dates.size() == 2) {
                        String dateStr = dates.get(0).getText().trim();
                        String timeStr = dates.get(1).getText().trim();
                        match.date = ScrapersUtils.formatDate(dateStr, timeStr);
                    }else continue;

                    // squadre
                    List<WebElement> teams = row.findElements(By.xpath(".//div[contains(@class, '" + euroBetTeamClass + "')]//a//span"));
                    try{
                        match.homeTeam = teams.get(0).getText().trim();
                        match.awayTeam = teams.get(2).getText().trim();
                    }catch(Exception e){
                        continue;
                    }

                    Match existingMatch = dataBase.findMatch(match);
                    if (existingMatch != null)
                        match = existingMatch;
                    else{
                        match.id = dataBase.insertMatch("matches", match);
                    }

                    // quote 1x2
                    List<WebElement> quotesContainers = row.findElements(By.xpath(".//div[contains(@class, '" + euroBetQuoteClass + "')]//a"));

                    for(int i = 0; i < quote_option_1x2.size(); i++){
                        if(!ScrapersUtils.manageOdd(quotesContainers.get(i).getText(), id, match.id, url, 1, quote_option_1x2.get(i), dataBase))
                            System.out.println("League " + leagueId + " - Invalid odd " + quote_option_1x2.get(i));
                    }
                } catch (Exception e) {
                    System.out.println("Errore nel recupero dei dati per la partita: " + url);
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // più veloce perchè non accede al database, però ovviamente meno flessibile, più vulnerabile ai cambiamenti del sito
    public void scrape_hard_coded(){
        pageScraper("https://www.eurobet.it/it/scommesse/#!/calcio/eu-champions-league/", 1);
        pageScraper("https://www.eurobet.it/it/scommesse/#!/calcio/eu-europa-league/", 2);
        pageScraper("https://www.eurobet.it/it/scommesse/#!/calcio/eu-conference-league/", 3);
        pageScraper("https://www.eurobet.it/it/scommesse/#!/calcio/it-serie-a/", 4);
        pageScraper("https://www.eurobet.it/it/scommesse/#!/calcio/ing-premier-league/", 5);
        pageScraper("https://www.eurobet.it/it/scommesse/#!/calcio/de-bundesliga/", 6);
        pageScraper("https://www.eurobet.it/it/scommesse/#!/calcio/es-liga/", 7);
        pageScraper("https://www.eurobet.it/it/scommesse/#!/calcio/fr-league-1/", 8);
    }
}
