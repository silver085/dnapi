package com.dn.DNApi.Domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("IpRiskList")
public class IPRiskList {
    @Id
    private String id;
    private String ipAddress;
    private String ISP;
    private String Proxy;
    private String ProxyType;
    private Boolean whitelisted = false;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    
    public String getIpAddress() {
        return ipAddress;
    }

    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    
    public String getISP() {
        return ISP;
    }

    
    public void setISP(String ISP) {
        this.ISP = ISP;
    }

    
    public String getProxy() {
        return Proxy;
    }

    
    public void setProxy(String proxy) {
        Proxy = proxy;
    }

    
    public String getProxyType() {
        return ProxyType;
    }

    
    public void setProxyType(String proxyType) {
        ProxyType = proxyType;
    }

    public Boolean getWhitelisted() {
        return whitelisted;
    }

    public void setWhitelisted(Boolean whitelisted) {
        this.whitelisted = whitelisted;
    }
}
