package com.dn.DNApi.Services.Recaptcha.Exceptions;

public class InvalidReCaptchaException extends Throwable {
    private String error;
    public InvalidReCaptchaException(String message) {
        this.error = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
