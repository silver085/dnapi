package com.dn.DNApi.Facades.Utils;

import org.apache.commons.lang.RandomStringUtils;

import java.util.Random;
import java.util.UUID;

public class Utils {

    public static String getRandomPassword(int length){
        boolean useLetters = true;
        boolean useNumbers = false;
        return RandomStringUtils.random(length, useLetters, useNumbers);
    }

    public static String getRandomUUID(){
        return  UUID.randomUUID().toString();
    }

    public static int getRandomNumberBetween(int min, int max){
        return new Random().nextInt(max - min + 1) + min;
    }
}
