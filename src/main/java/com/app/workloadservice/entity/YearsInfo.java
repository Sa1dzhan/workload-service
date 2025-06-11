package com.app.workloadservice.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class YearsInfo {
    private Map<Integer, MonthsInfo> months = new HashMap<>();
}
