package com.avegbird.barrage;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
	private BarrageSurfaceView myBarrage;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myBarrage = new BarrageSurfaceView(this);
		setContentView(myBarrage);
	}

	@Override
	protected void onStart() {
		super.onStart();

		new Thread(new Runnable(){
			@Override
			public void run() {
				BarrageSurfaceView.MyDrawThread randertherad = myBarrage.getRanderTherad();
				while (true) {
					if (randertherad != null) {
						try {
							Log.e("MainActivity", "set Barrage");
							randertherad.setBarrage("" + System.currentTimeMillis());
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						Log.e("MainActive", "randertherad is null ");
						randertherad = myBarrage.getRanderTherad();
					}
				}
			}
		}).start();
	}
}
