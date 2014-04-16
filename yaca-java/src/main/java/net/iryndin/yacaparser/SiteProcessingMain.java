package net.iryndin.yacaparser;

import net.iryndin.yacaparser.dto.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Assign to each site one or more categories
 * Remove duplicates
 */
public class SiteProcessingMain {

    public static void main(String[] args) throws IOException {
        List<RubricsWithSites> list = readSites();
        printStats(list);
        Map<String, SimpleRubricsEx> rubrics = readAndOrganizeRubrics();
        List<SiteWithRubrics> sites = processSites(list, rubrics);
        writeResult(sites);
    }

    private static void writeResult(List<SiteWithRubrics> list) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(list);

        try(PrintWriter out = new PrintWriter("classifiedSites.json", "UTF-8")) {
            out.println(json);
        }

        System.out.println("Sites written: " + list.size());
    }

    private static List<SiteWithRubrics> processSites(List<RubricsWithSites> list, Map<String, SimpleRubricsEx> rubrics) {
        Map<Site, Set<String>> map = new HashMap<>(1024*256);
        for (RubricsWithSites r : list) {
            String category = r.getUrl();
            for (Site site : r.getSites()) {
                if (!map.containsKey(site)) {
                    map.put(site, new TreeSet<String>());
                }
                map.get(site).add(category);
            }
        }
        System.out.println("total sites qty (w/o duplicates): " + map.size());

        //
        //
        //

        List<SiteWithRubrics> result = new ArrayList<>(map.size());

        for (Map.Entry<Site, Set<String>> e : map.entrySet()) {
            Set<String> directParents = e.getValue();
            Set<String> allParents = new TreeSet<>();
            for (String p : directParents) {
                SimpleRubricsEx rub = rubrics.get(p);
                allParents.addAll(rub.getAllParents());
            }

            result.add(new SiteWithRubrics(e.getKey(), new ArrayList<>(allParents)));
        }

        return result;
    }

    private static void printStats(List<RubricsWithSites> list) {
        System.out.println("Rubrics: " + list.size());
        int sitesQty=0;
        for (RubricsWithSites r : list) {
            sitesQty += r.getSites().size();
        }
        System.out.println("Sites qty: " + sitesQty);
    }

    private static Map<String, SimpleRubricsEx> readAndOrganizeRubrics() throws IOException {
        List<SimpleRubrics> allRubrics = DiscoverSitesMain.readRubrics();
        Map<String, SimpleRubrics> rubricsMap = new HashMap<>();
        for (SimpleRubrics r : allRubrics) {
            rubricsMap.put(r.getUrl(), r);
        }

        Map<String, SimpleRubricsEx> map2 = new HashMap<>();

        //int i=0;
        for (SimpleRubrics r : rubricsMap.values()) {
            //i++;
            //System.out.println(i);
            Set<String> allParents = new TreeSet<>();
            List<String> queue = new LinkedList<>(r.getParents());
            while (!queue.isEmpty()) {
                String q = queue.remove(0);
                SimpleRubrics par = rubricsMap.get(q);
                if (!allParents.contains(q)) {
                    queue.addAll(par.getParents());
                }
                allParents.add(q);
            }

            SimpleRubricsEx o = new SimpleRubricsEx(r);
            o.getAllParents().addAll(allParents);
            map2.put(r.getUrl(), o);
        }

        return map2;
    }

    private static List<RubricsWithSites> readSites() throws IOException {
        ObjectMapper om = new ObjectMapper();
        List<RubricsWithSites> list = om.readValue(new File("sites.json"), new TypeReference<List<RubricsWithSites>>() { });
        return list;
    }

}
