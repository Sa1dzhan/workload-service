package com.app.workloadservice.service;

import com.app.workloadservice.dto.DurationRequestDto;
import com.app.workloadservice.dto.DurationResponseDto;
import com.app.workloadservice.dto.WorkloadRequestDto;

public interface WorkloadService {
    void updateTrainerWorkload(WorkloadRequestDto request);

    DurationResponseDto getWorkloadDuration(DurationRequestDto username);
}
