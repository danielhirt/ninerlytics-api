package com.group6.api.models;

import java.time.Instant;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import com.opencsv.bean.CsvBindByPosition;

/**
 * POJO/data model representing the "users" measurement persisted to InfluxDB instance. 
 * @author Daniel C. Hirt
 */
@Measurement(name = "Connections")
public class UsersPoint {

	@CsvBindByPosition(position = 0)
	@Column(name = "time")
	private Instant time;
	
    @CsvBindByPosition(position = 1)
	@Column(name = "id")
	private Integer id;
	
    @CsvBindByPosition(position = 2)
	@Column(name = "Connected")
	private Integer connections;
	
    @CsvBindByPosition(position = 3)
	@Column(name = "Disconnects/Roamed")
	private Integer disconnections;
	

	public UsersPoint(Instant time, Integer id, Integer connections, Integer disconnections) {
		this.time = time;
		this.id = id;
		this.connections = connections;
		this.disconnections = disconnections;
	}

	public UsersPoint() {}
	
	public Instant getTime() {
		return time;
	}

	public void setTime(Instant time) {
		this.time = time;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((connections == null) ? 0 : connections.hashCode());
		result = prime * result + ((disconnections == null) ? 0 : disconnections.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
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
		return "UsersPoint [time=" + time + ", id=" + id + ", connections=" + connections + ", disconnections="
				+ disconnections + "]";
	}

	
}
