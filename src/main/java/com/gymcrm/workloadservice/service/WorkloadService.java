package com.gymcrm.workloadservice.service;

import com.gymcrm.dto.workload.DurationRequestDto;
import com.gymcrm.dto.workload.DurationResponseDto;
import com.gymcrm.dto.workload.WorkloadRequestDto;

public interface WorkloadService {
    void updateTrainerWorkload(WorkloadRequestDto request);

    DurationResponseDto getWorkloadDuration(DurationRequestDto username);
}
