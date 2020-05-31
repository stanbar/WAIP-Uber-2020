package com.ericsson.nrgsdk.examples.applications.whereami.models;

import com.ericsson.nrgsdk.examples.applications.whereami.LocationProcessor;

public class Client {
	public String number;
	public String destination;
	private LocationProcessor locationProcessor;
	
	public Client(String number, String name, LocationProcessor locationProcessor){
		this.number = number;
		this.locationProcessor = locationProcessor;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((number == null) ? 0 : number.hashCode());
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
		Client other = (Client) obj;
		if (number == null) {
			if (other.number != null)
				return false;
		} else if (!number.equals(other.number))
			return false;
		return true;
	}	
}
