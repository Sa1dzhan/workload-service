package com.gymcrm.workloadservice.repository;

import com.gymcrm.workloadservice.entity.TrainerWorkload;

public interface WorkloadRepository {
    void save(TrainerWorkload trainerWorkload);

    TrainerWorkload findByUsername(String username);
}
