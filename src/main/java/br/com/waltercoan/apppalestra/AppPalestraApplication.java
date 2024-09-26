package br.com.waltercoan.apppalestra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.retry.annotation.EnableRetry;
import br.com.waltercoan.apppalestra.config.AppConfigProperties;


@SpringBootApplication
@EnableRetry(proxyTargetClass=true) 
@EnableConfigurationProperties(AppConfigProperties.class)
public class AppPalestraApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppPalestraApplication.class, args);
	}

}
