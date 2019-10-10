package com.group6.api.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.InfluxDBIOException;
import org.influxdb.dto.Pong;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;
import org.springframework.stereotype.Service;

import com.group6.api.models.UsersPoint;

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
	private static final Logger logger = Logger.getLogger(InfluxDBSetupService.class.getName());
	private static final DateFormat formatter = new SimpleDateFormat("yyyy MMM dd HH:mm");
	
	/**
	 * Checks InfluxDB connection
	 * 
	 * @return true if connection is successful
	 */
	public boolean connectToInfluxDB() {

		if (this.testInfluxDBConnection(connection)) {

			if (!connection.databaseExists(databaseName)) {
				connection.createDatabase(databaseName);		
			}				
		}	
		return true;
	}
	

	public boolean testInfluxDBConnection(InfluxDB connection) {

		try {
			Pong response = connection.ping();

			if (response.getVersion().equalsIgnoreCase("unknown")) {
				logger.info("Error pinging server!" + "\n" + "Database version: " + response.getVersion());
				return false;

			} else {
				System.out.println("Connected successfully to InfluxDB!");
				System.out.println("Database version: " + response.getVersion() + "\n");
				return true;
			}

		} catch (InfluxDBIOException idbo) {
			logger.info("Exception while pinging database: " + idbo);
			return false;
		}

	}
	
	/**
	 * Method to process InfluxDB data queries, overriding REST controllers
	 * 
	 * @param query: the query to be executed against InfluxDB
	 * @return list of JSON objects from InfluxDB
	 */
	public List<UsersPoint> processDatabase(String query) {
		logger.info("Query recieved to be executed: " + query);
		
		QueryResult queryResult = connection.query(new Query(query, databaseName));	
		InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
		List<UsersPoint> list = resultMapper.toPOJO(queryResult, UsersPoint.class);	
		for (UsersPoint obj : list) {
			Date dateFormat = Date.from(obj.getTime());
			String formattedDate = formatter.format(dateFormat);
			obj.setDateAndTime(formattedDate);			
		}			
		return list;		
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
