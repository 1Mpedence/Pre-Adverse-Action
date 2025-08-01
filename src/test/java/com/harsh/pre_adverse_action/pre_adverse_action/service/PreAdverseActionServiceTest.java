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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PreAdverseActionServiceTest {
    @Value("${pre.adverse.action.email.address}")
    private String preAdverseActionEmail;

    @Value("${pre.adverse.action.email.subject}")
    private String preAdverseActionSubject;

    @Mock
    CandidateRepository candidateRepository;

    @Mock
    ReportRepository reportRepository;

    @Mock
    CourtSearchRepository courtSearchRepository;

    @Mock
    Dao dao;

    @Mock
    ObjectMapper jsonMapper;

    @Mock
    TemplateEngine templateEngine;

    @Mock
    EmailService emailService;

    @InjectMocks
    PreAdverseActionService service;

    private AutoCloseable openMocks;


    @BeforeEach
    void setup() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (openMocks != null) {
            openMocks.close();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    void getCandidates_success() {
        List<Candidate> candidateList = List.of(new Candidate());
        List<CandidateDTO> dtoList = List.of(new CandidateDTO());

        when(candidateRepository.findCandidateByActive(true)).thenReturn(candidateList);
        when(jsonMapper.convertValue(eq(candidateList), any(TypeReference.class))).thenReturn(dtoList);

        List<CandidateDTO> result = service.getCandidates();
        assertEquals(dtoList, result);
    }

    @Test
    void getCandidates_failure_throws() {
        when(candidateRepository.findCandidateByActive(true)).thenThrow(new RuntimeException("DB error"));
        assertThrows(RuntimeException.class, () -> service.getCandidates());
    }

    @Test
    void getCandidateDetails_success() {
        Candidate candidate = new Candidate();
        candidate.setId(1L);
        CandidateDTO dto = new CandidateDTO();

        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(jsonMapper.convertValue(candidate, CandidateDTO.class)).thenReturn(dto);

        CandidateDTO result = service.getCandidateDetails(1L);
        assertEquals(dto, result);
    }

    @Test
    void getCandidateDetails_notFound_throws() {
        when(candidateRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.getCandidateDetails(1L));
    }

    @Test
    void getCandidateReport_success() {
        Report report = new Report();
        ReportDTO dto = new ReportDTO();

        when(reportRepository.findByCandidateId(1L)).thenReturn(Optional.of(report));
        when(jsonMapper.convertValue(report, ReportDTO.class)).thenReturn(dto);

        ReportDTO result = service.getCandidateReport(1L);
        assertEquals(dto, result);
    }

    @Test
    void getCandidateReport_notFound_throws() {
        when(reportRepository.findByCandidateId(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.getCandidateReport(1L));
    }

    @SuppressWarnings("unchecked")
    @Test
    void getCourtSearches_success() {
        List<CourtSearch> searches = List.of(new CourtSearch());
        List<CourtSearchDTO> dtoList = List.of(new CourtSearchDTO());

        when(courtSearchRepository.findByCandidateId(1L)).thenReturn(searches);
        when(jsonMapper.convertValue(eq(searches), any(TypeReference.class))).thenReturn(dtoList);

        List<CourtSearchDTO> result = service.getCourtSearches(1L);
        assertEquals(dtoList, result);
    }

    @Test
    void getCourtSearches_failure_throws() {
        when(courtSearchRepository.findByCandidateId(1L)).thenThrow(new RuntimeException("DB error"));
        assertThrows(RuntimeException.class, () -> service.getCourtSearches(1L));
    }

    @Test
    void getCandidateReportSummaryDTO_success() {
        List<CandidateReportSummaryDTO> summaries = List.of(new CandidateReportSummaryDTO());
        when(dao.findAllCandidateReportSummaries("search", "adjudication", "status")).thenReturn(summaries);

        List<CandidateReportSummaryDTO> result = service.getCandidateReportSummaryDTO("search", "adjudication", "status");
        assertEquals(summaries, result);
    }

    @Test
    void getCandidateReportSummaryDTO_failure_throws() {
        when(dao.findAllCandidateReportSummaries(any(), any(), any())).thenThrow(new RuntimeException("DB error"));
        assertThrows(RuntimeException.class, () -> service.getCandidateReportSummaryDTO(null, null, null));
    }

    @Test
    void getEmailInfo_success() throws Exception {
        Candidate candidate = new Candidate();
        candidate.setId(1L);
        candidate.setEmail("candidate@example.com");
        candidate.setName("John Doe");

        CourtSearch cs1 = new CourtSearch();
        cs1.setSearch("Driving while license suspended");
        CourtSearch cs2 = new CourtSearch();
        cs2.setSearch("Global Watchlist");

        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(courtSearchRepository.findByCandidateIdAndStatus(1L, "CONSIDER")).thenReturn(Arrays.asList(cs1, cs2));

        PreAdverseActionEmailInfoDto result = service.getEmailInfo(1L);

        verify(candidateRepository).findById(1L);
        verify(courtSearchRepository).findByCandidateIdAndStatus(1L, "CONSIDER");

        assertNotNull(result);
        assertEquals(Arrays.asList("Driving while license suspended", "Global Watchlist"), result.getAvailableCharges());
    }

    @Test
    void getEmailInfo_candidateNotFound_throws() {
        when(candidateRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.getEmailInfo(1L));
    }

    @Test
    void sendNotice_success() throws Exception {
        Candidate candidate = new Candidate();
        candidate.setId(1L);
        candidate.setEmail("candidate@example.com");
        candidate.setName("John Doe");

        Report report = new Report();

        CourtSearch cs1 = new CourtSearch();
        cs1.setSearch("Driving while license suspended");
        CourtSearch cs2 = new CourtSearch();
        cs2.setSearch("Global Watchlist");

        PreAdverseActionEmailInfoDto request = PreAdverseActionEmailInfoDto.builder()
                .simpleMessage(SimpleMailMessageDTO.builder().subject("Subject").build())
                .availableCharges(List.of("Driving while license suspended", "Global Watchlist"))
                .autoSendDuration(10L)
                .build();

        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(reportRepository.findByCandidateId(1L)).thenReturn(Optional.of(report));
        when(courtSearchRepository.findByCandidateIdAndStatus(1L, "CONSIDER")).thenReturn(Arrays.asList(cs1, cs2));
        when(templateEngine.process(eq("pre-adverse-action-notice"), any(Context.class))).thenReturn("Email Body");
        when(reportRepository.save(report)).thenReturn(report);

        assertDoesNotThrow(() -> service.sendNotice(1L, request));

        verify(emailService).sendEmail(any(SimpleMailMessage.class));
        assertEquals("ADVERSE ACTION", report.getAdjudication());
        assertNotNull(report.getUpdatedAt());
        assertNotNull(report.getLastNotificationSent());
    }

    @Test
    void sendNotice_candidateNotFound_throws() {
        when(candidateRepository.findById(1L)).thenReturn(Optional.empty());
        PreAdverseActionEmailInfoDto dto = new PreAdverseActionEmailInfoDto();
        assertThrows(RuntimeException.class, () -> service.sendNotice(1L, dto));
    }

    @Test
    void sendNotice_reportNotFound_throws() {
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(new Candidate()));
        when(reportRepository.findByCandidateId(1L)).thenReturn(Optional.empty());
        PreAdverseActionEmailInfoDto dto = new PreAdverseActionEmailInfoDto();
        assertThrows(RuntimeException.class, () -> service.sendNotice(1L, dto));
    }
}
