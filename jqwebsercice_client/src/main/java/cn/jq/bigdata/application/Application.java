package cn.jq.bigdata.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = {"cn.jq.bigdata"})
@EnableConfigurationProperties
public class Application {

        public static void main(String[] args) {
        /**
         * 在main方法进行启动我们的应用程序
         */
        SpringApplication.run(Application.class,args);
    }
}
