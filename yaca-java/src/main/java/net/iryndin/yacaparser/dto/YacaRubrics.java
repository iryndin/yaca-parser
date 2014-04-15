package net.iryndin.yacaparser.dto;

import java.util.HashMap;
import java.util.Map;

public class YacaRubrics {

    private final String name;
    private final String url;
    private final Map<String, YacaRubrics> parents = new HashMap<>();
    private final Map<String, YacaRubrics> children = new HashMap<>();

    public YacaRubrics(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, YacaRubrics> getParents() {
        return parents;
    }

    public Map<String, YacaRubrics> getChildren() {
        return children;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        YacaRubrics that = (YacaRubrics) o;

        if (!url.equals(that.url)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }

    @Override
    public String toString() {
        return "YacaRubrics{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", parents=" + parents.size() +
                ", children=" + children.size() +
                '}';
    }
}