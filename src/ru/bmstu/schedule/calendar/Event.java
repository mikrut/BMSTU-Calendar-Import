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
import android.util.Log;



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
	
	private static String defaultLocation = "BMSTU";
	private static TimeZone defaultTimezone = TimeZone.getDefault();
	
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
	
	private static final int PROJECTION_ID_INDEX = 0;
	private static final int PROJECTION_CALENDAR_ID_INDEX = 1;
	private static final int PROJECTION_ORGANIZER_INDEX = 2;
	private static final int PROJECTION_TITLE_INDEX = 3;
	private static final int PROJECTION_DESCRIPTION_INDEX = 4;
	private static final int PROJECTION_DTSTART_INDEX = 5;
	private static final int PROJECTION_DURATION_INDEX = 6;
	private static final int PROJECTION_EVENT_LOCATION_INDEX = 7;
	private static final int PROJECTION_EVENT_TIMEZONE_INDEX = 8;
	private static final int PROJECTION_ALL_DAY_INDEX = 9;
	private static final int PROJECTION_RRULE_INDEX = 10;
	private static final int PROJECTION_AVAILABILITY_INDEX = 11;
	
	private static final String[] EVENT_BASIC_PROJECTION = {
		Events._ID,
		Events.CALENDAR_ID,
		Events.ORGANIZER,
		Events.TITLE,
		Events.DTSTART
	};
	
	private static final int BASIC_PROJECTION_ID_INDEX = 0;
	private static final int BASIC_PROJECTION_CALENDAR_ID_INDEX = 1;
	private static final int BASIC_PROJECTION_ORGANIZER_INDEX = 2;
	private static final int BASIC_PROJECTION_TITLE_INDEX = 3;
	private static final int BASIC_PROJECTION_DTSTART_INDEX = 4;
	
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
	
	public static void setDefaultLocation(String loc) {
		defaultLocation = loc;
	}
	
	public static void setDefaultTimezone(TimeZone tz) {
		defaultTimezone = tz;
	}
	
	public Event(String organiser, String title, String description) {
		this.organiser = organiser;
		this.title = title;
		this.description = description;
		
		Time yearEnd = new Time();
		yearEnd.set(0, 0, 0, 1, 6, 2016);
		
		Time dt = new Time();
		Date now = new Date();
		
		dt.set(now.getSeconds(), now.getMinutes(), now.getHours(),
				now.getDate(), now.getMonth(), now.getYear()+1900);
		Log.v("now:", dt.toString());
		Log.v("ms:", Long.toString(dt.toMillis(false)));
		
		this.setDtstart(dt)
			.setDuration(getOnePairDuration())
			.setEventLocation(defaultLocation)
			.setEventTimezone(defaultTimezone)
			.setAllDay(false)
			.setrRule("FREQ=WEEKLY;UNTIL="+yearEnd.format2445())
			.setAvailable(false);
	}
	
	public void save(ContentResolver resolver, long l) {
		save(resolver, Long.toString(l));
	}
	
	public void save(ContentResolver resolver, String calendarId) {
		Cursor cur = null;
		try {
			cur = findExising(resolver, calendarId);
		} catch (Exception e) {
			Log.e("Event.save try/catch", e.getMessage());
		}
		
		ContentValues mDataValues = new ContentValues();
		
		mDataValues.put(Events.CALENDAR_ID, calendarId);
		mDataValues.put(Events.ORGANIZER, organiser);
		mDataValues.put(Events.TITLE, title);
		mDataValues.put(Events.DESCRIPTION, description);
		mDataValues.put(Events.DTSTART, dtstart.toMillis(false));
		mDataValues.put(Events.DURATION, duration);
		mDataValues.put(Events.EVENT_LOCATION, eventLocation);
		mDataValues.put(Events.EVENT_TIMEZONE, eventTimezone.getID());
		mDataValues.put(Events.ALL_DAY, allDay);
		mDataValues.put(Events.RRULE, rRule);
		mDataValues.put(Events.AVAILABILITY, available);
		
		if (null != cur && cur.getCount() > 0) {
			cur.moveToFirst();
			mDataValues.put(Events._ID, cur.getString(BASIC_PROJECTION_ID_INDEX));
			cur.close();
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
	
	private Cursor findExising(ContentResolver resolver, String calendarId) throws Exception {
		String query = new StringBuilder()
		.append('(').append(Events.CALENDAR_ID).append(" = ?").append(')')
		.append(" AND ")
		.append('(').append(Events.TITLE).append(" = ?").append(')')
		.toString();
		
		String[] mSelectionArgs = {	calendarId, title};
		Cursor c = resolver.query(Events.CONTENT_URI, EVENT_BASIC_PROJECTION,
				query, mSelectionArgs, null);
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
	
	public Event setrRuleOnce() {
		this.rRule = "FREQ=DAILY;COUNT=2";
		return this;
	}

	public boolean isAvailable() {
		return available;
	}

	public Event setAvailable(boolean available) {
		this.available = available;
		return this;
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(" == ").append(title).append(" == ").append('\n')
		.append("Organiser: ").append(organiser).append('\n')
		.append(description).append('\n')
		.append("Time: ").append(dtstart)
		.append(" === \n");
		return b.toString();
	}
}
