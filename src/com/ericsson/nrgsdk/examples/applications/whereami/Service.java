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
	

	public boolean addDriver(Driver driver) {
		if (!drivers.contains(driver)) {
			drivers.add(driver);
			System.out.println("Dodano drivera o nazwie " + driver.number);
			return true;
		}

		return false;
	}

	public boolean removeDriver(Driver driver)
	{
		if(!drivers.contains(driver)){
			System.out.println("Blad! Nie ma takiego drivera! :( " + driver.number);
			return false;
		}
		else
			drivers.remove(driver);
			return true;

	}
	
	public Optional<Driver> getDriver(String number) {
		return Optional.of(drivers.get(drivers.indexOf(number)));
	}
	
	public boolean addClient(Client client) {
		if (!clients.contains(client)) {
			clients.add(client);
			System.out.println("Dodano klienta o nazwie " + client.number);
			return true;
		}

		return false;
	}

	public boolean removeClient(Client client)
	{
		if(!clients.contains(client)){
			System.out.println("Blad! Nie ma takiego klienta! :( " + client.number);
			return false;
		}
		else
			clients.remove(client);
			return true;

	}
	
	public List<Client> getClients() {
		return clients;
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
