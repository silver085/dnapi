package com.dn.DNApi.Domain;

public class RefStats {
    private int othersTokensBought;
    private int myGainedTokens;
    private int registeredUsers;

    public int getOthersTokensBought() {
        return othersTokensBought;
    }

    public void setOthersTokensBought(int othersTokensBought) {
        this.othersTokensBought = othersTokensBought;
    }

    public int getMyGainedTokens() {
        return myGainedTokens;
    }

    public void setMyGainedTokens(int myGainedTokens) {
        this.myGainedTokens = myGainedTokens;
    }

    public int getRegisteredUsers() {
        return registeredUsers;
    }

    public void setRegisteredUsers(int registeredUsers) {
        this.registeredUsers = registeredUsers;
    }
}
