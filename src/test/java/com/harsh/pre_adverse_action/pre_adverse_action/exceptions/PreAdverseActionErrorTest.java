package com.harsh.pre_adverse_action.pre_adverse_action.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PreAdverseActionErrorTest {

    @Test
    void testExceptionConstructor() {
        String message = "error occurred";
        Throwable cause = new RuntimeException("cause");
        PreAdverseActionError ex = new PreAdverseActionError(message, cause);

        assertEquals(message, ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
}
