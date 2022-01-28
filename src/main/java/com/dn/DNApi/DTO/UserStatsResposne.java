package com.dn.DNApi.DTO;

public class UserStatsResposne extends BaseResponse {
    private String userId;
    private String myReferenceId;
    private String referencedBy;
    private int registeredUsers;
    private int boughtTokens;
    private int earnedTokens;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMyReferenceId() {
        return myReferenceId;
    }

    public void setMyReferenceId(String myReferenceId) {
        this.myReferenceId = myReferenceId;
    }

    public String getReferencedBy() {
        return referencedBy;
    }

    public void setReferencedBy(String referencedBy) {
        this.referencedBy = referencedBy;
    }

    public int getRegisteredUsers() {
        return registeredUsers;
    }

    public void setRegisteredUsers(int registeredUsers) {
        this.registeredUsers = registeredUsers;
    }

    public int getBoughtTokens() {
        return boughtTokens;
    }

    public void setBoughtTokens(int boughtTokens) {
        this.boughtTokens = boughtTokens;
    }

    public int getEarnedTokens() {
        return earnedTokens;
    }

    public void setEarnedTokens(int earnedTokens) {
        this.earnedTokens = earnedTokens;
    }
}
