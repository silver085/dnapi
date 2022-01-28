package com.dn.DNApi.DTO;

public class UserExistsResponse extends BaseResponse {
    private String email;
    private boolean exists;

    public UserExistsResponse(String email, boolean exists) {
        this.email = email;
        this.exists = exists;
    }

    public UserExistsResponse(boolean error, String exception, String email, boolean exists) {
        super(error, exception);
        this.email = email;
        this.exists = exists;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }
}
