package com.springboot.common;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;
/**
 * Created by yzn00 on 2019/7/4.
 */


@Configuration
@EnableScheduling

public class QuartzConfig {
    @Bean
    public JobDetail myJobDetail() {
//        JobDetail jobDetail = JobBuilder.newJob(MyJob.class).withIdentity("myJob1", "myJobGroup1")
//                //JobDataMap可以给任务execute传递参数
//                .usingJobData("job_param", "job_param1").storeDurably().build();
//        return jobDetail;
        return null;
    }

    @Bean
    public Trigger myTrigger() {
        Trigger trigger = TriggerBuilder.newTrigger().forJob(myJobDetail())
                .withIdentity("myTrigger1", "myTriggerGroup1")
                .usingJobData("job_trigger_param", "job_trigger_param1").startNow()
//                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                //.withIntervalInSeconds(5).repeatForever())
        .withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * * * ?")
        ).build();
        return trigger;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource)
    {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setDataSource(dataSource);

        // quartz参数
        Properties prop = new Properties();
        //调度标识名 集群中每一个实例都必须使用相同的名称
        prop.put("org.quartz.scheduler.instanceName", "UserPullScheduler");
        //调度器实例编号自动生成，每个实例不能不能相同
        prop.put("org.quartz.scheduler.instanceId", "AUTO");

        // 线程池配置
        //实例化ThreadPool时，使用的线程类为SimpleThreadPool（一般使用SimpleThreadPool即可满足几乎所有用户的需求）
        prop.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        //并发个数,指定线程数，至少为1（无默认值）(一般设置为1-100之间的的整数合适)
        prop.put("org.quartz.threadPool.threadCount", "20");
        //设置线程的优先级（最大为java.lang.Thread.MAX_PRIORITY 10，最小为Thread.MIN_PRIORITY 1，默认为5）
        prop.put("org.quartz.threadPool.threadPriority", "5");

        // 数据库方式 JobStore配置
        prop.put("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        //持久化方式配置数据驱动，ORACLE数据
        prop.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");

//        //开启分布式部署，集群
//        prop.put("org.quartz.jobStore.isClustered", "true");

//        //分布式节点有效性检查时间间隔，单位：毫秒,默认值是15000
//        prop.put("org.quartz.jobStore.clusterCheckinInterval", "15000");
//        prop.put("org.quartz.jobStore.maxMisfiresToHandleAtATime", "1");
//        prop.put("org.quartz.jobStore.txIsolationLevelSerializable", "false");

        //容许的最大作业延长时间,最大能忍受的触发超时时间，如果超过则认为"失误",不敢再内存中还是数据中都要配置
        prop.put("org.quartz.jobStore.misfireThreshold", "12000");
        //quartz相关数据表前缀名
        prop.put("org.quartz.jobStore.tablePrefix", "QRTZ_");
        factory.setQuartzProperties(prop);

//        //调度标识名 集群中每一个实例都必须使用相同的名称
//        factory.setSchedulerName("UserPullScheduler");
        // 延时启动
        factory.setStartupDelay(1);
        factory.setApplicationContextSchedulerContextKey("applicationContextKey");
        // 可选，QuartzScheduler
        // 启动时更新己存在的Job，这样就不用每次修改targetObject后删除qrtz_job_details表对应记录了
        factory.setOverwriteExistingJobs(true);
        // 设置自动启动，默认为true
        factory.setAutoStartup(true);

        return factory;
    }
}


