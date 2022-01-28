package com.dn.DNApi.Facades.Utils.IPGeolocation;

import com.dn.DNApi.Configurations.Env;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class IPGeolocationService {
    @Autowired
    Env env;
    private static final Logger logger = LoggerFactory.getLogger(IPGeolocationService.class);


    public GWGeoLocalization getLocation(String ipAddress) {
        if(ipAddress.equals("0:0:0:0:0:0:0:1") || ipAddress.equals("127.0.0.1"))
            ipAddress = "213.225.2.120";
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity requestEntity = new HttpEntity(getHeaders());
        String uri = env.getProperty("ipstack.url") + ipAddress+ "?access_key=" + env.getProperty("ipstack.apikey");
        ResponseEntity<IPLookupResponse> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, IPLookupResponse.class);
        GWGeoLocalization localization = new GWGeoLocalization();
        IPLookupResponse ipLookupResponse = response.getBody();
        if(ipLookupResponse != null){
            localization.setCityName(ipLookupResponse.getCity());
            localization.setCountryName(ipLookupResponse.getCountry_code());
            localization.setPostalCode(ipLookupResponse.getZip());
            localization.setRegionCode(ipLookupResponse.getRegion_name());
            if(ipLookupResponse.getLocation() != null && ipLookupResponse.getLocation().getLanguages() != null){
                if(ipLookupResponse.getLocation().getLanguages().size() >= 1){
                    IPLangages lang = ipLookupResponse.getLocation().getLanguages().stream().findFirst().orElse(null);
                    if(lang != null){
                        localization.setLanguage(lang.getCode());
                    }else{
                        localization.setLanguage("en");
                    }
                }
            }
        }


        return localization;
    }

    private HttpHeaders getHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        // Request to return JSON format
        return headers;
    }
}