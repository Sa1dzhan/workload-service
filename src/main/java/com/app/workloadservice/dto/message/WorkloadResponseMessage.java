package com.app.workloadservice.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkloadResponseMessage extends RequestReplyBaseMessage {
    private WorkloadStatus status;

    public enum WorkloadStatus {
        SUCCESS,
        FAILED
    }
}
