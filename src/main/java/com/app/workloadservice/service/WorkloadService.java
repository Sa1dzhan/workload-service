package com.app.workloadservice.service;

import com.app.workloadservice.dto.WorkloadRequestDto;

public interface WorkloadService {
    void updateTrainerWorkload(WorkloadRequestDto request);
}
