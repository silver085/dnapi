package com.dn.DNApi.Domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document("Users")
public class User {
    @Id
    private String id;
    private String email;
    private String password;
    private Date creationDate;
    private String token;
    private Date lastLoginDate;
    private String emailToken;
    private boolean enabled = false;
    private boolean banned = false;
    private String baseLang;
    private String passwordRecovercode;
    private String myRefId;
    private String whoReferredMe;
    private RefStats refStats = new RefStats();
    private Boolean redFlag = false;
    private List<DeviceIdentifier> deviceIdentifiersHistory = new ArrayList<>();
    private List<LoginHistory> loginHistory = new ArrayList<>();
    private List<Transaction> transactions = new ArrayList<>();
    private List<LocalStorageHistory> lsHistory = new ArrayList<>();
    private String registrationIp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public String getEmailToken() {
        return emailToken;
    }

    public void setEmailToken(String emailToken) {
        this.emailToken = emailToken;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getBaseLang() {
        return baseLang;
    }

    public void setBaseLang(String baseLang) {
        this.baseLang = baseLang;
    }

    public String getPasswordRecovercode() {
        return passwordRecovercode;
    }

    public void setPasswordRecovercode(String passwordRecovercode) {
        this.passwordRecovercode = passwordRecovercode;
    }

    public List<LoginHistory> getLoginHistory() {
        return loginHistory;
    }

    public void setLoginHistory(List<LoginHistory> loginHistory) {
        this.loginHistory = loginHistory;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public String getMyRefId() {
        return myRefId;
    }

    public void setMyRefId(String myRefId) {
        this.myRefId = myRefId;
    }

    public String getWhoReferredMe() {
        return whoReferredMe;
    }

    public void setWhoReferredMe(String whoReferredMe) {
        this.whoReferredMe = whoReferredMe;
    }

    public RefStats getRefStats() {
        return refStats;
    }

    public void setRefStats(RefStats refStats) {
        this.refStats = refStats;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public List<DeviceIdentifier> getDeviceIdentifiersHistory() {
        return deviceIdentifiersHistory;
    }

    public void setDeviceIdentifiersHistory(List<DeviceIdentifier> deviceIdentifiersHistory) {
        this.deviceIdentifiersHistory = deviceIdentifiersHistory;
    }

    public Boolean getRedFlag() {
        return redFlag;
    }

    public void setRedFlag(Boolean redFlag) {
        this.redFlag = redFlag;
    }

    public List<LocalStorageHistory> getLsHistory() {
        return lsHistory;
    }

    public void setLsHistory(List<LocalStorageHistory> lsHistory) {
        this.lsHistory = lsHistory;
    }

    public String getRegistrationIp() {
        return registrationIp;
    }

    public void setRegistrationIp(String registrationIp) {
        this.registrationIp = registrationIp;
    }
}
