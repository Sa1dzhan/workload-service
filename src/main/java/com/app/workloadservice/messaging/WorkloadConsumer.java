package com.app.workloadservice.messaging;

import com.app.workloadservice.dto.WorkloadRequestDto;
import com.app.workloadservice.dto.message.WorkloadMessage;
import com.app.workloadservice.service.WorkloadService;
import com.app.workloadservice.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WorkloadConsumer {

    private final WorkloadService workloadService;

    @RabbitListener(queues = Constants.QUEUE_UPDATE)
    public void processWorkloadUpdate(WorkloadMessage<WorkloadRequestDto> message) {
        try {
            MDC.put("transactionId", message.getTransactionId());
            MDC.put("correlationId", message.getCorrelationId());

            workloadService.updateTrainerWorkload(message.getPayload());

            log.info("Successfully processed workload update for: {}", message.getUsername());
        } catch (Exception e) {
            log.error("Failed to process workload update for: {}", message.getUsername(), e);
            throw e;
        }
    }
}
