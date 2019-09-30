package com.group6.api.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * Service layer logic to handle parsing of data dumps for persistence in InfluxDB instance.
 * @author Matthew Walter, Daniel C. Hirt
 */
@Service
public class DataParserService extends Thread {
	
	@Autowired
	InfluxDBSetupService influxDBService;
	

    public void findFiles() {

        @SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);

        System.out.println("Please Enter Data Dump Folder Location:");
        String pathToFolder = scanner.nextLine();

        File folder = new File(pathToFolder.toString());
        File[] listOfFiles = folder.listFiles();
        
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
              System.out.println("File: " + listOfFiles[i].getName() + " Length of files: " + listOfFiles.length);
              generateHashMap(pathToFolder.toString() + "/" + listOfFiles[i].getName());
            }
        }
        
       
    }

  
    public void generateHashMap(String pathToFile) {
        
        try {
            Map<Date, ArrayList<String>> mapOfTimes = new HashMap<Date, ArrayList<String>>();

            BufferedReader br = new BufferedReader(new FileReader(pathToFile));
    
            String line = br.readLine();

            DateFormat format = new SimpleDateFormat("yyyy MMM dd HH:mm");
        
            while (line != null) {
                Date key = format.parse("2019 " + line.substring(0, 12));
                if (mapOfTimes.containsKey(key)) {
                    mapOfTimes.get(key).add(line.substring(17));
                }
                else {
                    ArrayList<String> temp = new ArrayList<String>();
                    temp.add(line.substring(17));
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
       
        int connect = 0;
        int disconnect = 0;
        int connected = 0;
        int disconnected = 0;
        int idNumber = 0;

        SortedSet<Date> keys = new TreeSet<Date>(mapOfTimes.keySet());
        Map<String, Boolean> userConnected = new HashMap<String, Boolean>();

        for (Date date : keys) {
            for (String data: mapOfTimes.get(date)) {
                Pattern pat = Pattern.compile("([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})");
                Matcher mat = pat.matcher(data);
                if (mat.find()) {
                    if (!userConnected.containsKey(mat.group())) {
                        if (data.contains("Assoc success")) {
                            userConnected.put(mat.group(), true);
                            connect++;
                        } else if (data.contains("Deauth")) {
                            userConnected.put(mat.group(), false);
                        }
                    }
                    else{ 
                        if(data.contains("Assoc success") && (userConnected.get(mat.group())) == false){
                            connect++;
                            userConnected.put(mat.group(), true);
                        } else if (data.contains("Deauth") && userConnected.get(mat.group())){
                            disconnect++;
                            userConnected.put(mat.group(), false);
                        }
                    }
                }
            }

            disconnected = disconnect;
            connected += (connect - disconnect);
            connect = 0;
            disconnect = 0;
            
            idNumber++;
            addToInflux(date, connected, disconnected, idNumber);
        }
    }

    private void addToInflux(Date date, int connected, int disconnected, int idNumber) {
    	
    	InfluxDB connection = InfluxDBFactory.connect("http://192.168.0.1:8086", "admin", "admin");
    	
        BatchPoints batchPoints = BatchPoints
            .database("devDB")
            .build();

        Point point = Point.measurement("users")
            .time(date.getTime(), TimeUnit.MILLISECONDS)
            .addField("id", idNumber)
            .addField("Connections", connected)
            .addField("Disconnects/Roamed", disconnected)
            .build();

        batchPoints.point(point);
        connection.write(batchPoints);
        System.out.println("Batchpoint Written: " + point.toString());
    }
}