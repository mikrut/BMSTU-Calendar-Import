package ru.bmstu.schedule.invisible;

import ru.bmstu.schedule.calendar.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;

public class SoundManager extends BroadcastReceiver {
	
	public final static String ON_ACTION = "ru.bmstu.schedule.invisible.SoundManager.TURN_ON";
	public final static String OFF_ACTION = "ru.bmstu.schedule.invisible.SoundManager.TURN_OFF";

	private static Integer lastVolume = null;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		String manipulateAlarms = pref.getString("pref_sound", context.getString(R.string.SOUND_NOCHANGE));
	
		if (intent.getAction().equals(ON_ACTION)) {
			turnOn(context, manipulateAlarms);
		} else if (intent.getAction().endsWith(OFF_ACTION)) {
			turnOff(context, manipulateAlarms);
		}
	}
	
	private void turnOn(Context context, String manipulateAlarms) {
		if (! manipulateAlarms.equals(context.getString(R.string.SOUND_NOCHANGE))) {
			
			AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			
			if (lastVolume == null)
				lastVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
			
			audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			audioManager.setStreamVolume(AudioManager.STREAM_RING, lastVolume, 0);
		}
	}
	
	private void turnOff(Context context, String manipulateAlarms) {
		if (! manipulateAlarms.equals(context.getString(R.string.SOUND_NOCHANGE))) {
			AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			
			lastVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
			
			if (manipulateAlarms.equals(context.getString(R.string.SOUND_DISABLED))) {
				audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			} else if (manipulateAlarms.equals(context.getString(R.string.SOUND_VIBRO))) {
				audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			}
		}
	}

}
