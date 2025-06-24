package com.gymcrm.workloadservice.service.impl;

import com.gymcrm.dto.workload.DurationRequestDto;
import com.gymcrm.dto.workload.DurationResponseDto;
import com.gymcrm.dto.workload.WorkloadRequestDto;
import com.gymcrm.workloadservice.entity.TrainerWorkload;
import com.gymcrm.workloadservice.repository.WorkloadRepository;
import com.gymcrm.workloadservice.service.WorkloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkloadServiceImpl implements WorkloadService {
    private final WorkloadRepository repository;

    @Override
    public void updateTrainerWorkload(WorkloadRequestDto request) {
        try {
            TrainerWorkload trainerWorkload = getTrainer(request);

            trainerWorkload.updateWorkload(
                    request.getTrainingDate(),
                    request.getTrainingDuration(),
                    request.getActionType()
            );

            repository.save(trainerWorkload);
            log.info("Updated workload for username {}", trainerWorkload.getUsername());
        } catch (Exception ex) {
            log.error("Error occurred: {}", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    private TrainerWorkload getTrainer(WorkloadRequestDto request) {
        TrainerWorkload trainerWorkload = repository.findByUsername(request.getUsername());
        if (Objects.isNull(trainerWorkload)) {
            trainerWorkload = new TrainerWorkload();
        }

        trainerWorkload.setUsername(request.getUsername());
        trainerWorkload.setFirstName(request.getFirstName());
        trainerWorkload.setLastName(request.getLastName());
        trainerWorkload.setStatus(request.getIsActive());

        return trainerWorkload;
    }

    @Override
    public DurationResponseDto getWorkloadDuration(DurationRequestDto request) {
        TrainerWorkload trainerWorkload = repository.findByUsername(request.getUsername());
        if (Objects.isNull(trainerWorkload)) {
            throw new RuntimeException("Trainer with such username " + request.getUsername() + " not found");
        }

        long duration = trainerWorkload.getDurationForMonth(
                request.getTrainingDate().getYear(),
                request.getTrainingDate().getMonth().getValue()
        );

        DurationResponseDto response = new DurationResponseDto();
        response.setUsername(request.getUsername());
        response.setDuration(duration);

        log.info("Retrieved duration {} for Trainer {}", response.getDuration(), response.getUsername());
        return response;
    }
}
