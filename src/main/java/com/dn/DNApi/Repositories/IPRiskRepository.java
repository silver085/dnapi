package com.dn.DNApi.Repositories;

import com.dn.DNApi.Domain.IPRiskList;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface IPRiskRepository extends MongoRepository<IPRiskList, String> {
    List<IPRiskList> getByIpAddress(String ipAddress);
    Long countByIpAddress(String ipAddress);
}
