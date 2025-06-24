package com.gymcrm.workloadservice.messaging;

import com.gymcrm.dto.message.WorkloadMessage;
import com.gymcrm.dto.message.WorkloadResponseMessage;
import com.gymcrm.dto.workload.WorkloadRequestDto;
import com.gymcrm.workloadservice.service.WorkloadService;
import com.gymcrm.workloadservice.util.Constants;
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
    public WorkloadResponseMessage processWorkloadUpdate(WorkloadMessage<WorkloadRequestDto> message) {
        WorkloadResponseMessage response = WorkloadResponseMessage.builder()
                .username(message.getUsername())
                .transactionId(message.getTransactionId())
                .build();

        try {
            MDC.put("transactionId", message.getTransactionId());
            log.info("Processing workload update for: {}", message.getUsername());

            workloadService.updateTrainerWorkload(message.getPayload());

            response.setStatus(WorkloadResponseMessage.WorkloadStatus.SUCCESS);
            log.info("Successfully processed workload update for: {}", message.getUsername());
        } catch (Exception e) {
            log.error("Failed to process workload update for: {}", message.getUsername(), e);
            response.setStatus(WorkloadResponseMessage.WorkloadStatus.FAILED);
        } finally {
            MDC.clear();
        }

        return response;
    }
}
