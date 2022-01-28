package com.dn.DNApi.Services.IPRisk;

import com.dn.DNApi.Configurations.Env;
import com.dn.DNApi.Domain.IPRiskList;
import com.dn.DNApi.Repositories.IPRiskRepository;
import com.dn.DNApi.Services.Mail.MailGuard.MailGuardResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class IPRiskService {
    @Autowired
    IPRiskRepository ipRiskRepository;
    @Autowired
    Env env;
    private static final Logger logger = LoggerFactory.getLogger(IPRiskService.class);


    public void evaluateIp(String ip) throws IPRiskException {

        String uri = (String) env.getProperty("ipguard.apiurl");
        uri = uri.replace("{ip}", ip);
        IPRiskResponse validation = null;
        if (ipRiskRepository.countByIpAddress(ip) > 0) {
            IPRiskList riskDB = ipRiskRepository.getByIpAddress(ip).get(0);
            if (riskDB != null) {
                validation = new IPRiskResponse();
                BeanUtils.copyProperties(riskDB, validation);
                if(riskDB.getWhitelisted()){
                    logger.info("IP {} is whitelisted!" , ip);
                   return ;
                }
                if(riskDB.getProxyType().equalsIgnoreCase("Ban")){
                    throw new IPRiskException("Banned manually");
                }
            }
        }

        if (validation == null) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                HttpEntity requestEntity = new HttpEntity(getHeaders());
                ResponseEntity<Object> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, Object.class);
                LinkedHashMap<String, String> res = (LinkedHashMap<String, String>) response.getBody();
                validation = convertToResponse(res);

            } catch (Exception e) {
                logger.error("Error during IPRisk API: {}", e.getMessage());
            }
        }

        if (validation != null) {
            if (validation.getProxy().equalsIgnoreCase("yes")) {
                IPRiskList toAdd = new IPRiskList();
                if(ipRiskRepository.countByIpAddress(ip) == 0){
                    BeanUtils.copyProperties(validation, toAdd);
                    ipRiskRepository.save(toAdd);
                }
                throw new IPRiskException("error.proxydetected");
            }
        }

    }

    public IPRiskResponse lookupIP(String ip){
        if(ip.equals("127.0.0.1")){
            ip = "151.57.215.238";
        }
        String uri = (String) env.getProperty("ipguard.apiurl");
        uri = uri.replace("{ip}", ip);
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity requestEntity = new HttpEntity(getHeaders());
        ResponseEntity<Object> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, Object.class);
        LinkedHashMap<String, String> res = (LinkedHashMap<String, String>) response.getBody();
        return convertToResponse(res);
    }

    private IPRiskResponse convertToResponse(LinkedHashMap<String, String> origin) {
        IPRiskResponse response = new IPRiskResponse();
        BeanUtils.copyProperties(origin, response);
        if (origin.get("Proxy") != null) {
            response.setProxy(origin.get("Proxy"));
        }
        if (origin.get("ProxyType") != null) {
            response.setProxyType(origin.get("ProxyType"));
        }
        if (origin.get("IpAddress") != null) {
            response.setIpAddress(origin.get("IpAddress"));
        }
        if (origin.get("ISP") != null) {
            response.setISP(origin.get("ISP"));
        }
        if(origin.get("CountryName")!=null){
            response.setCountryName(origin.get("CountryName"));
        }
        if(origin.get("CountryCode")!=null){
            response.setCountryCode(origin.get("CountryCode"));
        }
        if(origin.get("RegionName") != null){
            response.setRegionName(origin.get("RegionName"));
        }
        if(origin.get("CityName") != null){
            response.setCityName(origin.get("CityName"));
        }
        if(origin.get("MobileCarrier") != null){
            response.setMobileCarrier(origin.get("MobileCarrier"));
        }

        return response;
    }

    private HttpHeaders getHeaders() {
        String host = (String) env.getProperty("ipguard.apihost");
        String key = (String) env.getProperty("ipguard.apikey");
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("x-rapidapi-host", host);
        headers.set("x-rapidapi-key", key);
        headers.set("useQueryString", "true");
        // Request to return JSON format
        return headers;
    }

    public void banIP(String ip) {
        if(ipRiskRepository.countByIpAddress(ip) == 0){
            IPRiskList list = new IPRiskList();
            list.setIpAddress(ip);
            list.setProxy("None/Banned manually");
            list.setISP("None");
            list.setProxyType("Ban");
            ipRiskRepository.save(list);
        }

    }
}
