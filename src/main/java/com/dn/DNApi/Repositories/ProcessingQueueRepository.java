package com.dn.DNApi.Repositories;

import com.dn.DNApi.Domain.ImageQueue;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface ProcessingQueueRepository extends MongoRepository<ImageQueue, String> {
    int countAllByTokenAndCompleted(String token, boolean completed);
    List<ImageQueue> findAllByCompleted(boolean completed);
    List<ImageQueue> findAllByTokenAndCompletedTrue(String token);
    List<ImageQueue> findAllByTokenAndOnErrorTrueAndCompletedFalse(String token);
    List<ImageQueue> findAllBySubmitOnBefore(Date date);
}
