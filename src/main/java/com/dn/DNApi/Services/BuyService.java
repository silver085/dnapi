package com.dn.DNApi.Services;

import com.dn.DNApi.DTO.BaseResponse;
import com.dn.DNApi.DTO.OrderRequest;
import com.dn.DNApi.DTO.TransactionRequest;
import com.dn.DNApi.Facades.BuyFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BuyService {
    @Autowired
    BuyFacade buyFacade;

    public BaseResponse getBuyList() {
        return buyFacade.getBuyList();
    }

    public BaseResponse createOrder(OrderRequest createOrderReqeust) {
        return buyFacade.createOrder(createOrderReqeust);
    }

    public BaseResponse saveTransaction(TransactionRequest transactionRequest) {
        return buyFacade.saveTransaction(transactionRequest);
    }
}
