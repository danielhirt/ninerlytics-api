package com.thatgroup;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;


/**
 * Service layer logic to handle parsing of data dumps for persistence in InfluxDB instance.
 * @author Matthew Walter, Daniel C. Hirt
 */
@Service
public class DataParserService extends Thread {
	
	@Autowired
	InfluxDBSetupService influxDBService;
    public static void main(String[] args) {
        findFiles();
    }

    // Get all the files in any given folder
    private static void findFiles() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please Enter Data Dump Folder Location:");

        String pathToFolder = scanner.nextLine();

        // Temp HardCode
        //String pathToFolder = "";

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

            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            DateFormat format = new SimpleDateFormat("yyyy MMM dd HH:mm");

            while (line != null) {
                Date key = format.parse("2019 " + line.substring(0, 12));
                if (mapOfTimes.containsKey(key)) {
                    mapOfTimes.get(key).add(line.substring(17));
                } else {
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
        SortedSet<Date> keys = new TreeSet<Date>(mapOfTimes.keySet());

        Map<Date, Map<String, ArrayList<String>>> macMap = new HashMap<Date, Map<String, ArrayList<String>>>();

        InfluxDB db = InfluxDBFactory.connect("http://69.195.159.150:8086", "admin", "admin");

        for (Date date : keys) {
            Map<String, ArrayList<String>> userConnected = new HashMap<String, ArrayList<String>>();
            for (String data : mapOfTimes.get(date)) {
                // Mac Address Pattern
                Pattern pat = Pattern.compile("([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})");
                // Check for Matches in data
                Matcher mat = pat.matcher(data);
                if (mat.find()) { // If there is a Mac Address Found
                    String mac = mat.group();
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
            macMap.put(date, userConnected);
        }
        mapBuildings(macMap, db);

    }

    private void mapBuildings(Map<Date, Map<String, ArrayList<String>>> macMap, InfluxDB db){
        String[] buildings = {"Atki", "Barn", "Bioi", "Came", "CoEd", "Colv", "Cone", "Duke", "EPIC", "Faci", "FOPS", "Foun", "Fret", "Gade", "Grig", "Kenn", "King", "Laur", "Levi", 
        "Lync", "Macy", "McMi", "Memo", "PORT", "Pros", "Robi", "Rowe", "Smit", "Stor", "StuU", "With", "Winn", "Wood", "HunH", "BelH", "CenC", "SVDH", "Tenn", "Harr", "RUP", "BandCor2", "StuH", 
        "Heal", "Unio", "Stu-A", "Coun"};

        Map<Date, ArrayList<String>> buildingsData = new HashMap<Date, ArrayList<String>>();

        try {
            for(Date date : macMap.keySet()){
                ArrayList<String> formattedDatas = new ArrayList<String>();
                for(String mac : macMap.get(date).keySet()){
                    ArrayList<String> datas = macMap.get(date).get(mac);
                    for(String data : datas){
                        String building = Arrays.stream(buildings).parallel().filter(data::contains).findAny().toString();
                        String buildName = building.substring(9, building.length() - 1);
                        if(buildName == "empt"){
                            buildName = "UNKNOWN";
                        }
                        if(data.contains("Assoc success")){
                            formattedDatas.add(mac + "-Joined-" + buildName);
                        }
                        else if (data.contains("Deauth")){
                            formattedDatas.add(mac + "-Left-" + buildName);
                        }
                    }
                }
                buildingsData.put(date, formattedDatas);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        //addConnectedToInflux(date, connected, db);
        putDataIntoInflux(buildingsData, db);
    }

    private void putDataIntoInflux(Map<Date, ArrayList<String>> buildingsData, InfluxDB db){
        String[] buildings = {"Atki", "Barn", "Bioi", "Came", "CoEd", "Colv", "Cone", "Duke", "EPIC", "Faci", "FOPS", "Foun", "Fret", "Gade", "Grig", "Kenn", "King", "Laur", "Levi", 
        "Lync", "Macy", "McMi", "Memo", "PORT", "Pros", "Robi", "Rowe", "Smit", "Stor", "StuU", "With", "Winn", "Wood", "HunH", "BelH", "CenC", "SVDH", "Tenn", "Harr", "RUP", "BandCor2", "StuH", 
        "Heal", "Unio", "Stu-A", "Coun"};

        BatchPoints batchPoints = BatchPoints
        .database("connectedUsersWithBuilding")
        .build();

        for(Date date : buildingsData.keySet()){
            String[] array = buildingsData.get(date).toArray(new String[0]);
            
            for(String building : buildings){
                int connects = 0;
                int disconnects = 0;
                String connectedString = "Joined-"+ building;
                String disconnectedString = "Left-"+ building;
                for(String data : array){
                    if(data.contains(disconnectedString)){
                        disconnects++;
                    }
                    else if (data.contains(connectedString)){
                        connects++;
                    }
                }
                int connected = connects - disconnects;
                batchPoints = createPoint(date, connects, building, db, batchPoints);
            }
            
        }
        uploadBatchpoints(db, batchPoints);
    }

    private static void uploadBatchpoints(InfluxDB db, BatchPoints batchPoints){
        if(!db.databaseExists("connectedUsersWithBuilding")){
            db.createDatabase("connectedUsersWithBuilding"); 
        }
        db.write(batchPoints);
    }

    private static BatchPoints createPoint(Date date, int connected, String building, InfluxDB db, BatchPoints batchPoints){
        Point point = Point.measurement("Connections")
            .time(date.getTime(), TimeUnit.MILLISECONDS)
            .tag("Building", building)
            .addField("Connected", connected)
            .build();

        batchPoints.point(point);
        return batchPoints;
    }
}
