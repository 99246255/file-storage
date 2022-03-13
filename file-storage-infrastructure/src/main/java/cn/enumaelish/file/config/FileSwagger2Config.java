package cn.enumaelish.file.config;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author: EnumaElish
 * @Date: 2022/3/3 19:06
 * @Description:
 */
@Configuration
@EnableSwagger2
@EnableSwaggerBootstrapUI
public class FileSwagger2Config {

    @Value("${swagger2.enable:false}")
    private boolean enable;

    @Bean(name = "hydraFileApis")
    public Docket hydraFileApis() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("文件服务接口")
                .select()
                .apis(RequestHandlerSelectors.basePackage("cn.enumaelish.file.web"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .enable(enable);
    }



    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("文件服务接口接口文档")
                .description("")
                .termsOfServiceUrl("")
                .version("0.1")
                .build();
    }

}