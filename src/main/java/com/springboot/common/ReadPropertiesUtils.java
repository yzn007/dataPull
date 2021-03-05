package com.springboot.common;


import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Created by DFJX on 2019/6/23.
 */

public class ReadPropertiesUtils {
    public static Map<String, String> readConfig(String configFileName) throws IOException {
        Map<String, String> config = new HashMap<String, String>();
        InputStream in = ReadPropertiesUtils.class.getClassLoader().getResourceAsStream(configFileName);
        Properties properties = new Properties();
        properties.load(in);
        Set<Map.Entry<Object, Object>> entrySet = properties.entrySet();
        for (Map.Entry<java.lang.Object, java.lang.Object> entry : entrySet) {
            String key = String.valueOf(entry.getKey());
            String value = String.valueOf(entry.getValue());
            config.put(key, value);
        }
        return config;
    }
}
