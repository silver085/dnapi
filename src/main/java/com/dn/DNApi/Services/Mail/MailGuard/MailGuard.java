package com.dn.DNApi.Services.Mail.MailGuard;

import com.dn.DNApi.Configurations.Env;
import com.dn.DNApi.Domain.MailRisk;
import com.dn.DNApi.Facades.AuthenticationFacade;
import com.dn.DNApi.Repositories.MailRiskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class MailGuard {
    @Autowired
    Env env;
    @Autowired
    MailRiskRepository mailRiskRepository;

    private static final Logger logger = LoggerFactory.getLogger(MailRiskRepository.class);

    public void verifyEmail(String email) throws MailGuardException {
        if(isPresentInRepo(email)){
            throw new MailGuardException("error.wrongmail");
        }
        String apiUrl = (String) env.getProperty("mailguard.apiurl");
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity requestEntity = new HttpEntity(getHeaders());
        String uri = apiUrl + email;
        ResponseEntity<MailGuardResponse> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, MailGuardResponse.class);
        if(response.getBody() != null){
            if(response.getBody().isDisposable()){
                    addToRepository(email);
                    throw new MailGuardException("error.wrongmail");
            } else {
                if(!verifyToHost(email)){
                    throw new MailGuardException("error.wrongmail");
                }
            }
        }
    }

    private boolean verifyToHost(String email) {
        Socket socket;
        String host = email.split("@")[1];
        boolean succeeded = false;
        int port = 443;
        try {
            socket = new Socket(host, port); //https port
            succeeded = true;
            socket.close();
            logger.info("Host {} has been verified at port {}" , host, port);
        } catch (UnknownHostException e){
            logger.info("Error connecting to {} at port {} - Unknown host, mail rejected" , host, port );
        } catch (IOException e) {
            logger.info("Error connecting to {} at port {} : Error: {} - mail rejected" , host, port, e.getMessage() );
        }

        return succeeded;

    }

    private boolean isPresentInRepo(String email){
        String domainName = email.split("@")[1];
        return mailRiskRepository.countByDomainName(domainName)> 0;
    }

    private void addToRepository(String email){
        String domainName = email.split("@")[1];
        if(!isPresentInRepo(email)){
            MailRisk mailRisk = new MailRisk();
            mailRisk.setDomainName(domainName);
            mailRisk.setDate(new Date());
            mailRiskRepository.save(mailRisk);
        }
    }

    private HttpHeaders getHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("user-agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.61 Safari/537.36");
        // Request to return JSON format
        return headers;
    }
}
