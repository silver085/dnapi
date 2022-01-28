package com.dn.DNApi.Services;

import com.dn.DNApi.DTO.*;
import com.dn.DNApi.Facades.AuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class AuthenticationService {
    @Autowired
    AuthenticationFacade authenticationFacade;

    public BaseResponse getOrRenewToken(String deviceId, HttpServletRequest request) {
        return authenticationFacade.getOrRenewToken(deviceId, request);
    }

    public BaseResponse getWagesLeft(String token) {
        return authenticationFacade.getWagesLeft(token);
    }

    public BaseResponse makeMagicWages(String token) {
        return authenticationFacade.makeMagicWages(token);
    }

    public BaseResponse userExist(String email) {
        return authenticationFacade.userExist(email);
    }

    public BaseResponse register(RegisterRequest register, HttpServletRequest request) {
        return authenticationFacade.register(register, request);
    }

    public BaseResponse emailVerify(String token, HttpServletRequest request) {
        return authenticationFacade.verifyEmail(token, request);
    }

    public BaseResponse codeToMail(String email, HttpServletRequest request) {
        return authenticationFacade.sendCodeToMail(email, request);
    }

    public BaseResponse resetPass(String code, HttpServletRequest request) {
        return authenticationFacade.resetPass(code);
    }

    public BaseResponse login(LoginRequest loginRequest, HttpServletRequest request) {
        return authenticationFacade.loginUser(loginRequest, request);
    }

    public BaseResponse getToken(String userid, String token, HttpServletRequest request) {
        return authenticationFacade.getToken(userid, token, request);
    }

    public BaseResponse updatePasssword(UpdatePasswordRequest updateRequest, HttpServletRequest request) {
        return authenticationFacade.updatePassword(updateRequest, request);
    }

    public BaseResponse getStats(String userId) {
        return authenticationFacade.getStats(userId);
    }

    public BaseResponse getProbe() {
        return authenticationFacade.getProbe();
    }
}
