package net.iryndin.yacaparser.dto;

import java.util.List;

/**
 * Created by iryndin on 16.04.14.
 */
public class SiteWithRubrics extends Site {

    public SiteWithRubrics() {
    }

    public SiteWithRubrics(Site s, List<String> rubrics) {
        super(s.getUrl(), s.getTitle(), s.getCy());
        this.rubrics = rubrics;
    }

    private List<String> rubrics;

    public List<String> getRubrics() {
        return rubrics;
    }

    public void setRubrics(List<String> rubrics) {
        this.rubrics = rubrics;
    }
}
