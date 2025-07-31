package com.harsh.pre_adverse_action.pre_adverse_action.dtos;

import lombok.*;

import java.sql.Timestamp;

@Data
public class ReportDTO {
    private Long id;
    private String adjudication;
    private Long candidateId;
    private String status;
    private String packageName;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp completedAt;
    private Timestamp turnAroundTime;
    private Long autoSendDuration;
    private Timestamp lastNotificationSent;
    private String availableCharges;
    private String selectedCharges;
}
