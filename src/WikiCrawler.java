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
    static final String BASE_URL = "https://en.wikipedia.org";
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
    private HashSet<String> visited;
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
        q = new ArrayDeque<>();
        visited = new HashSet<>();
        this.topics = topics;
        links = new HashMap<>();
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
            if (visited.contains(currentPath)) {
                continue;
            }
            visited.add(currentPath);
            // Get page HTML
            String html = getPageText(currentPath);
            // Get the actual page content
            int pTag = html.indexOf("<p>");
            String parseString = html.substring(pTag+3);
            // Checking if page contains all the topics
            if (topics != null && topics.size() > 0) {
                boolean keepGoing = true;
                for (String s : topics) {
                    // Doesn't contain one of the topics exit
                    if (!parseString.toLowerCase().contains(s.toLowerCase())) {
                        keepGoing = false;
                        break;
                    }
                }
                // The real abort if the page doesn't contain things
                if (!keepGoing) {
                    continue;
                }
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
                added.add(link);
                q.add(link);
                appendToKey(currentPath, link);
            }
            pagesCount++;
            if (pagesCount > max) {
                writeGraph();
                return;
            }
        }
        writeGraph();
    }

    private void writeGraph() throws IOException {
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
        Thread.sleep(100);
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
        String pTagPattern = "<a[^>]* href=\"(.*?)\"";
        Pattern htmlPattern = Pattern.compile(pTagPattern);
        Matcher m = htmlPattern.matcher(parseString);
        ArrayList<String> urls = new ArrayList<>();
        String currentUrl;
        while (m.find()) {
            currentUrl = m.group().replace("\"", "")
                                  .replace("href=", "").replace(" ", "")
                                  .replace("<a", "");
            if (!currentUrl.contains(":") && !currentUrl.contains("#")) {
                urls.add(currentUrl);
            }
        }
        ArrayList<String> result = new ArrayList<>();
        for (String url : urls) {
            if (url.startsWith("/wiki")) {
                result.add(url);
            }
        }
        return result;
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