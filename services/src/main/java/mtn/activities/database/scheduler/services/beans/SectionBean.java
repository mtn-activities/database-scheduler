package mtn.activities.database.scheduler.services.beans;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;

import mtn.activities.database.scheduler.lib.Section;
import mtn.activities.database.scheduler.lib.SectionDay;
import mtn.activities.database.scheduler.models.entities.SectionDayEntity;
import mtn.activities.database.scheduler.models.entities.SectionDayId;
import mtn.activities.database.scheduler.models.entities.SectionEntity;
import mtn.activities.database.scheduler.models.converters.SectionConverter;

@RequestScoped
public class SectionBean {

    private final Logger log = Logger.getLogger(SectionBean.class.getName());

    @Inject
    private EntityManager em;

    // generic GET query for all items
    public List<Section> getSections() {
        TypedQuery<SectionEntity> query = em.createNamedQuery(
                "SectionEntity.getAll", SectionEntity.class);

        List<SectionEntity> resultList = query.getResultList();
        return resultList.stream().map(SectionConverter::toDto).collect(Collectors.toList());
    }

    // GET request with parameters
    public List<Section> getSectionsFilter(UriInfo uriInfo) {
        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, SectionEntity.class, queryParameters).stream()
                .map(SectionConverter::toDto).collect(Collectors.toList());
    }

    // POST
    public Section createSection(Section section) {
        SectionEntity sectionEntity = SectionConverter.toEntity(section);
        List<SectionDayEntity> sectionDayEntities = new ArrayList<>();
        if(section.getSectionDays() != null) {
            for(SectionDay sectionDay : section.getSectionDays())
                sectionDayEntities.add(SectionConverter.toDayEntity(sectionDay, sectionEntity));
        }

        try {
            beginTx();
            em.persist(sectionEntity);
            for(SectionDayEntity sectionDayEntity : sectionDayEntities)
                em.persist(sectionDayEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        if (sectionEntity.getId() == null)
            return null;

        return SectionConverter.toDto(sectionEntity, sectionDayEntities);
    }

    // GET by id
    public Section getSection(Integer sectionId) {
        return getSection(sectionId, false);
    }

    public Section getSection(Integer sectionId, boolean fullProjection) {
        SectionEntity sectionEntity = em.find(SectionEntity.class, sectionId);

        if(sectionEntity == null)
            return null;

        if(fullProjection) {
            TypedQuery<SectionDayEntity> query = em.createNamedQuery("SectionDayEntity.getBySection", SectionDayEntity.class);
            query.setParameter("identity", sectionEntity);
            List<SectionDayEntity> sectionDayEntities = query.getResultList();
            return SectionConverter.toDto(sectionEntity, sectionDayEntities);
        } else
            return SectionConverter.toDto(sectionEntity);
    }

    // PUT by id
    public Section putSection(Integer sectionId, Section section) {
        SectionEntity sectionEntity = em.find(SectionEntity.class, sectionId);
        if (sectionEntity == null)
            return null;

        List<SectionDayEntity> sectionDayEntities = new ArrayList<>();
        if(section.getSectionDays() != null) {
            for(SectionDay sectionDay : section.getSectionDays())
                sectionDayEntities.add(SectionConverter.toDayEntity(sectionDay, sectionEntity));
        }

        SectionEntity updatedSectionEntity = SectionConverter.toEntity(section);

        try {
            beginTx();
            updatedSectionEntity.setId(sectionEntity.getId());
            updatedSectionEntity = em.merge(updatedSectionEntity);
            sectionDayEntities = sectionDayEntities.stream().map(em::merge).collect(Collectors.toList());
            commitTx();
        } catch (Exception e) {
            rollbackTx();
            return null;
        }

        return SectionConverter.toDto(updatedSectionEntity, sectionDayEntities);
    }

    // DELETE by id
    public boolean deleteSection(Integer sectionId) {
        SectionEntity section = em.find(SectionEntity.class, sectionId);
        if(section == null)
            return false;

        TypedQuery<SectionDayEntity> query = em.createNamedQuery("SectionDayEntity.getBySection", SectionDayEntity.class);
        query.setParameter("identity", section);
        List<SectionDayEntity> sectionDayEntities = query.getResultList();

        try {
            beginTx();
            em.remove(section);
            for(SectionDayEntity sectionDayEntity : sectionDayEntities)
                em.remove(sectionDayEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
            return false;
        }

        return true;
    }

    // POST schedule day for section
    public boolean createSectionDay(Integer sectionId, SectionDay sectionDay) {
        SectionEntity section = em.find(SectionEntity.class, sectionId);
        if(section == null)
            return false;
        SectionDayEntity sectionDayEntity = SectionConverter.toDayEntity(sectionDay, section);

        try {
            beginTx();
            em.persist(sectionDayEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return sectionDayEntity.getSId() != null;
    }

    // GET a schedule day for section
    public SectionDay getSectionDay(Integer sectionId, Integer weekday) {
        SectionDayId sectionDayId = new SectionDayId();
        sectionDayId.setSId(sectionId);
        sectionDayId.setDay(weekday);
        SectionDayEntity sectionDayEntity = em.find(SectionDayEntity.class, sectionDayId);

        if(sectionDayEntity == null)
            return null;
        else
            return SectionConverter.toDayDto(sectionDayEntity);
    }

    // PUT a schedule day for section
    public SectionDay putSectionDay(Integer sectionId, Integer weekday, SectionDay sectionDay) {
        SectionEntity section = em.find(SectionEntity.class, sectionId);
        if(section == null)
            return null;

        SectionDayId sectionDayId = new SectionDayId();
        sectionDayId.setSId(sectionId);
        sectionDayId.setDay(weekday);
        SectionDayEntity sectionDayEntity = em.find(SectionDayEntity.class, sectionDayId);
        if(sectionDayEntity == null)
            return null;

        SectionDayEntity updatedSectionDayEntity = SectionConverter.toDayEntity(sectionDay, section);

        try {
            beginTx();
            updatedSectionDayEntity.setSId(sectionDayEntity.getSId());
            updatedSectionDayEntity.setDay(sectionDayEntity.getDay());
            updatedSectionDayEntity = em.merge(updatedSectionDayEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
            return null;
        }

        return SectionConverter.toDayDto(updatedSectionDayEntity);
    }

    // DELETE a schedule day for section
    public boolean deleteSectionDay(Integer sectionId, Integer weekday) {
        SectionDayId sectionDayId = new SectionDayId();
        sectionDayId.setSId(sectionId);
        sectionDayId.setDay(weekday);
        SectionDayEntity sectionDayEntity = em.find(SectionDayEntity.class, sectionDayId);
        if(sectionDayEntity == null)
            return false;

        try {
            beginTx();
            em.remove(sectionDayEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
            return false;
        }

        return true;
    }

    private void beginTx() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    private void commitTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}