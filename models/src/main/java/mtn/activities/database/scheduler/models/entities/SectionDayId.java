package mtn.activities.database.scheduler.models.entities;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;

@Embeddable
public class SectionDayId implements Serializable{
    private Integer sId;

    private Integer day;

    public Integer getSId() {
        return sId;
    }

    public void setSId(Integer sId) {
        this.sId = sId;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    @Override
    public int hashCode() {
        return (Objects.hash(this.getSId(), this.getDay()));
    }

    @Override
    public boolean equals(Object otherID) {
        if (this == otherID) {
            return true;
        }
        if (!(otherID instanceof SectionDayId)) {
            return false;
        }

        SectionDayId other = (SectionDayId) otherID;
        return Objects.equals(this.getSId(), other.getSId()) && Objects.equals(this.getDay(), other.getDay());
    }
}
