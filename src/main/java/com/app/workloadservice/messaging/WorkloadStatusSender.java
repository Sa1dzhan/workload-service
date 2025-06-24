package com.app.workloadservice.messaging;

import com.app.workloadservice.dto.message.WorkloadResponseMessage;
import com.app.workloadservice.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkloadStatusSender {
    private final RabbitTemplate rabbitTemplate;
    private final String responseQueue = Constants.QUEUE_RESPONSE;

    public void sendWorkloadStatusUpdate(String username, WorkloadResponseMessage.WorkloadStatus status) {
        try {
            WorkloadResponseMessage msg = new WorkloadResponseMessage();
            msg.setTransactionId(MDC.get("transactionId"));
            msg.setCorrelationId(MDC.get("correlationId"));
            msg.setUsername(username);
            msg.setStatus(status);

            rabbitTemplate.convertAndSend(responseQueue, msg);
        } catch (Exception e) {
            log.error("Failed to send workload update: {}", e.getMessage(), e);
        }
    }
}
