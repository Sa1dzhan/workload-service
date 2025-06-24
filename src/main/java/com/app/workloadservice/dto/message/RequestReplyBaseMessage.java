package com.app.workloadservice.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestReplyBaseMessage {
    protected String username;
    protected String correlationId;
    protected String transactionId;
}
