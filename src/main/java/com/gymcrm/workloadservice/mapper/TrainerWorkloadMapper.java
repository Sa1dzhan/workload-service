package com.gymcrm.workloadservice.mapper;

import com.gymcrm.workloadservice.entity.TrainerWorkload;
import com.gymcrm.workloadservice.entity.TrainerWorkloadDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrainerWorkloadMapper {
    TrainerWorkload toEntity(TrainerWorkloadDocument document);

    TrainerWorkloadDocument toDocument(TrainerWorkload entity);

}
