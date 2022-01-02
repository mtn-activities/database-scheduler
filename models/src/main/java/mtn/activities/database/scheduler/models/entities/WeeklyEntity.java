package mtn.activities.database.scheduler.models.entities;

import javax.persistence.*;

@Entity
@Table(name="weekly_name")
@NamedQueries(value =
        {
                @NamedQuery(name = "WeeklyEntity.getAll",
                        query = "SELECT w FROM WeeklyEntity w")
        })
public class WeeklyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="name")
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
