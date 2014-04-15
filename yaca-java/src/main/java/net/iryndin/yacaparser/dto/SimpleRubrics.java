package net.iryndin.yacaparser.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleRubrics {

    private String name;
    private String url;
    private final List<String> parents = new ArrayList<>();
    private final List<String> children = new ArrayList<>();

    public SimpleRubrics() {
    }

    public SimpleRubrics(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getParents() {
        return parents;
    }

    public List<String> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return "SimpleRubrics{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", parents=" + parents +
                ", children=" + children +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleRubrics that = (SimpleRubrics) o;

        if (url != null ? !url.equals(that.url) : that.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return url != null ? url.hashCode() : 0;
    }
}
