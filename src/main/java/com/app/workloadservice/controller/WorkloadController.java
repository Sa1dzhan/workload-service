package com.app.workloadservice.controller;

import com.app.workloadservice.dto.DurationRequestDto;
import com.app.workloadservice.dto.DurationResponseDto;
import com.app.workloadservice.dto.WorkloadRequestDto;
import com.app.workloadservice.service.WorkloadService;
import com.app.workloadservice.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/workload")
@RequiredArgsConstructor
@Slf4j
public class WorkloadController {
    private final WorkloadService workloadService;
    private final JwtUtil jwtUtil;

    @PostMapping("/update")
    public ResponseEntity<?> updateTrainerWorkload(
            @Valid @RequestBody WorkloadRequestDto request,
            @RequestHeader("Authorization") String token,
            @RequestHeader(value = "transactionId") String transactionId) {
        try {
            log.info("Transaction ID: {} | Workload update request for {}", transactionId, request.getUsername());

            String usrToken = jwtUtil.getUsernameFromToken(token.substring(7).trim());
            if (!Objects.equals(request.getUsername(), usrToken)) {
                log.error("Usernames not same {}(token) != {}(request)", usrToken, request.getUsername());
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            workloadService.updateTrainerWorkload(request);

            log.info("Transaction ID: {} | Successfully updated workload for {}", transactionId, request.getUsername());

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping
    public ResponseEntity<DurationResponseDto> getTrainerWorkload(
            @Valid @RequestBody DurationRequestDto request,
            @RequestHeader("Authorization") String token,
            @RequestHeader(value = "transactionId") String transactionId) {
        try {
            log.info("Transaction ID: {} | Duration request for {}", transactionId, request.getUsername());
            String usrToken = jwtUtil.getUsernameFromToken(token.substring(7).trim());
            if (!Objects.equals(request.getUsername(), usrToken)) {
                log.error("Usernames not same {}(token) != {}(request)", usrToken, request.getUsername());
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            DurationResponseDto response = workloadService.getWorkloadDuration(request);
            log.info("Transaction ID: {} | Successfully retrieved duration for {}", transactionId, usrToken);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
