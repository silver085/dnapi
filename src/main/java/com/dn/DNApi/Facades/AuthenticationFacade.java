package com.dn.DNApi.Facades;

import com.dn.DNApi.Configurations.Env;
import com.dn.DNApi.DTO.*;
import com.dn.DNApi.Domain.*;
import com.dn.DNApi.Facades.Utils.IPGeolocation.IPUtils;
import com.dn.DNApi.Facades.Utils.Utils;
import com.dn.DNApi.Facades.Utils.Validators;
import com.dn.DNApi.Repositories.SessionRepository;
import com.dn.DNApi.Repositories.UserRepository;
import com.dn.DNApi.Services.IPRisk.IPRiskException;
import com.dn.DNApi.Services.IPRisk.IPRiskResponse;
import com.dn.DNApi.Services.IPRisk.IPRiskService;
import com.dn.DNApi.Services.Mail.MailGuard.MailGuard;
import com.dn.DNApi.Services.Mail.MailGuard.MailGuardException;
import com.dn.DNApi.Services.Mail.MailService;
import com.dn.DNApi.Services.Recaptcha.CaptchaService;
import com.dn.DNApi.Services.Sniper.MultiAccountSniper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthenticationFacade {
    @Autowired
    SessionRepository sessionRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    Env env;
    @Autowired
    MailService mailService;
    @Autowired
    IPUtils ipUtils;
    @Autowired
    MailGuard mailGuard;
    @Autowired
    IPRiskService ipRiskService;
    @Autowired
    MultiAccountSniper sniper;
    @Autowired
    CaptchaService captchaService;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFacade.class);

    private String getIpFroHTTPRequest(HttpServletRequest request) throws IPRiskException {
        String ip;
        if (request.getHeader("X-FORWARDED-FOR") == null) {
            ip = request.getRemoteAddr();
            ip = ip.replace("0:0:0:0:0:0:0:1", "127.0.0.1");
        } else {
            ip = request.getHeader("X-FORWARDED-FOR");
        }
        String ipGuardEnabled = (String) env.getProperty("ipguard.enabled");
        if(ipGuardEnabled.equalsIgnoreCase("true")){
            try{
                ipRiskService.evaluateIp(ip);
            }catch (Exception | IPRiskException e){
                logger.info("IP Guard RISK Detection: {}" , ip);
                throw e;
            }
        }
        return ip;
    }
    private Session getOrCreateSession(String deviceId, HttpServletRequest request) throws Exception, IPRiskException {
        String ip = getIpFroHTTPRequest(request);

        if (deviceId == null) {
            throw new Exception("error.deviceidmissing");
        }
        if (ip == null) {
            throw new Exception("error.ipnotavailable");
        }

        Session session = getSessionByIpOrMac(ip, deviceId);
        if (session == null) {
            //no sessions generate new token
            session = new Session();
            int wages = Integer.parseInt((String) env.getProperty("application.processing.wadgesperday"));
            session.setWagesLeft(wages);
            session.setToken(java.util.UUID.randomUUID().toString());
            session.setCreationDate(new Date());
        }
        session.setIp(ip);
        session.setMacAddress(deviceId);
        session.setLastUseDate(new Date());
        sessionRepository.save(session);
        return session;
    }

    public BaseResponse getOrRenewToken(String deviceId, HttpServletRequest request) {
        Session session = null;
        try {
            session = getOrCreateSession(deviceId, request);
        } catch (Exception e) {
            logger.error("Error: {} {}", e.getMessage(), e.getStackTrace());
            return new ErrorResponse(e.getMessage());
        } catch (IPRiskException e) {
            logger.error("Error IPRISK: {}" , e.getMessage());
            return new ErrorResponse("error.proxydetected");
        }

        User user = userRepository.findAllByToken(session.getToken()).stream().findFirst().orElse(null);
        if(user != null){
            UserWithSession userWithSession = new UserWithSession();
            userWithSession.setIp(session.getIp());
            userWithSession.setWagesLeft(session.getWagesLeft());
            userWithSession.setToken(session.getToken());
            userWithSession.setEmail(user.getEmail());
            userWithSession.setId(user.getId());
            return userWithSession;
        }

        /*TokenAuthentitcation authentitcation = new TokenAuthentitcation();
        authentitcation.setIp(session.getIp());
        authentitcation.setWagesLeft(session.getWagesLeft());
        authentitcation.setToken(session.getToken());
        return authentitcation;*/
        return new ErrorResponse("error.noauth");
    }

    public Session getSessionByIpOrMac(String ip, String macAddress) {
        Session session = null;
        List<Session> sessionsByMacAddress = sessionRepository.findByMacAddress(macAddress);
        List<Session> sessionByIp = sessionRepository.findByIp(ip);
        if (sessionByIp.size() > 0) {
            session = sessionByIp.stream().findFirst().orElse(null);
        } else {
            if (sessionsByMacAddress.size() > 0)
                session = sessionsByMacAddress.stream().findFirst().orElse(null);

        }
        if (session != null) {
            session.setLastUseDate(new Date());
            return sessionRepository.save(session);
        }

        return session;
    }

    public BaseResponse getWagesLeft(String token) {
        Session session = sessionRepository.findDistinctByToken(token).orElse(null);
        User user = userRepository.findAllByToken(session.getToken()).stream().findFirst().orElse(null);
        if(user == null || !user.isEnabled()){
            return new ErrorResponse("error.notenabled");
        }
        if (session != null) {
            return new WagesLeftResponse(token, session.getWagesLeft());
        } else {
            return new ErrorResponse("No session found, please authenticate first");
        }
    }

    public Session getSessionByToken(String token) {
        Session session = sessionRepository.findDistinctByToken(token).orElse(null);
        if (session != null) {
            session.setLastUseDate(new Date());
            return sessionRepository.save(session);
        }
        return null;
    }

    public boolean spendAWadge(String token) {
        String environment = (String) env.getProperty("spring.profiles.active");
        //if (environment.equals("dev")) return true;
        Session session = getSessionByToken(token);
        if (session.getWagesLeft() > 0) {
            session.setWagesLeft(session.getWagesLeft() - 1);
            sessionRepository.save(session);
            return true;
        }
        return false;
    }

    public void addAWage(String token) {
        Session session = getSessionByToken(token);
        session.setWagesLeft(session.getWagesLeft() + 1);
        sessionRepository.save(session);

    }

    public void addWages(String token, int wages){
        Session session = getSessionByToken(token);
        session.setWagesLeft(session.getWagesLeft() + wages);
        sessionRepository.save(session);
    }

    public BaseResponse makeMagicWages(String token) {
        Session session = getSessionByToken(token);
        if (session != null) {
            session.setWagesLeft(10000);
            sessionRepository.save(session);
            return new WagesLeftResponse(token, session.getWagesLeft());
        }
        return new ErrorResponse("No session found.");
    }

    public BaseResponse userExist(String email) {
        boolean exist = isInDB(email.toLowerCase());
        return new UserExistsResponse(email, exist);
    }

    public boolean isInDB(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public BaseResponse register(RegisterRequest register, HttpServletRequest request) {
        String ip = null;

        try {
            ip = getIpFroHTTPRequest(request);
            logger.info("Register request from {}", ip);
        } catch (IPRiskException e) {
            logger.error("IP risk {} - IP: {}", e.getMessage(), ip);
            return new ErrorResponse("error.banned");
        }

        long countByRegIp = userRepository.countAllByRegistrationIp(ip);
        if(countByRegIp > 10){
            ipRiskService.banIP(ip);
            return new ErrorResponse("error.exceeded");
        }
        try {
            ipRiskService.evaluateIp(ip);
        } catch (IPRiskException e) {
            return new ErrorResponse("error.banned");
        }

        if(!captchaService.isValidRequest("REGISTER_REQUEST" , register.getEmail(), ip, register.getCaptcha())){
            return new ErrorResponse("error.recaptcha");
        }

        if (!Validators.isValidEmailAddress(register.getEmail().toLowerCase())) {
            return new ErrorResponse("error.wrongemail");
        }
        if (isInDB(register.getEmail())) {
            return new ErrorResponse("error.userexist");
        }
        try {
            Validators.validatePasswords(register.getPassword(), register.getConfirmPassword());
        } catch (Exception e) {
            return new ErrorResponse(e.getMessage());
        }
        String mailGuardEnabled = (String) env.getProperty("mailguard.enabled");
        if(mailGuardEnabled.equalsIgnoreCase("true")){
            try{
                mailGuard.verifyEmail(register.getEmail().toLowerCase());
            }catch (Exception | MailGuardException e){
                logger.info("Mail Guard Risk detected: {} using email: {}" , e.getMessage(), register.getEmail().toLowerCase());
                return new ErrorResponse("error.wrongemail");
            }
        }


        Session session = null;
        try {
            session = getOrCreateSession(register.getDeviceId(), request);
        } catch (Exception e) {
            logger.error("Error: {} {}", e.getMessage(), e.getStackTrace());
            return new ErrorResponse(e.getMessage());
        } catch (IPRiskException e) {
            logger.error("Error IPRISK: {}" , e.getMessage());
            return new ErrorResponse("error.proxydetected");
        }


        User user = new User();
        user.setEmail(register.getEmail().toLowerCase());
        user.setPassword(register.getPassword());
        user.setCreationDate(new Date());
        user.setToken(session.getToken());
        user.setRegistrationIp(ip);
        String mailToken = Utils.getRandomPassword(15);
        user.setEmailToken(mailToken);
        if(register.getBaseLang() == null){
            ip = session.getIp();
            String country = ipUtils.getLangFromIP(ip);
            if(country != null){
                user.setBaseLang(country);
            } else {
                user.setBaseLang("en");
            }

        } else {
            user.setBaseLang(register.getBaseLang());
        }
        eraseNotEnabledUsers(user);

        if(register.getRefId() != null){
            user.setWhoReferredMe(register.getRefId());
        }
        user.setMyRefId(Utils.getRandomPassword(6));
        userRepository.save(user);

        try {
            mailService.sendRegistrationEmail(user, mailToken);
        }catch (Exception e){
            logger.error("Error sending welcome email: {}" , e.getMessage());
            logger.error("UserId: {} Mail: {} MailToken: {} " , user.getId(), user.getEmail(), mailToken);
        }
        return new BaseResponse("ok");
    }

    private void eraseNotEnabledUsers(User user) {
        List<User> notEnabled = userRepository.findAllByToken(user.getToken()).stream().filter(u->!u.isEnabled()).collect(Collectors.toList());
        if(notEnabled.size() > 0){
            notEnabled.forEach(u->userRepository.delete(u));
        }
    }

    public BaseResponse verifyEmail(String token, HttpServletRequest request) {
        User user = userRepository.findByEmailToken(token).orElse(null);
        if(user == null)
            return new ErrorResponse("error.notoken");
        if(user.isEnabled())
            return new ErrorResponse("error.alreadyenabled");
        if(user.isBanned()){
            return new ErrorResponse("error.badcredentials");
        }
        user.setEnabled(true);
        if(user.getWhoReferredMe() != null){
            User whoReferredMe = userRepository.findByMyRefId(user.getWhoReferredMe()).orElse(null);
            if(whoReferredMe != null){
                whoReferredMe.getRefStats().setRegisteredUsers(whoReferredMe.getRefStats().getRegisteredUsers()+1);
                userRepository.save(whoReferredMe);
            }
        }
        userRepository.save(user);
        return new BaseResponse("OK");
    }

    public BaseResponse sendCodeToMail(String email, HttpServletRequest request) {
        User user = userRepository.findByEmail(email.toLowerCase()).orElse(null);
        if(user == null)
            return new ErrorResponse("error.nouser");
        if(!user.isEnabled())
            return new ErrorResponse("error.notenabled");
        if(user.isBanned()){
            return new ErrorResponse("error.badcredentials");
        }
        String passCode = Utils.getRandomPassword(8);
        user.setPasswordRecovercode(passCode);
        userRepository.save(user);
        mailService.sendPasswordRecoverCode(user,passCode);
        return new BaseResponse("OK");
    }

    public BaseResponse resetPass(String code) {
        User user = userRepository.findByPasswordRecovercode(code).orElse(null);

        if(user == null)
            return new ErrorResponse("error.nouser");
        if(user.isBanned()){
            return new ErrorResponse("error.badcredentials");
        }
        String newPass = Utils.getRandomPassword(12);
        user.setPassword(newPass);
        userRepository.save(user);
        mailService.sendPasswordReset(user, newPass);
        return new BaseResponse("OK");
    }

    public BaseResponse loginUser(LoginRequest loginRequest, HttpServletRequest request) {


        User user = userRepository.findByEmailAndPassword(loginRequest.getUsername().toLowerCase(), loginRequest.getPassword()).orElse(null);


        if(user == null)
            return new ErrorResponse("error.badcredentials");


        if(!user.isEnabled())
            return new ErrorResponse("error.usernotenabled");
        String ip = null;
        try {
            ip = getIpFroHTTPRequest(request);
        } catch (IPRiskException e) {
            logger.error("Error IPRISK: {}" , ip);
            return new ErrorResponse("error.proxydetected");
        }
        if(user.isBanned()){
            ipRiskService.banIP(ip);
            return new ErrorResponse("error.banned");
        }

        if(!captchaService.isValidRequest("LOGIN_REQUEST", loginRequest.getUsername(), ip, loginRequest.getCaptcha() )){
            return new ErrorResponse("error.captcha");
        }


            LoginHistory history = new LoginHistory();
        history.setDate(new Date());
        history.setIp(ip);
        try {
            IPRiskResponse localization = ipUtils.getIPLookup(ip);
            if (localization != null) {
                history.setCountryCode(localization.getCountryCode());
                history.setCity(localization.getCityName());
                history.setRegionCode(localization.getRegionName());
                history.setMobileCarrier(localization.getMobileCarrier());
                history.setISP(localization.getISP());
            }
        }catch (Exception e){
            logger.error("Cannot geolocate ip: {}", e.getMessage());
        }
        user.getLoginHistory().add(history);
        user.setLastLoginDate(new Date());

        if(user.getMyRefId() == null){
            String refId = Utils.getRandomPassword(6);
            while(refIdExists(refId)){
                refId = Utils.getRandomPassword(6);
            }
            user.setMyRefId(refId);
        }

        if(user.getRefStats() == null){
            user.setRefStats(new RefStats());
        }
        if(loginRequest.getUuid() != null){
            DeviceIdentifier identifier = new DeviceIdentifier();
            String[] split = loginRequest.getUuid().split("\\|");
            identifier.setDeviceHash(split[0]);
            identifier.setDevicePrint(split[1]);
            user.getDeviceIdentifiersHistory().add(identifier);
        }
        if(loginRequest.getLs() != null){
            LocalStorageHistory lsHistory = new LocalStorageHistory();
            lsHistory.setLsString(loginRequest.getLs());
            lsHistory.setDate(new Date());
            user.getLsHistory().add(lsHistory);
        }


        userRepository.save(user);
        if(sniper.evaluateUser(user)){
            return new ErrorResponse("error.badcredentials");
        }
        Session session = getSessionByToken(user.getToken());
        if(session == null){
            try {
                session = getOrCreateSession(loginRequest.getUuid(), request);
                session.setWagesLeft(0);
                session.setRemarks("Session created for null authentication.");
                sessionRepository.save(session);
                user.setToken(session.getToken());
                userRepository.save(user);
            } catch (Exception | IPRiskException e) {
                logger.error("Cannot create session for user {} - Error: {}", loginRequest.getUsername(), e.getMessage());
                return new ErrorResponse("error.badcredentials");
            }
        }
        UserWithSession response = new UserWithSession();
        response.setToken(user.getToken());
        response.setWagesLeft(session.getWagesLeft());
        response.setEmail(user.getEmail());
        response.setId(session.getId());
        response.setIp(ip);
        response.setUserId(user.getId());
        return response;

    }

    private boolean refIdExists(String refId){
        return userRepository.findByMyRefId(refId).isPresent();
    }

    private User checkForMultipleUsers(String userid, String token){
       List<User> users = userRepository.findAllByToken(token);
       if(users.size() == 0){
           return null;
       }
       if(users.size() == 1){
           return users.get(0);
       }

       List<User> notEnabled = users.stream().filter(u-> !u.isEnabled()).collect(Collectors.toList());
       notEnabled.forEach(u ->{
           userRepository.delete(u);
       });

       return userRepository.findById(userid).orElse(null);
    }

    public BaseResponse getToken(String userid, String token, HttpServletRequest request) {
        String ip = null;
        try {
           ip = getIpFroHTTPRequest(request);
        } catch (IPRiskException e) {
            logger.error("Error IPRISK detected on /token");
            return new ErrorResponse("error.proxydetected");
        }
        User user = this.checkForMultipleUsers(userid, token);



        if(user == null)
            return new ErrorResponse("error.noauth");
        if(!user.isEnabled()){
            return new ErrorResponse("error.usernotenabled");
        }
        if(user.isBanned()){
            if(ip != null)
                ipRiskService.banIP(ip);
            return new ErrorResponse("error.badcredentials");
        }

        try{
            this.checkMultiplePasswords(user);
        }catch (Exception | ExceptionMultipleAccountToBan e){
            return new ErrorResponse("error.badcredentials");
        }

        Session session = getSessionByToken(token);
        UserWithSession userWithSession = new UserWithSession();
        userWithSession.setIp(session.getIp());
        userWithSession.setWagesLeft(session.getWagesLeft());
        userWithSession.setToken(session.getToken());
        userWithSession.setEmail(user.getEmail());
        userWithSession.setId(user.getId());
        return userWithSession;
    }

    private void checkMultiplePasswords(User user) throws ExceptionMultipleAccountToBan {
        String actualPassword = user.getPassword();
        String actualToken = user.getToken();
        List<User> allSamePassword = userRepository.findAllByPassword(actualPassword);

        if(allSamePassword.size() > 0){
            if(actualPassword.equals("password") || actualPassword.equals("123456")){
                logger.info("Skipping, password weak");
                return;
            }
            logger.info("[{}@{}] -> Investigating" , user.getEmail(), user.getId());
            int count = (int) allSamePassword.stream().filter(u -> !u.getToken().equals(actualToken)).count();
            logger.info("[{}@{}] -> Total account with different token but same pass: {}" , user.getEmail(), user.getId(), count);
            if(count > 3){
                logger.info("[{}@{}] -> Has more then 3 account with different token, banning all." , user.getEmail(), user.getId());

                List<User> toban = allSamePassword.stream().peek(u -> u.setBanned(true)).collect(Collectors.toList());
                userRepository.saveAll(toban);
                toban.forEach(u -> {
                    logger.info("Banned account [{}@{}]", u.getId(), u.getEmail());
                });
                throw new ExceptionMultipleAccountToBan();
            }
        }
    }

    private boolean checkSession(String token, User user){
        Session session = getSessionByToken(token);
        if(session == null) return false;
        if(!user.getToken().equals(token)) return false;
        return true;
    }

    public BaseResponse updatePassword(UpdatePasswordRequest updateRequest, HttpServletRequest request) {
        User user = userRepository.findById(updateRequest.getUserId()).orElse(null);


        if(user == null)
            return new ErrorResponse("error.usernotfound");

        if(!checkSession(updateRequest.getToken(), user)) return new ErrorResponse("error.unauthorized");

        if(!user.getPassword().equals(updateRequest.getOldPassword()))
            return new ErrorResponse("error.badcredentials");

        if(!updateRequest.getNewPassword().equals(updateRequest.getConfirmPassword()))
            return new ErrorResponse("error.passwordmismatch");

        user.setPassword(updateRequest.getNewPassword());
        userRepository.save(user);
        return new BaseResponse("OK");
    }

    public User updateUserWithLang(User user) {
        Session s = getSessionByToken(user.getToken());
        String lastIp = s.getIp();
        String country = ipUtils.getLangFromIP(lastIp);
        if(country != null){
            user.setBaseLang(country);

        } else {
            user.setBaseLang("it");
        }


        userRepository.save(user);
        return user;
    }

    public BaseResponse getStats(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null){
            return new ErrorResponse("error.usernotexist");
        }
        if(user.getRefStats() == null){
            return new ErrorResponse("error.nostats");
        }
        UserStatsResposne response = new UserStatsResposne();
        response.setUserId(userId);
        response.setMyReferenceId(user.getMyRefId());
        response.setReferencedBy(user.getWhoReferredMe());
        response.setRegisteredUsers(user.getRefStats().getRegisteredUsers());
        response.setBoughtTokens(user.getRefStats().getOthersTokensBought());
        response.setEarnedTokens(user.getRefStats().getMyGainedTokens());
        return response;
    }

    public void addReferralWageForUser(User myReferrer, int wages) {
        if(myReferrer == null)
            return;
        String token = myReferrer.getToken();
        addWages(token, wages);
        myReferrer.getRefStats().setMyGainedTokens(myReferrer.getRefStats().getMyGainedTokens() + wages);
        myReferrer.getRefStats().setOthersTokensBought(myReferrer.getRefStats().getOthersTokensBought() + (wages * 2));
        userRepository.save(myReferrer);
    }

    public BaseResponse getProbe() {
        ProbeResponse probeResponse = new ProbeResponse();
        probeResponse.setApiVersion("1.0");
        probeResponse.setAppVersion("1.0");
        probeResponse.setMessage("ONLINE");
        return probeResponse;
    }

    public boolean checkMultipleAccountFormToken(String token) {
        logger.info("Checking accounts with token: {}" , token);
        List<User> users = userRepository.findAllByToken(token);
        if(users.size() > 0){
            User first = users.get(0);
            try {
                this.checkMultiplePasswords(first);
            } catch (ExceptionMultipleAccountToBan exceptionMultipleAccountToBan) {
                logger.info("Skipping process, user has been banned...");
                return false;
            }
            return true;
        }
        return true;
    }

    public User getUserByToken(String token) {
        User user = userRepository.findAllByToken(token).stream().findFirst().orElse(null);
        return user;
    }
}
