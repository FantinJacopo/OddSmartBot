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

    public static final int pageLoadingTime = 15;

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
                    for (WebElement quote : quotesContainers) {
                        if(!ScrapersUtils.manageOdd(quote.getText(), id, match.id, url, 1, quote_option_1x2.get(quotesContainers.indexOf(quote)), dataBase))
                            System.out.println("League " + leagueId + " - Invalid odd " + quote_option_1x2.get(quotesContainers.indexOf(quote)));
                    }

                    for(int i = 0; i < quote_option_1x2.size(); i++){
                        if(!ScrapersUtils.manageOdd(quotesContainers.get(i).getText(), id, match.id, url, 1, quote_option_1x2.get(i), dataBase))
                            System.out.println("League " + leagueId + " - Invalid odd " + quote_option_1x2.get(i));
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

    public static void euroBetPageScraper(ChromeDriver driver, String url) {
        try {
            driver.get(url);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className(euroBetRowClass)));

            // Trova tutte le righe delle partite
            List<WebElement> rows = driver.findElements(By.xpath("//div[contains(@class, '" + euroBetRowClass + "')]"));

            for (WebElement row : rows) {
                try {
                    List<WebElement> d = row.findElements(By.xpath(".//div[contains(@class, '" + euroBetDateClass + "')]//p"));
                    if (d.size() >= 2) {
                        String dateStr = d.get(0).getText().trim();
                        String timeStr = d.get(1).getText().trim();
                        try {
                            Date now = Calendar.getInstance().getTime();
                            int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(now));
                            if(now.after(new SimpleDateFormat("dd/MM/yyyy").parse(dateStr + "/" + year))){
                                year++;
                            }
                            dateStr = dateStr + "/" + year;

                            Date date = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(dateStr + " " + timeStr);
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                            System.out.println("Data e ora del match: " + sdf.format(date));
                        } catch (ParseException e) {
                            System.out.println("Errore nel parsing della data e ora.");
                        }
                    } else {
                        System.out.println("Informazioni di data e ora non trovate.");
                    }

                    List<WebElement> teams = row.findElements(By.xpath(".//div[contains(@class, '" + euroBetTeamClass + "')]//a//span"));

                    String homeTeam = teams.get(0).getText().trim();
                    String awayTeam = teams.get(2).getText().trim();

                    List<WebElement> quotesContainers = row.findElements(By.xpath(".//div[contains(@class, '" + euroBetQuoteClass + "')]//a"));

                    List<Double> quotes = new ArrayList<>();
                    for (WebElement quote : quotesContainers) {
                        quotes.add(Double.parseDouble(quote.getText().trim()));
                    }

                    System.out.println("Partita: " + homeTeam + " vs " + awayTeam);
                    System.out.println("Quote 1X2:");
                    System.out.println("Casa (1): " + quotes.get(0) + " - Pareggio (X): " + quotes.get(1) + " - Trasferta (2): " + quotes.get(2));
                } catch (Exception e) {
                    System.out.println("Errore nell'estrazione delle informazioni per una partita.");
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
