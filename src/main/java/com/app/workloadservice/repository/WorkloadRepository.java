package com.app.workloadservice.repository;

import com.app.workloadservice.entity.TrainerWorkload;

public interface WorkloadRepository {
    void saveTrainer(TrainerWorkload trainerWorkload);

    TrainerWorkload getTrainer(String username);
}
