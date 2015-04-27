package org.paulo.thepipsapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ThePipsReceiver extends BroadcastReceiver {
	public static final int REQUEST_CODE = 1000;
	public static final String ACTION_BEEP = "ticker.beep";
	public static final String ACTION_STOP = "ticker.stop";
	public static final String ACTION_START = "ticker.start";
	public static final String ACTION = "ticker";
	
	@Override
	public void onReceive(Context context, Intent intent) {
        Log.d(ThePips.TAG, "I've got an action, and it was: " + intent.getAction());
		if (intent.getAction().equals(ACTION)){
			Intent i = new Intent(context, ThePipsService.class);
			i.putExtra("command", "beep");
			context.startService(i);
			ThePipsCore pipsCore = new ThePipsCore(context);
			pipsCore.scheduleService();

		} else if (intent.getAction().equals(ACTION_STOP)){
			// not implemented
		}
	}

}
