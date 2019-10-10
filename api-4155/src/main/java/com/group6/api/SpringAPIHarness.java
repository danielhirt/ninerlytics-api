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

		System.out.println("");
		System.out.println(
				"*****************************************************************************************************");
		System.out.println(
				"****************************************4155 API v1.0.2-SPRT-1***************************************");
		System.out.println(
				"*****************************************************************************************************"
						+ "\n");

		Scanner scanner = new Scanner(System.in);
		boolean validInput = false;

		InfluxDBSetupService influxDBSetupService = new InfluxDBSetupService();

		if (influxDBSetupService.connectToInfluxDB()) {
			
			System.out.println("Would you like to add and parse new utilization data? (Y/N)");
			String input = scanner.nextLine();
			
			do {
				try {

					if (("y").equals(input.toLowerCase()) || ("yes").equals(input.toLowerCase())) {

						DataParserService parser = new DataParserService();
						parser.findFiles();
						validInput = true;
					}

				} catch (InputMismatchException e) {
					System.out.println("Please enter either Y or N:");
					input = scanner.nextLine();
				}

				finally {
					scanner.close();
					validInput = true;
					System.out.println("API initialized and listening on port 8080" + "\n"
							+ "Spring Framework Version: 2.1.8-RELEASE" + "\n" + "Java Version: 1.8");

				}

			} while (!validInput);

		} else {
			scanner.close();
			return;
		}

	}

}
