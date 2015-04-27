package org.paulo.thepipsapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.preference.PreferenceManager;
import android.util.Log;
//import android.widget.Toast;

//import java.util.Iterator;
import java.util.Map;

public class SettingsManager {

    /* Settings keys */
	private String KEY_NEXT_INTERVAL = "nextInterval"; //tick frequency in minutes
	private String KEY_TICK_MODE = "tickMode"; //0 - only ticks with phones plugged , 1 - always ticks
//	private String KEY_TICK_ENGINE = "tickManager"; //0 - RingtoneManager, 1 - MediaPlayer
	private String KEY_TICK_SOUND = "tickSound"; //ignored if ringtonemanager, soundfile name else
	private String KEY_PIP_MODE = "pipMode"; // 0 = pipmode, 1 = ringtonemode
	private String KEY_PIPS_RUNNING = "pipsRunning"; // 0 = pips are off
	private String KEY_DIFFERENTIATE_MODE = "differentiatePips";


    /* Constants */

    public static final int ONE_HOUR = 60;
	public static final int HALF_HOUR = 30;
	public static final int QUARTER_HOUR = 15;
//    public static final int ONE_HOUR = 4;
//	public static final int HALF_HOUR = 2;
//	public static final int QUARTER_HOUR = 1;
//    public static final int ONE_HOUR = 12;
//    public static final int HALF_HOUR = 6;
//    public static final int QUARTER_HOUR = 3;
	public static final int TICK_MODE_PHONESONLY = 0;
	public static final int TICK_MODE_PIP_OFF = 0;
	public static final int TICK_MODE_PIP_ON = 1;
	public static final int TICK_MODE_ALWAYS = 1;
//	public static final int TICK_ENGINE_RINGTONE = 0;
//	public static final int TICK_ENGINE_MEDIAPLAYER = 1;
	public static final int PIPS_RUNNING = 1;
	public static final int PIPS_NOT_RUNNING = 0;
	public static final int DIFFERENTIATE_OFF = 0;
	public static final int DIFFERENTIATE_ON = 1;

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private Context caller;
	
	public SettingsManager(Activity caller){
		settings = PreferenceManager.getDefaultSharedPreferences(caller.getApplicationContext());
        this.caller = caller;
		//uncomment to clear settings		
//		SharedPreferences.Editor editor = settings.edit();
//		editor.clear();
//		editor.commit();		
	}
	
	public SettingsManager(Context context){
		settings = PreferenceManager.getDefaultSharedPreferences(context);
        this.caller = context;
	}
	
//	public SettingsManager(SharedPreferences prefs){
//		settings = prefs;
//		//uncomment to clear settings
////		SharedPreferences.Editor editor = settings.edit();
////		editor.clear();
////		editor.commit();
//	}

	public int getPipsState(){
		return settings.getInt(KEY_PIPS_RUNNING, PIPS_NOT_RUNNING);
	}
	
	public void setPipsState(int newState){
		editInt(KEY_PIPS_RUNNING, newState);
	}
		
	
	public int getInterval(){
		return settings.getInt(KEY_NEXT_INTERVAL, ONE_HOUR);
	}
	
	public void setInterval(int newInterval){
		editInt(KEY_NEXT_INTERVAL, newInterval);
	}
	
	
	public int getPipMode(){
		return settings.getInt(KEY_PIP_MODE, TICK_MODE_PIP_OFF);
	}
	
	public void setPipMode(int newMode){
		editInt(KEY_PIP_MODE, newMode);
	}
	
	public int getMode(){
		return settings.getInt(KEY_TICK_MODE, TICK_MODE_PHONESONLY);
	}
	
	public void setMode(int newMode){
		switch (newMode){
			case TICK_MODE_ALWAYS:
			case TICK_MODE_PHONESONLY :
				editInt(KEY_TICK_MODE, newMode);
				break;
			default:
		}
	}

//	public int getEngine(){
//		return settings.getInt(KEY_TICK_ENGINE, TICK_ENGINE_RINGTONE);
//	}
//
//	public void setEngine(int newEngine){
//		switch (newEngine){
//			case TICK_ENGINE_RINGTONE:
//			case TICK_ENGINE_MEDIAPLAYER :
//				editor = settings.edit();
//				editor.putInt(KEY_TICK_ENGINE, newEngine);
//                save(editor);
//				break;
//			default:
//		}
//	}

	public String getTickSound(){
		return settings.getString(KEY_TICK_SOUND, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString());
	}

	public void setTickSound(String newSound){
		if (null != newSound && newSound.length() > 0 && newSound.length() < 256){ //256 seems like a nice number
			editString(KEY_TICK_SOUND, newSound);
		}
	}
	
	public int getDifferentiate(){
		return settings.getInt(KEY_DIFFERENTIATE_MODE, DIFFERENTIATE_OFF);
	}
	
	public void setDifferentiate(int diffMode){
		editInt(KEY_DIFFERENTIATE_MODE, diffMode);
	}

    public void dumpSettings(){
        if (!ThePips.DEBUG) return;
        Log.d(ThePips.TAG, "=============");
        Log.d(ThePips.TAG, "Settings dump");
        Log.d(ThePips.TAG, "=============");
        Map<String, ?> set =  settings.getAll();
        for (String k : set.keySet()){
            Log.d(ThePips.TAG, k + " = " + set.get(k).toString());
        }
        Log.d(ThePips.TAG, "====================");
        Log.d(ThePips.TAG, "End of Settings dump");
        Log.d(ThePips.TAG, "====================");
    }

    private void editInt(String key, int value){
        editor = settings.edit();
        editor.putInt(key,value);
        ThePipsToast toast = new ThePipsToast((Activity) caller);
        toast.setText(caller.getResources().getString(R.string.toastSettingsSaved));
        toast.showShort();
        editor.commit();
    }

    private void editString(String key, String value){
        editor = settings.edit();
        editor.putString(key,value);
        ThePipsToast toast = new ThePipsToast((Activity) caller);
        toast.setText(caller.getResources().getString(R.string.toastSettingsSaved));
        toast.showShort();
        editor.commit();
    }
}