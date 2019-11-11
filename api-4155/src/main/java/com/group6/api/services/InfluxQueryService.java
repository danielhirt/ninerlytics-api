package com.group6.api.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.group6.api.Constants;
import com.group6.api.controllers.InfluxController;
import com.group6.api.models.UsersPoint;

/**
 * Service layer logic to handle retrieval/manipulation of InfluxDB instance.
 * 
 * @author Daniel C. Hirt
 * 
 * @extends Constants: Class holding standardized data and constant data
 *          constraints
 */
@Service
public class InfluxQueryService extends Constants {

	@Autowired
	private InfluxDBSetupService influxDBSetupService;

	@Autowired
	private DataProcessingService dataProcessingService;

	private static final Logger logger = Logger.getLogger(InfluxController.class.getName());
	private static final DateFormat formatter = new SimpleDateFormat("yyyy MMM dd HH:mm");

	/**
	 * Executes a new query against InfluxDB to retrieve connection data specified
	 * by query string
	 * 
	 * @param query: The query string to be executed against InfluxDB
	 * @return list: List of JSON objects containing Wifi Utilization metrics
	 */
	private List<UsersPoint> processInfluxQuery(String query) {
		logger.info("Query recieved from queryConstructor " + query);

		QueryResult queryResult = influxDBSetupService.getConnection()
				.query(new Query(query, influxDBSetupService.getDatabaseName()));
		InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
		List<UsersPoint> list = resultMapper.toPOJO(queryResult, UsersPoint.class);

		dataProcessingService.processCoordinateData();
		Set<Entry<String, ArrayList<Double>>> set = dataProcessingService.getBuildingCoordinateData().entrySet();

		for (UsersPoint obj : list) {

			Date dateFormat = Date.from(obj.getTime());
			String formattedDate = formatter.format(dateFormat);
			obj.setDateAndTime(formattedDate);
			Iterator<Entry<String, ArrayList<Double>>> itr = set.iterator();
			
			while (itr.hasNext()) {
				Map.Entry<String, ArrayList<Double>> entry = itr.next();

				if (entry.getKey().equals(obj.getBuilding())) {

					obj.setLatitude(entry.getValue().get(0));
					obj.setLongitude(entry.getValue().get(1));
				}
			}
		}
		logger.info("Size of processed query to be returned: " + list.size());
		return list;
	}

	/**
	 * Constructs a query to be used against InfluxDB, based on paramaters recieved
	 * from REST Controller
	 * 
	 * @param tag: Value recieved from client determining what type of query to
	 *             execute.
	 * @return Returns the result of processInfluxQuery(String query)
	 */
	public List<UsersPoint> queryConstructor(String tag) {
		logger.info("Query paramater passed to queryConstructor " + tag);
		String query = new String();

		if (tag.equals("all")) {
			query = "SELECT * FROM \"connectionsByBuilding\"";
		} else {

			for (String index : this.getBuildings()) {
				if (tag.toLowerCase().equals(index.toLowerCase())) {
					query = "SELECT * FROM \"connectionsByBuilding\" WHERE \"Building\" =" + "\'" + tag + "\'";
				}
			}
		}
		return processInfluxQuery(query);
	}

}
