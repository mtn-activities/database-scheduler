package mtn.activities.database.scheduler.models.entities;

import javax.persistence.*;

@Entity
@Table(name="section_day")
@IdClass(SectionDayId.class)
@NamedQueries(value =
        {
                @NamedQuery(name = "SectionDayEntity.getBySection",
                        query = "SELECT sd FROM SectionDayEntity sd WHERE sd.sId = :identity"),
                @NamedQuery(name = "SectionDayEntity.getMaxPosByDay",
                        query = "SELECT max(sd.position) FROM SectionDayEntity sd WHERE sd.day = :day")
        })
public class SectionDayEntity {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sId")
    private SectionEntity sId;

    @Id
    private Integer day;

    @Column(name="position")
    private Integer position;

    public SectionEntity getSId() {
        return sId;
    }

    public void setSId(SectionEntity sId) {
        this.sId = sId;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
