package com.dn.DNApi.Domain;

import java.util.Date;

public class LocalStorageHistory {
    private String lsString;
    private Date date;

    public String getLsString() {
        return lsString;
    }

    public void setLsString(String lsString) {
        this.lsString = lsString;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
