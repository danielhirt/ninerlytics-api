package com.group6.api.services;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.InfluxDBIOException;
import org.influxdb.dto.Pong;
import org.springframework.stereotype.Service;

/**
 * Service layer logic to perform initialization and status checks against
 * InfluxDB instance.
 * 
 * @author Daniel C. Hirt
 */

@Service
public class InfluxDBSetupService {

	private String databaseURL = "http://localhost:8086";
	private String databaseName = "connectedUsersWithBuildingDEV";
	private InfluxDB connection = InfluxDBFactory.connect(databaseURL, "admin", "admin");


	public boolean connectToInfluxDB() {

		if (this.testInfluxDBConnection(connection)) {

			if (!connection.databaseExists(databaseName)) {
				connection.createDatabase(databaseName);
				// connection.createRetentionPolicy("defaultPolicy", "totalConnectedUsers",
				// "30d", 1, true);
				this.setConnection(connection);

			}

		}
		this.setConnection(connection);
		return true;
	}
	

	public boolean testInfluxDBConnection(InfluxDB connection) {

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
