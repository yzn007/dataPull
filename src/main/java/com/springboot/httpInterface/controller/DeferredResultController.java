package com.springboot.httpInterface.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by yzn00 on 2019/6/24.
 */

@RestController
@RequestMapping("/test/deferredResult")
public class DeferredResultController {
    //DeferredResult存储在队列中，若同时有多个request1接收到时，request2可以把消息发送给所有的request1请求
    private final List<DeferredResult<String>> queue = new LinkedList<>();

    @RequestMapping("request1")
    public DeferredResult<String> request1(){
        DeferredResult<String> result = new DeferredResult<>(10000L); //设置过期时间 10000ms
        result.onTimeout(()->result.setResult("deferredResult已过期"));
        synchronized (queue){
            queue.add(result);
        }
        return result;
    }

    @RequestMapping("request2")
    public String request2(String message){
        synchronized (queue){
            queue.forEach(deferredResult -> {
                deferredResult.setResult(message);
                queue.remove(deferredResult);
            });
        }
        return "success";
    }

}
