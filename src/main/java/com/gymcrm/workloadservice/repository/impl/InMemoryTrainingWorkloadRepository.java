package com.gymcrm.workloadservice.repository.impl;

import com.gymcrm.workloadservice.entity.TrainerWorkload;
import com.gymcrm.workloadservice.repository.WorkloadRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Objects;

@Repository
public class InMemoryTrainingWorkloadRepository implements WorkloadRepository {
    HashMap<String, TrainerWorkload> inMemory = new HashMap<>();

    @Override
    public void save(TrainerWorkload trainerWorkload) {
        inMemory.putIfAbsent(trainerWorkload.getUsername(), trainerWorkload);
    }

    @Override
    public TrainerWorkload findByUsername(String username) {
        return inMemory.get(username);
    }

    public void deleteTrainer(String username) {
        if (Objects.nonNull(inMemory.get(username))) {
            inMemory.remove(username);
        }
    }
}
