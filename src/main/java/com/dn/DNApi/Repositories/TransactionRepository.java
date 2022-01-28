package com.dn.DNApi.Repositories;

import com.dn.DNApi.Domain.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
}
