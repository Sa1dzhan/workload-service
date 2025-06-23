package com.app.workloadservice.dto.message;

import com.app.workloadservice.util.WorkloadStatus;
import lombok.Data;


@Data
public class WorkloadResponseMessage {
    private String transactionId;
    private String username;
    private WorkloadStatus status;
}
