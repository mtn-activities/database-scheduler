package mtn.activities.database.scheduler.models.entities;

import javax.persistence.*;

@Entity
@Table(name="weekly_day")
@IdClass(WeeklyDayId.class)
@NamedQueries(value =
        {
                @NamedQuery(name = "WeeklyDayEntity.getByWeekly",
                        query = "SELECT wd FROM WeeklyDayEntity wd WHERE wd.wId = :identity")
        })
public class WeeklyDayEntity {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wId")
    private WeeklyEntity wId;

    @Id
    private Integer day;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section")
    private SectionEntity section;

    @Column(name="position")
    private Integer position;

    public WeeklyEntity getWId() {
        return wId;
    }

    public void setWId(WeeklyEntity wId) {
        this.wId = wId;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public SectionEntity getSection() {
        return section;
    }

    public void setSection(SectionEntity section) {
        this.section = section;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
