package com.springboot.common;

/**
 * Created by yzn00 on 2021/3/15.
 */

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SchedulerListener implements JobListener {
    public static final String LISTENER_NAME = "QuartSchedulerListener";

    @Override
    public String getName() {
        return LISTENER_NAME; //must return a name
    }

    //任务被调度前
    @Override
    public void jobToBeExecuted(JobExecutionContext context) {

        String jobName = context.getJobDetail().getKey().toString();
//        System.out.println("jobToBeExecuted");
//        System.out.println("Job : " + jobName + " is going to start...");

    }

    //任务调度被拒了
    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
//        System.out.println("jobExecutionVetoed");
        //可以做一些日志记录原因

    }

    //任务被调度后
    @Override
    public void jobWasExecuted(JobExecutionContext context,
                               JobExecutionException jobException) {
//        System.out.println("jobWasExecuted");

        String jobName = context.getJobDetail().getKey().toString();
        Date date = null;
        try {
            for(JobExecutionContext o: context.getScheduler().getCurrentlyExecutingJobs()){
                if(o.getTrigger().getKey().toString().equals(jobName)) {
                    date = o.getNextFireTime();
                    SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    System.out.println("Job : " + jobName + " is finished...next fire time is:" +sp.format(date));
                    break;
                }
            }

        }catch (Exception e){

        }


        if (jobException!=null&&!jobException.getMessage().equals("")) {
//            System.out.println("Exception thrown by: " + jobName
//                    + " Exception: " + jobException.getMessage());
        }

    }
}
