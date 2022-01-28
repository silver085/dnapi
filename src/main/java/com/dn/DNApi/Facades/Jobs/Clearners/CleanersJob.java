package com.dn.DNApi.Facades.Jobs.Clearners;

import com.dn.DNApi.Configurations.Env;
import com.dn.DNApi.Domain.ImageQueue;
import com.dn.DNApi.Domain.Notification;
import com.dn.DNApi.Domain.Session;
import com.dn.DNApi.Facades.Jobs.QueueProcessor.QueueMultithreadProcessor;
import com.dn.DNApi.Facades.Utils.DateUtils;
import com.dn.DNApi.Repositories.NotificationRepository;
import com.dn.DNApi.Repositories.ProcessingQueueRepository;
import com.dn.DNApi.Repositories.SessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class CleanersJob {
    private static final Logger logger = LoggerFactory.getLogger(CleanersJob.class);

    @Autowired
    SessionRepository sessionRepository;
    @Autowired
    Env env;

    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    ProcessingQueueRepository processingQueueRepository;

    @Scheduled(cron = "2 0 0 * * *")
    public void cleanLibFiles(){
       logger.info("Started libstorage cleaner...");
       String path = (String) env.getProperty("application.dnlib.basefolder");
       deletePng(path);
       logger.info("Done.");
    }

    @Scheduled(cron = "1 0 0 * * *")
    public void cleanStorageFiles() {
        logger.info("Started storage cleaner...");
        String path = (String) env.getProperty("application.dnlib.storagefolder");
        deletePng(path);
        logger.info("Done.");
    }
    @Scheduled(cron = "0 0 0 * * *") //Ogni mezzanotte
    public void wageRefiller(){
        logger.info("Started wage refiller job...");
        Date oneWeekAgo = DateUtils.getOneWeekEarlier(new Date());
        String strWagesToRefill = (String) env.getProperty("application.processing.wadgesperday");
        int wagesToRefill = Integer.parseInt(strWagesToRefill);
        List<Session> lastSessions = sessionRepository.findAllByLastUseDateAfter(oneWeekAgo);
        logger.info("Collected all session after {}, {} sessions." , oneWeekAgo, lastSessions.size());
        logger.info("Refilling...");
        lastSessions.forEach(s ->{
            if(s.getWagesLeft() <= 0)
                s.setWagesLeft(wagesToRefill);
            sessionRepository.save(s);
        });
        logger.info("Done, exiting.");
    }

    @Scheduled(cron = "1 0 0 * * *")
    //@Scheduled(fixedDelay = 10000)
    public void cleanNotificationsAndQueues(){
        logger.info("Starting cleaning Notifications:");
        Date yesterday = DateUtils.getOneDayEarlier(new Date());
        List<Notification> untilYesterday = notificationRepository.findByDateBefore(yesterday);
        int notificationsSize = untilYesterday.size();
        notificationRepository.deleteAll(untilYesterday);
        logger.info("Cleaned {} notifications" , notificationsSize);
        logger.info("Cleaning Queues...");
        List<ImageQueue> queueList =  processingQueueRepository.findAllBySubmitOnBefore(yesterday);
        int queueSize = queueList.size();
        processingQueueRepository.deleteAll(queueList);
        logger.info("Cleaned {} queues.", queueSize);
    }



    private void deletePng(String stringPath) {
        Path path = Paths.get(stringPath);
        try (Stream<Path> walk = Files.walk(path)) {
            List<String> fileList = walk.map(Path::toString).filter(f->f.endsWith(".png")).collect(Collectors.toList());
            fileList.forEach(file ->{
                try {
                    Date yesterday = DateUtils.getOneDayEarlier(new Date());
                    BasicFileAttributes fileAttributes = Files.readAttributes(Paths.get(file), BasicFileAttributes.class);
                    Date fileCreation = new Date(fileAttributes.creationTime().toMillis());
                    if(fileCreation.compareTo(yesterday) < 0) {
                        logger.info("File {} is older then {}" , file, yesterday);
                        Files.delete(Paths.get(file));
                    }
                } catch (IOException e) {
                    logger.error("Cannot delete {} -> {} ", file, e.getMessage());
                }
            });
    } catch (IOException ex) {
            logger.error("Cannot walk path: {} -> {}", stringPath, ex.getMessage());
        }

    }
}