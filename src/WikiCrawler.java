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

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class WikiCrawler {
    /**
     * Base URL of search
     */
    static final String BASE_URL = "http://web.cs.iastate.edu/~pavan";
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
     * Hash map of Dest > list of pages that link to it
     */
    private HashMap<String, ArrayList<String>> links;

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
        q = new LinkedList<>();
        visited = new ArrayList<>();
        this.topics = topics;
        links = new HashMap<>();
    }

    /**
     * Crawls the BASE_URL based on seedUrl and BASE_URL
     */
    public void crawl() throws IOException, InterruptedException {
        // No topics don't scan
        if (topics.size() <= 0) {
            File f = new File(fileName);
            f.createNewFile();
            return;
        }

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
            // The real abort if the page doesn't contain things
            if (!keepGoing) {
                continue;
            }
            // Add the pages that linked to me to the graph
            ArrayList<String> pagesLinkedToMe = links.get(currentPath);
            if (pagesLinkedToMe != null) {
                for (String page : pagesLinkedToMe) {
                    graph.add(new AbstractMap.SimpleEntry<>(page, currentPath));
                }
            }
            // Contains all the topics, get all links on the page
            ArrayList<String> linksHTML = getLinks(html);
            ArrayList<String> added = new ArrayList<>();
            for (String link : linksHTML) {
                if (added.contains(link) || visited.contains(link)) {
                    continue;
                }
                q.add(link);
                appendToKey(currentPath, link);
                added.add(link);
            }
            pagesCount++;
            if (pagesCount >= max) {
                return;
            }
        }
        FileWriter fw = new FileWriter(fileName);
        for (Map.Entry entry : graph) {
            fw.write(entry.getKey() + " " + entry.getValue() + "\n");
        }
        fw.flush();
        fw.close();

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
        // Make a URL
        URL theURL = new URL(BASE_URL + path);
        // Get the stream
        InputStream inputStream = theURL.openStream();
        // Make a reader
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        // Build page string
        StringBuilder stringBuilder = new StringBuilder();

        String line;

        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        // Add one to the page count
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
        String pTagPattern = "href=\\\"/wiki/(.*?)\\\"";
        Pattern htmlPattern = Pattern.compile(pTagPattern);
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

    /**
     * Add link to dest list
     *
     * @param source source page
     * @param dest dest page
     */
    private void appendToKey(String source, String dest) {
        links.computeIfAbsent(dest, k -> new ArrayList<>());
        links.get(dest).add(source);
    }
}