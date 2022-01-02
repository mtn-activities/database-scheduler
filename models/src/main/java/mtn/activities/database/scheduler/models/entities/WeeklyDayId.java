package mtn.activities.database.scheduler.models.entities;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class WeeklyDayId implements Serializable {
    private Integer wId;

    private Integer day;

    public Integer getWId() {
        return wId;
    }

    public void setWId(Integer wId) {
        this.wId = wId;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    @Override
    public int hashCode() {
        return (Objects.hash(this.getWId(), this.getDay()));
    }

    @Override
    public boolean equals(Object otherID) {
        if (this == otherID) {
            return true;
        }
        if (!(otherID instanceof WeeklyDayId)) {
            return false;
        }

        WeeklyDayId other = (WeeklyDayId) otherID;
        return Objects.equals(this.getWId(), other.getWId()) && Objects.equals(this.getDay(), other.getDay());
    }
}
