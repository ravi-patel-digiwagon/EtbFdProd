package com.suryoday.FdOpening;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.suryoday.FdOpening.Others.CronProperties;

@SpringBootApplication
@EnableConfigurationProperties(CronProperties.class)
public class FdOpeningApplication {

	public static void main(String[] args) {
		SpringApplication.run(FdOpeningApplication.class, args);
	}

}
