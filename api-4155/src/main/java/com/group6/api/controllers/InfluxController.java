package com.group6.api.controllers;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.influxdb.InfluxDBIOException;
import org.influxdb.dto.Pong;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group6.api.models.UsersPoint;
import com.group6.api.services.FileService;
import com.group6.api.services.InfluxDBSetupService;
import com.group6.api.services.InfluxQueryService;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

/**
 * Business/HTTP layer logic to perform CRUD operations against InfluxDB
 * 
 * @author Daniel C. Hirt
 */
@RestController
@RequestMapping("/api/v1")
public class InfluxController {

	@Autowired
	private InfluxDBSetupService influxDBSetupService;

	@Autowired
	private InfluxQueryService influxQueryService;

	@Autowired
	private FileService fileService;
	
	/*
	 * Return connection data by building
	 */
	@GetMapping("/connectionsByBuilding/b={building}")
	private ResponseEntity<List<UsersPoint>> getConnectionDataByBuilding(@PathVariable String building) {
		
		String query = "SELECT * FROM \"Connections\" WHERE \"Building\" =" + "\'" + building +"\'";
		List<UsersPoint> data = influxQueryService.processInfluxQuery(query);
		
	    if (data == null) {
			return new ResponseEntity<List<UsersPoint>>(data, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<List<UsersPoint>>(data, HttpStatus.OK);		

	}
	

	/*
	 * Return entire parsed dataset to the front-end.
	 */
	@GetMapping("/connections")
	private ResponseEntity<List<UsersPoint>> getListOfConnections() {

		String query = "SELECT * FROM \"Connections\"";
		List<UsersPoint> data = influxQueryService.processInfluxQuery(query);

	    if (data == null) {
			return new ResponseEntity<List<UsersPoint>>(data, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<List<UsersPoint>>(data, HttpStatus.OK);
	}

	/*
	 * DEPRECATED: DO NOT MODIFY OR UNCOMMENT WILL BREAK API
	 * 
	 * Inserts a new data point from the front-end (this is a work in progress still
	 * to translate to Angular).
	 */
	
	/*
	@PostMapping("/addDataPoint")
	private ResponseEntity<UsersPoint> addNewDataPoint(@RequestBody UsersPoint newDataPoint) {
		UsersPoint addedDataPoint = influxQueryService.insertNewDataPoint(influxDBSetupService.getConnection(),
				influxDBSetupService.getDatabaseName(), newDataPoint);

		if (addedDataPoint == null) {
			return new ResponseEntity<UsersPoint>(addedDataPoint, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<UsersPoint>(addedDataPoint, HttpStatus.OK);
	}
	*/

	/*
	 * Tests connection to InfluxDB with a call from the front-end.
	 * 
	 */
	@GetMapping("/testDBConnection")
	private ResponseEntity<Boolean> testInfluxDBConnection() {
		boolean connected = true;
		try {
			Pong response = influxDBSetupService.getConnection().ping();
			if (response.getVersion().equalsIgnoreCase("unknown")) {
				connected = false;
			}
		} catch (InfluxDBIOException idbo) {
			System.out.println("Exception while pinging database from the front-end: " + idbo);
			return new ResponseEntity<Boolean>(connected, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<Boolean>(connected, HttpStatus.OK);
	}


	@GetMapping("/downloadCSV/{dataSet}")
	private ResponseEntity<Boolean> exportCSV(@PathVariable String dataSet, HttpServletResponse response)
			throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {

		String query = influxQueryService.queryConstructor(dataSet);
		List<UsersPoint> csvList = influxQueryService.processInfluxQuery(query);
		
		String filename = "users.csv";
		response.setContentType("text/csv");
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

		StatefulBeanToCsv<UsersPoint> writer = new StatefulBeanToCsvBuilder<UsersPoint>(response.getWriter())
				.withQuotechar(CSVWriter.NO_QUOTE_CHARACTER).withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withOrderedResults(false).build();
		if (fileService.getUsers(csvList) != null) {
			writer.write(fileService.getUsers(csvList));

		} else {
			return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

}
