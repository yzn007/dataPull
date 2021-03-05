package com.springboot.common;

import com.springboot.httpInterface.job.BaseJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by yzn00 on 2019/7/31.
 */
public class testSynchize implements BaseJob {

    UUID uuid = UUID.randomUUID();

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            System.out.println("testSynchize：["+uuid+"]启动任务======================="+
            new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            run();
//            System.out.println("testSynchize：["+uuid+"]下次执行时间====="+
//                    new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
//                            .format(new Date())+"==============");

        }catch (Exception e){

        }
    }

    public void run(){
        try {
            Thread.sleep(6000);
            System.out.println("testSynchize：["+uuid+"]执行完毕======================="+new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                    .format(new Date())+"==============");
        }catch (Exception ex){

        }

    }

}
