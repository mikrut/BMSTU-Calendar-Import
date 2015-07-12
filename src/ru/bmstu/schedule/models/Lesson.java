package ru.bmstu.schedule.models;

import java.lang.reflect.Array;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
//import java.time.DayOfWeek;









import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.bmstu.schedule.calendar.Event;
import android.support.annotation.Nullable;
import android.text.format.Time;
import android.util.Log;

public class Lesson {
	public enum RepeatType {ALL, NUMERATOR, DENOMINATOR};
	public enum ActivityType {DISC, CUSTOM};
	
	private static final int[][][] timetable = {
		{{ 8, 30}, {10, 05}},
		{{10, 15}, {11, 50}},
		{{12, 00}, {13, 35}},
		{{13, 50}, {15, 25}},
		{{15, 40}, {17, 15}},
		{{17, 25}, {19, 00}},
		{{19, 10}, {20, 45}}
	};
	
	private int pairIndex = 0;
	private RepeatType term = RepeatType.ALL;
	private int wday = 0;//DayOfWeek wday;
	@Nullable
	private Auditorium[] aud = null;
	@Nullable
	private Lecturer[] pub = null;
	private ActivityType activityType = ActivityType.DISC;
	@Nullable
	private String name = null;
	@Nullable
	private Stream stream = null;
	@Nullable
	private Group[] groups = null;
	
	private String defaultUndefinedName = "Unknown";
	
	public Lesson(JSONObject lessonJSON) throws JSONException {
		if (lessonJSON.has("time"))
			pairIndex = Integer.valueOf(lessonJSON.getJSONArray("time").getString(0));
		
		term = getTerm(lessonJSON);
		if (lessonJSON.has("wday"))
			wday = lessonJSON.getInt("wday");//DayOfWeek.of(lessonJSON.getInt("wday") + 1); // + 1 is necessary to cast to ISO standard
		
		aud = Lesson.<Auditorium>extractClass(lessonJSON, "aud", Auditorium.class);
		pub = Lesson.<Lecturer>extractClass(lessonJSON, "pub", Lecturer.class);		
		
		if (lessonJSON.has("activity")) {
			JSONObject activity = lessonJSON.getJSONObject("activity");
			String actType = activity.getString("type");
			if (actType.equals("custom"))
				activityType = ActivityType.CUSTOM;
					
			name = extractName(activity);
		}
		
		if (lessonJSON.has("stream"))
			stream = (Stream) Stream.getByUID(lessonJSON.getString("stream"));
		if (lessonJSON.has("groups"))
			groups = Lesson.<Group>extractClass(lessonJSON, "groups", Group.class);
	}
	
	private static RepeatType getTerm(JSONObject lessonJSON) {
		// term ::= 17-all | 17-w1 | 17-w2
		// i.e. ALL, NUMERATOR (неделя числителя), DENOMINATOR (абаз знаменателя)
		try {
			if (lessonJSON.has("term"))
				switch (lessonJSON.getString("term").charAt(4)) {
				case '1':
					return RepeatType.NUMERATOR;
				case '2':
					return RepeatType.DENOMINATOR;
				default:
					return RepeatType.ALL;
				}
		} catch (JSONException e) {
			Log.e("Get Term in Lesson object initialization", e.getLocalizedMessage());
		}
		return RepeatType.ALL;
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends UIDgettable> T[] extractClass(
			JSONObject lessonJSON,
			String classJSONName,
			Class<T> c) {
		T[] res = null;
		try {
			if (lessonJSON.has(classJSONName)) {
				JSONArray arr = lessonJSON.getJSONArray(classJSONName);
				res = (T[]) Array.newInstance(c, arr.length());
				
				for (int i = 0; i < arr.length(); i++) {
					res[i] = (T) T.getByUID(arr.getString(i));
				}
			}
		} catch (JSONException e) {
			Log.e("Extract Class "+c.getCanonicalName()+" in Lesson init", e.getLocalizedMessage());
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
		StringBuilder builder = new StringBuilder(256)
		.append("Name: ").append(getName()).append('\n')		
		.append("Location: ").append(getAud()).append('\n')
		.append("Groups: ").append(getGroups()).append('\n')
		.append("Lecturers: ").append(getPub()).append('\n')
		.append("Time: ").append(pairIndex).append('\n')
		.append("Day of week: ").append(wday+1);
		return builder.append('\n').toString();
	}
	
	private String buildWithDelimiters(Object[] array, String delimiter) {
		StringBuilder b = new StringBuilder(32);
		String _delimiter = "";
		if (array != null) for (Object o : array) {
			b.append(_delimiter).append(o.toString());
			_delimiter = delimiter;
		} else b.append(defaultUndefinedName);
		return b.toString();
	}
	
	public Time getDTime(Calendar d) {
		Time t = new Time();
		t.set(0, timetable[pairIndex][0][1], timetable[pairIndex][0][0],
				d.get(Calendar.DAY_OF_MONTH),
				d.get(Calendar.MONTH),
				d.get(Calendar.YEAR));
		return t;
	}
	
	public Event toEvent(Calendar semesterStart, Calendar semesterEnd) {
		return toEvent(semesterStart, semesterEnd, "", "");
	}
	
	public interface DescriptionBuilder {
		public String build (String lecturer, String auditorium, String name, String groups, ActivityType actType);
	}
	
	public Event toEvent(Calendar semesterStart, Calendar semesterEnd, String organizer, DescriptionBuilder builder) {
		return toEvent(semesterStart, semesterEnd, organizer, builder.build(getPub(), getAud(), getName(), getGroups(), activityType));
	}
	
	public Event toEvent(Calendar semesterStart, Calendar semesterEnd, String organizer, String description) {
		// Converting SUNDAY = 1 to 6, MONDAY = 2 to 0, TUE = 3 to 1 etc.
		// to make them compatible with wday.
		int beginDayOfWeek = (semesterStart.get(Calendar.DAY_OF_WEEK)+(7-Calendar.MONDAY))%7;
		int endDayOfWeek = (semesterEnd.get(Calendar.DAY_OF_WEEK)+(7-Calendar.MONDAY))%7;
		
		int beginDayOfYear;
		beginDayOfYear = semesterStart.get(Calendar.DAY_OF_YEAR)+wday-beginDayOfWeek;
		if (wday < beginDayOfWeek) {
			beginDayOfYear += 7;
			if (term == RepeatType.NUMERATOR)
				beginDayOfYear += 7;
		}
		
		Calendar beginDateTime = Calendar.getInstance(semesterStart.getTimeZone(), Locale.getDefault());
		beginDateTime.set(Calendar.YEAR,        semesterStart.get(Calendar.YEAR));
		beginDateTime.set(Calendar.DAY_OF_YEAR, beginDayOfYear);
		beginDateTime.set(Calendar.HOUR_OF_DAY, timetable[pairIndex][0][0]);
		beginDateTime.set(Calendar.MINUTE,      timetable[pairIndex][0][1]);
		beginDateTime.set(Calendar.SECOND,      0);
		beginDateTime.set(Calendar.MILLISECOND, 0);
		
		if (term == RepeatType.DENOMINATOR)
			beginDateTime.add(Calendar.WEEK_OF_YEAR, 1);
		
		Event e = new Event(organizer, getName(), description, semesterEnd)
		.setAllDay(false)
		.setAvailable(false)
		.setDtstart(beginDateTime)
		.setEventLocation("BMSTU: "+getAud());
		
		if (term != RepeatType.ALL)
			e.setrRuleTwoWeeks();
		if (wday < 2)
			Log.i("toEvent", beginDateTime.getTime().toLocaleString());//
		return e;
	}

	public String getAud() {
		return buildWithDelimiters(aud, ", ");
	}

	public String getPub() {
		return buildWithDelimiters(pub, ", ");
	}

	public String getName() {
		String postfix = "Семинар";
		if (groups.length > 1)
			postfix = "Лекция";
		if (name != null) return name + " (" + postfix + ")";
		else return defaultUndefinedName;
	}

	public String getStream() {
		if (stream != null) return stream.toString();
		else return defaultUndefinedName;
	}

	public String getGroups() {
		return buildWithDelimiters(groups, ", ");
	}
	
	public boolean hasGroup(String groupName) {
		for (Group g: groups) {
			if (g.toString().equals(groupName))
				return true;
		}
		return false;
	}
}
