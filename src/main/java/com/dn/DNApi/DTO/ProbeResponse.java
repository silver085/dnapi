package com.dn.DNApi.DTO;

import org.springframework.http.HttpStatus;

public class ProbeResponse extends BaseResponse{
    private String apiVersion;
    private String appVersion;
    private String message;


    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
