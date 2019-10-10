package com.group6.api.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.group6.api.Constants;


/**
 * Service layer logic to perform processing and structuring of data as needed.
 * 
 * @author Daniel C. Hirt
 */
@Service
public class DataProcessingService extends Constants {
	
	private Map<String, ArrayList<Double>> buildingCoordinateData = new HashMap<String, ArrayList<Double>>();

	public void processCoordinateData() {
		this.setBuildingCoordinateData(mapCoordinatesData(this.getBuildings()));			
	}

	private static Map<String, ArrayList<Double>> mapCoordinatesData(String[] buildings) {
		Map<String, ArrayList<Double>> coordinateMap = new HashMap<String, ArrayList<Double>>();

		for (String entry : buildings) {
			ArrayList<Double> coords = new ArrayList<Double>();
			// temporary just to map these buildings to begin visualizing heatmap data
			if (entry.equalsIgnoreCase("Atki") || entry.equalsIgnoreCase("Unio") || entry.equalsIgnoreCase("Wood")) {		
				switch (entry) {

				case "Atki":
					coords.add(35.305786);
					coords.add(-80.732111);
					break;
				case "Unio":
					coords.add(35.308915);
					coords.add(-80.733752);
					break;
				case "Wood":
					coords.add(35.307740);
					coords.add(-80.735344);
					break;
				}	
				coordinateMap.put(entry, coords);
			}
		}

		return coordinateMap;
	}

	public Map<String, ArrayList<Double>> getBuildingCoordinateData() {
		return buildingCoordinateData;
	}

	public void setBuildingCoordinateData(Map<String, ArrayList<Double>> buildingCoordinateData) {
		this.buildingCoordinateData = buildingCoordinateData;
	}

}
