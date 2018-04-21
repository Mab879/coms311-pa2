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
    public static final String BASE_URL = "https://en.wikipedia.org";
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
     * Total number pages crawled
     */
    private int requestCount = 0;
    /**
     * Number of pages Crawled (vertices on the graph)
     */
    private int pagesCount;
    /**
     * List of Urls Crawled
     */
    private HashSet<String> visited = new HashSet<>();
    /**
     *  A list of Key Value pairs of entries
     */
    private ArrayList<Map.Entry<String, String>> graph = new ArrayList<>();
    /**
     * Hash map of Dest > list of pages that link to it
     */
    private HashMap<String, ArrayList<String>> links = new HashMap<>();

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
        this.topics = topics;
    }

    /**
     * Crawls the BASE_URL based on seedUrl and BASE_URL
     */
    public void crawl() {
        Queue<String> q = new LinkedList<>();

        // Add the seed url to the queue
        q.add(seedUrl);
        visited.add(seedUrl);
        while (!q.isEmpty() && pagesCount < max) {
            // Remove from the top of the queue
            String currentPath = q.remove();
            String html;
            try {
                // Get page HTML
                html = getPageText(currentPath);
            } catch (InterruptedException | IOException e) {
                System.err.println("Problem downloading URL: " + currentPath);
                e.printStackTrace();
                return;
            }
            // Get the actual page content after the first plain <p> tag
            String parseString = html.substring(html.indexOf("<p>") + 3).toLowerCase();

            // Checking if page contains all the topics
            boolean hasAllTopics = true;
            for (String s : topics) {
                // Doesn't contain one of the topics exit
                if (!parseString.contains(s.toLowerCase())) {
                    hasAllTopics = false;
                    break;
                }
            }

            // If the page does not have all the topics, do not process it further
            if (hasAllTopics) {
                // Add the pages that linked to me to the graph
                ArrayList<String> pagesLinkedToMe = links.get(currentPath);
                if (pagesLinkedToMe != null) {
                    for (String page : pagesLinkedToMe) {
                        graph.add(new AbstractMap.SimpleEntry<>(page, currentPath));
                    }
                }

                // Enqueue all links on the page
                ArrayList<String> linksHTML = getLinks(html);
                for (String link : linksHTML) {
                    // If the URL is not visited, add it to the queue
                    if (!visited.contains(link)) {
                        q.add(link);
                        visited.add(link);
                        appendToLinksList(currentPath, link);
                    }
                }
                pagesCount++;
            }
        }
        writeGraph();
    }

    /**
     * Writes the graph to the filesystem. The first line is the number of vertices. The rest of the lines are directed
     * edges.
     */
    private void writeGraph() {
        try (FileWriter fw = new FileWriter(fileName)) {
            // Write the first line of the vertex count
            fw.write(Integer.toString(pagesCount) + "\n");

            // Write each edge
            for (Map.Entry entry : graph) {
                fw.write(entry.getKey() + " " + entry.getValue() + "\n");
            }
            fw.flush();
        } catch (IOException e) {
            System.err.println("Problem writing the graph to the filesystem.");
            e.printStackTrace();
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
        // Make a URL
        URL theURL = new URL(BASE_URL + path);
        // Get the stream
        InputStream inputStream = theURL.openStream();
        // Make a reader
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        // Build page string
        StringBuilder stringBuilder = new StringBuilder();

        // Read all lines
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append('\n');
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
    private void appendToLinksList(String source, String dest) {
        links.computeIfAbsent(dest, k -> new ArrayList<>());
        links.get(dest).add(source);
    }
}