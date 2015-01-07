package com.mb.fczclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("ÄÖÖÓÆô¶¯Íê³É!"+"  "+getResultCode());
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		am.cancel(PendingIntent.getBroadcast(context, getResultCode(),
				new Intent(context, AlarmReceiver.class), 0));
	}

}
