package com.springboot.httpInterface;

import com.springboot.common.MyJobFactory;
import com.springboot.httpInterface.controller.SchedulerManager;
import org.mybatis.spring.annotation.MapperScan;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;

//@RestController
@SpringBootApplication(scanBasePackages = {"com.springboot.httpInterface", "com.springboot.scala"})
//@RequestMapping("admin/")
@MapperScan(value = "com.springboot.httpInterface.dao")
@EnableScheduling
@EnableTransactionManagement
@Configuration
public class DataPullDownApplication extends SpringBootServletInitializer {

    public static void main(String[] args) throws SchedulerException {
        ConfigurableApplicationContext context = SpringApplication.run(DataPullDownApplication.class, args);
//        StaticContext.setConext(context);
        SpringContextUtil.setContext(context);
//         SpringApplication.run(DataPullDownApplication.class,args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder springApplicationBuilder) {
        return super.configure(springApplicationBuilder);
    }

    @Override
    protected WebApplicationContext createRootApplicationContext(ServletContext servletContext) {
        WebApplicationContext webApplicationContext = super.createRootApplicationContext(servletContext);
//        System.out.println("输出webApplication"+webApplicationContext);
        try {
            SpringContextUtil.setContext(webApplicationContext);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return webApplicationContext;
    }

//    @Autowired
//    private  MyJobFactory myJobFactory;

//    @Bean
//    public SchedulerFactoryBean schedulerFactoryBean() {
//        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
//        schedulerFactoryBean.setJobFactory(myJobFactory);
//        System.out.println("myJobFactory:"+myJobFactory);
//        return schedulerFactoryBean;
//    }





}

