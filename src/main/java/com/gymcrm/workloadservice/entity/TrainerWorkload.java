package com.gymcrm.workloadservice.entity;

import com.gymcrm.dto.workload.WorkloadRequestDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Document(collection = "trainerWorkloads")
public class TrainerWorkload {
    @Id
    private String id;
    @Indexed(unique = true)
    private String username;
    private String firstName;
    private String lastName;
    private Boolean status;
    private HashMap<Integer, YearsInfo> years = new HashMap<>();

    public void updateWorkload(LocalDate trainingDate, long trainingDuration, WorkloadRequestDto.ActionType actionType) {
        int year = trainingDate.getYear();
        int month = trainingDate.getMonth().getValue();

        YearsInfo yearInfo = getOrCreateYear(year);
        MonthsInfo monthInfo = getOrCreateMonth(yearInfo, month);

        if (actionType == WorkloadRequestDto.ActionType.ADD) {
            monthInfo.setDuration(monthInfo.getDuration() + trainingDuration);
        } else if (actionType == WorkloadRequestDto.ActionType.DELETE) {
            monthInfo.setDuration(Math.max(0, monthInfo.getDuration() - trainingDuration));
        }
    }

    private YearsInfo getOrCreateYear(int year) {
        return years.computeIfAbsent(year, k -> new YearsInfo());
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

    public long getDurationForMonth(int year, int month) {
        YearsInfo yInfo = getOrCreateYear(year);
        MonthsInfo mInfo = getOrCreateMonth(yInfo, month);

        return mInfo.getDuration();
    }
}
