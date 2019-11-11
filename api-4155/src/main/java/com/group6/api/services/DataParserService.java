package com.group6.api.services;

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
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.springframework.stereotype.Service;

import com.group6.api.Constants;


/**
 * Service layer logic to handle parsing of data dumps for persistence in InfluxDB instance.
 * @author Matthew Walter, Daniel C. Hirt
 * 
 * @version DEVELOPMENT
 */
@Service
public class DataParserService extends Constants {
	
	private static final Logger logger = Logger.getLogger(DataParserService.class.getName());
	
    // Get all the files in any given folder
    public void executeParser() { 	
        @SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
        System.out.println("Please Enter Data Dump Folder Location:");

        String pathToFolder = scanner.nextLine();
        File folder = new File(pathToFolder.toString());
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; ++i) {
            if (listOfFiles[i].isFile()) {
                logger.info("File: " + listOfFiles[i].getName() + "Amount of files: " + listOfFiles.length);
                generateHashMap(pathToFolder.toString() + "/" + listOfFiles[i].getName());
            }        
        }
        
        logger.info("PARSE COMPLETE");
    }

    public void generateHashMap(String pathToFile) { 	
        try {
            Map<Date, ArrayList<String>> mapOfTimes = new HashMap<Date, ArrayList<String>>();

            BufferedReader br = new BufferedReader(new FileReader(pathToFile));

            String line = br.readLine();
            while (line != null) {
                Date key = Date.from(Instant.parse(line.subSequence(0, 25)));
                if (mapOfTimes.containsKey(key)) {
                    mapOfTimes.get(key).add(line.substring(17));
                } else {
                    ArrayList<String> temp = new ArrayList<String>();
                    temp.add(line.substring(26));
                    mapOfTimes.put(key, temp);
                }
                line = br.readLine();
            }
            br.close();
            getConnectAndDisconnect(mapOfTimes);
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    private void getConnectAndDisconnect(Map<Date, ArrayList<String>> mapOfTimes) {
        SortedSet<Date> keys = new TreeSet<Date>(mapOfTimes.keySet());
        
        Map<Date, Map<String, ArrayList<String>>> macMap = new HashMap<Date, Map<String, ArrayList<String>>>();
        
        InfluxDB db = InfluxDBFactory.connect("http://localhost:8086", "admin", "admin");
        
        String[] sysIPs = this.getSysIPs();

        for (Date date : keys) {
            Map<String, ArrayList<String>> userConnected = new HashMap<String, ArrayList<String>>();
            for (String data : mapOfTimes.get(date)) {        	
                if (Arrays.stream(sysIPs).parallel().filter(data::contains).count() == 0) {              
                    Pattern pat = Pattern.compile(": .{40}:"); // Mac Address Pattern                    
                    Matcher mat = pat.matcher(data); // Check for Matches in data
                 
                    if (mat.find()) { // If there is a Mac Address Found
                        String mac = mat.group().substring(1, 42);
                        if (userConnected.keySet().contains(mac)) {
                            ArrayList<String> str = userConnected.get(mac);
                            str.add(data);
                            userConnected.put(mac, str);
                        } else {
                            ArrayList<String> str = new ArrayList<String>();
                            str.add(data);
                            userConnected.put(mac, str);
                        }
                    }
                }
            }
            macMap.put(date, userConnected);
        }
        mapBuildings(macMap, db);
    }

    private void mapBuildings(Map<Date, Map<String, ArrayList<String>>> macMap, InfluxDB db) {    	
        String[] buildings = this.getBuildings();
        
        Map<Date, ArrayList<String>> buildingsData = new HashMap<Date, ArrayList<String>>();
        try {
            for(Date date : macMap.keySet()){
                ArrayList<String> formattedDatas = new ArrayList<String>();
                for(String mac : macMap.get(date).keySet()){
                    ArrayList<String> list = macMap.get(date).get(mac);
                    for(String data : list){
                        String building = Arrays.stream(buildings).parallel().filter(data::contains).findAny().toString();
                        String buildName = building.substring(9, building.length() - 1);
                        
                        if (data.contains("Assoc success")) {
                            formattedDatas.add(mac + "+" + buildName);
                        } else if (data.contains("Deauth")) {
                            formattedDatas.add(mac + "-" + buildName);
                        }
                    }
                }
                buildingsData.put(date, formattedDatas);
            }           
        } catch (Exception e) {
            e.printStackTrace();
        }
        putDataIntoInflux(buildingsData, db);
    }

    private void putDataIntoInflux(Map<Date, ArrayList<String>> buildingsData, InfluxDB db) {  	
        String[] buildings = this.getBuildings();

        BatchPoints aggregateBatchPoints = BatchPoints
        .database("developmentDBConnections")
        .build();

        BatchPoints macBatchPoints = BatchPoints
        .database("developmentDBMacs")
        .build();
        
        for(Date date : buildingsData.keySet()) {
            String[] array = buildingsData.get(date).toArray(new String[0]);
            
            for(String building : buildings) {
                int connects = 0;
                int disconnects = 0;
                String connectedString = "+"+ building;
                String disconnectedString = "-"+ building;
                for(String data : array){
                	
                    if (data.contains(disconnectedString)) {
                        disconnects++;
                    } else if (data.contains(connectedString)) {
                        connects++;
                    }
                    macBatchPoints = createMacPoints(date, data, building, db, macBatchPoints);
                }
                aggregateBatchPoints = createAggregatePoint(date, connects, disconnects, building, db, aggregateBatchPoints);
            }          
        }
        uploadBatchpoints(db, aggregateBatchPoints);
        uploadBatchpoints(db, macBatchPoints);
    }

    private static BatchPoints createMacPoints(Date date, String data, String building, InfluxDB db, BatchPoints batchPoints){
        String action = "";
        if(data.substring(41, 42).equals("+")){
            action = "Joined";
        } else {
            action = "Left";
        }
        Point point = Point.measurement(data.substring(0, 41))
        .time(date.getTime(), TimeUnit.MILLISECONDS)
        .tag("Building", building)
        .addField("action", action)
        .build();

        batchPoints.point(point);

        return batchPoints;
    }

    private static void uploadBatchpoints(InfluxDB db, BatchPoints batchPoints) {   	
        if (!db.databaseExists(batchPoints.getDatabase())) {
            db.createDatabase(batchPoints.getDatabase()); 
        }      
        db.write(batchPoints);   
        logger.info("PARSE SUCCESS!");     
    }

    private static BatchPoints createAggregatePoint(Date date, int connected, int disconnected, String building, InfluxDB db, BatchPoints batchPoints) {
        Point point = Point.measurement("connectionsByBuilding")
            .time(date.getTime(), TimeUnit.MILLISECONDS)
            .tag("Building", building)
            .addField("Connected", connected)
            .addField("Disconnected", disconnected)
            .build();

        batchPoints.point(point);
        logger.info("Batchpoint Written: " + point.toString());
        return batchPoints;
    }
}