package com.springboot.httpInterface.services;

import com.springboot.httpInterface.entity.RouteVehicle;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yzn00 on 2019/7/27.
 */
public interface RouteVehicleService {
    List<RouteVehicle> getAllVehicle();
}

