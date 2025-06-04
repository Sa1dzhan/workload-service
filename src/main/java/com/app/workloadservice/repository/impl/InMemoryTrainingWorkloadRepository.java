package com.app.workloadservice.repository.impl;

import com.app.workloadservice.entity.Trainer;
import com.app.workloadservice.repository.WorkloadRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Objects;

@Repository
public class InMemoryTrainingWorkloadRepository implements WorkloadRepository {
    HashMap<String, Trainer> inMemory = new HashMap<>();

    @Override
    public void saveTrainer(Trainer trainer) {
        inMemory.putIfAbsent(trainer.getUsername(), trainer);
    }

    @Override
    public Trainer getTrainer(String username) {
        return inMemory.get(username);
    }

    public void deleteTrainer(String username) {
        if (Objects.nonNull(inMemory.get(username))) {
            inMemory.remove(username);
        }
    }
}
