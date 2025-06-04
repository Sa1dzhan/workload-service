package com.app.workloadservice.entity;

import lombok.*;

import java.util.HashMap;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Trainer {
    private String username;
    private String firstName;
    private String lastName;
    private Boolean status;
    private HashMap<Integer, YearsInfo> years = new HashMap<>();
}
