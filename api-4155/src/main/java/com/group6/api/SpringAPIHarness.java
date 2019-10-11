package com.group6.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.group6.api.services.InfluxDBSetupService;

@SpringBootApplication
public class SpringAPIHarness {

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(SpringAPIHarness.class, args);

		InfluxDBSetupService influxDBSetupService = new InfluxDBSetupService();
		
		if(influxDBSetupService.connectToInfluxDB()) {
			System.out.println("CONNECTION SUCCESS");
		} else {
			System.out.println("ERROR WITH CONNECTION");
		}

		
	
	}

}
