package com.dn.DNApi.Controllers;

import com.dn.DNApi.DTO.*;
import com.dn.DNApi.Services.ProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/processor")
public class Processor {
    @Autowired
    ProcessorService processorService;
    @CrossOrigin(origins = {"http://localhost:3000"})
    @GetMapping("/probe")
    public ProbeResponse getProbeResponse(){
        return processorService.getProbe();
    }

    @CrossOrigin(origins = {"http://localhost:3000"})
    @PostMapping("/process")
    public BaseResponse processImage(@RequestParam String token, @RequestBody ImageProcessingRequest image){
        return processorService.processImage(token, image);
    }



    @CrossOrigin(origins = {"http://localhost:3000"})
    @GetMapping("/status")
    public BaseResponse getProcessingStatus(@RequestParam String token, @RequestParam String queueId){
        return processorService.getQueue(token, queueId);
    }

    @CrossOrigin(origins = {"http://localhost:3000"})
    @GetMapping("/filedata")
    public BaseResponse getFileData(@RequestParam String token, @RequestParam String queueId){
        return processorService.getFileData(token, queueId);
    }

    @CrossOrigin(origins = {"http://localhost:3000"})
    @GetMapping("/flushqueue")
    public BaseResponse startQueue(){
        return processorService.flushQueue();
    }

    @CrossOrigin(origins = {"http://localhost:3000"})
    @GetMapping("/getnotifications")
    public BaseResponse getNotifications(@RequestParam String userId, int startIndex, int offset){
        return processorService.getNotifications(userId, startIndex, offset);
    }

    @CrossOrigin(origins = {"http://localhost:3000"})
    @GetMapping("/countnotifications")
    public BaseResponse countNotifications(@RequestParam String userId){
        return processorService.countUnreadNotifications(userId);
    }

    @CrossOrigin(origins = {"http://localhost:3000"})
    @GetMapping("/markasread")
    public BaseResponse markAsRead(@RequestParam List<String> notificationIds){
        return processorService.markAsRead(notificationIds);
    }


}
