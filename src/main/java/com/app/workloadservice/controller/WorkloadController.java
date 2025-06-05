package com.app.workloadservice.controller;

import com.app.workloadservice.dto.DurationRequestDto;
import com.app.workloadservice.dto.DurationResponseDto;
import com.app.workloadservice.dto.WorkloadRequestDto;
import com.app.workloadservice.service.WorkloadService;
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
            @Valid @RequestBody WorkloadRequestDto request,
            @RequestHeader(value = "transactionId") String transactionId) {

        log.info("Transaction ID: {} | Workload update request for {}", transactionId, request.getUsername());
        workloadService.updateTrainerWorkload(request);
        log.info("Transaction ID: {} | Successfully updated workload for {}", transactionId, request.getUsername());

        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping
    public ResponseEntity<DurationResponseDto> getTrainerWorkload(
            @Valid @RequestBody DurationRequestDto request,
            @RequestHeader(value = "transactionId") String transactionId) {

        log.info("Transaction ID: {} | Duration request for {}", transactionId, request.getUsername());
        DurationResponseDto response = workloadService.getWorkloadDuration(request);
        log.info("Transaction ID: {} | Successfully retrieved duration for {}", transactionId, response.getUsername());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
