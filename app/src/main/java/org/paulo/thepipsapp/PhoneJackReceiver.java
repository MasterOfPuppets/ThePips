package org.paulo.thepipsapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

public abstract class PhoneJackReceiver extends BroadcastReceiver implements OwnerAction {

	private Activity caller;
	private int jackStatus; // 0 = disconnected, 1 = connected, 2 = can't say
	
	public PhoneJackReceiver(Activity a) {
		caller = a;
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
			jackStatus = intent.getIntExtra("state", -1);
			action();
		}
	}
	
	public void startListen(){
		if (null == caller) return;
		IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
		caller.registerReceiver(this, filter);
	}
	
	public void stopListen(){
		if (null == caller) return;
		caller.unregisterReceiver(this);
	}
	
	public Boolean getJackStatus(){
		return jackStatus == 1;
	}
	
	@SuppressWarnings("deprecation")
	public Boolean getHeadSetStatus() {
		if (null == caller) return false;
		AudioManager am = (AudioManager) caller.getSystemService(Context.AUDIO_SERVICE);
		return am.isWiredHeadsetOn();
	}

}
