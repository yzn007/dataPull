package com.springboot.httpInterface.controller;

import com.springboot.httpInterface.services.TokenService;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by yzn00 on 2019/7/11.
 */
@Controller
@RequestMapping(value = "Token/")
public class TokenController {
    @Autowired
    TokenService tokenService;
    @RequestMapping(value = "getToken")
    @ResponseBody
    public Map getToken(){
        Map map = new HashedMap();
        map.put("data",tokenService.getAllToken());
        return map;
    }
}
