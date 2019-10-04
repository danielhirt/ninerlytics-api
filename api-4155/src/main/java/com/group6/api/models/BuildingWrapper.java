package com.group6.api.models;

import java.time.Instant;
import java.util.List;

import org.influxdb.annotation.Column;

public class BuildingWrapper {
	
	@Column(name = "buildingName")
	private String buildingName;
	
	@Column(name = "time")
	private List<Instant> time;
	
	@Column(name = "connections")
	private List<Integer> connections;
	
	@Column(name = "disconnections")
	private List<Integer> disconnections;

	public String getBuildingName() {
		return buildingName;
	}

	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}

	public List<Instant> getTime() {
		return time;
	}

	public void setTime(List<Instant> time) {
		this.time = time;
	}

	public List<Integer> getConnections() {
		return connections;
	}

	public void setConnections(List<Integer> connections) {
		this.connections = connections;
	}

	public List<Integer> getDisconnections() {
		return disconnections;
	}

	public void setDisconnections(List<Integer> disconnections) {
		this.disconnections = disconnections;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((buildingName == null) ? 0 : buildingName.hashCode());
		result = prime * result + ((connections == null) ? 0 : connections.hashCode());
		result = prime * result + ((disconnections == null) ? 0 : disconnections.hashCode());
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
		BuildingWrapper other = (BuildingWrapper) obj;
		if (buildingName == null) {
			if (other.buildingName != null)
				return false;
		} else if (!buildingName.equals(other.buildingName))
			return false;
		if (connections == null) {
			if (other.connections != null)
				return false;
		} else if (!connections.equals(other.connections))
			return false;
		if (disconnections == null) {
			if (other.disconnections != null)
				return false;
		} else if (!disconnections.equals(other.disconnections))
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
		return "BuildingWrapper [buildingName=" + buildingName + ", time=" + time + ", connections=" + connections
				+ ", disconnections=" + disconnections + "]";
	}

	
	
}
