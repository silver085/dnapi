package com.dn.DNApi.Repositories;

import com.dn.DNApi.Domain.Session;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;


import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface SessionRepository extends MongoRepository<Session, String> {
    List<Session> findByMacAddress(String macAddress);
    List<Session> findByIp(String ip);
    Optional<Session> findDistinctByToken(String token);
    List<Session> findAllByLastUseDateAfter(Date useDate);
}
