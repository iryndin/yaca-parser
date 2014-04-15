package net.iryndin.yacaparser.dto;

import java.util.List;

/**
 * User: iryndin
 * Date: 15.04.14 2:52
 */
public class RubricsWithSites {
    private String url;
    private List<Site> sites;

    public RubricsWithSites() {
    }

    public RubricsWithSites(String url, List<Site> sites) {
        this.url = url;
        this.sites = sites;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Site> getSites() {
        return sites;
    }

    public void setSites(List<Site> sites) {
        this.sites = sites;
    }
}
