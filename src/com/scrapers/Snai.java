package com.scrapers;

import com.database.objects.Endpoint;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class Snai implements IScraper {
    public ChromeDriver driver;

    // elementi sito snai
    public static final String snaiMainTable = "SportTableScommesse_table__kizsk";
    public static final String snaiRowClass = "ScommesseTableRow_container__6QF4F";
    public static final String snaiTeamClass = "ScommesseTableCompetitors_name__Xn06E";

    public Snai(ChromeDriver driver){
        this.driver = driver;
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
    }

    public static void snaiPageScraper(ChromeDriver driver, String url) {
        try {
            driver.get(url); // apre la pagina

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15)); // aspetta che la pagina carichi
            try{
                wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className(snaiMainTable)));
            }catch (Exception e){
                System.out.println("Tabella non trovata - non sono disponibili scommesse per questo campionato");
                return;
            }

            // Trova tutte le righe delle partite
            List<WebElement> rows = driver.findElements(By.xpath("//div[@class='" + snaiRowClass + "']"));

            for (WebElement row : rows) {
                try {
                    List<WebElement> teams = row.findElements(By.xpath(".//span[contains(@class, '" + snaiTeamClass +"')]"));
                    if (teams.size() == 2) {
                        String homeTeam = teams.get(0).getText().trim();
                        String awayTeam = teams.get(1).getText().trim();

                        WebElement q1 = row.findElement(By.xpath(".//div[@data-testid][contains(@data-testid, '-1')]//button"));
                        WebElement qX = row.findElement(By.xpath(".//div[@data-testid][contains(@data-testid, '-2')]//button"));
                        WebElement q2 = row.findElement(By.xpath(".//div[@data-testid][contains(@data-testid, '-3')]//button"));

                        String homeWinQuote = q1.getText();
                        String drawQuote = qX.getText();
                        String awayWinQuote = q2.getText();

                        System.out.println("Partita: " + homeTeam + " vs " + awayTeam);
                        System.out.println("Quote 1X2:");
                        System.out.println("Casa (1): " + homeWinQuote + " - Pareggio (X): " + drawQuote + " - Trasferta (2): " + awayWinQuote);
                    } else {
                        System.out.println("Errore: info non trovate o pagina non corretta.");
                    }
                } catch (Exception e) {
                    System.out.println("Errore nell'estrazione delle informazioni per una partita.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void scrape(Session session) {
        List<Endpoint> endpoints = session.createQuery("FROM Endpoint e WHERE e.id.bookmaker.id = :bookmakerId", Endpoint.class)
                .setParameter("bookmakerId", 1)
                .getResultList();
        for (Endpoint e : endpoints){
            System.out.println(e.getUrl());
            snaiPageScraper(driver, e.getUrl());
        }
        driver.quit();
    }

    // più veloce perchè non accede al database, però ovviamente meno flessibile
    public void scrape_hard_coded(ChromeDriver driver){
        snaiPageScraper(driver,"https://www.snai.it/sport/calcio/champions%20league");
        snaiPageScraper(driver,"https://www.snai.it/sport/calcio/europa%20league");
        snaiPageScraper(driver,"https://www.snai.it/sport/calcio/conference%20league");
        snaiPageScraper(driver, "https://www.snai.it/sport/calcio/serie%20a");
        snaiPageScraper(driver,"https://www.snai.it/sport/calcio/premier%20league");
        snaiPageScraper(driver,"https://www.snai.it/sport/calcio/bundesliga");
        snaiPageScraper(driver,"https://www.snai.it/sport/calcio/liga");
        snaiPageScraper(driver,"https://www.snai.it/sport/calcio/league%201");
    }
}
