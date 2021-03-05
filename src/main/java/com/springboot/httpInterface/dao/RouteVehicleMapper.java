package com.springboot.httpInterface.dao;

import com.springboot.httpInterface.entity.RouteVehicle;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by yzn00 on 2019/7/27.
 */
@Repository
@Mapper
public interface RouteVehicleMapper {
    List<RouteVehicle> getAllVehicles();
}
