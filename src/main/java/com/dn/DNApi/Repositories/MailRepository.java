package com.dn.DNApi.Repositories;

import com.dn.DNApi.Domain.Mail;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MailRepository extends MongoRepository<Mail, String> {

}
