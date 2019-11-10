package com.thatgroup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
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
        Date start, end;
        String passedInMeasurement = null;
        start = Date.from(Instant.parse("2019-10-10T15:17:01-04:00"));
        end = Date.from(Instant.parse("2019-10-10T15:17:03-04:00"));
        System.out.println("Starting Execution for Dates :");
        InfluxDB db = InfluxDBFactory.connect("http://69.195.159.150:8086", "admin", "admin");
        ArrayList<String> measurements = new ArrayList<String>();
        if(passedInMeasurement != null){
            measurements.add(passedInMeasurement);
        } else {
            measurements = getMeasurements(db);
        }
        JsonObject finalJson = new JsonObject();
        for(int i =0; i < measurements.size(); i++){
            finalJson.add(measurements.get(i), createJSON(db, measurements.get(i), start, end));
        }
        System.out.println(finalJson.toString());
    }

    private ArrayList<String> getMeasurements(InfluxDB db){
        Query getMeasurements = new Query("SHOW MEASUREMENTS", "test-macDB");
        String queryReturn = db.query(getMeasurements).getResults().toString();
        queryReturn = queryReturn.replace("[Result [series=[Series [name=measurements, tags=null, columns=[name], values=[[", "");
        queryReturn = queryReturn.replace("]]]], error=null]]", "");
        queryReturn = queryReturn.replaceAll("\\]", "");
        queryReturn = queryReturn.replaceAll("\\[", "");
        String[] queryReturnArray = queryReturn.split(",");
        ArrayList<String> measurements = new ArrayList<String>();
        for(int i =0; i < queryReturnArray.length; i++){
            measurements.add(queryReturnArray[i].trim());
        }
        return measurements;
    }

    private JsonObject createJSON(InfluxDB db, String measurement, Date startDate, Date endDate){
        JsonObject returnJson = new JsonObject();
        String newQuery = "SELECT * FROM \"" + measurement + "\" WHERE time > \'" + startDate.toInstant() + "\' AND time < \'" + endDate.toInstant() + "\'";
        Query getMeasurement = new Query(newQuery, "test-macDB");
        String queryReturn = db.query(getMeasurement).getResults().toString();
        queryReturn = queryReturn.replace("[Result [series=[Series [name=" + measurement + ", tags=null, columns=[time, Building, action], values=[", "");
        queryReturn = queryReturn.replace("]]], error=null]]", "");
        String[] queryReturnArray = queryReturn.split("],");
        for(int i = 0; i < queryReturnArray.length; i++){
            if(!queryReturnArray[i].contains("[Result [series=null, error=null]]")){
                returnJson.addProperty(queryReturnArray[i].split(",")[0].replace("[", "").replaceAll("\"", ""), queryReturnArray[i].split(",")[2].replace("]", "").trim() + " " + queryReturnArray[i].split(",")[1].trim());
            }
        }
        return returnJson;
    }
}