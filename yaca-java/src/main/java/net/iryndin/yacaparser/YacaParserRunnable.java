package net.iryndin.yacaparser;

import net.iryndin.yacaparser.dto.YacaRubrics;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class YacaParserRunnable implements Runnable {

    static final int TIMEOUT = 50000;

    private final YacaRubrics root;
    private final Set<String> parsedUrls;
    private final CountDownLatch latch;

    public YacaParserRunnable(YacaRubrics root, CountDownLatch latch) {
        this.root = root;
        this.latch = latch;
        this.parsedUrls = new HashSet<>(1024*8);
    }

    @Override
    public void run() {
        String url = root.getUrl();
        final long startMillis = System.currentTimeMillis();
        try {
            System.out.println("Start parsing: " + url);
            parseChildren(root);
        }  catch (Exception e) {
            e.printStackTrace();
        } finally {
            final long elapsedMillis = System.currentTimeMillis() - startMillis;
            final long seconds = elapsedMillis/1000;
            System.out.println("Complete parse (" + seconds + " secs): " + url);
            latch.countDown();
        }
    }

    void parseChildren(YacaRubrics parent) throws Exception {
        String url = parent.getUrl();

        // parse children for only those not parsed yet
        if (!parsedUrls.contains(url)) {
            parsedUrls.add(url);
            //System.out.println(url);

            Set<YacaRubrics> children = getFirstLevelChildren(url);

            if (!children.isEmpty()) {
                for (YacaRubrics ch : children) {
                    parent.getChildren().put(ch.getUrl(), ch);
                    ch.getParents().put(parent.getUrl(), parent);
                    parseChildren(ch);
                }
            }
        }
    }

    public static Set<YacaRubrics> getFirstLevelChildren(String url) throws IOException {
        Document doc = Jsoup.parse(new URL(url), TIMEOUT);
        Elements elems = doc.select("a[href].b-rubric__list__item__link");

        Set<YacaRubrics> children = new HashSet<>();

        for (Element e : elems) {
            String name = e.text().trim();
            String href = e.attr("abs:href");

            if (!href.startsWith("http://yaca.yandex.ru")) {
                continue;
            }

            children.add(new YacaRubrics(name, href));
        }

        return children;
    }
}
