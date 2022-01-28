package com.dn.DNApi.Facades.Jobs.Clearners;

import com.dn.DNApi.Domain.Mail;
import com.dn.DNApi.Domain.User;
import com.dn.DNApi.Facades.AuthenticationFacade;
import com.dn.DNApi.Repositories.MailRepository;
import com.dn.DNApi.Repositories.UserRepository;
import com.dn.DNApi.Services.Mail.MailErrorsConstants;
import com.dn.DNApi.Services.Mail.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MailsJob {

    @Autowired
    MailRepository mailRepository;
    @Autowired
    MongoOperations mongoOperations;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MailService mailService;
    @Autowired
    AuthenticationFacade authenticationFacade;
    private static final Logger logger = LoggerFactory.getLogger(MailsJob.class);
    private boolean blocked = false;

    @Scheduled(fixedDelay = 10 * 60 * 1000)
    public void retryFailedEmails(){
        if(this.blocked)
            return;
        blocked = true ;

        Query query = new Query();
        query.addCriteria(Criteria.where("lastError").exists(true));
        query.addCriteria(Criteria.where("retried").exists(false));
        List<Mail> onErrorMails = mongoOperations.find(query, Mail.class);
        onErrorMails.forEach(this::retryMail);
        blocked = false ;
    }

    private void retryMail(Mail m){
        blocked = true ;
        m.setRetried(true);
        mailRepository.save(m);

        String to = m.getTo();
        String subject = m.getSubject();

        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(to));
        List<User> users = mongoOperations.find(query, User.class);
        User user = users.stream().findFirst().orElse(null);
        if(user == null){
            blocked = false;
            return;
        }
        if(user.getBaseLang() == null){
            user = authenticationFacade.updateUserWithLang(user);
        }

        logger.info("Retring " + subject + " for " + to );
        if(subject.equals(MailErrorsConstants.WELCOME_MAIL_ERROR)){

            mailService.sendRegistrationEmail(user, user.getEmailToken());
        } else if (subject.equals(MailErrorsConstants.RECOVER_EMAIL_ERROR)){
            mailService.sendPasswordRecoverCode(user, user.getEmailToken());
        } else if (subject.equals(MailErrorsConstants.RECOVER_PASS_ERROR)){
            mailService.sendPasswordReset(user, user.getPassword());
        }
        blocked = false;
    }
}
