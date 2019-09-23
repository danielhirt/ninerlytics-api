package com.group6.api;

import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.group6.api.services.DataParserService;
import com.group6.api.services.InfluxDBSetupService;

@SpringBootApplication
public class SpringAPIHarness {

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(SpringAPIHarness.class, args);

		Scanner scanner = new Scanner(System.in);
		System.out.println("");
		System.out.println("*****************************************");
		System.out.println("*************4155 API v0.0.1*************");
		System.out.println("*****************************************");
		System.out.println("");
		System.out.println("Connecting to InfluxDB...");
		Thread.sleep(5000);
		System.out.println("");
		InfluxDBSetupService influxDBSetupService = new InfluxDBSetupService();

		if (influxDBSetupService.connectToInfluxDB()) {
			System.out.println(
					"Is there new data to parse? If not, select no and the API will launch to allow requests to InfluxDB.");
			System.out.println("(Y/N)");

			String response = scanner.nextLine();

			if (("y").equals(response.toLowerCase()) || ("yes").equals(response.toLowerCase())) {

				DataParserService dataParserService = new DataParserService();
				dataParserService.findFiles();

			} else {
				scanner.close();
				System.out.println("API Ready.");
			}
			
		} else {
			scanner.close();
			return;
		}

	}

}
