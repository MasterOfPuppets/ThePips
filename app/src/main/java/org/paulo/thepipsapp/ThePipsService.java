package org.paulo.thepipsapp;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

public class ThePipsService extends IntentService {

	public ThePipsService() {super("ThePipsService");}

	private boolean status = false;

	private int delay;
	private int frequency;
	private int mode = SettingsManager.TICK_MODE_PHONESONLY;
	private int pipMode = SettingsManager.TICK_MODE_PIP_OFF;
	private IntervalManager interval;
	private String tickSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString();
	private int differentiate;
//	Intent localIntent;
//	AppDataReceiver receiver;
	
	@Override
	public void onCreate(){
		super.onCreate();
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
	}	
	
	private void readSettings(){
		SettingsManager settings = new SettingsManager(this);
		interval = new IntervalManager(this);
		IntervalCalculation intervalCalc = new IntervalCalculation();
		setDelay(intervalCalc.getNextEventDelay());
		setFrequency(interval.getNextInterval());
		setMode(settings.getMode());
		setTickSound(settings.getTickSound());
		setPipMode(settings.getPipMode());
		setStatus(settings.getPipsState() == SettingsManager.PIPS_RUNNING);
		setDifferentiate(settings.getDifferentiate());
        settings.dumpSettings();
	}
		
	private void beep() {
        readSettings();
        if (!canPlay()) {
            return;
        }

        if (isStatus() && (getHeadSetStatus() || getMode() == SettingsManager.TICK_MODE_ALWAYS)) {
			if (getPipMode() == SettingsManager.TICK_MODE_PIP_ON) {
				    //decide the frequency here, we know it will be here every 15 minutes
					Tone tone = new Tone();
					if (getDifferentiate() == SettingsManager.DIFFERENTIATE_ON){
                        int minutes = getQuarter();
						if (this.getFrequency() == SettingsManager.QUARTER_HOUR){
							if (minutes == 1) {
								tone.firstQuarterPips();
							} else if (minutes == 2) {
								tone.secondQuerterPips();
							} else if (minutes == 3){
								tone.thirdQuarterPips();
							} else {
								tone.hourPips();
							}
						} else if (this.getFrequency() == SettingsManager.HALF_HOUR){
							if (minutes == 2) {
								tone.secondQuerterPips();
							} else  {//if (minutes == 0 || minutes == 4) {
								tone.hourPips();
							}
						} else {
                            tone.bbcPips();
					    }
					} else {
                        tone.bbcPips();
                    }
			} else {
                Ringtone r = RingtoneManager.getRingtone(
                        getApplicationContext(), Uri.parse(getTickSound()));
                r.play();
			}
		}
	}

	private int getQuarter(){
		return (int) Math.round(getMinutes() / SettingsManager.QUARTER_HOUR);
	}
	
	private int getMinutes(){
		long actualTime = System.currentTimeMillis();
		return (int)(actualTime / (1000*60)) % 60;
	}
	
	private boolean canPlay(){
//        Log.d(ThePips.TAG,"(canPlay) Minutes: " + String.valueOf(this.getMinutes()));
//        Log.d(ThePips.TAG,"(canPlay) Frequency: " + String.valueOf(this.getFrequency()));
		return this.getMinutes() % this.getFrequency() == 0.0;
	}
	
	public void execCommand(String command){
		if (null == command || command.isEmpty()) return;
        if (command.equals("beep")){
            readSettings();
            beep();
        }
        else if (command.equals("STOP")){
            stopSelf();
        }
	}
	
	public int getMode(){return this.mode;}
	public void setMode(int mode){this.mode = mode;}
	public String getTickSound(){return this.tickSound;}
	public void setTickSound(String sound){this.tickSound = sound;}
//	public int getDelay(){return this.delay;}
	public void setDelay(int delay){this.delay = delay;}

	public int getFrequency(){return this.frequency;}
	public void setFrequency(int freq){this.frequency = freq;}
		
	@SuppressWarnings("deprecation")
	public Boolean getHeadSetStatus() {
		AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		return am.isWiredHeadsetOn();
	}
	
//	private class AppDataReceiver extends BroadcastReceiver {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			execCommand(intent.getStringExtra("command"));
//		}
//	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		//readSettings();
		String command = intent.getStringExtra("command");
		execCommand(command);
	}

	public int getPipMode() {
		return pipMode;
	}
	public void setPipMode(int pipMode) {
		this.pipMode = pipMode;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public int getDifferentiate() {
		return differentiate;
	}
	public void setDifferentiate(int differentiate) {
		this.differentiate = differentiate;
	}		
}