package com.group6.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group6.api.models.UsersPoint;
import com.group6.api.services.InfluxDBSetupService;
import com.group6.api.services.InfluxQueryService;


/**
 * Business/HTTP layer logic to perform CRUD operations against InfluxDB
 * @author Daniel C. Hirt
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/v1")
public class InfluxController {

	@Autowired
	private InfluxDBSetupService influxDBSetupService;

	@Autowired
	private InfluxQueryService influxQueryService;

	/*
	 * Return entire parsed dataset to the front-end.
	 */
	@GetMapping("/connections")
	public ResponseEntity<List<UsersPoint>> getListOfConnections() {

		String query = "SELECT * FROM \"users\"";
		List<UsersPoint> usersPointList = influxQueryService.getPoints(influxDBSetupService.getConnection(), query,
				influxDBSetupService.getDatabaseName());
		
		influxQueryService.setUsersPointList(usersPointList);

		if (usersPointList.size() == 0) {
			return new ResponseEntity<List<UsersPoint>>(usersPointList, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<List<UsersPoint>>(usersPointList, HttpStatus.OK);
	}
	
	/*
	 * Inserts a new data point from the front-end (this is a work in progress still to translate to Angular).
	 */
	@PostMapping("/addDataPoint")
	public ResponseEntity<UsersPoint> addNewDataPoint(@RequestBody UsersPoint newDataPoint) {
		UsersPoint addedDataPoint = influxQueryService.insertNewDataPoint(influxDBSetupService.getConnection(), 
				influxDBSetupService.getDatabaseName(), newDataPoint);
		
		if (addedDataPoint == null) {
			return new ResponseEntity<UsersPoint>(addedDataPoint, HttpStatus.BAD_REQUEST);
		}	
		return new ResponseEntity<UsersPoint>(addedDataPoint, HttpStatus.OK);
	}
	

	
	
}
