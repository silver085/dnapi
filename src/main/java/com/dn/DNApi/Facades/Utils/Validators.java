package com.dn.DNApi.Facades.Utils;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class Validators {
    public static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    public static void validatePasswords(String password, String confirmPassword) throws Exception {
        if(password.length() < 6)
           throw new Exception("error.passwordlength");
        if(!password.equals(confirmPassword))
            throw new Exception("error.passwordmismatch");
    }
}
