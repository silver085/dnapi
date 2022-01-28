package com.dn.DNApi.DTO;

public class TokenAuthentitcation extends BaseResponse{
    private String token;
    private String ip;
    private int wagesLeft;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getWagesLeft() {
        return wagesLeft;
    }

    public void setWagesLeft(int wagesLeft) {
        this.wagesLeft = wagesLeft;
    }
}
