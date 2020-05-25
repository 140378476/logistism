package cn.ancono.logistism;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication()//exclude= {DataSourceAutoConfiguration.class}
public class LogistismApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogistismApplication.class, args);
    }

}
