package com.ericsson.nrgsdk.examples.applications.whereami;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ericsson.nrgsdk.examples.applications.whereami.models.Client;
import com.ericsson.nrgsdk.examples.applications.whereami.models.Driver;
import com.ericsson.nrgsdk.examples.applications.whereami.models.Ride;

public class Service {
	final public List<Driver> drivers = new ArrayList<Driver>();
	final public List<Client> clients = new ArrayList<Client>();
	final public List<Ride> rides = new ArrayList<Ride>();
	
	public Optional<Driver> getDriver(String number) {
		return Optional.of(drivers.get(drivers.indexOf(number)));
	}
	
	public Optional<Client> getClient(String number) {
		return Optional.of(clients.get(clients.indexOf(number)));
	}
	
	public boolean addRide(Ride ride) {
		if (!rides.contains(ride)) {
			rides.add(ride);
			System.out.println("Dodano ride o uuid " + ride.uuid);
			return true;
		}

		return false;
	}

	public boolean removeRide(Ride ride)
	{
		if(!rides.contains(ride)){
			System.out.println("Blad! Nie ma takiego ride! :( " + ride.uuid);
			return false;
		}
		else
			rides.remove(ride);
			return true;

	}
	
	public List<Ride> getRides() {
		return rides;
	}

}
