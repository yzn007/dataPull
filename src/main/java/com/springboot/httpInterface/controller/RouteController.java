package com.springboot.httpInterface.controller;

import com.springboot.httpInterface.services.RouteService;
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
@RequestMapping(value = "Route/")
public class RouteController {
    @Autowired
    RouteService routeService;
    @RequestMapping(value = "getRoute")
    @ResponseBody
    public Map getRoute(){
        Map map = new HashedMap();
        map.put("data",routeService.getAllRoute());
        return map;
    }
}
