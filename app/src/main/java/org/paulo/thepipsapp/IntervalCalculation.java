package org.paulo.thepipsapp;

import java.util.Calendar;
//import java.util.Date;


public class IntervalCalculation {

	private Calendar cal;
	private int interval;
	//private Date startTime;
	public static int PULSE_WIDTH = 1;
	
	//SettingsManager settings;
	
	public IntervalCalculation(int interval){
		this.interval = interval;
		cal = Calendar.getInstance();
//		startTime = cal.getTime();
	}
		
	public IntervalCalculation(){
		this(PULSE_WIDTH);
	}
	
	public int getNextPulseDelay(){
		return ((getMinutes() / PULSE_WIDTH) * PULSE_WIDTH) + PULSE_WIDTH - getMinutes();		
	}
	
	public int getMinutes(){
		return cal.get(Calendar.MINUTE);
	}
	public int getLastEvent(){
		return getMinutes() / this.interval;
	}
	//public int getRoundEvent(){return getMinutes() % interval;}
	public int getLastEventMinutes(){
		return getLastEvent() * this.interval;
	}
	public int getNextEventTime(){
		return getLastEventMinutes() + this.interval;
	}
	public int getNextEventDelay(){
		int next = getNextEventTime();
		int min = getMinutes();
		return next - min;
	}

//	public Date getNextEvent(){
//		Calendar c = Calendar.getInstance();
//		c.setTime(startTime);
//		c.add(Calendar.MINUTE, getNextEventDelay());
//		c.set(Calendar.SECOND, 0);
//		c.set(Calendar.MILLISECOND, 0);
//		return c.getTime();
//	}
}
