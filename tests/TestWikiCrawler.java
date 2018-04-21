import org.junit.Test;

import java.util.ArrayList;

public class TestWikiCrawler {

    @Test
    public void ISU() {
        ArrayList<String> topics = new ArrayList<>();
        topics.add("Iowa State");
        topics.add("Cyclones");
        WikiCrawler wc = new WikiCrawler("/wiki/Iowa_State_University", 100, topics, "isu2");
        wc.crawl();
    }

    @Test
    public void ComplexityTheory() {
        ArrayList<String> topics = new ArrayList<>();
        WikiCrawler wc = new WikiCrawler("/wiki/Complexity_theory", 20, topics, "complexitytheory.txt");
        wc.crawl();
    }

    @Test
    public void Report() {
        ArrayList<String> topics = new ArrayList<>();
        WikiCrawler wc = new WikiCrawler("/wiki/Computer_Science", 100, topics, "WikiCS.txt");
        wc.crawl();
    }

    @Test
    public void ReportAlgo() {
        NetworkInfluence inf = new NetworkInfluence("WikiCS.txt");
        System.out.println("MOST INFLUENTIAL DEGREE");
        System.out.println(inf.mostInfluentialDegree(10));
        System.out.println("\nMOST INFLUENTIAL MODULAR");
        System.out.println(inf.mostInfluentialModular(10));
        System.out.println("\nMOST INFLUENTIAL SUB MODULAR");
        System.out.println(inf.mostInfluentialSubModular(10));
    }
}
