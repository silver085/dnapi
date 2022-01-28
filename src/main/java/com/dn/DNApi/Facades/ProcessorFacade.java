package com.dn.DNApi.Facades;

import com.dn.DNApi.Configurations.Env;
import com.dn.DNApi.DTO.*;
import com.dn.DNApi.Domain.ImageQueue;
import com.dn.DNApi.Domain.Session;
import com.dn.DNApi.Domain.User;
import com.dn.DNApi.Facades.Jobs.QueueProcessor.QueueDetails;
import com.dn.DNApi.Facades.Jobs.QueueProcessor.QueueMultithreadProcessor;
import com.dn.DNApi.Facades.Utils.ImageUtils;
import com.dn.DNApi.Facades.Utils.Utils;
import com.dn.DNApi.Repositories.ProcessingQueueRepository;
import com.dn.DNApi.Services.Sniper.MultiAccountSniper;
import com.dn.DNApi.Services.Storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class ProcessorFacade {
    @Autowired
    Env env;
    @Autowired
    AuthenticationFacade authenticationFacade;
    @Autowired
    ProcessingQueueRepository processingQueueRepository;
    @Autowired
    QueueMultithreadProcessor queueProcessor;
    @Autowired
    NotificationFacade notificationFacade;
    @Autowired
    StorageService storageService;
    @Autowired
    MultiAccountSniper sniper;
    private static final Logger logger = LoggerFactory.getLogger(ProcessorFacade.class);

    public ProbeResponse getProbe() {
        ProbeResponse response = new ProbeResponse();
        response.setApiVersion((String) env.getProperty("application.app.version"));
        response.setAppVersion((String) env.getProperty("application.api.version"));
        response.setMessage((String) env.getProperty("application.api.welcomeprobe"));
        return response;
    }

    public BaseResponse process(String token, ImageProcessingRequest image) {
        if(!authenticationFacade.checkMultipleAccountFormToken(token)){
            return new ErrorResponse("error.process");
        }
        User u = authenticationFacade.getUserByToken(token);
        if(u!=null){
            if(sniper.evaluateUser(u)){
                return new ErrorResponse("error.process");
            }
        }
        if(u!=null && u.isBanned()){
            return new ErrorResponse("error.process");
        }
        if(image.getFileName() == null || image.getImageData() == null)
            return new ErrorResponse("Please set a valid image");
        if(isAllowedWage(token)){
            List<ImageQueue> oldQueue = processingQueueRepository.findAllByTokenAndOnErrorTrueAndCompletedFalse(token);
            oldQueue.forEach(q -> {
                if(q.isOnError()){
                    q.setCompleted(true);
                    q.setCompletedOn(new Date());
                    processingQueueRepository.save(q);
                }
            });
            int activeProcessingNumber = processingQueueRepository.countAllByTokenAndCompleted(token, false);
            String strMaxConcurrency = (String) env.getProperty("application.processing.maxconcurrentperuser");
            int maxConcurrency = 1;
            try{
                maxConcurrency = Integer.parseInt(strMaxConcurrency);
            }catch (NumberFormatException e){
                logger.error("Could not parse application.processing.maxconcurrentperuser");
            }

            if(activeProcessingNumber < maxConcurrency){
                if(!authenticationFacade.spendAWadge(token)){
                    return new ErrorResponse("You are out of wadges for today, come back tomorrow");
                }
                ImageQueue queue = new ImageQueue();
                queue.setFileName(image.getFileName());
                queue.setFileData(image.getImageData());
                queue.setToken(token);
                queue.setBsize(image.getBsize());
                queue.setAsize(image.getAsize());
                queue.setNsize(image.getNsize());
                queue.setVsize(image.getVsize());
                queue.setHsize(image.getHsize());
                processingQueueRepository.save(queue);
                ImageProcessingResult result = new ImageProcessingResult();
                result.setFileName(image.getFileName());
                result.setId(queue.getId());
                result.setStatus("processing");
                int queuePosition = queueProcessor.getQueuePosition(queue.getId());
                result.setQueuePosition(queuePosition);
                result.setWaitingTime(queueProcessor.getWaitingTime(queue.getId()));
                try {
                    notificationFacade.saveProcessNotification(token, queue);
                }catch(Exception e){
                    logger.error("Cannot save notification, queueId: {}", queue.getId());
                    queue.setOnError(true);
                    queue.setCompleted(true);
                    queue.setCompletedOn(new Date());
                    processingQueueRepository.save(queue);
                    authenticationFacade.addAWage(token);
                    return new ErrorResponse("error.unexpected");
                }
                return result;
            } else {
                return  new ErrorResponse("Already one processing in queue, please wait this to complete");
            }
        }
        return new ErrorResponse("Not allowed or not enough wages left");
    }

    private boolean isAllowedWage(String token){
        Session session = authenticationFacade.getSessionByToken(token);
        if(session == null)
            return false;
        if(session.getWagesLeft() > 0)
            return true;
        return false;
    }

    private boolean isAllowedOnQueue(String token, String queueId){
        Session session = authenticationFacade.getSessionByToken(token);
        if(session == null) return false;
        ImageQueue queue = processingQueueRepository.findById(queueId).orElse(null);
        if(queue == null) return false;
        if(!queue.getToken().equals(token)) return false;
        return true;
    }

    public BaseResponse getQueueStatus(String token, String queueId) {
        if(isAllowedOnQueue(token, queueId)){
            ImageQueue queue = processingQueueRepository.findById(queueId).orElse(null);
            if(queue == null) return new ErrorResponse("Cannot find a valid queue");
            ImageProcessingResult result = new ImageProcessingResult();
            result.setId(queueId);
            result.setFileName(queue.getFileName());
            if(queue.isCompleted() && !queue.isOnError())
            {
                result.setStatus("completed");
                result.setFileData(queue.getProcessedData());
                String fn = queue.getProcessedFilename().replace((String) env.getProperty("application.dnlib.storagefolder"), "");
                result.setProcessedFileName(fn.split("/")[1]+".png");

            } else {
                result.setStatus("processing");
                QueueDetails details = queueProcessor.getQueueDetails(queueId);
                result.setQueuePosition(details.getPosition());
                result.setWaitingTime(details.getTimeLeft());
                result.setTotalQueue(details.getTotalQueue());
                result.setWaitingMsg(Utils.getRandomNumberBetween(0, 6));
            }
            if(queue.isOnError()){
                result.setStatus("error");
            }
            return result;
        }
        return new ErrorResponse("Not allowed on this queue");
    }

    public BaseResponse startQueue() {
        //queueProcessor.processQueue();
        BaseResponse response = new BaseResponse();
        response.setMessage("Queue started");
        return response;
    }

    public BaseResponse flushQueue() {
        queueProcessor.flushQueue();
        BaseResponse response = new BaseResponse();
        response.setMessage("Queue flushed!");
        return response;
    }

    public BaseResponse getFileData(String token, String queueId) {
        if(isAllowedOnQueue(token, queueId)) {
            ImageQueue queue = processingQueueRepository.findById(queueId).orElse(null);
            if (queue == null) return new ErrorResponse("Cannot find a valid queue");
            if(!queue.isCompleted()) return new ErrorResponse("Queue still in progress");
            if(queue.isOnError()) return new ErrorResponse("Queue is on error :(");
            Path filePath = Paths.get(queue.getProcessedFilename());
            try {
                byte[] bytes = Files.readAllBytes(filePath);
                String b64 = Base64.getEncoder().encodeToString(bytes);
                FileDataResponse response = new FileDataResponse();
                String fn = queue.getProcessedFilename().replace((String) env.getProperty("application.dnlib.storagefolder"), "");
                response.setFileName(fn.split("/")[1] + ".png");
                response.setFileData(b64);
                return response;
            } catch (IOException e) {
                return new ErrorResponse("Error reading file");
            }
        }
        return new ErrorResponse("Not allowed on this queue");
    }
    private boolean isValidImageFile(String filename){
        try {
            Image image = ImageIO.read(new File(filename));
            if(image == null){
                logger.error("File {} is not a valid imagefile", filename);
                return false;
            }
            logger.info("File {} is VALID" , filename);
            return true;
        } catch (IOException e) {
            logger.error("File {} is not a valid imagefile ({})", filename, e.getMessage());
            return false;
        }
    }

    public BaseResponse processNew(String token, SubmitImageProcessingRequest request) {
        String image = storageService.store(request.getFile());
           if(image.endsWith(".jpg")||image.endsWith(".jpeg")){
               logger.info("Saved new file: {}" , image);
                if(!isValidImageFile(image)){
                    try {
                        Files.delete(Paths.get(image));
                    } catch (IOException e) {
                        logger.error("Cannot delete {}", image);
                    }
                    logger.error("Token {} submitted an invalid filename!" , token);
                    return new ErrorResponse("error.processing");
                }

               try{
                   File file = new File(image);
                   BufferedImage bufferedImage = ImageIO.read(file);
                   String b64Image = ImageUtils.getBufferedImageToB64(bufferedImage);
                   Files.delete(Paths.get(image));
                   logger.info("Deleted {}" , image);
                   ImageProcessingRequest oldRequest = new ImageProcessingRequest();
                   oldRequest.setImageData(b64Image);
                   oldRequest.setFileName("file.jpg");
                   oldRequest.setAsize(request.getAsize());
                   oldRequest.setBsize(request.getBsize());
                   oldRequest.setHsize(request.getHsize());
                   oldRequest.setNsize(request.getNsize());
                   oldRequest.setVsize(request.getVsize());
                   return process(token, oldRequest);
               }catch(Exception e){
                   e.printStackTrace();
                   logger.error("Exception during processNew: {}" , e.getMessage());
                   return new ErrorResponse("error.processing");
               }
           } else {
               return new  ErrorResponse("error.processing");
           }



    }

    public String getQueueInfo() {
        return queueProcessor.getQueueInfo();
    }
}
