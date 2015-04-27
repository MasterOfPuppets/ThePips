package org.paulo.thepipsapp;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;

public class ThePipsAbout extends Activity {

	private String ABOUT_TEXT_FILE = "files/about/about.html";
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.about);
		//TextView aboutText = (TextView) findViewById(R.id.aboutText);
		WebView aboutWeb = (WebView) findViewById(R.id.webView1);
		Button aboutButton = (Button) findViewById(R.id.buttonClose);
		//aboutText.setText(Html.fromHtml(getAboutText()));
		//aboutText.setMovementMethod(LinkMovementMethod.getInstance());
		aboutWeb.loadData(getAboutText(), "text/html",null);
		aboutButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private String getAboutText(){
		AssetManager aManager = this.getAssets();
		String ret = "";
		try {
			InputStream file = aManager.open(ABOUT_TEXT_FILE);
			int size = file.available();
			          byte[] buffer = new byte[size];
			          file.read(buffer);
			          file.close();
			          ret = new String(buffer);
		} catch (IOException e) {
			Log.e("The Pips","Cannot find the about file: " + ABOUT_TEXT_FILE);
			//e.printStackTrace();
		}
		return ret;
	}
}
