package ru.bmstu.schedule.calendar;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;

public class Calendar {
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
		Calendars.OWNER_ACCOUNT
	};
	
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
		
	}

	private void insert() {
		
	}
	
	private void update() {
		
	}
	
	public static Calendar findByName(String name, ContentResolver resolver) {
		String query = Calendars.NAME + " = ?";
		String[] mSelectionArgs = {	name };
		Cursor cur = resolver.query(Calendars.CONTENT_URI, CALENDAR_PROJECTION, query, mSelectionArgs, null);
		if (null == cur || cur.getCount() <= 0)
			return null;
		cur.moveToFirst();
		return new Calendar(cur.getString(0), cur.getString(3), resolver)
			.setVisible(cur.getString(1).equals("1")).setSyncEvents(cur.getString(2).equals("2"))
			.setAccountType(cur.getString(4)).setCalendarColor(cur.getInt(5))
			.setAccessLevel(cur.getInt(6)).setOwnerAccount(cur.getString(7));
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
}
