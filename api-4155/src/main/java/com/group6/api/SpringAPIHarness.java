package com.group6.api;

import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.group6.api.services.DataParserService;
import com.group6.api.services.InfluxDBSetupService;
import com.group6.api.services.ProfileService;

@SpringBootApplication
public class SpringAPIHarness {

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(SpringAPIHarness.class, args);
		
		ProfileService profileService = new ProfileService();
		DataParserService dataParserService = new DataParserService();
		InfluxDBSetupService influxDBSetupService = new InfluxDBSetupService();
		
		influxDBSetupService.connectToInfluxDB();
		profileService.initializeRoles();

		/*System.out.println("Would you like to parse new data?");

		Scanner input = new Scanner(System.in);
		String response = input.nextLine();

		if (("y").equals(response.toLowerCase())) {
			dataParserService.executeParser();
			input.close();
		} else {
			input.close();
			System.out.println("API initialized and listening on port 8080" + "\n"
					+ "Spring Framework Version: 2.1.8-RELEASE" + "\n" + "Java Version: 1.8");
		}*/

	}
}
