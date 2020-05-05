package jp.co.japantaxi.config;

import java.sql.Timestamp;
import java.util.Date;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2).select()
        .apis(RequestHandlerSelectors.basePackage("jp.co.japantaxi.controller"))
        .paths(PathSelectors.regex("/.*")).build().apiInfo(apiEndPointsInfo())
        .directModelSubstitute(Timestamp.class, Date.class);
  }

  private ApiInfo apiEndPointsInfo() {
    return new ApiInfoBuilder().title("Stockholm Service API Documentation")
        .description("Salesforce からのデータを定期的に保管するシステ")
        .contact(new Contact("SalesforceTeam", "https://co-well.vn", "support@co-well.vn"))
        .license("COWELL-ASIA").licenseUrl("https://co-well.vn").version("1.0.0").build();
  }
}