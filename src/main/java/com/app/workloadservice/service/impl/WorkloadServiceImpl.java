package com.app.workloadservice.service.impl;

import com.app.workloadservice.dto.DurationRequestDto;
import com.app.workloadservice.dto.DurationResponseDto;
import com.app.workloadservice.dto.WorkloadRequestDto;
import com.app.workloadservice.entity.MonthsInfo;
import com.app.workloadservice.entity.Trainer;
import com.app.workloadservice.entity.YearsInfo;
import com.app.workloadservice.repository.WorkloadMemory;
import com.app.workloadservice.service.WorkloadService;
import com.app.workloadservice.util.ActionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkloadServiceImpl implements WorkloadService {
    private final WorkloadMemory repository;

    @Override
    public void updateTrainerWorkload(WorkloadRequestDto request) {
        try {
            Trainer trainer = getTrainer(request);

            int year = request.getTrainingDate().getYear();
            int month = request.getTrainingDate().getMonth();

            YearsInfo yInfo = getOrCreateYear(trainer, year);
            MonthsInfo mInfo = getOrCreateMonth(yInfo, month);

            if (request.getActionType() == ActionType.ADD) {
                mInfo.setDuration(
                        mInfo.getDuration() + request.getTrainingDuration()
                );
            } else if (request.getActionType() == ActionType.DELETE) {
                mInfo.setDuration(
                        Math.max(0, mInfo.getDuration() - request.getTrainingDuration())
                );
            }

            repository.saveTrainer(trainer);
            log.info("Updated workload for username {}, date {}-{}, duration {}", trainer.getUsername(), year, month, mInfo.getDuration());
        } catch (Exception ex) {
            log.error("Error occurred: {}", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    private Trainer getTrainer(WorkloadRequestDto request) {
        Trainer trainer = repository.getTrainer(request.getUsername());
        if (Objects.isNull(trainer)) {
            trainer = new Trainer();
        }

        trainer.setUsername(trainer.getUsername());
        trainer.setFirstName(request.getFirstName());
        trainer.setLastName(request.getLastName());
        trainer.setStatus(request.getIsActive());

        return trainer;
    }

    private YearsInfo getOrCreateYear(Trainer trainer, int year) {
        YearsInfo yInfo = trainer.getYears().get(year);
        if (Objects.isNull(yInfo)) {
            yInfo = new YearsInfo();
            trainer.getYears().put(year, yInfo);
        }

        return yInfo;
    }

    private MonthsInfo getOrCreateMonth(YearsInfo yInfo, int month) {
        MonthsInfo mInfo = yInfo.getMonths().get(month);
        if (Objects.isNull(mInfo)) {
            mInfo = new MonthsInfo();
            mInfo.setDuration(0L);
            yInfo.getMonths().put(month, mInfo);
        }

        return mInfo;
    }

    @Override
    public DurationResponseDto getWorkloadDuration(DurationRequestDto request) {
        Trainer trainer = repository.getTrainer(request.getUsername());
        if (Objects.isNull(trainer)) {
            throw new RuntimeException("Trainer with such username " + request.getUsername() + " not found");
        }

        int year = request.getTrainingDate().getYear();
        int month = request.getTrainingDate().getMonth();

        YearsInfo yInfo = getOrCreateYear(trainer, year);
        MonthsInfo mInfo = getOrCreateMonth(yInfo, month);

        DurationResponseDto response = new DurationResponseDto();
        response.setUsername(request.getUsername());
        response.setDuration(mInfo.getDuration());

        log.info("Retrieved duration {} for Trainer {}", response.getDuration(), response.getUsername());
        return response;
    }
}
