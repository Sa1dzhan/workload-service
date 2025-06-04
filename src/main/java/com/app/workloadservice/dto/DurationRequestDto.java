package com.app.workloadservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class DurationRequestDto {
    @NotNull
    private String username;

    @NotNull
    public Date trainingDate;
}
