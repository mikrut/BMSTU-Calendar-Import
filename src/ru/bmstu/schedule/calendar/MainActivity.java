package ru.bmstu.schedule.calendar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.bmstu.schedule.calendar.helpers.Logger;
import ru.bmstu.schedule.models.Auditorium;
import ru.bmstu.schedule.models.Group;
import ru.bmstu.schedule.models.Lecturer;
import ru.bmstu.schedule.models.Lesson;
import ru.bmstu.schedule.models.Stream;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
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
		logger.clear();
		
		/*Cursor cursor = null;
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
    			Calendars.OWNER_ACCOUNT,
    			Calendars.CALENDAR_DISPLAY_NAME}), null, null, null);
    	HashSet<String> calendarIds = new HashSet<String>();
    	    	
        if(cursor.getCount() > 0)
        {
	        while (cursor.moveToNext()) {
	            String name = cursor.getString(0);
	            String accountName = cursor.getString(3);
	            String accountType = cursor.getString(4);
	            String calendarColor = cursor.getString(5);
	            String accessLevel = cursor.getString(6);
	            String owner = cursor.getString(7);
	
	            logger.log("Name: " + name);
	            //logger.log("Acc Name: " + accountName);
	            //logger.log("AccType: " + accountType);
	            //logger.log("CalCol: " + calendarColor);
	            logger.log("dispName: "+cursor.getString(8));
	            //logger.log("CalAcsLev" + accessLevel);
	            //logger.log("owner" + owner);
	        }
        }
        cursor.close();
        Calendar cal = Calendar.findByName("mihanik001@gmail.com", cr);
        logger.log(cal);
        cal = new Calendar("testcalen", "mihanik001@gmail.com", cr);
        cal.save();
        logger.log("Deleted: " + Calendar.deleteByName("testcalen", "mihanik001@gmail.com", cr));*/
        
        String json = null;
        try {
        	BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("rasp.json"), "UTF-8"));
        	//TextView txt = (TextView) findViewById(R.id.hello);
        	
        	StringBuilder builder = new StringBuilder();
        	String line;
        	
        	while ((line = reader.readLine()) != null) {
        		builder.append(line);
        	}
        	json = builder.toString();
        	//txt.append(json);
        } catch (IOException e) {
        	Log.e("Reading JSON file", e.getLocalizedMessage());
        }
        
        try {
        	JSONObject all = new JSONObject(json);
        	    	
    		extractInfo(all, "aud", Auditorium.class);
    		extractInfo(all, "group", Group.class);
    		extractInfo(all, "pub", Lecturer.class);
    		extractInfo(all, "stream", Stream.class);
    		        	
        	List<Lesson> lessons = new LinkedList<Lesson>();
        	JSONArray items = all.getJSONArray("item");
        	
        	for(int i = 0; i < items.length(); i++) {
        		Lesson lesson = new Lesson(items.getJSONObject(i));
        		lessons.add(lesson);
        		
        		logger.log(lesson.toString());
        	}
        } catch (JSONException e) {
        	Log.e("Parsing JSON", e.getLocalizedMessage());
        }
        /*Event e = new Event("mihanik001@gmail.com", "Event", "Hello world!").setrRuleOnce();
        logger.log(e);
        e.save(cr, cal.getId());*/
   	}
	
	interface SimpleHandler<T> {
		public void handle(T object);
	}
	
	private static <T> void extractInfo(JSONObject all,	String classJSONName, Class<T> c)  throws JSONException {
		extractInfo(all, classJSONName, c, null);
	}
	
	private static <T> void extractInfo(
			JSONObject all,
			String classJSONName,
			Class<T> c,
			SimpleHandler<T> handler) throws JSONException {
		JSONObject arr = all.getJSONObject(classJSONName);
    	Iterator<?> keys = arr.keys();
		while( keys.hasNext() ) {
		    String key = (String)keys.next();
		    try {
				T instance = (T) c.getConstructor(JSONObject.class).newInstance(arr.getJSONObject(key));
				if (handler != null)
					handler.handle(instance);
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
