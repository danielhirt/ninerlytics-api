package com.group6.api;

import java.util.InputMismatchException;
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
		System.out.println("**********************************************************************************************");
		System.out.println("****************************************4155 API v0.0.1***************************************");
		System.out.println("**********************************************************************************************" + "\n");
		System.out.println("Connecting to InfluxDB..." + "\n");

		InfluxDBSetupService influxDBSetupService = new InfluxDBSetupService();

		if (influxDBSetupService.connectToInfluxDB()) {
			System.out.println(
					"Is there new data to parse? If not, select no and the API will launch to allow requests to InfluxDB.");
			System.out.println("(y/n)");
			
			boolean invalid = true;
			String response = scanner.nextLine();
			
			do {
				try {
					
					if (("y").equals(response.toLowerCase()) || ("yes").equals(response.toLowerCase())) {
						invalid = false;
						DataParserService dataParserService = new DataParserService();
						dataParserService.findFiles();
						

					} else {
						invalid = false;
						scanner.close();
						System.out.println("Cleaning and performing tests..." + "\n");
						Thread.sleep(5000);
						System.out.println("API initialized and listening on port 8080" + 
						"\n" + "Spring Framework Version: 2.1.8-RELEASE" 
							 + "\n" + "Java Version: 1.8");
					}
					
				} catch (InputMismatchException ex) {
					System.out.println("Please only enter y or n.");
					response = scanner.nextLine();
				}
			} while(invalid);
														
		} else {
			scanner.close();
			return;
		}

	}

}
