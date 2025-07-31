package com.harsh.pre_adverse_action.pre_adverse_action.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerUnitTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @SuppressWarnings("unchecked")
    @Test
    void handleDataAccessException_withRootCause() {
        DataAccessException ex = new DataAccessException("DB failure") {
            @Override
            public Throwable getRootCause() {
                return new RuntimeException("Root cause message");
            }
        };

        ResponseEntity<Object> response = handler.handleDataAccessException(ex);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        Object body = response.getBody();
        assertNotNull(body, "Response body should not be null");
        Map<String, Object> map = assertInstanceOf(Map.class, body, "Response body should be a Map");

        assertTrue(map.containsKey("timestamp"));
        assertEquals("Database error", map.get("error"));
        assertEquals("Root cause message", map.get("message"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void handleDataAccessException_withoutRootCause() {
        DataAccessException ex = new DataAccessException("DB failure") {
            @Override
            public Throwable getRootCause() {
                return null;
            }

            @Override
            public String getMessage() {
                return "No root cause message";
            }
        };

        ResponseEntity<Object> response = handler.handleDataAccessException(ex);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        Object body = response.getBody();
        assertNotNull(body, "Response body should not be null");

        Map<String, Object> map = (Map<String, Object>) body;

        assertEquals("Database error", map.get("error"));
        assertEquals("No root cause message", map.get("message"));
    }


    @SuppressWarnings("unchecked")
    @Test
    void handleGenericException() {
        Exception ex = new Exception("Generic failure");

        ResponseEntity<Object> response = handler.handleGenericException(ex);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        Object body = response.getBody();
        assertNotNull(body, "Response body should not be null");

        Map<String, Object> map = (Map<String, Object>) body;

        assertTrue(map.containsKey("timestamp"));
        assertEquals("Internal Server Error", map.get("error"));
        assertEquals("Generic failure", map.get("message"));
    }
}
