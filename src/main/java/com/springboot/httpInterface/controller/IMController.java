package com.springboot.httpInterface.controller;

import com.springboot.httpInterface.entity.CommonMessage;
import com.springboot.httpInterface.services.ChannelService;
import com.springboot.httpInterface.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import com.springboot.httpInterface.entity.*;

/**
 * Created by yzn00 on 2019/6/24.
 */
@RestController
@RequestMapping("im")
public class IMController {
    private final ChannelService channelService;
    private final ClientService userService;

    @Autowired
    public IMController(ChannelService channelService, ClientService userService) {
        this.channelService = channelService;
        this.userService = userService;
    }

    //长轮询
    @PostMapping("poll")
    @ResponseBody
    public DeferredResult<List<CommonMessage>> poll(HttpServletRequest req){
        return channelService.poll(userService.getIMClient(req.getSession()));
    }

    //订阅
    @PostMapping("subscribe")
    public String subscribe(HttpServletRequest req,Message channel){
        channelService.subscribe(channel.getChannel(),userService.getIMClient(req.getSession()));//测试时User的作用仅仅是作为map的key所以new一个即可
        return "订阅: "+channel.getChannel();
    }
    //取消订阅
    @PostMapping("unsubscribe")
    public String unsubscribe(HttpServletRequest req,Message channel){
        if (channel != null&&channel.getChannel()!=null) {
            channelService.unsubscribe(channel.getChannel(),userService.getIMClient(req.getSession()));//测试时User的作用仅仅是作为map的key所以new一个即可
            return "取消订阅:"+channel.getChannel();
        }else{
            channelService.unsubscribe(userService.getIMClient(req.getSession()));//测试时User的作用仅仅是作为map的key所以new一个即可
            return "取消订阅全部频道";
        }
    }

    // 似乎识别不了内部的自定义对象CommonMessage,需要HTTP请求设置contentType: "application/json",
    // 把json转化为字符串传给data,并且此方法设置@RequestBody
    @PostMapping("emit")
    public String emit(HttpServletRequest req, @RequestBody Message msg){
        msg.setSender(userService.getIMClient(req.getSession()));
        channelService.emit(msg.getChannel(),msg.getSender(),msg.getMessage());
        return "发送成功";
    }

}
