package com.springboot.common;

import com.springboot.httpInterface.services.CxfWebService;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by yzn00 on 2019/7/23.
 */
public class WebServiceWrite extends Thread {

    @Override
    public void run() {
        Logger.getLogger(this.getClass().getName()).setLevel(Level.OFF);
        CxfWebService cxfWebService = new CxfWebService();
        cxfWebService.cl2();
    }
}
