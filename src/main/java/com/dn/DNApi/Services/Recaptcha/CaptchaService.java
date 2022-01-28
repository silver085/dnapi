package com.dn.DNApi.Services.Recaptcha;

import com.dn.DNApi.Configurations.Env;
import com.dn.DNApi.Domain.CaptchaAttempt;
import com.dn.DNApi.Repositories.CaptchaAttemptsRepository;
import com.dn.DNApi.Services.IPRisk.IPRiskService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class CaptchaService {
    @Autowired
    Env env;
    @Autowired
    CaptchaAttemptsRepository captchaAttemptsRepository;
    @Autowired
    IPRiskService ipRiskService;

    String recaptchaSecret;

    private static final String GOOGLE_RECAPTCHA_VERIFY_URL =
            "https://www.google.com/recaptcha/api/siteverify";

    @Autowired
    RestTemplateBuilder restTemplateBuilder;

    private static final Logger logger = LoggerFactory.getLogger(CaptchaService.class);

    @PostConstruct
    void init() {
        this.recaptchaSecret = (String) env.getProperty("google.recaptcha.key.secret");
    }


    public boolean verifyRecaptcha(String ip,
                                   String recaptchaResponse) {
        Map<String, String> body = new HashMap<>();
        body.put("secret", recaptchaSecret);
        body.put("response", recaptchaResponse);
        body.put("remoteip", ip);
      //  logger.info("Request body for recaptcha: {}", body);
        ResponseEntity<Map> recaptchaResponseEntity =
                restTemplateBuilder.build()
                        .postForEntity(GOOGLE_RECAPTCHA_VERIFY_URL +
                                        "?secret={secret}&response={response}&remoteip={remoteip}",
                                body, Map.class, body);

        //logger.info("Response from recaptcha: {}",
        //        recaptchaResponseEntity);
        Map<String, Object> responseBody =
                recaptchaResponseEntity.getBody();

        boolean recaptchaSucess = (Boolean) responseBody.get("success");
        return recaptchaSucess;
    }

    public boolean isValidRequest(String action, String email, String ip, String captcha) {
        if (captchaAttemptsRepository.countAllByIp(ip) > 5) {
            ipRiskService.banIP(ip);
        }

        boolean result = this.verifyRecaptcha(ip, captcha);
        logger.info("CAPTCHA VALIDATION Action {} for email: {} with IP: {} Result: {}" , action, email, ip , result);

        if(!result){
            CaptchaAttempt attempt = new CaptchaAttempt();
            attempt.setIp(ip);
            attempt.setDate(new Date());
            attempt.setEmail(email);
            attempt.setTokenUsed(captcha);
            captchaAttemptsRepository.save(attempt);
        }

        return result;
    }
}
