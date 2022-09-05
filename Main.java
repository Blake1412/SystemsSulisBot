import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(Scraper::scrape, 0, 1, TimeUnit.MINUTES);
        scheduler.scheduleAtFixedRate(Bot::checkForNew, 0, 5, TimeUnit.SECONDS);
    }
}
