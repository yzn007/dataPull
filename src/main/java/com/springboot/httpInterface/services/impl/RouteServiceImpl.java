package com.springboot.httpInterface.services.impl;

import com.springboot.httpInterface.dao.RouteMapper;
import com.springboot.httpInterface.entity.Route;
import com.springboot.httpInterface.services.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by yzn00 on 2019/7/11.
 */
@Service
public class RouteServiceImpl implements RouteService {
    @Autowired
    RouteMapper routeMapper;

    @Override
    public List<Route> getAllRoute() {
        return routeMapper.getAllRoute();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout=36000,rollbackFor=Exception.class)
    public void deleAllRec(Map m) {
        routeMapper.deleAllRec(m);
    }
}
