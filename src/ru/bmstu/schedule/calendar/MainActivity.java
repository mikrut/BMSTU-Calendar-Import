package ru.bmstu.schedule.calendar;

import java.util.HashSet;

import ru.bmstu.schedule.calendar.helpers.Logger;
import android.support.v7.app.ActionBarActivity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Calendars;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
	Logger logger;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		logger = new Logger((TextView) findViewById(R.id.hello));
		
		Cursor cursor = null;
    	ContentResolver cr = getContentResolver();
    	Uri uri = Calendars.CONTENT_URI;
    	

    	cursor = cr.query(uri, (new String[] { Calendars._ID, Calendars.CALENDAR_DISPLAY_NAME, Calendars.OWNER_ACCOUNT}), null, null, null);
    	HashSet<String> calendarIds = new HashSet<String>();
    	
    	logger.clear();
    	logger.log("Count="+cursor.getCount());
        if(cursor.getCount() > 0)
        {
        	logger.log("the control is just inside of the cursor.count loop");
	        while (cursor.moveToNext()) {
	            String _id = cursor.getString(0);
	            String displayName = cursor.getString(1);
	            Boolean selected = !cursor.getString(2).equals("0");
	
	            logger.log("Id: " + _id + " Display Name: " + displayName + " Selected: " + selected);
	            calendarIds.add(_id);
	        }
        }

	}

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
