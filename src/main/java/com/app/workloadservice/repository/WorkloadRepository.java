package com.app.workloadservice.repository;

import com.app.workloadservice.entity.Trainer;

public interface WorkloadRepository {
    void saveTrainer(Trainer trainer);

    Trainer getTrainer(String username);
}
