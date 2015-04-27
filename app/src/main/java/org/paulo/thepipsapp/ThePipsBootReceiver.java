package org.paulo.thepipsapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ThePipsBootReceiver extends BroadcastReceiver {
	private ThePipsCore pipsCore;
	@Override
	public void onReceive(Context context, Intent intent) {
		if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {			
			pipsCore = new ThePipsCore(context);
			pipsCore.scheduleService();
			Toast.makeText(context, "The pips are going to pip", Toast.LENGTH_SHORT).show();
		}
	}
}
