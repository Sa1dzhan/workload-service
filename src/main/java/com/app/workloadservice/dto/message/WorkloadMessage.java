package com.app.workloadservice.dto.message;

import lombok.Data;

@Data
public class WorkloadMessage<T> {
    private String transactionId;
    private String username;
    private T payload;
}
