package com.dn.DNApi.Facades;

import com.dn.DNApi.DTO.BaseResponse;
import com.dn.DNApi.DTO.ErrorResponse;
import com.dn.DNApi.DTO.NotificationPageDTO;
import com.dn.DNApi.DTO.NotificationsCountResponse;
import com.dn.DNApi.Domain.ImageQueue;
import com.dn.DNApi.Domain.Notification;
import com.dn.DNApi.Domain.User;
import com.dn.DNApi.Facades.Utils.ImageUtils;
import com.dn.DNApi.Repositories.NotificationRepository;
import com.dn.DNApi.Repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class NotificationFacade {
    private static final Logger logger = LoggerFactory.getLogger(NotificationFacade.class);

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    MongoOperations mongoOperations;

    @Autowired
    UserRepository userRepository;

    public BaseResponse getNotifications(String userid, int startIndex, int offset) {
        Pageable pageable = PageRequest.of(startIndex, offset, Sort.by(
                Sort.Order.desc("date")));
        Query query = new Query().addCriteria(
                Criteria.where("userId").is(userid)
        ).with(pageable);


        List<Notification> pageList = mongoOperations.find(query, Notification.class);

        Page<Notification> page = PageableExecutionUtils.getPage(pageList, pageable,
                () -> mongoOperations.count(Query.of(query).limit(-1).skip(-1), Notification.class));
        return new NotificationPageDTO(page);
    }

    public void saveProcessNotification(String token, ImageQueue queue) {
        User user = userRepository.findAllByToken(token).stream().findFirst().orElse(null);
        if(user==null)
            return;
        Notification n = new Notification();
        n.setDate(new Date());
        n.setQueueId(queue.getId());
        n.setStatus("Processing");
        n.setUserId(user.getId());
        n.setB64Icon(getB64IconFromQueue(queue.getFileData()));
        notificationRepository.save(n);
    }

    public void saveCompletedProcessNotification(ImageQueue currentImage) {
        User user = userRepository.findAllByToken(currentImage.getToken()).stream().findFirst().orElse(null);
        if(user==null)
            return;
        Notification n = new Notification();
        n.setDate(new Date());
        n.setQueueId(currentImage.getId());
        if(currentImage.isCompleted()){
            n.setStatus("Completed");
        }
        if(currentImage.isOnError()){
            n.setStatus("Error");
        }
        n.setUserId(user.getId());
        n.setB64Icon(getB64IconFromQueue(currentImage.getFileData()));
        notificationRepository.save(n);

    }

    private  String getB64IconFromQueue(String queueImage){
        String scaledImage = null;
        try {
            scaledImage = ImageUtils.getB64ResizedImgForNotifications(queueImage);
        } catch (IOException e) {
            logger.error("Could not create snapshot of image for notification: {}" , e.getMessage());
        }
        if(scaledImage!= null){
            scaledImage = "data:image/jpeg;base64," + scaledImage;
        }
        return scaledImage;
    }

    public BaseResponse countUnreadNotifications(String userId) {
        int unread = notificationRepository.countByUserIdAndReadFalse(userId);
        return new NotificationsCountResponse(userId, unread);
    }

    public BaseResponse markAsRead(List<String> ids) {
        if(ids.size() <= 0){
            return new ErrorResponse("error.empty");
        }

        Query query = new Query().addCriteria(Criteria.where("id").in(ids));
        List<Notification> unread = mongoOperations.find(query, Notification.class);
        List<Notification> marked = unread.stream().map(n -> {
            n.setRead(true);
            return n;
        }).collect(Collectors.toList());
        marked.forEach(m -> notificationRepository.save(m));
        return new BaseResponse("OK");
    }
}
