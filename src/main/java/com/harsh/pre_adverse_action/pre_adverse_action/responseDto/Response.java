package com.harsh.pre_adverse_action.pre_adverse_action.responseDto;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class Response<T> {
    private boolean success;
    private String message;
    private int statusCode;
    private T data;
    private Timestamp timestamp;
    private String path;
    private Object errorDetails;
}
