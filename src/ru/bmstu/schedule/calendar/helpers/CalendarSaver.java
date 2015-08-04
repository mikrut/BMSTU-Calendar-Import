package ru.bmstu.schedule.calendar.helpers;

import java.io.InputStream;
import java.security.acl.Group;
import java.util.List;

import ru.bmstu.schedule.calendar.Event;
import ru.bmstu.schedule.calendar.R;
import ru.bmstu.schedule.models.Lesson;
import ru.bmstu.schedule.models.Lesson.ActivityType;
import ru.bmstu.schedule.models.Lesson.DescriptionBuilder;
import ru.bmstu.schedule.models.UIDgettable;
import android.content.ContentResolver;
import android.util.Log;

public class CalendarSaver {
	private java.util.Calendar semesterStart;
	private java.util.Calendar semesterEnd;
	private String calendarName;
	private String userName;
	private ContentResolver resolver;

	private static String teacherCall = "Teacher";
	private static String auditoriumCall = "Auditorium";
	private static String groupsCall = "Groups";

	public static String getTeacherCall() {
		return teacherCall;
	}

	public static void setTeacherCall(String teacherCall) {
		CalendarSaver.teacherCall = teacherCall;
	}

	public static String getAuditoriumCall() {
		return auditoriumCall;
	}

	public static void setAuditoriumCall(String auditoriumCall) {
		CalendarSaver.auditoriumCall = auditoriumCall;
	}

	public static String getGroupsCall() {
		return groupsCall;
	}

	public static void setGroupsCall(String groupsCall) {
		CalendarSaver.groupsCall = groupsCall;
	}

	private DescriptionBuilder builder = new DescriptionBuilder() {
		public String build(String lecturer, String auditorium, String name,
				String groups, ActivityType actType) {
			return new StringBuilder(128).append(teacherCall + ": ")
					.append(lecturer).append('\n')
					.append(auditoriumCall + ": ").append(auditorium)
					.append('\n').append(groupsCall + ": ").append(groups)
					.toString();
		}
	};

	public CalendarSaver(java.util.Calendar semesterStart,
			java.util.Calendar semesterEnd, String calendarName,
			String userName, ContentResolver resolver) {
		this.semesterStart = semesterStart;
		this.semesterEnd = semesterEnd;
		this.calendarName = calendarName;
		this.resolver = resolver;
		this.userName = userName;
	}

	public void saveToCalendarByGroupUID(List<Lesson> lessons,	String organizerEmail) {
		saveToCalendar(lessons, organizerEmail);
	}

	public void saveToCalendar(List<Lesson> lessons, String organizerEmail) {
		ru.bmstu.schedule.calendar.Calendar cal = ru.bmstu.schedule.calendar.Calendar
				.findByName(calendarName, resolver);
		if (cal == null) {
			cal = new ru.bmstu.schedule.calendar.Calendar(calendarName,
					userName, resolver);
			cal.save();
		} else {
			cal.deleteAllEvents();
		}
		Log.v("SaveToCalendar", cal.toString());
		for (Lesson l : lessons) {
			Event e = l.toEvent(semesterStart, semesterEnd, organizerEmail,	builder);
			e.save(resolver, cal.getId());
		}
	}

	public void setBuilder(DescriptionBuilder builder) {
		this.builder = builder;
	}
}
