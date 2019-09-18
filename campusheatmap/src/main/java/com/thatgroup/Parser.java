package com.thatgroup;

import org.influxdb.*;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

public class Parser 
{
    public static void main( String[] args )
    {
        findFiles();
    }

    private static void findFiles(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please Enter Data Dump Folder Location:");

        String pathToFolder = scanner.nextLine();

        //Temp HardCode
        //String pathToFolder = "";

        File folder = new File(pathToFolder.toString());
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
              System.out.println("File " + listOfFiles[i].getName() + " Lenght of Files " + listOfFiles.length);
              generateHashMap(pathToFolder.toString() + "/" + listOfFiles[i].getName());
            }
        }
    }

    private static void generateHashMap(String pathToFile){
        try {
            Map<Date, ArrayList<String>> mapOfTimes = new HashMap<Date, ArrayList<String>>();

            BufferedReader br = new BufferedReader(new FileReader(pathToFile));
    
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            DateFormat format = new SimpleDateFormat("yyyy MMM dd HH:mm");
        
            while (line != null) {
                Date key = format.parse("2019 " + line.substring(0, 12));
                if(mapOfTimes.containsKey(key)){
                    mapOfTimes.get(key).add(line.substring(17));
                }
                else{
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
    
    private static void getConnectAndDisconnect(Map<Date, ArrayList<String>> mapOfTimes){
        int connect = 0;
        int connects = 0;
        int disconnect = 0;
        int disconnects = 0;
        int connected = 0;

        SortedSet<Date> keys = new TreeSet<Date>(mapOfTimes.keySet());

        Map<String, Boolean> userConnected = new HashMap<String, Boolean>();

        for(Date date : keys){
            for(String data: mapOfTimes.get(date)){
                Pattern pat = Pattern.compile("([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})");
                Matcher mat = pat.matcher(data);
                if(mat.find()){
                    if(!userConnected.containsKey(mat.group())){
                        if(data.contains("Assoc success")){
                            userConnected.put(mat.group(), true);
                            connect++;
                            connects++;
                        } else if (data.contains("Deauth")){
                            userConnected.put(mat.group(), false);
                        }
                    }
                    else{ 
                        if(data.contains("Assoc success") && (userConnected.get(mat.group())) == false){
                            connect++;
                            connects++;
                            userConnected.put(mat.group(), true);
                        } else if (data.contains("Deauth") && userConnected.get(mat.group())){
                            disconnect++;
                            disconnects++;
                            userConnected.put(mat.group(), false);
                        }
                    }
                }
            }
            connected += (connect - disconnect);
            connect = 0;
            disconnect = 0;
            System.out.println("There were " + connected + " at  " + date);
        }
    }

    private static void addToInflux(Date date, int connected){
        InfluxDB db = InfluxDBFactory.connect("http://localhost:8086");
        db.createDatabase("TempDataBase");

    }
}
