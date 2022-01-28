package com.dn.DNApi.Domain;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("MailRisk")
public class MailRisk {
    private String domainName;
    private Date date;
    private boolean enabled = true;

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
