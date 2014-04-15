package net.iryndin.yacaparser;

import net.iryndin.yacaparser.dto.SimpleRubrics;
import net.iryndin.yacaparser.dto.YacaRubrics;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Parse all rubrics
 */
public class RubricsParserMain {

    static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static void main(String[] args) throws Exception {
        List<YacaRubrics> roots = readRoots();

        CountDownLatch latch = new CountDownLatch(roots.size());
        for (YacaRubrics i : roots) {
            executorService.submit(new YacaParserRunnable(i, latch));
        }

        latch.await();

        Map<String, YacaRubrics> allRubrics = new HashMap<>();
        putAllRubricsToMap(roots, allRubrics);
        writeRubrics(allRubrics);

        executorService.shutdownNow();

    }

    private static void writeRubrics(Map<String, YacaRubrics> allRubrics) throws IOException {
        List<SimpleRubrics> allRubricsList = toNodeList(allRubrics.values());
        ObjectMapper om = new ObjectMapper();
        String json = om.writerWithDefaultPrettyPrinter().writeValueAsString(allRubricsList);

        int noParentsQty=0;
        for (SimpleRubrics s : allRubricsList) {
            if (s.getParents().isEmpty()) {
                System.out.println(s.getUrl());
                noParentsQty++;
            }
        }

        System.out.println("Rubrics with no parents qty: " + noParentsQty);

        System.out.println("=============");

        int twoParentsQty =0;
        for (SimpleRubrics s : allRubricsList) {
            if (s.getParents().size()==2) {
                System.out.println(s.getUrl() + " " + printStrings(s.getParents()));
                twoParentsQty++;
            }
        }

        System.out.println("Rubrics with two parents qty: " + twoParentsQty);

        try(PrintWriter out = new PrintWriter("allRubrics.json", "UTF-8")) {
            out.println(json);
        }

        System.out.println("Total rubrics: " + allRubricsList.size());
    }

    private static String printStrings(List<String> parents) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        int i=0;
        int sz = parents.size();
        for (String s : parents) {
            i++;
            sb.append(s);
            if (i<sz) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private static List<SimpleRubrics> toNodeList(Collection<YacaRubrics> nodes) {
        List<SimpleRubrics> list = new ArrayList<>(nodes.size());
        for (YacaRubrics y : nodes) {
            SimpleRubrics s = createSimpleRubrics(y);
            list.add(s);
        }
        return list;
    }

    private static SimpleRubrics createSimpleRubrics(YacaRubrics y) {
        SimpleRubrics s = new SimpleRubrics(y.getName(), y.getUrl());
        if (y.getChildren()!= null && !y.getChildren().isEmpty()) {
            s.getChildren().addAll(y.getChildren().keySet());
        }
        if (y.getParents()!= null && !y.getParents().isEmpty()) {
            s.getParents().addAll(y.getParents().keySet());
        }
        return s;
    }

    private static void putAllRubricsToMap(Collection<YacaRubrics> rubrics, Map<String, YacaRubrics> map) {
        if (rubrics == null || rubrics.isEmpty()) {
            return;
        }
        for (YacaRubrics i : rubrics) {
            String url = i.getUrl();
            if (map.containsKey(url)) {
                YacaRubrics j = map.get(url);
                j.getParents().putAll(i.getParents());
            } else {
                map.put(url, i);
            }
        }

        for (YacaRubrics i : rubrics) {
            putAllRubricsToMap(i.getChildren().values(), map);
        }
    }

    private static List<YacaRubrics> readRoots() throws IOException {
        ObjectMapper om = new ObjectMapper();
        InputStream in = RubricsParserMain.class.getClassLoader().getResourceAsStream("root.json");
        List<SimpleRubrics> list1 = om.readValue(in, new TypeReference<List<SimpleRubrics>>() { });
        List<YacaRubrics> list = new ArrayList<>(list1.size());
        for (SimpleRubrics r : list1) {
            YacaRubrics y = convert(r);
            list.add(y);
        }
        return list;
    }

    private static YacaRubrics convert(SimpleRubrics r) {
        YacaRubrics y = new YacaRubrics(r.getName(), r.getUrl());
        return y;
    }
}
