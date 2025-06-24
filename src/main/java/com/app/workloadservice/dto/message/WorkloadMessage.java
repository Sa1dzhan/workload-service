package com.app.workloadservice.dto.message;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkloadMessage<T> extends RequestReplyBaseMessage {
    private T payload;

    @Builder
    public WorkloadMessage(String username, String correlationId,
                           String transactionId, T payload) {
        super();
        this.username = username;
        this.correlationId = correlationId;
        this.transactionId = transactionId;
        this.payload = payload;
    }
}
