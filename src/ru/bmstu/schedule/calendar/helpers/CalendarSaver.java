package ru.bmstu.schedule.calendar.helpers;

import java.io.InputStream;
import java.security.acl.Group;
import java.util.List;

import ru.bmstu.schedule.calendar.Event;
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
	
	private DescriptionBuilder builder = new DescriptionBuilder() {
		public String build(String lecturer, String auditorium, String name, String groups, ActivityType actType){
			return new StringBuilder(128)
			.append("�������������: ").append(lecturer).append('\n')
			.append("���������: ").append(auditorium).append('\n')
			.append("������: ").append(groups)
			.toString();
		}
	};
	
	public CalendarSaver(
			java.util.Calendar semesterStart,
			java.util.Calendar semesterEnd,
			String calendarName,
			String userName,
			ContentResolver resolver) {
		this.semesterStart = semesterStart;
		this.semesterEnd = semesterEnd;
		this.calendarName = calendarName;
		this.resolver = resolver;
		this.userName = userName;
	}
	
	public void saveToCalendarByGroupUID(InputStream jsonData, String organizerEmail, String groupUID) {
		try {
			saveToCalendar(jsonData, organizerEmail, ((Group) UIDgettable.getByUID(groupUID)).getName());
		} catch (ClassCastException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	public void saveToCalendar(InputStream jsonData, String organizerEmail, String groupName) {
		List<Lesson> ls = ModelsInitializer.readLessonsFromJSON(jsonData);
		ru.bmstu.schedule.calendar.Calendar cal = ru.bmstu.schedule.calendar.Calendar.findByName(calendarName, resolver);
		if (cal == null) {
			cal = new ru.bmstu.schedule.calendar.Calendar(calendarName, userName, resolver);
			cal.save();
		} else {
			cal.deleteAllEvents();
		}
		Log.v("SaveToCalendar", cal.toString());
		for (Lesson l : ls) {
			if (l.hasGroup(groupName)) {
				Event e = l.toEvent(semesterStart, semesterEnd, organizerEmail, builder);
				e.save(resolver, cal.getId());
			}
		}
	}
	
	public void setBuilder(DescriptionBuilder builder) {
		this.builder = builder;
	}
}
