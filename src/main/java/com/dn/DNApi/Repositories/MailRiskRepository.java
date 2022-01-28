package com.dn.DNApi.Repositories;

import com.dn.DNApi.Domain.MailRisk;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MailRiskRepository extends MongoRepository<MailRisk, String> {
    List<MailRisk> findByDomainName(String domainName);
    long countByDomainName(String domainName);
}
