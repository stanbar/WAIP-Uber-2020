package com.ericsson.nrgsdk.examples.applications.whereami.models;

public class Ride {

	static public int RIDES_COUNTER = 0;
	public int number;
	public Client client;
	public Driver driver;
	public float price;
	public float distance;
	public boolean active = false;
	public boolean finished = false;

	public Ride(Client client) {
		this.client = client;
		this.number = Ride.RIDES_COUNTER;
		RIDES_COUNTER++;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + number;
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
		Ride other = (Ride) obj;
		if (number != other.number)
			return false;
		return true;
	}

}
