package com.springboot.httpInterface;

import org.springframework.context.ConfigurableApplicationContext;

/**
 * Created by yzn00 on 2019/8/2.
 */

public class StaticContext {
    private static ConfigurableApplicationContext context;

    public static void setConext(ConfigurableApplicationContext context) {
        StaticContext.context = context;
    }

    public static ConfigurableApplicationContext getContext() {
        return context;
    }
}