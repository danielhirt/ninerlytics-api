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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
@CrossOrigin(origins = "http://192.168.0.1:80")
@RequestMapping("/api/v1")
public class InfluxController {

	@Autowired
	private InfluxDBSetupService influxDBSetupService;

	@Autowired
	private InfluxQueryService influxQueryService;

	@Autowired
	private FileService fileService;

	/*
	 * Return entire parsed dataset to the front-end.
	 */
	@GetMapping("/connections")
	private ResponseEntity<List<UsersPoint>> getListOfConnections() {

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
	 * Inserts a new data point from the front-end (this is a work in progress still
	 * to translate to Angular).
	 */
	@PostMapping("/addDataPoint")
	private ResponseEntity<UsersPoint> addNewDataPoint(@RequestBody UsersPoint newDataPoint) {
		UsersPoint addedDataPoint = influxQueryService.insertNewDataPoint(influxDBSetupService.getConnection(),
				influxDBSetupService.getDatabaseName(), newDataPoint);

		if (addedDataPoint == null) {
			return new ResponseEntity<UsersPoint>(addedDataPoint, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<UsersPoint>(addedDataPoint, HttpStatus.OK);
	}

	/*
	 * Tests connection to InfluxDB with a call from the front-end.
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

	/*
	 * WORK IN PROGRESS, WILL BREAK API DO NOT MODIFY
	 */
	@PostMapping("/generateCSV")
	private ResponseEntity<String> createCSVFromInfluxDB(@RequestBody String pathToCsv) throws IOException {

		if (influxQueryService.getUsersPointList().size() == 0) {
			return new ResponseEntity<String>("Unable to fetch data! Check database.", HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<String>(
				fileService.generateCSVFile(influxQueryService.getUsersPointList(), pathToCsv), HttpStatus.OK);
	}

	@GetMapping("/downloadCSV")
	private ResponseEntity<Boolean> exportCSV(HttpServletResponse response)
			throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {

		String filename = "users.csv";
		response.setContentType("text/csv");
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

		StatefulBeanToCsv<UsersPoint> writer = new StatefulBeanToCsvBuilder<UsersPoint>(response.getWriter())
				.withQuotechar(CSVWriter.NO_QUOTE_CHARACTER).withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withOrderedResults(false).build();
		if (fileService.getUsers() != null) {
			writer.write(fileService.getUsers());

		} else {
			return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

}
