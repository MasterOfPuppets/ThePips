package org.paulo.thepipsapp;

//import android.app.Activity;
import android.content.Context;
//import android.content.SharedPreferences;

public class IntervalManager {

	private int nextInterval;
	
	SettingsManager settings;
	
//	public IntervalManager(Activity caller){
//		settings = new SettingsManager(caller);
//		nextInterval = SettingsManager.QUARTER_HOUR; //settings.getInterval();
//	}
	
	public IntervalManager(Context context){
		settings = new SettingsManager(context);
		nextInterval = settings.getInterval();
	}

//	@Deprecated
//	public IntervalManager(SharedPreferences prefs){
//		settings = new SettingsManager(prefs);
//		nextInterval = settings.getInterval();
//	}
	
//	public SettingsManager getSettings(){
//		return settings;
//	}
	
	public int getNextInterval(){		
		return nextInterval;
	}
	
//	public void setNextInterval(int next){
//		nextInterval = next;
//		settings.setInterval(next);
//	}
}
