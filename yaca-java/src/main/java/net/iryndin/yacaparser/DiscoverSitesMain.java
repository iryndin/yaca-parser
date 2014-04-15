package net.iryndin.yacaparser;

import com.google.common.util.concurrent.*;
import net.iryndin.yacaparser.dto.RubricsWithSites;
import net.iryndin.yacaparser.dto.SimpleRubrics;
import net.iryndin.yacaparser.dto.YacaRubrics;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Parse all rubrics
 */
public class DiscoverSitesMain {

    static final String SITES_FNAME = "sites.json";

    static final ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(16));

    static final Set<String> rubricsProcessed = new HashSet<>();
    static final List<RubricsWithSites> sitesList = new CopyOnWriteArrayList<>();

    static final BlockingQueue<List<RubricsWithSites>> writeQueue = new ArrayBlockingQueue<>(1024);

    static final ExecutorService writerExecutorService = Executors.newSingleThreadExecutor();
    static final AtomicInteger rubricsProcessedQty = new AtomicInteger();
    static final AtomicInteger sitesQty = new AtomicInteger();

    static int totalProcessingTasks = 0;

    public static void main(String[] args) throws Exception {
        List<SimpleRubrics> rubrics = readRubrics();
        System.out.println(rubrics.size() + " rubrics read");

        //rubrics = rubrics.subList(0, 30);
        //System.out.println(rubrics.size() + " rubrics truncated");
        totalProcessingTasks = rubrics.size();

        readDataIfExist();

        writerExecutorService.submit(new WriterRunnable());

        for (SimpleRubrics r : rubrics) {
            String rubricsUrl = r.getUrl();
            if (rubricsProcessed.contains(rubricsUrl)) {
                System.out.println(rubricsUrl + " ALREADY PROCESSED");
                continue;
            }
            rubricsProcessed.add(rubricsUrl);

            ListenableFuture<RubricsWithSites> discoveredSitesFuture = executorService.submit(new SiteDiscoverCallable(rubricsUrl));
            Futures.addCallback(discoveredSitesFuture, new FutureCallback<RubricsWithSites>() {
                public void onSuccess(RubricsWithSites result) {
                    putNewDataAndWriteIt(result);
                    System.out.println(result.getUrl() + " COMPLETED");
                    System.out.println("rubrics done: " + rubricsProcessedQty.incrementAndGet());
                    System.out.println("sites done: " + sitesQty.addAndGet(result.getSites().size()));
                }
                public void onFailure(Throwable thrown) {
                    System.out.println(thrown);
                    rubricsProcessedQty.incrementAndGet();
                }
            });
        }

        executorService.shutdown();
        //System.out.println("executorService shutdown");
        writerExecutorService.shutdown();
        //System.out.println("writerExecutorService shutdown");
    }

    private static void readDataIfExist() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<RubricsWithSites> list = objectMapper.readValue(new File(SITES_FNAME), new TypeReference<List<RubricsWithSites>>() { });
            sitesList.addAll(list);
            for (RubricsWithSites r : list) {
                rubricsProcessed.add(r.getUrl());
                rubricsProcessedQty.incrementAndGet();
                sitesQty.addAndGet(r.getSites().size());
            }
            System.out.println("Data read OKAY: " + rubricsProcessedQty.get() + " rubrics, " + sitesQty.get() + " sites");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private static void putNewDataAndWriteIt(RubricsWithSites result) {
        sitesList.add(result);
        writeQueue.add(Collections.unmodifiableList(sitesList));
    }

    private static List<SimpleRubrics> readRubrics() throws IOException {
        ObjectMapper om = new ObjectMapper();
        InputStream in = DiscoverSitesMain.class.getClassLoader().getResourceAsStream("allRubrics.json");
        List<SimpleRubrics> list = om.readValue(in, new TypeReference<List<SimpleRubrics>>() { });
        return list;
    }

    static class WriterRunnable implements Runnable {

        private ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public void run() {
            //while (!Thread.currentThread().isInterrupted()) {
            while (rubricsProcessedQty.get() < totalProcessingTasks) {
                try {
                    List<RubricsWithSites> list = writeQueue.poll(1, TimeUnit.SECONDS);
                    if (list != null) {
                        dumpList(list);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }

            System.out.println("WriterRunnable completed");
        }

        private void dumpList(List<RubricsWithSites> list) throws IOException {
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(list);

            try(PrintWriter out = new PrintWriter(SITES_FNAME, "UTF-8")) {
                out.println(json);
            }
        }
    }
}
