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


import mtn.activities.database.scheduler.lib.Weekly;
import mtn.activities.database.scheduler.lib.WeeklyDay;
import mtn.activities.database.scheduler.models.converters.WeeklyConverter;
import mtn.activities.database.scheduler.models.entities.*;

@RequestScoped
public class WeeklyBean {

    private final Logger log = Logger.getLogger(WeeklyBean.class.getName());

    @Inject
    private EntityManager em;

    // generic GET query for all items
    public List<Weekly> getWeeklies() {
        TypedQuery<WeeklyEntity> query = em.createNamedQuery(
                "WeeklyEntity.getAll", WeeklyEntity.class);

        List<WeeklyEntity> resultList = query.getResultList();
        return resultList.stream().map(WeeklyConverter::toDto).collect(Collectors.toList());
    }

    // GET request with parameters
    public List<Weekly> getWeekliesFilter(UriInfo uriInfo) {
        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, WeeklyEntity.class, queryParameters).stream()
                .map(WeeklyConverter::toDto).collect(Collectors.toList());
    }

    // POST
    public Weekly createWeekly(Weekly weekly) {
        WeeklyEntity weeklyEntity = WeeklyConverter.toEntity(weekly);
        List<WeeklyDayEntity> weeklyDayEntities = new ArrayList<>();
        if(weekly.getWeeklyDays() != null) {
            for (WeeklyDay weeklyDay : weekly.getWeeklyDays()) {
                Integer sectionId = weeklyDay.getSectionId();
                SectionEntity sectionEntity = em.find(SectionEntity.class, sectionId);
                if(sectionEntity != null)
                    weeklyDayEntities.add(WeeklyConverter.toDayEntity(weeklyDay, weeklyEntity, sectionEntity));
            }
        }

        try {
            beginTx();
            em.persist(weeklyEntity);
            for(WeeklyDayEntity weeklyDayEntity : weeklyDayEntities)
                em.persist(weeklyDayEntity);
            commitTx();
        }
        catch (Exception e) {
            rollbackTx();
        }

        if (weeklyEntity.getId() == null)
            return null;

        return WeeklyConverter.toDto(weeklyEntity, weeklyDayEntities);
    }

    // GET by id
    public Weekly getWeekly(Integer weeklyId) {
        return getWeekly(weeklyId, false);
    }

    public Weekly getWeekly(Integer weeklyId, boolean fullProjection) {
        WeeklyEntity weeklyEntity = em.find(WeeklyEntity.class, weeklyId);

        if (weeklyEntity == null)
            return null;

        if(fullProjection) {
            TypedQuery<WeeklyDayEntity> query = em.createNamedQuery("WeeklyDayEntity.getByWeekly", WeeklyDayEntity.class);
            query.setParameter("identity", weeklyEntity);
            List<WeeklyDayEntity> weeklyDayEntities = query.getResultList();
            return WeeklyConverter.toDto(weeklyEntity, weeklyDayEntities);
        } else
            return WeeklyConverter.toDto(weeklyEntity);
    }

    // PUT by id
    public Weekly putWeekly(Integer weeklyId, Weekly weekly) {
        WeeklyEntity weeklyEntity = em.find(WeeklyEntity.class, weeklyId);
        if (weeklyEntity == null)
            return null;

        List<WeeklyDayEntity> weeklyDayEntities = new ArrayList<>();
        if(weekly.getWeeklyDays() != null) {
            for(WeeklyDay weeklyDay : weekly.getWeeklyDays()) {
                Integer sectionId = weeklyDay.getSectionId();
                SectionEntity sectionEntity = em.find(SectionEntity.class, sectionId);
                if(sectionEntity != null)
                    weeklyDayEntities.add(WeeklyConverter.toDayEntity(weeklyDay, weeklyEntity, sectionEntity));
            }
        }

        WeeklyEntity updatedWeeklyEntity = WeeklyConverter.toEntity(weekly);

        try {
            beginTx();
            updatedWeeklyEntity.setId(weeklyEntity.getId());
            updatedWeeklyEntity = em.merge(updatedWeeklyEntity);
            weeklyDayEntities = weeklyDayEntities.stream().map(em::merge).collect(Collectors.toList());
            commitTx();
        }
        catch (Exception e) {
            rollbackTx();
            return null;
        }

        return WeeklyConverter.toDto(updatedWeeklyEntity, weeklyDayEntities);
    }

    // DELETE by id
    public boolean deleteWeekly(Integer weeklyId) {
        WeeklyEntity weekly = em.find(WeeklyEntity.class, weeklyId);
        if(weekly == null)
            return false;

        TypedQuery<WeeklyDayEntity> query = em.createNamedQuery("WeeklyDayEntity.getByWeekly", WeeklyDayEntity.class);
        query.setParameter("identity", weekly);
        List<WeeklyDayEntity> weeklyDayEntities = query.getResultList();

        try {
            beginTx();
            em.remove(weekly);
            for(WeeklyDayEntity weeklyDayEntity : weeklyDayEntities)
                em.remove(weeklyDayEntity);
            commitTx();
            }
            catch (Exception e) {
                rollbackTx();
                return false;
            }

        return true;
    }

    // check if section id is valid
    public boolean validateSection(Integer sectionId) {
        if(sectionId == null)
            return true;
        SectionEntity sectionEntity = em.find(SectionEntity.class, sectionId);
        return sectionEntity != null;
    }

    // POST schedule day for weekly
    public boolean createWeeklyDay(Integer weeklyId, WeeklyDay weeklyDay) {
        WeeklyEntity weekly = em.find(WeeklyEntity.class, weeklyId);
        if(weekly == null)
            return false;

        Integer sectionId = weeklyDay.getSectionId();
        SectionEntity section = em.find(SectionEntity.class, sectionId);
        if(section == null)
            return false;

        WeeklyDayEntity weeklyDayEntity = WeeklyConverter.toDayEntity(weeklyDay, weekly, section);
        try {
            beginTx();
            em.persist(weeklyDayEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return weeklyDayEntity.getDay() != null;
    }

    // GET a schedule day for weekly
    public WeeklyDay getWeeklyDay(Integer weeklyId, Integer weekday) {
        WeeklyDayId weeklyDayId = new WeeklyDayId();
        weeklyDayId.setWId(weeklyId);
        weeklyDayId.setDay(weekday);
        WeeklyDayEntity weeklyDayEntity = em.find(WeeklyDayEntity.class, weeklyDayId);

        if(weeklyDayEntity == null)
            return null;
        else
            return WeeklyConverter.toDayDto(weeklyDayEntity);
    }

    // PUT a schedule day for weekly
    public WeeklyDay putWeeklyDay(Integer weeklyId, Integer weekday, WeeklyDay weeklyDay) {
        WeeklyEntity weekly = em.find(WeeklyEntity.class, weeklyId);
        if(weekly == null)
            return null;

        Integer sectionId = weeklyDay.getSectionId();
        SectionEntity sectionEntity = em.find(SectionEntity.class, sectionId);
        if(sectionEntity == null)
            return null;

        WeeklyDayId weeklyDayId = new WeeklyDayId();
        weeklyDayId.setWId(weeklyId);
        weeklyDayId.setDay(weekday);
        WeeklyDayEntity weeklyDayEntity = em.find(WeeklyDayEntity.class, weeklyDayId);
        if(weeklyDayEntity == null)
            return null;

        WeeklyDayEntity updatedWeeklyDayEntity = WeeklyConverter.toDayEntity(weeklyDay, weekly, sectionEntity);

        try {
            beginTx();
            updatedWeeklyDayEntity.setWId(weeklyDayEntity.getWId());
            updatedWeeklyDayEntity.setDay(weeklyDayEntity.getDay());
            updatedWeeklyDayEntity = em.merge(updatedWeeklyDayEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
            return null;
        }

        return WeeklyConverter.toDayDto(updatedWeeklyDayEntity);
    }

    // DELETE a schedule day for weekly
    public boolean deleteWeeklyDay(Integer weeklyId, Integer weekday) {
        WeeklyDayId weeklyDayId = new WeeklyDayId();
        weeklyDayId.setWId(weeklyId);
        weeklyDayId.setDay(weekday);
        WeeklyDayEntity weeklyDayEntity = em.find(WeeklyDayEntity.class, weeklyDayId);
        if(weeklyDayEntity == null)
            return false;

        try {
            beginTx();
            em.remove(weeklyDayEntity);
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
