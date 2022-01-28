package com.dn.DNApi.Domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("CaptchaAttempts")
public class CaptchaAttempt {
    @Id
    private String id;
    private String ip;
    private Date date;
    private String tokenUsed;
    private String email;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTokenUsed() {
        return tokenUsed;
    }

    public void setTokenUsed(String tokenUsed) {
        this.tokenUsed = tokenUsed;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
