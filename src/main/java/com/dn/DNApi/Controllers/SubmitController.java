package com.dn.DNApi.Controllers;

import com.dn.DNApi.DTO.BaseResponse;
import com.dn.DNApi.DTO.SubmitImageProcessingRequest;
import com.dn.DNApi.Services.ProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class SubmitController {
    @Autowired
    ProcessorService processorService;

    @RequestMapping(method = RequestMethod.POST, value = "/api/processor/submitprocess")
    @ResponseBody
    public String submitProcessImage(@RequestParam String token,
                                           @RequestParam(required = false) Double asize,
                                           @RequestParam(required = false) Double bsize,
                                           @RequestParam(required = false) Double hsize,
                                           @RequestParam(required = false) Double nsize,
                                           @RequestParam(required = false) Double vsize,
                                           @ModelAttribute SubmitImageProcessingRequest image){
        image.setBsize(bsize);
        image.setAsize(asize);
        image.setHsize(hsize);
        image.setNsize(nsize);
        image.setVsize(vsize);

        String response = processorService.processImageNew(token, image);
        return response;

    }
}
