package com.dn.DNApi.PaypalCore;

import com.dn.DNApi.Configurations.Env;
import com.paypal.http.HttpResponse;
import com.paypal.orders.Order;
import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.orders.OrderRequest;
import com.paypal.orders.OrdersCaptureRequest;
import com.paypal.orders.OrdersGetRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class PaypalClient {
    @Autowired
    Env env;
    private static final Logger logger = LoggerFactory.getLogger(PaypalClient.class);

    String clientId = "YOUR APPLICATION CLIENT ID";
    String clientSecret = "YOUR APPLICATION CLIENT SECRET";
    private PayPalEnvironment environment;
    @PostConstruct
    public void setupClient(){
        this.clientId = (String) env.getProperty("paypal.clientid");
        this.clientSecret = (String) env.getProperty("paypal.secret");
        String mode = (String) env.getProperty("paypal.mode");
        if(mode.equalsIgnoreCase("sandbox")){
            environment = new PayPalEnvironment.Sandbox(
                    clientId,
                    clientSecret);
        } else{
            environment = new PayPalEnvironment.Live(
                    clientId,
                    clientSecret);
        }
    }


    public Order getOrder(String paypalOrderId) throws IOException {
        OrdersGetRequest request = new OrdersGetRequest(paypalOrderId);
        HttpResponse<Order> response = client().execute(request);
        return response.result();
    }

    private PayPalHttpClient client() {
        PayPalHttpClient client = new PayPalHttpClient(environment);
        logger.info("Paypal auth: {}" , environment.authorizationString());
        return client;
    }
}
