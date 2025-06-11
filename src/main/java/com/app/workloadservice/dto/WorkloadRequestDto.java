package com.app.workloadservice.dto;

import com.app.workloadservice.util.ActionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class WorkloadRequestDto {
    @NotNull
    private String username;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private Boolean isActive;

    @NotNull
    private LocalDate trainingDate;

    @NotNull
    private Long trainingDuration;

    @NotNull
    private ActionType actionType;
}
