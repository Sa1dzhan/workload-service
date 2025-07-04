package com.gymcrm.workloadservice.entity;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class YearsInfo {
    private Map<Integer, MonthsInfo> months = new HashMap<>();
}
