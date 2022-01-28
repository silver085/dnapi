package com.dn.DNApi.Configurations;

import com.dn.DNApi.Facades.AuthenticationFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:messages.properties")
public class LangMessages {
    @Autowired
    private Environment env;
    private static final Logger logger = LoggerFactory.getLogger(LangMessages.class);


    private Object getProperty(String key){
        return env.getProperty(key);
    }

    public String getLocalizedMessage(String key, String lang){
        if(lang.equalsIgnoreCase("en") || lang.equalsIgnoreCase("de") || lang.equalsIgnoreCase("it"))
            return (String) getProperty(key + "." + lang);
        else{
            logger.error("Could not determine lang, marking default: en | MessageKey: {}" , key);
            return (String) getProperty(key + ".en");
        }

    }


}
