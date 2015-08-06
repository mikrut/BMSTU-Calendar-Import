package ru.bmstu.schedule.models;

import java.lang.reflect.Array;
import java.util.Calendar;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.bmstu.schedule.calendar.Event;
import android.support.annotation.Nullable;
import android.util.Log;

public class Lesson {
	public enum RepeatType {
		ALL, NUMERATOR, DENOMINATOR
	};

	public enum ActivityType {
		DISC, CUSTOM
	};
	
	public enum LessonType {
		LECTURE, SEMINAR;
		
		@Override
		public String toString() {
			if (this.equals(LECTURE))
				return lectureName;
			else
				return seminarName;
		}
	};

	private static final int[][][] timetable = { { { 8, 30 }, { 10, 05 } },
			{ { 10, 15 }, { 11, 50 } }, { { 12, 00 }, { 13, 35 } },
			{ { 13, 50 }, { 15, 25 } }, { { 15, 40 }, { 17, 15 } },
			{ { 17, 25 }, { 19, 00 } }, { { 19, 10 }, { 20, 45 } } };

	private int pairIndex = 0;
	private RepeatType repeatType = RepeatType.ALL;
	private int wday = 0;// DayOfWeek wday;
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
	private static String lectureName = "Lecture";

	public static String getLectureName() {
		return lectureName;
	}

	public static void setLectureName(String lectureName) {
		Lesson.lectureName = lectureName;
	}

	public static String getSeminarName() {
		return seminarName;
	}

	public static void setSeminarName(String seminarName) {
		Lesson.seminarName = seminarName;
	}

	private static String seminarName = "Seminar";

	public Lesson(JSONObject lessonJSON) throws JSONException {
		if (lessonJSON.has("time"))
			pairIndex = Integer.valueOf(lessonJSON.getJSONArray("time")
					.getString(0));

		repeatType = getTerm(lessonJSON);
		if (lessonJSON.has("wday"))
			wday = lessonJSON.getInt("wday");// DayOfWeek.of(lessonJSON.getInt("wday")
												// + 1); // + 1 is necessary to
												// cast to ISO standard

		aud = Lesson.<Auditorium> extractClass(lessonJSON, "aud",
				Auditorium.class);
		pub = Lesson.<Lecturer> extractClass(lessonJSON, "pub", Lecturer.class);

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
			groups = Lesson.<Group> extractClass(lessonJSON, "groups",
					Group.class);
	}

	private static RepeatType getTerm(JSONObject lessonJSON) {
		// term ::= 17-all | 17-w1 | 17-w2
		// i.e. ALL, NUMERATOR (ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½),
		// DENOMINATOR (ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½)
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
			Log.e("Get Term in Lesson object initialization",
					e.getLocalizedMessage());
		}
		return RepeatType.ALL;
	}

	@SuppressWarnings("unchecked")
	private static <T extends UIDgettable> T[] extractClass(
			JSONObject lessonJSON, String classJSONName, Class<T> c) {
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
			Log.e("Extract Class " + c.getCanonicalName() + " in Lesson init",
					e.getLocalizedMessage());
		}
		return res;
	}

	private static String extractName(JSONObject activity) throws JSONException {
		String[] names = { activity.getString("name1"),
				activity.getString("name2"), activity.getString("name3") };
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
		StringBuilder builder = new StringBuilder(256).append("Name: ")
				.append(getName()).append('\n').append("Location: ")
				.append(getAud()).append('\n').append("Groups: ")
				.append(getGroups()).append('\n').append("Lecturers: ")
				.append(getPub()).append('\n').append("Time: ")
				.append(pairIndex).append('\n').append("Day of week: ")
				.append(wday + 1);
		return builder.append('\n').toString();
	}

	private String buildWithDelimiters(Object[] array, String delimiter) {
		StringBuilder b = new StringBuilder(32);
		String _delimiter = "";
		if (array != null)
			for (Object o : array) {
				b.append(_delimiter).append(o.toString());
				_delimiter = delimiter;
			}
		else
			b.append(defaultUndefinedName);
		return b.toString();
	}

	public Calendar setBeginTimeForDay(Calendar day) {
		Calendar t = (Calendar) day.clone();
		t.set(Calendar.SECOND, 0);
		t.set(Calendar.MILLISECOND, 0);
		t.set(Calendar.HOUR_OF_DAY, timetable[pairIndex][0][0]);
		t.set(Calendar.MINUTE, timetable[pairIndex][0][1]);
		return t;
	}
	
	public Calendar setEndTimeForDay(Calendar day) {
		Calendar t = (Calendar) day.clone();
		t.set(Calendar.SECOND, 0);
		t.set(Calendar.MILLISECOND, 0);
		t.set(Calendar.HOUR_OF_DAY, timetable[pairIndex][1][0]);
		t.set(Calendar.MINUTE, timetable[pairIndex][1][1]);
		return t;
	}

	public Event toEvent(Calendar semesterStart, Calendar semesterEnd) {
		return toEvent(semesterStart, semesterEnd, "", "");
	}

	public interface DescriptionBuilder {
		public String build(String lecturer, String auditorium, String name,
				String groups, ActivityType actType);
	}

	public Event toEvent(Calendar semesterStart, Calendar semesterEnd,
			String organizer, DescriptionBuilder builder) {
		return toEvent(semesterStart, semesterEnd, organizer, builder.build(
				getPub(), getAud(), getName(), getGroups(), activityType));
	}

	public Event toEvent(Calendar semesterStart, Calendar semesterEnd,
			String organizer, String description) {
		// Converting SUNDAY = 1 to 6, MONDAY = 2 to 0, TUE = 3 to 1 etc.
		// to make them compatible with wday.
		int beginDayOfWeek = (semesterStart.get(Calendar.DAY_OF_WEEK) + (7 - Calendar.MONDAY)) % 7;

		int beginDayOfYear;
		beginDayOfYear = semesterStart.get(Calendar.DAY_OF_YEAR) + wday
				- beginDayOfWeek;
		if (wday < beginDayOfWeek) {
			beginDayOfYear += 7;
		}

		Calendar beginDateTime = Calendar.getInstance(
				semesterStart.getTimeZone(), Locale.getDefault());
		beginDateTime.set(Calendar.YEAR, semesterStart.get(Calendar.YEAR));
		beginDateTime.set(Calendar.DAY_OF_YEAR, beginDayOfYear);
		beginDateTime.set(Calendar.HOUR_OF_DAY, timetable[pairIndex][0][0]);
		beginDateTime.set(Calendar.MINUTE, timetable[pairIndex][0][1]);
		beginDateTime.set(Calendar.SECOND, 0);
		beginDateTime.set(Calendar.MILLISECOND, 0);

		if (repeatType == RepeatType.DENOMINATOR)
			beginDateTime.add(Calendar.WEEK_OF_YEAR, 1);

		Event e = new Event(organizer, getName(), description, semesterEnd)
				.setAllDay(false).setAvailable(false).setDtstart(beginDateTime)
				.setEventLocation("ÌÃÒÓ: " + getAud());

		if (repeatType != RepeatType.ALL)
			e.setrRuleTwoWeeks();
		return e;
	}

	public String getAud() {
		return buildWithDelimiters(aud, ", ");
	}

	public String getPub() {
		return buildWithDelimiters(pub, ", ");
	}

	public String getName() {
		if (name != null)
			return name + " (" + getLessonType() + ")";
		else
			return defaultUndefinedName;
	}
	
	public String getDisciplineName() {
		if (name != null)
			return name;
		else
			return defaultUndefinedName;
	}
	
	public LessonType getLessonType() {
		if (groups.length > 1)
			return LessonType.LECTURE;
		return LessonType.SEMINAR;
	}

	public String getStream() {
		if (stream != null)
			return stream.toString();
		else
			return defaultUndefinedName;
	}

	public String getGroups() {
		return buildWithDelimiters(groups, ", ");
	}

	public boolean hasGroup(String groupName) {
		for (Group g : groups) {
			if (g.toString().equals(groupName))
				return true;
		}
		return false;
	}
	
	public static final int[][][] getTimetable() {
		return timetable;
	}
	
	public RepeatType getRepeatType() {
		return repeatType;
	}
	
	public int getWday() {
		return wday;
	}
	
	public int getPairIndex() {
		return pairIndex;
	}
	
	public boolean isAudKnown() {
		return aud != null;
	}
	
	public boolean isTeacherKnown() {
		return pub != null;
	}
}
