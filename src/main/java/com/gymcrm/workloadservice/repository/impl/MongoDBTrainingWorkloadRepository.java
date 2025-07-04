package com.gymcrm.workloadservice.repository.impl;

import com.gymcrm.workloadservice.entity.TrainerWorkload;
import com.gymcrm.workloadservice.entity.TrainerWorkloadDocument;
import com.gymcrm.workloadservice.mapper.TrainerWorkloadMapper;
import com.gymcrm.workloadservice.repository.WorkloadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
@Primary
@RequiredArgsConstructor
public class MongoDBTrainingWorkloadRepository implements WorkloadRepository {
    private final MongoTemplate mongoTemplate;
    private final TrainerWorkloadMapper mapper;

    @Override
    public void save(TrainerWorkload trainerWorkload) {
        mongoTemplate.save(mapper.toDocument(trainerWorkload));
    }

    @Override
    public TrainerWorkload findByUsername(String username) {
        Query query = new Query(Criteria.where("username").is(username));
        return mapper.toEntity(
                mongoTemplate.findOne(query, TrainerWorkloadDocument.class)
        );
    }
}
