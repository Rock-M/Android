package com.mb.fczclock;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TimePicker;

public class AlarmView extends LinearLayout {

	private Button btnAddAlarm;
	private ListView lvAlarmList;
	private ArrayAdapter<AlarmData> adapter;
	private static final String KEY_ALARM_LIST = "alarmList";
	private AlarmManager alarmManager;

	public AlarmView(Context context) {
		super(context);
		init();
	}

	public AlarmView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public AlarmView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	/**
	 * ��ʼ������
	 */
	private void init() {
		alarmManager = (AlarmManager) getContext().getSystemService(
				Context.ALARM_SERVICE);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		btnAddAlarm = (Button) findViewById(R.id.btnAddAlarm);
		lvAlarmList = (ListView) findViewById(R.id.lvAlarmList);

		// ����һ������������ʹ���Զ�������
		adapter = new ArrayAdapter<AlarmView.AlarmData>(getContext(),
				android.R.layout.simple_list_item_1);
		// ��������������ӽ�list��
		lvAlarmList.setAdapter(adapter);

		readSavedAlarmList();
		// ������Ӱ�ť
		btnAddAlarm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// ������嶯��
				addAlarm();
			}
		});

		// ����listView�ĳ���
		lvAlarmList
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, final int position, long id) {
						new AlertDialog.Builder(getContext())
								.setTitle("����ѡ��")
								.setItems(new CharSequence[] { "ɾ��" },
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												switch (which) {
												case 0:
													deleteAlarm(position);
													break;

												default:
													break;
												}
											}
										}).setNegativeButton("ȡ��", null).show();
						return true;
					}
				});
	}

	/**
	 * �������
	 */
	private void addAlarm() {
		Calendar c = Calendar.getInstance();
		// ѡ��ʱ���dialog
		new TimePickerDialog(getContext(),
				new TimePickerDialog.OnTimeSetListener() {

					/**
					 * ���Ի�����ѡ�е�ʱ�䣬���д���
					 */
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay,
							int minute) {
						Calendar c = Calendar.getInstance();
						c.set(Calendar.HOUR_OF_DAY, hourOfDay);
						c.set(Calendar.MINUTE, minute);
						c.set(Calendar.SECOND, 0);
						c.set(Calendar.MILLISECOND, 0);

						Calendar currentTime = Calendar.getInstance();
						if (c.getTimeInMillis() <= currentTime
								.getTimeInMillis()) {
							c.setTimeInMillis(currentTime.getTimeInMillis()
									+ 24 * 60 * 60 * 1000);
						}
						// ��ѡ��õ�����ʱ����뵽�������У�������ʾ��listView��
						AlarmData ad = new AlarmData(c.getTimeInMillis());
						adapter.add(ad);
						alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, ad
								.getTime(), 5 * 1000, PendingIntent
								.getBroadcast(getContext(), ad.getId(),
										new Intent(getContext(),
												AlarmReceiver.class), 0));
						saveAlarmList();
					}
				}, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true)
				.show();
	}

	/**
	 * ��������
	 */
	private void saveAlarmList() {
		SharedPreferences sp = getContext().getSharedPreferences(
				AlarmView.class.getName(), Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < adapter.getCount(); i++) {
			sb.append(adapter.getItem(i).getTime()).append(",");
		}

		if (sb.length() > 1) {
			String content = sb.toString().substring(0, sb.length() - 1);
			editor.putString(KEY_ALARM_LIST, content);
		} else {
			editor.putString(KEY_ALARM_LIST, null);
		}
		editor.commit();
	}

	/**
	 * ��ȡ�Ѿ����ڵ���������
	 */
	private void readSavedAlarmList() {
		SharedPreferences sp = getContext().getSharedPreferences(
				AlarmView.class.getName(), Context.MODE_PRIVATE);
		String content = sp.getString(KEY_ALARM_LIST, null);
		String[] timeStrings;
		if (content != null) {
			timeStrings = content.split(",");
			for (String string : timeStrings) {
				adapter.add(new AlarmData(Long.parseLong(string)));
			}
		}
	}

	/**
	 * ɾ���б�������
	 * 
	 * @param position
	 */
	private void deleteAlarm(int position) {
		AlarmData ad = adapter.getItem(position);
		adapter.remove(ad);
		saveAlarmList();
		//ɾ�����ӵ�ʱ��ͬ��ɾ�����ӷ���
		alarmManager.cancel(PendingIntent.getBroadcast(getContext(),
				ad.getId(), new Intent(getContext(), AlarmReceiver.class), 0));
	}

	/**
	 * ��̬�ڲ��� �Զ���Adapter����
	 * 
	 * @author M
	 * 
	 */
	private static class AlarmData {

		private String timeLabel = "";
		private long time = 0;
		private Calendar date;

		public AlarmData(long time) {
			this.time = time;
			date = Calendar.getInstance();
			date.setTimeInMillis(time);
			timeLabel = String.format("%d��%d��  %d:%d",
					date.get(Calendar.MONTH) + 1,
					date.get(Calendar.DAY_OF_MONTH),
					date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE));
		}

		public long getTime() {
			return time;
		}

		public String getTimeLabel() {
			return timeLabel;
		}

		public int getId() {
			return (int) (getTime() / 1000 / 60);
		}

		@Override
		public String toString() {
			return getTimeLabel();
		}
	}

}
