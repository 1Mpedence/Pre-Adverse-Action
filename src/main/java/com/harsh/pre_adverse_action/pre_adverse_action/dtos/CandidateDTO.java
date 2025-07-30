package com.harsh.pre_adverse_action.pre_adverse_action.dtos;

import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Data
public class CandidateDTO {
    private Long id;
    private String name;
    private String location;
    private String email;
    private String dob;
    private String phone;
    private String zipcode;
    private String socialSecurity;
    private String driversLicense;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Boolean active;
    private ReportDTO report;
    private List<CourtSearchDTO> courtSearches;
}
