package ru.bmstu.schedule.calendar;

import java.net.URL;
import java.util.HashSet;

import android.support.v7.app.ActionBarActivity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Calendars;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Cursor cursor = null;
    	ContentResolver cr = getContentResolver();
    	Uri uri = Calendars.CONTENT_URI;
    	

    	cursor = cr.query(uri, (new String[] { Calendars._ID, Calendars.CALENDAR_DISPLAY_NAME, Calendars.OWNER_ACCOUNT}), null, null, null);
    	HashSet<String> calendarIds = new HashSet<String>();
    	System.out.println("Count="+cursor.getCount());
        if(cursor.getCount() > 0)
        {
            System.out.println("the control is just inside of the cursor.count loop");
	        while (cursor.moveToNext()) {
	             String _id = cursor.getString(0);
	             String displayName = cursor.getString(1);
	             Boolean selected = !cursor.getString(2).equals("0");
	
	            System.out.println("Id: " + _id + " Display Name: " + displayName + " Selected: " + selected);
	            calendarIds.add(_id);
	        }
        }

	}

	private static final int PROJECTION_ID_INDEX = 0;
	private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
	private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
	private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
	
	public static final String[] EVENT_PROJECTION = new String[] {
	    Calendars._ID,                           // 0
	    Calendars.ACCOUNT_NAME,                  // 1
	    Calendars.CALENDAR_DISPLAY_NAME,         // 2
	    Calendars.OWNER_ACCOUNT                  // 3
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
