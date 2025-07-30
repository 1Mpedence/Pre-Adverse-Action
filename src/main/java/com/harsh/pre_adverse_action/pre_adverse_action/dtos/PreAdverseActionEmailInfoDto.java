package com.harsh.pre_adverse_action.pre_adverse_action.dtos;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
public class PreAdverseActionEmailInfoDto {
    private SimpleMailMessageDTO simpleMessage;

    private List<String> availableCharges;

    private Timestamp TurnAroundTime;

    private Long autoSendDuration;
}
