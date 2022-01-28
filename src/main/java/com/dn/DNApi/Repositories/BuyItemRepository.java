package com.dn.DNApi.Repositories;

import com.dn.DNApi.Domain.BuyItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BuyItemRepository extends MongoRepository<BuyItem, String> {
    long count();
}
