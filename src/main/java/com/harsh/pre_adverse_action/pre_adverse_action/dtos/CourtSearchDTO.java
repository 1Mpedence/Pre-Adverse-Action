package com.harsh.pre_adverse_action.pre_adverse_action.dtos;

import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourtSearchDTO {
    private Long id;
    private Long candidateId;
    private String search;
    private String status;
    private Timestamp date;
}
