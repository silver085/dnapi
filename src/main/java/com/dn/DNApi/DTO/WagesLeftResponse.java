package com.dn.DNApi.DTO;

public class WagesLeftResponse extends BaseResponse {
    private String token;
    private int wagesLeft;

    public WagesLeftResponse(String token, int wagesLeft) {
        this.token = token;
        this.wagesLeft = wagesLeft;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getWagesLeft() {
        return wagesLeft;
    }

    public void setWagesLeft(int wagesLeft) {
        this.wagesLeft = wagesLeft;
    }
}
