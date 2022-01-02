package mtn.activities.database.scheduler.lib;

import java.util.List;

public class Section {
    private Integer sectionId;
    private String sectionName;
    private List<SectionDay> sectionDays;

    public Integer getSectionId() {
        return sectionId;
    }

    public void setSectionId(Integer sectionId) {
        this.sectionId = sectionId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public List<SectionDay> getSectionDays() {
        return sectionDays;
    }

    public void setSectionDays(List<SectionDay> sectionDays) {
        this.sectionDays = sectionDays;
    }
}
