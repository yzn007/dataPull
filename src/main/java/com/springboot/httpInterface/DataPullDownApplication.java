package com.springboot.httpInterface;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;

//@RestController
@SpringBootApplication(scanBasePackages = {"com.springboot.httpInterface", "com.springboot.scala"})
//@RequestMapping("admin/")
@MapperScan(value = "com.springboot.httpInterface.dao")
@EnableScheduling
@EnableTransactionManagement
public class DataPullDownApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DataPullDownApplication.class, args);
//        StaticContext.setConext(context);
        SpringContextUtil.setContext(context);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder springApplicationBuilder) {
        return super.configure(springApplicationBuilder);
    }

    @Override
    protected WebApplicationContext createRootApplicationContext(ServletContext servletContext) {
        WebApplicationContext webApplicationContext = super.createRootApplicationContext(servletContext);
//        System.out.println("输出webApplication"+webApplicationContext);
        SpringContextUtil.setContext(webApplicationContext);
        return webApplicationContext;
    }
}

