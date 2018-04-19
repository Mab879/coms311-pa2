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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


public class WikiCrawler {
    static final String BASE_URL = "https://en.wikipedia.org/";
    private String seedUrl;
    private int max;
    private String fileName;
    private ArrayList<String> topics;
    private Queue<String> q = new LinkedList<>();

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
    }

    /**
     * Crawls the BASE_URL based on seedUrl and BASE_URL
     */
    public void crawl() throws IOException {
        q.add(seedUrl);
        while (!q.isEmpty()) {
            String currentPath = q.remove();
            String html = getPageText(currentPath);
        }
    }

    /**
     * The the text of the given page
     *
     * @param path relative url
     * @return text of the HTML page
     */
    private String getPageText(String path) throws IOException {
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

        return stringBuilder.toString();
    }

    /**
     * Get the links from the page
     *
     * @param html html of the page as string
     * @return a list of links
     */
    private ArrayList<String> getLinks(String html) {


    }
}