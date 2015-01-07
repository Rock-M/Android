package com.mb.fczclock;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TabHost;


public class MainActivity extends Activity {

	private TabHost tabHost;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();
		
		tabHost.addTab(tabHost.newTabSpec("tabTime").setIndicator("ʱ��").setContent(R.id.tabTime));
		tabHost.addTab(tabHost.newTabSpec("tabAlarm").setIndicator("����").setContent(R.id.tabAlarm));
		tabHost.addTab(tabHost.newTabSpec("tabTimer").setIndicator("��ʱ��").setContent(R.id.tabTimer));
		tabHost.addTab(tabHost.newTabSpec("tabStopWatch").setIndicator("���").setContent(R.id.tabStopWatch));


	}



}
