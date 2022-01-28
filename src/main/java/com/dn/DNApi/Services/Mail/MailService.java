package com.dn.DNApi.Services.Mail;

import com.dn.DNApi.Configurations.LangMessages;
import com.dn.DNApi.Domain.*;
import com.dn.DNApi.Facades.Mail.MailSenderFacade;
import com.dn.DNApi.Repositories.MailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

@Service
public class MailService {
    private static final Logger logger = LoggerFactory.getLogger(MailService.class);
    @Autowired
    MailSenderFacade mailSenderFacade;

    @Autowired
    MailRepository mailRepository;

    @Autowired
    LangMessages messages;

    public void sendTestMail(String to) {
        String template = "baseTemplate";
        HashMap<String, String> values = new HashMap<>();
        values.put("salutation", "Hey " + to + ",");
        values.put("welcometext", "This is a test email!");
        values.put("message", "Meaning that it works with templates");
        values.put("footer", "Sent you with love from Menude.me");
        mailSenderFacade.prepareAndSend("Test mail" , to, values, template);
    }

    public void sendSpamcheckMail(){
        String template = "baseTemplate";
        HashMap<String, String> values = new HashMap<>();
        String to = "ipm2j67@glockapps.com, allanb@glockapps.awsapps.com, markb@glockapps.awsapps.com, ingridmejiasri@aol.com, caseywrighde@aol.de, baileehinesfr@aol.fr, brendarodgersuk@aol.co.uk, franprohaska@aol.com, garrettjacqueline@aol.com, leannamccoybr@aol.com, gappsglock@icloud.com, allanvspear@icloud.com, zacheryfoleyrx@azet.sk, bcc@spamcombat.com, chazb@userflowhq.com, glock.julia@bol.com.br, stevebarrydr@fastmail.com, carloscohenm@freenet.de, verify79@buyemailsoftware.com, janefergusone@gmail.com, llionelcohenbr@gmail.com, bbarretthenryhe@gmail.com, joanyedonald@gmail.com, emilikerr@gmail.com, wandammorrison@gmail.com, lawrenceleddylr@gmail.com, alisonnlawrence@gmail.com, tinamallahancr@gmail.com, louiepettydr@gmail.com, lenorebayerd@gmail.com, cierawilliamsonwq@gmail.com, silviacopelandqy@gmail.com, daishacorwingx@gmail.com, verifycom79@gmx.com, verifyde79@gmx.de, gd@desktopemail.com, jpatton@fastdirectorysubmitter.com, frankiebeckerp@hotmail.com, yadiraalfordbj@hotmail.com, sgorska12@interia.pl, layneguerreropm@laposte.net, britnigrahamap@laposte.net, amandoteo79@libero.it, glocktest@vendasrd.com.br, b2bdeliver79@mail.com, verifymailru79@mail.ru, glockapps@mc.glockapps.com, verify79ssl@netcourrier.com, nsallan@expertarticles.com, evalotta.wojtasik@o2.pl, exosf@glockeasymail.com, brendonosbornx@outlook.com, tristonreevestge@outlook.com.br, brittanyrocha@outlook.de, glencabrera@outlook.fr, christopherfranklinhk@outlook.com, kaceybentleyerp@outlook.com, meaghanwittevx@outlook.com, aileenjamesua@outlook.com, shannongreerf@outlook.com, gabrielharberh@outlook.com, candidobashirian@outlook.com, vincenzaeffertz@outlook.com, verify79@seznam.cz, sa79@justlan.com, amandoteo79@virgilio.it, verify79@web.de, sebastianalvarezv@yahoo.com.br, verifyca79@yahoo.ca, justynbenton@yahoo.com, testiotestiko@yahoo.co.uk, emailtester493@yahoo.com, loganbridgesrk@yahoo.com, rogertoddw@yahoo.com, darianhuffg@yahoo.com, andreablackburn@yandex.ru, verifynewssl@zoho.com, lamb@glockdb.com";
        values.put("salutation", "Hey id:2020-05-18-12:58:05:819t ,");
        values.put("welcometext", "This is a test email!");
        values.put("message", "Meaning that it works with templates");
        values.put("footer", "Sent you with love from Menude.me");
        String[] tos = to.split(",");
        for(String t : tos){
            mailSenderFacade.prepareAndSend("Test mail" , t, values, template);
        }

    }

    public void sendRegistrationEmail(User user, String mailToken) {
        try {
            String template = "baseTemplate";
            HashMap<String, String> values = new HashMap<>();
            values.put("salutation", messages.getLocalizedMessage("registration.title" , user.getBaseLang()));
            values.put("welcometext", "");
            String welcomeMsg = messages.getLocalizedMessage("registration.msg", user.getBaseLang());
            try{
                welcomeMsg = welcomeMsg.replace("%token%" , mailToken);
            }catch (Exception e){
                logger.error("Error replacing token: {}" , e.getMessage());
            }

            values.put("message", welcomeMsg);
            values.put("footer", messages.getLocalizedMessage("mail.footer" , user.getBaseLang()));
            mailSenderFacade.prepareAndSend(messages.getLocalizedMessage("registration.subject" , user.getBaseLang()),
                    user.getEmail(),
                    values,
                    template
            );
        }catch (Exception e){
            Mail mail = new Mail();
            mail.setDate(new Date());
            mail.setSubject(MailErrorsConstants.WELCOME_MAIL_ERROR);
            mail.setContent(e.getMessage());
            mail.setLastError(Arrays.toString(e.getStackTrace()));
            mail.setTo(user.getEmail());
            mailRepository.save(mail);
        }


    }

    public void sendPasswordRecoverCode(User user, String passCode) {
        try{
            String template = "baseTemplate";
            HashMap<String, String> values = new HashMap<>();
            values.put("salutation", messages.getLocalizedMessage("passwordlost.title" , user.getBaseLang()));
            values.put("welcometext", "");
            String welcomeMsg = messages.getLocalizedMessage("passwordlost.msg", user.getBaseLang());
            welcomeMsg = welcomeMsg.replace("%code%" , passCode);
            values.put("message", welcomeMsg);
            values.put("footer", messages.getLocalizedMessage("mail.footer" , user.getBaseLang()));
            mailSenderFacade.prepareAndSend(messages.getLocalizedMessage("passwordlost.subject" , user.getBaseLang()),
                    user.getEmail(),
                    values,
                    template
            );
        }catch(Exception e){
            Mail mail = new Mail();
            mail.setDate(new Date());
            mail.setSubject(MailErrorsConstants.RECOVER_EMAIL_ERROR);
            mail.setContent(e.getMessage());
            mail.setLastError(Arrays.toString(e.getStackTrace()));
            mail.setTo(user.getEmail());
            mailRepository.save(mail);
        }

    }

    public void sendPasswordReset(User user, String newPass) {
        try{
            String template = "baseTemplate";
            HashMap<String, String> values = new HashMap<>();
            values.put("salutation", messages.getLocalizedMessage("passwordreset.title" , user.getBaseLang()));
            values.put("welcometext", "");
            String welcomeMsg = messages.getLocalizedMessage("passwordreset.msg", user.getBaseLang());
            welcomeMsg = welcomeMsg.replace("%pass%" , newPass);
            values.put("message", welcomeMsg);
            values.put("footer", messages.getLocalizedMessage("mail.footer" , user.getBaseLang()));
            mailSenderFacade.prepareAndSend(messages.getLocalizedMessage("passwordreset.subject" , user.getBaseLang()),
                    user.getEmail(),
                    values,
                    template
            );

        }catch (Exception e){
            Mail mail = new Mail();
            mail.setDate(new Date());
            mail.setSubject(MailErrorsConstants.RECOVER_PASS_ERROR);
            mail.setContent(e.getMessage());
            mail.setLastError(Arrays.toString(e.getStackTrace()));
            mail.setTo(user.getEmail());
            mailRepository.save(mail);
        }



    }

    public void sendPurchaseCompletedEmail(User user, Order order, Transaction transaction, BuyItem item) {
        try{
            String template = "baseTemplate";
            HashMap<String, String> values = new HashMap<>();
            values.put("salutation", messages.getLocalizedMessage("purchase.title" , user.getBaseLang()));
            values.put("welcometext", "");
            String welcomeMsg = messages.getLocalizedMessage("purchase.msg", user.getBaseLang());
            welcomeMsg = welcomeMsg.replace("%token%" , String.valueOf(item.getTokens()));
            values.put("message", welcomeMsg);
            values.put("footer", messages.getLocalizedMessage("mail.footer" , user.getBaseLang()));
            mailSenderFacade.prepareAndSend(messages.getLocalizedMessage("purchase.subject" , user.getBaseLang()),
                    user.getEmail(),
                    values,
                    template
            );

        }catch (Exception e){
            Mail mail = new Mail();
            mail.setDate(new Date());
            mail.setSubject(MailErrorsConstants.PURCHASE_COMPLETED);
            mail.setContent(e.getMessage());
            mail.setLastError(Arrays.toString(e.getStackTrace()));
            mail.setTo(user.getEmail());
            mailRepository.save(mail);
        }
    }
}
