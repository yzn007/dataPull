package com.springboot.httpInterface.controller;

import com.springboot.httpInterface.services.AsyncRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by yzn00 on 2019/6/20.
 */
@RequestMapping("/async")
@RestController
public class AsyncRequestController {

    @Autowired
    private AsyncRequestService asyncRequestService;

    @GetMapping("/value")
    public String getValue() {
        String msg =  null;
        Future<String> result = null;
        try{
            result = asyncRequestService.getValue();
            msg = result.get(10, TimeUnit.SECONDS);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (result != null){
                result.cancel(true);
            }
        }

        return msg;
    }

    @PostMapping("/value")
    public void postValue(String msg) {
        asyncRequestService.postValue(msg);
    }
}
