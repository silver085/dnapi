package com.dn.DNApi.Repositories;

import com.dn.DNApi.Domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailToken(String emailToken);
    Optional<User> findByPasswordRecovercode(String passwordRecoverCode);
    Optional<User> findByEmailAndPassword(String email, String password);
    Optional<User> findByMyRefId(String myRefId);
    List<User> findAllByPassword(String password);
    List<User> findAllByToken(String token);
    List<User> findAllByRegistrationIp(String registrationIp);
    long countAllByRegistrationIp(String registrationIp);
}
