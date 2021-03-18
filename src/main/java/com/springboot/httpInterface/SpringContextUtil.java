package com.springboot.httpInterface;

import com.springboot.httpInterface.controller.SchedulerManager;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.stereotype.Component;

/**
 * Created by yzn00 on 2019/8/3.
 */

//@Component
public class SpringContextUtil extends SpringBeanJobFactory implements ApplicationContextAware {
    private static ApplicationContext applicationContext = null;
    private transient AutowireCapableBeanFactory beanFactory;
    @Override
    public  void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        beanFactory= applicationContext.getAutowireCapableBeanFactory();
    }

    @Override
    protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
        //Using SpringBeanJobFactory to create job
        final Object job = super.createJobInstance(bundle);
        //Using context to get AutowireCapableBeanFactory and using beanFactory to autowire job instance
        beanFactory.autowireBean(job);
        return job;
    }

    public static void setContext(ApplicationContext applicationContext) throws BeansException, SchedulerException {
//        System.out.print("初始化设置上下文:"+applicationContext);
        SpringContextUtil.applicationContext = applicationContext;
        //内存模式打开
        startJob();
    }
    public static ApplicationContext getApplicationContext(){
        return applicationContext;
    }

    public static Object getBean(String beanName){
        System.out.print("getBean:"+applicationContext);
        return applicationContext.getBean(beanName);
    }
    public  static Object getBean(Class c){
        System.out.print("getBean:"+applicationContext);
        return applicationContext.getBean(c);
    }

    @Autowired
    private static SchedulerManager quartzManager;
    public static void startJob() throws SchedulerException {
        if(null== quartzManager)
            quartzManager = applicationContext.getBean(SchedulerManager.class);
        quartzManager.startJob();
    }
}
