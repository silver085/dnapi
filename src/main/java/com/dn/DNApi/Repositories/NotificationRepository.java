package com.dn.DNApi.Repositories;

import com.dn.DNApi.Domain.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByUserIdOrderByDateDesc(String userId);

    List<Notification> findByUserIdAndReadTrue(String userId);

    List<Notification> findByUserIdAndReadFalse(String userId);

    int countByUserIdAndReadFalse(String userId);

    List<Notification> findByUserId(String userId);

    List<Notification> findByDateBefore(Date date);
}
