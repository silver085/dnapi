package com.dn.DNApi.Repositories;

import com.dn.DNApi.Domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SniperUserRepository extends MongoRepository<User, String> {

}
