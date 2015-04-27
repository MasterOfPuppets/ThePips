package org.paulo.thepipsapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class ThePipsCore {
	
	private Context context;

	public ThePipsCore(Context context){
		this.context = context;
		//getSettings();
	}
	
	private PendingIntent getAlarmIntent(){
		Intent intent = new Intent(context, ThePipsReceiver.class);
		intent.setAction(ThePipsReceiver.ACTION);
		return PendingIntent.getBroadcast(context,ThePipsReceiver.REQUEST_CODE, intent,PendingIntent.FLAG_UPDATE_CURRENT);
	}
	
	private AlarmManager getAlarm(){
		return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	}
	
	public void scheduleService() {
		cancelService();
		IntervalCalculation intervalCalc = new IntervalCalculation();
		long firstMillis = ThePipsUtils.millis(intervalCalc.getNextPulseDelay());
		long startAt = System.currentTimeMillis() + firstMillis;
		getAlarm().set(AlarmManager.RTC_WAKEUP, startAt, getAlarmIntent());
	}

	public void cancelService() {
		getAlarm().cancel(getAlarmIntent());
	}	
}
