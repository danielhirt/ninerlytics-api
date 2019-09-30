package com.group6.api.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
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

	private List<UsersPoint> usersPointList = new ArrayList<UsersPoint>();
	
	/*
	 * Executes a new query against InfluxDB to retreive user connection data. 
	 */

	public List<UsersPoint> getPoints(InfluxDB connection, String query, String databaseName) {
	
		QueryResult queryResult = connection.query(new Query(query, databaseName));	
		InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
		List<UsersPoint> list = resultMapper.toPOJO(queryResult, UsersPoint.class);
		System.out.println("Size of user point list: " + usersPointList.size());
		
		this.setUsersPointList(list);

		return list;

	}

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

	public List<UsersPoint> getUsersPointList() {
		return usersPointList;
	}

	public void setUsersPointList(List<UsersPoint> usersPointList) {
		this.usersPointList = usersPointList;
	}

}
