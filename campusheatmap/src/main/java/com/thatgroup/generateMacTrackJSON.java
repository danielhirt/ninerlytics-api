package com.thatgroup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
//import org.springframework.stereotype.Service;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import com.google.gson.*;


/**
 * Service layer logic to handle parsing of data dumps for persistence in InfluxDB instance.
 * @author Matthew Walter, Daniel C. Hirt
 * 
 * @version DEPLOYMENT
 */

public class generateMacTrackJSON {
    public static void main(String[] args){
        generateMacTrackJSON run = new generateMacTrackJSON();
        run.orchestrator();
    }
    // Get all the files in any given folder
    public void orchestrator() {
        //To be passed in from API
        String startDate = "", endDate = "";
        String passedInMeasurement = null;

        //Parse date string into Dates
        Date start, end;
        start = Date.from(Instant.parse(startDate)); //"2019-10-10T15:17:01-04:00"));
        end = Date.from(Instant.parse(endDate)); //"2019-10-10T15:17:03-04:00"));

        System.out.println("Starting Execution for Dates " + startDate + " to " + endDate + ":");

        //Connect to DB
        InfluxDB db = InfluxDBFactory.connect("http://69.195.159.150:8086", "admin", "admin");

        //Init list of measurements
        ArrayList<String> measurements = new ArrayList<String>();

        //Check if getting all measurements, or just 1
        if(passedInMeasurement != null){
            measurements.add(passedInMeasurement);
        } else {
            measurements = getMeasurements(db);
        }

        //Init string of measurements for query
        StringBuilder multipleMeasurements = new StringBuilder();

        //Fill multiple measurements tring
        for(int i =0; i < measurements.size(); i++){
            if(i != (measurements.size() - 1)){
                multipleMeasurements = multipleMeasurements.append("\"" + measurements.get(i) + "\",");
            } else {
                multipleMeasurements = multipleMeasurements.append("\"" + measurements.get(i) + "\"");
            }
        }

        //Get final JSON object
        JsonObject jsonReturn = createFinalJSON(db, multipleMeasurements.toString(), start, end);

        System.out.println(jsonReturn); //This is what will be returned to the API
    }

    private ArrayList<String> getMeasurements(InfluxDB db){
        //Create query and query DB
        Query getMeasurements = new Query("SHOW MEASUREMENTS", "test-macDB");
        String queryReturn = db.query(getMeasurements).getResults().toString();

        //Clean return up
        queryReturn = queryReturn.replace("[Result [series=[Series [name=measurements, tags=null, columns=[name], values=[[", "");
        queryReturn = queryReturn.replace("]]]], error=null]]", "");
        queryReturn = queryReturn.replaceAll("\\]", "");
        queryReturn = queryReturn.replaceAll("\\[", "");

        //Split query return into array
        String[] queryReturnArray = queryReturn.split(",");
        ArrayList<String> measurements = new ArrayList<String>();

        //Iterate through return and add to measurements
        for(int i =0; i < queryReturnArray.length; i++){
            measurements.add(queryReturnArray[i].trim());
        }
        return measurements;
    }

    private JsonObject createFinalJSON(InfluxDB db, String measurement, Date startDate, Date endDate){
        //Init final JSON object
        JsonObject finalJson = new JsonObject();

        //Create query and then query DB
        String newQuery = "SELECT * FROM " + measurement + " WHERE time > \'" + startDate.toInstant() + "\' AND time < \'" + endDate.toInstant() + "\'";
        Query getMeasurement = new Query(newQuery, "test-macDB");
        List<QueryResult.Series> queryReturn = db.query(getMeasurement).getResults().get(0).getSeries();

        //Iterate through all returns to create each json element
        for(int i = 0; i < queryReturn.size(); i++){
            finalJson.add(queryReturn.get(i).getName(), createIndividualJSON(queryReturn.get(i), startDate, endDate));
        }
        return finalJson;
    }

    private JsonObject createIndividualJSON(QueryResult.Series queryReturnSeries, Date startDate, Date endDate){
        //Init return JSON object
        JsonObject returnJson = new JsonObject();

        //Get measurement from query return
        String measurement = queryReturnSeries.getName();

        //Clean and add query return to jsonelement
        String queryReturn = queryReturnSeries.toString();
        queryReturn = queryReturn.replace("Series [name=" + measurement + ", tags=null, columns=[time, Building, action], values=[", "");
        queryReturn = queryReturn.replace("]]", "");
        String[] queryReturnArray = queryReturn.split("],");
        for(int i = 0; i < queryReturnArray.length; i++){
            if(!queryReturnArray[i].contains("[Result [series=null, error=null]]")){
                returnJson.addProperty(queryReturnArray[i].split(",")[0].replace("[", "").replaceAll("\"", ""), queryReturnArray[i].split(",")[2].replace("]", "").trim() + " " + queryReturnArray[i].split(",")[1].trim());
            } else {
                returnJson = null;
            }
        }
        return returnJson;
    }
}