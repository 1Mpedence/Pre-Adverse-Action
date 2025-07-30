package com.harsh.pre_adverse_action.pre_adverse_action.controller;

import com.harsh.pre_adverse_action.pre_adverse_action.dtos.*;
import com.harsh.pre_adverse_action.pre_adverse_action.responseDto.Response;
import com.harsh.pre_adverse_action.pre_adverse_action.service.PreAdverseActionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/pre-adverse-action")
public class PreAdverseActionController {

    private final PreAdverseActionService preAdverseActionService;

    public PreAdverseActionController(PreAdverseActionService preAdverseActionService) {
        this.preAdverseActionService = preAdverseActionService;
    }

    @GetMapping("/candidates")
    public ResponseEntity<List<CandidateDTO>> getCandidates() {
        try {
            List<CandidateDTO> candidates = preAdverseActionService.getCandidates();
            log.info("Fetched {} candidates: {}", candidates.size(), candidates);
            return ResponseEntity.status(HttpStatus.OK).body(candidates);
        } catch (Exception ex) {
            log.error("Error fetching candidates", ex);
            throw new RuntimeException("Failed to fetch candidates", ex);
        }
    }

    @GetMapping("/candidate/{id}")
    public ResponseEntity<CandidateDTO> getCandidateDetails(@PathVariable Long id) {
        try {
            CandidateDTO candidate = preAdverseActionService.getCandidateDetails(id);
            log.info("Fetched candidate details for ID {}", id);
            return ResponseEntity.status(HttpStatus.OK).body(candidate);
        } catch (Exception ex) {
            log.error("Error fetching candidate details for ID {}", id, ex);
            throw new RuntimeException("Failed to fetch candidates for id:" + id, ex);
        }
    }

    @GetMapping("/candidate/{id}/report")
    public ResponseEntity<ReportDTO> getReport(@PathVariable Long id) {
        try {
            ReportDTO report = preAdverseActionService.getCandidateReport(id);
            log.info("Fetched report for candidate ID {}", id);
            return ResponseEntity.status(HttpStatus.OK).body(report);
        } catch (Exception ex) {
            log.error("Error fetching report for candidate ID {}", id, ex);
            throw new RuntimeException("Error fetching report for candidate ID" + id, ex);
        }
    }

    @GetMapping("/candidate/{id}/court-searches")
    public ResponseEntity<List<CourtSearchDTO>> getCourtSearches(@PathVariable Long id) {
        try {
            List<CourtSearchDTO> courtSearches = preAdverseActionService.getCourtSearches(id);
            log.info("Fetched {} court searches for candidate ID {}", courtSearches.size(), id);
            return ResponseEntity.status(HttpStatus.OK).body(courtSearches);
        } catch (Exception ex) {
            log.error("Error fetching court searches for candidate ID {}", id, ex);
            throw new RuntimeException("Error fetching court searches for candidate ID" + id, ex);
        }
    }

    @GetMapping("/candidateSummay")
    public ResponseEntity<List<CandidateReportSummaryDTO>> getCandidateSummary(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String adjudication,
            @RequestParam(required = false) String status) {
        try {
            List<CandidateReportSummaryDTO> candidates = preAdverseActionService.getCandidateReportSummaryDTO(search, adjudication, status);
            log.info("Fetched {} candidates, candidates: {}", candidates.size(), candidates);
            return ResponseEntity.status(HttpStatus.OK).body(candidates);
        } catch (Exception ex) {
            log.error("Error fetching candidates", ex);
            throw new RuntimeException("Error fetching candidates", ex);
        }
    }

    @GetMapping("/pre-adverse-action-notice/{candidateId}")
    public ResponseEntity<PreAdverseActionEmailInfoDto> getEmailInfo(@PathVariable Long candidateId) {
        try {
            PreAdverseActionEmailInfoDto emailInfo = preAdverseActionService.getEmailInfo(candidateId);
            log.info("Fetched pre-adverse email info for candidate ID {}", candidateId);
            return ResponseEntity.status(HttpStatus.OK).body(emailInfo);
        } catch (Exception ex) {
            log.error("Error fetching pre-adverse email info for candidate ID {}", candidateId, ex);
            throw new RuntimeException("Error while fetching email body for candidateId {}" + candidateId);
        }
    }

    @PostMapping("/pre-adverse-action-notice/send/{candidateId}")
    public ResponseEntity<String> sendPreAdverseNotice(@PathVariable Long candidateId, @RequestBody PreAdverseActionEmailInfoDto request) {
        try {
            preAdverseActionService.sendNotice(candidateId, request);
            log.info("Sent pre-adverse notice for candidate ID {}", candidateId);
            return ResponseEntity.status(HttpStatus.OK).body("Pre-Advance Action Notice successfully sent.");
        } catch (Exception ex) {
            log.error("Error sending pre-adverse action notice for candidate ID {}", candidateId, ex);
            throw new RuntimeException("Error while sending pre-adverse email for candidateId" + candidateId);
        }
    }
}
