package com.dn.DNApi.Repositories;

import com.dn.DNApi.Domain.CaptchaAttempt;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CaptchaAttemptsRepository extends MongoRepository<CaptchaAttempt, String> {
    long countAllByIp(String ip);
}
