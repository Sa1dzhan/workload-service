package com.gymcrm.workloadservice.mapper;

import com.gymcrm.workloadservice.entity.MonthsInfo;
import com.gymcrm.workloadservice.entity.TrainerWorkload;
import com.gymcrm.workloadservice.entity.TrainerWorkloadDocument;
import com.gymcrm.workloadservice.entity.YearsInfo;
import org.mapstruct.Mapper;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TrainerWorkloadMapper {
    TrainerWorkload toEntity(TrainerWorkloadDocument document);

    TrainerWorkloadDocument toDocument(TrainerWorkload entity);

    /**
     * from Long duration to YearsInfo.
     */
    default YearsInfo fromMonthsMap(Map<Integer, Long> monthsDuration) {
        if (monthsDuration == null) {
            return new YearsInfo();
        }
        Map<Integer, MonthsInfo> monthsInfoMap = monthsDuration.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new MonthsInfo(entry.getValue())
                ));
        return new YearsInfo(monthsInfoMap);
    }

    /**
     * from YearsInfo to Long duration.
     */
    default HashMap<Integer, Long> toMonthsMap(YearsInfo yearsInfo) {
        if (yearsInfo == null || yearsInfo.getMonths() == null) {
            return new HashMap<>();
        }
        return yearsInfo.getMonths().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getDuration(),
                        (a, b) -> b,
                        HashMap::new
                ));
    }

}
