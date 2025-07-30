package com.harsh.pre_adverse_action.pre_adverse_action.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harsh.pre_adverse_action.pre_adverse_action.dao.Dao;
import com.harsh.pre_adverse_action.pre_adverse_action.dtos.*;
import com.harsh.pre_adverse_action.pre_adverse_action.entities.Candidate;
import com.harsh.pre_adverse_action.pre_adverse_action.entities.CourtSearch;
import com.harsh.pre_adverse_action.pre_adverse_action.entities.Report;
import com.harsh.pre_adverse_action.pre_adverse_action.repository.CandidateRepository;
import com.harsh.pre_adverse_action.pre_adverse_action.repository.CourtSearchRepository;
import com.harsh.pre_adverse_action.pre_adverse_action.repository.ReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class PreAdverseActionService {
    @Value("${pre.adverse.action.email.address}")
    private String preAdverseActionEmail;

    @Value("${pre.adverse.action.email.subject}")
    private String preAdverseActionSubject;

    private final CandidateRepository candidateRepository;
    private final ReportRepository reportRepository;
    private final CourtSearchRepository courtSearchRepository;
    private final Dao dao;
    private final ObjectMapper jsonMapper;
    private final TemplateEngine templateEngine;
    private final EmailService emailService;

    public PreAdverseActionService(
            CandidateRepository candidateRepository,
            ReportRepository reportRepository,
            CourtSearchRepository courtSearchRepository,
            Dao dao,
            ObjectMapper jsonMapper,
            TemplateEngine templateEngine,
            EmailService emailService
    ) {
        this.candidateRepository = candidateRepository;
        this.reportRepository = reportRepository;
        this.courtSearchRepository = courtSearchRepository;
        this.dao = dao;
        this.jsonMapper = jsonMapper;
        this.templateEngine = templateEngine;
        this.emailService = emailService;
    }

    public List<CandidateDTO> getCandidates() {
        log.info("Fetching all candidates");
        try {
            List<Candidate> candidates = this.candidateRepository.findCandidateByActive(true);
            return this.jsonMapper.convertValue(candidates, new TypeReference<List<CandidateDTO>>() {});
        } catch (Exception e) {
            log.error("Error while converting candidates to DTOs", e);
            throw new RuntimeException("Unable to fetch candidates", e);
        }
    }

    public CandidateDTO getCandidateDetails(Long id) {
        log.info("Fetching candidate details for ID: {}", id);
        try {
            Candidate candidate = this.candidateRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Candidate not found for ID: {}", id);
                        return new NoSuchElementException("Candidate not found with ID: " + id);
                    });
            return this.jsonMapper.convertValue(candidate, CandidateDTO.class);
        } catch (Exception e) {
            log.error("Error while fetching candidate details for ID: {}", id, e);
            throw new RuntimeException("Unable to fetch candidate details", e);
        }
    }

    public ReportDTO getCandidateReport(Long id) {
        log.info("Fetching report for candidate ID: {}", id);
        try {
            Report report = this.reportRepository.findByCandidateId(id)
                    .orElseThrow(() -> {
                        log.warn("Report not found for candidate ID: {}", id);
                        return new NoSuchElementException("Report not found for candidate ID: " + id);
                    });
            return this.jsonMapper.convertValue(report, ReportDTO.class);
        } catch (Exception e) {
            log.error("Error while fetching report for candidate ID: {}", id, e);
            throw new RuntimeException("Unable to fetch candidate report", e);
        }
    }

    public List<CourtSearchDTO> getCourtSearches(Long id) {
        log.info("Fetching court searches for candidate ID: {}", id);
        try {
            List<CourtSearch> searches = this.courtSearchRepository.findByCandidateId(id);
            return this.jsonMapper.convertValue(searches, new TypeReference<List<CourtSearchDTO>>() {});
        } catch (Exception e) {
            log.error("Error while fetching court searches for candidate ID: {}", id, e);
            throw new RuntimeException("Unable to fetch court searches", e);
        }
    }

    public List<CandidateReportSummaryDTO> getCandidateReportSummaryDTO(String search, String adjudication, String status) {
        log.info("Fetching all candidate report summaries");
        try {
            return this.dao.findAllCandidateReportSummaries(search, adjudication, status);
        } catch (Exception e) {
            log.error("Error while fetching candidate report summaries", e);
            throw new RuntimeException("Unable to fetch report summaries", e);
        }
    }

    public PreAdverseActionEmailInfoDto getEmailInfo(Long id) {
        log.info("Fetching email info for candidate ID: {}", id);
        try {
            Candidate candidate = this.candidateRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Candidate not found for ID: {}", id);
                        return new NoSuchElementException("Candidate not found with ID: " + id);
                    });


            Report report = this.reportRepository.findByCandidateId(id).orElseThrow(() -> {
                log.warn("Report not fround for Candidate for ID: {}", id);
                return new NoSuchElementException("Report not fround for Candidate for ID: " + id);
            });

            String availableChargesJson = report.getAvailableCharges();
            List<String> availableCharges = jsonMapper.readValue(availableChargesJson, new TypeReference<List<String>>() {});


            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(preAdverseActionEmail);
            message.setTo(candidate.getEmail());
            message.setSubject(preAdverseActionSubject);

            Context context = new Context();
            context.setVariable("name", candidate.getName());
            context.setVariable("charges", availableCharges);
            context.setVariable("edit", true);
            String htmlContent = templateEngine.process("pre-adverse-action-notice", context);
            message.setText(htmlContent);

            SimpleMailMessageDTO simpleMailMessage = new SimpleMailMessageDTO();
            simpleMailMessage.setFrom(message.getFrom());
            simpleMailMessage.setTo(message.getTo());
            simpleMailMessage.setSubject(message.getSubject());
            simpleMailMessage.setText(message.getText());

            PreAdverseActionEmailInfoDto emailInfo = PreAdverseActionEmailInfoDto.builder()
                    .simpleMessage(simpleMailMessage)
                    .availableCharges(availableCharges)
                    .build();

            log.debug("Prepared email info: {}", emailInfo);
            return emailInfo;
        } catch (Exception e) {
            log.error("Error while preparing email info for candidate ID: {}", id, e);
            throw new RuntimeException("Unable to prepare email info", e);
        }
    }

    public void sendNotice(Long candidateId, PreAdverseActionEmailInfoDto request) {
        log.info("Sending pre-adverse action notice for candidate ID: {}", candidateId);
        try {
            Candidate candidate = this.candidateRepository.findById(candidateId).orElseThrow(() -> {
                log.warn("Candidate not found with ID: {}", candidateId);
                return new NoSuchElementException("Candidate not found with ID: " + candidateId);
            });

            Report report = this.reportRepository.findByCandidateId(candidateId)
                    .orElseThrow(() -> {
                        log.warn("Report not found for candidate ID: {}", candidateId);
                        return new NoSuchElementException("Report does not exist for: " + candidateId);
                    });

            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(preAdverseActionEmail);
            simpleMailMessage.setTo(candidate.getEmail());
            simpleMailMessage.setSubject(request.getSimpleMessage().getSubject());

            Context context = new Context();
            context.setVariable("name", candidate.getName());
            context.setVariable("charges", request.getAvailableCharges());
            context.setVariable("edit", false);
            String htmlContent = templateEngine.process("pre-adverse-action-notice", context);
            simpleMailMessage.setText(htmlContent);

            emailService.sendEmail(simpleMailMessage);

            report.setAdjudication("ADVERSE ACTION");

            String selectedCharges = jsonMapper.writeValueAsString(request.getAvailableCharges());
            report.setSelectedCharges(selectedCharges);
            report.setAutoSendDuration(request.getAutoSendDuration());
            report.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
            report.setLastNotificationSent(Timestamp.valueOf(LocalDateTime.now()));

            this.reportRepository.save(report);
            log.info("Updated report with adverse action for candidate ID: {}", candidateId);

        } catch (Exception e) {
            log.error("Error while sending pre-adverse action notice for candidate ID: {}", candidateId, e);
            throw new RuntimeException("Unable to send notice", e);
        }
    }
}
