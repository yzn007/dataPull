package com.springboot.httpInterface.dao;

import com.springboot.httpInterface.entity.Route;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by yzn00 on 2019/6/19.
 */
@Repository
@Mapper
public interface RouteMapper {
     List<Route> getAllRoute();
     void deleAllRec(Map m);
}
