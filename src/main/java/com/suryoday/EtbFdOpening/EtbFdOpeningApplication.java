package com.suryoday.EtbFdOpening;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.suryoday.EtbFdOpening.Others.CronProperties;

@SpringBootApplication
@EnableConfigurationProperties(CronProperties.class)
public class EtbFdOpeningApplication {

	public static void main(String[] args) {
		SpringApplication.run(EtbFdOpeningApplication.class, args);
	}

}
