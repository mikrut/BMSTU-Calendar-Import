package ru.bmstu.schedule.calendar;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.text.format.Time;



public class Event {
	private String organiser;
	private String title;
	private String description;
	private Time dtstart;
	private String duration;
	private String eventLocation;
	private TimeZone eventTimezone;
	private boolean allDay;
	private String rRule;
	private boolean available;
	
	private final static String DEFAULT_LOCATION = "BMSTU";
	private final static TimeZone DEFAULT_TIMEZONE = TimeZone.getDefault();
	
	private static final String[] EVENT_PROJECTION = {
		Events._ID,
		Events.CALENDAR_ID,
		Events.ORGANIZER,
		Events.TITLE,
		Events.DESCRIPTION,
		Events.DTSTART,
		Events.DURATION,
		Events.EVENT_LOCATION,
		Events.EVENT_TIMEZONE,
		Events.ALL_DAY,
		Events.RRULE,
		Events.AVAILABILITY
	};
	
	private static final String[] EVENT_BASIC_PROJECTION = {
		Events._ID,
		Events.CALENDAR_ID,
		Events.ORGANIZER,
		Events.TITLE,
		Events.DTSTART
	};
	
	private static String rfc2445Duration(int d, int h, int m, int s) {
		StringBuilder builder = new StringBuilder();
		builder.append('P').append(d).append('D')
		.append('T')
		.append(h).append('H')
		.append(m).append('M')
		.append(s).append('S');
		return builder.toString();
	}
	
	private static String rfc2445Duration(int h, int m, int s) {
		StringBuilder builder = new StringBuilder();
		builder.append('P').append('T')
		.append(h).append('H')
		.append(m).append('M')
		.append(s).append('S');
		return builder.toString();
	}
	
	private static String rfc2445Duration(Time start, Time end) {
		long diff = end.toMillis(false) - start.toMillis(false);
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(diff);
		Date t = calendar.getTime();
		return rfc2445Duration(t.getHours(), t.getMinutes(), t.getSeconds());
	}
	
	private static String getOnePairDuration() {
		return rfc2445Duration(1, 35, 0);
	}
	
	public Event(String organiser, String title, String description) {
		this.organiser = organiser;
		this.title = title;
		this.description = description;
		
		Time yearEnd = new Time();
		yearEnd.set(0, 0, 0, 1, 6, 2016);
		
		this.setDtstart(new Time())
			.setDuration(getOnePairDuration())
			.setEventLocation(DEFAULT_LOCATION)
			.setEventTimezone(DEFAULT_TIMEZONE)
			.setAllDay(false)
			.setrRule("FREQ=WEEKLY;UNTIL="+yearEnd.format2445())
			.setAvailable(false);
	}
	
	public void save(ContentResolver resolver, int calendarId) {
		Cursor cur = null;
		try {
			cur = findExising(resolver, calendarId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ContentValues mDataValues = new ContentValues();
		
		mDataValues.put(Events.CALENDAR_ID, calendarId);
		mDataValues.put(Events.ORGANIZER, organiser);
		mDataValues.put(Events.TITLE, title);
		mDataValues.put(Events.DESCRIPTION, description);
		mDataValues.put(Events.DTSTART, dtstart.format2445());
		mDataValues.put(Events.DURATION, duration);
		mDataValues.put(Events.EVENT_LOCATION, eventLocation);
		mDataValues.put(Events.EVENT_TIMEZONE, eventTimezone.getDisplayName());
		mDataValues.put(Events.ALL_DAY, allDay);
		mDataValues.put(Events.RRULE, rRule);
		mDataValues.put(Events.AVAILABILITY, available);
		
		if (null != cur && cur.getCount() > 0) {
			mDataValues.put(Events._ID, cur.getString(0));
			update(resolver, mDataValues);
		} else {
			insert(resolver, mDataValues);
		}
	}
	
	private static void update(ContentResolver resolver, ContentValues values) {
		String mSelectionClause = Events._ID + " = ?";
		String[] mSelectionArgs = {values.getAsString(Events._ID)};
		resolver.update(Events.CONTENT_URI, values, mSelectionClause, mSelectionArgs);
	}
	
	private static void insert(ContentResolver resolver, ContentValues values) {
		resolver.insert(Events.CONTENT_URI, values);
	}
	
	private Cursor findExising(ContentResolver resolver, int calendarId) throws Exception {
		String[] mSelectionArgs = {
			Integer.toString(calendarId)
		};
		Cursor c = resolver.query(Events.CONTENT_URI, EVENT_BASIC_PROJECTION,
				Events.CALENDAR_ID+" = ?", mSelectionArgs, null);
		if (null == c)
			throw new Exception("Null query result");
		return c;
	}

	public String getOrganiser() {
		return organiser;
	}

	public Event setOrganiser(String organiser) {
		this.organiser = organiser;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public Event setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Event setDescription(String description) {
		this.description = description;
		return this;
	}

	public Time getDtstart() {
		return dtstart;
	}

	public Event setDtstart(Time dtstart) {
		this.dtstart = dtstart;
		return this;
	}

	public String getDuration() {
		return duration;
	}

	public Event setDuration(String duration) {
		this.duration = duration;
		return this;
	}

	public String getEventLocation() {
		return eventLocation;
	}

	public Event setEventLocation(String eventLocation) {
		this.eventLocation = eventLocation;
		return this;
	}

	public TimeZone getEventTimezone() {
		return eventTimezone;
	}

	public Event setEventTimezone(TimeZone eventTimezone) {
		this.eventTimezone = eventTimezone;
		return this;
	}

	public boolean isAllDay() {
		return allDay;
	}

	public Event setAllDay(boolean allDay) {
		this.allDay = allDay;
		return this;
	}

	public String getrRule() {
		return rRule;
	}

	public Event setrRule(String rRule) {
		this.rRule = rRule;
		return this;
	}

	public boolean isAvailable() {
		return available;
	}

	public Event setAvailable(boolean available) {
		this.available = available;
		return this;
	}
	
}