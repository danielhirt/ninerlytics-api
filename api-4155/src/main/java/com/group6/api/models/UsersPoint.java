package com.group6.api.models;

import java.time.Instant;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import com.opencsv.bean.CsvBindByPosition;

/**
 * POJO/data model representing the "users" measurement persisted to InfluxDB instance. 
 * @author Daniel C. Hirt
 */
@Measurement(name = "connectionsByBuilding")
public class UsersPoint {

	@CsvBindByPosition(position = 0)
	@Column(name = "time")
	private Instant time;
	
	@CsvBindByPosition(position = 1)
    private String dateAndTime;
	
	@CsvBindByPosition(position = 2)
	@Column(name = "Building", tag = true)
	private String building;
	
    @CsvBindByPosition(position = 3)
	@Column(name = "Connected")
	private Integer connections;
	
    @CsvBindByPosition(position = 4)
	@Column(name = "Disconnected")
	private Integer disconnections;
    
    @CsvBindByPosition(position = 5)
    private double latitude;
    
    @CsvBindByPosition(position = 6)
    private double longitude;
    
    public UsersPoint() { }

	public UsersPoint(Instant time, String dateAndTime, String building, Integer connections, Integer disconnections, double latitude, double longitude) {
		this.time = time;
		this.dateAndTime = dateAndTime;
		this.building = building;
		this.connections = connections;
		this.disconnections = disconnections;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Instant getTime() {
		return time;
	}

	public void setTime(Instant time) {
		this.time = time;
	}

	public String getDateAndTime() {
		return dateAndTime;
	}

	public void setDateAndTime(String dateAndTime) {
		this.dateAndTime = dateAndTime;
	}

	public String getBuilding() {
		return building;
	}

	public void setBuilding(String building) {
		this.building = building;
	}

	public Integer getConnections() {
		return connections;
	}

	public void setConnections(Integer connections) {
		this.connections = connections;
	}

	public Integer getDisconnections() {
		return disconnections;
	}

	public void setDisconnections(Integer disconnections) {
		this.disconnections = disconnections;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((building == null) ? 0 : building.hashCode());
		result = prime * result + ((connections == null) ? 0 : connections.hashCode());
		result = prime * result + ((dateAndTime == null) ? 0 : dateAndTime.hashCode());
		result = prime * result + ((disconnections == null) ? 0 : disconnections.hashCode());
		long temp;
		temp = Double.doubleToLongBits(latitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UsersPoint other = (UsersPoint) obj;
		if (building == null) {
			if (other.building != null)
				return false;
		} else if (!building.equals(other.building))
			return false;
		if (connections == null) {
			if (other.connections != null)
				return false;
		} else if (!connections.equals(other.connections))
			return false;
		if (dateAndTime == null) {
			if (other.dateAndTime != null)
				return false;
		} else if (!dateAndTime.equals(other.dateAndTime))
			return false;
		if (disconnections == null) {
			if (other.disconnections != null)
				return false;
		} else if (!disconnections.equals(other.disconnections))
			return false;
		if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
			return false;
		if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UsersPoint [time=" + time + ", dateAndTime=" + dateAndTime + ", building=" + building + ", connections="
				+ connections + ", disconnections=" + disconnections + ", latitude=" + latitude + ", longitude="
				+ longitude + "]";
	}


	
	
}
