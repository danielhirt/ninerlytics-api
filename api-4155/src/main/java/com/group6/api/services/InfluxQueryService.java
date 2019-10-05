package com.group6.api.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.group6.api.models.UsersPoint;


/**
 * Service layer logic to handle retrieval/manipulation of InfluxDB instance.
 * @author Daniel C. Hirt
 */
@Service
public class InfluxQueryService {
	
	@Autowired
	private InfluxDBSetupService influxDBSetupService;

	
	/*
	 * Executes a new query against InfluxDB to retrieve connection data specified by query
	 */
	public List<UsersPoint> processInfluxQuery(String query) {
		QueryResult queryResult = influxDBSetupService.getConnection().query(new Query(query, influxDBSetupService.getDatabaseName()));	
		InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
		List<UsersPoint> list = resultMapper.toPOJO(queryResult, UsersPoint.class);
		System.out.println("Size of list: " + list.size());
		
		DateFormat formatter = new SimpleDateFormat("yyyy MMM dd HH:mm");
		
		for (UsersPoint obj : list) {
			Date dateFormat = Date.from(obj.getTime());
			String formattedDate = formatter.format(dateFormat);
			obj.setDateAndTime(formattedDate);
	
		}
		
		return list;
		
	}
	
	/*
	 * Method to construct queries for use when generating CSVs of data from the front-end
	 */	
	public String queryConstructor(String dataSet) {
		
		String query = null;
		
		// parameter from front-end following format example "building-Atki"
		if (dataSet.contains("building")) { 
			
			dataSet = dataSet.substring(9, 12);
			query = "SELECT * FROM \"Connections\" WHERE \"Building\" =" + "\'" + dataSet +"\'";
			
		} else if (dataSet.equals("all")) {	
			
			query = "SELECT * FROM \"Connections\"";
		}
		
		return query;
	}
	
	
	
	/*
	 * DEPRECATED: WILL BREAK API DO NOT MODIFY OR UNCOMMENT
	 */
	
	/*
	public UsersPoint insertNewDataPoint(InfluxDB connection, String databaseName, UsersPoint newPoint) {

		if (usersPointList != null && newPoint != null) {
			UsersPoint lastDataPoint = usersPointList.get(usersPointList.size() - 1);
			UsersPoint newDataPoint = new UsersPoint();
			newDataPoint.setTime(newPoint.getTime());
			newDataPoint.setId(lastDataPoint.getId() + 1);
			newDataPoint.setConnections(newPoint.getConnections());
			newDataPoint.setDisconnections(newPoint.getDisconnections());
			
			BatchPoints batchPoints = BatchPoints
					.database(influxDBSetupService.getDatabaseName())
					.build();
			
			Point point = Point.measurement("users")
					.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
					.addField("id", lastDataPoint.getId() + 1)
			        .addField("Connections", newPoint.getConnections())
			        .addField("Disconnects/Roamed", newPoint.getDisconnections())
			        .build();
			
			  batchPoints.point(point);
		      connection.write(batchPoints);
		      System.out.println("Batchpoint Written: " + point.toString());
		      
		      return newDataPoint;
			
		} else {
			return null;
		}
		
	
	}
	
	*/


}
