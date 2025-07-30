package com.harsh.pre_adverse_action.pre_adverse_action.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleMailMessageDTO {

    private String from;

    private String[] to;

    private String subject;

    private String text;

}
