package cn.enumaelish.newfile.config;

import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import cn.enumaelish.file.util.JsonUtil;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: EnumaElish
 * @Date: 2022/3/3 19:24
 * @Description: 
 */
@Configuration
public class HttpMessageConvertersConfig {

    /**
     * 使用fastjson序列化web返回结果
     * @return
     */
    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        // 1. 需要定义一个converter转换消息的对象
        FastJsonHttpMessageConverter fasHttpMessageConverter = new FastJsonHttpMessageConverter();
        // 2. 添加fastjson的配置信息，比如:是否需要格式化返回的json的数据
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(JsonUtil.getFEATURES());
        // 3. 在converter中添加配置信息
        fasHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        fastJsonConfig.setCharset(Charset.forName("UTF-8"));
        // 中文乱码解决方案
        List<MediaType> mediaTypes = new ArrayList<>();
        //设定json格式且编码为UTF-8
        mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        fasHttpMessageConverter.setSupportedMediaTypes(mediaTypes);
        HttpMessageConverter<?> converter = fasHttpMessageConverter;
        return new HttpMessageConverters(converter);
    }
}
