package com.dn.DNApi.DTO;

import com.dn.DNApi.Domain.BuyItem;

import java.util.List;

public class BuyListResponse extends BaseResponse {
    List<BuyItem> buyItems;

    public BuyListResponse(List<BuyItem> buyItemList) {
        this.buyItems = buyItemList;
    }

    public List<BuyItem> getBuyItems() {
        return buyItems;
    }

    public void setBuyItems(List<BuyItem> buyItems) {
        this.buyItems = buyItems;
    }
}
