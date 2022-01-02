package mtn.activities.database.scheduler.models.entities;

import javax.persistence.*;

@Entity
@Table(name="section_name")
@NamedQueries(value =
        {
                @NamedQuery(name = "SectionEntity.getAll",
                        query = "SELECT s FROM SectionEntity s")
        })
public class SectionEntity {
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
