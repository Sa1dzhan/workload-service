package com.gymcrm.workloadservice.controller;

import com.gymcrm.dto.workload.DurationRequestDto;
import com.gymcrm.dto.workload.DurationResponseDto;
import com.gymcrm.dto.workload.WorkloadRequestDto;
import com.gymcrm.workloadservice.service.WorkloadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workload")
@RequiredArgsConstructor
@Slf4j
public class WorkloadController {
    private final WorkloadService workloadService;

    @PostMapping("/update")
    public ResponseEntity<?> updateTrainerWorkload(
            @Valid @RequestBody WorkloadRequestDto request) {

        log.info("Workload update request for {}", request.getUsername());
        workloadService.updateTrainerWorkload(request);
        log.info("Successfully updated workload for {}", request.getUsername());

        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping
    public ResponseEntity<DurationResponseDto> getTrainerWorkload(
            @Valid @RequestBody DurationRequestDto request) {

        log.info("Duration request for {}", request.getUsername());
        DurationResponseDto response = workloadService.getWorkloadDuration(request);
        log.info("Successfully retrieved duration for {}", response.getUsername());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
