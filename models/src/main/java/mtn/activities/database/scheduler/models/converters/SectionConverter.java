package mtn.activities.database.scheduler.models.converters;

import mtn.activities.database.scheduler.lib.Section;
import mtn.activities.database.scheduler.lib.SectionDay;
import mtn.activities.database.scheduler.models.entities.SectionDayEntity;
import mtn.activities.database.scheduler.models.entities.SectionEntity;

import java.util.List;
import java.util.stream.Collectors;

public class SectionConverter {

    public static Section toDto(SectionEntity entity) {
        Section dto = new Section();
        dto.setSectionId(entity.getId());
        dto.setSectionName(entity.getName());

        return dto;
    }

    public static SectionDay toDayDto(SectionDayEntity dayEntity) {
        SectionDay dto = new SectionDay();
        dto.setDay(dayEntity.getDay());
        dto.setPosition(dayEntity.getPosition());

        return dto;
    }

    public static Section toDto(SectionEntity entity, List<SectionDayEntity> dayEntities) {
        Section dto = new Section();
        dto.setSectionId(entity.getId());
        dto.setSectionName(entity.getName());
        if(!dayEntities.isEmpty())
            dto.setSectionDays(dayEntities.stream().map(SectionConverter::toDayDto).collect(Collectors.toList()));

        return dto;

    }

    public static SectionEntity toEntity(Section dto) {
        SectionEntity entity = new SectionEntity();
        entity.setId(dto.getSectionId());
        entity.setName(dto.getSectionName());

        return entity;

    }

    public static SectionDayEntity toDayEntity(SectionDay dayDto, SectionEntity entity) {
        SectionDayEntity dayEntity = new SectionDayEntity();
        dayEntity.setSId(entity);
        dayEntity.setDay(dayDto.getDay());
        dayEntity.setPosition(dayDto.getPosition());

        return dayEntity;
    }
}
