package com.dn.DNApi.Controllers;


import com.dn.DNApi.DTO.BaseResponse;
import com.dn.DNApi.DTO.OrderRequest;
import com.dn.DNApi.DTO.PayPal.PaypalEventRequest;
import com.dn.DNApi.DTO.TransactionRequest;
import com.dn.DNApi.Facades.NotificationFacade;
import com.dn.DNApi.Services.BuyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/processor")
public class BuyController {
    private static final Logger logger = LoggerFactory.getLogger(BuyController.class);


    @Autowired
    BuyService buyService;

    @CrossOrigin(origins = {"http://localhost:3000"})
    @GetMapping("/buylist")
    private BaseResponse getBuyList(){
        return buyService.getBuyList();
    }

    @CrossOrigin(origins = {"http://localhost:3000"})
    @PostMapping("/createorder")
    private BaseResponse createOrder(@RequestBody OrderRequest createOrderReqeust){
        return buyService.createOrder(createOrderReqeust);
    }

    @CrossOrigin(origins = {"http://localhost:3000"})
    @PostMapping("/savetransaction")
    private BaseResponse saveTransaction(@RequestBody TransactionRequest transactionRequest){
        return buyService.saveTransaction(transactionRequest);
    }


    @CrossOrigin("*")
    @PostMapping("/paypalorder")
    public String paypalOrder(@RequestBody PaypalEventRequest request){
        logger.info("Received event from paypal: {}" , request);
        return "OK";
    }
}
