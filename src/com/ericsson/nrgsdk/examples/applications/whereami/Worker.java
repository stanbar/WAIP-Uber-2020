package com.ericsson.nrgsdk.examples.applications.whereami;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


public class Worker extends Thread
{
	int daysInMonth = 31;
	int hoursInDay = 24;
	public int[][] workerCalendar = new int[hoursInDay][daysInMonth];
	private String numer;
	private String name;
	private LocalDateTime startedWorkAt;
	private LocalDateTime endedWorkAt;
	private int hoursPerDay = 8;
	private long pauseLength = 15;
	private LocalDateTime pauseStart;

	private LocationProcessor itsLocationProcessor;

	public String getNumer()
	{
		return numer;
	}

	public Worker(String numer, String name, int hoursPerDay, LocationProcessor aLocationProcessor)
	{
		this.numer = numer;
		this.name = name;
		this.hoursPerDay = hoursPerDay;
		itsLocationProcessor = aLocationProcessor;
	}

	public long getCurrentWorkTime(){
		return ChronoUnit.HOURS.between(startedWorkAt,LocalDateTime.now());
	}

	public void setStartedWorkAt(LocalDateTime date){
		this.startedWorkAt = date;
	}

	public LocalDateTime getStartedWorkAt(){
		return startedWorkAt;
	}

	public LocalDateTime getEndedWorkAt(){
		return endedWorkAt;
	}

	public void setEndedWorkAt(LocalDateTime date){
		this.endedWorkAt = date;
	}

	public int getHoursPerDay(){
		return hoursPerDay;
	}

	public String getWorkerName(){
		return this.name;
	}

	public long getPauseLength(){
		return this.pauseLength;
	}

	public void checkLocalization() {
		itsLocationProcessor.requestLocation(numer);
	}

	public int setCalendar(int day, int hour){
		if (workerCalendar[day-1][hour-1] == 0){
			workerCalendar[day-1][hour-1] = 1;
			return 0;
		}else{
			return 1;
		}
	}

	public int getCalendar(int day, int hour){
		if (workerCalendar[day-1][hour-1] == 0){
			return 0;
		}else{
			return 1;
		}
	}

	public void run()
	{
		while (true)
		{
			System.out.println("Pracownik o numerze " + numer + " sprawdza swoja lokalizacje");
			itsLocationProcessor.requestLocation(numer);
			try
			{
				Thread.sleep(20000);
			}
			catch (IllegalArgumentException e)
			{
				System.out.println("IllegalArgumentException" + e.getMessage());
			}
			catch (InterruptedException e)
			{
				System.out.println("InterruptedException" + e.getMessage());
			}
		}
	}

	public LocalDateTime getPauseStart() {
		return pauseStart;
	}

	public void setPauseStart(LocalDateTime pauseStart) {
		this.pauseStart = pauseStart;
	}
}
