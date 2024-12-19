import org.hibernate.Session;
import org.hibernate.Transaction;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.MonthDay;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.Date;

import com.database.objects.HibernateUtil;
import com.database.objects.Bookmaker;
import com.database.objects.Endpoint;

public class Scraper {
    public static Session session = HibernateUtil.getSession();

    // variabili eurobet
    public static final String euroBetRowClass = "event-row"; // Classe della riga
    public static final String euroBetDateClass = "time-box";
    public static final String euroBetTeamClass = "event-players"; // Classe dei nomi delle squadre
    public static final String euroBetQuoteClass = "quota-new"; // Classe delle quote


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

    public static void main(String[] args) {
        //System.setProperty("webdriver.chrome.driver", "percorso\fino a\chromedriver.exe");
        snaiScraper();
    }
}