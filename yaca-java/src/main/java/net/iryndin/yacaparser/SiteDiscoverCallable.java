package net.iryndin.yacaparser;

import net.iryndin.yacaparser.dto.RubricsWithSites;
import net.iryndin.yacaparser.dto.Site;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;

public class SiteDiscoverCallable implements Callable<RubricsWithSites> {

    static final int TIMEOUT = 50000;

    private final String url;

    private final Set<String> discoveredSites = new HashSet<>();
    private final List<Site> allSites = new ArrayList<>();

    private final Queue<String> pagesToProcessQueue = new ArrayDeque<>();
    private final Set<String> discoveredPages = new HashSet<>();

    public SiteDiscoverCallable(String url) {
        this.url = url;
        addNewPageForProcessing(url);

    }

    private void addNewPageForProcessing(String url) {
        if (!discoveredPages.contains(url)) {
            pagesToProcessQueue.add(url);
            discoveredPages.add(url);
        }
    }


    @Override
    public RubricsWithSites call() throws Exception {
        while (!pagesToProcessQueue.isEmpty()) {
            String pageUrl = pagesToProcessQueue.poll();
            processPage(pageUrl);
        }

        return new RubricsWithSites(url, allSites);
    }

    private void processPage(String pageUrl) throws Exception {
        System.out.println("Page: " + pageUrl);
        Document doc = Jsoup.parse(new URL(pageUrl), TIMEOUT);
        discoverPageSites(doc);
        discoverPages(doc);
    }

    private void discoverPages(Document doc) {
        Elements elems = doc.select("a[href].b-pager__page");

        for (Element a : elems) {
            String pageUrl = a.attr("abs:href");
            addNewPageForProcessing(pageUrl);
        }
    }

    private void discoverPageSites(Document doc) {
        Elements elems = doc.select("li.b-result__item");

        if (!elems.isEmpty()) {
            List<Site> sites = new ArrayList<>();

            for (Element li : elems) {
                Element a = li.select("a.b-result__name").first();
                int cy = 0;
                // parse CY (some sites can be without CY (i.e. links to Apple AppStore apps)
                {
                    Elements quoteLis = li.select("span.b-result__quote");
                    if (!quoteLis.isEmpty()) {
                        String cyText = quoteLis.first().text();
                        String s = cyText.replaceAll("\\D", "");
                        try {
                            cy = Integer.parseInt(s);
                        } catch (NumberFormatException nfe) {
                            //
                        }
                    }
                }
                sites.add(new Site(a.attr("abs:href"), a.text(), cy));
            }

            addSitesFromPage(sites);
        }
    }

    private void addSitesFromPage(List<Site> sites) {
        for (Site s : sites) {
            String url = s.getUrl();
            if (!discoveredSites.contains(url)) {
                discoveredSites.add(url);
                allSites.add(s);
            }
        }
    }
}
