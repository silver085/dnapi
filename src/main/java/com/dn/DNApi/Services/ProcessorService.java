package com.dn.DNApi.Services;

import com.dn.DNApi.DTO.*;
import com.dn.DNApi.Facades.NotificationFacade;
import com.dn.DNApi.Facades.ProcessorFacade;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Service
public class ProcessorService {
    @Autowired
    ProcessorFacade processorFacade;
    @Autowired
    NotificationFacade notificationFacade;
    @Autowired
    ObjectMapper mapper;

    public ProbeResponse getProbe() {
        return processorFacade.getProbe();
    }

    public BaseResponse processImage(String token, ImageProcessingRequest image) {
        return processorFacade.process(token, image);
    }

    public BaseResponse getQueue(String token, String queueId) {
        return processorFacade.getQueueStatus(token, queueId);
    }

    public BaseResponse startQueue() {
        return processorFacade.startQueue();
    }

    public BaseResponse flushQueue() {
        return processorFacade.flushQueue();
    }

    public BaseResponse getFileData(String token, String queueId) {
        return processorFacade.getFileData(token, queueId);
    }

    public BaseResponse getNotifications(String userId, int startIndex, int offset) {
        return notificationFacade.getNotifications(userId, startIndex, offset);
    }

    public BaseResponse countUnreadNotifications(String userId) {
        return notificationFacade.countUnreadNotifications(userId);
    }

    public BaseResponse markAsRead(List<String> notificationIds) {
        return notificationFacade.markAsRead(notificationIds);
    }

    public String processImageNew(String token, SubmitImageProcessingRequest request) {
        BaseResponse result =  processorFacade.processNew(token, request);
        if(result instanceof ImageProcessingResult){
            try {
                String objJackson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
                return objJackson;
            } catch (JsonProcessingException e) {
                return "{error: 'true' , exception: 'error converting pojo'}";
            }
        } else if(result instanceof ErrorResponse){
            ErrorResponse errorResponse = (ErrorResponse) result;
            String objJackson = null;
            try {
                objJackson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorResponse);
            } catch (JsonProcessingException e) {
                return "{error: 'true' , exception: 'error converting pojo'}";
            }
            return objJackson;
        }

        return "";
    }
}
