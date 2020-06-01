package com.ericsson.nrgsdk.examples.applications.whereami;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.ericsson.nrgsdk.examples.applications.whereami.models.Client;
import com.ericsson.nrgsdk.examples.applications.whereami.models.Driver;
import com.ericsson.nrgsdk.examples.applications.whereami.models.Ride;

public class Service {
	final public List<Driver> drivers = new ArrayList<Driver>();
	final public List<Client> clients = new ArrayList<Client>();
	final public List<Ride> rides = new ArrayList<Ride>();

	public Optional<Driver> getDriver(final String number) {
		return drivers.stream().filter(new Predicate<Driver>() {
			@Override
			public boolean test(Driver driver) {
				return number == driver.number;
			}
		}).findAny();
	}

	public Optional<Client> getClient(final String number) {
		return clients.stream().filter(new Predicate<Client>() {
			@Override
			public boolean test(Client client) {
				return number == client.number;
			}
		}).findAny();
	}

	public Optional<Ride> getRide(final int number) {
		return rides.stream().filter(new Predicate<Ride>() {
			@Override
			public boolean test(Ride ride) {
				return number == ride.number;
			}
		}).findAny();
	}

	public Optional<Ride> getActiveRideForClientOrDriver(final String number) {
		return rides.stream().filter(new Predicate<Ride>() {
			@Override
			public boolean test(Ride ride) {
				return number == ride.client.number || number == ride.driver.number;
			}
		}).findAny();
	}

	public boolean addRide(Ride ride) {
		if (!rides.contains(ride)) {
			rides.add(ride);
			System.out.println("Dodano ride o numerze " + ride.number);
			return true;
		}

		return false;
	}

	public boolean removeRide(Ride ride) {
		if (!rides.contains(ride)) {
			System.out.println("Blad! Nie ma takiego ride! :( " + ride.number);
			return false;
		} else
			rides.remove(ride);
		return true;

	}

	public List<Ride> getRides() {
		return rides;
	}

}
