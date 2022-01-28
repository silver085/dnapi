package com.dn.DNApi.Facades.Mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Component
public class MailContentBuilder {
    private TemplateEngine templateEngine;

    @Autowired
    public MailContentBuilder(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String build(String template, Map<String,Object> values) {
        Context context = new Context();
        context.setVariables(values);
        return templateEngine.process(template, context);
    }

    public String buildText(Map<String,Object> values) {
        StringBuilder builder = new StringBuilder();

        builder.append((String) values.get("salutation")).append("\n");
        builder.append((String) values.get("welcometext")).append("\n");
        builder.append((String) values.get("message")).append("\n");
        builder.append((String) values.get("footer")).append("\n");

        return builder.toString();
    }
}
