package com.dn.DNApi.Facades.Jobs.QueueProcessor;

import com.dn.DNApi.Configurations.Env;
import com.dn.DNApi.Domain.ImageQueue;
import com.dn.DNApi.Domain.User;
import com.dn.DNApi.Facades.AuthenticationFacade;
import com.dn.DNApi.Facades.NotificationFacade;
import com.dn.DNApi.Facades.Utils.DateUtils;
import com.dn.DNApi.Repositories.ProcessingQueueRepository;
import com.dn.DNApi.Repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.*;

@Component
public class QueueMultithreadProcessor {
    private static final Logger logger = LoggerFactory.getLogger(QueueMultithreadProcessor.class);
    @Autowired
    ProcessingQueueRepository queueRepository;
    @Autowired
    Env env;
    @Autowired
    NotificationFacade notificationFacade;
    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationFacade authenticationFacade;

    static int MAX_CONCURRENT_THREADS = 2;
    private List<SrvQueueThread> threadList = Collections.synchronizedList(new ArrayList<SrvQueueThread>());
    private boolean executorBlocked = false;
    private int avgTime = 30;

    @Scheduled(fixedDelay = 2000)
    public void executeQueue(){
        if(executorBlocked) return;
        executorBlocked = true;

        logger.debug("Gathering process queue...");
        List<ImageQueue> queue = queueRepository.findAllByCompleted(false);
        logger.debug("Process queue is currently {}", queue.size());
        getCPULoad();

        if(queue.size() == 0){
            logger.debug("Process queue is empty, exiting.");
            executorBlocked = false;
            avgTime = 30;
            return;
        }



        for(SrvQueueThread thread : threadList){
            if(!thread.isAlive()){
                logger.debug("Deleting {} from thread list" , thread.getCurrentTime());
                ImageQueue currentImage = thread.getImageQueue();
                currentImage.setCompletedOn(new Date());
                int currentDuration = DateUtils.getSecondsDifference(currentImage.getStartedOn(), new Date());
                if(currentDuration > avgTime) avgTime = currentDuration;
                currentImage.setCompleted(true);
                currentImage.setRunning(false);

                queueRepository.save(currentImage);

                if(currentImage.isOnError()){
                    authenticationFacade.addAWage(currentImage.getToken());
                }

                try {
                    notificationFacade.saveCompletedProcessNotification(currentImage);
                }catch (Exception e){
                    logger.error("Cannot save notification QueueId: {}" , currentImage.getId());

                }
                threadList.remove(thread);
                executorBlocked = false;

                return;
            }
        }

        logger.debug("Active threads: {}", threadList.size());
        if(getCoreLoad() >= 5){
            logger.info("CPU Load high, exiting.");
            executorBlocked = false;
            return;
        }

        if(threadList.size() >= MAX_CONCURRENT_THREADS){
            logger.info("Threads queue is full, exiting.");
            executorBlocked = false;
            return;
        }
        ImageQueue nextQueue = queue.stream()
                .filter(q -> !q.isCompleted() && q.getCompletedOn() == null && !q.isRunning())
                .min(Comparator.comparing(ImageQueue::getSubmitOn))
                .orElse(null);

        if(nextQueue == null){
            executorBlocked = false;
            return;
        }

        SrvQueueThread activeThread = new SrvQueueThread(nextQueue);
        Optional<User> owner = userRepository.findAllByToken(nextQueue.getToken()).stream().findFirst();
        owner.ifPresent(user -> activeThread.setOwnerEmail(user.getEmail()));
        nextQueue.setRunning(true);
        nextQueue.setStartedOn(new Date());
        queueRepository.save(nextQueue);
        activeThread.setCommand((String) env.getProperty("application.dnlib.command"));
        activeThread.setDnLibBaseFolder((String) env.getProperty("application.dnlib.basefolder"));
        activeThread.setDnLibStorageFolder((String) env.getProperty("application.dnlib.storagefolder"));
      /*
            Adding optional sizes commands
       */

        activeThread.setBsizeCommand((String) env.getProperty("settings.bsize"));
        activeThread.setAsizeCommand((String) env.getProperty("settings.asize"));
        activeThread.setNsizeCommand((String) env.getProperty("settings.nsize"));
        activeThread.setVsizeCommand((String) env.getProperty("settings.vsize"));
        activeThread.setHsizeCommand((String) env.getProperty("settings.hsize"));

        threadList.add(activeThread);
        activeThread.start();
        executorBlocked = false;
    }

    public void flushQueue(){
        executorBlocked = true;
        List<ImageQueue> queue = queueRepository.findAllByCompleted(false);
        queue.forEach(q -> {
            q.setCompletedOn(new Date());
            q.setProcessedFilename("error");
            q.setErrorMessage("Queue has stopped by admin.");
            q.setCompleted(true);
            q.setOnError(true);
            queueRepository.save(q);
        });
        for(SrvQueueThread t : threadList){
            t.interrupt();
        }
        threadList = Collections.synchronizedList(new ArrayList<SrvQueueThread>());
        logger.debug("Thread queue has been erased.");
        executorBlocked = false;
    }

    public int getQueuePosition(String id) {
        int i = 0;
        for(SrvQueueThread t : this.threadList){
            if(t.getImageQueue().getId().equals(id)){
                break;
            }
            i++;
        }
        return i+1;
    }

    public int getWaitingTime(String id) {
        int position = getQueuePosition(id);
        return avgTime * position;
    }

    private double getCoreLoad() {
        OperatingSystemMXBean operatingSystemMXBean =
                (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        return  operatingSystemMXBean.getSystemLoadAverage();
    }

    public void getCPULoad(){
        OperatingSystemMXBean operatingSystemMXBean =
                (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        logger.debug("Available processor cores: {}" , operatingSystemMXBean.getAvailableProcessors() );
        logger.debug("Load average: {}", operatingSystemMXBean.getSystemLoadAverage() );

    }

    public QueueDetails getQueueDetails(String queueId){
        QueueDetails queueDetails = new QueueDetails();
        SrvQueueThread myThread = null;
        int index = 0;
        for(SrvQueueThread t : this.threadList){
            if(t.getImageQueue().getId().equals(queueId)){
                myThread = t;
                break;
            }
            index++;
        }
        queueDetails.setTotalQueue(threadList.size());
        queueDetails.setTimeLeft((index+1) * avgTime);
        queueDetails.setPosition(index+1);
        return queueDetails;
    }

    public String getQueueInfo() {
        List<ImageQueue> queue = queueRepository.findAllByCompleted(false);
        StringBuffer buffer = new StringBuffer();
        buffer.append("Queue on hold: ").append(queue.size()).append("<br/>");
        OperatingSystemMXBean operatingSystemMXBean =
                (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        buffer.append("Available processor cores: ").append( operatingSystemMXBean.getAvailableProcessors()).append("<br/>");
        buffer.append("Load average: ").append(operatingSystemMXBean.getSystemLoadAverage() ).append("<br/>");
        ImageQueue nextQueue = queue.stream()
                .filter(q -> !q.isCompleted() && q.getCompletedOn() == null && !q.isRunning())
                .min(Comparator.comparing(ImageQueue::getSubmitOn))
                .orElse(null);
        if(nextQueue != null){
            buffer.append("Next queueId: ").append(nextQueue.getId()).append("<br/>");
        }
        return buffer.toString();
    }
}
