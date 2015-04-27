package org.paulo.thepipsapp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ThePips extends Activity {
	public static String TAG = "ThePips";
    public static boolean DEBUG = true;
	//public static int VIEW_REFRESH_RATE = 3000; // view refresh rate in millis

	private ThePipsCore pipsCore;
	private Boolean headSetStatus;
	private SettingsManager settings;
	private PhoneJackReceiver phonesSensor;
	private String soundName = null;

	// View Components
	private TextView phonesStatusText;
	
	//private TextView tickStatusText;
	private TextView soundText;
	private RadioButton rbHourly;
	private RadioButton rbHalfHourly;
	private RadioButton rbQuarterHourly;
	private RadioButton rbModePhonesOnly;
	private RadioButton rbModeAlways;
	private RadioButton rbPipOn;
	private RadioButton rbPipOff;
	private ToggleButton pipsOnOff;
	private CheckBox cbDifferentiate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSettings();
		setupView();
		
		// headphones sensor
		phonesSensor = new PhoneJackReceiver(this) {
			@Override
			public void action() {
				if (this.getJackStatus()) {
					phonesStatusText.setText(getStr(R.string.headphonesIn));
					//toast("Phone jack connected");
					setHeadSetStatus(true);
				} else {
					phonesStatusText.setText(getStr(R.string.headphonesOut));
					//toast("Phone jack disconnected");
					setHeadSetStatus(false);
				}
			}			
		};
		phonesSensor.startListen();
		setHeadSetStatus(phonesSensor.getHeadSetStatus());
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	}
	
	private void getSettings(){
		settings = new SettingsManager(this);
	}
	
	private void setupView(){
		// first create view components...
		setContentView(R.layout.main);
		final Button butSoundChoose = (Button) findViewById(R.id.soundChooseButton);
		final Button aboutButton = (Button) findViewById(R.id.aboutButton);
		final Button earThePips = (Button) findViewById(R.id.earThePips);
		
		pipsOnOff = (ToggleButton) findViewById(R.id.pipsOnOff);
		phonesStatusText = (TextView) findViewById(R.id.phonesStatusText);
		//tickStatusText = (TextView) findViewById(R.id.tickStatusText);
		rbHourly = (RadioButton) findViewById(R.id.hourly);
		rbHalfHourly = (RadioButton) findViewById(R.id.halfHourly);
		rbQuarterHourly = (RadioButton) findViewById(R.id.quarterHourly);
		rbModePhonesOnly = (RadioButton) findViewById(R.id.modePhonesOnly);
		rbModeAlways = (RadioButton) findViewById(R.id.modeAlways);
		soundText = (TextView) findViewById(R.id.soundText);
		rbPipOn = (RadioButton) findViewById(R.id.rbPipOn);
		rbPipOff = (RadioButton) findViewById(R.id.rbPipOff);
		cbDifferentiate = (CheckBox) findViewById(R.id.cbDifferentiate);

		fillViewValues();
		
		// various click handlers
		earThePips.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Tone tone = new Tone();
				tone.bbcPips();
			}
		});
		aboutButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent aboutIntent = new Intent(ThePips.this, ThePipsAbout.class);
				startActivity(aboutIntent);
			}
		});
		
		butSoundChoose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String snd = settings.getTickSound();
				Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getStr(R.string.titleModalSelectTone));
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, null == snd ? null : Uri.parse(snd));
				ThePips.this.startActivityForResult(intent, 5);
			}
		});

	}
	
	public void cbDifferentiateClick(View view){
		if (((CheckBox) view).isChecked()){
			settings.setDifferentiate(SettingsManager.DIFFERENTIATE_ON);
		} else {
			settings.setDifferentiate(SettingsManager.DIFFERENTIATE_OFF);
		}
	}

	public void onPipsToggleClicked(View view){
		if (((ToggleButton) view).isChecked()) {
			settings.setPipsState(SettingsManager.PIPS_RUNNING);
			toast(getStr(R.string.toastThePipsAreRunning));
			pipsCore.scheduleService();
		} else {
			settings.setPipsState(SettingsManager.PIPS_NOT_RUNNING);
			toast(getStr(R.string.toastThePipsWereStopped));
			pipsCore.cancelService();
		}
		fillViewValues();
	}
	
	private void fillViewValues(){
		getSettings();
		// apply the saved settings to the view components

		// pips state
		pipsOnOff.setChecked(settings.getPipsState() == SettingsManager.PIPS_RUNNING);
		// frequency
		int wColor = Color.parseColor("#BAB2D4");
		int yColor = Color.parseColor("#A8AA39");
		yColor = wColor;
				
		rbHourly.setTextColor(yColor);
		rbHalfHourly.setTextColor(wColor);
		rbQuarterHourly.setTextColor(wColor);
		rbModePhonesOnly.setTextColor(wColor);
		rbModeAlways.setTextColor(wColor);
		rbPipOn.setTextColor(wColor);
		rbPipOff.setTextColor(wColor);
		cbDifferentiate.setTextColor(wColor);
		int freq = settings.getInterval();
		if (freq == SettingsManager.ONE_HOUR) {
			rbHourly.setChecked(true);
			rbHourly.setTextColor(yColor);
		} else if (freq == SettingsManager.HALF_HOUR) {
			rbHalfHourly.setChecked(true);
			rbHalfHourly.setTextColor(yColor);
		} else {
			rbQuarterHourly.setChecked(true);
			rbQuarterHourly.setTextColor(yColor);
		}
		
		// mode
		int mode = settings.getMode();
		if (mode == SettingsManager.TICK_MODE_PHONESONLY) {
			rbModePhonesOnly.setChecked(true);
			rbModePhonesOnly.setTextColor(yColor);
		} else {
			rbModeAlways.setChecked(true);
			rbModeAlways.setTextColor(yColor);
		}
		
		//pipMode
		int pipMode = settings.getPipMode();
		if (pipMode == SettingsManager.TICK_MODE_PIP_ON) {
			rbPipOn.setChecked(true);
			rbPipOn.setTextColor(yColor);
			cbDifferentiate.setEnabled(true);
		} else {
			rbPipOff.setChecked(true);
			rbPipOff.setTextColor(yColor);
			cbDifferentiate.setEnabled(false);
		}
		// differentating
		int diff = settings.getDifferentiate();
		if (diff == SettingsManager.DIFFERENTIATE_ON){
			cbDifferentiate.setChecked(true);
		} else {
			cbDifferentiate.setChecked(false);
		}
		soundText.setText(getStr(R.string.actualSoundLbl) + getTickSoundName(Uri.parse(settings.getTickSound())));
	}
	
	// get a new tick soundOverride
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
		if (resultCode == Activity.RESULT_OK && requestCode == 5) {
			Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
			if (uri != null) {
				settings.setTickSound(uri.toString());
				soundName = null;
				soundText.setText(getStr(R.string.actualSoundLbl) + getTickSoundName(uri));
				toast(getStr(R.string.toastNotificationSoundActive) + getTickSoundName(uri));
			}
		}
	}
		
	@Override
	protected void onStart(){
		super.onStart();
		pipsCore = new ThePipsCore(this);
	}
	@Override
	protected void onDestroy() {
		phonesSensor.stopListen();
		super.onDestroy();
	}

	public void radioButtonClick(View view) {
		boolean checked = ((RadioButton) view).isChecked();
		String toastMessage = getStr(R.string.toastFrequencySet);
		// Check which button was clicked		
		switch (view.getId()) {
		case R.id.hourly:
			if (checked){
				settings.setInterval(SettingsManager.ONE_HOUR);
			}
			break;
		case R.id.halfHourly:
			if (checked){
				settings.setInterval(SettingsManager.HALF_HOUR);
			}
			break;
		case R.id.quarterHourly:
			if (checked){
				settings.setInterval(SettingsManager.QUARTER_HOUR);
			}
			break;
		case R.id.modePhonesOnly:
			if (checked){
				settings.setMode(SettingsManager.TICK_MODE_PHONESONLY);
				toastMessage = getStr(R.string.toastOnWithHeadphones);
			}
			break;
		case R.id.modeAlways:
			if (checked){
				settings.setMode(SettingsManager.TICK_MODE_ALWAYS);
				toastMessage = getStr(R.string.toastAlwaysPip);
			}
			break;
		case R.id.rbPipOn:
			if (checked) {
				settings.setPipMode(SettingsManager.TICK_MODE_PIP_ON);
				toastMessage = getStr(R.string.toastPipSoundActive);
			}
			break;
		case R.id.rbPipOff:
			if (checked){
				settings.setPipMode(SettingsManager.TICK_MODE_PIP_OFF);
				toastMessage = getStr(R.string.toastNotificationSoundActive);
			}
			break;
		default:
		}
        toast(toastMessage);
		fillViewValues();
	}

	public Boolean getHeadSetStatus() {return headSetStatus;}
	private void setHeadSetStatus(Boolean headSetStatus) {this.headSetStatus = headSetStatus;}
	
	//
	// helper methods
	//
	private String getTickSoundName(Uri soundUri) {
		if (null != soundName) return soundName;
		Ringtone rt = RingtoneManager.getRingtone(getApplicationContext(),soundUri);
		soundName = rt.getTitle(getApplicationContext());
		return soundName;
	}
	
	public void toast(String msg) {
		ThePipsToast toast = new ThePipsToast(this);
		toast.setText(msg);
		toast.showShort();
	}
	
	private String getStr(int id){
		return getResources().getString(id);
	}
}