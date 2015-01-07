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
	 * 初始化方法
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

		// 创建一个适配器数组使用自定义类型
		adapter = new ArrayAdapter<AlarmView.AlarmData>(getContext(),
				android.R.layout.simple_list_item_1);
		// 将适配器数组添加进list中
		lvAlarmList.setAdapter(adapter);

		readSavedAlarmList();
		// 监听添加按钮
		btnAddAlarm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 添加闹铃动作
				addAlarm();
			}
		});

		// 监听listView的长按
		lvAlarmList
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, final int position, long id) {
						new AlertDialog.Builder(getContext())
								.setTitle("操作选项")
								.setItems(new CharSequence[] { "删除" },
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
										}).setNegativeButton("取消", null).show();
						return true;
					}
				});
	}

	/**
	 * 添加闹钟
	 */
	private void addAlarm() {
		Calendar c = Calendar.getInstance();
		// 选择时间的dialog
		new TimePickerDialog(getContext(),
				new TimePickerDialog.OnTimeSetListener() {

					/**
					 * 将对话框中选中的时间，进行处理
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
						// 将选择好的闹铃时间加入到适配器中，用于显示在listView中
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
	 * 保存闹钟
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
	 * 读取已经存在的闹铃配置
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
	 * 删除列表中闹铃
	 * 
	 * @param position
	 */
	private void deleteAlarm(int position) {
		AlarmData ad = adapter.getItem(position);
		adapter.remove(ad);
		saveAlarmList();
		//删除闹钟的时候，同步删除闹钟服务
		alarmManager.cancel(PendingIntent.getBroadcast(getContext(),
				ad.getId(), new Intent(getContext(), AlarmReceiver.class), 0));
	}

	/**
	 * 静态内部类 自定义Adapter类型
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
			timeLabel = String.format("%d月%d日  %d:%d",
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
