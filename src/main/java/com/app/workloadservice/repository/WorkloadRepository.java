package com.app.workloadservice.repository;

import com.app.workloadservice.entity.TrainerWorkload;

public interface WorkloadRepository {
    void save(TrainerWorkload trainerWorkload);

    TrainerWorkload findByUsername(String username);
}
