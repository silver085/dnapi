package com.dn.DNApi.Services.IPRisk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IPRiskResponse {
    private String IpAddress;
    private String ISP;
    private String Proxy;
    private String ProxyType;
    private String CountryCode;
    private String CountryName;
    private String RegionName;
    private String CityName;
    private String MobileCarrier;

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

    public String getIpAddress() {
        return IpAddress;
    }

    public void setIpAddress(String ipAddress) {
        IpAddress = ipAddress;
    }

    public String getISP() {
        return ISP;
    }

    public void setISP(String ISP) {
        this.ISP = ISP;
    }

    public String getCountryCode() {
        return CountryCode;
    }

    public void setCountryCode(String countryCode) {
        CountryCode = countryCode;
    }

    public String getCountryName() {
        return CountryName;
    }

    public void setCountryName(String countryName) {
        CountryName = countryName;
    }

    public String getRegionName() {
        return RegionName;
    }

    public void setRegionName(String regionName) {
        RegionName = regionName;
    }

    public String getCityName() {
        return CityName;
    }

    public void setCityName(String cityName) {
        CityName = cityName;
    }

    public String getMobileCarrier() {
        return MobileCarrier;
    }

    public void setMobileCarrier(String mobileCarrier) {
        MobileCarrier = mobileCarrier;
    }
}
