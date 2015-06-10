package ru.bmstu.schedule.calendar.helpers;

import android.util.Log;
import android.widget.TextView;

public class Logger {
	public enum Level {LOG, INFO, ERROR};
	private Level defaultLevel = Level.LOG;
	private Level logLevel = Level.LOG;
	final TextView text;
	
	public Logger(TextView v) {
		text = v;
	}
	
	public void clear() {
		text.setText("");
	}
	
	public void setLevel(Level l) {
		if (l != null)
			logLevel = l;
	}
	
	public void setDefaultLevel(Level l) {
		if (l != null)
			defaultLevel = l;
	}
	
	public void log(Object obj) {
		log(obj, defaultLevel);
	}
	
	public void log(Object obj, Level l) {
		if (logLevel.ordinal() >= l.ordinal()) {
			String message = "null";
			if (obj != null)
				message = obj.toString();
			text.append(message);
			text.append("\n");
			if (l == null)
				l = defaultLevel;
			
			switch(l) {
			case LOG:
				Log.v("Logger", message);
				break;
			case INFO:
				Log.i("Logger", message);
				break;
			case ERROR:
				Log.e("Logger", message);
				break;
			}
			
		}
	}
}
