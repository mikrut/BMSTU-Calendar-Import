package ru.bmstu.schedule.models;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
//import java.time.DayOfWeek;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Lesson {
	public enum RepeatType {ALL, NUMERATOR, DENOMINATOR};
	public enum ActivityType {DISC, CUSTOM};
	
	private int pairIndex;
	private RepeatType term = RepeatType.ALL;
	private int wday;//DayOfWeek wday;	
	private Auditorium[] aud;
	private Lecturer[] pub;
	private ActivityType activityType = ActivityType.DISC;
	private String name;
	private Stream stream;
	private Group[] groups;
	
	public Lesson(JSONObject lessonJSON) throws JSONException {
		pairIndex = Integer.valueOf(lessonJSON.getJSONArray("time").getString(0));
		term = getTerm(lessonJSON);				
		wday = lessonJSON.getInt("wday");//DayOfWeek.of(lessonJSON.getInt("wday") + 1); // + 1 is necessary to cast to ISO standard
		
		aud = Lesson.<Auditorium>extractClass(lessonJSON, "aud", Auditorium.class);
		pub = Lesson.<Lecturer>extractClass(lessonJSON, "pub", Lecturer.class);		
				
		JSONObject activity = lessonJSON.getJSONObject("activity");
		String actType = activity.getString("type");
		if (actType.equals("custom"))
			activityType = ActivityType.CUSTOM;
				
		name = extractName(activity);
		
		stream = (Stream) Stream.getByUID(lessonJSON.getString("stream"));
		groups = Lesson.<Group>extractClass(lessonJSON, "groups", Group.class);
	}
	
	private static RepeatType getTerm(JSONObject lessonJSON) throws JSONException {
		// term = 17-all | 17-w1 | 17-w2
		// i.e. ALL, NUMERATOR (неделя числителя), DENOMINATOR (абаз знаменателя)
		switch (lessonJSON.getString("term").charAt(5)) {
		case '1':
			return RepeatType.NUMERATOR;
		case '2':
			return RepeatType.DENOMINATOR;
		default:
			return RepeatType.ALL;
		}
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends UIDgettable> T[] extractClass(
			JSONObject lessonJSON,
			String classJSONName,
			Class<T> c) throws JSONException {
		JSONArray arr = lessonJSON.getJSONArray(classJSONName);
		T[] res = (T[]) Array.newInstance(c, arr.length());
		for (int i = 0; i < arr.length(); i++) {
			res[i] = (T) T.getByUID(arr.getString(i));
		}
		return res;
	}
	
	private static String extractName(JSONObject activity) throws JSONException {
		String[] names = {
				activity.getString("name1"),
				activity.getString("name2"),
				activity.getString("name3")
				};
		int maxLen = names[0].length();
		String longestName = names[0];
		for (int i = 1; i < names.length; i++) {
			if (names[i].length() > maxLen) {
				maxLen = names[i].length();
				longestName = names[i];
			}
		}
		return longestName;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder()
		.append("Name: ").append(name).append('\n')
		.append("Location: ").append(aud[0].toString()).append('\n')
		.append("Groups: ");
		String delimiter = "";
		for (Group g: groups) {
			builder.append(delimiter).append(g.toString());
			delimiter = ", ";
		}
		builder.append('\n')
		.append("Lecturers: ");
		delimiter = "";
		for (Lecturer l: pub) {
			builder.append(delimiter).append(l.toString());
			delimiter = ", ";
		}
		return builder.append('\n').toString();
	}
}
