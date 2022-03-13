package cn.enumaelish.file.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author: EnumaElish
 * @Date: 2022/3/3 19:12
 * @Description: 
 */
@Configuration
@ComponentScan(
		basePackages = {"cn.enumaelish.file","com.alibaba.cola"}
)
@MapperScan(basePackages = "cn.enumaelish.file.dao",sqlSessionFactoryRef="sqlSessionFactory")
public class FileAutoConfiguration {


}
