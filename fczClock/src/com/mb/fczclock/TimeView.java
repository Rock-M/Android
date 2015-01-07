package com.mb.fczclock;

import java.util.Calendar;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TimeView extends LinearLayout {
	
	private TextView tvTime;

	public TimeView(Context context) {
		super(context);
	}

	public TimeView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TimeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		tvTime = (TextView) findViewById(R.id.tvTime);
		System.out.println("A-->>>");
		timerHandler.sendEmptyMessage(0);
		System.out.println("F-->>>");
		
	}

	private void refreshTime(){
		System.out.println("C-->>>");
		Calendar c = Calendar.getInstance();
		tvTime.setText(String.format("%d:%d:%d", c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),c.get(Calendar.SECOND)));
		System.out.println(c.get(Calendar.HOUR_OF_DAY)+"  "+c.get(Calendar.MINUTE)+"  "+c.get(Calendar.SECOND));
	}
	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
		if(getVisibility() == View.VISIBLE){
			timerHandler.sendEmptyMessage(0);
		}else{
			timerHandler.removeMessages(0);
		}
	}
	
	private Handler timerHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			System.out.println("B-->>>" + msg);
			refreshTime();
			if(getVisibility() == View.VISIBLE){
				System.out.println("D-->>>");
				timerHandler.sendEmptyMessageDelayed(0, 1000);
				System.out.println("E-->>>");
			}
		};

	};
}
