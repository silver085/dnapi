package com.dn.DNApi.Controllers;


import com.dn.DNApi.DTO.BaseResponse;
import com.dn.DNApi.DTO.LoginRequest;
import com.dn.DNApi.DTO.RegisterRequest;
import com.dn.DNApi.DTO.UpdatePasswordRequest;
import com.dn.DNApi.Services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/processor")
public class Authentication {

    @Autowired
    AuthenticationService authenticationService;

    @CrossOrigin(origins = {"http://localhost:3000"})
    @GetMapping("/probetest")
    private BaseResponse getToken(){
        return authenticationService.getProbe();
    }


    @CrossOrigin(origins = {"http://localhost:3000"})
    @GetMapping("/token")
    private BaseResponse getToken(@RequestParam String userid, @RequestParam String token, HttpServletRequest request){

        return authenticationService.getToken(userid, token , request);
    }


    @CrossOrigin(origins = {"http://localhost:3000"})
    @GetMapping("/wagesleft")
    private BaseResponse getWagesLeft(@RequestParam String token){
        return authenticationService.getWagesLeft(token);
    }

    @CrossOrigin(origins = {"http://localhost:3000"})
    @GetMapping("/magic")
    private BaseResponse magicWages(@RequestParam String token){
        return authenticationService.makeMagicWages(token);
    }

    @CrossOrigin(origins = {"http://localhost:3000"})
    @GetMapping("/userexist")
    private BaseResponse userExist(@RequestParam String email) { return authenticationService.userExist(email); }

    @CrossOrigin(origins = {"http://localhost:3000"})
    @PostMapping("/register")
    private BaseResponse register(@RequestBody RegisterRequest register, HttpServletRequest request){
        return authenticationService.register(register, request);
    }
    @CrossOrigin(origins = {"http://localhost:3000"})
    @GetMapping("/emailverify")
    private BaseResponse emailVerify(@RequestParam String token, HttpServletRequest request){
        return authenticationService.emailVerify(token, request);
    }
    @CrossOrigin(origins = {"http://localhost:3000"})
    @GetMapping("/mailcode")
    private BaseResponse codeToMail(@RequestParam String email, HttpServletRequest request){
        return authenticationService.codeToMail(email, request);
    }

    @CrossOrigin(origins = {"http://localhost:3000"})
    @GetMapping("/verifyreset")
    private BaseResponse resetPassVerify(@RequestParam String code, HttpServletRequest request){
        return authenticationService.resetPass(code, request);
    }

    @CrossOrigin(origins = {"http://localhost:3000"})
    @PostMapping("/login")
    private BaseResponse resetPassVerify(@RequestBody LoginRequest loginRequest, HttpServletRequest request){
        return authenticationService.login(loginRequest, request);
    }

    @CrossOrigin(origins = {"http://localhost:3000"})
    @PostMapping("/updatepassword")
    private BaseResponse updatePassword(@RequestBody UpdatePasswordRequest updateRequest, HttpServletRequest request){
        return authenticationService.updatePasssword(updateRequest, request);
    }

    @CrossOrigin(origins = {"http://localhost:3000"})
    @GetMapping("/stats")
    private BaseResponse getStats(@RequestParam String userId){
        return authenticationService.getStats(userId);
    }





}
