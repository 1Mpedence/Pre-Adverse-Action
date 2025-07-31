package com.harsh.pre_adverse_action.pre_adverse_action.controller;

import com.harsh.pre_adverse_action.pre_adverse_action.dtos.*;
import com.harsh.pre_adverse_action.pre_adverse_action.exceptions.PreAdverseActionError;
import com.harsh.pre_adverse_action.pre_adverse_action.service.PreAdverseActionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PreAdverseActionControllerTest {

    @Mock
    PreAdverseActionService service;

    @InjectMocks
    PreAdverseActionController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getCandidates_success() {
        List<CandidateDTO> candidates = List.of(new CandidateDTO());
        when(service.getCandidates()).thenReturn(candidates);

        ResponseEntity<List<CandidateDTO>> response = controller.getCandidates();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(candidates, response.getBody());
    }

    @Test
    void getCandidates_failure_throws() {
        when(service.getCandidates()).thenThrow(new RuntimeException("error"));
        PreAdverseActionError exception = assertThrows(PreAdverseActionError.class, () -> controller.getCandidates());
        assertTrue(exception.getMessage().contains("Failed to fetch candidates"));
    }

    @Test
    void getCandidateDetails_success() {
        CandidateDTO dto = new CandidateDTO();
        when(service.getCandidateDetails(1L)).thenReturn(dto);

        ResponseEntity<CandidateDTO> response = controller.getCandidateDetails(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void getCandidateDetails_failure_throws() {
        when(service.getCandidateDetails(1L)).thenThrow(new RuntimeException("error"));
        PreAdverseActionError exception = assertThrows(PreAdverseActionError.class, () -> controller.getCandidateDetails(1L));
        assertTrue(exception.getMessage().contains("Failed to fetch candidates for id"));
    }

    @Test
    void getReport_success() {
        ReportDTO dto = new ReportDTO();
        when(service.getCandidateReport(1L)).thenReturn(dto);

        ResponseEntity<ReportDTO> response = controller.getReport(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void getReport_failure_throws() {
        when(service.getCandidateReport(1L)).thenThrow(new RuntimeException("error"));
        PreAdverseActionError exception = assertThrows(PreAdverseActionError.class, () -> controller.getReport(1L));
        assertTrue(exception.getMessage().contains("Error fetching report for candidate ID"));
    }

    @Test
    void getCourtSearches_success() {
        List<CourtSearchDTO> list = List.of(new CourtSearchDTO());
        when(service.getCourtSearches(1L)).thenReturn(list);

        ResponseEntity<List<CourtSearchDTO>> response = controller.getCourtSearches(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(list, response.getBody());
    }

    @Test
    void getCourtSearches_failure_throws() {
        when(service.getCourtSearches(1L)).thenThrow(new RuntimeException("error"));
        PreAdverseActionError exception = assertThrows(PreAdverseActionError.class, () -> controller.getCourtSearches(1L));
        assertTrue(exception.getMessage().contains("Error fetching court searches for candidate ID"));
    }

    @Test
    void getCandidateSummary_success() {
        List<CandidateReportSummaryDTO> list = List.of(new CandidateReportSummaryDTO());
        when(service.getCandidateReportSummaryDTO("search", "adj", "status")).thenReturn(list);

        ResponseEntity<List<CandidateReportSummaryDTO>> response = controller.getCandidateSummary("search", "adj", "status");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(list, response.getBody());
    }

    @Test
    void getCandidateSummary_failure_throws() {
        when(service.getCandidateReportSummaryDTO(any(), any(), any())).thenThrow(new RuntimeException("error"));
        PreAdverseActionError exception = assertThrows(PreAdverseActionError.class, () -> controller.getCandidateSummary(null, null, null));
        assertTrue(exception.getMessage().contains("Error fetching candidates"));
    }

    @Test
    void getEmailInfo_success() {
        PreAdverseActionEmailInfoDto dto = PreAdverseActionEmailInfoDto.builder().build();
        when(service.getEmailInfo(1L)).thenReturn(dto);

        ResponseEntity<PreAdverseActionEmailInfoDto> response = controller.getEmailInfo(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void getEmailInfo_failure_throws() {
        when(service.getEmailInfo(1L)).thenThrow(new RuntimeException("err"));
        PreAdverseActionError exception = assertThrows(PreAdverseActionError.class, () -> controller.getEmailInfo(1L));
        assertTrue(exception.getMessage().contains("Error while fetching email body"));
    }

    @Test
    void sendPreAdverseNotice_success() {
        doNothing().when(service).sendNotice(eq(1L), any(PreAdverseActionEmailInfoDto.class));

        ResponseEntity<String> response = controller.sendPreAdverseNotice(1L, PreAdverseActionEmailInfoDto.builder().build());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Pre-Advance Action Notice successfully sent.", response.getBody());
    }

    @Test
    void sendPreAdverseNotice_failure_throws() {
        doThrow(new RuntimeException("err")).when(service).sendNotice(eq(1L), any());

        PreAdverseActionError exception = assertThrows(PreAdverseActionError.class,
                () -> controller.sendPreAdverseNotice(1L, PreAdverseActionEmailInfoDto.builder().build()));
        assertTrue(exception.getMessage().contains("Error while sending pre-adverse email for candidateId"));
    }
}
