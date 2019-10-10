package com.group6.api.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class DataWrapper {
	
	
	  @Id
	  @GeneratedValue(strategy=GenerationType.AUTO)
	  private Integer id;
	  
	  @Column
	  private String dateAndTime;
	  
	  @Column
	  private String building;
	  
	  @Column
	  private Integer connections;
	  
	  @Column
	  private Integer disconnections;
	  
	protected DataWrapper() {}  
	  

	public DataWrapper(String dateAndTime, String building, Integer connections, Integer disconnections) {
		this.dateAndTime = dateAndTime;
		this.building = building;
		this.connections = connections;
		this.disconnections = disconnections;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((building == null) ? 0 : building.hashCode());
		result = prime * result + ((connections == null) ? 0 : connections.hashCode());
		result = prime * result + ((dateAndTime == null) ? 0 : dateAndTime.hashCode());
		result = prime * result + ((disconnections == null) ? 0 : disconnections.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		DataWrapper other = (DataWrapper) obj;
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DataWrapper [id=" + id + ", dateAndTime=" + dateAndTime + ", building=" + building + ", connections="
				+ connections + ", disconnections=" + disconnections + "]";
	}
	  
	  
	  
	
	
	
}
