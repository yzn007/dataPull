package com.springboot.httpInterface.controller;

import com.springboot.httpInterface.entity.RouteVehicle;
import com.springboot.httpInterface.services.RouteService;
import com.springboot.httpInterface.services.RouteVehicleService;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by yzn00 on 2019/7/27.
 */
@RestController
@RequestMapping(value ="RouteVehicle/")
public class RouteVehicleController {
    @Autowired
    RouteVehicleService routeVehicleService;

    @ResponseBody
    @RequestMapping
    public Map getAllVehs(){
        Map m = new HashedMap();
        m.put("data",routeVehicleService.getAllVehicle());
        return m;
    }

}
