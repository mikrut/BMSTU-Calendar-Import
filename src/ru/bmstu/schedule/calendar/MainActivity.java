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
    	

    	cursor = cr.query(uri, (new String[] {
    			Calendars.NAME,
    			Calendars.VISIBLE,
    			Calendars.SYNC_EVENTS,
    			Calendars.ACCOUNT_NAME,
    			Calendars.ACCOUNT_TYPE,
    			Calendars.CALENDAR_COLOR,
    			Calendars.CALENDAR_ACCESS_LEVEL,
    			Calendars.OWNER_ACCOUNT}), null, null, null);
    	HashSet<String> calendarIds = new HashSet<String>();
    	
    	logger.clear();
    	logger.log("Count="+cursor.getCount());
        if(cursor.getCount() > 0)
        {
        	logger.log("the control is just inside of the cursor.count loop");
	        while (cursor.moveToNext()) {
	            String name = cursor.getString(0);
	            String accountName = cursor.getString(3);
	            String accountType = cursor.getString(4);
	            String calendarColor = cursor.getString(5);
	            String accessLevel = cursor.getString(6);
	            String owner = cursor.getString(7);
	
	           /* logger.log("Name: " + name);
	            logger.log("Acc Name: " + accountName);
	            logger.log("AccType: " + accountType);
	            logger.log("CalCol" + calendarColor);
	            logger.log("CalAcsLev" + accessLevel);
	            logger.log("owner" + owner);*/
	        }
        }
        cursor.close();
       Calendar cal = Calendar.findByName("mihanik001@gmail.com", cr);
       logger.log(cal);
       /* Event e = new Event("mihanik001@gmail.com", "Event", "Hello world!").setrRuleOnce();
        logger.log(e);
        e.save(cr, calendarIds.iterator().next());*/
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
