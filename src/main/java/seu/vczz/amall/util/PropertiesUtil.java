package seu.vczz.amall.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * CREATE by vczz on 2018/3/17
 * 读取配置文件的工具
 */
public class PropertiesUtil {
    //日志
    private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
    //还有Properties类，没想到吧
    private static Properties props;

    static {
        String fileName = "amall.properties";
        props = new Properties();
        try {
            //由于src和resource都是被打包在了bin目录下，因此getResourceAsStream相当于在当前目录下
            props.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName),"UTF-8"));
        } catch (IOException e) {
            logger.error("配置文件读取异常",e);
        }
    }
    //通过key获得value
    public static String getProperty(String key){
        String value = props.getProperty(key.trim());
        //如果value为空，返回null
        if(StringUtils.isBlank(value)){
            return null;
        }
        return value.trim();
    }
    //通过key获得value，如果value为空，返回默认的value
    public static String getProperty(String key,String defaultValue){

        String value = props.getProperty(key.trim());
        if(StringUtils.isBlank(value)){
            value = defaultValue;
        }
        return value.trim();
    }

}
