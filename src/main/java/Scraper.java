import com.oddsmart.database.DataBase;
import com.scrapers.EuroBet;
import com.scrapers.Snai;
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
    //public static Session session = HibernateUtil.getSession();
    static DataBase db = new DataBase("localhost", "oddsmartbot", "root", "");
    static ChromeOptions options = new ChromeOptions()
            .addArguments("--remote-allow-origins=*")
            .addArguments("--start-maximized")
            .addArguments("--disable-blink-features=AutomationControlled") // Evita rilevamento di headless
            .addArguments("--disable-gpu")
            .setExperimentalOption("excludeSwitches", List.of("enable-automation")) // Rimuovi flag di automazione
            .setExperimentalOption("useAutomationExtension", false); // Disabilita estensioni di automazione
    static ChromeDriver driver = new ChromeDriver(options);

    public Scraper() {
        ChromeOptions options = new ChromeOptions()
                .addArguments("--remote-allow-origins=*")
                .addArguments("--start-maximized")
                .addArguments("--disable-blink-features=AutomationControlled") // Evita rilevamento di headless
                .addArguments("--disable-gpu")
                .setExperimentalOption("excludeSwitches", List.of("enable-automation")) // Rimuovi flag di automazione
                .setExperimentalOption("useAutomationExtension", false); // Disabilita estensioni di automazione
        driver = new ChromeDriver(options);
    }

    // variabili eurobet
    public static final String euroBetRowClass = "event-row"; // Classe della riga
    public static final String euroBetDateClass = "time-box";
    public static final String euroBetTeamClass = "event-players"; // Classe dei nomi delle squadre
    public static final String euroBetQuoteClass = "quota-new"; // Classe delle quote



    public static void main(String[] args) {
        //System.setProperty("webdriver.chrome.driver", "percorso\fino a\chromedriver.exe");
        Snai snai = new Snai(driver, db);
        snai.scrape();
        EuroBet euroBet = new EuroBet(driver, db);
        euroBet.scrape();

        driver.quit();
    }
}