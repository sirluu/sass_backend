package jp.co.japantaxi;

import java.util.TimeZone;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import jp.co.japantaxi.config.CustomBanner;

@ComponentScan(basePackages = {"jp.co.japantaxi.config", "jp.co.japantaxi.controller"})
@MapperScan(value = "jp.co.japantaxi.mapper")
@SpringBootApplication
public class WorkerApplication {
  public static void main(String[] args) {
    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tokyo")); 
    SpringApplication app = new SpringApplication(WorkerApplication.class);
    app.setBanner(new CustomBanner());
    app.run(args);
  }
}
