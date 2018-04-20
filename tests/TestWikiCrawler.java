import java.io.IOException;
import java.util.ArrayList;

public class TestWikiCrawler {
    public static void main(String[] args) throws IOException, InterruptedException {
        ArrayList<String> topics = new ArrayList<>();
        topics.add("Iowa State");
        topics.add("Cyclones");
        WikiCrawler wc = new WikiCrawler("/wiki/Iowa_State_University", 100,  topics,"isu2");
        wc.crawl();
    }
}
