package com.dn.DNApi.Services.Sniper;

import com.dn.DNApi.Domain.DeviceIdentifier;
import com.dn.DNApi.Domain.LoginHistory;
import com.dn.DNApi.Domain.User;
import com.dn.DNApi.Facades.AuthenticationFacade;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MultiAccountSniper {
    @Autowired
    MongoOperations mongoOperations;
    private static final Logger logger = LoggerFactory.getLogger(MultiAccountSniper.class);

    private List<User> getAllUserWithSameLocation(User u) {
        List<String> cities = u.getLoginHistory().stream().map(LoginHistory::getCity).collect(Collectors.toList());
        ;
        Query q = new Query(Criteria.where("loginHistory.city").in(cities));
        return mongoOperations.find(q, User.class);
    }

    private List<User> getAllUserWithSameDeviceHash(User u) {
        List<String> hashes = u.getDeviceIdentifiersHistory().stream().map(DeviceIdentifier::getDeviceHash).collect(Collectors.toList());
        Query q = new Query(Criteria.where("deviceIdentifiersHistory.deviceHash").in(hashes));
        //q.addCriteria(Criteria.where("banned").is(false));
        return mongoOperations.find(q, User.class);
    }

    public boolean evaluateUser(User user) {
        boolean isBanned = false;
        int score = 0;
        logger.info("[Sniper] Starting Sniper on user {} [{}]", user.getId(), user.getEmail());
        List<User> allUserFromSameCity = getAllUserWithSameLocation(user);
        List<User> allSamePass = allUserFromSameCity.stream().filter(u -> u.getPassword()!= null && u.getPassword().equalsIgnoreCase(user.getPassword())).collect(Collectors.toList());
        logger.info("[Sniper] User with same pass found: {}", allSamePass.size());
        if (allSamePass.size() > 1) {
            score++;
            logger.info("[Sniper] Score +1 (found at least 1 user with same location)");
        }
        List<User> userWithSameHash = getAllUserWithSameDeviceHash(user);
        if (userWithSameHash.size() >= 2) {
            score++;
            logger.info("[Sniper] Score +1 (found at least 2 user with same device hash)");
            List<String> tokens = new ArrayList<>();
            userWithSameHash.forEach(u -> {
                if (!tokens.contains(u.getToken())) {
                    tokens.add(u.getToken());
                }
            });
            tokens.add(user.getToken());
            if (tokens.size() > 1) {
                score = 10;
                logger.info("User high score, found {} tokens", tokens.size());
                assignSameToken(tokens, user.getToken());
                logger.info("Assigned same token to all found user {}" , user.getToken());
                //banUserFromTokenList(tokens);
                //isBanned = true;
                //logger.info("Banned {} users!", tokens.size());


            }
        }
        if (score >= 2) {
            logger.info("[Sniper] score higher than 2, marking as redFlag!");
            user.setRedFlag(true);
            mongoOperations.save(user);
        }

        return isBanned;
    }

    private void banUserFromTokenList(List<String> tokens) {
        Query q = new Query(Criteria.where("token").in(tokens));
        List<User> userlist = mongoOperations.find(q, User.class);
        userlist.forEach(user -> {
            user.setBanned(true);
            mongoOperations.save(user);
        });

    }

    private void assignSameToken(List<String> tokens, String validToken) {
        Query q = new Query(Criteria.where("token").in(tokens));
        List<User> userlist = mongoOperations.find(q, User.class);
        userlist.forEach(user -> {
            user.setToken(validToken);
            mongoOperations.save(user);
        });

    }
}
