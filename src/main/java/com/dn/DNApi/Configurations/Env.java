package com.dn.DNApi.Configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:application.properties")
public class Env {

    @Autowired
    private Environment env;


    public Object getProperty(String key){
        return env.getProperty(key);
    }



}