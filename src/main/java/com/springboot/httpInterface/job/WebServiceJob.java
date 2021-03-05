package com.springboot.httpInterface.job;

import com.alibaba.fastjson.JSONObject;
import com.springboot.common.JsonObjectToAttach;
import com.springboot.common.ReadPropertiesUtils;
import com.springboot.common.SaveDataStatic;
import com.springboot.common.WebServiceWrite;
import com.springboot.httpInterface.controller.HttpServiceTest;
import com.springboot.httpInterface.entity.Route;
import com.springboot.httpInterface.entity.Token;
import com.springboot.httpInterface.services.RouteService;
import com.springboot.httpInterface.services.TokenService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebServiceJob implements BaseJob {

    private static Logger _log = LoggerFactory.getLogger(WebServiceJob.class);

    final static int NUM_PROCESS = 6;
    static Map<String, String> config = new HashMap<String, String>();

    //取得静态表
    static Map<String, String> topicS = new HashMap<>();

    private String jsonStr = "";
    private String table = "BUS_VEHIC_LCTN_MSG";


    final String topicName = WebServiceJob.class.getSimpleName();
    final String configName = "project.properties";

    public WebServiceJob() {
        try {
            if (config.size() == 0)
                config.putAll(ReadPropertiesUtils.readConfig(configName));
            if (topicS.size() == 0)
                //取得静态表
                topicS = JsonObjectToAttach.getValidProperties(topicName, null, null, true);



        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }


    @Override
    public void execute(JobExecutionContext context)
            throws JobExecutionException {
//        _log.error("New Job执行时间: " + new Date());

//
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_PROCESS);

        try {
//    this.jsonStr = httpServiceTest.getJsonData("http://localhost/httpService/sendGetData?RayData=CurrTotlCnt", "utf-8");
            WebServiceWrite we = new WebServiceWrite();
            executorService.execute(we);
            executorService.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}