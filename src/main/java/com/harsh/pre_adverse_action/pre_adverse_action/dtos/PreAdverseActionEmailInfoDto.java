package com.harsh.pre_adverse_action.pre_adverse_action.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreAdverseActionEmailInfoDto {
    private SimpleMailMessageDTO simpleMessage;

    private List<String> availableCharges;

    private Long autoSendDuration;
}
