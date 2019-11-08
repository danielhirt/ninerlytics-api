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


/**
 * Service layer logic to handle parsing of data dumps for persistence in InfluxDB instance.
 * @author Matthew Walter, Daniel C. Hirt
 * 
 * @version DEPLOYMENT
 */

public class Parser {
    public static void main(String[] args){
        Parser run = new Parser();
        run.findFiles();
    }
    // Get all the files in any given folder
    public void findFiles() {
    	
        @SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
        System.out.println("Please Enter Data Dump Folder Location:");

        String pathToFolder = scanner.nextLine();


        File folder = new File(pathToFolder.toString());
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.println("File " + listOfFiles[i].getName() + " Length of Files " + listOfFiles.length);
                generateHashMap(pathToFolder.toString() + "/" + listOfFiles[i].getName());
            }
            
        }
        
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

        InfluxDB db = InfluxDBFactory.connect("http://69.195.159.150:8086", "admin", "admin");

        String[] sysIPs = {"10.47.128.140", "10.47.0.22", "10.47.0.23", "10.47.0.32", "10.47.0.33"};

        for (Date date : keys) {
            Map<String, ArrayList<String>> userConnected = new HashMap<String, ArrayList<String>>();
            for (String data : mapOfTimes.get(date)) {
                if(Arrays.stream(sysIPs).parallel().filter(data::contains).count() == 0){
                    // Mac Address Pattern
                    Pattern pat = Pattern.compile(": .{40}:");
                    // Check for Matches in data
                    Matcher mat = pat.matcher(data);
                    if (mat.find()) { // If there is a Mac Address Found
                        String mac = mat.group().substring(1, 42).trim();
                        if(userConnected.keySet().contains(mac)){
                            ArrayList<String> str = userConnected.get(mac);
                            str.add(data);
                            userConnected.put(mac, str);
                        }
                        else {
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

    private void mapBuildings(Map<Date, Map<String, ArrayList<String>>> macMap, InfluxDB db){
        String[] buildings = {"Atki", "Barn", "Bioi", "Came", "CoEd", "Colv", "Cone", "Duke", "EPIC", "Faci", "FOPS", "Foun", "Fret", "Gade", "Grig", "Kenn", "King", "Laur", "Levi", 
        "Lync", "Macy", "McMi", "Memo", "PORT", "Pros", "Robi", "Rowe", "Smit", "Stor", "StuU", "With", "Winn", "Wood", "HunH", "BelH", "CenC", "SVDH", "Tenn", "Harr", "RUP", "BandCor2", "StuH", 
        "Heal", "Unio", "Stu-A", "Coun", "Irwi", "BelG", "McEn", "Frid", "Denn", "StuA", "Rece", "Rees", "Cato", "NCRC", "McCo", "Mart", "Auxi", "Gari", "Pros", "Cafe", "Burs"};
        
        Map<Date, ArrayList<String>> buildingsData = new HashMap<Date, ArrayList<String>>();
        try {
            for(Date date : macMap.keySet()){
                ArrayList<String> formattedDatas = new ArrayList<String>();
                for(String mac : macMap.get(date).keySet()){
                    ArrayList<String> datas = macMap.get(date).get(mac);
                    for(String data : datas){
                        String building = Arrays.stream(buildings).parallel().filter(data::contains).findAny().toString();
                        String buildName = building.substring(9, building.length() - 1);
                        if(data.contains("Assoc success")){
                            formattedDatas.add(mac + "+" + buildName);
                        }
                        else if (data.contains("Deauth")){
                            formattedDatas.add(mac + "-" + buildName);
                        }
                    }
                }
                buildingsData.put(date, formattedDatas);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        // DEPRECATED: addConnectedToInflux(date, connected, db);
        putDataIntoInflux(buildingsData, db);
    }

    private void putDataIntoInflux(Map<Date, ArrayList<String>> buildingsData, InfluxDB db){
        String[] buildings = {"Atki", "Barn", "Bioi", "Came", "CoEd", "Colv", "Cone", "Duke", "EPIC", "Faci", "FOPS", "Foun", "Fret", "Gade", "Grig", "Kenn", "King", "Laur", "Levi", 
        "Lync", "Macy", "McMi", "Memo", "PORT", "Pros", "Robi", "Rowe", "Smit", "Stor", "StuU", "With", "Winn", "Wood", "HunH", "BelH", "CenC", "SVDH", "Tenn", "Harr", "RUP", "BandCor2", "StuH", 
        "Heal", "Unio", "Stu-A", "Coun", "Irwi", "BelG", "McEn", "Frid", "Denn", "StuA", "Rece", "Rees", "Cato", "NCRC", "McCo", "Mart", "Auxi", "Gari", "Pros", "Cafe", "Burs"};
        

        BatchPoints aggregateBatchPoints = BatchPoints
        .database("test-connDB")
        .build();

        BatchPoints macBatchPoints = BatchPoints
        .database("test-macDB")
        .build();
        
        for(Date date : buildingsData.keySet()){
            String[] array = buildingsData.get(date).toArray(new String[0]);
            
            for(String building : buildings){
                int connects = 0;
                int disconnects = 0;
                String connectedString = "+"+ building;
                String disconnectedString = "-"+ building;
                for(String data : array){
                    if(data.contains(disconnectedString)){
                        disconnects++;
                    }
                    else if (data.contains(connectedString)){
                        connects++;
                    }
                    macBatchPoints = createMacPoints(date, data, building, db, macBatchPoints);
                }
                // DEPRECATED: int connected = connects - disconnects;
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
        Point point = Point.measurement(data.substring(0, 40))
        .time(date.getTime(), TimeUnit.MILLISECONDS)
        .tag("Building", building)
        .addField("action", action)
        .build();

        batchPoints.point(point);

        return batchPoints;
    }

    private static void uploadBatchpoints(InfluxDB db, BatchPoints batchPoints){
        if(!db.databaseExists(batchPoints.getDatabase())){
            db.createDatabase(batchPoints.getDatabase()); 
        }
        db.write(batchPoints);
        
        System.out.println("PARSE SUCCESS!" + "\n");
        System.out.println("API initialized and listening on port 8080" + 
				"\n" + "Spring Framework Version: 2.1.8-RELEASE" 
					 + "\n" + "Java Version: 1.8");
        
    }

    private static BatchPoints createAggregatePoint(Date date, int connected, int disconnected, String building, InfluxDB db, BatchPoints batchPoints){
        Point point = Point.measurement("connectionsByBuilding")
            .time(date.getTime(), TimeUnit.MILLISECONDS)
            .tag("Building", building)
            .addField("Connected", connected)
            .addField("Disconnected", disconnected)
            .build();

        batchPoints.point(point);
        //System.out.println("Batchpoint Written: " + point.toString());
        return batchPoints;
    }
}