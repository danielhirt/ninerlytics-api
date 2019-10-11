package com.group6.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.group6.api.services.InfluxDBSetupService;

// old state to revert back to
@SpringBootApplication
public class SpringAPIHarness {

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(SpringAPIHarness.class, args);


		InfluxDBSetupService influxDBSetupService = new InfluxDBSetupService();
		influxDBSetupService.connectToInfluxDB();
		

}
}
