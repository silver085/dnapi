package com.dn.DNApi.Facades.Jobs.QueueProcessor;

import com.dn.DNApi.Configurations.Env;
import com.dn.DNApi.Domain.ImageQueue;
import com.dn.DNApi.Repositories.ProcessingQueueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class QueueProcessor {
    private static final Logger logger = LoggerFactory.getLogger(QueueProcessor.class);
    private SrvQueueThread activeThread = null;
    @Autowired
    ProcessingQueueRepository queueRepository;
    @Autowired
    Env env;


    //@Scheduled(fixedDelay = 10000)
    public void processQueue(){
        logger.info("Gathering process queue...");
        List<ImageQueue> queue = queueRepository.findAllByCompleted(false);
        logger.info("Process queue is currently {}", queue.size());
        if(queue.size() == 0){
            logger.info("Process queue is empty, exiting.");
            return;
        }



        if(activeThread != null){
            if(activeThread.isAlive()){
                logger.info("There is still a thread running, must wait, exiting.");
                return;
            } else {
                //thread completed!
                //close and remove

                ImageQueue currentImage = activeThread.getImageQueue();
                currentImage.setCompletedOn(new Date());
                currentImage.setCompleted(true);
                queueRepository.save(currentImage);
                activeThread = null;
                return;
            }
        }
        ImageQueue nextQueue = queue.stream()
                .filter(q -> !q.isCompleted() && q.getCompletedOn() == null)
                .min(Comparator.comparing(ImageQueue::getSubmitOn))
                .orElse(null);

        if(nextQueue != null){
            activeThread = new SrvQueueThread(nextQueue);
            activeThread.setCommand((String) env.getProperty("application.dnlib.command"));
            activeThread.setDnLibBaseFolder((String) env.getProperty("application.dnlib.basefolder"));
            activeThread.setDnLibStorageFolder((String) env.getProperty("application.dnlib.storagefolder"));
            activeThread.start();
        }
    }

}
