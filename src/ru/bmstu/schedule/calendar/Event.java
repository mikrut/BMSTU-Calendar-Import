package ru.bmstu.schedule.calendar;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.provider.CalendarContract.Events;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import android.util.Log;

@SuppressWarnings("unused")
public class Event {
	private String organiser;
	private String title;
	private String description;
	private Calendar dtstart;
	private String duration;
	private String eventLocation;
	private TimeZone eventTimezone;
	private boolean allDay;
	private String rRule;
	private boolean available;
	
	private String yearEndRFC2445;
	
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
	
	private static String rfc2445Duration(int h, int m, int s) {
		StringBuilder builder = new StringBuilder();
		builder.append('P').append('T')
		.append(h).append('H')
		.append(m).append('M')
		.append(s).append('S');
		return builder.toString();
	}
	
	private static String rfc2445Duration(Date start, Date end) {
		long diff = end.getTime() - start.getTime();
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(diff);
		return rfc2445Duration(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
	}
	
	private static final DateFormat rfc2445DateTime = new SimpleDateFormat("yyyyMMdd'T'hhmmss", new Locale("ru"));
	
	private static String getOnePairDuration() {
		return rfc2445Duration(1, 35, 0);
	}
	
	public static void setDefaultLocation(String loc) {
		defaultLocation = loc;
	}
	
	public static void setDefaultTimezone(TimeZone tz) {
		defaultTimezone = tz;
	}
	
	public Event(String organiserEmail, String title, String description, Calendar yearEnd) {
		this.organiser = organiserEmail;
		this.title = title;
		this.description = description;
				
		Calendar now = Calendar.getInstance();
		yearEndRFC2445 = rfc2445DateTime.format(yearEnd.getTime());
		
		this.setDtstart(now)
			.setDuration(getOnePairDuration())
			.setEventLocation(defaultLocation)
			.setEventTimezone(defaultTimezone)
			.setAllDay(false)
			.setrRule("FREQ=WEEKLY;UNTIL="+yearEndRFC2445)
			.setAvailable(false);
	}
	
	public void save(ContentResolver resolver, long l) {
		save(resolver, Long.toString(l));
	}
	
	public void save(ContentResolver resolver, String calendarId) {
		ContentValues mDataValues = new ContentValues();
		
		mDataValues.put(Events.CALENDAR_ID, calendarId);
		mDataValues.put(Events.ORGANIZER, organiser);
		mDataValues.put(Events.TITLE, title);
		mDataValues.put(Events.DESCRIPTION, description);
		mDataValues.put(Events.DTSTART, dtstart.getTimeInMillis());
		mDataValues.put(Events.DURATION, duration);
		mDataValues.put(Events.EVENT_LOCATION, eventLocation);
		mDataValues.put(Events.EVENT_TIMEZONE, eventTimezone.getID());
		mDataValues.put(Events.ALL_DAY, allDay ? 1 : 0);
		mDataValues.put(Events.RRULE, rRule);
		mDataValues.put(Events.AVAILABILITY, available);
		
		insert(resolver, mDataValues);
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

	public Calendar getDtstart() {
		return dtstart;
	}

	public Event setDtstart(Calendar dtstart) {
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
	
	public Event setrRuleTwoWeeks() {
		this.rRule = "FREQ=WEEKLY;UNTIL="+yearEndRFC2445+";INTERVAL=2";
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
		.append("Time: ").append(dtstart.getTime()).append('\n')
		.append("TInMills: ").append(dtstart.getTimeInMillis()).append('\n')
		.append("For Time: ").append(dtstart.getTime().getTime()).append('\n')
		.append("rRule: ").append(rRule).append('\n')
		.append(" === \n");
		return b.toString();
	}
}
