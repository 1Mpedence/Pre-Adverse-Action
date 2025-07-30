package com.harsh.pre_adverse_action.pre_adverse_action.dtos;

import lombok.Data;

@Data
public class CandidateReportSummaryDTO {
    private String name;

    private String location;

    private String adjudication;

    private String status;

    private String createdAt;
}
