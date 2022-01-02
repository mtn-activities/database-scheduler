package mtn.activities.database.scheduler.models.converters;

import mtn.activities.database.scheduler.lib.Weekly;
import mtn.activities.database.scheduler.lib.WeeklyDay;
import mtn.activities.database.scheduler.models.entities.SectionEntity;
import mtn.activities.database.scheduler.models.entities.WeeklyDayEntity;
import mtn.activities.database.scheduler.models.entities.WeeklyEntity;

import java.util.List;
import java.util.stream.Collectors;

public class WeeklyConverter {

    public static Weekly toDto(WeeklyEntity entity) {
        Weekly dto = new Weekly();
        dto.setWeeklyId(entity.getId());
        dto.setWeeklyName(entity.getName());

        return dto;
    }

    public static WeeklyDay toDayDto(WeeklyDayEntity dayEntity) {
        WeeklyDay dto = new WeeklyDay();
        dto.setDay(dayEntity.getDay());
        dto.setSectionId(dayEntity.getSection().getId());
        dto.setPosition(dayEntity.getPosition());

        return dto;
    }

    public static Weekly toDto(WeeklyEntity entity, List<WeeklyDayEntity> dayEntities) {
        Weekly dto = new Weekly();
        dto.setWeeklyId(entity.getId());
        dto.setWeeklyName(entity.getName());
        if(!dayEntities.isEmpty())
            dto.setWeeklyDays(dayEntities.stream().map(WeeklyConverter::toDayDto).collect(Collectors.toList()));

        return dto;
    }

    public static WeeklyEntity toEntity(Weekly dto) {
        WeeklyEntity entity = new WeeklyEntity();
        entity.setId(dto.getWeeklyId());
        entity.setName(dto.getWeeklyName());

        return entity;

    }

    public static WeeklyDayEntity toDayEntity(WeeklyDay dayDto, WeeklyEntity entity, SectionEntity section) {
        WeeklyDayEntity dayEntity = new WeeklyDayEntity();
        dayEntity.setWId(entity);
        dayEntity.setDay(dayDto.getDay());
        dayEntity.setSection(section);
        dayEntity.setPosition(dayDto.getPosition());

        return dayEntity;
    }
}
