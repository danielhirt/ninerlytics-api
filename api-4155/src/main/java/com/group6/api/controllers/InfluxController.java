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

		List<UsersPoint> data = influxQueryService.queryConstructor(building);

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

		List<UsersPoint> data = influxQueryService.queryConstructor(new String("all"));

		if (data == null) {
			return new ResponseEntity<List<UsersPoint>>(data, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<List<UsersPoint>>(data, HttpStatus.OK);
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
	 * Generates a CSV for download based on a specified set of data recieved from
	 * the client
	 */
	@GetMapping("/downloadCSV/{dataSet}")
	private ResponseEntity<Boolean> exportCSV(@PathVariable String dataSet, HttpServletResponse response)
			throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {

		boolean downloadSuccess = false;
		List<UsersPoint> csvList = influxQueryService.queryConstructor(dataSet);
		String filename = "users.csv";
		response.setContentType("text/csv");
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

		StatefulBeanToCsv<UsersPoint> writer = new StatefulBeanToCsvBuilder<UsersPoint>(response.getWriter())
				.withQuotechar(CSVWriter.NO_QUOTE_CHARACTER).withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withOrderedResults(false).build();
		if (fileService.getUsers(csvList) != null) {
			writer.write(fileService.getUsers(csvList));
			downloadSuccess = true;
		} else {
			downloadSuccess = false;
			return new ResponseEntity<Boolean>(downloadSuccess, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<Boolean>(downloadSuccess, HttpStatus.OK);
	}

}
