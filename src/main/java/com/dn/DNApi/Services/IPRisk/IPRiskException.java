package com.dn.DNApi.Services.IPRisk;

public class IPRiskException extends Throwable {
    String message;

    public IPRiskException(String message) {
        this.message = message;
    }
}
