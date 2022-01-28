package com.dn.DNApi.Controllers;

import com.dn.DNApi.Configurations.Env;
import com.dn.DNApi.Facades.ProcessorFacade;
import com.dn.DNApi.Facades.Utils.IPGeolocation.IPUtils;
import com.dn.DNApi.Services.Mail.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    @Autowired
    Env env;

    @Autowired
    MailService mailService;

    @Autowired
    IPUtils ipUtils;

    @Autowired
    ProcessorFacade processorFacade;

    @RequestMapping("/")
    public String mainEndpoint(){
        return (String) env.getProperty("application.api.welcomeprobe");
    }

    @RequestMapping("/sendtestmail")
    public String sendTestMail(@RequestParam String to) {
        mailService.sendTestMail(to);
        return "Sent!";
    }
    @RequestMapping("/spamcheck")
    public String sendSpamMail(){
        mailService.sendSpamcheckMail();
        return "OK!";
    }
    @RequestMapping("/ip2country")
    public String ip2Country(@RequestParam String ip){
        return ipUtils.getLangFromIP(ip);
    }

    @RequestMapping("/queuestatus")
    public String queueStatus(){
        return processorFacade.getQueueInfo();
    }
}
