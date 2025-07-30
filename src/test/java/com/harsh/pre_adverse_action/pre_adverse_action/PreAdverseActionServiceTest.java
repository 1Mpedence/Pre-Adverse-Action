package com.harsh.pre_adverse_action.pre_adverse_action;

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
import com.harsh.pre_adverse_action.pre_adverse_action.service.EmailService;
import com.harsh.pre_adverse_action.pre_adverse_action.service.PreAdverseActionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.SimpleMailMessage;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PreAdverseActionServiceTest {

    @InjectMocks
    private PreAdverseActionService service;

    @Mock private CandidateRepository candidateRepository;
    @Mock private ReportRepository reportRepository;
    @Mock private CourtSearchRepository courtSearchRepository;
    @Mock private Dao dao;
    @Mock private ObjectMapper jsonMapper;
    @Mock private TemplateEngine templateEngine;
    @Mock private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new PreAdverseActionService(candidateRepository, reportRepository, courtSearchRepository, dao, jsonMapper, templateEngine, emailService);
    }

    @Test
    void getCandidates_shouldReturnList() {
        List<Candidate> candidates = List.of(new Candidate());
        when(candidateRepository.findCandidateByActive(true)).thenReturn(candidates);
        when(jsonMapper.convertValue(any(), ArgumentMatchers.<TypeReference<List<CandidateDTO>>>any())).thenReturn(List.of(new CandidateDTO()));
        List<CandidateDTO> result = service.getCandidates();
        assertEquals(1, result.size());
    }

    @Test
    void getCandidates_shouldThrowOnError() {
        when(candidateRepository.findCandidateByActive(true)).thenThrow(RuntimeException.class);
        assertThrows(RuntimeException.class, () -> service.getCandidates());
    }

    @Test
    void getCandidateDetails_shouldReturnCandidate() {
        Candidate candidate = new Candidate();
        candidate.setId(1L);
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(jsonMapper.convertValue(any(), eq(CandidateDTO.class))).thenReturn(new CandidateDTO());
        CandidateDTO result = service.getCandidateDetails(1L);
        assertNotNull(result);
    }

    @Test
    void getCandidateDetails_shouldThrowIfNotFound() {
        when(candidateRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.getCandidateDetails(1L));
    }

    @Test
    void getCandidateReport_shouldReturnReport() {
        Report report = new Report();
        when(reportRepository.findByCandidateId(1L)).thenReturn(Optional.of(report));
        when(jsonMapper.convertValue(any(), eq(ReportDTO.class))).thenReturn(new ReportDTO());
        ReportDTO result = service.getCandidateReport(1L);
        assertNotNull(result);
    }

    @Test
    void getCandidateReport_shouldThrowIfNotFound() {
        when(reportRepository.findByCandidateId(anyLong())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.getCandidateReport(1L));
    }

    @Test
    void getCourtSearches_shouldReturnList() {
        List<CourtSearch> searches = List.of(new CourtSearch());
        when(courtSearchRepository.findByCandidateId(1L)).thenReturn(searches);
        when(jsonMapper.convertValue(any(), ArgumentMatchers.<TypeReference<List<CourtSearchDTO>>>any())).thenReturn(List.of(new CourtSearchDTO()));
        List<CourtSearchDTO> result = service.getCourtSearches(1L);
        assertEquals(1, result.size());
    }

    @Test
    void getCandidateReportSummaryDTO_shouldReturnList() {
        List<CandidateReportSummaryDTO> summaries = List.of(new CandidateReportSummaryDTO());
        when(dao.findAllCandidateReportSummaries(any(), any(), any())).thenReturn(summaries);
        List<CandidateReportSummaryDTO> result = service.getCandidateReportSummaryDTO(null, null, null);
        assertEquals(1, result.size());
    }

    @Test
    void getEmailInfo_shouldReturnDto() {
        Candidate candidate = new Candidate();
        candidate.setName("John");
        candidate.setEmail("john@example.com");
        candidate.setId(1L);

        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(templateEngine.process(eq("pre-adverse-action-notice"), any(Context.class))).thenReturn("email content");

        PreAdverseActionEmailInfoDto result = service.getEmailInfo(1L);
        assertEquals("john@example.com", result.getSimpleMessage().getTo()[0]);
        assertTrue(result.getAvailableCharges().size() > 0);
    }

    @Test
    void getEmailInfo_shouldThrowIfCandidateNotFound() {
        when(candidateRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.getEmailInfo(1L));
    }

    @Test
    void sendNotice_shouldSendAndUpdateReport() {
        Report report = new Report();
        when(candidateRepository.existsById(1L)).thenReturn(true);
        when(reportRepository.findByCandidateId(1L)).thenReturn(Optional.of(report));

        PreAdverseActionEmailInfoDto dto = PreAdverseActionEmailInfoDto.builder()
                .simpleMessage(SimpleMailMessageDTO.builder()
                        .from("test@checkr.com")
                        .to(new String[]{"john@example.com"})
                        .subject("Notice")
                        .text("Body").build())
                .autoSendDuration(3L)
                .build();

        service.sendNotice(1L, dto);

        verify(emailService).sendEmail(any(SimpleMailMessage.class));
        verify(reportRepository).save(any(Report.class));
    }

    @Test
    void sendNotice_shouldThrowIfCandidateNotFound() {
        when(candidateRepository.existsById(1L)).thenReturn(false);
        PreAdverseActionEmailInfoDto dto = PreAdverseActionEmailInfoDto.builder().simpleMessage(new SimpleMailMessageDTO()).build();
        assertThrows(RuntimeException.class, () -> service.sendNotice(1L, dto));
    }

    @Test
    void sendNotice_shouldThrowIfReportNotFound() {
        when(candidateRepository.existsById(1L)).thenReturn(true);
        when(reportRepository.findByCandidateId(1L)).thenReturn(Optional.empty());
        PreAdverseActionEmailInfoDto dto = PreAdverseActionEmailInfoDto.builder().simpleMessage(new SimpleMailMessageDTO()).build();
        assertThrows(RuntimeException.class, () -> service.sendNotice(1L, dto));
    }

    @Test
    void sendNotice_shouldHandleEmailServiceFailure() {
        Report report = new Report();
        when(candidateRepository.existsById(1L)).thenReturn(true);
        when(reportRepository.findByCandidateId(1L)).thenReturn(Optional.of(report));

        PreAdverseActionEmailInfoDto dto = PreAdverseActionEmailInfoDto.builder()
                .simpleMessage(SimpleMailMessageDTO.builder()
                        .from("test@checkr.com")
                        .to(new String[]{"john@example.com"})
                        .subject("Notice")
                        .text("Body").build())
                .autoSendDuration(3L)
                .build();

        doThrow(new RuntimeException("Email failure")).when(emailService).sendEmail(any());
        assertThrows(RuntimeException.class, () -> service.sendNotice(1L, dto));
    }
}
