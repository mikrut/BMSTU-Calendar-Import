package ru.bmstu.schedule.invisible;

import ru.bmstu.schedule.calendar.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;

public class SoundManager extends BroadcastReceiver {
	public final static String ON_ACTION = SoundManager.class.getCanonicalName()+".TURN_ON";
	public final static String OFF_ACTION = SoundManager.class.getCanonicalName()+".TURN_OFF";
	
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
			
			int volume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
			
			audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			audioManager.setStreamVolume(AudioManager.STREAM_RING, volume, 0);
		}
	}
	
	private void turnOff(Context context, String manipulateAlarms) {
		if (! manipulateAlarms.equals(context.getString(R.string.SOUND_NOCHANGE))) {
			AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			
			if (manipulateAlarms.equals(context.getString(R.string.SOUND_DISABLED))) {
				audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			} else if (manipulateAlarms.equals(context.getString(R.string.SOUND_VIBRO))) {
				audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			}
		}
	}

}
