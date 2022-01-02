package mtn.activities.database.scheduler.lib;

import java.util.List;

public class Weekly {
    private Integer weeklyId;
    private String weeklyName;
    private List<WeeklyDay> weeklyDays;

    public Integer getWeeklyId() {
        return weeklyId;
    }

    public void setWeeklyId(Integer weeklyId) {
        this.weeklyId = weeklyId;
    }

    public String getWeeklyName() {
        return weeklyName;
    }

    public void setWeeklyName(String weeklyName) {
        this.weeklyName = weeklyName;
    }

    public List<WeeklyDay> getWeeklyDays() {
        return weeklyDays;
    }

    public void setWeeklyDays(List<WeeklyDay> weeklyDays) {
        this.weeklyDays = weeklyDays;
    }
}
