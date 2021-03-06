package ru.bmstu.schedule.invisible;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import ru.bmstu.schedule.activities.WeekActivity;
import ru.bmstu.schedule.calendar.R;
import ru.bmstu.schedule.models.Lesson;
import ru.bmstu.schedule.models.SemesterInfo;
import ru.bmstu.schedule.models.readers.ModelsInitializer;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

public class DailySetter extends BroadcastReceiver {
	public final static String ACTION_SET_ALARMS = DailySetter.class.getCanonicalName()+".ACTION_SET_ALARMS";
	public final static String ACTION_SET_ALL    = DailySetter.class.getCanonicalName()+".ACTION_SET_ALL";
	
	@Override
    public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
        if (action.equals(ACTION_SET_ALARMS)) {
            setSoundManipulationAlarms(context);
        } else if (action.equals(Intent.ACTION_BOOT_COMPLETED)
        		|| action.equals(ACTION_SET_ALL)) {
        	setDailySetterAlarm(context);
        	setSoundManipulationAlarms(context);
        }
    }

	
	public static void setSoundManipulationAlarms(Context context) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		String jsonData;
		try {
			AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			alarmMgr.cancel(PendingIntent.getBroadcast(context, 0, new Intent(context, SoundManager.class), 0));
			
			jsonData = ModelsInitializer.fileToString(context.getAssets().open("rasp.json"), "UTF-8");
			List<Lesson> lessons = ModelsInitializer.readLessonsFromJSON(jsonData, pref.getString("pref_group", null));
			
			Calendar now = Calendar.getInstance();
			int nowWday = (now.get(Calendar.DAY_OF_WEEK) + (7 - Calendar.MONDAY)) % 7;
			
			SemesterInfo sem = ModelsInitializer.getSemesterInfo(context);
			// weekIndex = weekNumber mod 2
			int weekIndex = (now.get(Calendar.WEEK_OF_YEAR)-sem.theoryBegin.get(Calendar.WEEK_OF_YEAR)) % 2;
			if (weekIndex < 0) weekIndex += 2;
			
			for (Lesson l : lessons) {
				if (l.getWday() == nowWday && l.getRepeatType().ordinal() == weekIndex) {
					Calendar pairTime = l.setBeginTimeForDay(now);
					setSoundManipulationAlarm(pairTime, SoundManager.OFF_ACTION, context);
					pairTime = l.setEndTimeForDay(now);
					setSoundManipulationAlarm(pairTime, SoundManager.ON_ACTION, context);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void setSoundManipulationAlarm(Calendar time, String action, Context context) {
		Intent intent = new Intent(context, SoundManager.class);
		intent.setAction(action);
		
		PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		
		AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmMgr.set(AlarmManager.RTC, time.getTimeInMillis(), alarmIntent);
	}
	
	public static void setDailySetterAlarm(Context context) {
		Intent iDaily = new Intent(context, DailySetter.class);
        iDaily.setAction(ACTION_SET_ALARMS);
        
        PendingIntent dailySetterIntent = PendingIntent.getBroadcast(context, 0, iDaily, 0);
        
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmMgr.cancel(dailySetterIntent);
		
		Calendar midnight = Calendar.getInstance();
		midnight.add(Calendar.DAY_OF_YEAR, 1);
		midnight.set(Calendar.MILLISECOND, 0);
		//midnight.add(Calendar.SECOND, 30);
		midnight.set(Calendar.SECOND, 0);
		midnight.set(Calendar.MINUTE, 0);
		midnight.set(Calendar.HOUR,   0);
		
		alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, midnight.getTimeInMillis(),
				AlarmManager.INTERVAL_DAY, dailySetterIntent);
	}
}
