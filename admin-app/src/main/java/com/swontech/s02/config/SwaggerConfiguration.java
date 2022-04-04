package com.swontech.s02.config;

/**
 * swagger3.0 Configuration 클래스
 * http://localhost:8080/swagger-ui/index.html
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfiguration {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
                .select()
                // 현재 RequestMapping으로 할당된 모든 URL 리스트를 추출
                .apis(RequestHandlerSelectors.any())
                // /rest/v1/** 인 URL들만 필터링
                .paths(PathSelectors.ant("/rest/v1/**"))
                .build()
                .apiInfo(apiInfo());
    }

    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("SwonTech S02 REST Api Docs")
                .description("에스원테크 출입관리시스템 Rest Api 가이드 문서")
                .version("1.0")
                .build();
    }
}
