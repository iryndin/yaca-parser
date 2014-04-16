package net.iryndin.yacaparser.dto;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by iryndin on 16.04.14.
 */
public class SimpleRubricsEx extends SimpleRubrics {

    private final Set<String> allParents = new TreeSet<>();

    public SimpleRubricsEx() {
    }

    public SimpleRubricsEx(SimpleRubrics sr) {
        super(sr.getName(), sr.getUrl());
        getParents().addAll(sr.getParents());
        getChildren().addAll(sr.getChildren());
    }

    public Set<String> getAllParents() {
        return allParents;
    }

    @Override
    public String toString() {
        return "SimpleRubricsEx{" +
                "name='" + getName() + '\'' +
                ", url='" + getUrl() + '\'' +
                ", parents=" + getParents()+
                ", children=" + getChildren() +
                ", allParents=" + allParents +
                '}';
    }
}
