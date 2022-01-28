package com.dn.DNApi.DTO;

import com.dn.DNApi.Domain.Order;

public class OrderResponse extends BaseResponse {
    private Order order;
    public OrderResponse(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
