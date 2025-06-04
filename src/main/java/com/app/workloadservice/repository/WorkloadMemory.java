package com.app.workloadservice.repository;

import com.app.workloadservice.entity.Trainer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Objects;

@Component
public class WorkloadMemory {
    HashMap<String, Trainer> inMemory = new HashMap<>();

    public void saveTrainer(Trainer trainer) {
        inMemory.putIfAbsent(trainer.getUsername(), trainer);
    }

    public Trainer getTrainer(String username) {
        return inMemory.get(username);
    }

    public void deleteTrainer(String username) {
        if (Objects.nonNull(inMemory.get(username))) {
            inMemory.remove(username);
        }
    }
}
