package com.dn.DNApi.Facades.Utils.IPGeolocation;

import com.dn.DNApi.Domain.IPRiskList;
import com.dn.DNApi.Services.IPRisk.IPRiskResponse;
import com.dn.DNApi.Services.IPRisk.IPRiskService;
import com.maxmind.geoip2.WebServiceClient;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.Country;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;

@Service
public class IPUtils {
    @Autowired
    IPRiskService ipRiskService;

    public String getLangFromIP(String ip) {
        IPRiskResponse iplookup = getIPLookup(ip);
        if(iplookup.getCountryCode().equalsIgnoreCase("it"))
            return "it";
        return "en";
    }


    public IPRiskResponse getIPLookup(String ip) {
        return ipRiskService.lookupIP(ip);
    }
}
