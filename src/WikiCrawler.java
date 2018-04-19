// LEAVE THIS FILE IN THE DEFAULT PACKAGE
//  (i.e., DO NOT add 'package cs311.pa1;' or similar)

// DO NOT MODIFY THE EXISTING METHOD SIGNATURES
//  (you may, however, add member fields and additional methods)

// DO NOT INCLUDE LIBRARIES OUTSIDE OF THE JAVA STANDARD LIBRARY
//  (i.e., you may only include libraries of the form java.*)

/**
 * @author Matthew Burket
 * @author Joel May
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class WikiCrawler {
    /**
     * Base URL of search
     */
    static final String BASE_URL = "https://en.wikipedia.org/";
    /**
     * Url of page to start at
     */
    private String seedUrl;
    /**
     * Max number of pages to be crawled
     */
    private int max;
    /**
     * File name to save the results to
     */
    private String fileName;
    /**
     * List of topics
     */
    private ArrayList<String> topics;
    /**
     * DFS Queue
     */
    private Queue<String> q = new LinkedList<>();
    /**
     * Total number pages crawled
     */
    private int requestCount;
    /**
     * Number of pages Crawled
     */
    private int pagesCount;
    /**
     * List of Urls Crawled
     */
    private ArrayList<String> visited;
    /**
     *  A list of Key Value pairs of entries
     */
    private ArrayList<Map.Entry<String, String>> graph;
    /**
     * List of pages that have all the
     */
    private ArrayList<String> onTopic;

    /**
     * Create a Wiki Crawler
     *
     * @param seedUrl Page to start the crawl
     * @param max number of pages
     * @param topics list of topics
     * @param fileName file to save output to
     */
    public WikiCrawler(String seedUrl, int max, ArrayList<String> topics, String fileName) {
        this.seedUrl = seedUrl;
        this.max = max;
        this.fileName = fileName;
        requestCount = 0;
        graph = new ArrayList<>();
        this.topics = topics;
    }

    /**
     * Crawls the BASE_URL based on seedUrl and BASE_URL
     */
    public void crawl() throws IOException, InterruptedException {
        // Add the seed url to the
        q.add(seedUrl);
        while (!q.isEmpty()) {
            // Remove from the top of the queue
            String currentPath = q.remove();
            // Mark Current Path as visited
            visited.add(currentPath);
            // Get page HTML
            String html = getPageText(currentPath);
            // Checking if page contains all the topics
            boolean keepGoing = true;
            for (String s : topics) {
                // Doesn't contain one of the topics exit
                if (!html.contains(s)) {
                    keepGoing = false;
                    break;
                }
            }
            // The Real abort if the page doesn't contain things
            if (!keepGoing) {
                continue;
            }
            // Contains all the topics, get all links on the page
            ArrayList<String> links = getLinks(html);
            ArrayList<String> added = new ArrayList<>();
            for (String link : links) {
                if (added.contains(link) || visited.contains(link)) {
                    continue;
                }
                q.add(link);
                added.add(link);
                graph.add(new AbstractMap.SimpleEntry<>(currentPath, link));
            }
            pagesCount++;
            if (pagesCount >= max) {
                return;
            }
        }
    }

    /**
     * The the text of the given page
     *
     * @param path relative url
     * @return text of the HTML page
     */
    private String getPageText(String path) throws IOException, InterruptedException {
        if (path == null || path.isEmpty() || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Must define a path");
        }
        URL theURL = new URL(BASE_URL + path);
        InputStream inputStream = theURL.openStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder stringBuilder = new StringBuilder();


        String line;

        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        requestCount++;
        if (requestCount % 25 == 0) {
            Thread.sleep(3000);
        }
        return stringBuilder.toString();
    }

    /**
     * Get the links from the page
     *
     * @param html html of the page as string
     * @return a list of links
     */
    private ArrayList<String> getLinks(String html) {
        int pTag = html.indexOf("<p>");
        String parseString = html.substring(pTag+3);
        String p = "href=\\\"/wiki/(.*?)\\\"";
        Pattern htmlPattern = Pattern.compile(p);
        Matcher m = htmlPattern.matcher(parseString);
        ArrayList<String> urls = new ArrayList<>();
        String currentUrl;
        while (m.find()) {
            currentUrl = m.group().replace("\"", "").replace("href=", "");
            if (!currentUrl.contains(":") && !currentUrl.contains("#")) {
                urls.add(currentUrl);
            }
        }
        return urls;
    }
}