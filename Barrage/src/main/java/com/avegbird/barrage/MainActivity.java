package com.avegbird.barrage;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
	int Max_D = 1000;
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
				MyDrawThread randertherad = myBarrage.getRanderTherad();
				while (true) {
					if (randertherad != null) {
						try {
							randertherad.setBarrage("" + System.currentTimeMillis());
							long delay = (long) (Math.random()*Max_D);
							if (delay < Max_D/3)
								delay = (long) (Math.random()*100);
							Thread.sleep(delay);
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
