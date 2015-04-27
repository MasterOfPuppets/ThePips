package org.paulo.thepipsapp;

import org.paulo.thepipsapp.R;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ThePipsToast extends Toast {

	private String tText = "ThePipsToast";
	private TextView toastMsg;
	
	public ThePipsToast(Activity caller) {
		super(caller.getApplicationContext());
		LayoutInflater inflater =  caller.getLayoutInflater();
		View layout = inflater.inflate(R.layout.ticker_toast_layout,(ViewGroup) caller.findViewById(R.id.ticker_toast_layout_root));
		toastMsg = (TextView) layout.findViewById(R.id.msg);
		toastMsg.setText(tText);
		setDuration(Toast.LENGTH_SHORT);//default
		setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		setView(layout);
	}
	
	@Override
	public void show(){
		super.show();
	}
	@Override
	public void setText(CharSequence msg){
		this.tText = msg.toString();
		toastMsg.setText(msg);
	}
	public void showShort(){
		this.setDuration(Toast.LENGTH_SHORT);
		this.show();
	}	
	public void showLong(){
		this.setDuration(Toast.LENGTH_LONG);
		this.show();
	}	
}
