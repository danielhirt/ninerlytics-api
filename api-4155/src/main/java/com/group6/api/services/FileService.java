package com.group6.api.services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.group6.api.controllers.FileController;
import com.group6.api.models.UsersPoint;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * Service layer logic to perform file upload/download logic and processing. 
 * @author Daniel C. Hirt
 */
@Service
public class FileService {

	@Autowired
	private DataParserService dataParserService;
	
	@SuppressWarnings("unused")
	@Autowired
	private InfluxQueryService influxQueryService;

	private static final Logger logger = Logger.getLogger(FileController.class.getName());
	private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd-HH_mm_ss");
	private static final String uploadDirectory = "C:\\Users\\Daniel\\Desktop\\4155-Project\\uploads\\";
	private static final List<String> validFileTypes = Collections.unmodifiableList(Arrays.asList("txt"));
	private String saveDirectory = null;

	/*
	 * Logic to parse and add data to Influx recieved from the front-end as a Wifi
	 * log (still a work in progress)
	 */
	public String processFile(MultipartFile file, String parseFlag) throws IOException, Exception {
		String message = null;
		try {
			String originalName = this.logAndReadFileInformation(file, parseFlag);
			if (!validFileTypes.get(0).equals(originalName.substring(originalName.length() - 3))) {
				message = "Error. Please upload a valid file!";
				throw new RuntimeException(message);
			} else {
				LocalDateTime now = LocalDateTime.now();
				String filename = dtf.format(now) + "-" + originalName;
				saveDirectory = uploadDirectory + filename;
				File saveDestination = new File(saveDirectory);
				file.transferTo(saveDestination);

				if (parseFlag.equals("yes")) {
						dataParserService.generateHashMap(saveDirectory);		
				}
				message =  "Uploaded file: " + originalName;
				return message;
			}

		} catch (IOException e) {
			return "File processing error: " + e;
		}

	}
	
	/*
	 * WORK IN PROGRESS, WILL BREAK API DO NOT MODIFY
	 */
	public String generateCSVFile(List<UsersPoint> usersPointList, String pathToCsv) throws IOException {
		
		logger.info("User provided path: " + pathToCsv);
		String message = null;
		File directory = new File(pathToCsv);

		if (directory.exists()) {

			CSVWriter writer = new CSVWriter(new FileWriter(pathToCsv + "test.csv"));
			String[] columns = "Name,Time,Connections,Disconnections,ID".split(",");
			writer.writeNext(columns);
			
			List<String[]> rows = new LinkedList<String[]>();
			for (UsersPoint list: usersPointList) {
				rows.add(new String[]{"users",list.getTime().toString(),list.getBuilding().toString(),list.getConnections().toString(),
						list.getDisconnections().toString()});		
			}
			writer.writeAll(rows);
			writer.close();
			message = "Successfully generated CSV to: " + pathToCsv;
		} else {
			message = "Directory cannot be found!";
		}
		
		logger.info(message);
		return message;
	}
	
	
	/*
	 * Logic to generate a CSV file for use in Jupyter for Carson <3
	 */
	public List<UsersPoint> getUsers(List<UsersPoint> csvList) {
		List<UsersPoint> users = new ArrayList<>();
		for (UsersPoint list: csvList) {
			users.add(new UsersPoint(list.getTime(), list.getBuilding(), list.getConnections(), list.getDisconnections()));		
		}
		
		return users;
	}

	/*
	 * Log file information/upload to API
	 */
	private String logAndReadFileInformation(MultipartFile file, String parseFlag) throws IOException {

		InputStream inputStream = file.getInputStream();
		String originalName = file.getOriginalFilename();
		String contentType = file.getContentType();
		long size = file.getSize();
		logger.info("inputStream: " + inputStream);
		logger.info("originalName: " + originalName);
		logger.info("contentType: " + contentType);
		logger.info("size: " + size);
		logger.info("requestedParsing: " + parseFlag);

		return originalName;
	}

}
