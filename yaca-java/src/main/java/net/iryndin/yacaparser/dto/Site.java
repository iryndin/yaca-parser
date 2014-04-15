package net.iryndin.yacaparser.dto;

public class Site {
    private String url;
    private String title;
    private int cy;

    public Site() {
    }

    public Site(String url, String title, int cy) {
        this.url = url;
        this.title = title;
        this.cy = cy;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCy() {
        return cy;
    }

    public void setCy(int cy) {
        this.cy = cy;
    }

    @Override
    public String toString() {
        return "Site{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", cy=" + cy +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Site site = (Site) o;

        if (url != null ? !url.equals(site.url) : site.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return url != null ? url.hashCode() : 0;
    }
}
