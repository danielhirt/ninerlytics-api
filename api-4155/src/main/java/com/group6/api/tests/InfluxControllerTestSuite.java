package com.group6.api.tests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.group6.api.models.UsersPoint;
import com.group6.api.services.InfluxDBSetupService;
import com.group6.api.services.InfluxQueryService;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/v1/test")
public class InfluxControllerTestSuite {

	@Autowired
	private InfluxDBSetupService influxDBSetupService;

	@Autowired
	private InfluxQueryService influxQueryService;


	
}
