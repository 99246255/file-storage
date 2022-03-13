package cn.enumaelish.newfile.config;


import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

/**
 * @author: EnumaElish
 * @Date: 2022/3/3 19:23
 * @Description: 
 */
@Configuration
public class DataSourceConfig {
	
	@Autowired
	private Environment env;

	/**
	 * 数据源dataSource配置
	 */
	@Bean(name = "dataSource")
	public DataSource dataSource(){
		 DruidDataSource dataSource = new DruidDataSource();
		 dataSource.setUrl(env.getProperty("spring.datasource.url"));
		 dataSource.setUsername(env.getProperty("spring.datasource.username"));
		 dataSource.setPassword(env.getProperty("spring.datasource.password"));
		 dataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
		 dataSource.setInitialSize(Integer.valueOf(env.getProperty("spring.datasource.dbcp.initial-size")));
		 dataSource.setMinIdle(Integer.valueOf(env.getProperty("spring.datasource.dbcp.min-idle")));
		 dataSource.setMaxActive(Integer.valueOf(env.getProperty("spring.datasource.dbcp.max-active")));
		 dataSource.setMaxWait(Long.valueOf(env.getProperty("spring.datasource.dbcp.max-wait")));
		 dataSource.setTimeBetweenEvictionRunsMillis(Long.valueOf(env.getProperty("spring.datasource.dbcp.time-between-eviction-runs-millis")));
		 dataSource.setMinEvictableIdleTimeMillis(Long.valueOf(env.getProperty("spring.datasource.dbcp.min-evictable-idle-time-millis")));
		 dataSource.setValidationQuery(env.getProperty("spring.datasource.dbcp.validation-query"));
		 dataSource.setTestWhileIdle(Boolean.valueOf(env.getProperty("spring.datasource.dbcp.test-while-idle")));
		 dataSource.setTestOnBorrow(Boolean.valueOf(env.getProperty("spring.datasource.dbcp.test-on-borrow")));
		 dataSource.setTestOnReturn(Boolean.valueOf(env.getProperty("spring.datasource.dbcp.test-on-return")));
		dataSource.setUseLocalSessionState(false);
		 return dataSource;
	}

}
