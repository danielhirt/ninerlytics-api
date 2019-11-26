package com.group6.api.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;

@Service
public class MacTrackingService {

	@Autowired
	private InfluxDBSetupService influxDBSetupService;

	private static final Logger logger = Logger.getLogger(MacTrackingService.class.getName());

	public String generateMacAddressJSON(String startDate, String endDate) {
		//InfluxDB db = influxDBSetupService.getConnection();
		
		InfluxDB db = InfluxDBFactory.connect("http://69.195.159.150:8086/", "admin", "admin");
	
		ArrayList<String> measurements = new ArrayList<String>();
		StringBuilder multipleMeasurements = new StringBuilder(); // Init string of measurements for query

		Date start = Date.from(Instant.parse(startDate)); // "2019-10-10T15:17:01-04:00"));
		Date end = Date.from(Instant.parse(endDate)); // "2019-10-10T15:17:03-04:00"));

		logger.info("Starting execution for " + startDate + " to " + endDate + ":");
		measurements = getMeasurements(db);

		// Fill multiple measurements string
		for (int i = 0; i < measurements.size(); i++) {
			if (i != (measurements.size() - 1)) {
				multipleMeasurements = multipleMeasurements.append("\"" + measurements.get(i) + "\",");
			} else {
				multipleMeasurements = multipleMeasurements.append("\"" + measurements.get(i) + "\"");
			}
		}

		return createFinalJSON(db, multipleMeasurements.toString(), start, end).toString();

	}

	private ArrayList<String> getMeasurements(InfluxDB db) {
		// Create query and query DB
		Query getMeasurements = new Query("SHOW MEASUREMENTS", "test-macDB");
		String queryReturn = db.query(getMeasurements).getResults().toString();

		// Clean return up
		queryReturn = queryReturn
				.replace("[Result [series=[Series [name=measurements, tags=null, columns=[name], values=[[", "");
		queryReturn = queryReturn.replace("]]]], error=null]]", "");
		queryReturn = queryReturn.replaceAll("\\]", "");
		queryReturn = queryReturn.replaceAll("\\[", "");

		// Split query return into array
		String[] queryReturnArray = queryReturn.split(",");
		ArrayList<String> measurements = new ArrayList<String>();

		// Iterate through return and add to measurements
		for (int i = 0; i < queryReturnArray.length; i++) {
			measurements.add(queryReturnArray[i].trim());
		}
		return measurements;
	}

	private JsonObject createFinalJSON(InfluxDB db, String measurement, Date startDate, Date endDate) {
		// Init final JSON object
		JsonObject finalJSON = new JsonObject();

		// Create query and then query DB
		String newQuery = "SELECT * FROM " + measurement + " WHERE time > \'" + startDate.toInstant()
				+ "\' AND time < \'" + endDate.toInstant() + "\'";
		Query getMeasurement = new Query(newQuery, "test-macDB");
		List<QueryResult.Series> queryReturn = db.query(getMeasurement).getResults().get(0).getSeries();
	
		// Iterate through all returns to create each json element
		for (int i = 0; i < queryReturn.size(); i++) { // NULL HERE
			finalJSON.add(queryReturn.get(i).getName(), createIndividualJSON(queryReturn.get(i), startDate, endDate));
		}
		return finalJSON;
	}

	private JsonObject createIndividualJSON(QueryResult.Series queryReturnSeries, Date startDate, Date endDate) {
		// Init return JSON object
		JsonObject individualJSON = new JsonObject();

		// Get measurement from query return
		String measurement = queryReturnSeries.getName();

		// Clean and add query return to jsonelement
		String queryReturn = queryReturnSeries.toString();
		queryReturn = queryReturn
				.replace("Series [name=" + measurement + ", tags=null, columns=[time, Building, action], values=[", "");
		queryReturn = queryReturn.replace("]]", "");
		String[] queryReturnArray = queryReturn.split("],");
		for (int i = 0; i < queryReturnArray.length; i++) {
			if (!queryReturnArray[i].contains("[Result [series=null, error=null]]")) {
				individualJSON.addProperty(queryReturnArray[i].split(",")[0].replace("[", "").replaceAll("\"", ""),
						queryReturnArray[i].split(",")[2].replace("]", "").trim() + " "
								+ queryReturnArray[i].split(",")[1].trim());
			} else {
				individualJSON = null;
			}
		}
		return individualJSON;
	}
	

}
