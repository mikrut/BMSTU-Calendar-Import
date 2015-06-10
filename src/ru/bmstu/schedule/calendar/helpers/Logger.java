package ru.bmstu.schedule.calendar.helpers;

import android.widget.TextView;

public class Logger {
	public enum Level {LOG, INFO, ERROR};
	Level logLevel = Level.LOG;
	final TextView text;
	
	public Logger(TextView v) {
		text = v;
	}
	
	public void clear() {
		text.setText("");
	}
	
	public void setLevel(Level l) {
		logLevel = l;
	}
	
	public void log(String message) {
		log(message, Level.LOG);
	}
	
	public void log(String message, Level l) {
		if (logLevel.ordinal() >= l.ordinal()) {
			text.append(message);
			text.append("\n");
		}
	}
}
