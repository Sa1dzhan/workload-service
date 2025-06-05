package com.app.workloadservice.repository.impl;

import com.app.workloadservice.entity.TrainerWorkload;
import com.app.workloadservice.repository.WorkloadRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Objects;

@Repository
public class InMemoryTrainingWorkloadRepository implements WorkloadRepository {
    HashMap<String, TrainerWorkload> inMemory = new HashMap<>();

    @Override
    public void saveTrainer(TrainerWorkload trainerWorkload) {
        inMemory.putIfAbsent(trainerWorkload.getUsername(), trainerWorkload);
    }

    @Override
    public TrainerWorkload getTrainer(String username) {
        return inMemory.get(username);
    }

    public void deleteTrainer(String username) {
        if (Objects.nonNull(inMemory.get(username))) {
            inMemory.remove(username);
        }
    }
}
