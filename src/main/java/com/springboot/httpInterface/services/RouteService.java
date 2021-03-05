package com.springboot.httpInterface.services;

import com.springboot.httpInterface.entity.Route;

import java.util.List;
import java.util.Map;

/**
 * Created by yzn00 on 2019/7/11.
 */
public interface RouteService {
    List<Route>getAllRoute();
    void deleAllRec(Map m);
}
