package ru.bmstu.schedule.calendar;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;

public class Calendar {
	private long    id;
	private String  name;
	private String  accountName;
	private String  accountType;
	private int     calendarColor;
	private String  calendarAccessLevel;
	private String  ownerAccount;
	private boolean visible;
	private boolean syncEvents;
	private int     accessLevel;
	
	private ContentResolver resolver;
	
	private static boolean defaultVisible = true;
	private static boolean defaultSyncEvents = true;
	private static int     defaultColor = Color.RED;
	
	private static String[] CALENDAR_PROJECTION = {
		Calendars.NAME,
		Calendars.VISIBLE,
		Calendars.SYNC_EVENTS,
		Calendars.ACCOUNT_NAME,
		Calendars.ACCOUNT_TYPE,
		Calendars.CALENDAR_COLOR,
		Calendars.CALENDAR_ACCESS_LEVEL,
		Calendars.OWNER_ACCOUNT,
		Calendars._ID
	};
	
	private static final int PROJECTION_NAME_INDEX = 0;
	private static final int PROJECTION_VISIBLE_INDEX = 1;
	private static final int PROJECTION_SYNC_EVENTS_INDEX = 2;
	private static final int PROJECTION_ACCOUNT_NAME_INDEX = 3;
	private static final int PROJECTION_ACCOUNT_TYPE_INDEX = 4;
	private static final int PROJECTION_CALENDAR_COLOR_INDEX = 5;
	private static final int PROJECTION_ACCESS_LEVEL_INDEX = 6;
	private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 7;
	private static final int PROJECTION_ID_INDEX = 8;
		
	public Calendar(String name, String accName, ContentResolver resolver) {
		this.name = name;
		visible = defaultVisible;
		syncEvents = defaultSyncEvents;
		accountName = accName;
		accountType = "com.google";
		calendarColor = defaultColor;
		ownerAccount = accName;
		accessLevel = CalendarContract.Calendars.CAL_ACCESS_OWNER;
		this.resolver = resolver;
	}
	
	public void save() {
		Calendar c = findByName(name, resolver);
		ContentValues mDataValues = new ContentValues();
		
		mDataValues.put(Calendars.NAME, name);
		mDataValues.put(Calendars.CALENDAR_DISPLAY_NAME, name);
		mDataValues.put(Calendars.VISIBLE, visible);
		mDataValues.put(Calendars.SYNC_EVENTS, syncEvents);
		mDataValues.put(Calendars.ACCOUNT_NAME, accountName);
		mDataValues.put(Calendars.ACCOUNT_TYPE, accountType);
		mDataValues.put(Calendars.CALENDAR_COLOR, calendarColor);
		mDataValues.put(Calendars.OWNER_ACCOUNT, ownerAccount);
		
		if (null == c)
			insert(mDataValues);
		else
			update(mDataValues);
	}

	private void insert(ContentValues values) {
		Uri calUri = Calendars.CONTENT_URI.buildUpon()
				.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
				.appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, accountName)
				.appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, "com.google")
				.build();
		Uri newRow = resolver.insert(calUri, values);
		this.setId(ContentUris.parseId(newRow));
	}
	
	private void update(ContentValues values) {
		Uri calUri = Calendars.CONTENT_URI.buildUpon()
				.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
				.appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, accountName)
				.appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, "com.google")
				.build();
		String query = "(" + Calendars.NAME + " = ?) AND ("+Calendars.OWNER_ACCOUNT+" = ? ) AND ("+Calendars.ACCOUNT_NAME+" = ?)";
		String[] args = {name, ownerAccount, accountName};
		resolver.update(calUri, values, query, args);
	}
	
	public static int deleteByName(String name, String account, ContentResolver resolver) {
		Uri calUri = Calendars.CONTENT_URI.buildUpon()
				.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
				.appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, account)
				.appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, "com.google")
				.build();
		String query = "(" + Calendars.NAME + " = ?)";
		String[] args = {name};
		return resolver.delete(calUri, query, args);
	}
	
	public void deleteAllEvents() {
		String mSelectionClause = Events.CALENDAR_ID + " = ?";
		String[] mSelectionArgs = {String.valueOf(getId())};
		resolver.delete(Events.CONTENT_URI, mSelectionClause, mSelectionArgs);
	}
	
	public static Calendar findByName(String name, ContentResolver resolver) {
		String query = Calendars.NAME + " = ?";
		String[] mSelectionArgs = {	name };
		Cursor cur = resolver.query(Calendars.CONTENT_URI, CALENDAR_PROJECTION, query, mSelectionArgs, null);
		if (null == cur || cur.getCount() <= 0)
			return null;
		cur.moveToFirst();
		Calendar cal =  new Calendar(cur.getString(PROJECTION_NAME_INDEX), cur.getString(PROJECTION_ACCOUNT_NAME_INDEX), resolver)
			.setVisible(cur.getString(PROJECTION_VISIBLE_INDEX).equals("1"))
			.setSyncEvents(cur.getString(PROJECTION_SYNC_EVENTS_INDEX).equals("2"))
			.setAccountType(cur.getString(PROJECTION_ACCOUNT_TYPE_INDEX))
			.setCalendarColor(cur.getInt(PROJECTION_CALENDAR_COLOR_INDEX))
			.setAccessLevel(cur.getInt(PROJECTION_ACCESS_LEVEL_INDEX))
			.setOwnerAccount(cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX))
			.setId(cur.getInt(PROJECTION_ID_INDEX));
		cur.close();
		return cal;
	}
	
	public Calendar setName(String name) {
		this.name = name;
		return this;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Calendar setVisible(boolean vis) {
		visible = vis;
		return this;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public Calendar setSyncEvents(boolean sync) {
		syncEvents = sync;
		return this;
	}
	
	public boolean getSyncEvents() {
		return syncEvents;
	}

	public String getAccountName() {
		return accountName;
	}

	public Calendar setAccountName(String accountName) {
		this.accountName = accountName;
		return this;
	}

	public String getAccountType() {
		return accountType;
	}

	public Calendar setAccountType(String accountType) {
		this.accountType = accountType;
		return this;
	}

	public int getCalendarColor() {
		return calendarColor;
	}

	public Calendar setCalendarColor(int calendarColor) {
		this.calendarColor = calendarColor;
		return this;
	}

	public String getCalendarAccessLevel() {
		return calendarAccessLevel;
	}

	public Calendar setCalendarAccessLevel(String calendarAccessLevel) {
		this.calendarAccessLevel = calendarAccessLevel;
		return this;
	}

	public String getOwnerAccount() {
		return ownerAccount;
	}

	public Calendar setOwnerAccount(String ownerAccount) {
		this.ownerAccount = ownerAccount;
		return this;
	}

	public int getAccessLevel() {
		return accessLevel;
	}

	public Calendar setAccessLevel(int accessLevel) {
		this.accessLevel = accessLevel;
		return this;
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
		.append(" ~=~ ").append(name).append(" ~=~ \n")
		.append("Owner: ").append(ownerAccount).append('\n')
		.append("Rights: ").append(accessLevel).append('\n')
		.append("Account type: ").append(accountType).append('\n')
		.append("Visibility: ").append(visible).append('\n')
		.append("Sync Events: ").append(syncEvents).append('\n')
		.append("Acc name: ").append(accountName).append('\n').toString();
	}

	public long getId() {
		return id;
	}

	public Calendar setId(long id) {
		this.id = id;
		return this;
	}
}
