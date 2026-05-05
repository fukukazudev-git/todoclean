package com.example.todoclean.exception;

import java.time.LocalDateTime;
import java.util.List;
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private List<FieldValidationError> errors; // ← 追加

    public ErrorResponse(int status, String error, String message, String path, List<FieldValidationError> errors) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.errors = errors;
    }

    public LocalDateTime getTimestamp(){return timestamp;}

    public int getStatus(){return status;}

    public String getError(){return error;}

    public String getMessage(){return message;}
    
    public String getPath(){return path;}

    public List<FieldValidationError> getErrors(){return errors;}
    
}