package com.dn.DNApi.DTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class BaseResponse{
    private boolean error;
    private String exception;
    private String message;

    public BaseResponse() {
        this.error = false;
    }

    public BaseResponse(boolean error, String exception) {
        this.error = error;
        this.exception = exception;
    }

    public BaseResponse(String message){
        this.message = message;
        this.error = false;
        this.exception = null;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
