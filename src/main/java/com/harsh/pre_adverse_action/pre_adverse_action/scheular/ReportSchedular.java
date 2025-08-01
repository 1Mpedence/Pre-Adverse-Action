package com.harsh.pre_adverse_action.pre_adverse_action.scheular;

import com.harsh.pre_adverse_action.pre_adverse_action.entities.Candidate;
import com.harsh.pre_adverse_action.pre_adverse_action.entities.CourtSearch;
import com.harsh.pre_adverse_action.pre_adverse_action.entities.Report;
import com.harsh.pre_adverse_action.pre_adverse_action.exceptions.PreAdverseActionError;
import com.harsh.pre_adverse_action.pre_adverse_action.repository.CandidateRepository;
import com.harsh.pre_adverse_action.pre_adverse_action.repository.CourtSearchRepository;
import com.harsh.pre_adverse_action.pre_adverse_action.repository.ReportRepository;
import com.harsh.pre_adverse_action.pre_adverse_action.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportSchedular {
    @Value("${pre.adverse.action.email.address}")
    private String preAdverseActionEmail;

    @Value("${pre.adverse.action.email.subject}")
    private String preAdverseActionSubject;

    private final ReportRepository reportRepository;
    private final CandidateRepository candidateRepository;
    private final CourtSearchRepository courtSearchRepository;
    private final TemplateEngine templateEngine;
    private final EmailService emailService;

    ReportSchedular(ReportRepository reportRepository, CandidateRepository candidateRepository, CourtSearchRepository courtSearchRepository, TemplateEngine templateEngine, EmailService emailService) {
        this.reportRepository = reportRepository;
        this.candidateRepository = candidateRepository;
        this.courtSearchRepository = courtSearchRepository;
        this.templateEngine = templateEngine;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void checkReportsForNotifications() {
        List<Report> reports = reportRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Report report : reports) {
            Timestamp lastNotification = report.getLastNotificationSent();

            if (lastNotification == null || report.getAutoSendDuration() == null) {
                continue;
            }

            Duration d = Duration.between(lastNotification.toLocalDateTime(), now);
            Long days = d.toDays();
            if (days%report.getAutoSendDuration() == 0) {
                CompletableFuture.runAsync(() -> this.sendNotice(report.getCandidateId()));
            }
        }
    }
    public void sendNotice(Long candidateId) {
        log.info("Sending pre-adverse action notice for candidate ID: {}", candidateId);
        try {
            Candidate candidate = this.candidateRepository.findById(candidateId).orElseThrow(() -> {
                log.warn("Candidate not found with ID: {}", candidateId);
                return new NoSuchElementException("Candidate not found with ID: " + candidateId);
            });

            if(Boolean.FALSE.equals(candidate.getActive()))  return;

            Report report = this.reportRepository.findByCandidateId(candidateId)
                    .orElseThrow(() -> {
                        log.warn("Report not found for candidate ID: {}", candidateId);
                        return new NoSuchElementException("Report does not exist for: " + candidateId);
                    });

            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(preAdverseActionEmail);
            simpleMailMessage.setTo(candidate.getEmail());
            simpleMailMessage.setSubject(preAdverseActionSubject);

            List<CourtSearch> courtSearches = courtSearchRepository.findByCandidateIdAndStatus(candidateId, "CONSIDER");
            List<String> charges = courtSearches.stream().map(CourtSearch::getSearch).collect(Collectors.toList());

            Context context = new Context();
            context.setVariable("name", candidate.getName());
            context.setVariable("charges", charges);
            context.setVariable("edit", false);
            String htmlContent = templateEngine.process("pre-adverse-action-notice", context);
            simpleMailMessage.setText(htmlContent);

            emailService.sendEmail(simpleMailMessage);

            report.setAdjudication("ADVERSE ACTION");
            report.setLastNotificationSent(Timestamp.valueOf(LocalDateTime.now()));

            this.reportRepository.save(report);
            log.info("Updated report with adverse action for candidate ID: {}", candidateId);

        } catch (Exception e) {
            log.error("Error while sending pre-adverse action notice for candidate ID: {}", candidateId, e);
            throw new PreAdverseActionError("Unable to send notice", e);
        }
    }
}
