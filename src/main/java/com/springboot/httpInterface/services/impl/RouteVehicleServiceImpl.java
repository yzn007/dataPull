package com.springboot.httpInterface.services.impl;

import com.springboot.httpInterface.dao.RouteVehicleMapper;
import com.springboot.httpInterface.entity.RouteVehicle;
import com.springboot.httpInterface.services.RouteVehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yzn00 on 2019/7/27.
 */
@Service
public class RouteVehicleServiceImpl implements RouteVehicleService {
    @Autowired
    RouteVehicleMapper routeVehicleMapper;

    @Override
    public List<RouteVehicle> getAllVehicle(){
        return routeVehicleMapper.getAllVehicles();
    }
}
