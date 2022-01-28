package com.dn.DNApi.Services.Mail.MailGuard;

public class MailGuardException extends Throwable {
    String error;
    public MailGuardException(String s) {
        this.error = s;
    }
}
