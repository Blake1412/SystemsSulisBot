import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Scraper {

    private static final WebClient client;

    static {
        client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setThrowExceptionOnScriptError(false);

        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("/home/ubuntu/SystemsSulisBot/config.properties"));

            client.getCookieManager().addCookie(new Cookie("login.microsoftonline.com", "ESTSAUTHPERSISTENT", properties.getProperty("Cookie")));
            client.getPage("https://login.microsoftonline.com");
            client.waitForBackgroundJavaScript(1000);
            HtmlPage page = client.getPage("https://sulis.ul.ie");
            HtmlElement element = page.getFirstByXPath("//*[@id=\"loginLink1\"]");
            element.click();
            client.getOptions().setJavaScriptEnabled(false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void scrape() {
        DateTimeFormatter dateParseUS = DateTimeFormatter.ofPattern("MMM d',' uuuu h:mm a", Locale.US);
        List<Assignment> assignments = new ArrayList<>();
        Arrays.stream(Module.values()).forEach(module -> {
            try {
                HtmlPage page = client.getPage(module.getAssignmentsLink());
                List<HtmlElement> elements = page.getByXPath("//tr[position()>1]");
                for (HtmlElement element : elements) {
                    HtmlElement titleElement = element.getFirstByXPath(".//td[@headers=\"title\"]");
                    HtmlElement linkElement = element.getFirstByXPath(".//a");
                    HtmlElement openDateElement = element.getFirstByXPath(".//td[@headers=\"openDate\"]");
                    HtmlElement dueDateElement = element.getFirstByXPath(".//td[5]/span[@class=\"highlight\"]");

                    String title = titleElement.getVisibleText();
                    String link = linkElement.getAttribute("href");
                    String openDate = LocalDateTime.parse(openDateElement.getVisibleText(), dateParseUS).toString();
                    String dueDate = LocalDateTime.parse(dueDateElement.getVisibleText(), dateParseUS).toString();

                    assignments.add(new Assignment(title, module.name(), link, openDate, dueDate));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        List<String> dbLinks = DatabaseConnection.selectAssignments().stream().map(Assignment::getLink).toList();
        List<String> links = assignments.stream().map(Assignment::getLink).toList();
        for (String dbLink : dbLinks) {
            if (!links.contains(dbLink)) DatabaseConnection.delete("Assignment", List.of(String.format("link = '%s'", dbLink)));
        }
        assignments.forEach(DatabaseConnection::insert);

        DateTimeFormatter dateParseDefault = DateTimeFormatter.ofPattern("MMM d',' uuuu h:mm a");
        List<Announcement> announcements = new ArrayList<>();
        Arrays.stream(Module.values()).forEach(module -> {
            try {
                HtmlPage page = client.getPage(module.getAnnouncementsLink());
                List<HtmlElement> elements = page.getByXPath("//tr[position()>1]");
                for (HtmlElement element : elements) {
                    HtmlElement titleElement = element.getFirstByXPath(".//th[@headers=\"subject\"]");
                    HtmlElement linkElement = element.getFirstByXPath(".//a");
                    HtmlElement authorElement = element.getFirstByXPath(".//td[@headers=\"author\"]");
                    HtmlElement postedAtElement = element.getFirstByXPath(".//td[@headers=\"date\"]");

                    String title = titleElement.getVisibleText();
                    title = title.substring(0, title.length() / 2);
                    String link = linkElement.getAttribute("href");
                    String author = authorElement.getVisibleText();
                    String postedAt = LocalDateTime.parse(postedAtElement.getVisibleText(), dateParseDefault).toString();

                    announcements.add(new Announcement(title, module.name(), link, author, postedAt));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        dbLinks = DatabaseConnection.selectAnnouncements().stream().map(Announcement::getLink).toList();
        links = announcements.stream().map(Announcement::getLink).toList();
        for (String link : dbLinks) {
            if (!links.contains(link)) DatabaseConnection.delete("Announcement", List.of(String.format("link = '%s'", link)));
        }
        announcements.forEach(DatabaseConnection::insert);
    }
}
