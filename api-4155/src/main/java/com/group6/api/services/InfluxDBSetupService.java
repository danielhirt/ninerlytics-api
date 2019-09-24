package com.group6.api.services;

import java.util.Scanner;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.InfluxDBIOException;
import org.influxdb.dto.Pong;
import org.springframework.stereotype.Service;

/**
 * Service layer logic to perform initialization and status checks against InfluxDB instance. 
 * @author Daniel C. Hirt
 */
@Service
public class InfluxDBSetupService {

	private String databaseURL = "http://localhost:8086";
	private String databaseName = "devDB";
	private InfluxDB connection = InfluxDBFactory.connect(databaseURL);

	public boolean connectToInfluxDB() {

		if (testInfluxDBConnection(connection)) {

			if (!connection.databaseExists(databaseName)) {
				connection.createDatabase(databaseName);
				connection.createRetentionPolicy("defaultPolicy", "devDB", "30d", 1, true);
				this.setConnection(connection);
				return true;
			} else if (connection.databaseExists(databaseName)) {
				@SuppressWarnings("resource")
				Scanner scanner = new Scanner(System.in);
				System.out.println("A database already exists: " + databaseName + "\n" + "Would you like to overwrite it? (y/n):");
				String response = scanner.nextLine();
				
				if (("y").equals(response.toLowerCase()) || ("yes").equals(response.toLowerCase())) {
					connection.deleteDatabase(databaseName);
					connection.createDatabase(databaseName);
					connection.createRetentionPolicy("defaultPolicy", "devDB", "30d", 1, true);
					this.setConnection(connection);
					
			
				} else {
					return true;
				}
				
			}

		} else if (!testInfluxDBConnection(connection)) {
			System.out.println("\n" + "Please verify the InfluxDB server is running and try again.");
			return false;
		}
		
		return true;

	}

	private static boolean testInfluxDBConnection(InfluxDB connection) {

		try {
			Pong response = connection.ping();

			if (response.getVersion().equalsIgnoreCase("unknown")) {
				System.out.println("Error pinging server!" + "\n" + "Database version: " + response.getVersion());
				return false;

			} else {
				System.out.println("Connected successfully to InfluxDB!");
				System.out.println("Database version: " + response.getVersion() + "\n");
				return true;
			}

		} catch (InfluxDBIOException idbo) {
			System.out.println("Exception while pinging database: " + idbo);
			return false;
		}

	}


	public InfluxDB getConnection() {
		return connection;
	}

	public void setConnection(InfluxDB connection) {
		this.connection = connection;
	}

	public String getDatabaseURL() {
		return databaseURL;
	}

	public void setDatabaseURL(String databaseURL) {
		this.databaseURL = databaseURL;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}


	
}
