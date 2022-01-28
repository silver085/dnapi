package com.dn.DNApi.Configurations;

import com.dn.DNApi.Domain.BuyItem;
import com.dn.DNApi.Domain.LoginHistory;
import com.dn.DNApi.Domain.Session;
import com.dn.DNApi.Domain.User;
import com.dn.DNApi.Facades.Jobs.Clearners.MailsJob;
import com.dn.DNApi.Repositories.BuyItemRepository;
import com.dn.DNApi.Repositories.SessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Configuration
public class ApplicationReadyListener {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationReadyListener.class);

    @Autowired
    BuyItemRepository buyItemRepository;
    @Autowired
    MongoOperations mongoOperations;
    @Autowired
    SessionRepository sessionRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void applicationStartup(){
        logger.info("Starting application bootstrap...");
        buyItemRepository.deleteAll();

        if(buyItemRepository.count() == 0){
            logger.info("Buy items are 0, filling...");

            BuyItem token2 = new BuyItem();
            token2.setTextBundle("text.buytoken2");
            token2.setTextDescBundle("text.buytoken2");
            token2.setPrice(0.90);
            token2.setTokens(2);
            buyItemRepository.save(token2);

            BuyItem token5 = new BuyItem();
            token5.setTextBundle("text.buytoken5");
            token5.setTextDescBundle("text.buytoken5");
            token5.setPrice(2.00);
            token5.setTokens(5);
            buyItemRepository.save(token5);

            BuyItem token10 = new BuyItem();
            token10.setTextBundle("text.buytoken10");
            token10.setTextDescBundle("text.buytoken10");
            token10.setPrice(3.80);
            token10.setTokens(10);
            buyItemRepository.save(token10);

            BuyItem token100 = new BuyItem();
            token100.setTextBundle("text.buytoken100");
            token100.setTextDescBundle("text.buytoken100");
            token100.setPrice(28.0);
            token100.setTokens(100);
            buyItemRepository.save(token100);

            BuyItem token200 = new BuyItem();
            token200.setTextBundle("text.buytoken200");
            token200.setTextDescBundle("text.buytoken200");
            token200.setPrice(45.00);
            token200.setTokens(200);
            buyItemRepository.save(token200);

        }



    }
}
