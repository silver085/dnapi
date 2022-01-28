package com.dn.DNApi.Repositories;

import com.dn.DNApi.Domain.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> {

}
