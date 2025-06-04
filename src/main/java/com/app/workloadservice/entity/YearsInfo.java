package com.app.workloadservice.entity;

import lombok.*;

import java.util.HashMap;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class YearsInfo {
    private HashMap<Integer, MonthsInfo> months = new HashMap<>();
}
