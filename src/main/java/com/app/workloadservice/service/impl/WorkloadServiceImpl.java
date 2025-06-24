package com.app.workloadservice.service.impl;

import com.app.workloadservice.dto.DurationRequestDto;
import com.app.workloadservice.dto.DurationResponseDto;
import com.app.workloadservice.dto.WorkloadRequestDto;
import com.app.workloadservice.dto.message.WorkloadResponseMessage;
import com.app.workloadservice.entity.TrainerWorkload;
import com.app.workloadservice.messaging.WorkloadStatusSender;
import com.app.workloadservice.repository.WorkloadRepository;
import com.app.workloadservice.service.WorkloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkloadServiceImpl implements WorkloadService {
    private final WorkloadRepository repository;
    private final WorkloadStatusSender statusSender;

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

            statusSender.sendWorkloadStatusUpdate(trainerWorkload.getUsername(), WorkloadResponseMessage.WorkloadStatus.SUCCESS);
            log.info("Updated workload for username {}", trainerWorkload.getUsername());
        } catch (Exception ex) {
            log.error("Error occurred: {}", ex.getMessage());
            statusSender.sendWorkloadStatusUpdate(request.getUsername(), WorkloadResponseMessage.WorkloadStatus.FAILED);
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
