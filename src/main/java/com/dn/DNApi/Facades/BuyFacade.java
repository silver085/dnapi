package com.dn.DNApi.Facades;

import com.dn.DNApi.DTO.*;
import com.dn.DNApi.Domain.*;
import com.dn.DNApi.PaypalCore.PaypalClient;
import com.dn.DNApi.Repositories.*;
import com.dn.DNApi.Services.Mail.MailService;
import com.paypal.payments.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class BuyFacade {
    private static final Logger logger = LoggerFactory.getLogger(BuyFacade.class);
    @Autowired
    BuyItemRepository buyItemRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PaypalClient client;
    @Autowired
    AuthenticationFacade authenticationFacade;
    @Autowired
    SessionRepository sessionRepository;
    @Autowired
    MailService mailService;

    @Autowired
    TransactionRepository transactionRepository;
    public BuyListResponse getBuyList() {
        List<BuyItem> buyItemList = buyItemRepository.findAll();
        return new BuyListResponse(buyItemList);
    }

    private void evadeOrder(Transaction transaction){
        Order order = orderRepository.findById(transaction.getOrderId()).orElse(null);
        if(order == null){
            logger.error("Cannot find order of transaction: {}" , transaction.getId());
            return;
        }
        order.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);
        String itemId = order.getItemId();
        BuyItem item = buyItemRepository.findById(itemId).orElse(null);
        if(item == null){
            logger.error("Cannot find item for orderId: {}", order.getId());
            return;
        }
        String userId = order.getUserId();
        User user = userRepository.findById(userId).orElse(null);
        if(user == null){
            logger.error("Cannot find user for orderId: {}" , order.getId());
            return;
        }
        Session session = authenticationFacade.getSessionByToken(user.getToken());
        session.setWagesLeft(session.getWagesLeft() + item.getTokens());
        sessionRepository.save(session);
        mailService.sendPurchaseCompletedEmail(user,order, transaction, item);
        logger.info("Purchase completed! TRID: {} ORDID: {}" , transaction.getId(), order.getId());
        logger.info("Added {} tokens to user {} (email: {})" , item.getTokens(), user.getId(), user.getEmail());

        if(user.getWhoReferredMe() != null){
            User myReferrer = userRepository.findByMyRefId(user.getWhoReferredMe()).orElse(null);
            int wages = item.getTokens() / 2;
            authenticationFacade.addReferralWageForUser(myReferrer , wages);
        }

    }

    public BaseResponse createOrder(OrderRequest createOrderReqeust) {
        Order order = new Order();
        order.setUserId(createOrderReqeust.getUserId());
        order.setItemId(createOrderReqeust.getItemId());
        BuyItem item = buyItemRepository.findById(createOrderReqeust.getItemId()).orElse(null);
        if(item == null)
            return new ErrorResponse("error.noitem");
        order.setPrice(item.getPrice());
        order.setStatus(OrderStatus.CREATED);
        orderRepository.save(order);
        return new OrderResponse(order);
    }

    public BaseResponse saveTransaction(TransactionRequest transactionRequest) {
        User user = userRepository.findById(transactionRequest.getUserId()).orElse(null);
        if(user == null)
            return new ErrorResponse("error.nouser");
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setClientStatus(transactionRequest.getClientStatus());
        transaction.setPaypalOrderId(transactionRequest.getPaypalOrderId());
        transaction.setUserId(user.getId());
        transaction.setOrderId(transactionRequest.getOrderId());
        transactionRepository.save(transaction);
            if(transaction.getClientStatus().equalsIgnoreCase("completed")){
                try {
                    com.paypal.orders.Order paypalOrder = client.getOrder(transaction.getPaypalOrderId());
                    if(paypalOrder.status().equalsIgnoreCase("completed")){
                        evadeOrder(transaction);
                    } else {
                        return new ErrorResponse("error.paymentnotcompleted");
                    }
                } catch (IOException e) {
                   logger.error("Cannot doublecheck transaction? {} " , e.getMessage());
                   return new ErrorResponse("error.doublecheck");
                }

            } else {
                return new ErrorResponse("error." + transaction.getClientStatus());
            }
        return new BaseResponse("saved");
    }
}
